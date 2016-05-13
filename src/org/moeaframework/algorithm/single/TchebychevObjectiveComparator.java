package org.moeaframework.algorithm.single;

import java.io.Serializable;

import org.moeaframework.core.Solution;

public class TchebychevObjectiveComparator implements SingleObjectiveComparator, Serializable {
	
	private static final long serialVersionUID = 5018011451944335718L;
	
	private double[] weights;
	
	public TchebychevObjectiveComparator() {
		this(1.0);
	}
	
	public TchebychevObjectiveComparator(double... weights) {
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
		double max = Double.NEGATIVE_INFINITY;

		for (int i = 0; i < solution.getNumberOfObjectives(); i++) {
			max = Math.max(max, Math.max(weights[i], 0.0001) * solution.getObjective(i));
		}

		return max;
	}

}
