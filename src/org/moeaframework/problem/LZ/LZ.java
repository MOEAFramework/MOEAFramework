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
package org.moeaframework.problem.LZ;

import java.util.ArrayList;
import java.util.List;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;
import org.moeaframework.util.validate.Validate;

/*
 * The following source code is modified from the complicated Pareto set test problem suite by Hui Li and Qingfu Zhang
 * available at {@link http://dces.essex.ac.uk/staff/qzhang/}.  Permission to distributed these modified source codes
 * under the GNU Lesser General Public License was obtained via e-mail correspondence with the authors.
 */

/**
 * Abstract class for implementing problems from the complicated Pareto sets test suite by Hui Li and Qingfu Zhang.
 */
public abstract class LZ extends AbstractProblem {

	/**
	 * The {@code ptype} code specifying the type of Pareto front.
	 */
	private final int pType;

	/**
	 * The {@code ltype} code specifying the type of Pareto set.
	 */
	private final int lType;

	/**
	 * The {@code dtype} code specifying the type of non-negative function.
	 */
	private final int dType;

	/**
	 * Construcs an LZ problem instance with the specified number of variables, number of objectives, {@code ptype}
	 * code, {@code ltype} code, and {@code dtype} code.
	 * 
	 * @param numberOfVariables the number of variables
	 * @param numberOfObjectives the number of objectives
	 * @param pType the {@code ptype} code specifying the type of Pareto front
	 * @param lType the {@code ltype} code specifying the type of Pareto set
	 * @param dType the {@code dtype} code specifying the type of non-negative function
	 */
	public LZ(int numberOfVariables, int numberOfObjectives, int pType, int lType, int dType) {
		super(numberOfVariables, numberOfObjectives);
		this.pType = pType;
		this.lType = lType;
		this.dType = dType;

		validate();
	}

	/**
	 * Returns the {@code ptype} code specifying the type of Pareto front.
	 * 
	 * @return the {@code ptype} code specifying the type of Pareto front
	 */
	public int getPType() {
		return pType;
	}

	/**
	 * Returns the {@code ltype} code specifying the type of Pareto set.
	 * 
	 * @return the {@code ltype} code specifying the type of Pareto set
	 */
	public int getLType() {
		return lType;
	}

	/**
	 * Returns the {@code dtype} code specifying the type of non-negative function.
	 * 
	 * @return the {@code dtype} code specifying the type of non-negative function
	 */
	public int getDType() {
		return dType;
	}

	@Override
	public void evaluate(Solution solution) {
		solution.setObjectiveValues(evaluate(RealVariable.getReal(solution)));
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(numberOfVariables, numberOfObjectives);

		for (int i = 0; i < numberOfVariables; i++) {
			solution.setVariable(i, new RealVariable(0.0, 1.0));
		}

		return solution;
	}

	/**
	 * Validates the inputs, throwing an {@link IllegalArgumentException} if any inputs are invalid.
	 */
	private void validate() {
		Validate.that("numberOfObjectives", numberOfObjectives).isBetween(2, 3);

		if (numberOfObjectives == 2) {
			Validate.that("pType", pType).isBetween(21, 24);
		} else if (numberOfObjectives == 3) {
			Validate.that("pType", pType).isBetween(31, 34);
		}
		
		Validate.that("dType", dType).isBetween(1, 4);

		if (numberOfObjectives == 2) {
			Validate.that("lType", lType).isBetween(21, 26);
		} else {
			Validate.that("lType", lType).isBetween(31, 32);
		}
	}

