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
package org.moeaframework.analysis.tools;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.moeaframework.analysis.io.ResultFileReader;
import org.moeaframework.analysis.series.ResultSeries;
import org.moeaframework.analysis.viewer.RuntimeViewer;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.problem.Problem;
import org.moeaframework.util.cli.CommandLineUtility;

/**
 * Command line utility for visualizing the content of result files.
 */
public class ResultFileViewer extends CommandLineUtility {
	
	private ResultFileViewer() {
		super();
	}
	
	@Override
	public Options getOptions() {
		Options options = super.getOptions();
		
		OptionUtils.addProblemOption(options);
		OptionUtils.addReferenceSetOption(options);
		
		return options;
	}

	@Override
	public void run(CommandLine commandLine) throws Exception {
		RuntimeViewer viewer = new RuntimeViewer(null);
		
		try (Problem problem = OptionUtils.getProblemInstance(commandLine, true)) {
			NondominatedPopulation referenceSet = OptionUtils.getReferenceSet(commandLine, true);
			
			if (referenceSet != null) {
				viewer.getController().setReferenceSet(referenceSet);
			}
			
			for (String filename : commandLine.getArgs()) {
				try (ResultFileReader reader = ResultFileReader.openLegacy(problem, new File(filename))) {
					viewer.getController().addSeries(filename, ResultSeries.of(reader));
				}
			}
		}
	}
	
	/**
	 * The main entry point for this command line utility.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new ResultFileViewer().start(args);
	}

}
