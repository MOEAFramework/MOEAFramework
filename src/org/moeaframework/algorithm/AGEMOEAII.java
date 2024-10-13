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
package org.moeaframework.algorithm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.linear.SingularMatrixException;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Selection;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.core.attribute.Fitness;
import org.moeaframework.core.attribute.NormalizedObjectives;
import org.moeaframework.core.attribute.Rank;
import org.moeaframework.core.comparator.ChainedComparator;
import org.moeaframework.core.comparator.FitnessComparator;
import org.moeaframework.core.comparator.RankComparator;
import org.moeaframework.core.configuration.Property;
import org.moeaframework.core.initialization.RandomInitialization;
import org.moeaframework.core.population.NondominatedSorting;
import org.moeaframework.core.population.Population;
import org.moeaframework.core.selection.TournamentSelection;
import org.moeaframework.core.spi.OperatorFactory;
import org.moeaframework.util.LinearAlgebra;
import org.moeaframework.util.Vector;
import org.moeaframework.util.clustering.CachedDistanceMeasure;
import org.moeaframework.util.clustering.DistanceMeasure;

/**
 * Implementation of AGE-MOEA-II, which is an adaptive evolutionary algorithm that estimates the Pareto front geometry
 * and uses this to score solutions during selection.
 * <p>
 * References:
 * <ol>
 *   <li>Panichella, A.  "An Adaptive Evolutionary Algorithm based on Non-Euclidean Geometry for Many-objective
 *       Optimization."  GECCO '19, July 13-17, 2019, Prague, Czech Republic, pp. 595-603.
 *   <li>Panichella, A.  "An Improved Pareto Front Modeling Algorithm for Large-scale Many-Objective Optimization."
 *       GECCO '22, July 9-13, 2022, Boston, MA, USA, pp. 565-573.
 *   <li>The JMetal implementation contributed by A. Panichella - https://github.com/jMetal/jMetal/pull/451
 * </ol>
 */
public class AGEMOEAII extends AbstractEvolutionaryAlgorithm {

	private Selection selection;

	/**
	 * Constructs the AGE-MOEA-II algorithm with default settings.
	 * 
	 * @param problem the problem being solved
	 */
	public AGEMOEAII(Problem problem) {
		this(problem,
				Settings.DEFAULT_POPULATION_SIZE,
				new AGEMOEAIIPopulation(problem.getNumberOfObjectives()),
				new TournamentSelection(2, new ChainedComparator(new RankComparator(), AGEMOEAIIPopulation.COMPARATOR)),
				OperatorFactory.getInstance().getVariation(problem),
				new RandomInitialization(problem));
	}

	/**
	 * Constructs the AGE-MOEA-II algorithm with the specified components.
	 * 
	 * @param problem the problem being solved
	 * @param initialPopulationSize the initial population size
	 * @param population the population used to store solutions
	 * @param selection the selection operator
	 * @param variation the variation operator
	 * @param initialization the initialization method
	 */
	public AGEMOEAII(Problem problem, int initialPopulationSize, AGEMOEAIIPopulation population,
			Selection selection, Variation variation, Initialization initialization) {
		super(problem, initialPopulationSize, population, null, initialization, variation);
		this.selection = selection;
	}
	
	@Override
	public void initialize() {
		super.initialize();
		
		// Compute ranks and fitness of solutions in initial population
		AGEMOEAIIPopulation population = getPopulation();
		population.truncate(population.size());
	}

	@Override
	public void iterate() {
		AGEMOEAIIPopulation population = getPopulation();
		Variation variation = getVariation();
		Population offspring = new Population();
		int populationSize = population.size();

		while (offspring.size() < populationSize) {
			Solution[] parents = selection.select(variation.getArity(), population);
			offspring.addAll(variation.evolve(parents));
		}

		evaluateAll(offspring);

		population.addAll(offspring);
		population.truncate(populationSize);
	}

	@Override
	@Property("operator")
	public void setVariation(Variation variation) {
		super.setVariation(variation);
	}

	@Override
	@Property("populationSize")
	public void setInitialPopulationSize(int initialPopulationSize) {
		super.setInitialPopulationSize(initialPopulationSize);
	}

	@Override
	public AGEMOEAIIPopulation getPopulation() {
		return (AGEMOEAIIPopulation)super.getPopulation();
	}

	/**
	 * Population that computes the AGE-MOEA-II survival score, stored as a fitness value, for truncation.
	 */
	static class AGEMOEAIIPopulation extends Population {
		
		/**
		 * Comparator used to sort solutions by their survival score.
		 */
		public static final FitnessComparator COMPARATOR = new FitnessComparator(true);

