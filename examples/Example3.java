/* Copyright 2009-2016 David Hadka
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
import java.io.IOException;

import org.moeaframework.Executor;
import org.moeaframework.Instrumenter;
import org.moeaframework.analysis.collector.Accumulator;

/**
 * Demonstrates the use of the {@code Instrumenter} to collect run-time
 * dynamics.  The output lists the NFE, elapsed time and generational distance
 * throughout the run.
 */
public class Example3 {

	public static void main(String[] args) throws IOException {
		// setup the instrumenter to record the generational distance metric
		Instrumenter instrumenter = new Instrumenter()
				.withProblem("UF1")
				.withFrequency(100)
				.attachElapsedTimeCollector()
				.attachGenerationalDistanceCollector();
		
		// use the executor to run the algorithm with the instrumenter
		new Executor()
				.withProblem("UF1")
				.withAlgorithm("NSGAII")
				.withMaxEvaluations(10000)
				.withInstrumenter(instrumenter)
				.run();
		
		Accumulator accumulator = instrumenter.getLastAccumulator();
		
		// print the runtime dynamics
		System.out.format("  NFE    Time      Generational Distance%n");
		
		for (int i=0; i<accumulator.size("NFE"); i++) {
			System.out.format("%5d    %-8.4f  %-8.4f%n",
					accumulator.get("NFE", i),
					accumulator.get("Elapsed Time", i),
					accumulator.get("GenerationalDistance", i));
		}
	}

}
