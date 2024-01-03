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
package org.moeaframework.problem.LSMOP;

/**
 * Shape functions used by the LSMOP test problem suite.
 */
public interface ShapeFunction {
	
	/**
	 * Computes the shape function.
	 * 
	 * @param x the decision variables
	 * @return the computed value
	 */
	public double apply(double[] x);
	
	/**
	 * Sphere function (unimodal, separable).
	 */
	public static final ShapeFunction Sphere = x -> {
		double result = 0.0;
			
		for (int i = 0; i < x.length; i++) {
			result += x[i] * x[i];
		}
			
		return result;
	};
	
	/**
	 * Rosenbrock function (multimodal, non-separable).
	 */
	public static final ShapeFunction Rosenbrock = x -> {
		double s1 = 0.0;
		double s2 = 0.0;
		double tmp;
		
		for (int i = 0; i < x.length-1; i++) {
			tmp = (x[i]*x[i] - x[i+1]);
			s1 += tmp*tmp;
			tmp = (x[i] - 1.0);
			s2 += tmp*tmp;
		}
		
		return 100.0*s1 + s2;
	};
	
	/**
	 * Schwefel function (unimodal, non-separable).
	 */
	public static final ShapeFunction Schwefel = x -> {
		double result = Math.abs(x[0]);
		
		for (int i = 1; i < x.length; i++) {
			result = Math.max(result, Math.abs(x[i]));
		}
		
		return result;
	};
	
	/**
	 * Rastrigin function (multimodal, separable).
	 */
	public static final ShapeFunction Rastrigin = x -> {
		double tmp1 = 0.0;
		double tmp2 = 0.0;
		
		for (int i = 0; i < x.length; i++) {
			tmp1 += Math.cos(2.0 * Math.PI * x[i]);
			tmp2 += x[i] * x[i];
		}
		
		return 10.0 * (x.length - tmp1) + tmp2;
	};
	
	/**
	 * Griewank function (multimodal, non-separable).
	 */
	public static final ShapeFunction Griewank = x -> {
        double tmp1 = 0.0;
        double tmp2 = 1.0;
        
        for (int i = 0; i < x.length; i++) {
        	tmp1 += x[i] * x[i];
        	tmp2 *= Math.cos(x[i] / Math.sqrt(i+1));
        }
        
        return (tmp1 / 4000.0) - tmp2 + 1.0;
	};
	
	/**
	 * Ackley function (multimodal, separable).
	 */
	public static final ShapeFunction Ackley = x -> {
		double tmp1 = 0.0;
		double tmp2 = 0.0;
		
		for (int i = 0; i < x.length; i++) {
			tmp1 += x[i] * x[i];
			tmp2 += Math.cos(2.0 * Math.PI * x[i]);
		}
		
		tmp1 = Math.exp(-0.2 * Math.sqrt(tmp1 / x.length));
		tmp2 = Math.exp(tmp2 / x.length);
		
		return -20.0*tmp1 - tmp2 + 20.0 + Math.E;
	};

}
