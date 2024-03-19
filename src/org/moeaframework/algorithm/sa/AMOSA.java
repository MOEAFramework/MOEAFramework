/* Copyright 2018-2019 Ibrahim DEMIR, 2023 David Hadka
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
import org.moeaframework.core.initialization.RandomInitialization;
import org.moeaframework.core.operator.Mutation;
import org.moeaframework.core.spi.OperatorFactory;

/**
 * Implementation of the simulated annealing-based multiobjective optimization algorithm (AMOSA).  AMOSA incorporates
 * the archive mechanism in order to provide non-dominated set of solutions so called Pareto front.
 * <p>
 * References:
 * <ol>
 *   <li>Sanghamitra Bandyopadhyay, Sriparna Saha, Ujjwal Maulik, Kalyanmoy Deb (2008).  A Simulated Annealing-Based
 *       Multiobjective Optimization Algorithm: AMOSA.  IEEE Transactions on Evolutionary Computation, vol. 12, no. 3,
 *       pp. 269-283.
 * </ol>
 */
public class AMOSA extends AbstractSimulatedAnnealingAlgorithm {

	private double gamma;
	private int softLimit;
	private int hardLimit;

	private int numberOfIterationsPerTemperature;
	private int numberOfHillClimbingIterationsForRefinement;
	
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
		super(problem, initialTemperature, new GeometricCoolingSchedule(alpha), initialization, mutation);
		setGamma(gamma);
		setSoftLimit(softLimit);
		setHardLimit(hardLimit);
		setNumberOfIterationsPerTemperature(numberOfIterationsPerTemperature);
		setNumberOfHillClimbingIterationsForRefinement(numberOfHillClimbingIterationsForRefinement);
		setArchive(new NondominatedPopulation());
		setTerminationCondition(new TemperatureBasedTerminationCondition());
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

		currentPoint = archive.get(PRNG.nextInt(archive.size()));
	}

	@Override
	protected void iterate() {
		for (int i=0; i < this.numberOfIterationsPerTemperature; i++) {
			DominanceComparator comparator = new ParetoDominanceComparator();
			Solution newPoint = mutation.mutate(currentPoint);
			evaluate(newPoint);

			// r is the array of range of each Objective in the archive along with newPoint
			double[] r = calculateR(newPoint);

			// Check The domination status of newPoint and currentPoint
			int comparisonResult = comparator.compare(currentPoint, newPoint);

			if (comparisonResult == -1) {	// Case 1 : currentPoint dominates newPoint 
				double averageDeltaDominance = calculateAverageDeltaDominance(newPoint,r,comparator);
				double probability = 1d/(1d+Math.exp(averageDeltaDominance*temperature));

				if (PRNG.nextDouble() < probability) {
					this.currentPoint = newPoint;
				}
			} else if(comparisonResult == 0) {	// Case 2 : currentPoint and newPoint are non-dominating to each other
				DominationAmount dominationAmount = calculateDominationAmounts(newPoint,comparator);

				if (dominationAmount.getDominatedAmount() > 0) {   // Case 2(a) : newPoint is dominated by k(k>=1) points in the archive
					double averageDeltaDominance = calculateAverageDeltaDominance(newPoint,r,comparator);
					double probability = 1d/(1d+Math.exp(averageDeltaDominance*temperature));

					if (PRNG.nextDouble() < probability) {
						this.currentPoint = newPoint;
					}
				} else if (dominationAmount.getDominatedAmount() == 0 && dominationAmount.getDominatesAmount() == 0) {    // Case 2(b) : newPoint is non-dominating w.r.t all the points in the archive
					this.currentPoint = newPoint;
					this.archive.add(this.currentPoint);

					if(archive.size() > this.softLimit) {
						cluster();
					}
				} else if (dominationAmount.getDominatesAmount() > 0) {    // Case 2(c) : newPoint dominates by k(k>=1) points of the archive
					this.currentPoint = newPoint;
					this.archive.add(this.currentPoint); // Since archive is an instance of NonDominatedPopulation, adding operator automatically removes dominated solutions
				}
			} else {		// Case 3 : newPoint dominates currentPoint
				DominationAmount dominationAmount = calculateDominationAmounts(newPoint,comparator);

				if (dominationAmount.getDominatedAmount()>0) {   // Case 3(a) : newPoint is dominated by k(k>=1) points in the archive
					MinimumDeltaDominance minimumDeltaDominance = calculateMinimumDeltaDominance(newPoint,r,comparator);
					double probability = 1d/(1d+Math.exp(-1d*minimumDeltaDominance.getMinimumDeltaDominance()));

					if (PRNG.nextDouble() < probability) {
						this.currentPoint = archive.get(minimumDeltaDominance.getIndex());
					} else {
						this.currentPoint = newPoint;
					}
				} else if (dominationAmount.getDominatedAmount()==0 && dominationAmount.getDominatesAmount()==0) {    // Case 3(b) : newPoint is non-dominating w.r.t all the points in the archive
					this.currentPoint = newPoint;

					if (!this.archive.add(this.currentPoint)) {
						archive.remove(this.currentPoint);
					} else if (archive.size() > this.softLimit) {
						cluster();
					}
				} else if (dominationAmount.getDominatesAmount() > 0) {    // Case 3(c) : newPoint dominates by k(k>=1) points of the archive
					this.currentPoint = newPoint;
					this.archive.add(this.currentPoint); // Since archive is an instance of NonDominatedPopulation, adding operator automatically removes dominated solutions
				}
			}
		}
	}
	
	@Override
	public void terminate() {
		if (archive.size() > softLimit) {
			cluster();
		}

		super.terminate();
	}
		

	//with respect to III.C. Amount of Domination
	private double[] calculateR(Solution newPoint) {
		double[] r = new double[newPoint.getNumberOfObjectives()];
		double[] worsts = new double[newPoint.getNumberOfObjectives()];
		double[] bests = new double[newPoint.getNumberOfObjectives()];

		for (int i=0; i < newPoint.getNumberOfObjectives(); i++) {
			worsts[i] = newPoint.getObjective(i);
			bests[i] = newPoint.getObjective(i);
		}

		for (int i=0; i < newPoint.getNumberOfObjectives(); i++) {
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
	private double calculateAverageDeltaDominance(Solution newPoint, double[] r, DominanceComparator comparator) {
		double totalDeltaDominance = 0d;
		int k = 0;

		for (int i=0; i < archive.size();i++) {
			if (comparator.compare(archive.get(i), newPoint) == -1) {
				k++;
				totalDeltaDominance += calculateDeltaDominance(newPoint, archive.get(i),r);
			}
		}

		if (comparator.compare(currentPoint, newPoint) == -1) {
			k++;
			totalDeltaDominance += calculateDeltaDominance(newPoint, currentPoint, r);
		}

		return totalDeltaDominance/k;
	}

	private DominationAmount calculateDominationAmounts(Solution newPoint, DominanceComparator comparator) {
		DominationAmount dominationAmount = new DominationAmount();

		for (int i=0; i < archive.size(); i++) {
			int result = comparator.compare(newPoint, archive.get(i));

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

	private MinimumDeltaDominance calculateMinimumDeltaDominance(Solution newPoint, double[] r, DominanceComparator comparator) {
		MinimumDeltaDominance minimumDeltaDominance = new MinimumDeltaDominance();

		for (int i=0; i < archive.size();i++) {
			if (comparator.compare(newPoint, archive.get(i)) == -1) {
				double deltaDominance = calculateDeltaDominance(newPoint, archive.get(i), r);
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

	private void cluster() {
		this.archive = new SingleLinkageClustering(archive).cluster(hardLimit);
	}

}
