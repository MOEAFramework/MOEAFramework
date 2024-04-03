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
package org.moeaframework.problem.BBOB2016;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.spi.ProblemProvider;

/* 
 * The following source code is derived from the Coco Framework available at <https://github.com/numbbo/coco> under the
 * 3-clause BSD license. The original code is copyright 2013 by the NumBBO/CoCO team.  See the AUTHORS file located in
 * the Coco Framework repository for more details.
 */

/**
 * Problem provider for all problems in the BBOB 2016 test suite.  This test suite consists of bi-objective problems
 * constructed from two single-objective functions.  Each single-objective function name follows the pattern:
 * <pre>
 *     bbob_f001_i02_d05
 * </pre>
 * In this case, we are requesting the second instance of the first BBOB function with five decision variables.  The
 * location of the optimum differs in each instance.  To create the name of a BBOB 2016 problem supported by this
 * problem provider, separate two function names a comma, such as:
 * <pre>
 *     bbob_f001_i02_d05,bbob_f021_i02_d07
 * </pre>
 * The dimension or number of decision variables must be identical in both functions.  This also recognizes the problem
 * name format used by the Coco Framework:
 * <pre>
 *     bbob-biobj(bbob_f001_i02_d05__bbob_f021_i02_d07)
 * </pre>
 */
public class BBOB2016Problems extends ProblemProvider {
	
	private static final Pattern singleProblemPattern = Pattern.compile(
			"^bbob_f([0-9]+)_i([0-9]+)_d([0-9]+)$");
	
	private static final Pattern multiProblemPattern = Pattern.compile(
			"^bbob_f([0-9]+)_i([0-9]+)_d([0-9]+)((\\,|__)bbob_f([0-9]+)_i([0-9]+)_d([0-9]+))*$");
	
	/**
	 * Constructs the problem provider for BBOB 2016 test suite.
	 */
	public BBOB2016Problems() {
		super();
	}

	@Override
	public Problem getProblem(String name) {
		name = name.toLowerCase();
		
		if (name.startsWith("bbob-biobj(") && name.endsWith(")")) {
			name = name.substring(11, name.length()-1);
		}
		
		Matcher matcher = multiProblemPattern.matcher(name);

		if (matcher.matches()) {
			String[] parts = name.split("(,|__)");
			BBOBFunction[] functions = new BBOBFunction[parts.length];
			
			for (int i = 0; i < parts.length; i++) {
				Matcher singleMatcher = singleProblemPattern.matcher(parts[i]);
				
				if (singleMatcher.matches()) {
					functions[i] = createInstance(
							Integer.parseInt(singleMatcher.group(1)),
							Integer.parseInt(singleMatcher.group(3)),
							Integer.parseInt(singleMatcher.group(2)));
				} else {
					throw new FrameworkException("unable to parse BBOB function " + parts[i]);
				}
			}
			
			return new StackedProblem(functions);
		} else {
			return null;
		}
	}

	@Override
	public NondominatedPopulation getReferenceSet(String name) {
		return null;
	}

