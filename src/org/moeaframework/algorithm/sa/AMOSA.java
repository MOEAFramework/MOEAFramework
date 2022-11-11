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
import org.moeaframework.core.Initialization;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.core.comparator.DominanceComparator;
import org.moeaframework.core.comparator.ParetoDominanceComparator;

/**
 * Implementation of the simulated annealing-based multiobjective optimization algorithm (AMOSA).
 * Algorithm incorporates the archive mechanism in order to provide non-dominated set of solutions
 * so called Pareto front.
 * <p>
 * References:
 * <ol>
 *   <li>Sanghamitra Bandyopadhyay, Sriparna Saha, Ujjwal Maulik, Kalyanmoy Deb(2008).
 *   A Simulated Annealing-Based Multiobjective Optimization Algorithm: AMOSA.
 *   IEEE Transactions on Evolutionary Computation, vol. 12, no. 3, pp. 269-283.
 * </ol>
 * 
 * @preview
 */
public class AMOSA extends AbstractSimulatedAnnealingAlgorithm {
	
	protected final Initialization initialization;
	protected final Variation variation;

	/**
	 * The archive storing the non-dominated solutions.
	 */
	protected NondominatedPopulation archive;
	
	protected final int softLimit;
	protected final int hardLimit;
	
	protected Solution currentPT;
	
	protected final double alpha;
	protected final int numberOfIterationsPerTemperature;
	protected final int numberOfHillClimbingIterationsForRefinement;
	
	public AMOSA(Problem problem, Initialization initialization, Variation variation, int softLimit, int hardLimit, double tMin, double tMax, double alpha, int numberOfIterationsPerTemperature,
			int numberOfHillClimbingIterationsForRefinement) {
		super(problem,tMin,tMax);
		this.initialization = initialization;
		this.variation = variation;
		this.softLimit = softLimit;
		this.hardLimit = hardLimit;
		this.alpha = alpha;
		this.numberOfIterationsPerTemperature = numberOfIterationsPerTemperature;
		this.numberOfHillClimbingIterationsForRefinement = numberOfHillClimbingIterationsForRefinement;
		this.archive = new NondominatedPopulation();
	}
	
	@Override
	public void initialize() {
		super.initialize();

		Solution[] initialSolutions = initialization.initialize();
		evaluateAll(initialSolutions);

		//refine all initial solutions and add into pareto set: archive
		ParetoDominanceComparator paretoDominanceComparator = new ParetoDominanceComparator();
		
		for (int i=0; i < initialSolutions.length; i++) {
			for (int j=0; j < this.numberOfHillClimbingIterationsForRefinement; j++) {
				Solution child = this.variation.evolve(new Solution[] { initialSolutions[i] })[0];
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
		this.temperature = tMax;
	}
	

	@Override
	protected void iterate() {
		if (temperature >= tMin) {
			for (int i=0; i < this.numberOfIterationsPerTemperature; i++) {
				DominanceComparator comparator = new ParetoDominanceComparator();
				Solution newPT = variation.evolve(new Solution[] {currentPT})[0];
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
				}else if(comparisonResult == 0) {	// Case 2 : currentPt and newPT are non-dominating to each other
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
