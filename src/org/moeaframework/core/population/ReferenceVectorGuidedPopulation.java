/* Copyright 2009-2025 David Hadka
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
package org.moeaframework.core.population;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Solution;
import org.moeaframework.core.attribute.NormalizedObjectives;
import org.moeaframework.util.Vector;
import org.moeaframework.util.validate.Validate;
import org.moeaframework.util.weights.NormalBoundaryDivisions;
import org.moeaframework.util.weights.NormalBoundaryIntersectionGenerator;

/**
 * A reference vector guided population, for use with RVEA, that truncates the population using the method outlined in
 * [1].
 * <p>
 * References:
 * <ol>
 *   <li>R. Cheng, Y. Jin, M. Olhofer, and B. Sendhoff.  "A Reference Vector Guided Evolutionary Algorithm for
 *       Many-objective Optimization."  IEEE Transactions on Evolutionary Computation, Issue 99, 2016.
 * </ol>
 */
public class ReferenceVectorGuidedPopulation extends Population {

	/**
	 * The number of objectives.
	 */
	private final int numberOfObjectives;

	/**
	 * The number of divisions.
	 */
	private final NormalBoundaryDivisions divisions;

	/**
	 * The ideal point.
	 */
	double[] idealPoint;
	
	/**
	 * The original, unnormalized reference vectors.
	 */
	private double[][] originalWeights;

	/**
	 * The normalized reference vectors.
	 */
	double[][] weights;
	
	/**
	 * The minimum angle between reference vectors.
	 */
	private double[] minAngles;
	
	/**
	 * Scaling factor used in the angle-penalized distance function.  This should be set to
	 * {@code currentGeneration / maxGenerations}.
	 */
	private double scalingFactor = 0.0;
	
	/**
	 * Controls the rate of change in the angle-penalized distance function.
	 */
	private final double alpha;
	
	/**
	 * Constructs a new populationf or RVEA using default settings.
	 * 
	 * @param numberOfObjectives the number of objectives
	 * @param divisions the number of divisions
	 */
	public ReferenceVectorGuidedPopulation(int numberOfObjectives, NormalBoundaryDivisions divisions) {
		this(numberOfObjectives, divisions, 2.0);
	}
	
	/**
	 * Constructs a new population for RVEA.
	 * 
	 * @param numberOfObjectives the number of objectives
	 * @param divisions the number of divisions
	 * @param alpha controls the rate of change in the angle-penalized distance function
	 */
	public ReferenceVectorGuidedPopulation(int numberOfObjectives, NormalBoundaryDivisions divisions, double alpha) {
		super();
		this.numberOfObjectives = numberOfObjectives;
		this.divisions = divisions;
		this.alpha = alpha;
		
		initialize();
	}

	/**
	 * Constructs a new population for RVEA.
	 * 
	 * @param numberOfObjectives the number of objectives
	 * @param divisions the number of divisions
	 * @param alpha controls the rate of change in the angle-penalized distance function
	 * @param iterable the solutions used to initialize this population
	 */
	public ReferenceVectorGuidedPopulation(int numberOfObjectives, NormalBoundaryDivisions divisions, double alpha,
			Iterable<? extends Solution> iterable) {
		super(iterable);
		this.numberOfObjectives = numberOfObjectives;
		this.divisions = divisions;
		this.alpha = alpha;

		initialize();
	}
	
	/**
	 * Returns the number of divisions used to generate the reference vectors.
	 * 
	 * @return the number of divisions
	 */
	public NormalBoundaryDivisions getDivisions() {
		return divisions;
	}
	
	/**
	 * Returns the {@code alpha} parameter, which controls the rate of change in the angle-penalized distance function.
	 * 
	 * @return the {@code alpha} parameter value
	 */
	public double getAlpha() {
		return alpha;
	}
	
	/**
	 * Scaling factor used in the angle-penalized distance function.  This should be set to
	 * {@code currentGeneration / maxGenerations}.  Smaller values favor convergence while larger values favor
	 * diversity.
	 * 
	 * @param scalingFactor the scaling factor, between 0 and 1
	 */
	public void setScalingFactor(double scalingFactor) {
		if (scalingFactor < 0.0) {
			scalingFactor = 0.0;
		} else if (scalingFactor > 1.0) {
			scalingFactor = 1.0;
		}
		
		this.scalingFactor = scalingFactor;
	}
	
