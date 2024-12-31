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
package org.moeaframework.analysis.sensitivity;

import java.io.PrintStream;
import java.io.PrintWriter;

import org.apache.commons.io.output.CloseShieldOutputStream;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.stat.StatUtils;
import org.moeaframework.analysis.parameter.Parameter;
import org.moeaframework.analysis.parameter.ParameterSet;
import org.moeaframework.analysis.sample.Samples;
import org.moeaframework.core.PRNG;
import org.moeaframework.util.sequence.Saltelli;

/**
 * Global sensitivity analysis of blackbox model output using Saltelli's improved Sobol' global variance decomposition
 * procedure.
 * <ol>
 *   <li>When requesting {@code N} samples, the {@link Saltelli} sampling strategy generates {@code N * (2 * P + 2)}
 *       actual samples, where {@code P} is the number of parameters being analyzed.
 *   <li>Negative sensitivity values can occur and typically coincide with large confidence intervals.  Increasing the
 *       sample size can help, but this generally means the sensitivities are near zero.
 * </ol>
 * <p>
 * This code was derived and translated from the C code used in the Tang et al. (2007) study cited below.
 * <p>
 * References:
 * <ol>
 *   <li>Tang, Y., Reed, P., Wagener, T., and van Werkhoven, K., "Comparing Sensitivity Analysis Methods to Advance
 *       Lumped Watershed Model Identification and Evaluation," Hydrology and Earth System Sciences, vol. 11, no. 2,
 *       pp. 793-817, 2007.
 *   <li>Saltelli, A., et al. "Global Sensitivity Analysis: The Primer." John Wiley &amp; Sons Ltd, 2008.
 * </ol>
 */
public class SobolSensitivityAnalysis implements SensitivityAnalysis<SobolSensitivityAnalysis.SobolSensitivityResult> {

	private final int resamples;

	private final ParameterSet parameterSet;

	private final int N;

	/**
	 * Constructs a new Sobol' global variance decomposition instance.
	 * 
	 * @param parameterSet the parameters being analyzed
	 * @param N the number of samples
	 */
	public SobolSensitivityAnalysis(ParameterSet parameterSet, int N) {
		this(parameterSet, N, 1000);
	}
	
	/**
	 * Constructs a new Sobol' global variance decomposition instance.
	 * 
	 * @param parameterSet the parameters being analyzed
	 * @param N the number of samples
	 * @param resamples the number of resamples used to bootstrap the 50% confidence intervals
	 */
	public SobolSensitivityAnalysis(ParameterSet parameterSet, int N, int resamples) {
		super();
		this.parameterSet = parameterSet;
		this.N = N;
		this.resamples = resamples;
	}
	
	@Override
	public ParameterSet getParameterSet() {
		return parameterSet;
	}
	
	@Override
	public Samples generateSamples() {
		return parameterSet.sample(N * (2 * parameterSet.size() + 2), new Saltelli());
	}
	
	@Override
	public SobolSensitivityResult evaluate(double[] responses) {
		int expectedResponses = N * (2 * parameterSet.size() + 2);
		
		if (expectedResponses != responses.length) {
			throw new IllegalArgumentException("Expected N * (2 * P + 2) = " + expectedResponses +
					" responses, given " + responses.length);
		}
		
		return new SobolSensitivityResult(responses);
	}
	
