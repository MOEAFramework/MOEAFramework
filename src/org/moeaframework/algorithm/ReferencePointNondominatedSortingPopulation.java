/* Copyright 2009-2015 David Hadka
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

import static org.moeaframework.core.FastNondominatedSorting.RANK_ATTRIBUTE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.math3.stat.StatUtils;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.NondominatedSortingPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Population;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.DominanceComparator;
import org.moeaframework.core.comparator.RankComparator;
import org.moeaframework.util.Vector;

/**
 * Implementation of the reference-point-based nondominated sorting method
 * for NSGA-III.  NSGA-III includes an additional parameter, the number of
 * divisions, that controls the spacing of reference points.  For large
 * objective counts, an alternate two-layered approach was also proposed
 * allowing the user to specify the divisions on the outer and inner layer.
 * When using the two-layered approach, the number of outer divisions should
 * less than the number of objectives, otherwise it will generate reference
 * points overlapping with the inner layer.  If there are M objectives and
 * p divisions, then {@code binomialCoefficient(M+p-1, p)} reference points are
 * generated.
 * <p>
 * Unfortunately, since no official implementation has been released by the
 * original authors, we have made our best effort to implement the algorithm as
 * described in the journal article.  We would like to thank Tsung-Che Chiang
 * for developing the first publicly available implementation of NSGA-III in
 * C++.
 * <p>
 * References:
 * <ol>
 *   <li>Deb, K. and Jain, H.  "An Evolutionary Many-Objective Optimization
 *       Algorithm Using Reference-Point-Based Nondominated Sorting Approach,
 *       Part I: Solving Problems With Box Constraints."  IEEE Transactions on
 *       Evolutionary Computation, 18(4):577-601, 2014.
 *   <li><a href="http://web.ntnu.edu.tw/~tcchiang/publications/nsga3cpp/nsga3cpp.htm">C++ Implementation by Tsung-Che Chiang</a>
 * </ol>
 */
public class ReferencePointNondominatedSortingPopulation extends NondominatedSortingPopulation {

	/**
	 * The name of the attribute for storing the normalized objectives.
	 */
	private static final String NORMALIZED_OBJECTIVES = "Normalized Objectives";

	/**
	 * The number of objectives.
	 */
	private final int numberOfObjectives;

	/**
	 * The number of outer divisions.
	 */
	private final int divisionsOuter;

	/**
	 * The number of inner divisions, or {@code 0} if no inner divisions should
	 * be used.
	 */
	private final int divisionsInner;

	/**
	 * The ideal point, updated each iteration.
	 */
	private double[] idealPoint;

	/**
	 * The list of reference points, or weights.
	 */
	private List<double[]> weights;

	/**
	 * Constructs an empty population that maintains the {@code rank}
	 * attribute for its solutions.
	 * 
	 * @param numberOfObjectives the number of objectives
	 * @param divisions the number of divisions
	 */
	public ReferencePointNondominatedSortingPopulation(int numberOfObjectives,
			int divisions) {
		super();
		this.numberOfObjectives = numberOfObjectives;
		this.divisionsOuter = divisions;
		this.divisionsInner = 0;

		setup();
	}

	/**
	 * Constructs a new population with the specified solutions that maintains
	 * the {@code rank} attribute for its solutions.
	 * 
	 * @param numberOfObjectives the number of objectives
	 * @param divisions the number of divisions
	 * @param comparator the dominance comparator
	 * @param iterable the solutions used to initialize this population
	 */
	public ReferencePointNondominatedSortingPopulation(
			int numberOfObjectives, int divisions,
			DominanceComparator comparator,
			Iterable<? extends Solution> iterable) {
		super(comparator, iterable);
		this.numberOfObjectives = numberOfObjectives;
		this.divisionsOuter = divisions;
		this.divisionsInner = 0;

		setup();
	}

	/**
	 * Constructs an empty population that maintains the {@code rank} attribute
	 * for its solutions.
	 * 
	 * @param numberOfObjectives the number of objectives
	 * @param divisions the number of divisions
	 * @param comparator the dominance comparator
	 */
	public ReferencePointNondominatedSortingPopulation(
			int numberOfObjectives, int divisions,
			DominanceComparator comparator) {
		super(comparator);
		this.numberOfObjectives = numberOfObjectives;
		this.divisionsOuter = divisions;
		this.divisionsInner = 0;

		setup();
	}

