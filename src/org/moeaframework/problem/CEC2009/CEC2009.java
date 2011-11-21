/*
 * The following source code is modified from the CEC 2009 test problem suite
 * available at {@link http://dces.essex.ac.uk/staff/qzhang/}.  Permission to 
 * distributed these modified source codes under the GNU Lesser General Public 
 * License was obtained via e-mail correspondence with the authors.
 */
package org.moeaframework.problem.CEC2009;

class CEC2009 {

	private static final double PI = 3.1415926535897932384626433832795;

	private static final double EPSILON = 1.0e-10;

	private static double MYSIGN(double x) {
		return (x > 0.0 ? 1.0 : -1.0);
	}

	public static void UF1(double[] x, double[] f, int nx) {
		int j, count1, count2;
		double sum1, sum2, yj;
		sum1 = sum2 = 0.0;
		count1 = count2 = 0;
		for (j = 2; j <= nx; j++) {
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

	public static void UF2(double[] x, double[] f, int nx) {
		int j, count1, count2;
		double sum1, sum2, yj;
		sum1 = sum2 = 0.0;
		count1 = count2 = 0;
		for (j = 2; j <= nx; j++) {
			if (j % 2 == 0) {
				yj = x[j - 1]
						- 0.3
						* x[0]
						* (x[0]
								* Math.cos(24.0 * PI * x[0] + 4.0 * j * PI / nx) + 2.0)
						* Math.sin(6.0 * PI * x[0] + j * PI / nx);
				sum2 += yj * yj;
				count2++;
			} else {
				yj = x[j - 1]
						- 0.3
						* x[0]
						* (x[0]
								* Math.cos(24.0 * PI * x[0] + 4.0 * j * PI / nx) + 2.0)
						* Math.cos(6.0 * PI * x[0] + j * PI / nx);
				sum1 += yj * yj;
				count1++;
			}
		}
		f[0] = x[0] + 2.0 * sum1 / (double)count1;
		f[1] = 1.0 - Math.sqrt(x[0]) + 2.0 * sum2 / (double)count2;
	}

	public static void UF3(double[] x, double[] f, int nx) {
		int j, count1, count2;
		double sum1, sum2, prod1, prod2, yj, pj;
		sum1 = sum2 = 0.0;
		count1 = count2 = 0;
		prod1 = prod2 = 1.0;
		for (j = 2; j <= nx; j++) {
			yj = x[j - 1]
					- Math.pow(x[0], 0.5 * (1.0 + 3.0 * (j - 2.0) / (nx - 2.0)));
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

	public static void UF4(double[] x, double[] f, int nx) {
		int j, count1, count2;
		double sum1, sum2, yj, hj;
		sum1 = sum2 = 0.0;
		count1 = count2 = 0;
		for (j = 2; j <= nx; j++) {
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

	public static void UF5(double[] x, double[] f, int nx) {
		int j, count1, count2;
		double sum1, sum2, yj, hj, N, E;
		sum1 = sum2 = 0.0;
		count1 = count2 = 0;
		N = 10.0;
		E = 0.1;
		for (j = 2; j <= nx; j++) {
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

	public static void UF6(double[] x, double[] f, int nx) {
		int j, count1, count2;
		double sum1, sum2, prod1, prod2, yj, hj, pj, N, E;
		N = 2.0;
		E = 0.1;
		sum1 = sum2 = 0.0;
		count1 = count2 = 0;
		prod1 = prod2 = 1.0;
		for (j = 2; j <= nx; j++) {
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
		if (hj < 0.0)
			hj = 0.0;
		f[0] = x[0] + hj + 2.0 * (4.0 * sum1 - 2.0 * prod1 + 2.0)
				/ (double)count1;
		f[1] = 1.0 - x[0] + hj + 2.0 * (4.0 * sum2 - 2.0 * prod2 + 2.0)
				/ (double)count2;
	}

	public static void UF7(double[] x, double[] f, int nx) {
		int j, count1, count2;
		double sum1, sum2, yj;
		sum1 = sum2 = 0.0;
		count1 = count2 = 0;
		for (j = 2; j <= nx; j++) {
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

	public static void UF8(double[] x, double[] f, int nx) {
		int j, count1, count2, count3;
		double sum1, sum2, sum3, yj;
		sum1 = sum2 = sum3 = 0.0;
		count1 = count2 = count3 = 0;
		for (j = 3; j <= nx; j++) {
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

	public static void UF9(double[] x, double[] f, int nx) {
		int j, count1, count2, count3;
		double sum1, sum2, sum3, yj, E;
		E = 0.1;
		sum1 = sum2 = sum3 = 0.0;
		count1 = count2 = count3 = 0;
		for (j = 3; j <= nx; j++) {
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
		if (yj < 0.0)
			yj = 0.0;
		f[0] = 0.5 * (yj + 2 * x[0]) * x[1] + 2.0 * sum1 / (double)count1;
		f[1] = 0.5 * (yj - 2 * x[0] + 2.0) * x[1] + 2.0 * sum2 / (double)count2;
		f[2] = 1.0 - x[1] + 2.0 * sum3 / (double)count3;
	}

	public static void UF10(double[] x, double[] f, int nx) {
		int j, count1, count2, count3;
		double sum1, sum2, sum3, yj, hj;
		sum1 = sum2 = sum3 = 0.0;
		count1 = count2 = count3 = 0;
		for (j = 3; j <= nx; j++) {
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

	public static void CF1(double[] x, double[] f, double[] c, int nx) {
		int j, count1, count2;
		double sum1, sum2, yj, N, a;
		N = 10.0;
		a = 1.0;
		sum1 = sum2 = 0.0;
		count1 = count2 = 0;
		for (j = 2; j <= nx; j++) {
			yj = x[j - 1]
					- Math.pow(x[0], 0.5 * (1.0 + 3.0 * (j - 2.0) / (nx - 2.0)));
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

	public static void CF2(double[] x, double[] f, double[] c, int nx) {
		int j, count1, count2;
		double sum1, sum2, yj, N, a, t;
		N = 2.0;
		a = 1.0;
		sum1 = sum2 = 0.0;
		count1 = count2 = 0;
		for (j = 2; j <= nx; j++) {
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

	public static void CF3(double[] x, double[] f, double[] c, int nx) {
		int j, count1, count2;
		double sum1, sum2, prod1, prod2, yj, pj, N, a;
		N = 2.0;
		a = 1.0;
		sum1 = sum2 = 0.0;
		count1 = count2 = 0;
		prod1 = prod2 = 1.0;
		for (j = 2; j <= nx; j++) {
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

	public static void CF4(double[] x, double[] f, double[] c, int nx) {
		int j;
		double sum1, sum2, yj, t;
		sum1 = sum2 = 0.0;
		for (j = 2; j <= nx; j++) {
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

	public static void CF5(double[] x, double[] f, double[] c, int nx) {
		int j;
		double sum1, sum2, yj;
		sum1 = sum2 = 0.0;
		for (j = 2; j <= nx; j++) {
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

	public static void CF6(double[] x, double[] f, double[] c, int nx) {
		int j;
		double sum1, sum2, yj;
		sum1 = sum2 = 0.0;
		for (j = 2; j <= nx; j++) {
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
		c[1] = x[3]
				- 0.8
				* x[0]
				* Math.sin(6.0 * x[0] * PI + 4.0 * PI / nx)
				- MYSIGN(0.25 * Math.sqrt(1 - x[0]) - 0.5 * (1.0 - x[0]))
				* Math.sqrt(Math.abs(0.25 * Math.sqrt(1 - x[0]) - 0.5
						* (1.0 - x[0])));
	}

	public static void CF7(double[] x, double[] f, double[] c, int nx) {
		int j;
		double sum1, sum2, yj;
		sum1 = sum2 = 0.0;
		for (j = 2; j <= nx; j++) {
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
		c[1] = x[3]
				- Math.sin(6.0 * x[0] * PI + 4.0 * PI / nx)
				- MYSIGN(0.25 * Math.sqrt(1 - x[0]) - 0.5 * (1.0 - x[0]))
				* Math.sqrt(Math.abs(0.25 * Math.sqrt(1 - x[0]) - 0.5
						* (1.0 - x[0])));
	}

	public static void CF8(double[] x, double[] f, double[] c, int nx) {
		int j, count1, count2, count3;
		double sum1, sum2, sum3, yj, N, a;
		N = 2.0;
		a = 4.0;
		sum1 = sum2 = sum3 = 0.0;
		count1 = count2 = count3 = 0;
		for (j = 3; j <= nx; j++) {
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
		c[0] = (f[0] * f[0] + f[1] * f[1])
				/ (1 - f[2] * f[2])
				- a
				* Math.abs(Math
						.sin(N
								* PI
								* ((f[0] * f[0] - f[1] * f[1])
										/ (1 - f[2] * f[2]) + 1.0))) - 1.0;
	}

	public static void CF9(double[] x, double[] f, double[] c, int nx) {
		int j, count1, count2, count3;
		double sum1, sum2, sum3, yj, N, a;
		N = 2.0;
		a = 3.0;
		sum1 = sum2 = sum3 = 0.0;
		count1 = count2 = count3 = 0;
		for (j = 3; j <= nx; j++) {
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
		c[0] = (f[0] * f[0] + f[1] * f[1])
				/ (1 - f[2] * f[2])
				- a
				* Math.sin(N
						* PI
						* ((f[0] * f[0] - f[1] * f[1]) / (1 - f[2] * f[2]) + 1.0))
				- 1.0;
	}

	public static void CF10(double[] x, double[] f, double[] c, int nx) {
		int j, count1, count2, count3;
		double sum1, sum2, sum3, yj, hj, N, a;
		N = 2.0;
		a = 1.0;
		sum1 = sum2 = sum3 = 0.0;
		count1 = count2 = count3 = 0;
		for (j = 3; j <= nx; j++) {
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
		c[0] = (f[0] * f[0] + f[1] * f[1])
				/ (1 - f[2] * f[2])
				- a
				* Math.sin(N
						* PI
						* ((f[0] * f[0] - f[1] * f[1]) / (1 - f[2] * f[2]) + 1.0))
				- 1.0;
	}

	public static void R2_DTLZ2_M5(double[] x, double[] f, int nx, int n_obj) {
		int i = 0;
		int j = 0;
		int k = nx - n_obj + 1;
		double g = 0;
		double M_10D[][] = {
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
		double lamda_l_10D[] = { 0.313, 0.312, 0.321, 0.316, 0.456, 1, 1, 1, 1,
				1 };
		double M_30D[][] = {
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
		double lamda_l_30D[] = { 0.113, 0.105, 0.117, 0.119, 0.108, 0.110,
				0.101, 0.107, 0.111, 0.109, 0.120, 0.108, 0.101, 0.105, 0.116,
				1.000, 1.000, 1.000, 1.000, 1.000, 1.000, 1.000, 1.000, 1.000,
				1.000, 1.000, 1.000, 1.000, 1.000, 1.000 };
		double z[] = new double[nx];
		double zz[] = new double[nx];
		double p[] = new double[nx];
		double psum[] = new double[n_obj];
		double M[][] = new double[nx][nx];
		double lamda_l[] = new double[nx];
		if (nx == 10) {
			for (i = 0; i < nx; i++) {
				for (j = 0; j < nx; j++) {
					M[i][j] = M_10D[i][j];
				}
				lamda_l[i] = lamda_l_10D[i];
			}
		} else {
			for (i = 0; i < nx; i++) {
				for (j = 0; j < nx; j++) {
					M[i][j] = M_30D[i][j];
				}
				lamda_l[i] = lamda_l_30D[i];
			}
		}
		for (i = 0; i < nx; i++) {
			z[i] = 0;
			for (j = 0; j < nx; j++) {
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
		for (j = 0; j < n_obj; j++) {
			psum[j] = 0;
		}
		for (i = nx - k + 1; i <= nx; i++) {
			g += Math.pow(zz[i - 1] - 0.5, 2);
			for (j = 0; j < n_obj; j++) {
				psum[j] = Math.sqrt(Math.pow(psum[j], 2)
						+ Math.pow(p[i - 1], 2));
			}
		}
		for (i = 1; i <= n_obj; i++) {
			double ff = (1 + g);
			for (j = n_obj - i; j >= 1; j--) {
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

	public static void R3_DTLZ3_M5(double[] x, double[] f, int nx, int n_obj) {
		int i = 0;
		int j = 0;
		int k = nx - n_obj + 1;
		double g = 0;
		double M_10D[][] = {
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
		double lamda_l_10D[] = { 0.313, 0.312, 0.321, 0.316, 0.456, 1, 1, 1, 1,
				1 };
		double M_30D[][] = {
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
		double lamda_l_30D[] = { 0.113, 0.105, 0.117, 0.119, 0.108, 0.110,
				0.101, 0.107, 0.111, 0.109, 0.120, 0.108, 0.101, 0.105, 0.116,
				1.000, 1.000, 1.000, 1.000, 1.000, 1.000, 1.000, 1.000, 1.000,
				1.000, 1.000, 1.000, 1.000, 1.000, 1.000 };
		double z[] = new double[nx];
		double zz[] = new double[nx];
		double p[] = new double[nx];
		double psum[] = new double[n_obj];
		double M[][] = new double[nx][nx];
		double lamda_l[] = new double[nx];
		if (nx == 10) {
			for (i = 0; i < nx; i++) {
				for (j = 0; j < nx; j++) {
					M[i][j] = M_10D[i][j];
				}
				lamda_l[i] = lamda_l_10D[i];
			}
		} else {
			for (i = 0; i < nx; i++) {
				for (j = 0; j < nx; j++) {
					M[i][j] = M_30D[i][j];
				}
				lamda_l[i] = lamda_l_30D[i];
			}
		}
		for (i = 0; i < nx; i++) {
			z[i] = 0;
			for (j = 0; j < nx; j++) {
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
		for (j = 0; j < n_obj; j++) {
			psum[j] = 0;
		}
		for (i = nx - k + 1; i <= nx; i++) {
			g += Math.pow(zz[i - 1] - 0.5, 2)
					- Math.cos(20 * PI * (zz[i - 1] - 0.5));
			for (j = 0; j < n_obj; j++) {
				psum[j] = Math.sqrt(Math.pow(psum[j], 2)
						+ Math.pow(p[i - 1], 2));
			}
		}
		g = 100 * (k + g);
		for (i = 1; i <= n_obj; i++) {
			double ff = (1 + g);
			for (j = n_obj - i; j >= 1; j--) {
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

	public static void WFG1_M5(double[] z, double[] f, int nx, int M) {
		int i = 0;
		int j = 0;
		double y[] = new double[30];
		double t1[] = new double[30];
		double t2[] = new double[30];
		double t3[] = new double[30];
		double t4[] = new double[5];
		int k = M == 2 ? 4 : 2 * (M - 1);
		for (i = 0; i < nx; i++) {
			y[i] = z[i] / (2.0 * (i + 1));
		}
		for (i = 0; i < k; i++) {
			t1[i] = y[i];
		}
		for (i = k; i < nx; i++) {
			t1[i] = s_linear(y[i], 0.35);
		}
		for (i = 0; i < k; i++) {
			t2[i] = t1[i];
		}
		for (i = k; i < nx; i++) {
			t2[i] = b_flat(t1[i], 0.8, 0.75, 0.85);
		}
		for (i = 0; i < nx; i++) {
			t3[i] = b_poly(t2[i], 0.02);
		}
		double w[] = new double[30];
		double y_sub[] = new double[30];
		double w_sub[] = new double[30];
		double y_sub2[] = new double[30];
		double w_sub2[] = new double[30];
		for (i = 1; i <= nx; i++) {
			w[i - 1] = 2.0 * i;
		}
		for (i = 1; i <= M - 1; i++) {
			int head = (i - 1) * k / (M - 1);
			int tail = i * k / (M - 1);
			for (j = head; j < tail; j++) {
				y_sub[j - head] = t3[j];
				w_sub[j - head] = w[j];
			}
			t4[i - 1] = r_sum(y_sub, w_sub, tail - head);
		}
		for (j = k; j < nx; j++) {
			y_sub2[j - k] = t3[j];
			w_sub2[j - k] = w[j];
		}
		t4[i - 1] = r_sum(y_sub2, w_sub2, nx - k);
		int m;
		int A[] = new int[5];
		double x[] = new double[5];
		double h[] = new double[5];
		double S[] = new double[5];
		A[0] = 1;
		for (i = 1; i < M - 1; i++) {
			A[i] = 1;
		}
		for (i = 0; i < M - 1; i++) {
			double tmp1;
			tmp1 = t4[M - 1];
			if (A[i] > tmp1) {
				tmp1 = A[i];
			}
			x[i] = tmp1 * (t4[i] - 0.5) + 0.5;
		}
		x[M - 1] = t4[M - 1];
		for (m = 1; m <= M - 1; m++) {
			h[m - 1] = convex(x, m, M);
		}
		h[m - 1] = mixed(x, 5, 1.0);
		for (m = 1; m <= M; m++) {
			S[m - 1] = m * 2.0;
		}
		for (i = 0; i < M; i++) {
			f[i] = 1.0 * x[M - 1] + S[i] * h[i];
		}
	}

	private static double correct_to_01(double aa, double epsilon) {
		double min = 0.0, max = 1.0;
		double min_epsilon = min - epsilon;
		double max_epsilon = max + epsilon;
		if (aa <= min && aa >= min_epsilon) {
			return min;
		} else if (aa >= max && aa <= max_epsilon) {
			return max;
		} else {
			return aa;
		}
	}

	private static double convex(double[] x, int m, int M) {
		int i;
		double result = 1.0;
		for (i = 1; i <= M - m; i++) {
			result *= 1.0 - Math.cos(x[i - 1] * PI / 2.0);
		}
		if (m != 1) {
			result *= 1.0 - Math.sin(x[M - m] * PI / 2.0);
		}
		return correct_to_01(result, EPSILON);
	}

	private static double mixed(double[] x, int A, double alpha) {
		double tmp = 2.0 * A * PI;
		return correct_to_01(Math.pow(1.0 - x[0]
				- Math.cos(tmp * x[0] + PI / 2.0) / tmp, alpha), EPSILON);
	}

	private static double min_double(double aa, double bb) {
		return aa < bb ? aa : bb;
	}

	private static double b_poly(double y, double alpha) {
		return correct_to_01(Math.pow(y, alpha), EPSILON);
	}

	private static double b_flat(double y, double A, double B, double C) {
		double tmp1 = min_double(0.0, Math.floor(y - B)) * A * (B - y) / B;
		double tmp2 = min_double(0.0, Math.floor(C - y)) * (1.0 - A) * (y - C)
				/ (1.0 - C);
		return correct_to_01(A + tmp1 - tmp2, EPSILON);
	}

	private static double s_linear(double y, double A) {
		return correct_to_01(Math.abs(y - A) / Math.abs(Math.floor(A - y) + A),
				EPSILON);
	}

	private static double r_sum(double[] y, double[] w, int ny) {
		int i;
		double numerator = 0.0;
		double denominator = 0.0;
		for (i = 0; i < ny; i++) {
			numerator += w[i] * y[i];
			denominator += w[i];
		}
		return correct_to_01(numerator / denominator, EPSILON);
	}
}
