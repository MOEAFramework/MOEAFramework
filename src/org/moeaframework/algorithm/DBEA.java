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
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.core.comparator.ObjectiveComparator;
import org.moeaframework.core.configuration.Property;
import org.moeaframework.core.configuration.Validate;
import org.moeaframework.core.initialization.RandomInitialization;
import org.moeaframework.core.spi.OperatorFactory;
import org.moeaframework.util.TypedProperties;
import org.moeaframework.util.weights.NormalBoundaryDivisions;
import org.moeaframework.util.weights.NormalBoundaryIntersectionGenerator;

/* The original Matlab version of I-DBEA was written by Md. Asafuddoula, Tapabrata Ray and Ruhul Sarker.  This class
 * has been tested against their Matlab version to ensure it produces identical results.  See the DBEATest.java class
 * for more information about the testing procedure.
 * 
 * A Java version of I-DBEA written by Md Asafuddoula was also cross-referenced when developing this class.  The Java
 * version was released under the GNU LGPL, version 3 or later, and is copyright 2015 Md Asafuddoula.
 * 
 * Note: There are some differences between Md Asafuddoula's newer Java version and their older Matlab version,
 * including the removal of corner sort.  Experimental tests on their Java version indicate performance between the
 * two versions differ, becoming more substantial with more objectives, with the Matlab version appearing superior.
 * For this reason, we have replicated the Matlab version within the MOEA Framework.
 */

/**
 * Implementation of the Improved Decomposition-Based Evolutionary Algorithm (I-DBEA).  This implementation is based
 * on the Matlab version published by the original authors.
 * <p>
 * References:
 * <ol>
 *   <li>Asafuddoula, M., T. Ray, and R. Sarker (2015).  "A Decomposition-Based Evolutionary Algorithm for
 *       Many-Objective Optimization."  IEEE Transaction on Evolutionary Computation, 19(3):445-460.
 *   <li><a href="http://www.mdolab.net/Ray/Research-Data/Matlab-DBEA.rar">Matlab-DBEA.rar</a>
 * </ol>
 */
public class DBEA extends AbstractEvolutionaryAlgorithm {
	
	/**
	 * Set to {@code true} to remove random permutations to make unit testing easier.
	 */
	static boolean TESTING_MODE = false;

	/**
	 * The ideal point used for scaling the objectives.
	 */
	double[] idealPoint;
	
	/**
	 * The intercepts used for scaling the objectives.
	 */
	double[] intercepts;
	
	/**
	 * The reference points (weights).
	 */
	List<double[]> weights;
	
	/**
	 * The solutions that define the corners.
	 */
	Population corner;
	
	/**
	 * The number of divisions for generating reference points.
	 */
	private NormalBoundaryDivisions divisions;
	
	/**
	 * Constructs a new instance of the DBEA algorithm with default settings.
	 * 
	 * @param problem the problem being solved
	 */
	public DBEA(Problem problem) {
		this(problem, NormalBoundaryDivisions.forProblem(problem));
	}
	
	/**
	 * Constructs a new instance of the DBEA algorithm with the given number of divisions.
	 * 
	 * @param problem the problem being solved
	 * @param divisions the number of divisions
	 */
	public DBEA(Problem problem, NormalBoundaryDivisions divisions) {
		this(problem,
				new RandomInitialization(problem),
				OperatorFactory.getInstance().getVariation(problem),
				divisions);
	}

	/**
	 * Constructs a new instance of the DBEA algorithm.
	 * 
	 * @param problem the problem being solved
	 * @param initialization the initialization method
	 * @param variation the variation operator
	 * @param divisions the number of divisions
	 */
	private DBEA(Problem problem, Initialization initialization, Variation variation, NormalBoundaryDivisions divisions) {
		super(problem, 100 /* overwritten by setDivisions */, new Population(), null, initialization, variation);
		setDivisions(divisions);
	}
	
	/**
	 * Returns the number of divisions used to generate reference points.
	 * 
	 * @return the number of divisions
	 */
	public NormalBoundaryDivisions getDivisions() {
		return divisions;
	}
	
