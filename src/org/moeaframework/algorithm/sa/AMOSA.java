/* Copyright 2018-2019 Ibrahim DEMIR
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
package org.moeaframework.algorithm.sa;

import java.io.NotSerializableException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.moeaframework.algorithm.AlgorithmInitializationException;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.DominanceComparator;
import org.moeaframework.core.comparator.ParetoDominanceComparator;
import org.moeaframework.core.configuration.Property;
import org.moeaframework.core.configuration.Validate;
import org.moeaframework.core.operator.Mutation;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.core.spi.OperatorFactory;

/**
 * Implementation of the simulated annealing-based multiobjective optimization algorithm (AMOSA).
 * Algorithm incorporates the archive mechanism in order to provide non-dominated set of solutions
 * so called Pareto front.
 * <p>
 * References:
 * <ol>
 *   <li>Sanghamitra Bandyopadhyay, Sriparna Saha, Ujjwal Maulik, Kalyanmoy Deb(2008).
 *       A Simulated Annealing-Based Multiobjective Optimization Algorithm: AMOSA.
 *       IEEE Transactions on Evolutionary Computation, vol. 12, no. 3, pp. 269-283.
 * </ol>
 * 
 * @preview
 */
public class AMOSA extends AbstractSimulatedAnnealingAlgorithm {
	
	protected final Initialization initialization;
	protected Mutation mutation;
	
	protected double gamma;
	protected int softLimit;
	protected int hardLimit;
	
	protected double alpha;
	protected int numberOfIterationsPerTemperature;
	protected int numberOfHillClimbingIterationsForRefinement;
	
	protected Solution currentPT;
	protected NondominatedPopulation archive;
	
	/**
	 * Creates a new instance of the AMOSA algorithm with default settings.
	 * 
	 * @param problem the problem to solve
	 */
	public AMOSA(Problem problem) {
		this(problem,
				new RandomInitialization(problem),
				OperatorFactory.getInstance().getMutation(problem),
				2.0, // gamma
				Settings.DEFAULT_POPULATION_SIZE, // softLimit
				10, // hardLimit
				0.0000001, // stoppingTemperature
				200.0, // initialTemperature
				0.8, // alpha
				500, // numberOfIterationPerTemperature
				20); //numberOfHillClimbingIterationsForRefinement
	}
	
	/**
	 * Creates a new instance of the AMOSA algorithm.
	 * 
	 * @param problem the problem to solve
	 * @param initialization the method for initializing solutions
	 * @param mutation the mutation operator
	 * @param gamma the gamma value
	 * @param softLimit the soft limit
	 * @param hardLimit the hard limit
	 * @param stoppingTemperature the stopping (minimum) temperature
	 * @param initialTemperature the initial (maximum) temperature
	 * @param alpha the cooling rate
	 * @param numberOfIterationsPerTemperature the number of iterations at each temperature
	 * @param numberOfHillClimbingIterationsForRefinement the number of hill climbing iterations performed on initial
	 *        solutions
	 */
	public AMOSA(Problem problem, Initialization initialization, Mutation mutation, double gamma, int softLimit,
			int hardLimit, double stoppingTemperature, double initialTemperature, double alpha,
			int numberOfIterationsPerTemperature, int numberOfHillClimbingIterationsForRefinement) {
		super(problem, stoppingTemperature, initialTemperature);
		setMutation(mutation);
		setGamma(gamma);
		setSoftLimit(softLimit);
		setHardLimit(hardLimit);
		setAlpha(alpha);
		setNumberOfIterationsPerTemperature(numberOfIterationsPerTemperature);
		setNumberOfHillClimbingIterationsForRefinement(numberOfHillClimbingIterationsForRefinement);
		
		Validate.notNull("initialization", initialization);
		
		this.initialization = initialization;
		this.archive = new NondominatedPopulation();
	}
	
	/**
	 * Returns the gamma value.
	 * 
	 * @return the gamma value
	 */
	public double getGamma() {
		return gamma;
	}

