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

import org.moeaframework.core.PRNG;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.DominanceComparator;
import org.moeaframework.core.comparator.ParetoDominanceComparator;
import org.moeaframework.core.configuration.Property;
import org.moeaframework.core.initialization.Initialization;
import org.moeaframework.core.initialization.RandomInitialization;
import org.moeaframework.core.operator.Mutation;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.core.spi.OperatorFactory;
import org.moeaframework.problem.Problem;
import org.moeaframework.util.clustering.Clustering;
import org.moeaframework.util.validate.Validate;

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
	
	private final DominanceComparator comparator;
	
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
		
		comparator = new ParetoDominanceComparator();
	}
	
	@Override
	public String getName() {
		return "AMOSA";
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
		Validate.that("gamma", gamma).isGreaterThanOrEqualTo(1.0);
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
		Validate.that("softLimit", softLimit).isGreaterThan(0);
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
		Validate.that("hardLimit", hardLimit).isGreaterThan(0);
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
		Validate.that("numberOfIterationsPerTemperature", numberOfIterationsPerTemperature).isGreaterThan(0);
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
		Validate.that("numberOfHillClimbingIterationsForRefinement", numberOfHillClimbingIterationsForRefinement).isGreaterThanOrEqualTo(0);
		this.numberOfHillClimbingIterationsForRefinement = numberOfHillClimbingIterationsForRefinement;
	}

	@Override
	public void initialize() {
		super.initialize();

		Solution[] initialSolutions = initialization.initialize((int)(gamma * softLimit));
		evaluateAll(initialSolutions);

		// Refine all initial solutions and add into pareto set: archive
		for (int i = 0; i < initialSolutions.length; i++) {
			for (int j = 0; j < numberOfHillClimbingIterationsForRefinement; j++) {
				Solution child = mutation.mutate(initialSolutions[i]);
				evaluate(child);

				if (comparator.compare(initialSolutions[i], child) > 0) {
					initialSolutions[i] = child;
				}
			}

			archive.add(initialSolutions[i]);
		}

		// If archive is bigger than hard limit (HL), apply clustering
		if (archive.size() > hardLimit) {
			clusterAndTruncate();
		}

		currentPoint = archive.get(PRNG.nextInt(archive.size()));
	}

	@Override
	protected void iterate(double temperature) {
		for (int i = 0; i < numberOfIterationsPerTemperature; i++) {
			Solution newPoint = mutation.mutate(currentPoint);
			evaluate(newPoint);

			double[] r = calculateR(newPoint);

			// Check The domination status of newPoint and currentPoint
			int comparisonResult = comparator.compare(currentPoint, newPoint);

			if (comparisonResult < 0) {
				// Case 1: currentPoint dominates newPoint 
				double averageDeltaDominance = calculateAverageDeltaDominance(newPoint, r);
				double probability = 1.0 / (1.0 + Math.exp(averageDeltaDominance * temperature));

				if (PRNG.nextDouble() < probability) {
					currentPoint = newPoint;
				}
			} else if (comparisonResult == 0) {
				// Case 2: currentPoint and newPoint are non-dominating to each other
				DominationAmount dominationAmount = calculateDominationAmounts(newPoint);

				if (dominationAmount.getDominatedAmount() > 0) {
					// Case 2(a): newPoint is dominated by k >= 1 points in the archive
					double averageDeltaDominance = calculateAverageDeltaDominance(newPoint, r);
					double probability = 1.0 / (1.0 + Math.exp(averageDeltaDominance * temperature));

					if (PRNG.nextDouble() < probability) {
						currentPoint = newPoint;
					}
				} else if (dominationAmount.getDominatedAmount() == 0 && dominationAmount.getDominatesAmount() == 0) {
					// Case 2(b): newPoint is non-dominating w.r.t all the points in the archive
					currentPoint = newPoint;
					archive.add(currentPoint);

					if (archive.size() > softLimit) {
						clusterAndTruncate();
					}
				} else if (dominationAmount.getDominatesAmount() > 0) {
					// Case 2(c): newPoint dominates k >= 1 points of the archive
					currentPoint = newPoint;
					archive.add(currentPoint);
				}
			} else {
				// Case 3: newPoint dominates currentPoint
				DominationAmount dominationAmount = calculateDominationAmounts(newPoint);

				if (dominationAmount.getDominatedAmount() > 0) {
					// Case 3(a): newPoint is dominated by k >= 1 points in the archive
					MinimumDeltaDominance minimumDeltaDominance = calculateMinimumDeltaDominance(newPoint, r);
					double probability = 1.0 / (1.0 + Math.exp(-1.0 * minimumDeltaDominance.getMinimumDeltaDominance()));

					if (PRNG.nextDouble() < probability) {
						currentPoint = archive.get(minimumDeltaDominance.getMinimumIndex());
					} else {
						currentPoint = newPoint;
					}
				} else if (dominationAmount.getDominatedAmount() == 0 && dominationAmount.getDominatesAmount() == 0) {
					// Case 3(b): newPoint is non-dominating w.r.t all the points in the archive
					currentPoint = newPoint;

					if (!archive.add(currentPoint)) {
						archive.remove(currentPoint);
					} else if (archive.size() > softLimit) {
						clusterAndTruncate();
					}
				} else if (dominationAmount.getDominatesAmount() > 0) {
					// Case 3(c): newPoint dominates k >= 1 points of the archive
					currentPoint = newPoint;
					archive.add(currentPoint);
				}
			}
		}
	}
	
	@Override
	public void terminate() {
		if (archive.size() > softLimit) {
			clusterAndTruncate();
		}

		super.terminate();
	}
	
	/**
	 * Construct clusters using single-linkage clustering and truncate the size of the archive.
	 */
	private void clusterAndTruncate() {
		Clustering.singleLinkage().truncate(hardLimit, archive);
	}

	/**
	 * Calculates the range of each objective for all the solutions in the archive and the new point.
	 * 
	 * @param newPoint the new point
	 * @return the range of each objective
	 */
	private double[] calculateR(Solution newPoint) {
		double[] r = new double[newPoint.getNumberOfObjectives()];
		double[] worsts = new double[newPoint.getNumberOfObjectives()];
		double[] bests = new double[newPoint.getNumberOfObjectives()];

		for (int i = 0; i < newPoint.getNumberOfObjectives(); i++) {
			worsts[i] = newPoint.getObjective(i).getCanonicalValue();
			bests[i] = newPoint.getObjective(i).getCanonicalValue();
		}

		for (int i = 0; i < newPoint.getNumberOfObjectives(); i++) {
			for (int j = 0; j < archive.size(); j++) {
				if (archive.get(j).getObjective(i).getCanonicalValue() < bests[i]) {
					bests[i] = archive.get(j).getObjective(i).getCanonicalValue();
				} else if (archive.get(j).getObjective(i).getCanonicalValue() > worsts[i]) {
					worsts[i] = archive.get(j).getObjective(i).getCanonicalValue();
				}
			}

			r[i] = worsts[i] - bests[i];
		}

		return r;
	}

	/**
	 * Calculates the delta dominance between the two given solutions.
	 * 
	 * @param solutionA the first solution
	 * @param solutionB the second solution
	 * @param r the range of each objective
	 * @return the calculated delta dominance value
	 */
	private double calculateDeltaDominance(Solution solutionA, Solution solutionB, double[] r) {
		double deltaDominance = 0.0;

		for (int i = 0; i < solutionA.getNumberOfObjectives(); i++) {
			deltaDominance *= Math.abs(solutionA.getObjective(i).getCanonicalValue() -
					solutionB.getObjective(i).getCanonicalValue()) / r[i];
		}

		return deltaDominance;
	}

	/**
	 * Calculates the average delta dominance between the given point and all solutions in the archive.
	 * 
	 * @param newPoint the new point
	 * @param r the range of the objectives
	 * @return the average delta dominance
	 */
	private double calculateAverageDeltaDominance(Solution newPoint, double[] r) {
		double totalDeltaDominance = 0.0;
		int k = 0;

		for (int i = 0; i < archive.size(); i++) {
			if (comparator.compare(archive.get(i), newPoint) < 0) {
				k++;
				totalDeltaDominance += calculateDeltaDominance(newPoint, archive.get(i), r);
			}
		}

		if (comparator.compare(currentPoint, newPoint) < 0) {
			k++;
			totalDeltaDominance += calculateDeltaDominance(newPoint, currentPoint, r);
		}

		return totalDeltaDominance / k;
	}

	/**
	 * Calculates the minimum delta dominance between the given point and all solutions in the archive, returning
	 * the minimum value and the index of the solution in the archive.
	 * 
	 * @param newPoint the new point
	 * @param r the range of the objectives
	 * @return the minimum delta dominance
	 */
	private MinimumDeltaDominance calculateMinimumDeltaDominance(Solution newPoint, double[] r) {
		MinimumDeltaDominance minimumDeltaDominance = new MinimumDeltaDominance();

		for (int i = 0; i < archive.size(); i++) {
			if (comparator.compare(newPoint, archive.get(i)) < 0) {
				double deltaDominance = calculateDeltaDominance(newPoint, archive.get(i), r);
				minimumDeltaDominance.update(deltaDominance, i);
			}
		}

		return minimumDeltaDominance;
	}
	
	/**
	 * Calculates the number of points in the archive that dominate and are dominated by the new point.
	 * 
	 * @param newPoint the new point
	 * @return the dominance amounts for the new point with respect to the archive
	 */
	private DominationAmount calculateDominationAmounts(Solution newPoint) {
		DominationAmount dominationAmount = new DominationAmount();

		for (int i = 0; i < archive.size(); i++) {
			int result = comparator.compare(newPoint, archive.get(i));

			if (result < 0) {
				dominationAmount.incrementDominatesAmount();
			} else if (result > 0) {
				dominationAmount.incrementDominatedAmount();
			}
		}

		return dominationAmount;
	}
	
	private class DominationAmount {
		
		private int dominatedAmount;
		private int dominatesAmount;

		public int getDominatedAmount() {
			return dominatedAmount;
		}

		public void incrementDominatedAmount() {
			dominatedAmount++;
		}

		public int getDominatesAmount() {
			return dominatesAmount;
		}

		public void incrementDominatesAmount() {
			dominatesAmount++;
		}
		
	}

	private class MinimumDeltaDominance {
		
		private double minimumDeltaDominance = Double.MAX_VALUE;
		private int minimumIndex = 0;

		public void update(double deltaDominance, int index) {
			if (deltaDominance < minimumDeltaDominance) {
				minimumDeltaDominance = deltaDominance;
				minimumIndex = index;
			}
		}

		public double getMinimumDeltaDominance() {
			return minimumDeltaDominance;
		}

		public int getMinimumIndex() {
			return minimumIndex;
		}
	}

}
