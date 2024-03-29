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
package org.moeaframework.mock;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import org.apache.commons.lang3.reflect.TypeUtils;
import org.junit.Assert;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.variable.BinaryIntegerVariable;
import org.moeaframework.core.variable.BinaryVariable;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.Permutation;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.core.variable.Subset;

/**
 * Utility for mocking or building solutions for tests.  This can be used as a drop-in replacement for {@link Solution}
 * or use {@link #build()} to construct an actual {@code Solution} instance.
 * <p>
 * Use the {@code with*} methods to dynamically construct the solution.  If based on an existing problem or solution,
 * checks are performed to ensure the mock is valid.
 */
public class MockSolution extends Solution {

	private static final long serialVersionUID = 1527466724950317738L;

	private Optional<Problem> problem;

	private Optional<Solution> solution;

	private Optional<Variable[]> variables;

	private Optional<double[]> objectives;

	private Optional<double[]> constraints;

	private boolean readOnly;

	private MockSolution() {
		this(null);
	}

	private MockSolution(Solution solution) {
		super(0, 0, 0);
		this.solution = Optional.ofNullable(solution);

		problem = Optional.empty();
		variables = Optional.empty();
		objectives = Optional.empty();
		constraints = Optional.empty();
		readOnly = false;
	}

	public static MockSolution of(Solution solution) {
		Variable[] variables = new Variable[solution.getNumberOfVariables()];

		for (int i = 0; i < solution.getNumberOfVariables(); i++) {
			variables[i] = solution.getVariable(i).copy();
		}

		MockSolution mock = new MockSolution(solution);
		mock.withVariables(variables);
		mock.withObjectives(solution.getObjectives());
		mock.withConstraints(solution.getConstraints());
		mock.addAttributes(solution.getAttributes());
		return mock;
	}

	public static MockSolution of(Problem problem) {
		MockSolution result = MockSolution.of(problem.newSolution());
		result.problem = Optional.of(problem);
		return result;
	}

	public static MockSolution of() {
		return new MockSolution();
	}

	public MockSolution readOnly() {
		this.readOnly = true;
		return this;
	}

	public MockSolution at(double... variables) {
		throwIfReadOnly();
		throwIfNoSolutionOrVariables();

		EncodingUtils.setReal(this, variables);
		return this;
	}

	public MockSolution fill(double value) {
		throwIfReadOnly();
		throwIfNoSolutionOrVariables();

		for (int i = 0; i < solution.get().getNumberOfVariables(); i++) {
			EncodingUtils.setReal(variables.get()[i], value);
		}

		return this;
	}

	public MockSolution atLowerBounds() {
		throwIfReadOnly();
		throwIfNoSolutionOrVariables();
		
		for (int i = 0; i < variables.get().length; i++) {
			Variable variable = variables.get()[i];
			
			if (variable instanceof RealVariable realVariable) {
				realVariable.setValue(realVariable.getLowerBound());
			} else if (variable instanceof BinaryIntegerVariable binaryIntVariable) {
				binaryIntVariable.setValue(binaryIntVariable.getLowerBound());
			} else if (variable instanceof BinaryVariable binaryVariable) {
				for (int j = 0; j < binaryVariable.getNumberOfBits(); j++) {
					binaryVariable.set(j, false);
				}
			} else if (variable instanceof Permutation permutation) {
				permutation.fromArray(IntStream.range(0, permutation.size()).toArray());
			} else if (variable instanceof Subset subset) {
				subset.fromArray(IntStream.range(0, subset.getL()).toArray());
			} else {
				Assert.fail(getLastMethodName() + " not supported with type " + variable.getClass().getSimpleName());
			}
		}
		
		return this;
	}
	