	/**
	 * Sets the value of gamma, which determines the number of random solutions generated to fill the
	 * initial population.  This value must be >= 1 and is typically set to 2.
	 * 
	 * @param gamma the gamma value
	 */
	@Property
	public void setGamma(double gamma) {
		Validate.greaterThanOrEqual("gamma", 1, gamma);
		this.gamma = gamma;
	}

	/**
	 * Returns the soft limit.
	 * 
	 * @return the soft limit
	 */
	public int getSoftLimit() {
		return softLimit;
	}
	
	/**
	 * Sets the soft limit, which controls the maximum size that the archive can be filled before clustering is used
	 * to reduce the size to the hard limit.
	 * 
	 * @param softLimit the soft limit
	 */
	@Property(alias="SL")
	public void setSoftLimit(int softLimit) {
		Validate.greaterThanZero("softLimit", softLimit);
		this.softLimit = softLimit;
	}

	/**
	 * Returns the hard limit.
	 * 
	 * @return the hard limit
	 */
	public int getHardLimit() {
		return hardLimit;
	}
	
	/**
	 * Sets the hard limit, which controls the maximum size of the archive or result set at termination.
	 * 
	 * @param hardLimit the hard limit
	 */
	@Property(alias="HL")
	public void setHardLimit(int hardLimit) {
		Validate.greaterThanZero("hardLimit", hardLimit);
		this.hardLimit = hardLimit;
	}
	
	/**
	 * Returns the mutation operator.
	 * 
	 * @return the mutation operator
	 */
	public Mutation getMutation() {
		return mutation;
	}
	
	/**
	 * Sets the mutation operator.
	 * 
	 * @param mutation the mutation operator
	 */
	@Property("operator")
	public void setMutation(Mutation mutation) {
		Validate.notNull("mutation", mutation);
		this.mutation = mutation;
	}

	/**
	 * Returns the cooling rate.
	 * 
	 * @return the cooling rate
	 */
	public double getAlpha() {
		return alpha;
	}

	/**
	 * Sets the cooling rate.  When combined with {@link #setNumberOfIterationsPerTemperature(int)}, this
	 * controls the rate at which the temperature decreases.
	 * 
	 * @param alpha the cooling rate
	 */
	@Property
	public void setAlpha(double alpha) {
		Validate.greaterThanZero("alpha", alpha);
		this.alpha = alpha;
	}

	/**
	 * Returns the number of iterations performed at each temperature.  Note that all iterations for the same
	 * temperature are evaluated in a single call to {@link #step()}.
	 * 
	 * @return the number of iterations
	 */
	public int getNumberOfIterationsPerTemperature() {
		return numberOfIterationsPerTemperature;
	}

	/**
	 * Sets the number of iterations performed at each temperature.
	 * 
	 * @param numberOfIterationsPerTemperature the number of iterations
	 */
	@Property(alias="iter")
	public void setNumberOfIterationsPerTemperature(int numberOfIterationsPerTemperature) {
		Validate.greaterThanZero("numberOfIterationsPerTemperature", numberOfIterationsPerTemperature);
		this.numberOfIterationsPerTemperature = numberOfIterationsPerTemperature;
	}

	/**
	 * Returns the number of hill climbing iterations for refinement.
	 * 
	 * @return the number of iterations
	 */
	public int getNumberOfHillClimbingIterationsForRefinement() {
		return numberOfHillClimbingIterationsForRefinement;
	}

	/**
	 * Sets the number of hill climbing iterations to refine initial solutions.  This is only performed during
	 * initialization and, as such, this value can only be set before initialization.
	 * 
	 * @param numberOfHillClimbingIterationsForRefinement the number of iterations
	 */
	@Property(alias="hillClimbIter")
	public void setNumberOfHillClimbingIterationsForRefinement(int numberOfHillClimbingIterationsForRefinement) {
		assertNotInitialized();
		Validate.greaterThanOrEqualToZero("numberOfHillClimbingIterationsForRefinement", numberOfHillClimbingIterationsForRefinement);
		this.numberOfHillClimbingIterationsForRefinement = numberOfHillClimbingIterationsForRefinement;
	}