	/**
	 * Normalize the reference vectors.
	 */
	public void adapt() {
		// compute the minimum and maximum of the objectives
		double[] zmin = new double[numberOfObjectives];
		double[] zmax = new double[numberOfObjectives];
		
		Arrays.fill(zmin, Double.POSITIVE_INFINITY);
		Arrays.fill(zmax, Double.NEGATIVE_INFINITY);
		
		for (Solution solution : this) {
			for (int i = 0; i < numberOfObjectives; i++) {
				zmin[i] = Math.min(zmin[i], solution.getObjective(i).getCanonicalValue());
				zmax[i] = Math.max(zmax[i], solution.getObjective(i).getCanonicalValue());
			}
		}
		
		// create the new normalized reference vectors
		weights = new double[originalWeights.length][];
		
		for (int i = 0; i < originalWeights.length; i++) {
			double[] newWeight = originalWeights[i].clone();
			
			for (int j = 0; j < numberOfObjectives; j++) {
				newWeight[j] *= Math.max(0.01, zmax[j] - zmin[j]);
			}
			
			weights[i] = Vector.normalize(newWeight);
		}
		
		// compute the minimum angles between reference vectors
		minAngles = new double[weights.length];
		
		for (int i = 0; i < weights.length; i++) {
			minAngles[i] = smallestAngleBetweenWeights(i);
		}
	}

	/**
	 * Initializes the reference vectors and compute the minimum angles between the vectors
	 */
	private void initialize() {
		// validate arguments
		Validate.that("numberOfObjectives", numberOfObjectives).isGreaterThanOrEqualTo(2);
		
		// create the reference vectors
		originalWeights = new NormalBoundaryIntersectionGenerator(numberOfObjectives, divisions).generate()
				.toArray(double[][]::new);
		
		for (int i = 0; i < originalWeights.length; i++) {
			originalWeights[i] = Vector.normalize(originalWeights[i]);
		}
		
		// create a copy of the reference vectors (so the original reference
		// vectors remain unchanged when we adapt)
		weights = new double[originalWeights.length][];
		
		for (int i = 0; i < originalWeights.length; i++) {
			weights[i] = originalWeights[i].clone();
		}
		
		// compute the minimum angles between reference vectors
		minAngles = new double[weights.length];
		
		for (int i = 0; i < weights.length; i++) {
			minAngles[i] = smallestAngleBetweenWeights(i);
		}
	}
	
	/**
	 * Compute the ideal point.
	 */
	protected void calculateIdealPoint() {
		idealPoint = new double[numberOfObjectives];
		Arrays.fill(idealPoint, Double.POSITIVE_INFINITY);
		
		for (Solution solution : this) {
			if (solution.getNumberOfObjectives() != numberOfObjectives) {
				throw new FrameworkException("Incorrect number of objectives, expected " + numberOfObjectives +
						" but solution has " + solution.getNumberOfObjectives());
			}

			for (int i = 0; i < numberOfObjectives; i++) {
				idealPoint[i] = Math.min(idealPoint[i], solution.getObjective(i).getCanonicalValue());
			}
		}
	}
	
	/**
	 * Offsets the solutions in this population by the ideal point.  This method does not modify the objective values,
	 * instead it sets the {@link NormalizedObjectives} attribute.
	 */
	protected void translateByIdealPoint() {
		for (Solution solution : this) {
			double[] objectives = solution.getCanonicalObjectiveValues();

			for (int i = 0; i < numberOfObjectives; i++) {
				objectives[i] -= idealPoint[i];
			}

			NormalizedObjectives.setAttribute(solution, objectives);
		}
	}
	
	/**
	 * Returns the cosine between the objective vector and a reference vector.  This method assumes the line is a
	 * normalized weight vector; the point does not need to be normalized.
	 * 
	 * @param line the line originating from the origin
	 * @param point the point
	 * @return the cosine
	 */
	protected static double cosine(double[] line, double[] point) {
		return Vector.dot(point, line) / Vector.magnitude(point);
	}
	
	/**
	 * Returns the angle between the objective vector and a reference vector.  This method assumes the line is a
	 * normalized weight vector; the point does not need to be normalized.
	 * 
	 * @param line the line originating from the origin
	 * @param point the point
	 * @return the angle (acosine)
	 */
	protected static double acosine(double[] line, double[] point) {
		return Math.acos(cosine(line, point));
	}
	
