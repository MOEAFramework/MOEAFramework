import java.util.Arrays;

import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;

/**
 * Demonstrates using an Executor to solve the UF1 test problem with NSGA-II,
 * one of the most widely-used multiobjective evolutionary algorithms.
 */
public class Example1 {

	public static void main(String[] args) {
		//configure and run this experiment
		NondominatedPopulation result = new Executor()
				.withProblem("UF1")
				.withAlgorithm("NSGAII")
				.withMaxEvaluations(10000)
				.distributeOnAllCores()
				.run();
		
		//display the results
		for (Solution solution : result) {
			System.out.println(Arrays.toString(solution.getObjectives()));
		}
	}

}