	@Override
	protected void initialize() {
		super.initialize();
		
		if (mutation == null) {
			throw new FrameworkException("no mutation operator set, must set one by calling setMutation(...)");
		}

		Solution[] initialSolutions = initialization.initialize((int)(gamma * softLimit));
		evaluateAll(initialSolutions);

		//refine all initial solutions and add into pareto set: archive
		ParetoDominanceComparator paretoDominanceComparator = new ParetoDominanceComparator();
		
		for (int i=0; i < initialSolutions.length; i++) {
			for (int j=0; j < this.numberOfHillClimbingIterationsForRefinement; j++) {
				Solution child = this.mutation.mutate(initialSolutions[i]);
				evaluate(child);
				
				if (paretoDominanceComparator.compare(initialSolutions[i], child) > 0) {
					initialSolutions[i] = child;
				}
			}
			
			archive.add(initialSolutions[i]);
		}
		
		//if archive is bigger than hard limit (HL), apply clustering
		if (archive.size() > hardLimit) {
			cluster();
		}
		
		currentPT = archive.get(PRNG.nextInt(archive.size()));
		this.temperature = initialTemperature;
	}

	@Override
	protected void iterate() {
		if (temperature >= stoppingTemperature) {
			for (int i=0; i < this.numberOfIterationsPerTemperature; i++) {
				DominanceComparator comparator = new ParetoDominanceComparator();
				Solution newPT = mutation.mutate(currentPT);
				evaluate(newPT);
				
				// r is the array of range of each Objective in the archive along with newPT
				double[] r = calculateR(newPT);
				
				// Check The domination status of newPT and currentPT
				int comparisonResult = comparator.compare(currentPT, newPT);
				
				if (comparisonResult == -1) {	// Case 1 : currentPT dominates newPT 
					double averageDeltaDominance = calculateAverageDeltaDominance(newPT,r,comparator);
					double probability = 1d/(1d+Math.exp(averageDeltaDominance*temperature));
					
					if (PRNG.nextDouble() < probability) {
						this.currentPT=newPT;
					}
				} else if(comparisonResult == 0) {	// Case 2 : currentPt and newPT are non-dominating to each other
					DominationAmount dominationAmount = calculateDominationAmounts(newPT,comparator);
					
					if (dominationAmount.getDominatedAmount() > 0) {   // Case 2(a) : newPT is dominated by k(k>=1) points in the archive
						double averageDeltaDominance = calculateAverageDeltaDominance(newPT,r,comparator);
						double probability = 1d/(1d+Math.exp(averageDeltaDominance*temperature));
						
						if (PRNG.nextDouble() < probability) {
							this.currentPT = newPT;
						}
					} else if (dominationAmount.getDominatedAmount() == 0 && dominationAmount.getDominatesAmount() == 0) {    // Case 2(b) : newPT is non-dominating w.r.t all the points in the archive
						this.currentPT = newPT;
						this.archive.add(this.currentPT);
						
						if(archive.size() > this.softLimit) {
							cluster();
						}
					} else if (dominationAmount.getDominatesAmount() > 0) {    // Case 2(c) : newPT dominates by k(k>=1) points of the archive
						this.currentPT=newPT;
						this.archive.add(this.currentPT); // Since archive is an instance of NonDominatedPopulation, adding operator automatically removes dominated solutions
					}
				} else {		// Case 3 : newPT dominates currentPT
					DominationAmount dominationAmount = calculateDominationAmounts(newPT,comparator);
					
					if (dominationAmount.getDominatedAmount()>0) {   // Case 3(a) : newPT is dominated by k(k>=1) points in the archive
						MinimumDeltaDominance minimumDeltaDominance = calculateMinimumDeltaDominance(newPT,r,comparator);
						double probability = 1d/(1d+Math.exp(-1d*minimumDeltaDominance.getMinimumDeltaDominance()));
						
						if (PRNG.nextDouble() < probability) {
							this.currentPT = archive.get(minimumDeltaDominance.getIndex());
						} else {
							this.currentPT = newPT;
						}
					} else if (dominationAmount.getDominatedAmount()==0 && dominationAmount.getDominatesAmount()==0) {    // Case 3(b) : newPT is non-dominating w.r.t all the points in the archive
						this.currentPT = newPT;
						
						if (!this.archive.add(this.currentPT)) {
							archive.remove(this.currentPT);
						} else if (archive.size() > this.softLimit) {
							cluster();
						}
					} else if (dominationAmount.getDominatesAmount() > 0) {    // Case 3(c) : newPT dominates by k(k>=1) points of the archive
						this.currentPT = newPT;
						this.archive.add(this.currentPT); // Since archive is an instance of NonDominatedPopulation, adding operator automatically removes dominated solutions
					}
				}
			}
		} else {
			if (archive.size() > softLimit) {
				cluster();
			}
			
			terminate();
			return;
		}
		
		temperature *= alpha;
	}

