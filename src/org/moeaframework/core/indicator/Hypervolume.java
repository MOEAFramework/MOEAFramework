/* Copyright 2009-2018 David Hadka
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

/*
 * Portions of this source code are derived from the PISA library.  The PISA
 * license is provided below.
 * 
 * Copyright (c) 2006-2007 Swiss Federal Institute of Technology, Computer 
 * Engineering and Networks Laboratory. All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software and its 
 * documentation for any purpose, without fee, and without written agreement is 
 * hereby granted, provided that the above copyright notice and the following 
 * two paragraphs appear in all copies of this software.
 *
 * IN NO EVENT SHALL THE SWISS FEDERAL INSTITUTE OF TECHNOLOGY, COMPUTER 
 * ENGINEERING AND NETWORKS LABORATORY BE LIABLE TO ANY PARTY FOR DIRECT, 
 * INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING OUT OF THE 
 * USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF THE SWISS FEDERAL 
 * INSTITUTE OF TECHNOLOGY, COMPUTER ENGINEERING AND NETWORKS LABORATORY HAS 
 * BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * THE SWISS FEDERAL INSTITUTE OF TECHNOLOGY, COMPUTER ENGINEERING AND NETWORKS 
 * LABORATORY, SPECIFICALLY DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE. THE SOFTWARE PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE 
 * SWISS FEDERAL INSTITUTE OF TECHNOLOGY, COMPUTER ENGINEERING AND NETWORKS 
 * LABORATORY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, 
 * ENHANCEMENTS, OR MODIFICATIONS.
 */

/**
 * Hypervolume indicator. Represents the volume of objective space dominated by
 * solutions in the approximation set.
 * <p>
 * Due to the computational burden of computing the hypervolume indicator and
 * the various estimation algorithms available, the ability to redirect the
 * hypervolume calculation to an external third-party executable is provided.
 * See {@link #invokeNativeHypervolume} for details.
 */
public class Hypervolume extends NormalizedIndicator {

