/* Copyright 2009-2012 David Hadka
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
package org.moeaframework.problem.CEC2009;

class CEC2009 {
    
    /* 
     * The following source code is modified from the CEC 2009 test problem
     * suite available at {@link http://dces.essex.ac.uk/staff/qzhang/}.
     * Permission to distribute these modified source codes under the GNU
     * Lesser General Public License was obtained via e-mail correspondence
     * with the original authors.
     */

	private static final double PI = 3.1415926535897932384626433832795;

	/**
	 * Returns {@code 1.0} if the specified value is strictly positive;
	 * otherwise returns {@code -1.0}.
	 * 
	 * @param x the value
	 * @return {@code 1.0} if the specified value is strictly positive;
	 *         otherwise returns {@code -1.0}
	 */
	private static double MYSIGN(double x) {
		return (x > 0.0 ? 1.0 : -1.0);
	}

	/**
	 * Evaluates the UF1 problem.
	 * 
	 * @param x the decision variables
	 * @param f the objective function output
	 * @param nx the number of decision variables
	 */
	public static void UF1(double[] x, double[] f, int nx) {
		int count1 = 0;
		int count2 = 0;
		double sum1 = 0.0;
		double sum2 = 0.0;
		double yj;

		for (int j = 2; j <= nx; j++) {
			yj = x[j - 1] - Math.sin(6.0 * PI * x[0] + j * PI / nx);
			yj = yj * yj;
			
			if (j % 2 == 0) {
				sum2 += yj;
				count2++;
			} else {
				sum1 += yj;
				count1++;
			}
		}
		
		f[0] = x[0] + 2.0 * sum1 / (double)count1;
		f[1] = 1.0 - Math.sqrt(x[0]) + 2.0 * sum2 / (double)count2;
	}

	/**
	 * Evaluates the UF2 problem.
	 * 
	 * @param x the decision variables
	 * @param f the objective function output
	 * @param nx the number of decision variables
	 */
	public static void UF2(double[] x, double[] f, int nx) {
		int count1 = 0;
		int count2 = 0;
		double sum1 = 0.0;
		double sum2 = 0.0;
		double yj;

		for (int j = 2; j <= nx; j++) {
			if (j % 2 == 0) {
				yj = x[j - 1] - 0.3 * x[0] * (x[0]
						* Math.cos(24.0 * PI * x[0] + 4.0 * j * PI / nx) + 2.0)
						* Math.sin(6.0 * PI * x[0] + j * PI / nx);
				sum2 += yj * yj;
				count2++;
			} else {
				yj = x[j - 1] - 0.3 * x[0] * (x[0]
						* Math.cos(24.0 * PI * x[0] + 4.0 * j * PI / nx) + 2.0)
						* Math.cos(6.0 * PI * x[0] + j * PI / nx);
				sum1 += yj * yj;
				count1++;
			}
		}
		
		f[0] = x[0] + 2.0 * sum1 / (double)count1;
		f[1] = 1.0 - Math.sqrt(x[0]) + 2.0 * sum2 / (double)count2;
	}

	/**
	 * Evaluates the UF3 problem.
	 * 
	 * @param x the decision variables
	 * @param f the objective function output
	 * @param nx the number of decision variables
	 */
	public static void UF3(double[] x, double[] f, int nx) {
		int count1 = 0;
		int count2 = 0;
		double sum1 = 0.0;
		double sum2 = 0.0;
		double prod1 = 1.0;
		double prod2 = 1.0;
		double yj;
		double pj;

		for (int j = 2; j <= nx; j++) {
			yj = x[j - 1] - Math.pow(x[0], 0.5 * (1.0 + 3.0 * (j - 2.0) / 
					(nx - 2.0)));
			pj = Math.cos(20.0 * yj * PI / Math.sqrt(j + 0.0));
			
			if (j % 2 == 0) {
				sum2 += yj * yj;
				prod2 *= pj;
				count2++;
			} else {
				sum1 += yj * yj;
				prod1 *= pj;
				count1++;
			}
		}
		
		f[0] = x[0] + 2.0 * (4.0 * sum1 - 2.0 * prod1 + 2.0) / (double)count1;
		f[1] = 1.0 - Math.sqrt(x[0]) + 2.0 * (4.0 * sum2 - 2.0 * prod2 + 2.0)
				/ (double)count2;
	}

	/**
	 * Evaluates the UF4 problem.
	 * 
	 * @param x the decision variables
	 * @param f the objective function output
	 * @param nx the number of decision variables
	 */
	public static void UF4(double[] x, double[] f, int nx) {
		int count1 = 0;
		int count2 = 0;
		double sum1 = 0.0;
		double sum2 = 0.0;
		double yj;
		double hj;

		for (int j = 2; j <= nx; j++) {
			yj = x[j - 1] - Math.sin(6.0 * PI * x[0] + j * PI / nx);
			hj = Math.abs(yj) / (1.0 + Math.exp(2.0 * Math.abs(yj)));
			
			if (j % 2 == 0) {
				sum2 += hj;
				count2++;
			} else {
				sum1 += hj;
				count1++;
			}
		}
		
		f[0] = x[0] + 2.0 * sum1 / (double)count1;
		f[1] = 1.0 - x[0] * x[0] + 2.0 * sum2 / (double)count2;
	}

	/**
	 * Evaluates the UF5 problem.
	 * 
	 * @param x the decision variables
	 * @param f the objective function output
	 * @param nx the number of decision variables
	 */
	public static void UF5(double[] x, double[] f, int nx) {
		int count1 = 0;
		int count2 = 0;
		double sum1 = 0.0;
		double sum2 = 0.0;
		double yj;
		double hj;
		double N = 10.0;
		double E = 0.1;

		for (int j = 2; j <= nx; j++) {
			yj = x[j - 1] - Math.sin(6.0 * PI * x[0] + j * PI / nx);
			hj = 2.0 * yj * yj - Math.cos(4.0 * PI * yj) + 1.0;
			
			if (j % 2 == 0) {
				sum2 += hj;
				count2++;
			} else {
				sum1 += hj;
				count1++;
			}
		}
		
		hj = (0.5 / N + E) * Math.abs(Math.sin(2.0 * N * PI * x[0]));
		f[0] = x[0] + hj + 2.0 * sum1 / (double)count1;
		f[1] = 1.0 - x[0] + hj + 2.0 * sum2 / (double)count2;
	}

	/**
	 * Evaluates the UF6 problem.
	 * 
	 * @param x the decision variables
	 * @param f the objective function output
	 * @param nx the number of decision variables
	 */
	public static void UF6(double[] x, double[] f, int nx) {
		int count1 = 0;
		int count2 = 0;
		double sum1 = 0.0;
		double sum2 = 0.0;
		double prod1 = 1.0;
		double prod2 = 1.0;
		double yj;
		double hj;
		double pj;
		double N = 2.0;
		double E = 0.1;

		for (int j = 2; j <= nx; j++) {
			yj = x[j - 1] - Math.sin(6.0 * PI * x[0] + j * PI / nx);
			pj = Math.cos(20.0 * yj * PI / Math.sqrt(j + 0.0));
			
			if (j % 2 == 0) {
				sum2 += yj * yj;
				prod2 *= pj;
				count2++;
			} else {
				sum1 += yj * yj;
				prod1 *= pj;
				count1++;
			}
		}
		
		hj = 2.0 * (0.5 / N + E) * Math.sin(2.0 * N * PI * x[0]);
		
		if (hj < 0.0) {
			hj = 0.0;
		}
		
		f[0] = x[0] + hj + 2.0 * (4.0 * sum1 - 2.0 * prod1 + 2.0)
				/ (double)count1;
		f[1] = 1.0 - x[0] + hj + 2.0 * (4.0 * sum2 - 2.0 * prod2 + 2.0)
				/ (double)count2;
	}

	/**
	 * Evaluates the UF7 problem.
	 * 
	 * @param x the decision variables
	 * @param f the objective function output
	 * @param nx the number of decision variables
	 */
	public static void UF7(double[] x, double[] f, int nx) {
		int count1 = 0;
		int count2 = 0;
		double sum1 = 0.0;
		double sum2 = 0.0;
		double yj;

		for (int j = 2; j <= nx; j++) {
			yj = x[j - 1] - Math.sin(6.0 * PI * x[0] + j * PI / nx);
			
			if (j % 2 == 0) {
				sum2 += yj * yj;
				count2++;
			} else {
				sum1 += yj * yj;
				count1++;
			}
		}
		
		yj = Math.pow(x[0], 0.2);
		f[0] = yj + 2.0 * sum1 / (double)count1;
		f[1] = 1.0 - yj + 2.0 * sum2 / (double)count2;
	}

	/**
	 * Evaluates the UF8 problem.
	 * 
	 * @param x the decision variables
	 * @param f the objective function output
	 * @param nx the number of decision variables
	 */
	public static void UF8(double[] x, double[] f, int nx) {
		int count1 = 0;
		int count2 = 0;
		int count3 = 0;
		double sum1 = 0.0;
		double sum2 = 0.0;
		double sum3 = 0.0;
		double yj;

		for (int j = 3; j <= nx; j++) {
			yj = x[j - 1] - 2.0 * x[1]
					* Math.sin(2.0 * PI * x[0] + j * PI / nx);
			
			if (j % 3 == 1) {
				sum1 += yj * yj;
				count1++;
			} else if (j % 3 == 2) {
				sum2 += yj * yj;
				count2++;
			} else {
				sum3 += yj * yj;
				count3++;
			}
		}
		
		f[0] = Math.cos(0.5 * PI * x[0]) * Math.cos(0.5 * PI * x[1]) + 2.0
				* sum1 / (double)count1;
		f[1] = Math.cos(0.5 * PI * x[0]) * Math.sin(0.5 * PI * x[1]) + 2.0
				* sum2 / (double)count2;
		f[2] = Math.sin(0.5 * PI * x[0]) + 2.0 * sum3 / (double)count3;
	}

	/**
	 * Evaluates the UF9 problem.
	 * 
	 * @param x the decision variables
	 * @param f the objective function output
	 * @param nx the number of decision variables
	 */
	public static void UF9(double[] x, double[] f, int nx) {
		int count1 = 0;
		int count2 = 0;
		int count3 = 0;
		double sum1 = 0.0;
		double sum2 = 0.0;
		double sum3 = 0.0;
		double yj;
		double E = 0.1;

		for (int j = 3; j <= nx; j++) {
			yj = x[j - 1] - 2.0 * x[1]
					* Math.sin(2.0 * PI * x[0] + j * PI / nx);
			
			if (j % 3 == 1) {
				sum1 += yj * yj;
				count1++;
			} else if (j % 3 == 2) {
				sum2 += yj * yj;
				count2++;
			} else {
				sum3 += yj * yj;
				count3++;
			}
		}
		
		yj = (1.0 + E) * (1.0 - 4.0 * (2.0 * x[0] - 1.0) * (2.0 * x[0] - 1.0));
		
		if (yj < 0.0) {
			yj = 0.0;
		}
		
		f[0] = 0.5 * (yj + 2 * x[0]) * x[1] + 2.0 * sum1 / (double)count1;
		f[1] = 0.5 * (yj - 2 * x[0] + 2.0) * x[1] + 2.0 * sum2 / (double)count2;
		f[2] = 1.0 - x[1] + 2.0 * sum3 / (double)count3;
	}

	/**
	 * Evaluates the UF10 problem.
	 * 
	 * @param x the decision variables
	 * @param f the objective function output
	 * @param nx the number of decision variables
	 */
	public static void UF10(double[] x, double[] f, int nx) {
		int count1 = 0;
		int count2 = 0;
		int count3 = 0;
		double sum1 = 0.0;
		double sum2 = 0.0;
		double sum3 = 0.0;
		double yj;
		double hj;

		for (int j = 3; j <= nx; j++) {
			yj = x[j - 1] - 2.0 * x[1]
					* Math.sin(2.0 * PI * x[0] + j * PI / nx);
			hj = 4.0 * yj * yj - Math.cos(8.0 * PI * yj) + 1.0;
			
			if (j % 3 == 1) {
				sum1 += hj;
				count1++;
			} else if (j % 3 == 2) {
				sum2 += hj;
				count2++;
			} else {
				sum3 += hj;
				count3++;
			}
		}
		
		f[0] = Math.cos(0.5 * PI * x[0]) * Math.cos(0.5 * PI * x[1]) + 2.0
				* sum1 / (double)count1;
		f[1] = Math.cos(0.5 * PI * x[0]) * Math.sin(0.5 * PI * x[1]) + 2.0
				* sum2 / (double)count2;
		f[2] = Math.sin(0.5 * PI * x[0]) + 2.0 * sum3 / (double)count3;
	}

	/**
	 * Evaluates the CF1 problem.
	 * 
	 * @param x the decision variables
	 * @param f the objective function output
	 * @param c the constraint violation output
	 * @param nx the number of decision variables
	 */
	public static void CF1(double[] x, double[] f, double[] c, int nx) {
		int count1 = 0;
		int count2 = 0;
		double sum1 = 0.0;
		double sum2 = 0.0;
		double yj;
		double N = 10.0;
		double a = 1.0;

		for (int j = 2; j <= nx; j++) {
			yj = x[j - 1] - Math.pow(x[0], 0.5 * (1.0 + 3.0 * (j - 2.0) / 
					(nx - 2.0)));
			
			if (j % 2 == 1) {
				sum1 += yj * yj;
				count1++;
			} else {
				sum2 += yj * yj;
				count2++;
			}
		}
		
		f[0] = x[0] + 2.0 * sum1 / (double)count1;
		f[1] = 1.0 - x[0] + 2.0 * sum2 / (double)count2;
		c[0] = f[1] + f[0] - a
				* Math.abs(Math.sin(N * PI * (f[0] - f[1] + 1.0))) - 1.0;
	}

	/**
	 * Evaluates the CF2 problem.
	 * 
	 * @param x the decision variables
	 * @param f the objective function output
	 * @param c the constraint violation output
	 * @param nx the number of decision variables
	 */
	public static void CF2(double[] x, double[] f, double[] c, int nx) {
		int count1 = 0;
		int count2 = 0;
		double sum1 = 0.0;
		double sum2 = 0.0;
		double yj;
		double N = 2.0;
		double a = 1.0;
		double t;

		for (int j = 2; j <= nx; j++) {
			yj = x[j - 1] - Math.sin(6.0 * PI * x[0] + j * PI / nx);
			
			if (j % 2 == 1) {
				yj = x[j - 1] - Math.sin(6.0 * PI * x[0] + j * PI / nx);
				sum1 += yj * yj;
				count1++;
			} else {
				yj = x[j - 1] - Math.cos(6.0 * PI * x[0] + j * PI / nx);
				sum2 += yj * yj;
				count2++;
			}
		}
		
		f[0] = x[0] + 2.0 * sum1 / (double)count1;
		f[1] = 1.0 - Math.sqrt(x[0]) + 2.0 * sum2 / (double)count2;
		t = f[1] + Math.sqrt(f[0]) - a
				* Math.sin(N * PI * (Math.sqrt(f[0]) - f[1] + 1.0)) - 1.0;
		c[0] = MYSIGN(t) * Math.abs(t) / (1 + Math.exp(4.0 * Math.abs(t)));
	}

	/**
	 * Evaluates the CF3 problem.
	 * 
	 * @param x the decision variables
	 * @param f the objective function output
	 * @param c the constraint violation output
	 * @param nx the number of decision variables
	 */
	public static void CF3(double[] x, double[] f, double[] c, int nx) {
		int count1 = 0;
		int count2 = 0;
		double sum1 = 0.0;
		double sum2 = 0.0;
		double prod1 = 1.0;
		double prod2 = 1.0;
		double yj;
		double pj;
		double N = 2.0;
		double a = 1.0;

		for (int j = 2; j <= nx; j++) {
			yj = x[j - 1] - Math.sin(6.0 * PI * x[0] + j * PI / nx);
			pj = Math.cos(20.0 * yj * PI / Math.sqrt(j + 0.0));
			
			if (j % 2 == 0) {
				sum2 += yj * yj;
				prod2 *= pj;
				count2++;
			} else {
				sum1 += yj * yj;
				prod1 *= pj;
				count1++;
			}
		}
		
		f[0] = x[0] + 2.0 * (4.0 * sum1 - 2.0 * prod1 + 2.0) / (double)count1;
		f[1] = 1.0 - x[0] * x[0] + 2.0 * (4.0 * sum2 - 2.0 * prod2 + 2.0)
				/ (double)count2;
		c[0] = f[1] + f[0] * f[0] - a
				* Math.sin(N * PI * (f[0] * f[0] - f[1] + 1.0)) - 1.0;
	}

	/**
	 * Evaluates the CF4 problem.
	 * 
	 * @param x the decision variables
	 * @param f the objective function output
	 * @param c the constraint violation output
	 * @param nx the number of decision variables
	 */
	public static void CF4(double[] x, double[] f, double[] c, int nx) {
		double sum1 = 0.0;
		double sum2 = 0.0;
		double yj;
		double t;

		for (int j = 2; j <= nx; j++) {
			yj = x[j - 1] - Math.sin(6.0 * PI * x[0] + j * PI / nx);
			
			if (j % 2 == 1) {
				sum1 += yj * yj;
			} else {
				if (j == 2) {
					sum2 += yj < 1.5 - 0.75 * Math.sqrt(2.0) ? Math.abs(yj)
							: (0.125 + (yj - 1) * (yj - 1));
				} else {
					sum2 += yj * yj;
				}
			}
		}
		
		f[0] = x[0] + sum1;
		f[1] = 1.0 - x[0] + sum2;
		t = x[1] - Math.sin(6.0 * x[0] * PI + 2.0 * PI / nx) - 0.5 * x[0]
				+ 0.25;
		c[0] = MYSIGN(t) * Math.abs(t) / (1 + Math.exp(4.0 * Math.abs(t)));
	}

	/**
	 * Evaluates the CF5 problem.
	 * 
	 * @param x the decision variables
	 * @param f the objective function output
	 * @param c the constraint violation output
	 * @param nx the number of decision variables
	 */
	public static void CF5(double[] x, double[] f, double[] c, int nx) {
		double sum1 = 0.0;
		double sum2 = 0.0;
		double yj;

		for (int j = 2; j <= nx; j++) {
			if (j % 2 == 1) {
				yj = x[j - 1] - 0.8 * x[0]
						* Math.cos(6.0 * PI * x[0] + j * PI / nx);
				sum1 += 2.0 * yj * yj - Math.cos(4.0 * PI * yj) + 1.0;
			} else {
				yj = x[j - 1] - 0.8 * x[0]
						* Math.sin(6.0 * PI * x[0] + j * PI / nx);
				
				if (j == 2) {
					sum2 += yj < 1.5 - 0.75 * Math.sqrt(2.0) ? Math.abs(yj)
							: (0.125 + (yj - 1) * (yj - 1));
				} else {
					sum2 += 2.0 * yj * yj - Math.cos(4.0 * PI * yj) + 1.0;
				}
			}
		}
		
		f[0] = x[0] + sum1;
		f[1] = 1.0 - x[0] + sum2;
		c[0] = x[1] - 0.8 * x[0] * Math.sin(6.0 * x[0] * PI + 2.0 * PI / nx)
				- 0.5 * x[0] + 0.25;
	}

	/**
	 * Evaluates the CF6 problem.
	 * 
	 * @param x the decision variables
	 * @param f the objective function output
	 * @param c the constraint violation output
	 * @param nx the number of decision variables
	 */
	public static void CF6(double[] x, double[] f, double[] c, int nx) {
		double sum1 = 0.0;
		double sum2 = 0.0;
		double yj;

		for (int j = 2; j <= nx; j++) {
			if (j % 2 == 1) {
				yj = x[j - 1] - 0.8 * x[0]
						* Math.cos(6.0 * PI * x[0] + j * PI / nx);
				sum1 += yj * yj;
			} else {
				yj = x[j - 1] - 0.8 * x[0]
						* Math.sin(6.0 * PI * x[0] + j * PI / nx);
				sum2 += yj * yj;
			}
		}
		
		f[0] = x[0] + sum1;
		f[1] = (1.0 - x[0]) * (1.0 - x[0]) + sum2;
		c[0] = x[1] - 0.8 * x[0] * Math.sin(6.0 * x[0] * PI + 2.0 * PI / nx)
				- MYSIGN((x[0] - 0.5) * (1.0 - x[0]))
				* Math.sqrt(Math.abs((x[0] - 0.5) * (1.0 - x[0])));
		c[1] = x[3] - 0.8 * x[0] * Math.sin(6.0 * x[0] * PI + 4.0 * PI / nx)
				- MYSIGN(0.25 * Math.sqrt(1 - x[0]) - 0.5 * (1.0 - x[0]))
				* Math.sqrt(Math.abs(0.25 * Math.sqrt(1 - x[0]) - 0.5
						* (1.0 - x[0])));
	}

	/**
	 * Evaluates the CF7 problem.
	 * 
	 * @param x the decision variables
	 * @param f the objective function output
	 * @param c the constraint violation output
	 * @param nx the number of decision variables
	 */
	public static void CF7(double[] x, double[] f, double[] c, int nx) {
		double sum1 = 0.0;
		double sum2 = 0.0;
		double yj;

		for (int j = 2; j <= nx; j++) {
			if (j % 2 == 1) {
				yj = x[j - 1] - Math.cos(6.0 * PI * x[0] + j * PI / nx);
				sum1 += 2.0 * yj * yj - Math.cos(4.0 * PI * yj) + 1.0;
			} else {
				yj = x[j - 1] - Math.sin(6.0 * PI * x[0] + j * PI / nx);
				
				if (j == 2 || j == 4) {
					sum2 += yj * yj;
				} else {
					sum2 += 2.0 * yj * yj - Math.cos(4.0 * PI * yj) + 1.0;
				}
			}
		}
		
		f[0] = x[0] + sum1;
		f[1] = (1.0 - x[0]) * (1.0 - x[0]) + sum2;
		c[0] = x[1] - Math.sin(6.0 * x[0] * PI + 2.0 * PI / nx)
				- MYSIGN((x[0] - 0.5) * (1.0 - x[0]))
				* Math.sqrt(Math.abs((x[0] - 0.5) * (1.0 - x[0])));
		c[1] = x[3] - Math.sin(6.0 * x[0] * PI + 4.0 * PI / nx)
				- MYSIGN(0.25 * Math.sqrt(1 - x[0]) - 0.5 * (1.0 - x[0]))
				* Math.sqrt(Math.abs(0.25 * Math.sqrt(1 - x[0]) - 0.5
						* (1.0 - x[0])));
	}

	/**
	 * Evaluates the CF8 problem.
	 * 
	 * @param x the decision variables
	 * @param f the objective function output
	 * @param c the constraint violation output
	 * @param nx the number of decision variables
	 */
	public static void CF8(double[] x, double[] f, double[] c, int nx) {
		int count1 = 0;
		int count2 = 0;
		int count3 = 0;
		double sum1 = 0.0;
		double sum2 = 0.0;
		double sum3 = 0.0;
		double yj;
		double N = 2.0;
		double a = 4.0;

		for (int j = 3; j <= nx; j++) {
			yj = x[j - 1] - 2.0 * x[1]
					* Math.sin(2.0 * PI * x[0] + j * PI / nx);
			
			if (j % 3 == 1) {
				sum1 += yj * yj;
				count1++;
			} else if (j % 3 == 2) {
				sum2 += yj * yj;
				count2++;
			} else {
				sum3 += yj * yj;
				count3++;
			}
		}
		
		f[0] = Math.cos(0.5 * PI * x[0]) * Math.cos(0.5 * PI * x[1]) + 2.0
				* sum1 / (double)count1;
		f[1] = Math.cos(0.5 * PI * x[0]) * Math.sin(0.5 * PI * x[1]) + 2.0
				* sum2 / (double)count2;
		f[2] = Math.sin(0.5 * PI * x[0]) + 2.0 * sum3 / (double)count3;
		c[0] = (f[0] * f[0] + f[1] * f[1]) / (1 - f[2] * f[2])
				- a * Math.abs(Math.sin(N * PI * ((f[0] * f[0] - f[1] * f[1])
						/ (1 - f[2] * f[2]) + 1.0))) - 1.0;
	}

	/**
	 * Evaluates the CF9 problem.
	 * 
	 * @param x the decision variables
	 * @param f the objective function output
	 * @param c the constraint violation output
	 * @param nx the number of decision variables
	 */
	public static void CF9(double[] x, double[] f, double[] c, int nx) {
		int count1 = 0;
		int count2 = 0;
		int count3 = 0;
		double sum1 = 0.0;
		double sum2 = 0.0;
		double sum3 = 0.0;
		double yj;
		double N = 2.0;
		double a = 3.0;

		for (int j = 3; j <= nx; j++) {
			yj = x[j - 1] - 2.0 * x[1]
					* Math.sin(2.0 * PI * x[0] + j * PI / nx);
			
			if (j % 3 == 1) {
				sum1 += yj * yj;
				count1++;
			} else if (j % 3 == 2) {
				sum2 += yj * yj;
				count2++;
			} else {
				sum3 += yj * yj;
				count3++;
			}
		}
		
		f[0] = Math.cos(0.5 * PI * x[0]) * Math.cos(0.5 * PI * x[1]) + 2.0
				* sum1 / (double)count1;
		f[1] = Math.cos(0.5 * PI * x[0]) * Math.sin(0.5 * PI * x[1]) + 2.0
				* sum2 / (double)count2;
		f[2] = Math.sin(0.5 * PI * x[0]) + 2.0 * sum3 / (double)count3;
		c[0] = (f[0] * f[0] + f[1] * f[1]) / (1 - f[2] * f[2])
				- a * Math.sin(N * PI * ((f[0] * f[0] - f[1] * f[1]) / 
						(1 - f[2] * f[2]) + 1.0)) - 1.0;
	}

	/**
	 * Evaluates the CF10 problem.
	 * 
	 * @param x the decision variables
	 * @param f the objective function output
	 * @param c the constraint violation output
	 * @param nx the number of decision variables
	 */
	public static void CF10(double[] x, double[] f, double[] c, int nx) {
		int count1 = 0;
		int count2 = 0;
		int count3 = 0;
		double sum1 = 0.0;
		double sum2 = 0.0;
		double sum3 = 0.0;
		double yj;
		double hj;
		double N = 2.0;
		double a = 1.0;

		for (int j = 3; j <= nx; j++) {
			yj = x[j - 1] - 2.0 * x[1]
					* Math.sin(2.0 * PI * x[0] + j * PI / nx);
			hj = 4.0 * yj * yj - Math.cos(8.0 * PI * yj) + 1.0;
			
			if (j % 3 == 1) {
				sum1 += hj;
				count1++;
			} else if (j % 3 == 2) {
				sum2 += hj;
				count2++;
			} else {
				sum3 += hj;
				count3++;
			}
		}
		
		f[0] = Math.cos(0.5 * PI * x[0]) * Math.cos(0.5 * PI * x[1]) + 2.0
				* sum1 / (double)count1;
		f[1] = Math.cos(0.5 * PI * x[0]) * Math.sin(0.5 * PI * x[1]) + 2.0
				* sum2 / (double)count2;
		f[2] = Math.sin(0.5 * PI * x[0]) + 2.0 * sum3 / (double)count3;
		c[0] = (f[0] * f[0] + f[1] * f[1]) / (1 - f[2] * f[2])
				- a * Math.sin(N * PI * ((f[0] * f[0] - f[1] * f[1]) / 
						(1 - f[2] * f[2]) + 1.0)) - 1.0;
	}

	/**
	 * Evaluates the UF11 problem.
	 * 
	 * @param x the decision variables
	 * @param f the objective function output
	 * @param nx the number of decision variables
	 * @param n_obj the number of objectives
	 */
	public static void R2_DTLZ2_M5(double[] x, double[] f, int nx, int n_obj) {
		int k = nx - n_obj + 1;
		double g = 0;
		double[][] M_10D = {
				{ 0.0346, -0.7523, 0.3561, -0.2958, 0.4675, 0, 0, 0, 0, 0 },
				{ 0.8159, -0.0423, 0.4063, 0.3455, -0.2192, 0, 0, 0, 0, 0 },
				{ -0.3499, 0.3421, 0.8227, -0.2190, -0.1889, 0, 0, 0, 0, 0 },
				{ -0.0963, -0.4747, -0.0998, -0.2429, -0.8345, 0, 0, 0, 0, 0 },
				{ -0.4487, -0.2998, 0.1460, 0.8283, -0.0363, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 1, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 1, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 1, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 1, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 } };
		double[] lamda_l_10D = { 0.313, 0.312, 0.321, 0.316, 0.456, 1, 1, 1, 1,
				1 };
		double[][] M_30D = {
				{ 0.0128, 0.2165, 0.4374, -0.0800, 0.0886, -0.2015, 0.1071,
						0.2886, 0.2354, 0.2785, -0.1748, 0.2147, 0.1649,
						-0.3043, 0.5316, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0.000, 0.000 },
				{ 0.4813, 0.2420, -0.3663, -0.0420, -0.0088, -0.4945, -0.3073,
						0.1990, 0.0441, -0.0627, 0.0191, 0.3880, -0.0618,
						-0.0319, -0.1833, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0.000, 0.000 },
				{ 0.4816, -0.2254, 0.0663, 0.4801, 0.2009, -0.0008, -0.1501,
						0.0269, -0.2037, 0.4334, -0.2157, -0.3175, -0.0923,
						0.1451, 0.1118, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0.000, 0.000 },
				{ -0.0876, -0.2667, -0.0063, 0.2114, 0.4506, 0.0823, -0.0125,
						0.2313, 0.0840, -0.2376, 0.1938, -0.0030, 0.3391,
						0.0863, 0.1231, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0.000, 0.000 },
				{ -0.1025, 0.4011, -0.0117, 0.2076, 0.2585, 0.1124, -0.0288,
						0.3095, -0.6146, -0.2376, 0.1938, -0.0030, 0.3391,
						0.0863, 0.1231, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0.000, 0.000 },
				{ 0.4543, -0.2761, -0.2985, -0.2837, 0.0634, 0.1070, 0.2996,
						-0.2690, -0.1634, -0.1452, 0.1799, -0.0014, 0.2394,
						-0.2745, 0.3969, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0.000, 0.000 },
				{ -0.1422, -0.4364, 0.0751, -0.2235, 0.3966, -0.0252, 0.0908,
						0.0477, -0.2254, 0.1801, -0.0552, 0.5770, -0.0396,
						0.3765, -0.0522, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0.000, 0.000 },
				{ 0.3542, -0.2245, 0.3497, -0.1609, -0.1107, 0.0079, 0.2241,
						0.4517, 0.1309, -0.3355, -0.1123, -0.1831, 0.3000,
						0.2045, -0.3191, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0.000, 0.000 },
				{ 0.0005, 0.0377, -0.2808, -0.0641, 0.1316, 0.2191, 0.0207,
						0.3308, 0.4117, 0.3839, 0.5775, -0.1219, 0.1192,
						0.2435, 0.0414, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0.000, 0.000 },
				{ -0.1177, -0.0001, -0.1992, -0.4533, 0.4234, -0.0191, -0.3740,
						0.1325, 0.0972, -0.2042, -0.3493, -0.4018, -0.1087,
						0.0918, 0.2217, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0.000, 0.000 },
				{ 0.1818, 0.3022, -0.1388, -0.2380, -0.0773, 0.6463, 0.0450,
						0.1030, -0.0958, 0.2837, -0.3969, 0.1779, -0.0251,
						-0.1543, -0.2452, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0.000, 0.000 },
				{ -0.1889, -0.4397, -0.2206, 0.0981, -0.5203, 0.1325, -0.3427,
						0.4242, -0.1271, -0.0291, -0.0795, 0.1213, 0.0565,
						-0.1092, 0.2720, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0 },
				{ -0.1808, -0.0624, -0.2689, 0.2289, 0.1128, -0.0844, -0.0549,
						-0.2202, 0.2450, 0.0825, -0.3319, 0.0513, 0.7523,
						0.0043, -0.1472, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0 },
				{ -0.0983, 0.0611, -0.4145, 0.3017, 0.0410, -0.0703, 0.6250,
						0.2449, 0.1307, -0.1714, -0.3045, 0.0218, -0.2837,
						0.1408, 0.1633, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0 },
				{ 0.2026, 0.0324, 0.1496, 0.3129, 0.1437, 0.4331, -0.2629,
						-0.1498, 0.3746, -0.4366, 0.0163, 0.3316, -0.0697,
						0.1833, 0.2412, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						1, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 1, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 1, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 1, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 1, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 1, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 1, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 1, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 1, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 1 } };
		double[] lamda_l_30D = { 0.113, 0.105, 0.117, 0.119, 0.108, 0.110,
				0.101, 0.107, 0.111, 0.109, 0.120, 0.108, 0.101, 0.105, 0.116,
				1.000, 1.000, 1.000, 1.000, 1.000, 1.000, 1.000, 1.000, 1.000,
				1.000, 1.000, 1.000, 1.000, 1.000, 1.000 };
		double[] z = new double[nx];
		double[] zz = new double[nx];
		double[] p = new double[nx];
		double[] psum = new double[n_obj];
		double[][] M = new double[nx][nx];
		double[] lamda_l = new double[nx];
		
		if (nx == 10) {
			for (int i = 0; i < nx; i++) {
				for (int j = 0; j < nx; j++) {
					M[i][j] = M_10D[i][j];
				}
				
				lamda_l[i] = lamda_l_10D[i];
			}
		} else {
			for (int i = 0; i < nx; i++) {
				for (int j = 0; j < nx; j++) {
					M[i][j] = M_30D[i][j];
				}
				
				lamda_l[i] = lamda_l_30D[i];
			}
		}
		
		for (int i = 0; i < nx; i++) {
			z[i] = 0;
			
			for (int j = 0; j < nx; j++) {
				z[i] += M[i][j] * x[j];
			}
			
			if (z[i] >= 0 && z[i] <= 1) {
				zz[i] = z[i];
				p[i] = 0;
			} else if (z[i] < 0) {
				zz[i] = -lamda_l[i] * z[i];
				p[i] = -z[i];
			} else {
				zz[i] = 1 - lamda_l[i] * (z[i] - 1);
				p[i] = z[i] - 1;
			}
		}
		
		for (int j = 0; j < n_obj; j++) {
			psum[j] = 0;
		}
		
		for (int i = nx - k + 1; i <= nx; i++) {
			g += Math.pow(zz[i - 1] - 0.5, 2);
			
			for (int j = 0; j < n_obj; j++) {
				psum[j] = Math.sqrt(Math.pow(psum[j], 2)
						+ Math.pow(p[i - 1], 2));
			}
		}
		
		for (int i = 1; i <= n_obj; i++) {
			double ff = (1 + g);
			
			for (int j = n_obj - i; j >= 1; j--) {
				ff *= Math.cos(zz[j - 1] * PI / 2.0);
				psum[i - 1] = Math.sqrt(Math.pow(psum[i - 1], 2)
						+ Math.pow(p[j - 1], 2));
			}
			
			if (i > 1) {
				ff *= Math.sin(zz[(n_obj - i + 1) - 1] * PI / 2.0);
				psum[i - 1] = Math.sqrt(Math.pow(psum[i - 1], 2)
						+ Math.pow(p[(n_obj - i + 1) - 1], 2));
			}
			
			f[i - 1] = 2.0 / (1 + Math.exp(-psum[i - 1])) * (ff + 1);
		}
	}

	/**
	 * Evaluates the UF12 problem.
	 * 
	 * @param x the decision variables
	 * @param f the objective function output
	 * @param nx the number of decision variables
	 * @param n_obj the number of objectives
	 */
	public static void R3_DTLZ3_M5(double[] x, double[] f, int nx, int n_obj) {
		int k = nx - n_obj + 1;
		double g = 0;
		double[][] M_10D = {
				{ -0.2861, 0.2796, -0.8507, 0.2837, 0.1893, 0, 0, 0, 0, 0 },
				{ 0.2837, 0.8861, 0.1219, -0.3157, 0.1407, 0, 0, 0, 0, 0 },
				{ 0.6028, 0.1119, -0.0810, 0.5963, -0.5119, 0, 0, 0, 0, 0 },
				{ -0.6450, 0.3465, 0.4447, 0.4753, -0.2005, 0, 0, 0, 0, 0 },
				{ 0.2414, -0.0635, 0.2391, 0.4883, 0.8013, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 1, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 1, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 1, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 1, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 } };
		double[] lamda_l_10D = { 0.313, 0.312, 0.321, 0.316, 0.456, 1, 1, 1, 1,
				1 };
		double[][] M_30D = {
				{ -0.1565, -0.2418, 0.5427, -0.2191, 0.2522, -0.0563, 0.1991,
						0.1166, 0.2140, -0.0973, -0.0755, 0.4073, 0.4279,
						-0.1876, -0.0968, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0.000, 0.000 },
				{ 0.1477, -0.2396, -0.0022, 0.4180, 0.2675, -0.1365, -0.0729,
						0.4761, -0.0685, 0.2105, 0.1388, 0.1465, -0.0256,
						0.0292, 0.5767, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0.000, 0.000 },
				{ 0.0322, 0.3727, -0.0467, 0.1651, -0.0672, 0.0638, -0.1168,
						0.4055, 0.6714, -0.1948, -0.1451, 0.1734, -0.2788,
						-0.0769, -0.1433, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0.000, 0.000 },
				{ -0.3688, 0.1935, 0.3691, 0.4298, 0.2340, 0.2593, -0.3081,
						-0.2013, -0.2779, -0.0932, 0.0003, 0.0149, -0.2303,
						-0.3261, -0.0517, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0.000, 0.000 },
				{ 0.0580, -0.0609, 0.0004, -0.1831, 0.0003, 0.4742, -0.2530,
						-0.0750, 0.0839, 0.1606, 0.6020, 0.4103, -0.0857,
						0.2954, -0.0819, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0.000, 0.000 },
				{ -0.2145, -0.0056, -0.0251, 0.2288, -0.4870, -0.5486, 0.1253,
						-0.1512, -0.0390, 0.0722, 0.3074, 0.4160, -0.1304,
						-0.1610, -0.0848, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0.000, 0.000 },
				{ 0.2557, -0.1087, 0.0679, -0.3120, 0.3567, -0.4644, -0.3535,
						0.1060, -0.2158, -0.1330, -0.0154, 0.0911, -0.4154,
						0.0356, -0.3085, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0.000, 0.000 },
				{ 0.2303, 0.4996, 0.1883, 0.1870, 0.1850, -0.0216, 0.4409,
						-0.0573, -0.2396, 0.1471, -0.1540, 0.2731, -0.0398,
						0.4505, -0.1131, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0.000, 0.000 },
				{ -0.1576, -0.0023, 0.2588, 0.2105, 0.2250, -0.2978, 0.0175,
						-0.1157, 0.3717, 0.0562, 0.4068, -0.5081, 0.0718,
						0.3443, -0.1488, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0.000, 0.000 },
				{ 0.1047, -0.0568, -0.2771, 0.3803, 0.0046, 0.0188, -0.1500,
						0.2053, -0.2290, -0.4582, 0.1191, 0.0639, 0.4946,
						0.1121, -0.4018, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0.000, 0.000 },
				{ 0.3943, -0.0374, 0.3004, 0.1472, -0.2988, 0.0443, -0.2483,
						0.1350, -0.0160, 0.5834, -0.1095, -0.1398, 0.1711,
						-0.1867, -0.3518, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0.000, 0.000 },
				{ 0.1244, -0.6134, 0.1823, 0.3012, -0.1968, 0.1616, 0.1025,
						-0.1972, 0.1162, -0.2079, -0.3062, 0.0585, -0.3286,
						0.3187, -0.0812, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0 },
				{ 0.1832, -0.1559, -0.4327, 0.2059, 0.4677, 0.0317, 0.2233,
						-0.3589, 0.2393, 0.2468, 0.0148, 0.1193, -0.0279,
						-0.3600, -0.2261, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0 },
				{ 0.5027, 0.1935, 0.1571, 0.0503, -0.0503, -0.1443, -0.3080,
						-0.4939, 0.1847, -0.2762, 0.0042, 0.0960, 0.2239,
						-0.0579, 0.3840, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0 },
				{ 0.3948, -0.0002, 0.2172, -0.0293, -0.0835, 0.1614, 0.4559,
						0.1626, -0.1155, -0.3087, 0.4331, -0.2223, -0.2213,
						-0.3658, -0.0188, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						1, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 1, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 1, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 1, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 1, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 1, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 1, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 1, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 1, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 1 } };
		double[] lamda_l_30D = { 0.113, 0.105, 0.117, 0.119, 0.108, 0.110,
				0.101, 0.107, 0.111, 0.109, 0.120, 0.108, 0.101, 0.105, 0.116,
				1.000, 1.000, 1.000, 1.000, 1.000, 1.000, 1.000, 1.000, 1.000,
				1.000, 1.000, 1.000, 1.000, 1.000, 1.000 };
		double[] z = new double[nx];
		double[] zz = new double[nx];
		double[] p = new double[nx];
		double[] psum = new double[n_obj];
		double[][] M = new double[nx][nx];
		double[] lamda_l = new double[nx];
		
		if (nx == 10) {
			for (int i = 0; i < nx; i++) {
				for (int j = 0; j < nx; j++) {
					M[i][j] = M_10D[i][j];
				}
				
				lamda_l[i] = lamda_l_10D[i];
			}
		} else {
			for (int i = 0; i < nx; i++) {
				for (int j = 0; j < nx; j++) {
					M[i][j] = M_30D[i][j];
				}
				
				lamda_l[i] = lamda_l_30D[i];
			}
		}
		
		for (int i = 0; i < nx; i++) {
			z[i] = 0;
			
			for (int j = 0; j < nx; j++) {
				z[i] += M[i][j] * x[j];
			}
			
			if (z[i] >= 0 && z[i] <= 1) {
				zz[i] = z[i];
				p[i] = 0;
			} else if (z[i] < 0) {
				zz[i] = -lamda_l[i] * z[i];
				p[i] = -z[i];
			} else {
				zz[i] = 1 - lamda_l[i] * (z[i] - 1);
				p[i] = z[i] - 1;
			}
		}
		
		for (int j = 0; j < n_obj; j++) {
			psum[j] = 0;
		}
		
		for (int i = nx - k + 1; i <= nx; i++) {
			g += Math.pow(zz[i - 1] - 0.5, 2)
					- Math.cos(20 * PI * (zz[i - 1] - 0.5));
			
			for (int j = 0; j < n_obj; j++) {
				psum[j] = Math.sqrt(Math.pow(psum[j], 2)
						+ Math.pow(p[i - 1], 2));
			}
		}
		
		g = 100 * (k + g);
		
		for (int i = 1; i <= n_obj; i++) {
			double ff = (1 + g);
			
			for (int j = n_obj - i; j >= 1; j--) {
				ff *= Math.cos(zz[j - 1] * PI / 2.0);
				psum[i - 1] = Math.sqrt(Math.pow(psum[i - 1], 2)
						+ Math.pow(p[j - 1], 2));
			}
			
			if (i > 1) {
				ff *= Math.sin(zz[(n_obj - i + 1) - 1] * PI / 2.0);
				psum[i - 1] = Math.sqrt(Math.pow(psum[i - 1], 2)
						+ Math.pow(p[(n_obj - i + 1) - 1], 2));
			}
			
			f[i - 1] = 2.0 / (1 + Math.exp(-psum[i - 1])) * (ff + 1);
		}
	}

}
