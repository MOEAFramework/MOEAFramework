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
package org.moeaframework.problem.ZCAT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.configuration.Validate;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;
import org.moeaframework.problem.AnalyticalProblem;

/**
 * Abstract class for implementing ZCAT test problems.
 */
public abstract class ZCAT extends AbstractProblem implements AnalyticalProblem {
	
	/**
	 * The precision when considering if two floating-point numbers are equal.  This differs from {@code Settings.EPS}
	 * to match the original ZCAT source code. 
	 */
	public static final double EPSILON = Math.ulp(1.0);
	
	/**
	 * The difficulty level between {@code 1} and {@code 6}.
	 */
	protected final int level;
	
	/**
	 * {@code true} if bias is applied; {@code false} otherwise.
	 */
	protected final boolean bias;
	
	/**
	 * {@code true} if imbalance is applied; {@code false} otherwise.
	 */
	protected final boolean imbalance;
	
	/**
	 * The shape function for the Pareto front.
	 */
	protected final PFShapeFunction F;
	
	/**
	 * The shape function for the Pareto set.
	 */
	protected final PSShapeFunction G;
	
	/**
	 * Constructs a new ZCAT problem.
	 * 
	 * @param numberOfObjectives the number of objectives
	 * @param level the difficulty level between {@code 1} and {@code 6}
	 * @param bias {@code true} if bias is applied to the decision variables; {@code false} otherwise
	 * @param imbalance {@code true} if imbalance is applied; {@code false} otherwise.
	 * @param F the Pareto front shape function
	 * @param G the Pareto set shape function
	 */
	public ZCAT(int numberOfObjectives, int level, boolean bias, boolean imbalance,
			PFShapeFunction F, PSShapeFunction G) {
		this(10 * numberOfObjectives, numberOfObjectives, level, bias, imbalance, F, G);
	}
	
	/**
	 * Constructs a new ZCAT problem.
	 * 
	 * @param numberOfVariables the number of decision variables
	 * @param numberOfObjectives the number of objectives
	 * @param level the difficulty level between {@code 1} and {@code 6}
	 * @param bias {@code true} if bias is applied to the decision variables; {@code false} otherwise
	 * @param imbalance {@code true} if imbalance is applied; {@code false} otherwise.
	 * @param F the Pareto front shape function
	 * @param G the Pareto set shape function
	 */
	public ZCAT(int numberOfVariables, int numberOfObjectives, int level, boolean bias, boolean imbalance,
			PFShapeFunction F, PSShapeFunction G) {
		super(numberOfVariables, numberOfObjectives, 0);
		this.level = level;
		this.bias = bias;
		this.imbalance = imbalance;
		this.F = F;
		this.G = G;
		
		Validate.greaterThanOrEqual("numberOfObjectives", 2, numberOfObjectives);
		Validate.inclusiveBetween("level", 1, 6, level);
		Validate.notNull("F", F);
		Validate.notNull("G", G);
	}
	
	@Override
	public String getName() {
		return getClass().getSimpleName() + "_" + numberOfObjectives;
	}

