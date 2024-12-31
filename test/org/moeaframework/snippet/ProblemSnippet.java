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
package org.moeaframework.snippet;

import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.io.output.CloseShieldOutputStream;
import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.analysis.io.ResultFileWriter;
import org.moeaframework.analysis.series.ResultEntry;
import org.moeaframework.core.Solution;
import org.moeaframework.core.constraint.Between;
import org.moeaframework.core.constraint.Equal;
import org.moeaframework.core.constraint.GreaterThan;
import org.moeaframework.core.constraint.GreaterThanOrEqual;
import org.moeaframework.core.constraint.LessThan;
import org.moeaframework.core.constraint.LessThanOrEqual;
import org.moeaframework.core.constraint.NotEqual;
import org.moeaframework.core.constraint.Outside;
import org.moeaframework.core.objective.Maximize;
import org.moeaframework.core.objective.Minimize;
import org.moeaframework.core.population.Population;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.mock.MockProblem;
import org.moeaframework.problem.CEC2009.UF1;
import org.moeaframework.problem.DTLZ.DTLZ2;
import org.moeaframework.problem.Problem;

public class ProblemSnippet {

	@Test
	public void UF1() {
		// begin-example: problem-no-args
		Problem problem = new UF1();
		// end-example: problem-no-args

		Assert.assertNotNull(problem);
	}

	@Test
	public void DTLZ2_3() {
		// begin-example: problem-with-args
		Problem problem = new DTLZ2(3);
		// end-example: problem-with-args

		Assert.assertNotNull(problem);
	}

	@Test
	public void BBOB2016() {
		// begin-example: bbob-2016-problem
		Problem problem = ProblemFactory.getInstance().getProblem("bbob-biobj(bbob_f1_i2_d5,bbob_f21_i2_d5)");
		// end-example: bbob-2016-problem

		Assert.assertNotNull(problem);
	}

	@Test
	public void objectives() {
		Solution solution = new Solution(0, 2);

		// begin-example: objective-definition
		solution.setObjective(0, new Minimize());
		solution.setObjective(1, new Maximize());
		// end-example: objective-definition

		// begin-example: objective-values
		// Get or set the objective value directly
		solution.getObjective(0).setValue(100.0);
		solution.getObjective(0).getValue();

		// Alternative way to get or set the objective value
		solution.setObjectiveValue(0, 100.0);
		solution.getObjectiveValue(0);
		// end-example: objective-values
	}

	@Test
	public void constraints() {
		Solution solution = new Solution(0, 0, 2);

		// begin-example: constraint-definition
		// Require the constraint to be less than (or equal) to a given value
		solution.setConstraint(0, LessThan.value(10.0));
		solution.setConstraint(1, LessThanOrEqual.to(10.0));

		// Require the constraint to be greater than (or equal) to a given value
		solution.setConstraint(0, GreaterThan.value(10.0));
		solution.setConstraint(1, GreaterThanOrEqual.to(10.0));

		// Require the constraint to be equal or not equal to a given value
		solution.setConstraint(0, Equal.to(10.0));
		solution.setConstraint(1, NotEqual.to(10.0));

		// Require the constraint to be between or outside some lower and upper bounds
		solution.setConstraint(0, Between.values(-10.0, 10.0));
		solution.setConstraint(1, Outside.values(-10.0, 10.0));
		// end-example: constraint-definition

		// begin-example: constraint-values
		// Get or set the constraint value directly
		solution.getConstraint(0).setValue(100.0);
		solution.getConstraint(0).getValue();

		// Alternative way to get or set the constraint value
		solution.setConstraintValue(0, 100.0);
		solution.getConstraintValue(0);
		// end-example: constraint-values

		// begin-example: constraint-violation
		// Checking if a single constraint is feasible or violated
		solution.getConstraint(0).isViolation();
		solution.getConstraint(0).getMagnitudeOfViolation();

		// Checking all constraints of a solution
		solution.isFeasible();
		solution.getSumOfConstraintViolations();
		// end-example: constraint-violation
	}

	@Test
	public void names() throws IOException {
		Solution solution = new Solution(2, 2, 1);

		// begin-example: custom-names
		solution.setVariable(0, new RealVariable("x", 0.0, 1.0));
		solution.setVariable(1, new RealVariable("y", 0.0, 1.0));
		
		solution.setObjective(0, new Minimize("minWeight"));
		solution.setObjective(1, new Maximize("maxProfit"));
		
		solution.setConstraint(0, new LessThanOrEqual("cost", 1000));
		// end-example: custom-names
				
		try (PrintWriter out = new PrintWriter(CloseShieldOutputStream.wrap(System.out));
				ResultFileWriter writer = new ResultFileWriter(MockProblem.of(solution), out)) {
			Population population = new Population();
			population.add(solution);
			
			writer.write(new ResultEntry(population));
		}
	}

}
