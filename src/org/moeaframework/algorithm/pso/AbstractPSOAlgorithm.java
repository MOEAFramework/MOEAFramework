package org.moeaframework.algorithm.pso;

import org.moeaframework.algorithm.AbstractAlgorithm;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.core.comparator.DominanceComparator;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.RealVariable;

/**
 * Generic multi-objective particle swarm optimizer (MOPSO) implementation.
 */
public abstract class AbstractPSOAlgorithm extends AbstractAlgorithm {

	protected int swarmSize;
	
	protected int leaderSize;
	
	protected Solution[] particles;
	
	protected Solution[] localBestParticles;
	
	protected NondominatedPopulation leaders;
	
	protected NondominatedPopulation archive;
	
	protected double[][] speed;
	
	protected DominanceComparator leaderComparator;
	
	protected DominanceComparator dominanceComparator;
	
	protected Variation mutation;
	
	public AbstractPSOAlgorithm(Problem problem, int swarmSize, int leaderSize,
			DominanceComparator leaderComparator,
			DominanceComparator dominanceComparator,
			NondominatedPopulation leaders,
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
		speed = new double[swarmSize][problem.getNumberOfVariables()];
	}
	
	protected void updateSpeeds() {
		for (int i = 0; i < swarmSize; i++) {
			updateSpeed(i);
		}
	}
	
	protected void updateSpeed(int i) {
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
			
			speed[i][j] = W * speed[i][j] + 
					C1*r1*(localBestValue - particleValue) +
					C2*r2*(leaderValue - particleValue);
		}
	}
	
	protected void updatePositions() {
		for (int i = 0; i < swarmSize; i++) {
			updatePosition(i);
		}
	}
	
	protected void updatePosition(int i) {
		Solution parent = particles[i];
		Solution offspring = parent.copy();
		
		for (int j = 0; j < problem.getNumberOfVariables(); j++) {
			RealVariable variable = (RealVariable)offspring.getVariable(j);
			double value = variable.getValue() + speed[i][j];
			
			if (value < variable.getLowerBound()) {
				value = variable.getLowerBound();
				speed[i][j] *= -1;
			} else if (value > variable.getUpperBound()) {
				value = variable.getUpperBound();
				speed[i][j] *= -1;
			}
			
			variable.setValue(value);
		}
		
		particles[i] = offspring;
	}
	
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
	
	protected void updateLocalBest() {
		for (int i = 0; i < swarmSize; i++) {
			int flag = dominanceComparator.compare(particles[i],
					localBestParticles[i]);
			
			if (flag <= 0) {
				localBestParticles[i] = particles[i];
			}
		}
	}
	
	protected void mutate() {
		for (int i = 0; i < swarmSize; i++) {
			mutate(i);
		}
	}
	
	protected void mutate(int i) {
		if (mutation != null) {
			particles[i] = mutation.evolve(new Solution[] { particles[i] })[0];
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
		
		Solution[] initialParticles = new RandomInitialization(problem,
				swarmSize).initialize();
		
		evaluateAll(initialParticles);
		
		for (int i = 0; i < swarmSize; i++) {
			particles[i] = initialParticles[i];
			localBestParticles[i] = initialParticles[i];
		}
		
		leaders.addAll(initialParticles);
		
		if (archive != null) {
			archive.addAll(initialParticles);
		}
	}

	@Override
	protected void iterate() {
		updateSpeeds();
		updatePositions();
		mutate();
		
		evaluateAll(particles);
		
		updateLocalBest();
		leaders.addAll(particles);
		archive.addAll(particles);
	}

}
