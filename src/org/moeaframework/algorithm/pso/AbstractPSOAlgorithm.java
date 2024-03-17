/* Copyright 2009-2024 David Hadka
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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.moeaframework.algorithm.AbstractAlgorithm;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.DominanceComparator;
import org.moeaframework.core.configuration.Configurable;
import org.moeaframework.core.configuration.Property;
import org.moeaframework.core.configuration.Validate;
import org.moeaframework.core.fitness.FitnessBasedArchive;
import org.moeaframework.core.initialization.RandomInitialization;
import org.moeaframework.core.operator.Mutation;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.RealVariable;

/**
 * Abstract multi-objective particle swarm optimizer (MOPSO).
 */
public abstract class AbstractPSOAlgorithm extends AbstractAlgorithm implements Configurable {
	
	/**
	 * The number of particles.
	 */
	private int swarmSize;
	
	/**
	 * The number of leaders.
	 */
	private int leaderSize;
	
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
	protected Mutation mutation;
	
	/**
	 * Constructs a new abstract PSO algorithm.
	 * 
	 * @param problem the problem
	 * @param swarmSize the number of particles
	 * @param leaderSize the number of leaders
	 * @param leaderComparator comparator for selecting leaders
	 * @param dominanceComparator comparator for updating the local best particles
	 * @param leaders non-dominated population for storing the leaders
	 * @param archive non-dominated population for storing the external archive; or {@code null} if no external archive
	 *        is defined
	 * @param mutation mutation operator, or {@code null} if no mutation is defined
	 */
	public AbstractPSOAlgorithm(Problem problem, int swarmSize, int leaderSize, DominanceComparator leaderComparator,
			DominanceComparator dominanceComparator, FitnessBasedArchive leaders, NondominatedPopulation archive,
			Mutation mutation) {
		super(problem);
		setSwarmSize(swarmSize);
		setLeaderSize(leaderSize);
		
		Validate.problemType(problem, RealVariable.class);
		Validate.notNull("leaderComparer", leaderComparator);
		Validate.notNull("dominanceComparator", dominanceComparator);
		Validate.notNull("leaders", leaders);

		this.leaderComparator = leaderComparator;
		this.dominanceComparator = dominanceComparator;
		this.leaders = leaders;
		this.archive = archive;
		this.mutation = mutation;
	}
	
	/**
	 * Returns the number of particles (aka swarm size or population size).
	 * 
	 * @return the swarm size
	 */
	public int getSwarmSize() {
		return swarmSize;
	}

	/**
	 * Sets the number of particles (aka swarm size or population size).  This value can only be set before
	 * initialization.
	 * 
	 * @param swarmSize the swarm size
	 */
	@Property(alias="populationSize")
	public void setSwarmSize(int swarmSize) {
		assertNotInitialized();
		Validate.greaterThanZero("swarmSize", swarmSize);
		this.swarmSize = swarmSize;
	}

	/**
	 * Returns the number of leaders, which tracks the best particles according to some fitness criteria.
	 * 
	 * @return the leader size
	 */
	public int getLeaderSize() {
		return leaderSize;
	}

	/**
	 * Sets the number of leaders, which tracks the best particles according to some fitness criteria.  This value
	 * can only be set before initialization.
	 * 
	 * @param leaderSize the leader size
	 */
	@Property(alias="archiveSize")
	public void setLeaderSize(int leaderSize) {
		assertNotInitialized();
		Validate.greaterThanZero("leaderSize", leaderSize);
		this.leaderSize = leaderSize;
	}
	
	/**
	 * Returns the mutation operator, or {@code null} if no mutation is defined.
	 * 
	 * @return the mutation operator or {@code null}
	 */
	public Mutation getMutation() {
		return mutation;
	}
	
	/**
	 * Returns the archive of non-dominated solutions; or {@code null} of no external archive is used.
	 * 
	 * @return the archive or {@code null}
	 */
	protected NondominatedPopulation getArchive() {
		return archive;
	}
	
	/**
	 * Sets the archive of non-dominated solutions; or {@code null} of no external archive is used.  This value
	 * can only be set before initialization.
	 * 
	 * @param archive the archive or {@code null}.
	 */
	protected void setArchive(NondominatedPopulation archive) {
		assertNotInitialized();
		this.archive = archive;
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
			int flag = dominanceComparator.compare(particles[i], localBestParticles[i]);
			
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
			particles[i] = mutation.mutate(particles[i]);
		}
	}

	@Override
	public NondominatedPopulation getResult() {
		if (archive == null) {
			return new NondominatedPopulation(leaders);
		} else {
			return new NondominatedPopulation(archive);
		}
	}

	@Override
	protected void initialize() {
		super.initialize();
		
		Solution[] initialParticles = new RandomInitialization(problem).initialize(swarmSize);
		evaluateAll(initialParticles);
		
		particles = new Solution[swarmSize];
		localBestParticles = new Solution[swarmSize];
		velocities = new double[swarmSize][problem.getNumberOfVariables()];
		
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
	
	/**
	 * Returns the current particles.
	 * 
	 * @return the current particles
	 */
	public List<Solution> getParticles() {
		return copyToList(particles);
	}
	
	/**
	 * Returns the local best particles.
	 * 
	 * @return the local best particles
	 */
	public List<Solution> getLocalBestParticles() {
		return copyToList(localBestParticles);
	}
	
	/**
	 * Returns the current leaders.
	 * 
	 * @return the current leaders
	 */
	public List<Solution> getLeaders() {
		return leaders.asList(true);
	}
	
	@Override
	public void saveState(ObjectOutputStream stream) throws IOException {
		super.saveState(stream);
		stream.writeObject(particles);
		stream.writeObject(localBestParticles);
		stream.writeObject(velocities);
		leaders.saveState(stream);
		
		if (archive != null) {
			archive.saveState(stream);
		}
	}

	@Override
	public void loadState(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		super.loadState(stream);
		particles = (Solution[])stream.readObject();
		localBestParticles = (Solution[])stream.readObject();
		velocities = (double[][])stream.readObject();
		leaders.loadState(stream);
		
		if (archive != null) {
			archive.loadState(stream);
		}
	}
	
	/**
	 * Converts an array of solutions to a list of solutions, creating copies of each solution.
	 * 
	 * @param solutions the array of solutions
	 * @return the list of copied solutions
	 */
	protected static List<Solution> copyToList(Solution[] solutions) {
		List<Solution> result = new ArrayList<Solution>(solutions.length);
		
		for (Solution solution : solutions) {
			result.add(solution.copy());
		}
		
		return result;
	}
	
}
