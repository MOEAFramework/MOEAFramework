/* Copyright 2009-2018 David Hadka
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

import java.io.NotSerializableException;
import java.io.Serializable;
import java.util.List;

import org.moeaframework.algorithm.AbstractAlgorithm;
import org.moeaframework.algorithm.AlgorithmInitializationException;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.core.comparator.DominanceComparator;
import org.moeaframework.core.fitness.FitnessBasedArchive;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.util.SolutionUtils;

/**
 * Abstract multi-objective particle swarm optimizer (MOPSO).
 */
public abstract class AbstractPSOAlgorithm extends AbstractAlgorithm {
	
	/**
	 * The original implementation of OMOPSO in JMetal returns the leaders
	 * instead of the epsilon-dominance archive as described in the literature.
	 * This results in a small performance difference that is detected by our
	 * unit tests.  To enable unit testing to compare the two implementations,
	 * this flag forces OMOPSO to behave like the JMetal implementation and
	 * only return the leaders.
	 */
	static boolean TESTING_MODE = false;

	/**
	 * The number of particles.
	 */
	protected int swarmSize;
	
	/**
	 * The number of leaders.
	 */
	protected int leaderSize;
	
	/**
	 * The particles.
	 */
	protected Solution[] particles;
	
	/**
	 * The local best particles.
	 */
	protected Solution[] localBestParticles;
	
	/**
	 * The leaders.
	 */
	protected FitnessBasedArchive leaders;
	
	/**
	 * The archive of non-dominated solutions; or {@code null} of no external
	 * archive is used.
	 */
	protected NondominatedPopulation archive;
	
	/**
	 * The speed / velocity of each particle.
	 */
	protected double[][] velocities;
	
	/**
	 * Comparator for selecting leaders.
	 */
	protected DominanceComparator leaderComparator;
	
	/**
	 * Comparator for updating the local best particles.
	 */
	protected DominanceComparator dominanceComparator;
	
	/**
	 * Mutation operator, or {@code null} if no mutation is defined.
	 */
	protected Variation mutation;
	
	/**
	 * Constructs a new abstract PSO algorithm.
	 * 
	 * @param problem the problem
	 * @param swarmSize the number of particles
	 * @param leaderSize the number of leaders
	 * @param leaderComparator comparator for selecting leaders
	 * @param dominanceComparator comparator for updating the local best
	 *        particles
	 * @param leaders non-dominated population for storing the leaders
	 * @param archive non-dominated population for storing the external archive;
	 *        or {@code null} if no external archive is defined
	 * @param mutation mutation operator, or {@code null} if no mutation is
	 *        defined
	 */
	public AbstractPSOAlgorithm(Problem problem, int swarmSize, int leaderSize,
			DominanceComparator leaderComparator,
			DominanceComparator dominanceComparator,
			FitnessBasedArchive leaders,
			NondominatedPopulation archive,
			Variation mutation) {
		super(problem);
		this.swarmSize = swarmSize;
		this.leaderSize = leaderSize;
		this.leaderComparator = leaderComparator;
		this.dominanceComparator = dominanceComparator;
		this.leaders = leaders;
		this.archive = archive;
		this.mutation = mutation;

		particles = new Solution[swarmSize];
		localBestParticles =  new Solution[swarmSize];
		velocities = new double[swarmSize][problem.getNumberOfVariables()];
	}
	
	/**
	 * Update the speeds of all particles.
	 */
	protected void updateVelocities() {
		for (int i = 0; i < swarmSize; i++) {
			updateVelocity(i);
		}
	}
	
	/**
	 * Update the speed of an individual particle.
	 * 
	 * @param i the index of the particle
	 */
	protected void updateVelocity(int i) {
		Solution particle = particles[i];
		Solution localBestParticle = localBestParticles[i];
		Solution leader = selectLeader();
		
		double r1 = PRNG.nextDouble();
		double r2 = PRNG.nextDouble();
		double C1 = PRNG.nextDouble(1.5, 2.0);
		double C2 = PRNG.nextDouble(1.5, 2.0);
		double W = PRNG.nextDouble(0.1, 0.5);
		
		for (int j = 0; j < problem.getNumberOfVariables(); j++) {
			double particleValue = EncodingUtils.getReal(particle.getVariable(j));
			double localBestValue = EncodingUtils.getReal(localBestParticle.getVariable(j));
			double leaderValue = EncodingUtils.getReal(leader.getVariable(j));
			
			velocities[i][j] = W * velocities[i][j] + 
					C1*r1*(localBestValue - particleValue) +
					C2*r2*(leaderValue - particleValue);
		}
	}
	