		private final int numberOfObjectives;

		private final double[] zeros;
		
		private double[] idealPoint;
		
		private double[] intercepts;
		
		private double p;

		/**
		 * Constructs a new, empty AGE-MOEA-II population.
		 * 
		 * @param numberOfObjectives the number of objectives
		 */
		public AGEMOEAIIPopulation(int numberOfObjectives) {
			super();
			this.numberOfObjectives = numberOfObjectives;

			zeros = Vector.of(numberOfObjectives, 0.0); // constant used for the origin point after normalization
		}

		/**
		 * Truncates the population to the given size using survival scores.
		 * 
		 * @param size the desired size of the population
		 */
		public void truncate(int size) {
			int rank = Rank.BEST_RANK;
			Population selected = new Population();

			NondominatedSorting nds = new NondominatedSorting();
			nds.evaluate(this);

			// Apply survival score (fitness) to first non-dominated front, truncating if necessary
			Population front = filter(Rank.isRank(rank));
			computeSurvivalScore(front);

			if (front.size() > size) {
				front.truncate(size, COMPARATOR);
			}

			selected.addAll(front);

			// Apply the proximity (convergence) score to the remaining fronts
			while (selected.size() < size) {
				rank += 1;
				front = filter(Rank.isRank(rank));
				
				computeConvergenceScore(front);
				
				if (selected.size() + front.size() > size) {
					front.truncate(size - selected.size(), COMPARATOR);
				}
				
				selected.addAll(front);
			}

			clear();
			addAll(selected);
		}

		/**
		 * Computes the convergence score of the front.  This should only be applied to ranks {@code 2, ..., N}.
		 * 
		 * @param front the current front
		 */
		protected void computeConvergenceScore(Population front) {
			normalize(front, idealPoint, intercepts);
			
			for (Solution solution : front) {
				Fitness.setAttribute(solution, minkowskiDistance(solution, zeros, p));
			}
		}

		/**
		 * Computes the survival score, which combines the proximity and diversity scores.  As a side-effect, this
		 * method stores the {@link #idealPoint}, {@link #intercepts}, and estimated Pareto front geometry {@link #p}.
		 * This should only be applied to the first Pareto front (rank {@value Rank#BEST_RANK}).
		 * 
		 * @param front the first Pareto front
		 */
		protected void computeSurvivalScore(Population front) {
			// Normalize the front
			Solution[] extremePoints = getExtremePoints(front);
			idealPoint = front.getLowerBounds();
			intercepts = calculateIntercepts(extremePoints);
			normalize(front, idealPoint, intercepts);

			// Estimate the front geometry
			p = fitGeometry(front, extremePoints);
			
			// Measure distance using proximity and diversity scores.
			DistanceMeasure<Solution> distances = getDistanceMeasure(p);

			// Survival score
			List<Solution> remaining = front.asList();
			List<Solution> assigned = new ArrayList<Solution>();

			for (Solution solution : extremePoints) {
				Fitness.setAttribute(solution, Double.POSITIVE_INFINITY);
				assigned.add(solution);
				remaining.remove(solution);
			}

			while (!remaining.isEmpty()) {
				Pair<Solution, Double> result = findNextSolutionToScore(distances, assigned, remaining);
				Fitness.setAttribute(result.getKey(), result.getValue());
				assigned.add(result.getKey());
				remaining.remove(result.getKey());
			}
		}
		
		/**
		 * Combines the proximity and diversity scores into a distance measure.  These scores are based on the Geodesic
		 * distance using the estimated geometry of the front.  Also note this distance measure is <strong>not</strong>
		 * symmetric!
		 * 
		 * @param p the estimated curvature of the L_p manifold
		 * @return the distance measure
		 */
		DistanceMeasure<Solution> getDistanceMeasure(double p) {
			return new CachedDistanceMeasure<>((i, j) -> {
				double proximity = minkowskiDistance(i, zeros, p);

				double[] first = projectPoint(NormalizedObjectives.getAttribute(i), p);
				double[] second = projectPoint(NormalizedObjectives.getAttribute(j), p);
				double[] midpt = projectPoint(Vector.divide(Vector.add(first, second), 2.0), p);

				return (minkowskiDistance(first, midpt, 2.0) + minkowskiDistance(midpt, second, 2.0)) / proximity;
			}, false);
		}

