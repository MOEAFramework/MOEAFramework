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
package org.moeaframework.problem;

import org.moeaframework.core.Solution;
import org.moeaframework.core.constraint.AbstractConstraint;
import org.moeaframework.core.constraint.Constraint;
import org.moeaframework.core.objective.AbstractObjective;
import org.moeaframework.core.objective.NormalizedObjective;
import org.moeaframework.core.objective.Objective;
import org.moeaframework.core.variable.Variable;

/**
 * A problem "stub" that is typically used when processing data files when the specific problem type is unknown.
 * It can be configured with specific variable, objective, and constraint types, if known.  However, if specific types
 * are not provided, functionality may be limited and can result in exceptions.
 */
public class ProblemStub extends AbstractProblem {
	
	private final String name;
	
	private final Variable[] variableDefinitions;
	
	private final Objective[] objectiveDefinitions;
	
	private final Constraint[] constraintDefinitions;
	
	/**
	 * Constructs a problem stub with the specified number of objectives.
	 * 
	 * @param numberOfObjectives the number of objectives
	 */
	public ProblemStub(int numberOfObjectives) {
		this("", 0, numberOfObjectives, 0);
	}
	
	/**
	 * Constructs a problem stub with the specified number of variables, objectives, and constraints.
	 * 
	 * @param name the problem name
	 * @param numberOfVariables the number of variables
	 * @param numberOfObjectives the number of objectives
	 * @param numberOfConstraints the number of constraints
	 */
	public ProblemStub(String name, int numberOfVariables, int numberOfObjectives, int numberOfConstraints) {
		super(numberOfVariables, numberOfObjectives, numberOfConstraints);
		this.name = name;
		this.variableDefinitions = new Variable[numberOfVariables];
		this.objectiveDefinitions = new Objective[numberOfObjectives];
		this.constraintDefinitions = new Constraint[numberOfConstraints];
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the specific variable type, if known.
	 * 
	 * @param index the index
	 * @param variable the variable
	 */
	public void setVariableDefinition(int index, Variable variable) {
		variableDefinitions[index] = variable;
	}
	
	/**
	 * Sets the specific objective type, if known.
	 * 
	 * @param index the index
	 * @param objective the objective
	 */
	public void setObjectiveDefinition(int index, Objective objective) {
		objectiveDefinitions[index] = objective;
	}
	
	/**
	 * Sets the specific constraint type, if known.
	 * 
	 * @param index the index
	 * @param constraint the constraint
	 */
	public void setConstraintDefinition(int index, Constraint constraint) {
		constraintDefinitions[index] = constraint;
	}

	@Override
	public void evaluate(Solution solution) {
		throw new ProblemStubException();
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(numberOfVariables, numberOfObjectives, numberOfConstraints);
		
		for (int i = 0; i < numberOfVariables; i++) {
			solution.setVariable(i, variableDefinitions[i] == null ?
					new VariableStub() : variableDefinitions[i].copy());
		}
		
		for (int i = 0; i < numberOfObjectives; i++) {
			solution.setObjective(i, objectiveDefinitions[i] == null ?
					new ObjectiveStub() : objectiveDefinitions[i].copy());
		}
		
		for (int i = 0; i < numberOfConstraints; i++) {
			solution.setConstraint(i, constraintDefinitions[i] == null ?
					new ConstraintStub() : constraintDefinitions[i].copy());
		}
		
		return solution;
	}
	
	private static class VariableStub implements Variable {

		private static final long serialVersionUID = -2523538899581927161L;
		
		private String value;
		
		public VariableStub() {
			super();
		}
		
		public VariableStub(String value) {
			super();
			this.value = value;
		}

		@Override
		public VariableStub copy() {
			return new VariableStub(value);
		}

		@Override
		public void randomize() {
			throw new ProblemStubException();
		}

		@Override
		public String encode() {
			return value;
		}

		@Override
		public void decode(String value) {
			this.value = value;
		}
		
		@Override
		public String getDefinition() {
			throw new ProblemStubException();
		}

		@Override
		public String getName() {
			return null;
		}
		
	}
	
	private static class ObjectiveStub extends AbstractObjective {

		private static final long serialVersionUID = 2283955017555370139L;

		public ObjectiveStub() {
			super();
		}
		
		public ObjectiveStub(double value) {
			super();
			this.value = value;
		}

		@Override
		public double getCanonicalValue() {
			throw new ProblemStubException();
		}

		@Override
		public int compareTo(double value) {
			throw new ProblemStubException();
		}

		@Override
		public NormalizedObjective normalize(double minimum, double maximum) {
			throw new ProblemStubException();
		}

		@Override
		public int getEpsilonIndex(double epsilon) {
			throw new ProblemStubException();
		}

		@Override
		public double getEpsilonDistance(double epsilon) {
			throw new ProblemStubException();
		}

		@Override
		public double applyWeight(double weight) {
			throw new ProblemStubException();
		}

		@Override
		public double getIdealValue() {
			throw new ProblemStubException();
		}

		@Override
		public ObjectiveStub copy() {
			return new ObjectiveStub(value);
		}
		
		@Override
		public String getDefinition() {
			throw new ProblemStubException();
		}
		
	}
	
	private static class ConstraintStub extends AbstractConstraint {

		private static final long serialVersionUID = -4485446494128932190L;

		public ConstraintStub() {
			super();
		}
		
		public ConstraintStub(double value) {
			super();
			this.value = value;
		}

		@Override
		public double getMagnitudeOfViolation() {
			return 0;
		}

		@Override
		public ConstraintStub copy() {
			return new ConstraintStub(value);
		}
		
		@Override
		public String getDefinition() {
			throw new ProblemStubException();
		}
		
	}
	
	private static class ProblemStubException extends UnsupportedOperationException {

		private static final long serialVersionUID = -5784052018627328509L;

		public ProblemStubException() {
			super("attempting an operation that requires a problem definition");
		}
		
	}

}