	/**
	 * Constructs an instance of one of the BBOB test functions.
	 * 
	 * @param function the index of the test function
	 * @param dimension the number of decision variables
	 * @param instance the function instance
	 * @return the BBOB test function
	 */
	@SuppressWarnings("resource")
	public static BBOBFunction createInstance(int function, int dimension, int instance) {
		int rseed = function + 10000 * instance;

		return switch (function) {
			case 1 -> {
				double[] xopt = BBOBUtils.computeXOpt(rseed, dimension);
				double fopt = BBOBUtils.computeFOpt(function, instance);
	
				BBOBFunction problem = new Sphere(dimension);
				problem = new TransformVariablesShift(problem, xopt);
				problem = new TransformObjectiveShift(problem, fopt);
				yield problem;
			}
			case 2 -> {
				double[] xopt = BBOBUtils.computeXOpt(rseed, dimension);
				double fopt = BBOBUtils.computeFOpt(function, instance);
	
				BBOBFunction problem = new Ellipsoid(dimension);
				problem = new TransformVariablesOscillate(problem);
				problem = new TransformVariablesShift(problem, xopt);
				problem = new TransformObjectiveShift(problem, fopt);
				yield problem;
			}
			case 6 -> {
				double[] xopt = BBOBUtils.computeXOpt(rseed, dimension);
				double fopt = BBOBUtils.computeFOpt(function, instance);
				double[] b = new double[dimension];
				double[][] M = new double[dimension][dimension];
	
				double[][] rot1 = BBOBUtils.computeRotation(rseed + 1000000, dimension);
				double[][] rot2 = BBOBUtils.computeRotation(rseed, dimension);
	
				for (int i = 0; i < dimension; i++) {
					b[i] = 0.0;
	
					for (int j = 0; j < dimension; j++) {
						M[i][j] = 0.0;
	
						for (int k = 0; k < dimension; k++) {
							double exponent = 1.0 * k / (dimension - 1.0);
							M[i][j] += rot1[i][k] * Math.pow(Math.sqrt(10.0), exponent) * rot2[k][j];
						}
					}
				}
	
				BBOBFunction problem = new AttractiveSector(dimension, xopt);
				problem = new TransformObjectiveOscillate(problem);
				problem = new TransformObjectivePower(problem, 0.9);
				problem = new TransformObjectiveShift(problem, fopt);
				problem = new TransformVariablesAffine(problem, M, b);
				problem = new TransformVariablesShift(problem, xopt);
				yield problem;
			}
			case 8 -> {
				double[] xopt = BBOBUtils.computeXOpt(rseed, dimension);
				double fopt = BBOBUtils.computeFOpt(function, instance);
				double[] minusOne = new double[dimension];
	
				for (int i = 0; i < dimension; i++) {
					minusOne[i] = -1.0;
					xopt[i] *= 0.75;
				}
	
				BBOBFunction problem = new Rosenbrock(dimension);
				problem = new TransformVariablesShift(problem, minusOne);
				problem = new TransformVariablesScale(problem, Math.max(1.0, Math.sqrt(dimension) / 8.0));
				problem = new TransformVariablesShift(problem, xopt);
				problem = new TransformObjectiveShift(problem, fopt);
				yield problem;
			}
			case 13 -> {
				double[] xopt = BBOBUtils.computeXOpt(rseed, dimension);
				double fopt = BBOBUtils.computeFOpt(function, instance);
				double[] b = new double[dimension];
				double[][] M = new double[dimension][dimension];
	
				double[][] rot1 = BBOBUtils.computeRotation(rseed + 1000000, dimension);
				double[][] rot2 = BBOBUtils.computeRotation(rseed, dimension);
	
				for (int i = 0; i < dimension; i++) {
					b[i] = 0.0;
	
					for (int j = 0; j < dimension; j++) {
						M[i][j] = 0.0;
	
						for (int k = 0; k < dimension; k++) {
							double exponent = 1.0 * k / (dimension - 1.0);
							M[i][j] += rot1[i][k] * Math.pow(Math.sqrt(10.0), exponent) * rot2[k][j];
						}
					}
				}
	
				BBOBFunction problem = new SharpRidge(dimension);
				problem = new TransformObjectiveShift(problem, fopt);
				problem = new TransformVariablesAffine(problem, M, b);
				problem = new TransformVariablesShift(problem, xopt);
				yield problem;
			}
			case 14 -> {
				double[] xopt = BBOBUtils.computeXOpt(rseed, dimension);
				double fopt = BBOBUtils.computeFOpt(function, instance);
				double[] b = new double[dimension];
				double[][] M = BBOBUtils.computeRotation(rseed + 1000000, dimension);
	
				BBOBFunction problem = new DifferentPowers(dimension);
				problem = new TransformObjectiveShift(problem, fopt);
				problem = new TransformVariablesAffine(problem, M, b);
				problem = new TransformVariablesShift(problem, xopt);
				yield problem;
			}
			case 15 -> {
				double[] xopt = BBOBUtils.computeXOpt(rseed, dimension);
				double fopt = BBOBUtils.computeFOpt(function, instance);
				double[] zeros = new double[dimension];
				double[] b = new double[dimension];
				double[][] M = new double[dimension][dimension];
	
				double[][] rot1 = BBOBUtils.computeRotation(rseed + 1000000, dimension);
				double[][] rot2 = BBOBUtils.computeRotation(rseed, dimension);
	
				for (int i = 0; i < dimension; i++) {
					b[i] = 0.0;
	
					for (int j = 0; j < dimension; j++) {
						M[i][j] = 0.0;
	
						for (int k = 0; k < dimension; k++) {
							double exponent = 1.0 * k / (dimension - 1.0);
							M[i][j] += rot1[i][k] * Math.pow(Math.sqrt(10.0), exponent) * rot2[k][j];
						}
					}
				}
	
				BBOBFunction problem = new Rastrigin(dimension);
				problem = new TransformObjectiveShift(problem, fopt);
				problem = new TransformVariablesAffine(problem, M, b);
				problem = new TransformVariablesAsymmetric(problem, 0.2);
				problem = new TransformVariablesOscillate(problem);
				problem = new TransformVariablesAffine(problem, rot1, zeros);
				problem = new TransformVariablesShift(problem, xopt);
				yield problem;
			}
			case 17 -> {
				double[] xopt = BBOBUtils.computeXOpt(rseed, dimension);
				double fopt = BBOBUtils.computeFOpt(function, instance);
				double[] zeros = new double[dimension];
				double[] b = new double[dimension];
				double[][] M = new double[dimension][dimension];
	
				double[][] rot1 = BBOBUtils.computeRotation(rseed + 1000000, dimension);
				double[][] rot2 = BBOBUtils.computeRotation(rseed, dimension);
	
				for (int i = 0; i < dimension; i++) {
					b[i] = 0.0;
	
					for (int j = 0; j < dimension; j++) {
						double exponent = 1.0 * i / (dimension - 1.0);
						M[i][j] += rot2[i][j] * Math.pow(Math.sqrt(10.0), exponent);
					}
				}
	
				BBOBFunction problem = new Schaffers(dimension);
				problem = new TransformObjectiveShift(problem, fopt);
				problem = new TransformVariablesAffine(problem, M, b);
				problem = new TransformVariablesAsymmetric(problem, 0.5);
				problem = new TransformVariablesAffine(problem, rot1, zeros);
				problem = new TransformVariablesShift(problem, xopt);
				problem = new TransformObjectivePenalize(problem, 10.0);
				yield problem;
			}
			case 20 -> {
				double[] tmp1 = BBOBUtils.computeXOpt(rseed, dimension);
				double fopt = BBOBUtils.computeFOpt(function, instance);
				double[] xopt = new double[dimension];
				double[] tmp2 = new double[dimension];
				double[] b = new double[dimension];
				double[][] M = new double[dimension][dimension];
	
				for (int i = 0; i < dimension; i++) {
					xopt[i] = 0.5 * 4.2096874633;
	
					if (tmp1[i] - 0.5 < 0.0) {
						xopt[i] *= -1;
					}
				}
	
				for (int i = 0; i < dimension; i++) {
					b[i] = 0.0;
	
					for (int j = 0; j < dimension; j++) {
						if (i == j) {
							double exponent = 1.0 * i / (dimension - 1.0);
							M[i][j] += Math.pow(Math.sqrt(10.0), exponent);
						}
					}
				}
	
				for (int i = 0; i < dimension; i++) {
					tmp1[i] = -2 * Math.abs(xopt[i]);
					tmp2[i] = 2 * Math.abs(xopt[i]);
				}
	
				BBOBFunction problem = new Schwefel(dimension);
				problem = new TransformObjectiveShift(problem, fopt);
				problem = new TransformVariablesScale(problem, 100);
				problem = new TransformVariablesShift(problem, tmp1);
				problem = new TransformVariablesAffine(problem, M, b);
				problem = new TransformVariablesShift(problem, tmp2);
				problem = new TransformVariablesZHat(problem, xopt);
				problem = new TransformVariablesScale(problem, 2);
				problem = new TransformVariablesXHat(problem, rseed);
				yield problem;
			}
			case 21 -> {
				int numberOfPeaks = 101;
				double maxcondition = 1000.0;
				double maxcondition1 = Math.sqrt(1000.0);
				double b = 10.0;
				double c = 5.0;
				double[] fitvalues = { 1.1, 9.1 };
				double[] xopt = new double[dimension];
				double fopt = BBOBUtils.computeFOpt(function, instance);
				double[][] xLocal = new double[dimension][numberOfPeaks];
				double[][] arrScales = new double[numberOfPeaks][dimension];
				double[][] rotation = BBOBUtils.computeRotation(rseed, dimension);
	
				/* Initialize all the data of the inner problem */
				double[] gallagher_peaks = BBOBUtils.uniform(numberOfPeaks-1, rseed);
				List<Integer> rperm = new ArrayList<Integer>();
	
				for (int i = 0; i < numberOfPeaks-1; i++) {
					rperm.add(i);
				}
	
				Collections.sort(rperm, new GallagherPeakComprator(gallagher_peaks));
	
				/* Random permutation */
				double[] arrCondition = new double[numberOfPeaks];
				arrCondition[0] = maxcondition1;
	
				double[] peaks = new double[numberOfPeaks];
				peaks[0] = 10;
	
				for (int i = 1; i < numberOfPeaks; i++) {
					arrCondition[i] = Math.pow(maxcondition, rperm.get(i-1) / (numberOfPeaks - 2.0));
					peaks[i] = (i - 1.0) / (numberOfPeaks - 2.0) * (fitvalues[1] - fitvalues[0]) + fitvalues[0];
				}
	
				for (int i = 0; i < numberOfPeaks; i++) {
					rperm.clear();
					gallagher_peaks = BBOBUtils.uniform(dimension, rseed + (1000*i));
	
					for (int j = 0; j < dimension; j++) {
						rperm.add(j);
					}
	
					Collections.sort(rperm, new GallagherPeakComprator(gallagher_peaks));
					
					for (int j = 0; j < dimension; j++) {
						arrScales[i][j] = Math.pow(arrCondition[i], rperm.get(j) / (dimension - 1.0) - 0.5);
					}
				}
	
				gallagher_peaks = BBOBUtils.uniform(dimension*numberOfPeaks, rseed);
	
				for (int i = 0; i < dimension; i++) {
					xopt[i] = 0.8 * (b * gallagher_peaks[i] - c);
	
					for (int j = 0; j < numberOfPeaks; j++) {
						xLocal[i][j] = 0.0;
	
						for (int k = 0; k < dimension; k++) {
							xLocal[i][j] += rotation[i][k] * (b * gallagher_peaks[j * dimension + k] - c);
						}
	
						if (j == 0) {
							xLocal[i][j] *= 0.8;
						}
					}
				}
	
				BBOBFunction problem = new Gallagher(dimension, rotation, xLocal, arrScales, peaks);
				problem = new TransformObjectiveShift(problem, fopt);
				yield problem;
			}
			default -> throw new FrameworkException("unknown BBOB function " + function);
		};
	}

	/**
	 * Comparator used by the Gallagher function to order the peaks by height.
	 */
	private static class GallagherPeakComprator implements Comparator<Integer> {

		/**
		 * The height of each peak.
		 */
		private final double[] gallagher_peaks;

		/**
		 * Constructs a new comparator for ordering Gallagher peaks by height.
		 * 
		 * @param gallagher_peaks the height of each peak
		 */
		public GallagherPeakComprator(double[] gallagher_peaks) {
			super();
			this.gallagher_peaks = gallagher_peaks;
		}

		@Override
		public int compare(Integer i1, Integer i2) {
			return Double.compare(gallagher_peaks[i1], gallagher_peaks[i2]);
		}

	}

}
