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
import java.io.IOException;

import org.moeaframework.Executor;
import org.moeaframework.Instrumenter;

/**
 * The prior examples demonstrated how to collect the end-of-run result from an
 * algorithm.  We can also use the Instrumenter class to collect runtime dynamics
 * as the algorithm is running.  This includes, but is not limited to:
 * 
 * 1. The number of function evaluations,
 * 2. The elapsed time,
 * 3. Quality indicators (hypervolume, generational distance, etc.), and
 * 4. The Pareto front.
 * 
 * In this example, we will record the elapsed time and generational distance every
 * 100 function evaluations (the frequency) while solving the UF1 problem with NSGA-II.
 * The results are then displayed to the console in a table.
 */
public class Example5 {

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