	/**
	 * Constructs a hypervolume evaluator for the specified problem and 
	 * reference set.
	 * 
	 * @param problem the problem
	 * @param referenceSet the reference set
	 */
	public Hypervolume(Problem problem, NondominatedPopulation referenceSet) {
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
	public Hypervolume(Problem problem, NondominatedPopulation referenceSet,
			double[] referencePoint) {
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
	public Hypervolume(Problem problem, double[] minimum, double[] maximum) {
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

	/**
	 * Returns {@code true} if {@code solution1} dominates {@code solution2}
	 * with respect to the first {@code numberOfObjectives} objectives;
	 * {@code false} otherwise.
	 * 
	 * @param solution1 the first solution
	 * @param solution2 the second solution
	 * @param numberOfObjectives the number of objectives used when determining
	 *        domination
	 * @return {@code true} if {@code solution1} dominates {@code solution2}
	 *         with
	 *         respect to the first {@code numberOfObjectives} objectives;
	 *         {@code false} otherwise
	 */
	private static boolean dominates(Solution solution1, Solution solution2,
			int numberOfObjectives) {
		boolean betterInAnyObjective = false;
		boolean worseInAnyObjective = false;

		for (int i = 0; i < numberOfObjectives; i++) {
			if (worseInAnyObjective) {
				break;
			}

			if (solution1.getObjective(i) > solution2.getObjective(i)) {
				betterInAnyObjective = true;
			} else if (solution1.getObjective(i) < solution2.getObjective(i)) {
				worseInAnyObjective = true;
			}
		}

		return !worseInAnyObjective && betterInAnyObjective;
	}

	/**
	 * Swaps the {@code i}th and {@code j}th indices in the population.
	 * 
	 * @param population the population
	 * @param i the first index to be swapped
	 * @param j the second index to be swapped
	 */
	private static void swap(List<Solution> population, int i, int j) {
		Solution temp = population.get(i);
		population.set(i, population.get(j));
		population.set(j, temp);
	}

	/*
	 * all nondominated points regarding the first 'no_objectives' dimensions
	 * are collected; the points 0..no_points-1 in 'front' are considered; the
	 * points in 'front' are resorted, such that points [0..n-1] represent the
	 * nondominated points; n is returned
	 */
	private static int filterNondominatedSet(List<Solution> population,
			int numberOfSolutions, int numberOfObjectives) {
		int i = 0;
		int n = numberOfSolutions;

		while (i < n) {
			int j = i + 1;
			while (j < n) {
				if (dominates(population.get(i), population.get(j),
						numberOfObjectives)) {
					/* remove point j */
					n--;
					swap(population, j, n);
				} else if (dominates(population.get(j), population.get(i),
						numberOfObjectives)) {
					/*
					 * remove point i; ensure that the point copied to index i
					 * is considered in the next outer loop (thus, decrement i)
					 */
					n--;
					swap(population, i, n);
					i--;
					break;
				} else {
					j++;
				}
			}

			i++;
		}

		return n;
	}

	/*
	 * calculate next value regarding dimension 'objective'; consider points
	 * 0..no_points-1 in 'front'
	 */
	private static double surfaceUnchangedTo(List<Solution> population,
			int numberOfSolutions, int objective) {
		double min = population.get(0).getObjective(objective);

		for (int i = 1; i < numberOfSolutions; i++) {
			min = Math.min(min, population.get(i).getObjective(objective));
		}

		return min;
	}

	/*
	 * remove all points which have a value <= 'threshold' regarding the
	 * dimension 'objective'; the points [0..no_points-1] in 'front' are
	 * considered; 'front' is resorted, such that points [0..n-1] represent the
	 * remaining points; 'n' is returned
	 */
	private static int reduceNondominatedSet(List<Solution> population,
			int numberOfSolutions, int objective, double threshold) {
		int n = numberOfSolutions;

		for (int i = 0; i < n; i++) {
			if (population.get(i).getObjective(objective) <= threshold) {
				n--;
				swap(population, i, n);
			}
		}

		return n;
	}

	/**
	 * The internal, unnormalized hypervolume calculation.  While this method
	 * is public, we do not encourage its use since incorrect arguments can
	 * cause unexpected behavior.  Instead, use the
	 * {@link #Hypervolume(Problem, NondominatedPopulation)} constructor
	 * to create a normalizing version of the hypervolume calculation.
	 * 
	 * @param population the population
	 * @param numberOfSolutions the number of solutions
	 * @param numberOfObjectives the number of objectives
	 * @return the hypervolume metric
	 */
	public static double calculateHypervolume(List<Solution> population,
			int numberOfSolutions, int numberOfObjectives) {
		double volume = 0.0;
		double distance = 0.0;
		int n = numberOfSolutions;

		while (n > 0) {
			int numberOfNondominatedPoints = filterNondominatedSet(population,
					n, numberOfObjectives - 1);

			double tempVolume = 0.0;
			if (numberOfObjectives < 3) {
				tempVolume = population.get(0).getObjective(0);
			} else {
				tempVolume = calculateHypervolume(population,
						numberOfNondominatedPoints, numberOfObjectives - 1);
			}

			double tempDistance = surfaceUnchangedTo(population, n,
					numberOfObjectives - 1);
			volume += tempVolume * (tempDistance - distance);
			distance = tempDistance;
			n = reduceNondominatedSet(population, n, numberOfObjectives - 1,
					distance);
		}

		return volume;
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
	static double evaluate(Problem problem,
			NondominatedPopulation approximationSet) {
		boolean isInverted = true;
		boolean isCustomHypervolume = (Settings.getHypervolume() != null) && 
				(problem.getNumberOfObjectives() > 2) ;
		
		if (isCustomHypervolume) {
			isInverted = Settings.isHypervolumeInverted();
		}

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

		if (isCustomHypervolume) {
			return invokeNativeHypervolume(problem, solutions, isInverted);
		} else {
			return calculateHypervolume(solutions, solutions.size(), 
					problem.getNumberOfObjectives());
		}
	}

	/**
	 * Since hypervolume calculation is expensive, this method provides the
	 * ability to execute a native process to calculate hypervolume. If
	 * provided, the {@code org.moeaframework.core.indicator.native.hypervolume}
	 * system property defines the command for invoking the native hypervolume
	 * executable. The command is a {@link MessageFormat} pattern with the
	 * following arguments available for use:
	 * <ul>
	 * <li>{0} number of objectives
	 * <li>{1} approximation set size
	 * <li>{2} file containing the approximation set
	 * <li>{3} file containing the reference point
	 * <li>{4} the reference point, separated by spaces
	 * </ul>
	 * <p>
	 * Note: To avoid unnecessarily writing files, the command is first checked
	 * if the above arguments are specified.  Use the exact argument string as
	 * shown above (e.g., {@code {3}}) in the command.
	 * 
	 * @param problem the problem
	 * @param solutions the normalized and possibly inverted solutions
	 * @param isInverted {@code true} if the solutions are inverted;
	 *        {@code false} otherwise
	 * @return the hypervolume value
	 */
	protected static double invokeNativeHypervolume(Problem problem,
			List<Solution> solutions, boolean isInverted) {
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
			File approximationSetFile = File.createTempFile(
						"approximationSet", null);
			approximationSetFile.deleteOnExit();
				
			PopulationIO.writeObjectives(approximationSetFile, solutions);
			
			//conditionally generate reference point file
			File referencePointFile = null;
			
			if (command.contains("{3}")) {
				referencePointFile = File.createTempFile("referencePoint",
						null);
				referencePointFile.deleteOnExit();

				Solution referencePoint = new Solution(
						new double[problem.getNumberOfObjectives()]);

				for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
					referencePoint.setObjective(i, nadirPoint);
				}

				PopulationIO.writeObjectives(referencePointFile, 
						new Population(new Solution[] { referencePoint }));
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
					referencePointFile == null ? "" : 
						referencePointFile.getCanonicalPath(),
					referencePointString == null ? "" : 
						referencePointString.toString()};

			// invoke the native process
			return invokeNativeProcess(MessageFormat.format(command, 
					arguments));
		} catch (IOException e) {
			throw new FrameworkException(e);
		}
	}

	/**
	 * Invokes the native process whose last output token should be the
	 * indicator value.
	 * 
	 * @param command the command to execute
	 * @return the indicator value
	 * @throws IOException if an I/O error occurred
	 */
	private static double invokeNativeProcess(String command)
			throws IOException {
		Process process = new ProcessBuilder(
				Settings.parseCommand(command)).start();
		RedirectStream.redirect(process.getErrorStream(), System.err);
		BufferedReader reader = null;
		String lastLine = null;

		try {
			reader = new BufferedReader(new InputStreamReader(process
					.getInputStream()));
			String line = null;

			while ((line = reader.readLine()) != null) {
				lastLine = line;
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}

		String[] tokens = lastLine.split("\\s+");
		return Double.parseDouble(tokens[tokens.length - 1]);
	}

}
