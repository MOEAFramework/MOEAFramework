package org.moeaframework.examples.parallel;

import org.moeaframework.Executor;

/**
 * Example of parallelizing function evaluations across multiple cores.
 * Ideally, we expect on a computer with N cores to see the parallel
 * version speed up by N times, but it can be slightly lower due to
 * overhead.
 */
public class ParallelizationExample {
	
	public static void main(String[] args) {
		//first run on a single core (serially).
		System.out.println("First run - serial");
		
		long startSerial = System.currentTimeMillis();
		new Executor()
				.withAlgorithm("NSGAII")
				.withProblemClass(ExpensiveSchafferProblem.class)
				.withMaxEvaluations(100000)
				.run();
		long endSerial = System.currentTimeMillis();
		
		//now distribute evaluations on all available cores.
		System.out.println("Second run - parallel (" +
				Runtime.getRuntime().availableProcessors() + " cores)");
		
		long startParallel = System.currentTimeMillis();
		new Executor()
				.withAlgorithm("NSGAII")
				.withProblemClass(ExpensiveSchafferProblem.class)
				.withMaxEvaluations(100000)
				.distributeOnAllCores()
				.run();
		long endParallel = System.currentTimeMillis();
		
		//display results
		System.out.println("Results:");
		System.out.println("  Serial   - " + (endSerial - startSerial) + " ms");
		System.out.println("  Parallel - " + (endParallel - startParallel) + " ms");
		
		double speedup = (endSerial - startSerial) / (double)(endParallel - startParallel);
		System.out.println("  Speedup  - " + String.format("%.02f", speedup));
	}

}