	//with respect to III.C. Amount of Domination
	private double[] calculateR(Solution newPT) {
		double[] r = new double[newPT.getNumberOfObjectives()];
		double[] worsts = new double[newPT.getNumberOfObjectives()];
		double[] bests = new double[newPT.getNumberOfObjectives()];
		
		for (int i=0; i < newPT.getNumberOfObjectives(); i++) {
			worsts[i] = newPT.getObjective(i);
			bests[i] = newPT.getObjective(i);
		}
		
		for (int i=0; i < newPT.getNumberOfObjectives(); i++) {
			for (int j=0; j < archive.size(); j++) {
				if (archive.get(j).getObjective(i) < bests[i]) {
					bests[i] = archive.get(j).getObjective(i);
				} else if (archive.get(j).getObjective(i) > worsts[i]) {
					worsts[i] = archive.get(j).getObjective(i);
				}
			}
			
			r[i] = worsts[i] - bests[i];
		}
		
		return r;
	}
	
	//calculates delta dominanance between 2 given solutions with respect to III.C. Amount of Domination
	private double calculateDeltaDominance(Solution solutionA, Solution solutionB, double[] r) {
		double deltaDominance = 0d;
		
		for (int i=0; i < solutionA.getNumberOfObjectives(); i++) {
			deltaDominance *= Math.abs(solutionA.getObjective(i) - solutionB.getObjective(i)) / r[i];
		}
		
		return deltaDominance;
	}
	
	// Calculates total delta dominance between the given solution and all solutions in the archive with respect to III.C. Amount of Domination
	// Has if control to be able to support both calculations at cases {1} and {2a}
	private double calculateAverageDeltaDominance(Solution newPT, double[] r, DominanceComparator comparator) {
		double totalDeltaDominance = 0d;
		int k = 0;
		
		for (int i=0; i < archive.size();i++) {
			if (comparator.compare(archive.get(i), newPT) == -1) {
				k++;
				totalDeltaDominance += calculateDeltaDominance(newPT, archive.get(i),r);
			}
		}
		
		if (comparator.compare(currentPT, newPT) == -1) {
			k++;
			totalDeltaDominance += calculateDeltaDominance(newPT, currentPT,r);
		}
		
		return totalDeltaDominance/k;
	}
	
	private DominationAmount calculateDominationAmounts(Solution newPT, DominanceComparator comparator) {
		DominationAmount dominationAmount = new DominationAmount();
		
		for (int i=0; i < archive.size(); i++) {
			int result = comparator.compare(newPT, archive.get(i));
			
			if (result == -1) {
				dominationAmount.increaseDominatesAmount();
			} else if (result == 1) {
				dominationAmount.increaseDominatedAmount();
			}
		}
		
		return dominationAmount;
	}
	
	private class DominationAmount{
		private int dominatedAmount;
		private int dominatesAmount;
		
		public int getDominatedAmount() {
			return dominatedAmount;
		}
		
		public void increaseDominatedAmount() {
			this.dominatedAmount++;
		}
		
		public int getDominatesAmount() {
			return dominatesAmount;
		}
		
		public void increaseDominatesAmount() {
			this.dominatesAmount++;
		}
	}