	/**
	 * Update the positions of all particles.
	 */
	protected void updatePositions() {
		for (int i = 0; i < swarmSize; i++) {
			updatePosition(i);
		}
	}
	
	/**
	 * Update the position of an individual particle.
	 * 
	 * @param i the index of the particle
	 */
	protected void updatePosition(int i) {
		Solution parent = particles[i];
		Solution offspring = parent.copy();
		
		for (int j = 0; j < problem.getNumberOfVariables(); j++) {
			RealVariable variable = (RealVariable)offspring.getVariable(j);
			double value = variable.getValue() + velocities[i][j];
			
			if (value < variable.getLowerBound()) {
				value = variable.getLowerBound();
				velocities[i][j] *= -1;
			} else if (value > variable.getUpperBound()) {
				value = variable.getUpperBound();
				velocities[i][j] *= -1;
			}
			
			variable.setValue(value);
		}
		
		particles[i] = offspring;
	}
	
	/**
	 * Randomly select a leader.
	 * 
	 * @return the selected leader
	 */
	protected Solution selectLeader() {
		Solution leader1 = leaders.get(PRNG.nextInt(leaders.size()));
		Solution leader2 = leaders.get(PRNG.nextInt(leaders.size()));
		int flag = leaderComparator.compare(leader1, leader2);
		
		if (flag < 0) {
			return leader1;
		} else if (flag > 0) {
			return leader2;
		} else if (PRNG.nextBoolean()) {
			return leader1;
		} else {
			return leader2;
		}
	}
	
	/**
	 * Updates the local best particles.
	 */
	protected void updateLocalBest() {
		for (int i = 0; i < swarmSize; i++) {
			int flag = dominanceComparator.compare(particles[i],
					localBestParticles[i]);
			
			if (flag <= 0) {
				localBestParticles[i] = particles[i];
			}
		}
	}
	
	/**
	 * Applies the mutation operator to all particles.
	 */
	protected void mutate() {
		for (int i = 0; i < swarmSize; i++) {
			mutate(i);
		}
	}
	
	/**
	 * Applies the mutation operator to an individual particle.
	 * 
	 * @param i the index of the particle
	 */
	protected void mutate(int i) {
		if (mutation != null) {
			particles[i] = mutation.evolve(new Solution[] { particles[i] })[0];
		}
	}

	@Override
	public NondominatedPopulation getResult() {
		if (archive == null || TESTING_MODE) {
			return new NondominatedPopulation(leaders);
		} else {
			return new NondominatedPopulation(archive);
		}
	}

	@Override
	protected void initialize() {
		super.initialize();
		
		Solution[] initialParticles = new RandomInitialization(problem,
				swarmSize).initialize();
		
		evaluateAll(initialParticles);
		
		for (int i = 0; i < swarmSize; i++) {
			particles[i] = initialParticles[i];
			localBestParticles[i] = initialParticles[i];
		}
		
		leaders.addAll(initialParticles);
		leaders.update();
		
		if (archive != null) {
			archive.addAll(initialParticles);
		}
	}

	@Override
	protected void iterate() {
		updateVelocities();
		updatePositions();
		mutate();
		
		evaluateAll(particles);
		
		updateLocalBest();
		leaders.addAll(particles);
		leaders.update();
		
		if (archive != null) {
			archive.addAll(particles);
		}
	}
	
	public List<Solution> getParticles() {
		return SolutionUtils.copyToList(particles);
	}
	
	public List<Solution> getLocalBestParticles() {
		return SolutionUtils.copyToList(localBestParticles);
	}
	
	public List<Solution> getLeaders() {
		return SolutionUtils.copyToList(leaders);
	}
	
