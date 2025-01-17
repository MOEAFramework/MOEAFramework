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
package org.moeaframework.problem;

import java.io.IOException;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.core.Solution;
import org.moeaframework.core.constraint.Equal;
import org.moeaframework.core.objective.Minimize;
import org.moeaframework.core.variable.RealVariable;

@SuppressWarnings("resource")
public class ProblemStubTest {
	
	@Test
	public void testUndefined() throws IOException {
		ProblemStub stub = new ProblemStub("foo", 1, 2, 1);
		
		Solution solution = stub.newSolution();
		Assert.assertEquals(1, solution.getNumberOfVariables());
		Assert.assertEquals(2, solution.getNumberOfObjectives());
		Assert.assertEquals(1, solution.getNumberOfConstraints());
		
		Assert.assertTrue(Double.isNaN(solution.getObjectiveValue(0)));
		Assert.assertTrue(Double.isNaN(solution.getObjectiveValue(1)));
		Assert.assertTrue(Double.isNaN(solution.getConstraintValue(0)));
		
		Assert.assertNotNull(solution.copy());
		
		Assert.assertThrows(UnsupportedOperationException.class, () -> solution.getVariable(0).randomize());
		Assert.assertThrows(UnsupportedOperationException.class, () -> solution.getObjective(0).getCanonicalValue());
		Assert.assertThrows(UnsupportedOperationException.class, () -> stub.evaluate(solution));
	}
	
	@Test
	public void testWithDefinitions() throws IOException {
		ProblemStub stub = new ProblemStub("foo", 1, 1, 1);
		stub.setVariableDefinition(0, new RealVariable(0.0, 1.0));
		stub.setObjectiveDefinition(0, new Minimize());
		stub.setConstraintDefinition(0, new Equal(0.0));
		
		Solution solution = stub.newSolution();
		Assert.assertEquals(1, solution.getNumberOfVariables());
		Assert.assertEquals(1, solution.getNumberOfObjectives());
		Assert.assertEquals(1, solution.getNumberOfConstraints());
		
		Assert.assertTrue(solution.getVariable(0) instanceof RealVariable);
		Assert.assertTrue(solution.getObjective(0) instanceof Minimize);
		Assert.assertTrue(solution.getConstraint(0) instanceof Equal);
		
		Assert.assertTrue(Double.isNaN(RealVariable.getReal(solution.getVariable(0))));
		Assert.assertTrue(Double.isNaN(solution.getObjectiveValue(0)));
		Assert.assertTrue(Double.isNaN(solution.getConstraintValue(0)));
		
		Assert.assertNotNull(solution.copy());
		Assert.assertThrows(UnsupportedOperationException.class, () -> stub.evaluate(solution));
	}

}