	/**
	 * Constructs a new population with the specified solutions that maintains
	 * the {@code rank} attribute for its solutions.
	 * 
	 * @param numberOfObjectives the number of objectives
	 * @param divisions the number of divisions
	 * @param iterable the solutions used to initialize this population
	 */
	public ReferencePointNondominatedSortingPopulation(
			int numberOfObjectives, int divisions,
			Iterable<? extends Solution> iterable) {
		super(iterable);
		this.numberOfObjectives = numberOfObjectives;
		this.divisionsOuter = divisions;
		this.divisionsInner = 0;

		setup();
	}

	/**
	 * Constructs an empty population that maintains the {@code rank} attribute
	 * for its solutions.
	 * 
	 * @param numberOfObjectives the number of objectives
	 * @param divisionsOuter the number of outer divisions
	 * @param divisionsInner the number of inner divisions
	 */
	public ReferencePointNondominatedSortingPopulation(int numberOfObjectives,
			int divisionsOuter, int divisionsInner) {
		super();
		this.numberOfObjectives = numberOfObjectives;
		this.divisionsOuter = divisionsOuter;
		this.divisionsInner = divisionsInner;

		setup();
	}

	/**
	 * Constructs a new population with the specified solutions that maintains
	 * the {@code rank} attribute for its solutions.
	 * 
	 * @param numberOfObjectives the number of objectives
	 * @param divisionsOuter the number of outer divisions
	 * @param divisionsInner the number of inner divisions
	 * @param comparator the dominance comparator
	 * @param iterable the solutions used to initialize this population
	 */
	public ReferencePointNondominatedSortingPopulation(
			int numberOfObjectives, int divisionsOuter, int divisionsInner,
			DominanceComparator comparator,
			Iterable<? extends Solution> iterable) {
		super(comparator, iterable);
		this.numberOfObjectives = numberOfObjectives;
		this.divisionsOuter = divisionsOuter;
		this.divisionsInner = divisionsInner;

		setup();
	}

	/**
	 * Constructs an empty population that maintains the {@code rank} attribute
	 * for its solutions.
	 * 
	 * @param numberOfObjectives the number of objectives
	 * @param divisionsOuter the number of outer divisions
	 * @param divisionsInner the number of inner divisions
	 * @param comparator the dominance comparator
	 */
	public ReferencePointNondominatedSortingPopulation(
			int numberOfObjectives, int divisionsOuter, int divisionsInner,
			DominanceComparator comparator) {
		super(comparator);
		this.numberOfObjectives = numberOfObjectives;
		this.divisionsOuter = divisionsOuter;
		this.divisionsInner = divisionsInner;

		setup();
	}

	/**
	 * Constructs a new population with the specified solutions that maintains
	 * the {@code rank} attribute for its solutions.
	 * 
	 * @param numberOfObjectives the number of objectives
	 * @param divisionsOuter the number of outer divisions
	 * @param divisionsInner the number of inner divisions
	 * @param iterable the solutions used to initialize this population
	 */
	public ReferencePointNondominatedSortingPopulation(
			int numberOfObjectives, int divisionsOuter, int divisionsInner,
			Iterable<? extends Solution> iterable) {
		super(iterable);
		this.numberOfObjectives = numberOfObjectives;
		this.divisionsOuter = divisionsOuter;
		this.divisionsInner = divisionsInner;

		setup();
	}

	/**
	 * Initializes the ideal point and reference points (weights).
	 */
	private void setup() {
		idealPoint = new double[numberOfObjectives];
		Arrays.fill(idealPoint, Double.POSITIVE_INFINITY);

		if (divisionsInner > 0) {
			if (divisionsOuter >= numberOfObjectives) {
				System.err.println("The specified number of outer divisions produces intermediate reference points, recommend setting divisionsOuter < numberOfObjectives.");
			}

			weights = generateWeights(null, divisionsOuter);

			// offset the inner weights
			List<double[]> inner = generateWeights(null, divisionsInner);

			for (int i = 0; i < inner.size(); i++) {
				double[] weight = inner.get(i);

				for (int j = 0; j < weight.length; j++) {
					weight[j] = (1.0/numberOfObjectives + weight[j])/2;
				}
			}

			weights.addAll(inner);
		} else {
			if (divisionsOuter < numberOfObjectives) {
				System.err.println("No intermediate reference points will be generated for the specified number of divisions, recommend increasing divisions");
			}

			weights = generateWeights(null, divisionsOuter);
		}
	}