	/**
	 * Controls the Pareto front shape.
	 */
	private double[] alphafunction(double[] x) {
		return switch (numberOfObjectives) {
			case 2 -> {
				yield switch (pType) {
					case 21 -> new double[] { x[0], 1 - Math.sqrt(x[0]) };
					case 22 -> new double[] { x[0], 1 - x[0] * x[0] };
					case 23 -> new double[] { x[0], 1 - Math.sqrt(x[0]) - x[0] * Math.sin(10 * x[0] * x[0] * Math.PI) };
					case 24 -> new double[] { x[0], 1 - x[0] - 0.05 * Math.sin(4 * Math.PI * x[0]) };
					default -> throw new IllegalStateException();
				};
			}
			case 3 -> {
				yield switch (pType) {
					case 31 -> new double[] {
							Math.cos(x[0] * Math.PI / 2) * Math.cos(x[1] * Math.PI / 2),
							Math.cos(x[0] * Math.PI / 2) * Math.sin(x[1] * Math.PI / 2),
							Math.sin(x[0] * Math.PI / 2) };
					case 32 -> new double[] {
							1 - Math.cos(x[0] * Math.PI / 2) * Math.cos(x[1] * Math.PI / 2),
							1 - Math.cos(x[0] * Math.PI / 2) * Math.sin(x[1] * Math.PI / 2),
							1 - Math.sin(x[0] * Math.PI / 2) };
					case 33 -> new double[] {
							x[0],
							x[1],
							3 - (Math.sin(3 * Math.PI * x[0]) + Math.sin(3 * Math.PI * x[1])) - 2 * (x[0] + x[1]) };
					case 34 -> new double[] {
							x[0] * x[1],
							x[0] * (1 - x[1]),
							(1 - x[0]) };
					default -> throw new IllegalStateException();
				};
			}
			default -> throw new IllegalStateException();
		};
	}

	/**
	 * Controls the distance.
	 */
	private double betafunction(double[] x) {
		int dim = x.length;

		return switch (dType) {
			case 1 -> {
				double beta = 0;
	
				for (int i = 0; i < dim; i++) {
					beta += x[i] * x[i];
				}
	
				yield 2.0 * beta / dim;
			}
			case 2 -> {
				double beta = 0;
	
				for (int i = 0; i < dim; i++) {
					beta += Math.sqrt(i + 1) * x[i] * x[i];
				}
	
				yield 2.0 * beta / dim;
			}
			case 3 -> {
				double sum = 0;
				double xx;
	
				for (int i = 0; i < dim; i++) {
					xx = 2 * x[i];
					sum += (xx * xx - Math.cos(4 * Math.PI * xx) + 1);
				}
	
				yield 2.0 * sum / dim;
			}
			case 4 -> {
				double sum = 0;
				double prod = 1;
				double xx;
	
				for (int i = 0; i < dim; i++) {
					xx = 2 * x[i];
					sum += xx * xx;
					prod *= Math.cos(10 * Math.PI * xx / Math.sqrt(i + 1));
				}
	
				yield 2.0 * (sum - 2 * prod + 2) / dim;
			}
			default -> throw new IllegalStateException();
		};
	}

	/**
	 * Controls the Pareto set shape for 2D instances.
	 */
	private double psfunc2(double x, double t1, int dim, int css) {
		dim = dim + 1;

		double xy = 2 * (x - 0.5);

		return switch (lType) {
			case 21 -> xy - Math.pow(t1, 0.5 * (numberOfVariables + 3 * dim - 8) / (numberOfVariables - 2));
			case 22 -> xy - Math.sin(6 * Math.PI * t1 + dim * Math.PI / numberOfVariables);
			case 23 -> {
				double theta = 6 * Math.PI * t1 + dim * Math.PI / numberOfVariables;
				double ra = 0.8 * t1;
				if (css == 1) {
					yield xy - ra * Math.cos(theta);
				} else {
					yield xy - ra * Math.sin(theta);
				}
			}
			case 24 -> {
				double theta = 6 * Math.PI * t1 + dim * Math.PI / numberOfVariables;
				double ra = 0.8 * t1;
				if (css == 1) {
					yield xy - ra * Math.cos(theta / 3);
				} else {
					yield xy - ra * Math.sin(theta);
				}
			}
			case 25 -> {
				double rho = 0.8;
				double phi = Math.PI * t1;
				double theta = 6 * Math.PI * t1 + dim * Math.PI / numberOfVariables;
				if (css == 1) {
					yield xy - rho * Math.sin(phi) * Math.sin(theta);
				} else if (css == 2) {
					yield xy - rho * Math.sin(phi) * Math.cos(theta);
				} else {
					yield xy - rho * Math.cos(phi);
				}
			}
			case 26 -> {
				double theta = 6 * Math.PI * t1 + dim * Math.PI / numberOfVariables;
				double ra = 0.3 * t1 * (t1 * Math.cos(4 * theta) + 2);
				if (css == 1) {
					yield xy - ra * Math.cos(theta);
				} else {
					yield xy - ra * Math.sin(theta);
				}
			}
			default -> throw new IllegalStateException();
		};
	}