		/**
		 * Identify the extreme points in the front, where the {@code i}-th index corresponds to the point nearest the
		 * {@code i}-th objective boundary.  The same solution can appear multiple times in the result!
		 * <p>
		 * Note: while the paper mentions the same normalization procedure as {@link NSGAIII} is used, the actual
		 * method to find extreme points is not clearly defined.  Our research into NSGA-III found the authors used a
		 * scalarizing approach, but the JMetal implementation of AGE-MOEA provided by the author uses point-line
		 * distance.  We don't anticipate this causes a significant difference, but for consistency we are following
		 * the author's implementation of point-line distance.
		 * 
		 * @param front the current front
		 * @return the extreme points
		 */
		protected Solution[] getExtremePoints(Population front) {
			Solution[] result = new Solution[numberOfObjectives];

			for (int i = 0; i < numberOfObjectives; i++) {
				double[] weights = new double[numberOfObjectives];
				weights[i] = 1.0;

				double minDistance = Double.POSITIVE_INFINITY;
				Solution minSolution = null;

				for (Solution solution : front) {
					double distance = Vector.pointLineDistance(solution.getCanonicalObjectiveValues(), weights);

					if (distance < minDistance) {
						minDistance = distance;
						minSolution = solution;
					}
				}

				result[i] = minSolution;
			}

			return result;
		}

		/**
		 * Calculate the intercepts.  This follows the same procedure as {@link NSGAIII}, where we compute the
		 * intercepts of a hyperplane fit to the extreme points.  If the extreme points are degenerate, typically due
		 * to duplicate or nearly-identical points, this method falls back to selecting the boundary defined by the
		 * extreme points. 
		 * 
		 * @param extremePoints the extreme points for each objective
		 * @return the intercepts for each objective
		 */
		protected double[] calculateIntercepts(Solution[] extremePoints) {
			boolean degenerate = false;
			double[] intercepts = new double[numberOfObjectives];

			try {
				double[] b = new double[numberOfObjectives];
				double[][] A = new double[numberOfObjectives][numberOfObjectives];

				for (int i = 0; i < numberOfObjectives; i++) {
					b[i] = 1.0;

					for (int j = 0; j < numberOfObjectives; j++) {
						A[i][j] = extremePoints[i].getObjective(j).getCanonicalValue();
					}
				}
				
				double[] result = LinearAlgebra.lsolve(A, b);
				
				for (int i = 0; i < numberOfObjectives; i++) {
					intercepts[i] = 1.0 / result[i];
				}
			} catch (SingularMatrixException e) {
				degenerate = true;
			}

			if (degenerate) {
				for (int i = 0; i < numberOfObjectives; i++) {
					intercepts[i] = extremePoints[i].getObjective(i).getCanonicalValue();
				}
			}

			return intercepts;
		}

		/**
		 * Normalize the solutions in the front using the given ideal point and intercepts.  The normalized objectives
		 * are stored as the {@link NormalizedObjectives} attribute.
		 * 
		 * @param front the current front
		 * @param idealPoint the ideal point
		 * @param intercepts the intercepts
		 */
		protected void normalize(Population front, double[] idealPoint, double[] intercepts) {
			for (Solution solution : front) {
				double[] objectives = new double[numberOfObjectives];

				for (int i = 0; i < numberOfObjectives; i++) {
					objectives[i] = solution.getObjective(i).normalize(idealPoint[i], intercepts[i]).getValue();
				}

				NormalizedObjectives.setAttribute(solution, objectives);
			}
		}

		/**
		 * Estimates the Pareto front geometry of the rank {@value Rank#BEST_RANK} front and extreme points.
		 * 
		 * @param front the rank {@value Rank#BEST_RANK} front
		 * @param extremePoints the exteme points of the rank {@value Rank#BEST_RANK} front
		 * @return the curvature of the L_p manifold (best fit)
		 */
		protected double fitGeometry(Population front, Solution[] extremePoints) {
			double[] d = new double[front.size()];
			Set<Solution> extremePointsSet = new HashSet<Solution>(List.of(extremePoints));

			for (int i = 0; i < front.size(); i++) {
				if (extremePointsSet.contains(front.get(i))) {
					d[i] = Double.POSITIVE_INFINITY;
				} else {
					d[i] = minkowskiDistance(front.get(i), zeros, 2);
				}
			}

			double minDistance = Double.POSITIVE_INFINITY;
			Solution minSolution = null;

			for (int i = 0; i < front.size(); i++) {
				if (d[i] < minDistance) {
					minDistance = d[i];
					minSolution = front.get(i);
				}
			}

			if (minSolution == null) {
				return 1.0;
			}

			double p = findZero(minSolution, 0.001);

			if (Double.isNaN(p) || p < 0.1 || Double.isInfinite(p)) {
				p = 1.0;
			}

			return p;
		}