	/**
	 * Associates each solution to the nearest reference vector, returning a list-of-lists.  The outer list maps to
	 * each reference vector using their index.  The inner list is an unordered collection of the solutions
	 * associated with the reference point.
	 * 
	 * @param population the population of solutions
	 * @return the association of solutions to reference points
	 */
	protected List<List<Solution>> associateToReferencePoint(Population population) {
		List<List<Solution>> result = new ArrayList<>();

		for (int i = 0; i < weights.length; i++) {
			result.add(new ArrayList<>());
		}

		for (Solution solution : population) {
			double[] objectives = NormalizedObjectives.getAttribute(solution);
			double maxDistance = Double.NEGATIVE_INFINITY;
			int maxIndex = -1;

			for (int i = 0; i < weights.length; i++) {
				double distance = cosine(weights[i], objectives);

				if (distance > maxDistance) {
					maxDistance = distance;
					maxIndex = i;
				}
			}
			
			// if there is only a single solution, then the normalized objectives will be 0 (since the ideal
			// point == the solution); in this case, the solution could be associated with any reference vector
			if (maxIndex < 0) {
				maxIndex = 0;
			}

			result.get(maxIndex).add(solution);
		}

		return result;
	}
	
	/**
	 * Computes the smallest angle between the given reference vector and all remaining vectors.
	 * 
	 * @param index the index of the reference vector
	 * @return the smallest angle between the given reference vector and all remaining vectors
	 */
	protected double smallestAngleBetweenWeights(int index) {
		double smallestAngle = Double.POSITIVE_INFINITY;
		
		for (int i = 0; i < weights.length; i++) {
			if (i != index) {
				smallestAngle = Math.min(smallestAngle, acosine(weights[index], weights[i]));
			}
		}
		
		return smallestAngle;
	}
	
	/**
	 * Select the solution with the smallest penalized distance.
	 * 
	 * @param solutions the solutions
	 * @param index the index of the reference vector
	 * @return the solution with the smallest penalized distance
	 */
	protected Solution select(List<Solution> solutions, int index) {
		double[] weight = weights[index];
		double minDistance = Double.POSITIVE_INFINITY;
		Solution minSolution = null;
		
		for (Solution solution : solutions) {
			if (solution.isFeasible()) {
				double[] objectives = NormalizedObjectives.getAttribute(solution);
				
				double penalty = numberOfObjectives * Math.pow(scalingFactor, alpha) *
						acosine(weight, objectives) / minAngles[index];
				
				double tempDistance = Vector.magnitude(objectives) * (1.0 + penalty);
				
				if (tempDistance < minDistance) {
					minDistance = tempDistance;
					minSolution = solution;
				}
			}
		}
		
		if (minSolution == null) {
			// all solutions were infeasible, find one with smallest constraint violation
			for (Solution solution : solutions) {
				double tempDistance = solution.getSumOfConstraintViolations();
				
				if (tempDistance < minDistance) {
					minDistance = tempDistance;
					minSolution = solution;
				}
			}
		}
		
		return minSolution;
	}

	@Override
	public void truncate(int size, Comparator<? super Solution> comparator) {
		throw new UnsupportedOperationException("Call truncate() instead");
	}
	
	/**
	 * Truncates the population so that only one solution is associated with each reference vector.
	 */
	public void truncate() {
		// update the ideal point
		calculateIdealPoint();

		// translate objectives so the ideal point is at the origin
		translateByIdealPoint();

		// associate each solution to a reference vector
		List<List<Solution>> members = associateToReferencePoint(this);

		// elitist selection
		clear();

		for (int i = 0; i < members.size(); i++) {
			List<Solution> associations = members.get(i);

			if (associations.size() > 0) {
				add(select(associations, i));
			}
		}
	}
	
	@Override
	public ReferenceVectorGuidedPopulation copy() {
		ReferenceVectorGuidedPopulation result = new ReferenceVectorGuidedPopulation(numberOfObjectives, divisions,
				alpha);
		
		for (Solution solution : this) {
			result.add(solution.copy());
		}
		
		return result;
	}
	
	@Override
	public void saveState(ObjectOutputStream stream) throws IOException {
		super.saveState(stream);
		stream.writeObject(idealPoint);
		stream.writeObject(originalWeights);
		stream.writeObject(weights);
		stream.writeObject(minAngles);
		stream.writeDouble(scalingFactor);
	}

	@Override
	public void loadState(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		super.loadState(stream);
		idealPoint = (double[])stream.readObject();
		originalWeights = (double[][])stream.readObject();
		weights = (double[][])stream.readObject();
		minAngles = (double[])stream.readObject();
		scalingFactor = stream.readDouble();
	}

}
