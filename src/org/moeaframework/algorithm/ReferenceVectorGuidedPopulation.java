/* Copyright 2009-2016 David Hadka
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Population;
import org.moeaframework.core.Solution;
import org.moeaframework.util.Vector;
import org.moeaframework.util.weights.NormalBoundaryIntersectionGenerator;

/**
 * A reference vector guided population, for use with RVEA, that truncates
 * the population using the method outlined in [1].
 * <p>
 * References:
 * <ol>
 *   <li>R. Cheng, Y. Jin, M. Olhofer, and B. Sendhoff.  "A Reference Vector
 *       Guided Evolutionary Algorithm for Many-objective Optimization."
 *       IEEE Transactions on Evolutionary Computation, Issue 99, 2016.
 * </ol>
 */
public class ReferenceVectorGuidedPopulation extends Population {
	
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
	 * The ideal point.
	 */
	double[] idealPoint;
	
	/**
	 * The original, unnormalized reference vectors.
	 */
	private List<double[]> originalWeights;

	/**
	 * The normalized reference vectors.
	 */
	List<double[]> weights;
	
	/**
	 * The minimum angle between reference vectors.
	 */
	private double[] minAngles;
	
	/**
	 * Scaling factor used in the angle-penalized distance function.  This
	 * should be set to {@code currentGeneration / maxGenerations}.
	 */
	private double scalingFactor = 0.0;
	
	/**
	 * Controls the rate of change in the angle-penalized distance function.
	 */
	private final double alpha;
	
	/**
	 * Constructs a new population for RVEA.
	 * 
	 * @param numberOfObjectives the number of objectives
	 * @param divisions the number of divisions
	 * @param alpha controls the rate of change in the angle-penalized distance
	 *        function
	 */
	public ReferenceVectorGuidedPopulation(int numberOfObjectives,
			int divisions, double alpha) {
		super();
		this.numberOfObjectives = numberOfObjectives;
		this.divisionsOuter = divisions;
		this.divisionsInner = 0;
		this.alpha = alpha;
		
		initialize();
	}

	/**
	 * Constructs a new population for RVEA.
	 * 
	 * @param numberOfObjectives the number of objectives
	 * @param divisions the number of divisions
	 * @param alpha controls the rate of change in the angle-penalized distance
	 *        function
	 * @param iterable the solutions used to initialize this population
	 */
	public ReferenceVectorGuidedPopulation(
			int numberOfObjectives, int divisions, double alpha,
			Iterable<? extends Solution> iterable) {
		super(iterable);
		this.numberOfObjectives = numberOfObjectives;
		this.divisionsOuter = divisions;
		this.divisionsInner = 0;
		this.alpha = alpha;

		initialize();
	}

	/**
	 * Constructs a new population for RVEA.
	 * 
	 * @param numberOfObjectives the number of objectives
	 * @param divisionsOuter the number of outer divisions
	 * @param divisionsInner the number of inner divisions
	 * @param alpha controls the rate of change in the angle-penalized distance
	 *        function
	 */
	public ReferenceVectorGuidedPopulation(int numberOfObjectives,
			int divisionsOuter, int divisionsInner, double alpha) {
		super();
		this.numberOfObjectives = numberOfObjectives;
		this.divisionsOuter = divisionsOuter;
		this.divisionsInner = divisionsInner;
		this.alpha = alpha;

		initialize();
	}

	/**
	 * Constructs a new population for RVEA.
	 * 
	 * @param numberOfObjectives the number of objectives
	 * @param divisionsOuter the number of outer divisions
	 * @param divisionsInner the number of inner divisions
	 * @param alpha controls the rate of change in the angle-penalized distance
	 *        function
	 * @param iterable the solutions used to initialize this population
	 */
	public ReferenceVectorGuidedPopulation(
			int numberOfObjectives, int divisionsOuter, int divisionsInner,
			double alpha, Iterable<? extends Solution> iterable) {
		super(iterable);
		this.numberOfObjectives = numberOfObjectives;
		this.divisionsOuter = divisionsOuter;
		this.divisionsInner = divisionsInner;
		this.alpha = alpha;

		initialize();
	}
	
