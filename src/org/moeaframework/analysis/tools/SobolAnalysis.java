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
package org.moeaframework.analysis.tools;

import java.io.File;
import java.io.PrintWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.moeaframework.analysis.io.MetricFileWriter;
import org.moeaframework.analysis.parameter.ParameterSet;
import org.moeaframework.analysis.sensitivity.SobolSensitivityAnalysis;
import org.moeaframework.analysis.sensitivity.SobolSensitivityAnalysis.SobolSensitivityResult;
import org.moeaframework.util.cli.CommandLineUtility;
import org.moeaframework.util.io.MatrixIO;

/**
 * Global sensitivity analysis of blackbox model output using Saltelli's improved Sobol' global variance decomposition
 * procedure.
 * <p>
 * The following code was derived and translated from the C code used in the study cited below. Refer to this article
 * for a description of the procedure.
 * <p>
 * References:
 * <ol>
 *   <li>Tang, Y., Reed, P., Wagener, T., and van Werkhoven, K., "Comparing Sensitivity Analysis Methods to Advance
 *       Lumped Watershed Model Identification and Evaluation," Hydrology and Earth System Sciences, vol. 11, no. 2,
 *       pp. 793-817, 2007.
 *   <li>Saltelli, A., et al. "Global Sensitivity Analysis: The Primer." John Wiley &amp; Sons Ltd, 2008.
 * </ol>
 */
public class SobolAnalysis extends CommandLineUtility {

	private SobolAnalysis() {
		super();
	}

	@Override
	public Options getOptions() {
		Options options = super.getOptions();

		options.addOption(Option.builder("p")
				.longOpt("parameterFile")
				.hasArg()
				.argName("file")
				.required()
				.build());
		options.addOption(Option.builder("i")
				.longOpt("input")
				.hasArg()
				.argName("file")
				.required()
				.build());
		options.addOption(Option.builder("m")
				.longOpt("metric")
				.hasArg()
				.argName("value")
				.required()
				.build());
		options.addOption(Option.builder("o")
				.longOpt("output")
				.hasArg()
				.argName("file")
				.build());
		options.addOption(Option.builder("r")
				.longOpt("resamples")
				.hasArg()
				.argName("number")
				.build());

		return options;
	}

	@Override
	public void run(CommandLine commandLine) throws Exception {
		//setup the parameters
		ParameterSet parameterSet = ParameterSet.load(new File(commandLine.getOptionValue("parameterFile")));
		int index = MetricFileWriter.getMetricIndex(commandLine.getOptionValue("metric"));
		int P = parameterSet.size();
		int resamples = 1000;
		
		if (commandLine.hasOption("resamples")) {
			resamples = Integer.parseInt(commandLine.getOptionValue("resamples"));
		}

		//load the model response file
		File input = new File(commandLine.getOptionValue("input"));
		double[] responses = MatrixIO.loadColumn(input, index);
		int N = responses.length / (2 * P + 2);
		
		SobolSensitivityAnalysis analysis = new SobolSensitivityAnalysis(parameterSet, N, resamples);
		SobolSensitivityResult result = analysis.evaluate(responses);

		try (PrintWriter output = createOutputWriter(commandLine.getOptionValue("output"))) {
			result.save(output);
		}
	}

	/**
	 * Command line utility for global sensitivity analysis using Sobol's global variance decomposition based on
	 * Saltelli's work.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new SobolAnalysis().start(args);
	}

}
