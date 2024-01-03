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
package org.moeaframework.core.indicator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Population;
import org.moeaframework.core.PopulationIO;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.util.io.RedirectStream;

/**
 * Calculates hypervolume using a compiled executable.  This originally was
 * intended to allow use of faster, natively compiled codes.  However, today
 * the MOEA Framework's default hypervolume code is sufficient for most purposes.
 * <p>
 * If this feature is still required, it can be configured by setting the
 * {@code org.moeaframework.core.indicator.native.hypervolume}
 * system property or in {@value Settings#DEFAULT_CONFIGURATION_FILE}.  This setting specifies
 * the command line for running the executable.  The command can be customized using
 * the following substitutions:
 * <ul>
 *   <li>{0} number of objectives
 *   <li>{1} approximation set size
 *   <li>{2} file containing the approximation set
 *   <li>{3} file containing the reference point
 *   <li>{4} the reference point, separated by spaces
 * </ul>
 * Note: To avoid unnecessarily writing files, the command is first checked
 * if the above arguments are specified.  Use the exact argument string as
 * shown above (e.g., {@code {3}}) in the command.
 */
public class NativeHypervolume extends NormalizedIndicator {

	/**
	 * Constructs a hypervolume evaluator for the specified problem and 
	 * reference set.
	 * 
	 * @param problem the problem
	 * @param referenceSet the reference set
	 */
	public NativeHypervolume(Problem problem, NondominatedPopulation referenceSet) {
		super(problem, referenceSet, true);
	}
	
	/**
	 * Constructs a hypervolume evaluator for the specified problem using the
	 * given reference set and reference point.
	 * 
	 * @param problem the problem
	 * @param referenceSet the reference set
	 * @param referencePoint the reference point
	 */
	public NativeHypervolume(Problem problem, NondominatedPopulation referenceSet, double[] referencePoint) {
		super(problem, referenceSet, referencePoint);
	}
	
	/**
	 * Constructs a hypervolume evaluator for the specified problem using the
	 * given minimum and maximum bounds.
	 * 
	 * @param problem the problem
	 * @param minimum the minimum bounds of the set
	 * @param maximum the maximum bounds of the set
	 */
	public NativeHypervolume(Problem problem, double[] minimum, double[] maximum) {
		super(problem, new NondominatedPopulation(), minimum, maximum);
	}

	/**
	 * Inverts the objective values since this hypervolume algorithm operates
	 * on maximization problems.
	 * 
	 * @param problem the problem
	 * @param solution the solution to be inverted
	 */
	protected static void invert(Problem problem, Solution solution) {
		for (int j = 0; j < problem.getNumberOfObjectives(); j++) {
			double value = solution.getObjective(j);

			if (value < 0.0) {
				value = 0.0;
			} else if (value > 1.0) {
				value = 1.0;
			}

			solution.setObjective(j, 1.0 - value);
		}
	}

	@Override
	public double evaluate(NondominatedPopulation approximationSet) {
		return evaluate(problem, normalize(approximationSet));
	}

	/**
	 * Computes the hypervolume of the normalized approximation set.
	 * 
	 * @param problem the problem
	 * @param approximationSet the normalized approximation set
	 * @return the hypervolume of the normalized approximation set
	 */
	static double evaluate(Problem problem, NondominatedPopulation approximationSet) {
		if (Settings.getHypervolume() == null) {
			throw new FrameworkException("must specify hypervolume command as system property or in " +
					Settings.DEFAULT_CONFIGURATION_FILE);
		}
		
		boolean isInverted = Settings.isHypervolumeInverted();

		List<Solution> solutions = new ArrayList<Solution>();

		outer: for (Solution solution : approximationSet) {
			//prune any solutions which exceed the Nadir point
			for (int i=0; i<solution.getNumberOfObjectives(); i++) {
				if (solution.getObjective(i) > 1.0) {
					continue outer;
				}
			}
			
			Solution clone = solution.copy();
					
			if (isInverted) {
				invert(problem, clone);
			}
					
			solutions.add(clone);
		}

		return invokeNativeHypervolume(problem, solutions, isInverted);
	}

	/**
	 * Generates the input files and calls the executable to calculate hypervolume.
	 * 
	 * @param problem the problem
	 * @param solutions the normalized and possibly inverted solutions
	 * @param isInverted {@code true} if the solutions are inverted;
	 *        {@code false} otherwise
	 * @return the hypervolume value
	 */
	protected static double invokeNativeHypervolume(Problem problem, List<Solution> solutions, boolean isInverted) {
		try {
			String command = Settings.getHypervolume();
			
			//compute the nadir point for minimization or maximization scenario
			double nadirPoint;
			
			if (isInverted) {
				nadirPoint = 0.0; // - Settings.getHypervolumeDelta();
			} else {
				nadirPoint = 1.0; // + Settings.getHypervolumeDelta();
			}
			
			//generate approximation set file
			File approximationSetFile = File.createTempFile("approximationSet", null);
			approximationSetFile.deleteOnExit();
				
			PopulationIO.writeObjectives(approximationSetFile, solutions);
			
			//conditionally generate reference point file
			File referencePointFile = null;
			
			if (command.contains("{3}")) {
				referencePointFile = File.createTempFile("referencePoint", null);
				referencePointFile.deleteOnExit();

				Solution referencePoint = new Solution(new double[problem.getNumberOfObjectives()]);

				for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
					referencePoint.setObjective(i, nadirPoint);
				}

				PopulationIO.writeObjectives(referencePointFile, new Population(new Solution[] { referencePoint }));
			}
			
			//conditionally generate reference point argument
			StringBuilder referencePointString = null;
			
			if (command.contains("{4}")) {
				referencePointString = new StringBuilder();
				
				for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
					if (i > 0) {
						referencePointString.append(' ');
					}
					
					referencePointString.append(nadirPoint);
				}
			}

			// construct the command for invoking the native process
			Object[] arguments = new Object[] {
					(Integer)problem.getNumberOfObjectives(),
					(Integer)solutions.size(),
					approximationSetFile.getCanonicalPath(),
					referencePointFile == null ? "" : referencePointFile.getCanonicalPath(),
					referencePointString == null ? "" : referencePointString.toString()};

			// invoke the native process
			return invokeNativeProcess(MessageFormat.format(command, arguments));
		} catch (IOException e) {
			throw new FrameworkException(e);
		}
	}

	/**
	 * Invokes the native process whose last output token should be the indicator value.
	 * 
	 * @param command the command to execute
	 * @return the indicator value
	 * @throws IOException if an I/O error occurred
	 */
	private static double invokeNativeProcess(String command) throws IOException {
		Process process = new ProcessBuilder(Settings.parseCommand(command)).start();
		RedirectStream.redirect(process.getErrorStream(), System.err);
		String lastLine = null;

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
			String line = null;

			while ((line = reader.readLine()) != null) {
				lastLine = line;
			}
		}

		String[] tokens = lastLine.split("\\s+");
		return Double.parseDouble(tokens[tokens.length - 1]);
	}

}
