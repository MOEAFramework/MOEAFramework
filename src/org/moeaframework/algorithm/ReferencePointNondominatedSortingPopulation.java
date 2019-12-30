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
package org.moeaframework.algorithm;

import static org.moeaframework.core.FastNondominatedSorting.RANK_ATTRIBUTE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.NondominatedSortingPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Population;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.DominanceComparator;
import org.moeaframework.core.comparator.RankComparator;
import org.moeaframework.util.Vector;
import org.moeaframework.util.weights.NormalBoundaryIntersectionGenerator;

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
 *   <li>Deb, K. and Jain, H.  "Handling Many-Objective Problems Using an
 *       Improved NSGA-II Procedure.  WCCI 2012 IEEE World Contress on
 *       Computational Intelligence, Brisbane, Australia, June 10-15, 2012.
 *   <li><a href="http://web.ntnu.edu.tw/~tcchiang/publications/nsga3cpp/nsga3cpp.htm">C++ Implementation by Tsung-Che Chiang</a>
 * </ol>
 */
public class ReferencePointNondominatedSortingPopulation extends NondominatedSortingPopulation {

	/**
	 * The name of the attribute for storing the normalized objectives.
	 */
	static final String NORMALIZED_OBJECTIVES = "Normalized Objectives";

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
	double[] idealPoint;

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

		initialize();
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

		initialize();
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

		initialize();
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

		initialize();
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

		initialize();
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

		initialize();
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

		initialize();
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

		initialize();
	}

	/**
	 * Initializes the ideal point and reference points (weights).
	 */
	private void initialize() {
		idealPoint = new double[numberOfObjectives];
		Arrays.fill(idealPoint, Double.POSITIVE_INFINITY);
		
		weights = new NormalBoundaryIntersectionGenerator(numberOfObjectives,
				divisionsOuter, divisionsInner).generate();
	}

	/**
	 * Updates the ideal point given the solutions currently in this population.
	 */
	protected void updateIdealPoint() {
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
	protected void translateByIdealPoint() {
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
	protected void normalizeByIntercepts(double[] intercepts) {
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
	protected static double achievementScalarizingFunction(Solution solution, double[] weights) {
		double max = Double.NEGATIVE_INFINITY;
		double[] objectives = (double[])solution.getAttribute(NORMALIZED_OBJECTIVES);

		for (int i = 0; i < solution.getNumberOfObjectives(); i++) {
			max = Math.max(max, objectives[i]/weights[i]);
		}

		return max;
	}

	/**
	 * Returns the extreme point in the given objective.  The extreme point is
	 * the point that minimizes the achievement scalarizing function using a
	 * reference point near the given objective.
	 * 
	 * The NSGA-III paper (1) does not provide any details on the scalarizing
	 * function, but an earlier paper by the authors (2) where some precursor
	 * experiments are performed does define a possible function, replicated
	 * below.
	 * 
	 * @param objective the objective index
	 * @return the extreme point in the given objective
	 */
	protected Solution findExtremePoint(int objective) {
		double eps = 0.000001;
		double[] weights = new double[numberOfObjectives];

		for (int i = 0; i < numberOfObjectives; i++) {
			if (i == objective) {
				weights[i] = 1.0;
			} else {
				weights[i] = eps;
			}
		}

		Solution result = null;
		double resultASF = Double.POSITIVE_INFINITY;

		for (int i = 0; i < size(); i++) {
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
	 * points and each axis.  The original paper (1) is unclear how to handle
	 * degenerate cases, which occurs more frequently at larger dimensions.  In
	 * this implementation, we simply use the nadir point for scaling.
	 * 
	 * @return an array of the intercept points for each objective
	 */
	protected double[] calculateIntercepts() {
		Solution[] extremePoints = extremePoints();
		boolean degenerate = false;
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
			degenerate = true;
		}

		if (!degenerate) {
			// avoid small or negative intercepts
			for (int i = 0; i < numberOfObjectives; i++) {
				if (intercepts[i] < 0.001) {
					degenerate = true;
					break;
				}
			}
		}
		
		if (degenerate) {
			Arrays.fill(intercepts, Double.NEGATIVE_INFINITY);
			
			for (Solution solution : this) {
				for (int i = 0; i < numberOfObjectives; i++) {
					intercepts[i] = Math.max(
							Math.max(intercepts[i], Settings.EPS),
							solution.getObjective(i));
				}
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
	protected static double pointLineDistance(double[] line, double[] point) {
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
	protected List<List<Solution>> associateToReferencePoint(Population population) {
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
	protected Solution findSolutionWithMinimumDistance(List<Solution> solutions, double[] weight) {
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
			// remove all solutions past the last front
			sort(new RankComparator());

			int maxRank = (Integer)super.get(size-1).getAttribute(RANK_ATTRIBUTE);
			Population front = new Population();

			for (int i = 0; i < size(); i++) {
				int rank = (Integer)get(i).getAttribute(RANK_ATTRIBUTE);
				
				if (rank > maxRank) {
					front.add(get(i));
				}
			}

			removeAll(front);
			
			// update the ideal point
			updateIdealPoint();

			// translate objectives so the ideal point is at the origin
			translateByIdealPoint();

			// calculate the extreme points, calculate the hyperplane defined
			// by the extreme points, and compute the intercepts
			normalizeByIntercepts(calculateIntercepts());

			// get the solutions in the last front
			front = new Population();

			for (int i = 0; i < size(); i++) {
				int rank = (Integer)get(i).getAttribute(RANK_ATTRIBUTE);

				if (rank == maxRank) {
					front.add(get(i));
				}
			}

			removeAll(front);

			// associate each solution to a reference point
			List<List<Solution>> members = associateToReferencePoint(this);
			List<List<Solution>> potentialMembers = associateToReferencePoint(front);
			Set<Integer> excluded = new HashSet<Integer>();

			// loop over niche-preservation operation until population is full
			while (size() < size) {
				// identify reference point with the fewest associated members
				List<Integer> minIndices = new ArrayList<Integer>();
				int minCount = Integer.MAX_VALUE;

				for (int i = 0; i < members.size(); i++) {
					if (!excluded.contains(i) && (members.get(i).size() <= minCount)) {
						if (members.get(i).size() < minCount) {
							minIndices.clear();
							minCount = members.get(i).size();
						}
						
						minIndices.add(i);
					}
				}
				
				int minIndex = PRNG.nextItem(minIndices);

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
