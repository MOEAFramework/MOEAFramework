/* Copyright 2009-2022 David Hadka
 *
 * This file is part of the MOEA Framework.
 *
 * The MOEA Framework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * The MOEA Framework is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the MOEA Framework.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.moeaframework.algorithm.pso;

import org.moeaframework.analysis.sensitivity.EpsilonHelper;
import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.comparator.CrowdingComparator;
import org.moeaframework.core.comparator.ParetoDominanceComparator;
import org.moeaframework.core.fitness.CrowdingDistanceFitnessEvaluator;
import org.moeaframework.core.fitness.FitnessBasedArchive;
import org.moeaframework.core.operator.TypeSafeMutation;
import org.moeaframework.core.operator.Mutation;
import org.moeaframework.core.variable.RealVariable;

// NOTE: This implementation is derived from the original manuscripts and the
// JMetal implementation.

/**
 * Implementation of OMOPSO, a multi-objective particle swarm optimizer (MOPSO).
 * According to [2], OMOPSO is one of the top-performing PSO algorithms.
 * <p>
 * References:
 * <ol>
 *   <li>Sierra, M. R. and C. A. Coello Coello (2005).  Improving PSO-based
 *       Multi-Objective Optimization using Crowding, Mutation and
 *       &epsilon;-Dominance.  Evolutionary Multi-Criterion Optimization,
 *       pp. 505-519.
 *   <li>Durillo, J. J., J. Garc�a-Nieto, A. J. Nebro, C. A. Coello Coello,
 *       F. Luna, and E. Alba (2009).  Multi-Objective Particle Swarm
 *       Optimizers: An Experimental Comparison.  Evolutionary Multi-Criterion
 *       Optimization, pp. 495-509.
 * </ol>
 */
public class OMOPSO extends AbstractPSOAlgorithm {

	/**
	 * The uniform mutation operator, whose parameters remain unchanged.
	 */
	private final Mutation uniformMutation;
	
	/**
	 * The non-uniform mutation operator, whose parameters change during a run.
	 */
	private final Mutation nonUniformMutation;
	
	/**
	 * Constructs a new OMOPSO instance with default settings.
	 * 
	 * @param problem the problem
	 * @param maxIterations the maximum number of iterations for scaling non-uniform mutation;
	 *        typically this should be {@code maxEvaluations / swarmSize}
	 */
	public OMOPSO(Problem problem, int maxIterations) {
		this(problem,
				Settings.DEFAULT_POPULATION_SIZE,
				Settings.DEFAULT_POPULATION_SIZE,
				new double[] { EpsilonHelper.getEpsilon(problem) },
				1.0 / problem.getNumberOfVariables(),
				0.5,
				maxIterations);
	}
	
	/**
	 * Constructs a new OMOPSO instance.
	 * 
	 * @param problem the problem
	 * @param swarmSize the number of particles
	 * @param leaderSize the number of leaders
	 * @param epsilons the &epsilon;-values used in the external archive
	 * @param mutationProbability the mutation probability for uniform and non-uniform mutation
	 * @param mutationPerturbation the perturbation index for uniform and non-uniform mutation
	 * @param maxIterations the maximum iterations for scaling the non-uniform mutation
	 */
	public OMOPSO(Problem problem, int swarmSize, int leaderSize,
			double[] epsilons, double mutationProbability,
			double mutationPerturbation, int maxIterations) {
		super(problem, swarmSize, leaderSize,
				new CrowdingComparator(),
				new ParetoDominanceComparator(),
				new FitnessBasedArchive(new CrowdingDistanceFitnessEvaluator(), leaderSize),
				new EpsilonBoxDominanceArchive(epsilons),
				null);
		
		this.uniformMutation = new UniformMutation(mutationProbability, mutationPerturbation);
		this.nonUniformMutation = new NonUniformMutation(mutationProbability, mutationPerturbation, maxIterations);
		
		problem.assertType(RealVariable.class);
	}
	
	@Override
	protected void mutate(int i) {
		if (i % 3 == 0) {
			particles[i] = nonUniformMutation.mutate(particles[i]);
		} else if (i % 3 == 1) {
			particles[i] = uniformMutation.mutate(particles[i]);
		}
	}
	
	/**
	 * The non-uniform mutation operator.
	 */
	private class NonUniformMutation extends TypeSafeMutation<RealVariable> {
		
		private final double perturbation;
		
		private final int maxIterations;
		
		public NonUniformMutation(double probability, double perturbation, int maxIterations) {
			super(RealVariable.class, probability);
			this.perturbation = perturbation;
			this.maxIterations = maxIterations;
		}
		
		@Override
		public void mutate(RealVariable variable) {
			double value = variable.getValue();
				
			if (PRNG.nextBoolean()) {
				value += getDelta(variable.getUpperBound() - value);
			} else {
				value += getDelta(variable.getLowerBound() - value);
			}

			if (value < variable.getLowerBound()) {
				value = variable.getLowerBound();
			} else if (value > variable.getUpperBound()) {
				value = variable.getUpperBound();
			}
					
			variable.setValue(value);
		}
		
		public double getDelta(double difference) {
			int currentIteration = getNumberOfEvaluations() / swarmSize;
			double fraction = currentIteration / (double)maxIterations;
			
			return difference * (1.0 - Math.pow(PRNG.nextDouble(), Math.pow(1.0 - fraction, perturbation)));
		}

	}
	
	/**
	 * The uniform mutation operator.
	 */
	private class UniformMutation extends TypeSafeMutation<RealVariable> {
		
		private final double perturbation;
		
		public UniformMutation(double probability, double perturbation) {
			super(RealVariable.class, probability);
			this.perturbation = perturbation;
		}

		@Override
		public void mutate(RealVariable variable) {
			double value = variable.getValue();
					
			value += (PRNG.nextDouble() - 0.5) * perturbation;
					
			if (value < variable.getLowerBound()) {
				value = variable.getLowerBound();
			} else if (value > variable.getUpperBound()) {
				value = variable.getUpperBound();
			}
					
			variable.setValue(value);
		}
		
	}

}