	/**
	 * Sets the number of divisions used to generate reference points.  This method can only be called before
	 * initializing the algorithm.
	 * 
	 * @param divisions the number of divisions
	 */
	public void setDivisions(NormalBoundaryDivisions divisions) {
		assertNotInitialized();
		Validate.notNull("divisions", divisions);
		this.divisions = divisions;
		
		setInitialPopulationSize(divisions.getNumberOfReferencePoints(problem));
	}
	
	@Override
	@Property("operator")
	public void setVariation(Variation variation) {
		super.setVariation(variation);
	}

	@Override
	protected void initialize() {
		super.initialize();

		generateWeights();
		preserveCorner();
		initializeIdealPointAndIntercepts();
	}
	
	/**
	 * Generates the reference directions (weights) based on the number of outer and inner divisions.
	 */
	void generateWeights() {
		NormalBoundaryIntersectionGenerator generator = new NormalBoundaryIntersectionGenerator(
				problem.getNumberOfObjectives(), divisions);
		
		weights = generator.generate();
	}
	
	/**
	 * Preserve the solutions that comprise the corners of the Pareto front.
	 */
	void preserveCorner() {
		Population feasibleSolutions = getFeasibleSolutions(getPopulation());
		
		if (feasibleSolutions.size() >= 2*problem.getNumberOfObjectives()) {
			corner = corner_sort(feasibleSolutions);
		}
	}
	
	/**
	 * Generates a random permutation of the given length.
	 * 
	 * @param length the length of the permutation
	 * @return the random permutation
	 */
	int[] randomPermutation(int length) {
		int[] permutation = new int[length];
		
		for (int i = 0; i < length; i++) {
			permutation[i] = i;
		}
		
		PRNG.shuffle(permutation);
		
		return permutation;
	}

	@Override
	protected void iterate() {
		Population population = getPopulation();
		Variation variation = getVariation();
		
		int[] permutation = randomPermutation(population.size());

		for (int i = 0; i < population.size(); i++) {
			int n = permutation[i];
			
			Solution[] parents = new Solution[2];
			parents[0] = population.get(i);
			parents[1] = population.get(n);
			
			Solution[] children = variation.evolve(parents);

			evaluate(children[0]);

			if (!checkDomination(children[0])) {
				updateIdealPointAndIntercepts(children[0]);
				updatePopulation(children[0]);
			}
		}
		
		// this call is likely not necessary, but is included in the Matlab version
		preserveCorner();
	}
	
	/**
	 * Returns the feasible solutions.
	 * 
	 * @param population the entire population containing feasible and infeasible solutions
	 * @return the feasible solutions in the population
	 */
	Population getFeasibleSolutions(Population population) {
		Population feasibleSolutions = new Population(population);
		feasibleSolutions.filter(Solution::isFeasible);
		return feasibleSolutions;
	}
	
	/**
	 * Returns the non-dominated (rank 0) front.
	 * 
	 * @param population the entire population
	 * @return the solutions that are non-dominated
	 */
	private Population getNondominatedFront(Population population) {
		NondominatedPopulation front = new NondominatedPopulation();
		front.addAll(population);
		return front;
	}

	/**
	 * Initializes the ideal point and intercepts based on the bounds of the initial population.
	 */
	void initializeIdealPointAndIntercepts() {
		idealPoint = new double[problem.getNumberOfObjectives()];
		intercepts = new double[problem.getNumberOfObjectives()];
		
		for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
			idealPoint[i] = Double.POSITIVE_INFINITY;
			intercepts[i] = Double.NEGATIVE_INFINITY;
		}
		
		Population feasibleSolutions = getFeasibleSolutions(getPopulation());
		
		