	/**
	 * Scaling factor used in the angle-penalized distance function.  This
	 * should be set to {@code currentGeneration / maxGenerations}.  Smaller
	 * values favor convergence while larger values favor diversity.
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
				zmin[i] = Math.min(zmin[i], solution.getObjective(i));
				zmax[i] = Math.max(zmax[i], solution.getObjective(i));
			}
		}
		
		// create the new normalized reference vectors
		weights.clear();
		
		for (double[] weight : originalWeights) {
			double[] newWeight = weight.clone();
			
			for (int i = 0; i < numberOfObjectives; i++) {
				newWeight[i] *= Math.max(0.01, zmax[i] - zmin[i]);
			}
			
			weights.add(Vector.normalize(newWeight));
		}
		
		// compute the minimum angles between reference vectors
		minAngles = new double[weights.size()];
		
		for (int i = 0; i < weights.size(); i++) {
			minAngles[i] = smallestAngleBetweenWeights(i);
		}
	}

	/**
	 * Initializes the reference vectors and compute the minimum angles between
	 * the vectors
	 */
	private void initialize() {
		// validate arguments
		if (numberOfObjectives < 2) {
			throw new FrameworkException("requires at least two objectives");
		}
		
		// create the reference vectors
		originalWeights = new NormalBoundaryIntersectionGenerator(
				numberOfObjectives, divisionsOuter, divisionsInner).generate();
		
		for (int i = 0; i < originalWeights.size(); i++) {
			originalWeights.set(i, Vector.normalize(originalWeights.get(i)));
		}
		
		// create a copy of the reference vectors (so the original reference
		// vectors remain unchanged when we adapt)
		weights = new ArrayList<double[]>();
		
		for (double[] weight : originalWeights) {
			weights.add(weight.clone());
		}
		
		// compute the minimum angles between reference vectors
		minAngles = new double[weights.size()];
		
		for (int i = 0; i < weights.size(); i++) {
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
	 * Returns the cosine between the objective vector and a reference vector.
	 * This method assumes the line is a normalized weight vector; the point
	 * does not need to be normalized.
	 * 
	 * @param line the line originating from the origin
	 * @param point the point
	 * @return the cosine
	 */
	protected static double cosine(double[] line, double[] point) {
		return Vector.dot(point, line) / Vector.magnitude(point);
	}
	
	/**
	 * Returns the angle between the objective vector and a reference vector.
	 * This method assumes the line is a normalized weight vector; the point
	 * does not need to be normalized.
	 * 
	 * @param line the line originating from the origin
	 * @param point the point
	 * @return the angle (acosine)
	 */
	protected static double acosine(double[] line, double[] point) {
		return Math.acos(cosine(line, point));
	}
	
	/**
	 * Associates each solution to the nearest reference vector, returning a
	 * list-of-lists.  The outer list maps to each reference vector using their
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
			double maxDistance = Double.NEGATIVE_INFINITY;
			int maxIndex = -1;

			for (int i = 0; i < weights.size(); i++) {
				double distance = cosine(weights.get(i), objectives);

				if (distance > maxDistance) {
					maxDistance = distance;
					maxIndex = i;
				}
			}
			
			// if there is only a single solution, then the normalized
			// objectives will be 0 (since the ideal point == the solution);
			// in this case, the solution could be associated with any
			// reference vector
			if (maxIndex < 0) {
				maxIndex = 0;
			}

			result.get(maxIndex).add(solution);
		}

		return result;
	}
	
	/**
	 * Computes the smallest angle between the given reference vector and all
	 * remaining vectors.
	 * 
	 * @param index the index of the reference vector
	 * @return the smallest angle between the given reference vector and all
	 *         remaining vectors
	 */
	protected double smallestAngleBetweenWeights(int index) {
		double smallestAngle = Double.POSITIVE_INFINITY;
		
		for (int i = 0; i < weights.size(); i++) {
			if (i != index) {
				smallestAngle = Math.min(smallestAngle,
						acosine(weights.get(index), weights.get(i)));
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
		double[] weight = weights.get(index);
		double minDistance = Double.POSITIVE_INFINITY;
		Solution minSolution = null;
		
		for (Solution solution : solutions) {
			if (!solution.violatesConstraints()) {
				double[] objectives = (double[])solution.getAttribute(
						NORMALIZED_OBJECTIVES);
				
				double penalty = numberOfObjectives *
						Math.pow(scalingFactor, alpha) *
						acosine(weight, objectives) / minAngles[index];
				
				double tempDistance = Vector.magnitude(objectives) * (1.0 + penalty);
				
				if (tempDistance < minDistance) {
					minDistance = tempDistance;
					minSolution = solution;
				}
			}
		}
		
		if (minSolution == null) {
			// all solutions were infeasible, find one with smallest constraint
			// violation
			for (Solution solution : solutions) {
				double tempDistance = 0.0;
				
				for (int i = 0; i < solution.getNumberOfConstraints(); i++) {
					tempDistance += Math.abs(solution.getConstraint(i));
				}
				
				if (tempDistance < minDistance) {
					minDistance= tempDistance;
					minSolution = solution;
				}
			}
		}
		
		return minSolution;
	}

	@Override
	public void truncate(int size, Comparator<? super Solution> comparator) {
		throw new UnsupportedOperationException("call truncate() instead");
	}
	
	/**
	 * Truncates the population so that only one solution is associated with
	 * each reference vector.
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
	
	/**
	 * Returns the serializable state of this population.
	 * 
	 * @return the serializable state of this population
	 */
	protected ReferenceVectorGuidedPopulationState getState() {
		double[] idealPointClone = idealPoint == null ? null : idealPoint.clone();
		List<double[]> originalWeightsClone = new ArrayList<double[]>();
		List<double[]> weightsClone = new ArrayList<double[]>();
		double[] minAnglesClone = minAngles == null ? null : minAngles.clone();
		List<Solution> populationList = new ArrayList<Solution>();
		
		for (double[] weight : originalWeights) {
			originalWeightsClone.add(weight.clone());
		}
		
		for (double[] weight : weights) {
			weightsClone.add(weight.clone());
		}
		
		for (Solution solution : this) {
			populationList.add(solution);
		}
		
		return new ReferenceVectorGuidedPopulationState(populationList,
				idealPointClone, originalWeightsClone, weightsClone,
				minAnglesClone, scalingFactor);
	}
	
	/**
	 * Configures this population given the serializable state.
	 * 
	 * @param state the serializable state
	 */
	protected void setState(ReferenceVectorGuidedPopulationState state) {
		idealPoint = state.getIdealPoint() == null ? null : state.getIdealPoint().clone();
		minAngles = state.getMinAngles() == null ? null : state.getMinAngles().clone();
		scalingFactor = state.getScalingFactor();
		originalWeights.clear();
		weights.clear();
		clear();
		
		for (double[] weight : state.getOriginalWeights()) {
			originalWeights.add(weight.clone());
		}
		
		for (double[] weight : state.getWeights()) {
			weights.add(weight.clone());
		}
		
		addAll(state.getPopulation());
	}
	
	/**
	 * Proxy for serializing the state of this population.  All data fields
	 * provided to or read from this proxy should be cloned.
	 */
	protected static class ReferenceVectorGuidedPopulationState implements Serializable {

		private static final long serialVersionUID = 2967034665150122964L;
		
		/**
		 * The population stored in a serializable list.
		 */
		private final List<Solution> population;

		/**
		 * The ideal point.
		 */
		private final double[] idealPoint;
		
		/**
		 * The original weight vectors.
		 */
		private final List<double[]> originalWeights;
		
		/**
		 * The weight vectors.
		 */
		private final List<double[]> weights;
		
		/**
		 * The minimum angles between weight vectors.
		 */
		private final double[] minAngles;
		
		/**
		 * The scaling factor.
		 */
		private final double scalingFactor;
		
		/**
		 * Creates a new proxy for serializing the state of this population.
		 * 
		 * @param population the population stored in a serializable list
		 * @param idealPoint the ideal point
		 * @param originalWeights the original weight vectors
		 * @param weights the weight vectors
		 * @param minAngles the minimum angles between weight vectors
		 * @param scalingFactor the scaling factor
		 */
		public ReferenceVectorGuidedPopulationState(List<Solution> population,
				double[] idealPoint, List<double[]> originalWeights,
				List<double[]> weights, double[] minAngles,
				double scalingFactor) {
			super();
			this.population = population;
			this.idealPoint = idealPoint;
			this.originalWeights = originalWeights;
			this.weights = weights;
			this.minAngles = minAngles;
			this.scalingFactor = scalingFactor;
		}
		
		/**
		 * Returns the population stored in a serializable list.
		 * 
		 * @return the population stored in a serializable list
		 */
		public List<Solution> getPopulation() {
			return population;
		}

		/**
		 * Returns the ideal point.
		 * 
		 * @return the ideal point
		 */
		public double[] getIdealPoint() {
			return idealPoint;
		}

		/**
		 * Returns the original weight vectors.
		 * 
		 * @return the original weight vectors
		 */
		public List<double[]> getOriginalWeights() {
			return originalWeights;
		}

		/**
		 * Returns the weight vectors.
		 * 
		 * @return the weight vectors
		 */
		public List<double[]> getWeights() {
			return weights;
		}

		/**
		 * Returns the minimum angles between weight vectors.
		 * 
		 * @return the minimum angles between weight vectors
		 */
		public double[] getMinAngles() {
			return minAngles;
		}

		/**
		 * Returns the scaling factor.
		 * 
		 * @return the scaling factor
		 */
		public double getScalingFactor() {
			return scalingFactor;
		}
		
	}

}
