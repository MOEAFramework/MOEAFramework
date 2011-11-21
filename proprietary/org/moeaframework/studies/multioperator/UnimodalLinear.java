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

public class UnimodalLinear extends MultioperatorTestProblem {
	
	private final double x0;
	
	public UnimodalLinear() {
		this(0.2);
	}
	
	public UnimodalLinear(double x0) {
		super();
		this.x0 = x0;
	}

	@Override
	public void evaluate(Solution solution) {
		double x = ((RealVariable)solution.getVariable(0)).getValue();
		solution.setObjective(0, Math.abs(x - x0));
	}

	@Override
	public String getName() {
		return "UnimodalLinear";
	}

	@Override
	public boolean isOptimum(Solution solution, double epsilon) {
		double x = ((RealVariable)solution.getVariable(0)).getValue();
		return Math.abs(x - x0) < epsilon;
	}	

}