		if (!feasibleSolutions.isEmpty()) {
			for (int i = 0; i < feasibleSolutions.size(); i++) {
				for (int j = 0; j < problem.getNumberOfObjectives(); j++) {
					idealPoint[j] = Math.min(idealPoint[j], feasibleSolutions.get(i).getObjective(j));
					intercepts[j] = Math.max(intercepts[j], feasibleSolutions.get(i).getObjective(j));
				}
			}
		}
	}

	/**
	 * Returns the solution with the largest objective value for the given objective.
	 * 
	 * @param objective the objective
	 * @param population the population of solutions
	 * @return the solution with the largest objective value
	 */
	Solution largestObjectiveValue(int objective, Population population) {
		Solution largest = null;
		double value = Double.NEGATIVE_INFINITY;
		
		for (Solution solution : population) {
			if (solution.getObjective(objective) > value) {
				largest = solution;
				value = solution.getObjective(objective);
			}
		}
		
		return largest;
	}
	
	/**
	 * Returns a copy of the population sorted by the objective value in ascending order.
	 * 
	 * @param objective the objective
	 * @param population the population
	 * @return a copy of the population ordered by the objective value
	 */
	Population orderBySmallestObjective(final int objective, Population population) {
		Population result = new Population();
		result.addAll(population);
		result.sort(new ObjectiveComparator(objective));
		return result;
	}
	
	/**
	 * Returns a copy of the population sorted by the sum-of-squares of all but one objective.
	 * 
	 * @param objective the ignored objective
	 * @param population the population
	 * @return a copy of the population ordered by the sum-of-squares of all but one objective
	 */
	Population orderBySmallestSquaredValue(final int objective, Population population) {
		Population result = new Population();
		result.addAll(population);
		
		result.sort(new Comparator<Solution>() {

			@Override
			public int compare(Solution s1, Solution s2) {
				double sum1 = 0.0;
				double sum2 = 0.0;
				
				for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
					if (i != objective) {
						sum1 += Math.pow(s1.getObjective(i), 2.0);
						sum2 += Math.pow(s2.getObjective(i), 2.0);
					}
				}
				
				return Double.compare(sum1, sum2);
			}
			
		});
		
		return result;
	}
	
	/**
	 * Counts the number of unique solutions in the population.  This method checks both the identify of the solutions
	 * (if they are the same Java object) and the value of the individual objectives.
	 * 
	 * @param population the population
	 * @return the number of unique solutions in the population
	 */
	int numberOfUniqueSolutions(Population population) {
		int count = 0;
		
		outer: for (int i = 0; i < population.size(); i++) {
			Solution solution1 = population.get(i);
			
			for (int j = 0; j < i; j++) {
				Solution solution2 = population.get(j);
				
				if (solution1 == solution2 || solution1.euclideanDistance(solution2) < Settings.EPS) {
					continue outer;
				}
			}
			
			count++;
		}
		
		return count;
	}
	
	/**
	 * Updates the ideal point and intercepts given the new solution.
	 * 
	 * @param solution the new solution
	 */
	void updateIdealPointAndIntercepts(Solution solution) {
		if (solution.isFeasible()) {
			// update the ideal point
			for (int j = 0; j < problem.getNumberOfObjectives(); j++) {
				idealPoint[j] = Math.min(idealPoint[j], solution.getObjective(j));
				intercepts[j] = Math.max(intercepts[j], solution.getObjective(j));
			}

			// compute the axis intercepts
			Population feasibleSolutions = getFeasibleSolutions(getPopulation());
			feasibleSolutions.add(solution);
			
			Population nondominatedSolutions = getNondominatedFront(feasibleSolutions);
			
			if (!nondominatedSolutions.isEmpty()) {
				// find the points with the largest value in each objective
				Population extremePoints = new Population();
				
				for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
					extremePoints.add(largestObjectiveValue(i, nondominatedSolutions));
				}
				
				if (numberOfUniqueSolutions(extremePoints) < problem.getNumberOfObjectives()) {
					for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
						intercepts[i] = extremePoints.get(i).getObjective(i);
					}
				} else {
					try {
						RealMatrix b = new Array2DRowRealMatrix(problem.getNumberOfObjectives(), 1);
						RealMatrix A = new Array2DRowRealMatrix(problem.getNumberOfObjectives(),
								problem.getNumberOfObjectives());
						
						for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
							b.setEntry(i, 0, 1.0);
	
							for (int j = 0; j < problem.getNumberOfObjectives(); j++) {
								A.setEntry(i, j, extremePoints.get(i).getObjective(j));
							}
						}
	
						double numerator = new LUDecomposition(A).getDeterminant();
						b.scalarMultiply(numerator);
						RealMatrix normal = MatrixUtils.inverse(A).multiply(b);
						
						for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
							intercepts[i] = numerator / normal.getEntry(i, 0);
							
							if (intercepts[i] <= 0 || Double.isNaN(intercepts[i]) || Double.isInfinite(intercepts[i])) {
								intercepts[i] = extremePoints.get(i).getObjective(i);
							}
						}
					} catch (RuntimeException e) {
						for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
							intercepts[i] = extremePoints.get(i).getObjective(i);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Returns the allowed constraint violation.
	 * 
	 * @param population the population
	 * @return the allowed constraint violation
	 */
	double constraintApproach(Population population) {
		double feasible = 1;
		double violation = 0.0;
		
		for (int i = 0; i < population.size(); i++) {
			if (population.get(i).violatesConstraints()) {
				violation += population.get(i).getSumOfConstraintViolations();
			} else {
				feasible++;
			}
		}
		
		return (feasible / population.size()) * (violation / population.size());
	}

	/**
	 * Updates the population with the child solution.
	 * 
	 * @param child the child solution
	 */
	void updatePopulation(Solution child) {
		Population population = getPopulation();
		
		double eps = 0; // unused in I-DBEA
		double eps_con = constraintApproach(population);
		boolean success = false;
		
		// update the corners if necessary
		if (corner != null && child.isFeasible()) {
			corner.add(child);
			corner = corner_sort(corner);
		}

		double[] f2 = normalizedObjectives(child);
		int[] order = randomPermutation(population.size());
		
		if (TESTING_MODE) {
			for (int i = 0; i < order.length; i++) {
				order[i] = i;
			}
		}
		
		for (int i = 0; i < population.size(); i++) {
			int j = order[i];
			double[] weight = weights.get(j);
			double[] f1 = normalizedObjectives(population.get(j));
			
			double d1_parent = distanceD1(f1, weight);
			double d1_child = distanceD1(f2, weight);
			double d2_parent = distanceD2(f1, d1_parent);
			double d2_child = distanceD2(f2, d1_child);
			double cv_parent = population.get(j).getSumOfConstraintViolations();
			double cv_child = child.getSumOfConstraintViolations();
			
			if(cv_child < eps_con && cv_parent < eps_con || (cv_child == cv_parent)) {
				if (compareSolution(d1_child, d2_child, d1_parent, d2_parent, eps)) {
					population.replace(j, child);
					success = true;
				}
			}
			
			if (cv_child < cv_parent) {
				population.replace(j, child);
				success = true;
			}
			
			if (success) {
				break;
			}
		}
	}
	
	/**
	 * Performs corner sort to identify 2*M solutions, where M is the number of objectives, that comprise the corners
	 * of the population.
	 * 
	 * @param population the population
	 * @return the 2*M corner solutions
	 */
	Population corner_sort(Population population) {
		Population unique = new Population();
		Population duplicates = new Population();
		
		// remove duplicate solutions
		for (int i = 0; i < population.size(); i++) {
			if (unique.contains(population.get(i))) {
				duplicates.add(population.get(i));
			} else {
				boolean isDuplicate = false;
				
				for (int j = 0; j < unique.size(); j++) {
					if (Arrays.equals(unique.get(j).getObjectives(), population.get(i).getObjectives())) {
						duplicates.add(population.get(i));
						isDuplicate = true;
						break;
					}
				}
				
				if (!isDuplicate) {
					unique.add(population.get(i));
				}
			}
		}
		
		// sort the solutions
		List<Population> sortedSets = new ArrayList<Population>();
		
		for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
			sortedSets.add(orderBySmallestObjective(i, unique));
		}
		
		for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
			sortedSets.add(orderBySmallestSquaredValue(i, unique));
		}
		
		// identify the corners
		Population result = new Population();
		int current_id = 0;
		int current_f = 0;
		
		while (result.size() < unique.size()) {
			Solution r = sortedSets.get(current_f).get(current_id);
			
			if (!result.contains(r)) {
				result.add(r);
			}
			
			current_f++;
			
			if (current_f >= 2*problem.getNumberOfObjectives()) {
				current_f = 0;
				current_id++;
			}
		}
		
		result.addAll(duplicates);
		
		// reduce the set to 2*M solutions
		Population prunedSet = new Population();
		
		for (int i = 0; i < 2*problem.getNumberOfObjectives(); i++) {
			prunedSet.add(result.get(i));
		}
		
		return prunedSet;
	}
	
	/**
	 * Returns {@code true} if this solution is dominated by any member of the population.
	 * 
	 * @param solution the solution
	 * @return {@code true} if the solution is dominated; {@code false} otherwise
	 */
	boolean checkDomination(Solution solution) {
		if (solution.violatesConstraints()) {
			return false;
		}
		
		// include the corner solutions
		Population combinedPopulation = new Population();
		combinedPopulation.addAll(getPopulation());
		
		if (corner != null) {
			combinedPopulation.addAll(corner);
		}
		
		// check for dominance
		for (Solution otherSolution : getFeasibleSolutions(combinedPopulation)) {
			int count = 0;
			
			for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
				if (otherSolution.getObjective(i) < solution.getObjective(i)) {
					count++;
				}
			}
			
			if (count == problem.getNumberOfObjectives()) {
				return true;
			}
			
		}
		
		return false;
	}

	/**
	 * Computes the distance away from the ideal point along the reference direction.
	 * 
	 * @param f the normalized objective values for the point
	 * @param w the reference direction
	 * @return the distance
	 */
	private double distanceD1(double[] f, double[] w) {
		double dn = normVector(w);
		
		for (int j = 0; j < problem.getNumberOfObjectives(); j++) {
			w[j] = w[j] / dn;
		}
		
		return innerproduct(f, w);
	}

	/**
	 * Computes the perpendicular distance to the reference direction.
	 * 
	 * @param f the normalized objective values for the point
	 * @param d1 the reference direction
	 * @return the perpendicular distance
	 */
	private double distanceD2(double[] f, double d1) {
		return Math.sqrt(Math.pow(normVector(f), 2) - Math.pow(d1, 2));
	}
	
	/**
	 * Computes the norm of a vector.
	 * 
	 * @param z the vector
	 * @return the norm value
	 */
	private double normVector(double[] z) {
		double sum = 0;

		for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
			sum += z[i] * z[i];
		}

		return Math.sqrt(sum);
	}

	/**
	 * Computes the inner product of two vectors.
	 * 
	 * @param vec1 the first vector
	 * @param vec2 the second vector
	 * @return the inner product value
	 */
	private double innerproduct(double[] vec1, double[] vec2) {
		double sum = 0;

		for (int i = 0; i < vec1.length; i++) {
			sum += vec1[i] * vec2[i];
		}

		return sum;
	}

	/**
	 * Returns {@code true} if the child should replace the parent; {@code false} otherwise.
	 * 
	 * @param d1_child the D1 length of the child
	 * @param d2_child the D2 length of the child
	 * @param d1_parent the D1 length of the parent
	 * @param d2_parent the D2 length of the parent
	 * @param eps the objective alignment option, set to 0 in I-DBEA
	 * @return {@code true} if the child should replace the parent; {@code false} otherwise
	 */
	private boolean compareSolution(double d1_child, double d2_child, double d1_parent, double d2_parent, double eps) {
		if ((d2_child == d2_parent) || ((d2_child < eps) && (d2_parent < eps))) {
			if (d1_child < d1_parent) {
				return true;
			}
		} else if (d2_child < d2_parent) {
			return true;
		}

		return false;
	}

	/**
	 * Returns the normalized objective values for the given solution.
	 * 
	 * @param solution the solution
	 * @return the normalized objective values
	 */
	private double[] normalizedObjectives(Solution solution) {
		double[] objectiveValues = new double[problem.getNumberOfObjectives()];
		
		for (int j = 0; j < problem.getNumberOfObjectives(); j++) {
			objectiveValues[j] = (solution.getObjective(j) - idealPoint[j]) / (intercepts[j] - idealPoint[j]);
		}
		
		return objectiveValues;
	}

	@Override
	public NondominatedPopulation getResult() {
		NondominatedPopulation result = super.getResult();
		result.addAll(corner);
		return result;
	}
	
	@Override
	public void applyConfiguration(TypedProperties properties) {		
		NormalBoundaryDivisions divisions = NormalBoundaryDivisions.tryFromProperties(properties);
		
		if (divisions != null) {
			setDivisions(divisions);
		}
		
		super.applyConfiguration(properties);
	}

	@Override
	public TypedProperties getConfiguration() {
		TypedProperties properties = super.getConfiguration();
		properties.addAll(divisions.toProperties());
		return properties;
	}

}