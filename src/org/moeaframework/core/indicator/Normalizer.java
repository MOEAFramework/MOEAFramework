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
package org.moeaframework.core.indicator;

import java.util.Arrays;
import java.util.Iterator;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;

/**
 * Normalizes populations so that all objectives reside in the range {@code [0, 1]}.  This normalization ignores
 * infeasible solutions, so the resulting normalized population contains no infeasible solutions.  A reference set
 * should be used to ensure the normalization is uniformly applied.
 */
public class Normalizer {
	
	/**
	 * The problem.
	 */
	private final Problem problem;
	
	/**
	 * A delta added to the maximum value (used when computing the reference point for hypervolume calculations).
	 */
	private final double delta;
	
	/**
	 * The reference point if defined (used for hypervolume calculations).
	 */
	private final double[] referencePoint;
	
	/**
	 * The minimum value for each objective.
	 */
	private double[] minimum;

	/**
	 * The maximum value for each objective.
	 */
	private double[] maximum;
	
	/**
	 * Constructs a normalizer for normalizing populations so that all objectives reside in the range {@code [0, 1]}.
	 * This constructor derives the minimum and maximum bounds from the given population.
	 * 
	 * @param problem the problem
	 * @param population the population defining the minimum and maximum bounds
	 * @throws IllegalArgumentException if the population set contains fewer than two solutions, or if there exists
	 *         an objective with an empty range
	 */
	public Normalizer(Problem problem, Population population) {
		super();
		this.problem = problem;
		this.delta = 0.0;
		this.referencePoint = null;

		calculateRanges(population);		
		checkRanges();
	}
	
	/**
	 * Constructs a normalizer for normalizing populations so that all objectives reside in the range {@code [0, 1]}.
	 * This constructor derives the minimum and maximum bounds from the given population and a given delta.  This
	 * delta offsets the maximum bounds, typically for hypervolume calculations, to ensure there is a non-zero distance
	 * between the extremal points and the reference point.
	 * 
	 * @param problem the problem
	 * @param population the population defining the minimum and maximum bounds
	 * @param delta a delta added to the maximum value
	 * @throws IllegalArgumentException if the population set contains fewer than two solutions, or if there exists
	 *         an objective with an empty range
	 */
	public Normalizer(Problem problem, Population population, double delta) {
		super();
		this.problem = problem;
		this.delta = delta;
		this.referencePoint = null;
		
		calculateRanges(population);		
		checkRanges();
	}
	
	/**
	 * Constructs a normalizer for normalizing populations so that all objectives reside in the range {@code [0, 1]}.
	 * This constructor derives the minimum and maximum bounds from the given population and a given reference point.
	 * This is typically used by hypervolume calculations, which measures the volume of spacing between each solution
	 * and the reference point.
	 * 
	 * @param problem the problem
	 * @param population the population defining the minimum and maximum bounds
	 * @param referencePoint the reference point; if {@code null}, the bounds are based on the population
	 * @throws IllegalArgumentException if the population set contains fewer than two solutions, or if there exists
	 *         an objective with an empty range
	 */
	public Normalizer(Problem problem, Population population, double[] referencePoint) {
		super();
		this.problem = problem;
		this.delta = 0.0;
		this.referencePoint = referencePoint == null ? null : referencePoint.clone();

		calculateRanges(population);		
		checkRanges();
	}
	
	/**
	 * Constructs a normalizer for normalizing population so that all objectives reside in the range {@code [0, 1]}.
	 * This constructor allows defining the minimum and maximum bounds explicitly.
	 * 
	 * @param problem the problem
	 * @param minimum the minimum bounds of each objective
	 * @param maximum the maximum bounds of each objective
	 */
	public Normalizer(Problem problem, double[] minimum, double[] maximum) {
		super();
		this.problem = problem;
		this.delta = 0.0;
		this.referencePoint = null;
		this.minimum = new double[problem.getNumberOfObjectives()];
		this.maximum = new double[problem.getNumberOfObjectives()];
		
		for (int j = 0; j < problem.getNumberOfObjectives(); j++) {
			this.minimum[j] = minimum[j >= minimum.length ? minimum.length-1 : j];
			this.maximum[j] = maximum[j >= maximum.length ? maximum.length-1 : j];
		}
		
		checkRanges();
	}
	
	/**
	 * Calculates the range of each objective given the population.  The range is defined by the minimum and maximum
	 * value of each objective.
	 * 
	 * @param population the population defining the minimum and maximum bounds
	 * @throws IllegalArgumentException if the population contains fewer than two solutions
	 */
	private void calculateRanges(Population population) {
		Population feasibleSolutions = new Population(population);
		feasibleSolutions.filter(Solution::isFeasible);
		
		if (feasibleSolutions.size() < 2) {
			throw new IllegalArgumentException("requires at least two solutions to compute bounds for normalization");
		}
		
		minimum = feasibleSolutions.getLowerBounds();
		maximum = feasibleSolutions.getUpperBounds();
		
		if (referencePoint != null) {
			for (int j = 0; j < problem.getNumberOfObjectives(); j++) {
				maximum[j] = referencePoint[j >= referencePoint.length ? referencePoint.length-1 : j];
			}
		} else if (delta > 0.0) {
			for (int j = 0; j < problem.getNumberOfObjectives(); j++) {
				maximum[j] += delta * (maximum[j] - minimum[j]);
			}
		}
		
		if (Settings.isVerbose()) {
			System.err.println("Normalizer for " + problem.getName() + " created with bounds " +
					Arrays.toString(minimum) + " and " + Arrays.toString(maximum));
		}
	}
	
	/**
	 * Checks if any objective has a range that is smaller than machine precision.
	 * 
	 * @throws IllegalArgumentException if any objective has a range that is smaller than machine precision
	 */
	private void checkRanges() {
		for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
			if (Math.abs(maximum[i] - minimum[i]) < Settings.EPS) {
				throw new IllegalArgumentException("unable to normalize, objective " + i +
						" has identical lower and upper bounds");
			}
		}
	}
	
	/**
	 * Returns a new non-dominated population containing the normalized solutions from the specified population.
	 * 
	 * @param population the population
	 * @return a new non-dominated population containing the normalized solutions from the specified population
	 */
	public NondominatedPopulation normalize(NondominatedPopulation population) {
		NondominatedPopulation result = population.copy();
		normalizeInPlace(result);
		return result;
	}
	
	/**
	 * Returns a new population containing the normalized solutions from the specified population.
	 * 
	 * @param population the population
	 * @return a new population containing the normalized solutions from the specified population
	 */
	public Population normalize(Population population) {
		Population result = population.copy();
		normalizeInPlace(result);
		return result;
	}
	
	/**
	 * Performs the actual normalization by modifying the objective values in place.  While we typically discourage
	 * modifying solutions in a population, we allow it here because normalization does not change the structure of
	 * the population (meaning dominance, rankings, etc. are unchanged).
	 * 
	 * @param population the unnormalized population
	 */
	private void normalizeInPlace(Population population) {
		Iterator<Solution> iterator = population.iterator();
		
		while (iterator.hasNext()) {
			Solution solution = iterator.next();
			
			if (solution.violatesConstraints()) {
				iterator.remove();
			} else {
				for (int j = 0; j < problem.getNumberOfObjectives(); j++) {
					solution.setObjective(j, (solution.getObjective(j) - minimum[j]) / (maximum[j] - minimum[j]));
				}
			}
		}
	}

}
