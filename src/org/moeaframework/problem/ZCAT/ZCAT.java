package org.moeaframework.problem.ZCAT;

import java.util.Arrays;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.problem.AbstractProblem;

public abstract class ZCAT extends AbstractProblem {
	
	public static final double EPSILON = Math.ulp(1.0);
	
	private final int level;
	
	private final boolean bias;
	
	private final boolean imbalance;
		
	private final PFShapeFunction F;
	
	private final PSShapeFunction G;
	
	public ZCAT(int numberOfObjectives, int level, boolean bias, boolean imbalance,
			PFShapeFunction F, PSShapeFunction G) {
		this(10 * numberOfObjectives, numberOfObjectives, level, bias, imbalance, F, G);
	}
	
	public ZCAT(int numberOfVariables, int numberOfObjectives, int level, boolean bias, boolean imbalance,
			PFShapeFunction F, PSShapeFunction G) {
		super(numberOfVariables, numberOfObjectives, 0);
		this.level = level;
		this.bias = bias;
		this.imbalance = imbalance;
		this.F = F;
		this.G = G;
	}

	@Override
	public void evaluate(Solution solution) {	
		double[] x = EncodingUtils.getReal(solution);
		
		double[] y = getY(x); // Normalization
		double[] alpha = getAlpha(y, F); // Position
		double[] beta = getBeta(y, G); // Distance
		double[] f = evaluateF(alpha, beta); // Fitness values
		
		solution.setObjectives(f);
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(numberOfVariables, numberOfObjectives);
		
		for (int i = 0; i < numberOfVariables; i++) {
			solution.setVariable(i, EncodingUtils.newReal(-0.5 * (i + 1), 0.5 * (i + 1)));
		}
		
		return solution;
	}
	
	/**
	 * The dimension of the Pareto front / Pareto set.  This is typically {@code numberOfObjectives-1} but can
	 * differ for certain problems (ZCAT14 - ZCAT16, ZCAT19).
	 * 
	 * @return the dimension of the Pareto front / Pareto set
	 */
	public int getDimension(double[] y) {
		return numberOfObjectives - 1;
	}
	
	public double[] getAlpha(double[] y, PFShapeFunction F) {
		double[] a = F.apply(y, numberOfObjectives);
		
		for (int i = 0; i < numberOfObjectives; i++) {
			a[i] = Math.pow(i + 1, 2.0) * a[i];
		}
		
		return a;
	}
	
	public double[] getBeta(double[] y, PSShapeFunction G) {
		int m = getDimension(y);
		double[] z = getZ(y, m, G);
		double[] w = getW(z, m);
		double[] b = new double[numberOfObjectives];
		
		if (numberOfVariables == m) {
			Arrays.fill(b, 0.0);
		} else {
			for (int i = 0; i < numberOfObjectives; i++) {
				double[] J = getJ(i + 1, w);
				double Zvalue = evaluateZ(J, i + 1);
				b[i] = Math.pow(i + 1, 2.0) * Zvalue;
			}
		}
		
		return b;
	}
	
	public double[]	getJ(int i, double[] w) {
		double[] J = new double[0];
		int size = 0;
		
		for (int j = 1; j <= w.length; j++) {
			if ((j - i) % numberOfObjectives == 0) {
				size++;
				J = Arrays.copyOf(J, size);
				J[size - 1] = w[j - 1];
			}
		}
		
		if (size == 0) {
			return new double[] { w[0] };
		}
		
		return J;
	}
	
	public double[] getW(double[] z, int m) {
		double[] w = new double[numberOfVariables - m];
		
		for (int i = 0; i < numberOfVariables - m; i++) {
			w[i] = bias ? Zbias(z[i]) : z[i];
		}
		
		return w;
	}
	
	public double[] getY(double[] x) {
		double[] y = new double[numberOfVariables];
		
		for (int i = 0; i < numberOfVariables; i++) {
			double lb = -0.5 * (i + 1);
			double ub = 0.5 * (i + 1);
			
			y[i] = (x[i] - lb) / (ub - lb);
		}

		return y;
	}
	
	public double[] getZ(double[] y, int m, PSShapeFunction G) {
		double[] g = G.apply(y, m, numberOfVariables);
		double[] z = new double[numberOfVariables - m];
		
		for (int i = m; i < numberOfVariables; i++) {
			double diff = y[i] - g[i - m];
			z[i - m] = Math.abs(diff) < EPSILON ? 0.0 : diff;
		}

		return z;
	}
	
	public double Zbias(double z) {
	    return Math.pow(Math.abs(z), 0.05);
	}
	
	public double evaluateZ(double[] w, int i) {
		if (imbalance) {
			if (i % 2 == 0) {
				return BasisFunction.Z4.apply(w);
			} else {
				return BasisFunction.Z1.apply(w);
			}
		}

		switch (level) {
		case 1:
			return BasisFunction.Z1.apply(w);
		case 2:
			return BasisFunction.Z2.apply(w);
		case 3:
			return BasisFunction.Z3.apply(w);
		case 4:
			return BasisFunction.Z4.apply(w);
		case 5:
			return BasisFunction.Z5.apply(w);
		case 6:
			return BasisFunction.Z6.apply(w);
		default:
			return BasisFunction.Z1.apply(w);
		}
	}
	
	public double[] evaluateF(double[] alpha, double[] beta) {
		double[] f = new double[numberOfObjectives];
		
		for (int i = 0; i < numberOfObjectives; i++) {
			f[i] = alpha[i] + beta[i];
		}
		
		return f;
	}
	
}