	/**
	 * Extends the given array with a new element appended to the end.
	 * 
	 * @param array the original array, or {@code null} for an empty array
	 * @param value the value to append
	 * @return the extended array
	 */
	private double[] extend(double[] array, double value) {
		double[] result = null;

		if (array == null) {
			result = new double[1];
		} else {
			result = Arrays.copyOf(array, array.length + 1);
		}

		result[result.length-1] = value;
		return result;
	}

	/**
	 * Generates the reference points (weights) for the given number of
	 * divisions.
	 * 
	 * @param point the partially generated point, or {@code null} for the first
	 *        invocation
	 * @param divisions the number of divisions
	 * @return the list of reference points
	 */
	private List<double[]> generateWeights(double[] point, int divisions) {
		List<double[]> result = new ArrayList<double[]>();
		double sum = 0.0;
		int N = divisions;

		if (point != null) {
			sum = StatUtils.sum(point);
			N = (int)((1.0 - sum)*divisions);
		} else {
			point = new double[0];
		}

		if (point.length < numberOfObjectives-1) {
			for (int i = 0; i <= N; i++) {
				result.addAll(generateWeights(extend(point, i/(double)divisions), divisions));
			}
		} else {
			result.add(extend(point, 1.0 - sum));
		}

		return result;
	}

	/**
	 * Updates the ideal point given the solutions currently in this population.
	 */
	private void updateIdealPoint() {
		for (Solution solution : this) {
			if (solution.getNumberOfObjectives() != numberOfObjectives) {
				throw new FrameworkException("incorrect number of objectives");
			}

			for (int i = 0; i < numberOfObjectives; i++) {
				idealPoint[i] = Math.min(idealPoint[i], solution.getObjective(i));
			}
		}
	}

	/**
	 * Offsets the solutions in this population by the ideal point.  This
	 * method does not modify the objective values, it creates a new attribute
	 * with the name {@value NORMALIZED_OBJECTIVES}.
	 */
	private void translateByIdealPoint() {
		for (Solution solution : this) {
			double[] objectives = solution.getObjectives();

			for (int i = 0; i < numberOfObjectives; i++) {
				objectives[i] -= idealPoint[i];
			}

			solution.setAttribute(NORMALIZED_OBJECTIVES, objectives);
		}
	}

	/**
	 * Normalizes the solutions in this population by the given intercepts
	 * (or scaling factors).  This method does not modify the objective values,
	 * it modifies the {@value NORMALIZED_OBJECTIVES} attribute.
	 * 
	 * @param intercepts the intercepts used for scaling
	 */
	private void normalizeByIntercepts(double[] intercepts) {
		for (Solution solution : this) {
			double[] objectives = (double[])solution.getAttribute(NORMALIZED_OBJECTIVES);

			for (int i = 0; i < solution.getNumberOfObjectives(); i++) {
				objectives[i] /= intercepts[i];
			}
		}
	}

	/**
	 * The Chebyshev achievement scalarizing function.
	 * 
	 * @param solution the normalized solution
	 * @param weights the reference point (weight vector)
	 * @return the value of the scalarizing function
	 */
	protected double achievementScalarizingFunction(Solution solution, double[] weights) {
		double result = Double.NEGATIVE_INFINITY;
		double[] objectives = (double[])solution.getAttribute(NORMALIZED_OBJECTIVES);

		for (int i = 0; i < solution.getNumberOfObjectives(); i++) {
			result = Math.max(result, objectives[i]/weights[i]);
		}

		return result;
	}

	/**
	 * Returns the extreme point in the given objective.  The extreme point is
	 * the point that minimizes the achievement scalarizing function using a
	 * reference point near the given objective.
	 * 
	 * @param objective the objective index
	 * @return the extreme point in the given objective
	 */
	private Solution findExtremePoint(int objective) {
		double eps = 0.001;
		double[] weights = new double[numberOfObjectives];

		for (int i = 0; i < weights.length; i++) {
			if (i == objective) {
				weights[i] = 1.0 - eps*(weights.length-1);
			} else {
				weights[i] = eps;
			}
		}

		Solution result = get(0);
		double resultASF = Double.POSITIVE_INFINITY;

		for (int i = 1; i < size(); i++) {
			Solution solution = get(i);
			double solutionASF = achievementScalarizingFunction(solution, weights);

			if (solutionASF < resultASF) {
				result = solution;
				resultASF = solutionASF;
			}
		}

		return result;
	}

