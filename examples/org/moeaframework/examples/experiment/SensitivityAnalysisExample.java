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
package org.moeaframework.examples.experiment;

import java.io.File;

import org.moeaframework.analysis.sensitivity.Evaluator;
import org.moeaframework.analysis.sensitivity.ResultFileEvaluator;
import org.moeaframework.analysis.sensitivity.SampleGenerator;
import org.moeaframework.analysis.sensitivity.SobolAnalysis;
import org.moeaframework.core.Settings;

/**
 * Performs Sobol sensitivity analysis on the parameters of NSGA-II whens solving the 2-objective DTLZ2
 * problem.  The sensitivity is based on the hypervolume metric.
 * 
 * The result will show the first, total, and second-order effects for the parameters.  Larger values
 * indicate the parameter (or pair for second-order effects) has more influence on the hypervolume metric.
 * 
 * Please note that this example generates and evaluates several thousand samples.  It may take
 * several minutes to process.
 */
public class SensitivityAnalysisExample {
	
	public static void main(String[] args) throws Exception {
		// hide warnings when converting samples from doubles to integers
		Settings.PROPERTIES.setBoolean(Settings.KEY_SUPPRESS_TRUNCATION_WARNING, true);
		
		if (!new File("NSGAII_Samples.txt").exists()) {
			System.out.println("Generating samples file...");
			SampleGenerator.main(new String[] { 
					"--parameterFile", "examples/org/moeaframework/examples/experiment/NSGAII_Params.txt",
					"--method", "saltelli",      // required for Sobol analysis
					"--numberOfSamples", "1000", // produces N * (2 * D + 2) samples when using the Saltelli method
					"--output", "NSGAII_Samples.txt"
			});
		}
		
		System.out.println("Evaluating NSGA-II on DTLZ2...");
		Evaluator.main(new String[] {
				"--parameterFile", "examples/org/moeaframework/examples/experiment/NSGAII_Params.txt",
				"--input", "NSGAII_Samples.txt",
				"--output", "NSGAII_DTLZ2_Results.txt",
				"--problem", "DTLZ2",
				"--algorithm", "NSGAII",
				"--epsilon", "0.01"
		});
		
		System.out.println("Evaluating performance metrics...");
		ResultFileEvaluator.main(new String[] {
				"--problem", "DTLZ2",
				"--input", "NSGAII_DTLZ2_Results.txt",
				"--output", "NSGAII_DTLZ2_Metrics.txt",
				"--epsilon", "0.01",
				"--force"
		});
				
		System.out.println("Computing sensitivity analysis results...");
		SobolAnalysis.main(new String[] {
				"--parameterFile", "examples/org/moeaframework/examples/experiment/NSGAII_Params.txt",
				"--input", "NSGAII_DTLZ2_Metrics.txt",
				"--metric", "hypervolume"
		});
	}

}