	@Override
	public Serializable getState() throws NotSerializableException {
		if (!isInitialized()) {
			throw new AlgorithmInitializationException(this, 
					"algorithm not initialized");
		}

		List<Solution> particlesList = SolutionUtils.toList(particles);
		List<Solution> localBestParticlesList = SolutionUtils.toList(localBestParticles);
		List<Solution> leadersList = SolutionUtils.toList(leaders);
		List<Solution> archiveList = archive == null ? null : SolutionUtils.toList(archive);
		double[][] velocitiesClone = new double[velocities.length][];
		
		for (int i = 0; i < velocities.length; i++) {
			velocitiesClone[i] = velocities[i].clone();
		}

		return new PSOAlgorithmState(getNumberOfEvaluations(),
				particlesList, localBestParticlesList, leadersList,
				archiveList, velocitiesClone);
	}

	@Override
	public void setState(Object objState) throws NotSerializableException {
		super.initialize();

		PSOAlgorithmState state = (PSOAlgorithmState)objState;

		numberOfEvaluations = state.getNumberOfEvaluations();
		
		if (state.getParticles().size() != swarmSize) {
			throw new NotSerializableException(
					"swarmSize does not match serialized state");
		}

		for (int i = 0; i < swarmSize; i++) {
			particles[i] = state.getParticles().get(i);
		}
		
		for (int i = 0; i < swarmSize; i++) {
			localBestParticles[i] = state.getLocalBestParticles().get(i);
		}
		
		leaders.addAll(state.getLeaders());
		leaders.update();

		if (archive != null) {
			archive.addAll(state.getArchive());
		}
		
		for (int i = 0; i < swarmSize; i++) {
			for (int j = 0; j < problem.getNumberOfVariables(); j++) {
				velocities[i][j] = state.getVelocities()[i][j];
			}
		}
	}
	
	/**
	 * Proxy for serializing and deserializing the state of an
	 * {@code AbstractPSOAlgorithm}. This proxy supports saving
	 * the {@code numberOfEvaluations}, {@code population} and {@code archive}.
	 */
	private static class PSOAlgorithmState implements Serializable {

		private static final long serialVersionUID = -1895823731827106938L;

		/**
		 * The number of objective function evaluations.
		 */
		private final int numberOfEvaluations;

		/**
		 * The particles stored in a serializable list.
		 */
		private final List<Solution> particles;

		/**
		 * The local best particles stored in a serializable list.
		 */
		private final List<Solution> localBestParticles;
		
		/**
		 * The leaders stored in a serializable list.
		 */
		private final List<Solution> leaders;
		
		/**
		 * The archive stored in a serializable list.
		 */
		private final List<Solution> archive;
		
		/**
		 * The velocities.
		 */
		private final double[][] velocities;

		/**
		 * Constructs a proxy to serialize and deserialize the state of an 
		 * {@code AbstractPSOAlgorithm}.
		 * 
		 * @param numberOfEvaluations the number of objective function
		 *        evaluations
		 * @param population the population stored in a serializable list
		 * @param archive the archive stored in a serializable list
		 */
		public PSOAlgorithmState(int numberOfEvaluations,
				List<Solution> particles,
				List<Solution> localBestParticles,
				List<Solution> leaders,
				List<Solution> archive,
				double[][] velocities) {
			super();
			this.numberOfEvaluations = numberOfEvaluations;
			this.particles = particles;
			this.localBestParticles = localBestParticles;
			this.leaders = leaders;
			this.archive = archive;
			this.velocities = velocities;
		}

		/**
		 * Returns the number of objective function evaluations.
		 * 
		 * @return the number of objective function evaluations
		 */
		public int getNumberOfEvaluations() {
			return numberOfEvaluations;
		}

		/**
		 * Returns the particles stored in a serializable list.
		 * 
		 * @return the particles stored in a serializable list
		 */
		public List<Solution> getParticles() {
			return particles;
		}

		/**
		 * Returns the local best particles stored in a serializable list.
		 * 
		 * @return the local best particles stored in a serializable list
		 */
		public List<Solution> getLocalBestParticles() {
			return localBestParticles;
		}

		/**
		 * Returns the leaders stored in a serializable list.
		 * 
		 * @return the leaders stored in a serializable list
		 */
		public List<Solution> getLeaders() {
			return leaders;
		}

		/**
		 * Returns the velocities.
		 * 
		 * @return the velocities
		 */
		public double[][] getVelocities() {
			return velocities;
		}

		/**
		 * Returns the archive stored in a serializable list.
		 * 
		 * @return the archive stored in a serializable list
		 */
		public List<Solution> getArchive() {
			return archive;
		}

	}

}