	/**
	 * Returns the extreme points for all objectives.
	 * 
	 * @return an array of extreme points, each index corresponds to each
	 *         objective
	 */
	private Solution[] extremePoints() {
		Solution[] result = new Solution[numberOfObjectives];

		for (int i = 0; i < numberOfObjectives; i++) {
			result[i] = findExtremePoint(i);
		}

		return result;
	}

	/**
	 * Calculates the intercepts between the hyperplane formed by the extreme
	 * points and each axis.
	 * 
	 * @return an array of the intercept points for each objective
	 */
	private double[] calculateIntercepts() {
		Solution[] extremePoints = extremePoints();
		double[] intercepts = new double[numberOfObjectives];

		try {
			double[] b = new double[numberOfObjectives];
			double[][] A = new double[numberOfObjectives][numberOfObjectives];

			for (int i = 0; i < numberOfObjectives; i++) {
				double[] objectives = (double[])extremePoints[i].getAttribute(NORMALIZED_OBJECTIVES);

				b[i] = 1.0;

				for (int j = 0; j < numberOfObjectives; j++) {
					A[i][j] = objectives[j];
				}
			}

			double[] result = lsolve(A, b);

			for (int i = 0; i < numberOfObjectives; i++) {
				intercepts[i] = 1.0 / result[i];
			}
		} catch (RuntimeException e) {
			for (int i = 0; i < numberOfObjectives; i++) {
				double[] objectives = (double[])extremePoints[i].getAttribute(NORMALIZED_OBJECTIVES);
				intercepts[i] = objectives[i];
			}
		}

		// avoid small intercepts since we will be dividing by this number
		for (int i = 0; i < numberOfObjectives; i++) {
			if (intercepts[i] < Settings.EPS) {
				intercepts[i] = Settings.EPS;
			}
		}

		return intercepts;
	}

	// Gaussian elimination with partial pivoting
	// Copied from http://introcs.cs.princeton.edu/java/95linear/GaussianElimination.java.html
	/**
	 * Gaussian elimination with partial pivoting.
	 * 
	 * @param A the A matrix
	 * @param b the b vector
	 * @return the solved equation using Gaussian elimination
	 */
	private double[] lsolve(double[][] A, double[] b) {
		int N  = b.length;

		for (int p = 0; p < N; p++) {
			// find pivot row and swap
			int max = p;

			for (int i = p + 1; i < N; i++) {
				if (Math.abs(A[i][p]) > Math.abs(A[max][p])) {
					max = i;
				}
			}

			double[] temp = A[p];
			A[p] = A[max];
			A[max] = temp;

			double t = b[p];
			b[p] = b[max];
			b[max] = t;

			// singular or nearly singular
			if (Math.abs(A[p][p]) <= Settings.EPS) {
				throw new RuntimeException("Matrix is singular or nearly singular");
			}

			// pivot within A and b
			for (int i = p + 1; i < N; i++) {
				double alpha = A[i][p] / A[p][p];
				b[i] -= alpha * b[p];

				for (int j = p; j < N; j++) {
					A[i][j] -= alpha * A[p][j];
				}
			}
		}

		// back substitution
		double[] x = new double[N];

		for (int i = N - 1; i >= 0; i--) {
			double sum = 0.0;

			for (int j = i + 1; j < N; j++) {
				sum += A[i][j] * x[j];
			}

			x[i] = (b[i] - sum) / A[i][i];
		}

		return x;
	}

	/**
	 * Returns the minimum perpendicular distance between a point and a line.
	 * 
	 * @param line the line originating from the origin
	 * @param point the point
	 * @return the minimum distance
	 */
	private double pointLineDistance(double[] line, double[] point) {
		return Vector.magnitude(Vector.subtract(Vector.multiply(
				Vector.dot(line, point) / Vector.dot(line, line),
				line), point));
	}

