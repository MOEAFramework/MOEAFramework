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

public class Multimodal extends MultioperatorTestProblem {
	
	public Multimodal() {
		super();
	}

	@Override
	public void evaluate(Solution solution) {
		double x = ((RealVariable)solution.getVariable(0)).getValue();
		double y = ((RealVariable)solution.getVariable(1)).getValue();
		solution.setObjective(0, -Math.exp(-Math.pow(x-0.8, 2.0)/0.1) - 
				1.2*Math.exp(-Math.pow(x-0.2, 2.0)/0.005) + 
						Math.pow(y - 0.5, 2.0));
	}

	@Override
	public boolean isOptimum(Solution solution, double epsilon) {
		double x = ((RealVariable)solution.getVariable(0)).getValue();
		double y = ((RealVariable)solution.getVariable(1)).getValue();
		return Math.sqrt(Math.pow(x - 0.2, 2.0) + Math.pow(y - 0.5, 2.0)) < epsilon;
	}	

}
