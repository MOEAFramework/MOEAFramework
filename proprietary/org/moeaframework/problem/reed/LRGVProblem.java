/*
 * Provided by Joe Kasprzyk on 09 Sept 2011.
 */
package org.moeaframework.problem.reed;

import java.io.IOException;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.ExternalProblem;

public class LRGVProblem extends ExternalProblem {
	
	public LRGVProblem() throws IOException {
	    super("./lrgv_sampler", "awr", "0");
	}

	@Override
	public String getName() {
		return "LRGV";
	}

	@Override
	public int getNumberOfVariables() {
		return 8;
	}

	@Override
	public int getNumberOfObjectives() {
		return 5;
	}

	@Override
	public int getNumberOfConstraints() {
		return 3;
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(8, 5, 3);
		solution.setVariable(0, new RealVariable(0.0, 1.0));
		solution.setVariable(1, new RealVariable(0.0, 1.0));
		solution.setVariable(2, new RealVariable(0.1, 1.0));
		solution.setVariable(3, new RealVariable(0.1, 0.4));
		solution.setVariable(4, new RealVariable(0.0, 3.0));
		solution.setVariable(5, new RealVariable(0.0, 3.0));
		solution.setVariable(6, new RealVariable(0.0, 3.0));
		solution.setVariable(7, new RealVariable(0.0, 3.0));
		return solution;
	}

	@Override
	public void evaluate(Solution solution) {
		double precision = 100;

		//get variables
		RealVariable rv2 = (RealVariable)solution.getVariable(2); 
		RealVariable rv3 = (RealVariable)solution.getVariable(3); 
		RealVariable rv4 = (RealVariable)solution.getVariable(4); 
		RealVariable rv5 = (RealVariable)solution.getVariable(5); 
		RealVariable rv6 = (RealVariable)solution.getVariable(6); 
		RealVariable rv7 = (RealVariable)solution.getVariable(7); 

		double v2 = rv2.getValue();
		double v3 = rv3.getValue();
		double v4 = rv4.getValue();
		double v5 = rv5.getValue();
		double v6 = rv6.getValue();
		double v7 = rv7.getValue();

		//perform transformations

		//delta_o_high discretization
		if (v2 < 0.05) v2 = 0;
		else if (v2 < 0.15) v2 = 0.1;
		else if (v2 < 0.25) v2 = 0.2;
		else if (v2 < 0.35) v2 = 0.3;
		else if (v2 < 0.45) v2 = 0.4;
		else if (v2 < 0.55) v2 = 0.5;
		else if (v2 < 0.65) v2 = 0.6;
		else if (v2 < 0.75) v2 = 0.7;
		else if (v2 < 0.85) v2 = 0.8;
		else if (v2 < 0.95) v2 = 0.9;
		else v2 = 1.0;

		//xi discretization
		if (v3 < 0.125) v3 = 0.10;
		else if (v3 < 0.175) v3 = 0.15;
		else if (v3 < 0.225) v3 = 0.20;
		else if (v3 < 0.275) v3 = 0.25;
		else if (v3 < 0.325) v3 = 0.30;
		else if (v3 < 0.375) v3 = 0.35;
		else v3 = 0.40;		

		//alpha/beta discretization
		v4 = Math.floor(v4 * precision +.5)/precision;
		v5 = Math.floor(v5 * precision +.5)/precision;
		v6 = Math.floor(v6 * precision +.5)/precision;
		v7 = Math.floor(v7 * precision +.5)/precision;

		//alpha/beta 3.0 constraint
		if (v4 + v5 > 3.0) v5 = 3.0 - v4; //transform
		if (v6 + v7 > 3.0) v7 = 3.0 - v6;
		
		//set values
		rv2.setValue(v2);
		rv3.setValue(v3);		
		rv4.setValue(v4);
		rv5.setValue(v5);
		rv6.setValue(v6);
		rv7.setValue(v7);
		
		super.evaluate(solution);
	}

}