	private MinimumDeltaDominance calculateMinimumDeltaDominance(Solution newPT, double[] r, DominanceComparator comparator) {
		MinimumDeltaDominance minimumDeltaDominance = new MinimumDeltaDominance();
		
		for (int i=0; i < archive.size();i++) {
			if (comparator.compare(newPT, archive.get(i)) == -1) {
				double deltaDominance = calculateDeltaDominance(newPT, archive.get(i), r);
				minimumDeltaDominance.update(deltaDominance, i);
			}
		}
		
		return minimumDeltaDominance;
	}
	
	private class MinimumDeltaDominance{
		double minimumDeltaDominance = Double.MAX_VALUE;
		int index=0;
		
		public void update(double deltaDominance, int index) {
			if (deltaDominance < this.minimumDeltaDominance) {
				this.minimumDeltaDominance = deltaDominance;
				this.index=index;
			}
		}
		
		public double getMinimumDeltaDominance() {
			return minimumDeltaDominance;
		}
		
		public int getIndex() {
			return index;
		}
	}
	
	protected void cluster() {
		this.archive = new SingleLinkageClustering(archive).cluster(hardLimit);
	}

	@Override
	public NondominatedPopulation getResult() {
		return archive;
	}
	
	public Mutation getVariation() {
		return mutation;
	}
	
	@Property("operator")
	public void setVariation(Mutation mutation) {
		this.mutation = mutation;
	}

	@Override
	public Serializable getState() throws NotSerializableException {
		if (!isInitialized()) {
			throw new AlgorithmInitializationException(this, 
					"algorithm not initialized");
		}

		Solution current = this.currentPT;
		List<Solution> archiveList = new ArrayList<Solution>();
		
		if (archive != null) {
			for (Solution solution : archive) {
				archiveList.add(solution);
			}
		}

		return new AMOSAAlgorithmState(getNumberOfEvaluations(), current, archiveList, temperature);
	}

	@Override
	public void setState(Object objState) throws NotSerializableException {
		super.initialize();
		
		AMOSAAlgorithmState state = (AMOSAAlgorithmState)objState;

		numberOfEvaluations = state.getNumberOfEvaluations();
		currentPT = state.getCurrent();
		temperature = state.getTemperature();
		
		if (archive != null) {
			archive.addAll(state.getArchive());
		}
	}
	
	/**
	 * Proxy for serializing and deserializing the state of an
	 * {@code AMOSAAlgorithm}. This proxy supports saving
	 * the {@code numberOfEvaluations}, {@code current} and {@code archive}.
	 */
	private static class AMOSAAlgorithmState implements Serializable {

		private static final long serialVersionUID = 5456685096695481165L;

		/**
		 * The number of objective function evaluations.
		 */
		private final int numberOfEvaluations;

		/**
		 * The current object is stored in a serializable object.
		 */
		private final Solution current;
		
		/**
		 * The temperature
		 */
		private final double temperature;

		/**
		 * The archive stored in a serializable list.
		 */
		private final List<Solution> archive;

		/**
		 * Constructs a proxy to serialize and deserialize the state of an 
		 * {@code AbstractEvolutionaryAlgorithm}.
		 * 
		 * @param numberOfEvaluations the number of objective function
		 *        evaluations
		 * @param population the population stored in a serializable list
		 * @param archive the archive stored in a serializable list
		 */
		public AMOSAAlgorithmState(int numberOfEvaluations,
				Solution current, List<Solution> archive, double temperature) {
			super();
			this.numberOfEvaluations = numberOfEvaluations;
			this.current = current;
			this.archive = archive;
			this.temperature = temperature;
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
		 * Returns the population stored in a serializable list.
		 * 
		 * @return the population stored in a serializable list
		 */
		public Solution getCurrent() {
			return current;
		}

		/**
		 * Returns the archive stored in a serializable list.
		 * 
		 * @return the archive stored in a serializable list
		 */
		public List<Solution> getArchive() {
			return archive;
		}
		
		/**
		 * Returns the temperature.
		 * 
		 * @return the temperature
		 */
		public double getTemperature() {
			return temperature;
		}
	}
}