	@Override
	public void evaluate(Solution solution) {	
		double[] x = EncodingUtils.getReal(solution);
		
		double[] y = getY(x);                // Normalization
		double[] alpha = getAlpha(y, F);     // Position
		double[] beta = getBeta(y, G);       // Distance
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
	 * differ for certain degenerate (ZCAT14 - ZCAT16) or hybrid (ZCAT19 - ZCAT20) problems.
	 * 
	 * @param y the normalized decision variables
	 * @return the dimension of the Pareto front / Pareto set
	 */
	public int getDimension(double[] y) {
		return numberOfObjectives - 1;
	}
	
	/**
	 * Computes the position of the solution on the Pareto front.
	 * 
	 * @param y the normalized decision variable values
	 * @param F the function defining the Pareto front shape
	 * @return the position values
	 */
	protected double[] getAlpha(double[] y, PFShapeFunction F) {
		double[] a = F.apply(y, numberOfObjectives);
		
		for (int i = 0; i < numberOfObjectives; i++) {
			a[i] = Math.pow(i + 1, 2.0) * a[i];
		}
		
		return a;
	}
	
	/**
	 * Computes the distance of the solution to the Pareto front.
	 * 
	 * @param y the normalized decision variable values
	 * @param G the function defining the Pareto set shape
	 * @return the distance values
	 */
	protected double[] getBeta(double[] y, PSShapeFunction G) {
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
	
	/**
	 * Computes the {@code J} vector.
	 * 
	 * @param i the objective index
	 * @param w the {@code w} vector
	 * @return the intermediate vector {@code J}
	 */
	protected double[] getJ(int i, double[] w) {
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
	
	/**
	 * Applies bias to the {@code z} vector when enabled.
	 * 
	 * @param z the {@code z} vector
	 * @param m the dimension of the Pareto front / set
	 * @return the result of biasing the {@code z} vector
	 */
	protected double[] getW(double[] z, int m) {
		double[] w = new double[numberOfVariables - m];
		
		for (int i = 0; i < numberOfVariables - m; i++) {
			w[i] = bias ? Zbias(z[i]) : z[i];
		}
		
		return w;
	}
	
	/**
	 * Normalizes the decision variables.
	 * 
	 * @param x the raw, unnormalized decision variable values
	 * @return the normalized decision variables values scaled between {@code [0, 1]}
	 */
	protected double[] getY(double[] x) {
		double[] y = new double[numberOfVariables];
		
		for (int i = 0; i < numberOfVariables; i++) {
			double lb = -0.5 * (i + 1);
			double ub = 0.5 * (i + 1);
			
			y[i] = (x[i] - lb) / (ub - lb);
		}

		return y;
	}
	
	/**
	 * Applies the Pareto shape function to the decision variables.
	 * 
	 * @param y the normalized decision variables
	 * @param m the dimension of the Pareto front / set
	 * @param G the Pareto shape function
	 * @return the resulting vector {@code z}
	 */
	protected double[] getZ(double[] y, int m, PSShapeFunction G) {
		double[] g = G.apply(y, m, numberOfVariables);
		double[] z = new double[numberOfVariables - m];
		
		for (int i = m; i < numberOfVariables; i++) {
			double diff = y[i] - g[i - m];
			z[i - m] = Math.abs(diff) < EPSILON ? 0.0 : diff;
		}

		return z;
	}
	
	/**
	 * Calculates the bias for a single element of the {@code z} vector.
	 * 
	 * @param z the single element of the {@code z} vector
	 * @return the value with bias applied
	 */
	protected double Zbias(double z) {
	    return Math.pow(Math.abs(z), 0.05);
	}
	
	/**
	 * Applies imbalance / the difficulty level to the intermediate vector {@code w}.
	 * 
	 * @param w the intermediate vector {@code w}
	 * @param i the index
	 * @return the result of applying the imbalance / difficulty level function
	 */
	protected double evaluateZ(double[] w, int i) {
		if (imbalance) {
			if (i % 2 == 0) {
				return BasisFunction.Z4.apply(w);
			} else {
				return BasisFunction.Z1.apply(w);
			}
		}

		return switch (level) {
			case 1 -> BasisFunction.Z1.apply(w);
			case 2 -> BasisFunction.Z2.apply(w);
			case 3 -> BasisFunction.Z3.apply(w);
			case 4 -> BasisFunction.Z4.apply(w);
			case 5 -> BasisFunction.Z5.apply(w);
			case 6 -> BasisFunction.Z6.apply(w);
			default -> BasisFunction.Z1.apply(w);
		};
	}
	
	/**
	 * Evaluates the final objective values by combining the position and distance values.
	 * 
	 * @param alpha the position values
	 * @param beta the distance values
	 * @return the final objective values
	 */
	protected double[] evaluateF(double[] alpha, double[] beta) {
		double[] f = new double[numberOfObjectives];
		
		for (int i = 0; i < numberOfObjectives; i++) {
			f[i] = alpha[i] + beta[i];
		}
		
		return f;
	}
	
	@Override
	public Solution generate() {
		Solution solution = newSolution();
		
		double y0 = PRNG.nextDouble();
		int m = getDimension(new double[] { y0 });
		int k = getNumberOfSegments(F);
		
		if (k > 0) {
			List<double[]> segments = getSegments(k);
			
			double[] segment = PRNG.nextItem(segments); // pick random segment
			y0 = PRNG.nextDouble(segment[0], segment[1]);
		}
		
		double[] y = new double[m];
		y[0] = y0;
		
	    for (int i = 1; i < m; i++) {
	        y[i] = PRNG.nextDouble();
	    }
	    
	    double[] g = G.apply(y, m, numberOfVariables);
	    
	    for (int i = 0; i < m; i++) {
	    	setDecisionVariable(y[i], i, solution);
	    }
	    
	    for (int i = m; i < numberOfVariables; i++) {
	    	setDecisionVariable(g[i - m], i, solution);
	    }
	    
	    evaluate(solution);
		
		return solution;
	}
	
	/**
	 * Returns the number of segments in the Pareto front for the given shape function.  This is controlled
	 * by the {@code k} constant defined in each function.
	 * 
	 * @param F the PF shape function
	 * @return the number of segments
	 */
	private int getNumberOfSegments(PFShapeFunction F) {
		if (F == PFShapeFunction.F11) {
			return 4;
		} else if (F == PFShapeFunction.F12 || F == PFShapeFunction.F13 || F == PFShapeFunction.F15) {
			return 3;
		} else if (F == PFShapeFunction.F16) {
			return 5;
		} else {
			return 0;
		}
	}
	
	/**
	 * Defines the lower and upper bounds of {@code y[0]} for each segment.
	 * 
	 * @param k the number of segments in the Pareto front
	 * @return the lower and upper bounds of each segment
	 */
	private List<double[]> getSegments(int k) {
		// These segments can be found at https://github.com/evo-mx/ZCAT/tree/main/src/seg
		List<double[]> segments = new ArrayList<double[]>();
		
		if (k == 3) {
			segments.add(new double[] { 0.0,               0.243933581942011 });
			segments.add(new double[] {	0.417357435020449, 0.643933581942011 });
			segments.add(new double[] {	0.817357435020257, 1.0               });
		} else if (k == 4) {
			segments.add(new double[] { 0.0,               0.174238272815722 });
			segments.add(new double[] { 0.298112453586178, 0.459952558530008 });
			segments.add(new double[] { 0.583826743124699, 0.745666844244294 });
			segments.add(new double[] { 0.869541025014755, 1.0               });
		} else if (k == 5) {
			segments.add(new double[] { 0.0,               0.135518656634451 });
			segments.add(new double[] { 0.231865241678485, 0.357740878856673 });
			segments.add(new double[] { 0.454087466874766, 0.579963101078895 });
			segments.add(new double[] { 0.676309689096988, 0.802185323301117 });
			segments.add(new double[] { 0.898531908344865, 1.0               });
		} else {
			throw new IllegalArgumentException("getSegments not defined for k=" + k);
		}
		
		return segments;
	}
	
	/**
	 * Sets the decision variable of the solution by scaling the value {@code y} within the bounds.
	 * 
	 * @param y the normalized value
	 * @param i the index of the decision variable
	 * @param solution the solution
	 */
	private void setDecisionVariable(double y, int i, Solution solution) {
		RealVariable variable = (RealVariable)solution.getVariable(i);
		variable.setValue(y * (variable.getUpperBound() - variable.getLowerBound()) + variable.getLowerBound());
	}
	
}
