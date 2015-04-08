package org.moeaframework.algorithm.pso;

import org.moeaframework.core.PRNG;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.CrowdingComparator;
import org.moeaframework.core.comparator.ParetoDominanceComparator;
import org.moeaframework.core.fitness.CrowdingDistanceFitnessEvaluator;
import org.moeaframework.core.fitness.FitnessBasedArchive;
import org.moeaframework.core.operator.real.PM;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.RealVariable;

//NOTE: This implementation is derived from the original manuscripts and the
//JMetal implementation.

/**
 * Implementation of SMPSO, the speed-constrained multi-objective particle
 * swarm optimizer.
 * <p>
 * References:
 * <ol>
 *   <li>Nebro, A. J., J. J. Durillo, J. Garcia-Nieto, and C. A. Coello Coello
 *       (2009).  SMPSO: A New PSO-based Metaheuristic for Multi-objective
 *       Optimization.  2009 IEEE Symposium on Computational Intelligence in
 *       Multi-Criteria Decision-Making, pp. 66-73.
 *   <li>Durillo, J. J., J. Garc�a-Nieto, A. J. Nebro, C. A. Coello Coello,
 *       F. Luna, and E. Alba (2009).  Multi-Objective Particle Swarm
 *       Optimizers: An Experimental Comparison.  Evolutionary Multi-Criterion
 *       Optimization, pp. 495-509.
 * </ol>
 */
public class SMPSO extends AbstractPSOAlgorithm {
	
	private double[] minimumDelta;
	
	private double[] maximumDelta;
	
	public SMPSO(Problem problem, int swarmSize, int leaderSize,
			double mutationProbability, double distributionIndex) {
		super(problem, swarmSize, leaderSize, new CrowdingComparator(),
				new ParetoDominanceComparator(),
				new FitnessBasedArchive(new CrowdingDistanceFitnessEvaluator(), leaderSize),
				null,
				new PM(mutationProbability, distributionIndex));

		minimumDelta = new double[problem.getNumberOfVariables()];
		maximumDelta = new double[problem.getNumberOfVariables()];
		
		Solution prototypeSolution = problem.newSolution();
		
		for (int i = 0; i < problem.getNumberOfVariables(); i++) {
			RealVariable variable = (RealVariable)prototypeSolution.getVariable(i);
			maximumDelta[i] = (variable.getUpperBound() - variable.getLowerBound()) / 2.0;
			minimumDelta[i] = -maximumDelta[i];
		}
	}
	
	protected void updateSpeed(int i) {
		Solution particle = particles[i];
		Solution localBestParticle = localBestParticles[i];
		Solution leader = selectLeader();
		
		double r1 = PRNG.nextDouble();
		double r2 = PRNG.nextDouble();
		double C1 = PRNG.nextDouble(1.5, 2.5);
		double C2 = PRNG.nextDouble(1.5, 2.5);
		double W = PRNG.nextDouble(0.1, 0.1);
		
		for (int j = 0; j < problem.getNumberOfVariables(); j++) {
			double particleValue = EncodingUtils.getReal(particle.getVariable(j));
			double localBestValue = EncodingUtils.getReal(localBestParticle.getVariable(j));
			double leaderValue = EncodingUtils.getReal(leader.getVariable(j));
			
			double velocity = constrictionCoefficient(C1, C2) * 
					W * speed[i][j] + 
					C1*r1*(localBestValue - particleValue) +
					C2*r2*(leaderValue - particleValue);
			
			if (velocity > maximumDelta[j]) {
				velocity = maximumDelta[j];
			} else if (velocity < minimumDelta[j]) {
				velocity = minimumDelta[j];
			}
			
			speed[i][j] = velocity;
		}
	}
	
	protected double constrictionCoefficient(double c1, double c2) {
		double rho = c1 + c2;
		
		if (rho <= 4) {
			return 1.0;
		} else {
			return 2.0 / (2.0 - rho - Math.sqrt(Math.pow(rho, 2.0) - 4.0 * rho));
		}
	}

	@Override
	protected void mutate(int i) {
		// The SMPSO paper [1] states that mutation is applied 15% of the time,
		// but the JMetal implementation applies to every 6th particle.  Should
		// the application of mutation be random instead?
		if (i % 6 == 0) {
			particles[i] = mutation.evolve(new Solution[] { particles[i] })[0];
		}
	}


}
