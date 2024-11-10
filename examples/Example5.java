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
import java.io.File;
import java.io.IOException;

import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.analysis.runtime.InstrumentedAlgorithm;
import org.moeaframework.analysis.runtime.Instrumenter;
import org.moeaframework.problem.CEC2009.UF1;
import org.moeaframework.problem.Problem;

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
 * In this example, we will record the generational distance every 100 function
 * evaluations (the frequency) while solving the UF1 problem with NSGA-II.
 * The results are then displayed to the console in a table.
 */
public class Example5 {

	public static void main(String[] args) throws IOException {		
		// Setup the problem and algorithm
		Problem problem = new UF1();
		NSGAII algorithm = new NSGAII(problem);
		
		// Instrument the algorithm to collect the generational distance
		Instrumenter instrumenter = new Instrumenter()
				.withReferenceSet(new File("pf/UF1.pf"))
				.withFrequency(100)
				.attachGenerationalDistanceCollector();
		
		InstrumentedAlgorithm<NSGAII> instrumentedAlgorithm =
				instrumenter.instrument(algorithm);
		
		instrumentedAlgorithm.run(10000);
		
		// Display the runtime dynamics
		instrumentedAlgorithm.getObservations().display();
	}

}
