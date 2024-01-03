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
package org.moeaframework.examples.plots;

import org.moeaframework.Executor;
import org.moeaframework.Instrumenter;
import org.moeaframework.analysis.collector.Observations;
import org.moeaframework.analysis.plot.Plot;

/**
 * Displays a plot showing the hypervolume and generational distance runtime dynamics.
 */
public class PlotRuntimeDynamics {

	public static void main(String[] args) {
		Instrumenter instrumenter = new Instrumenter()
				.withProblem("UF1")
				.withFrequency(100)
				.attachHypervolumeCollector()
				.attachGenerationalDistanceCollector();
		
		new Executor()
				.withSameProblemAs(instrumenter)
				.withAlgorithm("NSGAII")
				.withMaxEvaluations(10000)
				.withInstrumenter(instrumenter)
				.run();
		
		Observations observations = instrumenter.getObservations();
		
		Plot plot = new Plot();
		plot.add(observations);
		plot.show();
	}

}
