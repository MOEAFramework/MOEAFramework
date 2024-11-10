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

import java.io.File;

import org.moeaframework.Instrumenter;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.analysis.collector.InstrumentedAlgorithm;
import org.moeaframework.analysis.diagnostics.ApproximationSetViewer;
import org.moeaframework.problem.CEC2009.UF1;
import org.moeaframework.problem.Problem;

/**
 * Displays an interactive plot showing the convergence of the NSGA-II algorithm on
 * UF1 at each iteration.
 */
public class PlotApproximationSetConvergence {

	public static void main(String[] args) {
		Problem problem = new UF1();
		
		Instrumenter instrumenter = new Instrumenter()
				.withProblem(problem)
				.withReferenceSet(new File("pf/UF1.dat"))
				.withFrequency(100)
				.attachApproximationSetCollector();
		
		NSGAII algorithm = new NSGAII(problem);
		
		InstrumentedAlgorithm<NSGAII> instrumentedAlgorithm =
				instrumenter.instrument(algorithm);
		
		instrumentedAlgorithm.run(10000);

		ApproximationSetViewer.show("NSGA-II",
				instrumenter.getReferenceSet(),
				instrumenter.getObservations());
	}

}
