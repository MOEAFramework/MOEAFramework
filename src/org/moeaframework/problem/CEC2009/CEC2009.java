/* Copyright 2009-2016 David Hadka
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

/**
 * Implementations for the actual function evaluations used by the CEC 2009
 * test problem suite.
 */
class CEC2009 {
	
	/**
	 * Private constructor to prevent instantiation.
	 */
	private CEC2009() {
		super();
	}
    
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
	 * Transforms the decision variables according to the given rotation matrix
	 * and scaling values.  This is used in the UF11 and UF12 problems.
	 * 
	 * @param x the original decision variables
	 * @param zz the transformed decision variables output
	 * @param psum the penalty value output
	 * @param M the rotation matrix
	 * @param lamda_l the scaling values
	 * @param nx the number of decision variables
	 * @param n_obj the number of objectives
	 */
	public static void transform(double[] x, double[] zz, double[] psum, 
			double[][] M, double[] lamda_l, int nx, int n_obj) {
		int k = nx - n_obj + 1;
		double[] p = new double[nx];
		
		for (int i = 0; i < nx; i++) {
			double z = 0.0;
			
			for (int j = 0; j < nx; j++) {
				z += M[i][j] * x[j];
			}
			
			if (z >= 0 && z <= 1) {
				zz[i] = z;
				p[i] = 0;
			} else if (z < 0) {
				zz[i] = -lamda_l[i] * z;
				p[i] = -z;
			} else {
				zz[i] = 1 - lamda_l[i] * (z - 1);
				p[i] = z - 1;
			}
		}
		
		for (int j = 0; j < n_obj; j++) {
			psum[j] = 0;
		}
		
		for (int i = nx - k + 1; i <= nx; i++) {
			for (int j = 0; j < n_obj; j++) {
				psum[j] = Math.sqrt(Math.pow(psum[j], 2)
						+ Math.pow(p[i - 1], 2));
			}
		}
		
		for (int i = 1; i <= n_obj; i++) {
			for (int j = n_obj - i; j >= 1; j--) {
				psum[i - 1] = Math.sqrt(Math.pow(psum[i - 1], 2)
						+ Math.pow(p[j - 1], 2));
			}
			
			if (i > 1) {
				psum[i - 1] = Math.sqrt(Math.pow(psum[i - 1], 2)
						+ Math.pow(p[(n_obj - i + 1) - 1], 2));
			}
		}
	}

}
