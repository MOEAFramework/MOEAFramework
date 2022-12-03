package org.moeaframework.examples.configuring;

import java.io.IOException;

import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.operator.real.PCX;
import org.moeaframework.problem.DTLZ.DTLZ2;

/**
 * Demonstrates how to create and configure an algorithm programmatically.  This is the new way to 
 * configure algorithms starting in version 3.2.
 */
public class DirectConfiguration {

	public static void main(String[] args) throws IOException {
		Problem problem = new DTLZ2(2);
		NSGAII algorithm = new NSGAII(problem);
		
		algorithm.setInitialPopulationSize(250);
		algorithm.setVariation(new PCX(5, 2));
		algorithm.setArchive(new EpsilonBoxDominanceArchive(0.01));
		
		algorithm.run(10000);

		System.out.format("Objective1  Objective2%n");
		
		for (Solution solution : algorithm.getResult()) {
			System.out.format("%.4f      %.4f%n",
					solution.getObjective(0),
					solution.getObjective(1));
		}
	}

}