	public MockSolution atUpperBounds() {
		throwIfReadOnly();
		throwIfNoSolutionOrVariables();
		
		for (int i = 0; i < variables.get().length; i++) {
			Variable variable = variables.get()[i];
			
			if (variable instanceof RealVariable realVariable) {
				realVariable.setValue(realVariable.getUpperBound());
			} else if (variable instanceof BinaryIntegerVariable binaryIntVariable) {
				binaryIntVariable.setValue(binaryIntVariable.getUpperBound());
			} else if (variable instanceof BinaryVariable binaryVariable) {
				for (int j = 0; j < binaryVariable.getNumberOfBits(); j++) {
					binaryVariable.set(j, true);
				}
			} else if (variable instanceof Permutation permutation) {
				permutation.fromArray(IntStream.range(0, permutation.size()).map(x -> permutation.size() - x - 1).toArray());
			} else if (variable instanceof Subset subset) {
				subset.fromArray(IntStream.range(0, subset.getU()).map(x -> subset.getN() - x - 1).toArray());
			} else {
				Assert.fail(getLastMethodName() + " not supported with type " + variable.getClass().getSimpleName());
			}
		}
		
		return this;
	}

	public MockSolution withVariables(Variable... variables) {
		if (solution.isPresent()) {
			if (variables.length != solution.get().getNumberOfVariables()) {
				Assert.fail("Invalid mock usage: incorrect number of variables");
			}

			for (int i = 0; i < solution.get().getNumberOfVariables(); i++) {
				if (!TypeUtils.isAssignable(variables[i].getClass(), solution.get().getVariable(i).getClass())) {
					Assert.fail("Invalid mock usage: attempted to set " + variables[i].getClass().getSimpleName() + 
							" when expecting " + solution.get().getVariable(i).getClass());
				}
			}
		}

		this.variables = Optional.of(variables);
		return this;
	}

	public MockSolution withObjectives(double... objectives) {
		if (solution.isPresent() && objectives.length != solution.get().getNumberOfObjectives()) {
			Assert.fail("Invalid mock usage: incorrect number of objectives");
		}

		this.objectives = Optional.of(objectives);
		return this;
	}

	public MockSolution withConstraints(double... constraints) {
		if (solution.isPresent() && constraints.length != solution.get().getNumberOfConstraints()) {
			Assert.fail("Invalid mock usage: incorrect number of constraints");
		}

		this.constraints = Optional.of(constraints);
		return this;
	}

	public MockSolution withConstraintViolation() {
		if (solution.isPresent() && solution.get().getNumberOfConstraints() == 0) {
			Assert.fail("Invalid mock usage: can not have constraint violation with no constraints");
		}

		if (constraints.isEmpty()) {
			this.constraints = Optional.of(new double[solution.isPresent() ? solution.get().getNumberOfConstraints() : 1]);
		}

		this.constraints.get()[0] = 1.0;
		return this;
	}

	public MockSolution withAttribute(String key, Serializable value) {
		super.setAttribute(key, value);
		return this;
	}

	public MockSolution randomize() {
		throwIfReadOnly();
		throwIfVariablesNotSet();

		for (Variable variable : variables.get()) {
			variable.randomize();
		}

		return this;
	}

	public MockSolution evaluate() {
		throwIfProblemNotSet();
		return evaluate(problem.get());
	}

	public MockSolution evaluate(Problem problem) {
		throwIfReadOnly();
		problem.evaluate(this);
		
		if (Settings.isVerbose()) {
			display();
		}
		
		return this;
	}

	@Override
	public int getNumberOfObjectives() {
		return objectives.isEmpty() ? 0 : objectives.get().length;
	}

	@Override
	public int getNumberOfVariables() {
		return variables.isEmpty() ? 0 : variables.get().length;
	}

	@Override
	public int getNumberOfConstraints() {
		return constraints.isEmpty() ? 0 : constraints.get().length;
	}

	@Override
	public Variable getVariable(int index) {
		throwIfVariablesNotSet();
		return variables.get()[index];
	}

	@Override
	public void setVariable(int index, Variable variable) {
		throwIfReadOnly();
		throwIfVariablesNotSet();
		variables.get()[index] = variable;
	}

	public Variable[] getVariables() {
		throwIfVariablesNotSet();
		return variables.get();
	}