	/**
	 * Associates each solution to the nearest reference point, returning a
	 * list-of-lists.  The outer list maps to each reference point using their
	 * index.  The inner list is an unordered collection of the solutions
	 * associated with the reference point.
	 * 
	 * @param population the population of solutions
	 * @return the association of solutions to reference points
	 */
	private List<List<Solution>> associateToReferencePoint(Population population) {
		List<List<Solution>> result = new ArrayList<List<Solution>>();

		for (int i = 0; i < weights.size(); i++) {
			result.add(new ArrayList<Solution>());
		}

		for (Solution solution : population) {
			double[] objectives = (double[])solution.getAttribute(NORMALIZED_OBJECTIVES);
			double minDistance = Double.POSITIVE_INFINITY;
			int minIndex = -1;

			for (int i = 0; i < weights.size(); i++) {
				double distance = pointLineDistance(weights.get(i), objectives);

				if (distance < minDistance) {
					minDistance = distance;
					minIndex = i;
				}
			}

			result.get(minIndex).add(solution);
		}

		return result;
	}

	/**
	 * Returns the solution with the minimum perpendicular distance to the
	 * given reference point.
	 * 
	 * @param solutions the list of solutions being considered
	 * @param weight the reference point
	 * @return the solution nearest to the reference point
	 */
	private Solution findSolutionWithMinimumDistance(List<Solution> solutions, double[] weight) {
		double minDistance = Double.POSITIVE_INFINITY;
		Solution minSolution = null;

		for (int i = 0; i < solutions.size(); i++) {
			double[] objectives = (double[])solutions.get(i).getAttribute(NORMALIZED_OBJECTIVES);
			double distance = pointLineDistance(weight, objectives);

			if (distance < minDistance) {
				minDistance = distance;
				minSolution = solutions.get(i);
			}
		}

		return minSolution;
	}

	/**
	 * Truncates the population to the specified size using the reference-point
	 * based nondominated sorting method.
	 */
	@Override
	public void truncate(int size, Comparator<? super Solution> comparator) {
		if (size() > size) {
			// update the ideal point
			updateIdealPoint();

			// translate objectives so the ideal point is at the origin
			translateByIdealPoint();

			// calculate the extreme points, calculate the hyperplane defined
			// by the extreme points, and compute the intercepts
			normalizeByIntercepts(calculateIntercepts());

			// identify points in last front and remove from population; we
			// need to be careful to avoid alternating calls to remove and get
			// to prevent unnecessary nondominated sorting updates
			sort(new RankComparator());

			int maxRank = (Integer)super.get(size-1).getAttribute(RANK_ATTRIBUTE);
			Population front = new Population();

			for (int i = 0; i < size(); i++) {
				int rank = (Integer)get(i).getAttribute(RANK_ATTRIBUTE);

				if (rank >= maxRank) {
					front.add(get(i));
				}
			}

			removeAll(front);

			for (int i = front.size()-1; i >= 0; i--) {
				int rank = (Integer)front.get(i).getAttribute(RANK_ATTRIBUTE);

				if (rank > maxRank) {
					front.remove(i);
				}
			}

			// associate each solution to a reference point
			List<List<Solution>> members = associateToReferencePoint(this);
			List<List<Solution>> potentialMembers = associateToReferencePoint(front);
			Set<Integer> excluded = new HashSet<Integer>();

			// loop over niche-preservation operation until population is full
			while (size() < size) {
				// identify reference point with the fewest associated members
				int minIndex = -1;
				int minCount = Integer.MAX_VALUE;

				for (int i = 0; i < members.size(); i++) {
					if (!excluded.contains(i) && (members.get(i).size() < minCount)) {
						minIndex = i;
						minCount = members.get(i).size();
					}
				}

				// add associated solution
				if (minCount == 0) {
					if (potentialMembers.get(minIndex).isEmpty()) {
						excluded.add(minIndex);
					} else {
						Solution minSolution = findSolutionWithMinimumDistance(potentialMembers.get(minIndex), weights.get(minIndex));
						add(minSolution);
						members.get(minIndex).add(minSolution);
						potentialMembers.get(minIndex).remove(minSolution);
					}
				} else {
					if (potentialMembers.get(minIndex).isEmpty()) {
						excluded.add(minIndex);
					} else {
						Solution randSolution = PRNG.nextItem(potentialMembers.get(minIndex));
						add(randSolution);
						members.get(minIndex).add(randSolution);
						potentialMembers.get(minIndex).remove(randSolution);
					}
				}
			}
		}
	}

	/**
	 * Truncates the population to the specified size using the reference-point
	 * based nondominated sorting method.
	 */
	@Override
	public void truncate(int size) {
		truncate(size, new RankComparator());
	}

}
