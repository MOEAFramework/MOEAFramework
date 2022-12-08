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
import java.io.IOException;

import org.moeaframework.Executor;
import org.moeaframework.Instrumenter;

/**
 * The Instrumenter collects runtime dynamics from an algorithm.  That is, at some
 * fixed frequency, it will record information about the current algorithm, including
 * the number of function evaluations, elapsed time, and performance indicators using
 * the current population.  This data is aggregated into a table (the Observations)
 * which can be displayed or saved to a file.
 * 
 * In the example below, we will record the elapsed time and generational distance
 * every 100 function evaluations while solving the UF1 problem with NSGA-II.  This
 * data is then displayed on the console.
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
		
		// print the runtime dynamics
		instrumenter.getObservations().display();
	}

}
