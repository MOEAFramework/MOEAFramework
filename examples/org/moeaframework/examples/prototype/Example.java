package org.moeaframework.examples.prototype;

import java.io.IOException;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.problem.DTLZ.DTLZ2;

/**
 * Prototype of the new interface, allowing algorithms to be created and executed
 * directly.
 */
public class Example {

	public static void main(String[] args) throws IOException {
		Problem problem = new DTLZ2(2);
		
		NSGAII algorithm = new NSGAII(problem);
		algorithm.run(10000);

		NondominatedPopulation result = algorithm.getResult();
		
		//display the results
		System.out.format("Objective1  Objective2%n");
		
		for (Solution solution : result) {
			System.out.format("%.4f      %.4f%n",
					solution.getObjective(0),
					solution.getObjective(1));
		}
	}

}
