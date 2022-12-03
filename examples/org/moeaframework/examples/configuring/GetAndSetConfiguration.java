package org.moeaframework.examples.configuring;

import java.io.IOException;

import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.problem.DTLZ.DTLZ2;
import org.moeaframework.util.TypedProperties;

public class GetAndSetConfiguration {
	
	public static void main(String[] args) throws IOException {
		Problem problem = new DTLZ2(2);
		NSGAII algorithm = new NSGAII(problem);
		
		// Display the current configuration
		TypedProperties properties = algorithm.getConfiguration();
		
		System.out.println("Initial Configuration:");
		properties.display();
		
		// Modify and apply a different configuration
		properties.setString("operator", "pcx");
		properties.setInt("pcx.parents", 5);
		algorithm.applyConfiguration(properties);
		
		// Display the new configuration
		properties = algorithm.getConfiguration();
		
		System.out.println();
		System.out.println("Updated Configuration:");
		properties.display();
		
		// Finally solve the problem
		algorithm.run(10000);
		
		System.out.println();
		System.out.println("Objective1  Objective2");
		
		for (Solution solution : algorithm.getResult()) {
			System.out.format("%.4f      %.4f%n",
					solution.getObjective(0),
					solution.getObjective(1));
		}
	}

}