	@Override
	public double getObjective(int index) {
		throwIfObjectivesNotSet();
		return objectives.get()[index];
	}

	@Override
	public void setObjective(int index, double objective) {
		throwIfReadOnly();
		throwIfObjectivesNotSet();
		objectives.get()[index] = objective;
	}

	@Override
	public void setObjectives(double[] objectives) {
		throwIfReadOnly();
		this.objectives = Optional.of(objectives);
	}

	@Override
	public double[] getObjectives() {
		throwIfObjectivesNotSet();
		return objectives.get().clone();
	}

	@Override
	public double getConstraint(int index) {
		throwIfConstraintsNotSet();
		return constraints.get()[index];
	}

	@Override
	public void setConstraints(double[] constraints) {
		throwIfReadOnly();
		this.constraints = Optional.of(constraints);
	}

	@Override
	public double[] getConstraints() {
		throwIfConstraintsNotSet();
		return constraints.get().clone();
	}

	@Override
	public void setConstraint(int index, double constraint) {
		throwIfReadOnly();
		throwIfConstraintsNotSet();
		constraints.get()[index] = constraint;
	}

	@Override
	public Object setAttribute(String key, Serializable value) {
		throwIfReadOnly();
		return super.setAttribute(key, value);
	}

	@Override
	public Object removeAttribute(String key) {
		throwIfReadOnly();
		return super.removeAttribute(key);
	}

	@Override
	public void addAttributes(Map<String, Serializable> attributes) {
		throwIfReadOnly();
		super.addAttributes(attributes);
	}

	@Override
	public void clearAttributes() {
		throwIfReadOnly();
		super.clearAttributes();
	}

	@Override
	public Solution copy() {
		return build();
	}

	@Override
	public Solution deepCopy() {
		return build();
	}

	public Solution build() {
		Solution solution = new Solution(getNumberOfVariables(), getNumberOfObjectives(), getNumberOfConstraints());

		if (variables.isPresent()) {
			for (int i = 0; i < variables.get().length; i++) {
				solution.setVariable(i, variables.get()[i].copy());
			}
		}

		if (objectives.isPresent()) {
			solution.setObjectives(objectives.get());
		}

		if (constraints.isPresent()) {
			solution.setConstraints(constraints.get());
		}

		solution.addAttributes(getAttributes());

		return solution;
	}

	public void assertEquals(Solution other) {
		TestUtils.assertEquals(this, other);
	}

	private void throwIfProblemNotSet() {
		if (problem.isEmpty()) {
			Assert.fail("Invalid mock usage: must specify problem before calling " + getLastMethodName());
		}
	}

	private void throwIfVariablesNotSet() {
		if (variables.isEmpty()) {
			Assert.fail("Invalid mock usage: variables must be set before calling " + getLastMethodName());
		}
	}

	private void throwIfObjectivesNotSet() {
		if (objectives.isEmpty()) {
			Assert.fail("Invalid mock usage: objectives must be set before calling " + getLastMethodName());
		}
	}

	private void throwIfConstraintsNotSet() {
		if (constraints.isEmpty()) {
			Assert.fail("Invalid mock usage: constraints must be set before calling " + getLastMethodName());
		}
	}

	private void throwIfReadOnly() {
		if (readOnly) {
			Assert.fail("Invalid mock usage: attempt to modify a read-only mock by calling " + getLastMethodName());
		}
	}
	
	private void throwIfNoSolutionOrVariables() {
		if (solution.isEmpty() && this.variables.isEmpty()) {
			Assert.fail("Invalid mock usage: must specify solution or variables before calling " + getLastMethodName());
		}
	}

	private String getLastMethodName() {
		StackWalker walker = StackWalker.getInstance();
		Optional<String> methodName = walker.walk(frames -> frames
				.filter(f -> !f.getMethodName().equals("getLastMethodName") && !f.getMethodName().startsWith("throwIf"))
				.findFirst()
				.map(StackWalker.StackFrame::getMethodName));

		return methodName.isPresent() ? methodName.get() : "<UnknownCaller>";
	}

}