		/**
		 * Uses the Newton-Raphson method to solve Equation (6) in the referenced paper.
		 * 
		 * @param solution the point used to compute the L_p curvature
		 * @param precision the numeric precision used as a termination condition
		 * @return the curvature of the L_p manifold (best fit)
		 */
		protected double findZero(Solution solution, double precision) {
			double p = 1.0;
			double previousP = p;
			double[] objectives = NormalizedObjectives.getAttribute(solution);

			for (int i = 0; i < 100; i++) {
				double f = evalFunction(objectives, p);
				double df = evalDerivative(objectives, p);

				p = p - f / df;

				if (Math.abs(p - previousP) <= precision) {
					break;
				} else {
					previousP = p;
				}
			}

			return p;
		}

		/**
		 * Identifies and computes the score for the next solution.  This is based on Algorithm 2 in the original
		 * AGE-MOEA paper.
		 * 
		 * @param distances the distance map
		 * @param assigned solutions that have already been assigned a score
		 * @param remaining solutions that are remaining and need to be assigned a score
		 * @return the next solution to assign a score
		 */
		protected Pair<Solution, Double> findNextSolutionToScore(DistanceMeasure<Solution> distances,
				List<Solution> assigned, List<Solution> remaining) {
			double bestScore = 0.0;
			Solution bestSolution = null;

			for (Solution remainingSolution : remaining) {
				double min1 = Double.POSITIVE_INFINITY;
				double min2 = Double.POSITIVE_INFINITY;

				for (Solution assignedSolution : assigned) {
					double distance = distances.compute(remainingSolution, assignedSolution);
					
					if (distance < min1) {
						min2 = min1;
						min1 = distance;
					} else if (distance < min2) {
						min2 = distance;
					}
				}
				
				if (min1 + min2 >= bestScore) {
					bestScore = min1 + min2;
					bestSolution = remainingSolution;
				}
			}
						
			return Pair.of(bestSolution, bestScore);
		}

		/**
		 * Projects the given point onto the L_p manifold.
		 * 
		 * @param point the point to project
		 * @param p the curvature of the L_p manifold
		 * @return the projected point
		 */
		protected double[] projectPoint(double[] point, double p) {
			double[] projection = point.clone();
			double dist = minkowskiDistance(projection, zeros, p);

			for (int i = 0; i < numberOfObjectives; i++) {
				projection[i] *= 1.0 / dist;
			}

			return projection;
		}

		/**
		 * Computes the Minkowski distance between the normalized objective values of a solution and a target point.
		 * 
		 * @param source the solution
		 * @param targetPoint the target point
		 * @param p the curvature of the L_p manifold
		 * @return the Minkowski distance
		 */
		protected double minkowskiDistance(Solution source, double[] targetPoint, double p) {
			return minkowskiDistance(NormalizedObjectives.getAttribute(source), targetPoint, p);
		}

		/**
		 * Computes the Minkowski distance between two points
		 * 
		 * @param sourcePoint the source point
		 * @param targetPoint the target point
		 * @param p the curvature of the L_p manifold
		 * @return the Minkowski distance
		 */
		protected double minkowskiDistance(double[] sourcePoint, double[] targetPoint, double p) {
			double value = 0.0;

			for (int i = 0; i < numberOfObjectives; i++) {
				value += Math.pow(Math.abs(sourcePoint[i] - targetPoint[i]), p);
			}

			return Math.pow(value, 1.0 / p);
		}

		/**
		 * The function for the L_p manifold, optimized by the Newton-Raphson method.
		 * 
		 * @param point the point used to compute the L_p curvature
		 * @param p the current estimated curvature of the L_p manifold
		 * @return the value of the function
		 */
		protected double evalFunction(double[] point, double p) {
			double result = 0.0;

			for (int i = 0; i < numberOfObjectives; i++) {
				result += Math.pow(Math.abs(point[i]), p);
			}

			return Math.log(result);
		}

		/**
		 * The derivative function for the L_p manifold, optimized by the Newton-Raphson method.
		 * 
		 * @param point the point used to compute the L_p curvature
		 * @param p the current estimated curvature of the L_p manifold
		 * @return the derivative value of the function
		 */
		protected double evalDerivative(double[] point, double p) {
			double numerator = 0.0;
			double denominator = 0.0;

			for (int i = 0; i < numberOfObjectives; i++) {
				if (point[i] > 0.0) {
					numerator += Math.pow(point[i], p) * Math.log(point[i]);
					denominator += Math.pow(point[i], p);
				}
			}

			if (denominator == 0.0) {
				return 1.0;
			}

			return numerator / denominator;
		}

	}

}
