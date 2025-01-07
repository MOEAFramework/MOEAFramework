/* Copyright 2009-2025 David Hadka
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
package org.moeaframework.examples.runtime;

import java.io.IOException;

import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.algorithm.extension.Frequency;
import org.moeaframework.analysis.plot.Plot;
import org.moeaframework.analysis.runtime.InstrumentedAlgorithm;
import org.moeaframework.analysis.runtime.Instrumenter;
import org.moeaframework.problem.CEC2009.UF1;
import org.moeaframework.problem.Problem;

/**
 * Collects runtime data using the Instrumenter and displays a line graph showing the hypervolume and generational
 * distance.
 */
public class PlotRuntimeDynamics {

	public static void main(String[] args) throws IOException {
		// Setup the problem and algorithm
		Problem problem = new UF1();
		NSGAII algorithm = new NSGAII(problem);
		
		// Instrument the algorithm to collect the hypervolume and generational distance
		Instrumenter instrumenter = new Instrumenter()
				.withReferenceSet("pf/UF1.pf")
				.withFrequency(Frequency.ofEvaluations(100))
				.attachHypervolumeCollector()
				.attachGenerationalDistanceCollector();
		
		InstrumentedAlgorithm<NSGAII> instrumentedAlgorithm = instrumenter.instrument(algorithm);
		instrumentedAlgorithm.run(10000);
		
		// Render a plot of the runtime dynamics series
		new Plot()
		    .add(instrumentedAlgorithm.getSeries())
		    .show();
	}

}