	/**
	 * Controls the Pareto set shape for 3D instances.
	 */
	private double psfunc3(double x, double t1, double t2, int dim) {
		dim = dim + 1;

		double xy = 4 * (x - 0.5);

		if (lType == 31) {
			double rate = dim / (double)numberOfVariables;
			return xy - 4 * (t1 * t1 * rate + t2 * (1.0 - rate)) + 2;
		} else if (lType == 32) {
			double theta = 2 * Math.PI * t1 + dim * Math.PI / numberOfVariables;
			return xy - 2 * t2 * Math.sin(theta);
		} else {
			throw new IllegalStateException();
		}
	}

	/**
	 * Converts a {@code List<Double>} to a {@code double[]}.
	 * 
	 * @param list the list to be converted
	 * @return an array of doubles containing the values in the list
	 */
	private double[] toArray(List<Double> list) {
		double[] array = new double[list.size()];

		for (int i = 0; i < list.size(); i++) {
			array[i] = list.get(i);
		}

		return array;
	}

	/**
	 * Evaluates the decision variables and returns the objectives.
	 */
	private double[] evaluate(double[] x_var) {
		double[] y_obj = new double[numberOfObjectives];

		switch (numberOfObjectives) {
			case 2 -> {
				switch (lType) {
					case 21, 22, 23, 24, 26 -> {
						List<Double> aa = new ArrayList<>();
						List<Double> bb = new ArrayList<>();
		
						for (int n = 1; n < numberOfVariables; n++) {
							if (n % 2 == 0) {
								aa.add(psfunc2(x_var[n], x_var[0], n, 1));
							} else {
								bb.add(psfunc2(x_var[n], x_var[0], n, 2));
							}
						}
		
						double g = betafunction(toArray(aa));
						double h = betafunction(toArray(bb));
						double[] alpha = alphafunction(x_var);
		
						y_obj[0] = alpha[0] + h;
						y_obj[1] = alpha[1] + g;
					}
					case 25 -> {
						List<Double> aa = new ArrayList<>();
						List<Double> bb = new ArrayList<>();
		
						for (int n = 1; n < numberOfVariables; n++) {
							if (n % 3 == 0) {
								aa.add(psfunc2(x_var[n], x_var[0], n, 1));
							} else if (n % 3 == 1) {
								bb.add(psfunc2(x_var[n], x_var[0], n, 2));
							} else {
								double c = psfunc2(x_var[n], x_var[0], n, 3);
		
								if (n % 2 == 0) {
									aa.add(c);
								} else {
									bb.add(c);
								}
							}
						}
		
						double g = betafunction(toArray(aa));
						double h = betafunction(toArray(bb));
						double[] alpha = alphafunction(x_var);
		
						y_obj[0] = alpha[0] + h;
						y_obj[1] = alpha[1] + g;
					}
					default -> throw new IllegalStateException();
				}
			}
			case 3 -> {
				switch (lType) {
					case 31, 32 -> {
						List<Double> aa = new ArrayList<>();
						List<Double> bb = new ArrayList<>();
						List<Double> cc = new ArrayList<>();
		
						for (int n = 2; n < numberOfVariables; n++) {
							double a = psfunc3(x_var[n], x_var[0], x_var[1], n);
		
							if (n % 3 == 0) {
								aa.add(a);
							} else if (n % 3 == 1) {
								bb.add(a);
							} else {
								cc.add(a);
							}
						}
		
						double g = betafunction(toArray(aa));
						double h = betafunction(toArray(bb));
						double e = betafunction(toArray(cc));
						double[] alpha = alphafunction(x_var);
		
						y_obj[0] = alpha[0] + h;
						y_obj[1] = alpha[1] + g;
						y_obj[2] = alpha[2] + e;
					}
					default -> throw new IllegalStateException();
				}
			}
			default -> throw new IllegalStateException();
		}

		return y_obj;
	}

}
