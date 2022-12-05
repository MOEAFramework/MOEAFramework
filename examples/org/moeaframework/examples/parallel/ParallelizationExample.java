/* Copyright 2009-2022 David Hadka
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
