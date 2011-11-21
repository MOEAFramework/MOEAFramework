/* Copyright 2009-2011 David Hadka
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
package org.moeaframework.studies.multioperator;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;

public class Unimodal extends MultioperatorTestProblem {
	
	private final double x0;
	
	private final double y0;
	
	public Unimodal() {
		this(0.2, 0.5);
	}
	
	public Unimodal(double x0, double y0) {
		super();
		this.x0 = x0;
		this.y0 = y0;
	}

	@Override
	public void evaluate(Solution solution) {
		double x = ((RealVariable)solution.getVariable(0)).getValue();
		double y = ((RealVariable)solution.getVariable(1)).getValue();
		
		solution.setObjective(0, Math.pow(x - x0, 2.0) + Math.pow(y - y0, 2.0));
	}

	@Override
	public String getName() {
		return "Unimodal";
	}

	@Override
	public boolean isOptimum(Solution solution, double epsilon) {
		double x = ((RealVariable)solution.getVariable(0)).getValue();
		double y = ((RealVariable)solution.getVariable(1)).getValue();
		
		return Math.sqrt(Math.pow(x - x0, 2.0) + Math.pow(y - y0, 2.0)) < epsilon;
	}	

}
