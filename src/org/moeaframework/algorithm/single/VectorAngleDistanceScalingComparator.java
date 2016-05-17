package org.moeaframework.algorithm.single;

import java.io.Serializable;

import org.moeaframework.core.Solution;
import org.moeaframework.util.Vector;

public class VectorAngleDistanceScalingComparator implements SingleObjectiveComparator, Serializable {

	private static final long serialVersionUID = -2535092560377062714L;

	private final double q;
	
	private final double[] weights;
	
	public VectorAngleDistanceScalingComparator(double[] weights) {
		this(weights, 100.0);
	}
	
	public VectorAngleDistanceScalingComparator(double[] weights, double q) {
		super();
		this.weights = weights;
		this.q = q;
	}

	@Override
	public int compare(Solution solution1, Solution solution2) {
		double fitness1 = calculateFitness(solution1, weights, q);
		double fitness2 = calculateFitness(solution2, weights, q);
		
		return Double.compare(fitness1, fitness2);
	}

	public static double calculateFitness(Solution solution, double[] weights, double q) {
		double[] objectives = solution.getObjectives();
		double magnitude = Vector.magnitude(objectives);
		double cosine = Vector.dot(weights, Vector.divide(objectives, magnitude));
		
		// prevent numerical error
		if (cosine > 1.0) {
			cosine = 1.0;
		}
		
		return magnitude / (Math.pow(cosine, q)+0.01);
	}

	
}
