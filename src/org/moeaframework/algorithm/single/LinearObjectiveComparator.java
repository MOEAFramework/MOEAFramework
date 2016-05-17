package org.moeaframework.algorithm.single;

import java.io.Serializable;

import org.moeaframework.core.Solution;

public class LinearObjectiveComparator implements SingleObjectiveComparator, Serializable {
	
	private static final long serialVersionUID = 5157359855613094380L;
	
	private double[] weights;
	
	public LinearObjectiveComparator() {
		this(1.0);
	}
	
	public LinearObjectiveComparator(double... weights) {
		super();
		this.weights = weights;
		
		if ((this.weights == null) || (this.weights.length == 0)) {
			this.weights = new double[] { 1.0 };
		}
	}

	@Override
	public int compare(Solution solution1, Solution solution2) {
		double fitness1 = calculateFitness(solution1, weights);
		double fitness2 = calculateFitness(solution2, weights);
		
		return Double.compare(fitness1, fitness2);
	}
	
	public static double calculateFitness(Solution solution, double[] weights) {
		double fitness = 0.0;
		
		for (int i = 0; i < solution.getNumberOfObjectives(); i++) {
			fitness += weights[i >= weights.length ? weights.length-1 : i] *
					solution.getObjective(i);
		}
		
		return fitness;
	}

}
