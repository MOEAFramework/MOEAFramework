/* Copyright 2009-2024 David Hadka
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

import java.util.stream.IntStream;

import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.problem.misc.Schaffer;
import org.moeaframework.util.Timer;
import org.moeaframework.util.distributed.DistributedProblem;

/**
 * Example of parallelizing function evaluations across multiple cores.
 * Ideally, we expect on a computer with N cores to see the parallel
 * version speed up by N times, but it can be slightly lower due to
 * overhead.
 */
public class ParallelizationExample {
	
	public static void main(String[] args) {
		ExpensiveSchafferProblem problem = new ExpensiveSchafferProblem();
		
		//first run on a single core (serially).
		System.out.println("First run - serial");
		
		Timer serialTimer = Timer.startNew();
		
		NSGAII serialAlgorithm = new NSGAII(problem);
		serialAlgorithm.run(100000);
		
		serialTimer.stop();
		
		//now distribute evaluations on all available cores.
		System.out.println("Second run - parallel (" +
				Runtime.getRuntime().availableProcessors() + " cores)");
		
		Timer parallelTimer = Timer.startNew();
		
		try (Problem distributedProblem = DistributedProblem.from(problem)) {
			NSGAII distributedAlgorithm = new NSGAII(distributedProblem);
			distributedAlgorithm.run(100000);
		}
		
		parallelTimer.stop();
		
		//display results
		System.out.println("Results:");
		System.out.println("  Serial   - " + serialTimer.getElapsedTime() + " sec");
		System.out.println("  Parallel - " + parallelTimer.getElapsedTime() + " sec");
		
		double speedup = serialTimer.getElapsedTime() / parallelTimer.getElapsedTime();
		System.out.println("  Speedup  - " + String.format("%.02f", speedup));
	}
	
	public static class ExpensiveSchafferProblem extends Schaffer {

		public ExpensiveSchafferProblem() {
			super();
		}

		@Override
		public void evaluate(Solution solution) {
			//simulate a computationally expensive problem; we store the result as an
			//attribute to prevent Java from treating the loop as "dead code"
			solution.setAttribute("sum", IntStream.range(0, 100000).asDoubleStream().sum());
			
			//evaluate the actual Schaffer problem
			super.evaluate(solution);
		}

	}

}
