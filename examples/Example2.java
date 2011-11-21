import java.io.IOException;

import org.moeaframework.Analyzer;
import org.moeaframework.Executor;

/**
 * Demonstrates how only a few lines of code are necessary to setup, run
 * and statistically compare multiple algorithms.
 */
public class Example2 {

	public static void main(String[] args) throws IOException {
		String problem = "UF1";
		String[] algorithms = { "NSGAII", "GDE3", "eMOEA" };

		//setup the experiment
		Executor executor = new Executor()
				.withProblem(problem)
				.withMaxEvaluations(10000);

		Analyzer analyzer = new Analyzer()
				.withProblem(problem)
				.includeAllMetrics()
				.showStatisticalSignificance();

		//run each algorithm for 50 seeds
		for (String algorithm : algorithms) {
			analyzer.addAll(algorithm, 
					executor.withAlgorithm(algorithm).runSeeds(50));
		}

		//print the results
		analyzer.printAnalysis();
	}
	
}