	/**
	 * Computes the results of the Sobol' global variance decomposition procedure.
	 */
	public class SobolSensitivityResult implements SensitivityResult, FirstOrderSensitivity, SecondOrderSensitivity,
	TotalOrderSensitivity {
		
		/**
		 * Model responses from the original parameters.
		 */
		private final double[] A;

		/**
		 * Model responses from the resampled parameters.
		 */
		private final double[] B;

		/**
		 * Model responses from the original samples where the j-th parameter is replaced by the corresponding
		 * resampled parameter.
		 */
		private final double[][] C_A;

		/**
		 * Model responses from the resampled samples where the j-th parameter is replaced by the corresponding
		 * original parameter.
		 */
		private final double[][] C_B;
		
		private SobolSensitivityResult(double[] responses) {
			super();
			A = new double[N];
			B = new double[N];
			C_A = new double[N][parameterSet.size()];
			C_B = new double[N][parameterSet.size()];
			
			int index = 0;

			for (int i = 0; i < N; i++) {
				A[i] = responses[index++];

				for (int j = 0; j < parameterSet.size(); j++) {
					C_A[i][j] = responses[index++];
				}

				for (int j = 0; j < parameterSet.size(); j++) {
					C_B[i][j] = responses[index++];
				}

				B[i] = responses[index++];
			}
		}
		
		@Override
		public ParameterSet getParameterSet() {
			return parameterSet;
		}
		
		@Override
		public Sensitivity<Parameter<?>> getFirstOrder(Parameter<?> key) {
			int index = parameterSet.indexOf(key);
			double[] a0 = new double[N];
			double[] a1 = new double[N];
			double[] a2 = new double[N];

			for (int i = 0; i < N; i++) {
				a0[i] = A[i];
				a1[i] = C_A[i][index];
				a2[i] = B[i];
			}
			
			return new Sensitivity<>(key, computeFirstOrder(a0, a1, a2), computeFirstOrderConfidence(a0, a1, a2));
		}
		
		@Override
		public Sensitivity<Parameter<?>> getTotalOrder(Parameter<?> key) {
			int index = parameterSet.indexOf(key);
			double[] a0 = new double[N];
			double[] a1 = new double[N];
			double[] a2 = new double[N];

			for (int i = 0; i < N; i++) {
				a0[i] = A[i];
				a1[i] = C_A[i][index];
				a2[i] = B[i];
			}
			
			return new Sensitivity<>(key, computeTotalOrder(a0, a1, a2), computeTotalOrderConfidence(a0, a1, a2));
		}
		
		@Override
		public Sensitivity<Pair<Parameter<?>, Parameter<?>>> getSecondOrder(Parameter<?> left, Parameter<?> right) {
			int leftIndex = parameterSet.indexOf(left);
			int rightIndex = parameterSet.indexOf(right);
			double[] a0 = new double[N];
			double[] a1 = new double[N];
			double[] a2 = new double[N];
			double[] a3 = new double[N];
			double[] a4 = new double[N];

			for (int i = 0; i < N; i++) {
				a0[i] = A[i];
				a1[i] = C_B[i][leftIndex];
				a2[i] = C_A[i][rightIndex];
				a3[i] = C_A[i][leftIndex];
				a4[i] = B[i];
			}
			
			return new Sensitivity<>(Pair.of(left, right), computeSecondOrder(a0, a1, a2, a3, a4),
					computeSecondOrderConfidence(a0, a1, a2, a3, a4));
		}
		
		/**
		 * Computes and displays the first-, total-, and second- order Sobol' sensitivities and 50% bootstrap
		 * confidence intervals.
		 * 
		 * @param output the output writer
		 */
		public void save(PrintWriter output) {
			output.println("Parameter	Sensitivity [Confidence]");

			output.println("First-Order Effects");
			for (int j = 0; j < parameterSet.size(); j++) {
				Sensitivity<Parameter<?>> result = getFirstOrder(parameterSet.get(j));

				output.print("  ");
				output.print(result.getKey().getName());
				output.print(' ');
				output.print(result.getSensitivity());
				output.print(" [");
				output.print(result.getConfidenceInterval());
				output.println(']');
			}

			output.println("Total-Order Effects");
			for (int j = 0; j < parameterSet.size(); j++) {
				Sensitivity<Parameter<?>> result = getTotalOrder(parameterSet.get(j));

				output.print("  ");
				output.print(result.getKey().getName());
				output.print(' ');
				output.print(result.getSensitivity());
				output.print(" [");
				output.print(result.getConfidenceInterval());
				output.println(']');
			}

			output.println("Second-Order Effects");
			for (int j = 0; j < parameterSet.size(); j++) {
				for (int k = j + 1; k < parameterSet.size(); k++) {
					Sensitivity<Pair<Parameter<?>, Parameter<?>>> result = getSecondOrder(parameterSet.get(j),
							parameterSet.get(k));

					output.print("  ");
					output.print(result.getKey().getLeft().getName());
					output.print(" * ");
					output.print(result.getKey().getRight().getName());
					output.print(' ');
					output.print(result.getSensitivity());
					output.print(" [");
					output.print(result.getConfidenceInterval());
					output.println(']');
				}
			}
		}
		
		@Override
		public void display(PrintStream out) {
			try (PrintWriter writer = new PrintWriter(CloseShieldOutputStream.wrap(out))) {
				save(writer);
			}
		}
		
		/**
		 * Returns the first-order sensitivity of the i-th parameter.  Note how the contents of the array {@code a1}
		 * specify the parameter being analyzed.
		 * 
		 * @param a0 the output from the first independent samples
		 * @param a1 the output from the samples produced by swapping the i-th parameter in the first independent samples
		 *        with the i-th parameter from the second independent samples
		 * @param a2 the output from the second independent samples
		 * @return the first-order sensitivity of the i-th parameter
		 */
		private double computeFirstOrder(double[] a0, double[] a1, double[] a2) {
			double c = 0.0;
			for (int i = 0; i < N; i++) {
				c += a0[i];
			}
			c /= N;

			double tmp1 = 0.0;
			double tmp2 = 0.0;
			double tmp3 = 0.0;
			double EY2 = 0.0;

			for (int i = 0; i < N; i++) {
				EY2 += (a0[i] - c) * (a2[i] - c);
				tmp1 += (a2[i] - c) * (a2[i] - c);
				tmp2 += (a2[i] - c);
				tmp3 += (a1[i] - c) * (a2[i] - c);
			}

			EY2 /= N;

			double V = (tmp1 / (N - 1)) - Math.pow(tmp2 / N, 2.0);
			double U = tmp3 / (N - 1);

			return (U - EY2) / V;
		}
		
		/**
		 * Returns the first-order confidence interval of the i-th parameter.  The arguments to this method mirror the
		 * arguments to {@link #computeFirstOrder}.
		 * 
		 * @param a0 the output from the first independent samples
		 * @param a1 the output from the samples produced by swapping the i-th parameter in the first independent samples
		 *        with the i-th parameter from the second independent samples
		 * @param a2 the output from the second independent samples
		 * @return the first-order confidence interval of the i-th parameter
		 */
		private double computeFirstOrderConfidence(double[] a0, double[] a1, double[] a2) {
			double[] b0 = new double[N];
			double[] b1 = new double[N];
			double[] b2 = new double[N];
			double[] s = new double[resamples];

			for (int i = 0; i < resamples; i++) {
				for (int j = 0; j < N; j++) {
					int index = PRNG.nextInt(N);

					b0[j] = a0[index];
					b1[j] = a1[index];
					b2[j] = a2[index];
				}

				s[i] = computeFirstOrder(b0, b1, b2);
			}

			double ss = StatUtils.sum(s) / resamples;
			double sss = 0.0;
			
			for (int i = 0; i < resamples; i++) {
				sss += Math.pow(s[i] - ss, 2.0);
			}

			return 1.96 * Math.sqrt(sss / (resamples - 1));
		}

		/**
		 * Returns the total-order sensitivity of the i-th parameter.  Note how the contents of the array {@code a1}
		 * specify the parameter being analyzed.
		 * 
		 * @param a0 the output from the first independent samples
		 * @param a1 the output from the samples produced by swapping the i-th parameter in the first independent samples
		 *        with the i-th parameter from the second independent samples
		 * @param a2 the output from the second independent samples
		 * @return the total-order sensitivity of the i-th parameter
		 */
		private double computeTotalOrder(double[] a0, double[] a1, double[] a2) {
			double c = 0.0;
			
			for (int i = 0; i < N; i++) {
				c += a0[i];
			}
			
			c /= N;

			double tmp1 = 0.0;
			double tmp2 = 0.0;
			double tmp3 = 0.0;

			for (int i = 0; i < N; i++) {
				tmp1 += (a0[i] - c) * (a0[i] - c);
				tmp2 += (a0[i] - c) * (a1[i] - c);
				tmp3 += (a0[i] - c);
			}

			double EY2 = Math.pow(tmp3 / N, 2.0);
			double V = (tmp1 / (N - 1)) - EY2;
			double U = tmp2 / (N - 1);

			return 1.0 - ((U - EY2) / V);
		}

		/**
		 * Returns the total-order confidence interval of the i-th parameter.  The arguments to this method mirror the
		 * arguments to {@link #computeTotalOrder}.
		 * 
		 * @param a0 the output from the first independent samples
		 * @param a1 the output from the samples produced by swapping the i-th parameter in the first independent samples
		 *        with the i-th parameter from the second independent samples
		 * @param a2 the output from the second independent samples
		 * @return the total-order confidence interval of the i-th parameter
		 */
		private double computeTotalOrderConfidence(double[] a0, double[] a1, double[] a2) {
			double[] b0 = new double[N];
			double[] b1 = new double[N];
			double[] b2 = new double[N];
			double[] s = new double[resamples];

			for (int i = 0; i < resamples; i++) {
				for (int j = 0; j < N; j++) {
					int index = PRNG.nextInt(N);

					b0[j] = a0[index];
					b1[j] = a1[index];
					b2[j] = a2[index];
				}

				s[i] = computeTotalOrder(b0, b1, b2);
			}

			double ss = StatUtils.sum(s) / resamples;
			double sss = 0.0;
			
			for (int i = 0; i < resamples; i++) {
				sss += Math.pow(s[i] - ss, 2.0);
			}

			return 1.96 * Math.sqrt(sss / (resamples - 1));
		}

		/**
		 * Returns the second-order sensitivity of the i-th and j-th parameters.  Note how the contents of the arrays
		 * {@code a1}, {@code a2}, and {@code a3} specify the two parameters being analyzed.
		 * 
		 * @param a0 the output from the first independent samples
		 * @param a1 the output from the samples produced by swapping the i-th parameter in the second independent samples
		 *        with the i-th parameter from the first independent samples
		 * @param a2 the output from the samples produced by swapping the j-th parameter in the first independent samples
		 *        with the j-th parameter from the second independent samples
		 * @param a3 the output from the samples produced by swapping the i-th parameter in the first independent samples
		 *        with the i-th parameter from the second independent samples
		 * @param a4 the output from the second independent samples
		 * @return the second-order sensitivity of the i-th and j-th parameters
		 */
		private double computeSecondOrder(double[] a0, double[] a1, double[] a2, double[] a3, double[] a4) {
			double c = 0.0;
			
			for (int i = 0; i < N; i++) {
				c += a0[i];
			}
			
			c /= N;

			double EY = 0.0;
			double EY2 = 0.0;
			double tmp1 = 0.0;
			double tmp2 = 0.0;
			double tmp3 = 0.0;
			double tmp4 = 0.0;
			double tmp5 = 0.0;

			for (int i = 0; i < N; i++) {
				EY += (a0[i] - c) * (a4[i] - c);
				EY2 += (a1[i] - c) * (a3[i] - c);
				tmp1 += (a1[i] - c) * (a1[i] - c);
				tmp2 += (a1[i] - c);
				tmp3 += (a1[i] - c) * (a2[i] - c);
				tmp4 += (a2[i] - c) * (a4[i] - c);
				tmp5 += (a3[i] - c) * (a4[i] - c);
			}

			EY /= N;
			EY2 /= N;

			double V = (tmp1 / (N - 1)) - Math.pow(tmp2 / N, 2.0);
			double Vij = (tmp3 / (N - 1)) - EY2;
			double Vi = (tmp4 / (N - 1)) - EY;
			double Vj = (tmp5 / (N - 1)) - EY2;

			return (Vij - Vi - Vj) / V;
		}

		/**
		 * Returns the second-order confidence interval of the i-th and j-th parameters.  The arguments to this method
		 * mirror the arguments to {@link #computeSecondOrder}.
		 * 
		 * @param a0 the output from the first independent samples
		 * @param a1 the output from the samples produced by swapping the i-th parameter in the second independent samples
		 *        with the i-th parameter from the first independent samples
		 * @param a2 the output from the samples produced by swapping the j-th parameter in the first independent samples
		 *        with the j-th parameter from the second independent samples
		 * @param a3 the output from the samples produced by swapping the i-th parameter in the first independent samples
		 *        with the i-th parameter from the second independent samples
		 * @param a4 the output from the second independent samples
		 * @return the second-order confidence interval of the i-th and j-th parameters
		 */
		private double computeSecondOrderConfidence(double[] a0, double[] a1, double[] a2, double[] a3, double[] a4) {
			double[] b0 = new double[N];
			double[] b1 = new double[N];
			double[] b2 = new double[N];
			double[] b3 = new double[N];
			double[] b4 = new double[N];
			double[] s = new double[resamples];

			for (int i = 0; i < resamples; i++) {
				for (int j = 0; j < N; j++) {
					int index = PRNG.nextInt(N);

					b0[j] = a0[index];
					b1[j] = a1[index];
					b2[j] = a2[index];
					b3[j] = a3[index];
					b4[j] = a4[index];
				}

				s[i] = computeSecondOrder(b0, b1, b2, b3, b4);
			}

			double ss = StatUtils.sum(s) / resamples;
			double sss = 0.0;
			
			for (int i = 0; i < resamples; i++) {
				sss += Math.pow(s[i] - ss, 2.0);
			}

			return 1.96 * Math.sqrt(sss / (resamples - 1));
		}
	}

}
