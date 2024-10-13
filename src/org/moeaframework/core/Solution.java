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
package org.moeaframework.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.SerializationUtils;
import org.moeaframework.core.constraint.Constraint;
import org.moeaframework.core.objective.Objective;
import org.moeaframework.core.population.Population;
import org.moeaframework.core.variable.Variable;
import org.moeaframework.util.clustering.DistanceMeasure;
import org.moeaframework.util.format.Column;
import org.moeaframework.util.format.Formattable;
import org.moeaframework.util.format.TabularData;
import org.moeaframework.util.validate.Validate;

/**
 * A solution to an optimization problem, storing the decision variables, objectives, constraints and attributes.
 * Attributes are arbitrary {@code (key, value)} pairs; they are instance-specific and are not carried over in
 * the copy constructor.
 * <p>
 * Solutions should only be constructed in {@link Problem#newSolution()} or cloned from an existing solution with
 * {@link #copy()}.  This ensures the solutions and configured correctly for the given optimization problem.
 */
public class Solution implements Formattable<Solution>, Serializable {

	private static final long serialVersionUID = -1192586435663892479L;

	/**
	 * The decision variables of this solution.
	 */
	private final Variable[] variables;

	/**
	 * The objectives of this solution.
	 */
	private final Objective[] objectives;

	/**
	 * The constraints of this solution.
	 */
	private final Constraint[] constraints;

	/**
	 * The attributes of this solutions.
	 */
	private final Map<String, Serializable> attributes;

	/**
	 * Constructs a solution with the specified number of variables and objectives with no constraints.
	 * 
	 * @param numberOfVariables the number of variables defined by this solution
	 * @param numberOfObjectives the number of objectives defined by this solution
	 */
	public Solution(int numberOfVariables, int numberOfObjectives) {
		this(numberOfVariables, numberOfObjectives, 0);
	}

	/**
	 * Constructs a solution with the specified number of variables, objectives and constraints.
	 * 
	 * @param numberOfVariables the number of variables defined by this solution
	 * @param numberOfObjectives the number of objectives defined by this solution
	 * @param numberOfConstraints the number of constraints defined by this solution
	 */
	public Solution(int numberOfVariables, int numberOfObjectives, int numberOfConstraints) {
		super();
		Validate.that("numberOfVariables", numberOfVariables).isGreaterThanOrEqualTo(0);
		Validate.that("numberOfObjectives", numberOfObjectives).isGreaterThanOrEqualTo(0);
		Validate.that("numberOfConstraints", numberOfConstraints).isGreaterThanOrEqualTo(0);
		
		variables = new Variable[numberOfVariables];
		objectives = new Objective[numberOfObjectives];
		constraints = new Constraint[numberOfConstraints];
		attributes = new HashMap<String, Serializable>();
	}

	/**
	 * Copy constructor.
	 * 
	 * @param solution the solution being copied
	 */
	protected Solution(Solution solution) {
		this(solution.getNumberOfVariables(), solution.getNumberOfObjectives(), solution.getNumberOfConstraints());

		for (int i = 0; i < solution.getNumberOfVariables(); i++) {
			setVariable(i, solution.getVariable(i) == null ? null : solution.getVariable(i).copy());
		}

		for (int i = 0; i < getNumberOfObjectives(); i++) {
			setObjective(i, solution.getObjective(i) == null ? null : solution.getObjective(i).copy());
		}

		for (int i = 0; i < getNumberOfConstraints(); i++) {
			setConstraint(i, solution.getConstraint(i) == null ? null : solution.getConstraint(i).copy());
		}
	}

	/**
	 * Returns an independent copy of this solution. It is required that {@code x.copy()} is completely independent
	 * from {@code x} . This means any method invoked on {@code x.copy()} in no way alters the state of {@code x}
	 * and vice versa. It is typically the case that {@code x.copy().getClass() == x.getClass()} and
	 * {@code x.copy().equals(x)}
	 * <p>
	 * Note that a solution's attributes are not copied, as the attributes are generally specific to each instance.
	 * 
	 * @return an independent copy of this solution
	 */
	public Solution copy() {
		return new Solution(this);
	}
	
	/**
	 * Similar to {@link #copy()} except all attributes are also copied.  As a result, this method tends to be
	 * significantly slower than {@code copy()} if many large objects are stored as attributes.
	 * 
	 * @return an independent copy of this solution
	 */
	public Solution deepCopy() {
		Solution copy = copy();
		
		for (Map.Entry<String, Serializable> entry : getAttributes().entrySet()) {
			copy.setAttribute(entry.getKey(), SerializationUtils.clone(entry.getValue()));
		}
		
		return copy;
	}

	/**
	 * Returns the number of objectives defined by this solution.
	 * 
	 * @return the number of objectives defined by this solution
	 */
	public int getNumberOfObjectives() {
		return objectives.length;
	}

	/**
	 * Returns the number of variables defined by this solution.
	 * 
	 * @return the number of variables defined by this solution
	 */
	public int getNumberOfVariables() {
		return variables.length;
	}

	/**
	 * Returns the number of constraints defined by this solution.
	 * 
	 * @return the number of constraints defined by this solution
	 */
	public int getNumberOfConstraints() {
		return constraints.length;
	}

	/**
	 * Returns the variable at the specified index.
	 * 
	 * @param index index of the variable to return
	 * @return the variable at the specified index
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         {@code (index < 0) || (index >= getNumberOfVariables())}
	 */
	public Variable getVariable(int index) {
		return variables[index];
	}
	
	/**
	 * Sets the variable at the specified index.
	 * 
	 * @param index index of the variable being set
	 * @param variable the new value of the variable being set
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         {@code (index < 0) || (index >= getNumberOfVariables())}
	 */
	public void setVariable(int index, Variable variable) {
		variables[index] = variable;
	}
	
	private Objective getObjectiveOrDefault(int index) {
		if (objectives[index] == null) {
			objectives[index] = Objective.createDefault();
		}
		
		return objectives[index];
	}
	
	/**
	 * Returns the objective at the specified index.
	 * 
	 * @param index index of the objective to return
	 * @return the objective at the specified index
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         {@code (index < 0) || (index >= getNumberOfObjectives())}
	 */
	public Objective getObjective(int index) {
		return getObjectiveOrDefault(index);
	}

	/**
	 * Sets the objective at the specified index.
	 * 
	 * @param index index of the objective to set
	 * @param objective the new value of the objective being set
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         {@code (index < 0) || (index >= getNumberOfObjectives())}
	 */
	public void setObjective(int index, Objective objective) {
		objectives[index] = objective;
	}
	
	/**
	 * Returns the objective value at the specified index.
	 * 
	 * @param index index of the objective to return
	 * @return the objective value at the specified index
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         {@code (index < 0) || (index >= getNumberOfObjectives())}
	 */
	public double getObjectiveValue(int index) {
		return getObjectiveOrDefault(index).getValue();
	}

	/**
	 * Sets the objective value at the specified index.
	 * 
	 * @param index index of the objective to set
	 * @param objective the new value of the objective being set
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         {@code (index < 0) || (index >= getNumberOfObjectives())}
	 */
	public void setObjectiveValue(int index, double objective) {
		getObjectiveOrDefault(index).setValue(objective);
	}
	
	/**
	 * Sets all objective values of this solution.
	 * 
	 * @param objectives the new objective values for this solution
	 * @throws IllegalArgumentException if {@code objectives.length != getNumberOfObjectives()}
	 * @deprecated use {@link #setObjectiveValues(double[]) instead
	 */
	@Deprecated
	public void setObjectives(double[] objectives) {
		setObjectiveValues(objectives);
	}

	/**
	 * Sets all objective values of this solution.
	 * 
	 * @param objectives the new objective values for this solution
	 * @throws IllegalArgumentException if {@code objectives.length != getNumberOfObjectives()}
	 */
	public void setObjectiveValues(double[] objectives) {
		Validate.that("objectives.length", objectives.length).isEqualTo(getNumberOfObjectives());

		for (int i = 0; i < objectives.length; i++) {
			setObjectiveValue(i, objectives[i]);
		}
	}

	/**
	 * Returns an array containing the objective values of this solution.  Modifying the returned array will not modify
	 * the internal state of this solution.
	 * 
	 * @return an array containing the objective values of this solution
	 */
	public double[] getObjectiveValues() {
		double[] result = new double[objectives.length];
		
		for (int i = 0; i < objectives.length; i++) {
			result[i] = getObjectiveValue(i);
		}
		
		return result;
	}
	
	/**
	 * Returns an array containing the canonical objective values of this solution.  See
	 * {@link Objective#getCanonicalValue() for more details.
	 * 
	 * @return an array containing the canonical objective values of this solution
	 */
	public double[] getCanonicalObjectiveValues() {
		double[] result = new double[this.objectives.length];
		
		for (int i = 0; i < objectives.length; i++) {
			result[i] = getObjective(i).getCanonicalValue();
		}
		
		return result;
	}
	
	/**
	 * Returns {@code true} if all constraints are satisfied; {@code false} otherwise.  This is the opposite of
	 * {@link #violatesConstraints()}.
	 * 
	 * @return {@code true} if all constraints are satisfied; {@code false} otherwise
	 */
	public boolean isFeasible() {
		return !violatesConstraints();
	}

	/**
	 * Returns {@code true} if any of the constraints are violated; {@code false} otherwise.  This is the opposite of
	 * {@link #isFeasible()}.
	 * 
	 * @return {@code true} if any of the constraints are violated; {@code false} otherwise
	 */
	public boolean violatesConstraints() {
		for (int i = 0; i < getNumberOfConstraints(); i++) {
			if (getConstraint(i).isViolation()) {
				return true;
			}
		}

		return false;
	}
	
	/**
	 * Returns the sum of constraint violations, taking the absolute value of each constraint.  Consequently, larger
	 * values reflect larger constraint violations.
	 * 
	 * @return the sum of constraint violations
	 */
	public double getSumOfConstraintViolations() {
		double sum = 0.0;
		
		for (int i = 0; i < getNumberOfConstraints(); i++) {
			sum += getConstraint(i).getMagnitudeOfViolation();
		}
		
		return sum;
	}
	
	private Constraint getConstraintOrDefault(int index) {
		if (constraints[index] == null) {
			constraints[index] = Constraint.createDefault();
		}
		
		return constraints[index];
	}
	
	/**
	 * Returns the constraint at the specified index.
	 * 
	 * @param index index of the variable to be returned
	 * @return the constraint at the specified index
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         {@code (index < 0) || (index >= getNumberOfConstraints())}
	 */
	public Constraint getConstraint(int index) {
		return getConstraintOrDefault(index);
	}
	
	/**
	 * Sets the constraint at the specified index.
	 * 
	 * @param index the index of the constraint being set
	 * @param constraint the new value of the constraint being set
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         {@code (index < 0) || (index >= getNumberOfConstraints())}
	 */
	public void setConstraint(int index, Constraint constraint) {
		constraints[index] = constraint;
	}

	/**
	 * Returns the constraint value at the specified index.
	 * 
	 * @param index index of the variable to be returned
	 * @return the constraint value at the specified index
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         {@code (index < 0) || (index >= getNumberOfConstraints())}
	 */
	public double getConstraintValue(int index) {
		return getConstraintOrDefault(index).getValue();
	}
	
	/**
	 * Sets the constraint value at the specified index.
	 * 
	 * @param index the index of the constraint being set
	 * @param constraint the new value of the constraint being set
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         {@code (index < 0) || (index >= getNumberOfConstraints())}
	 */
	public void setConstraintValue(int index, double constraint) {
		getConstraintOrDefault(index).setValue(constraint);
	}
	
	/**
	 * Sets all constraint values of this solution.
	 * 
	 * @param constraints the new constraint values for this solution
	 * @throws IllegalArgumentException if {@code constraints.length != getNumberOfConstraints()}
	 * @deprecated use {@link #setConstraintValues(double[]) instead
	 */
	@Deprecated
	public void setConstraints(double[] constraints) {
		setConstraintValues(constraints);
	}

	/**
	 * Sets all constraint values of this solution.
	 * 
	 * @param constraints the new constraint values for this solution
	 * @throws IllegalArgumentException if {@code constraints.length != getNumberOfConstraints()}
	 */
	public void setConstraintValues(double[] constraints) {
		Validate.that("constraints.length", constraints.length).isEqualTo(getNumberOfConstraints());

		for (int i = 0; i < constraints.length; i++) {
			setConstraintValue(i, constraints[i]);
		}
	}

	/**
	 * Returns an array containing the constraint values of this solution. Modifying the returned array will not modify
	 * the internal state of this solution.
	 * 
	 * @return an array containing the constraint values of this solution
	 */
	public double[] getConstraintValues() {
		double[] constraints = new double[this.constraints.length];
		
		for (int i = 0; i < constraints.length; i++) {
			constraints[i] = getConstraintValue(i);
		}
		
		return constraints;
	}

	/**
	 * Returns the value of the attribute that is associated with the specified key, or {@code null} if no value has
	 * been associated with the key.
	 * 
	 * @param key the key whose associated value is to be returned
	 * @return the value associated with the specified key, or {@code null} if no value has been associated with
	 *         the key
	 */
	public Object getAttribute(String key) {
		return attributes.get(key);
	}

	/**
	 * Associates the specified value with the specified key. Returns the old value associated with the key, or
	 * {@code null} if no prior value has been associated with the key.
	 * 
	 * @param key the key with which the specified value is to be associated
	 * @param value the value to be associated with the specified key
	 * @return the old value associated with the key, or {@code null} if no prior value has been associated with
	 *         the key
	 */
	public Object setAttribute(String key, Serializable value) {
		return attributes.put(key, value);
	}

	/**
	 * Removes the specified key and its associated value from this solution.  Returns the old value associated with
	 * the key, or {@code null} if no prior value has been associated with the key.
	 * 
	 * @param key the key to be removed
	 * @return the old value associated with the key, or {@code null} if no prior value has been associated with
	 *         the key
	 */
	public Object removeAttribute(String key) {
		return attributes.remove(key);
	}

	/**
	 * Returns {@code true} if the specified key exists in this solution's attributes; {@code false} otherwise.
	 * 
	 * @param key the key whose presence is being tested
	 * @return {@code true} if the specified key exists in this solution's attributes; {@code false} otherwise
	 */
	public boolean hasAttribute(String key) {
		return attributes.containsKey(key);
	}

	/**
	 * Returns the {@link Map} containing this solution's attributes.
	 * 
	 * @return the {@code Map} containing this solution's attributes
	 */
	public Map<String, Serializable> getAttributes() {
		return attributes;
	}

	/**
	 * Adds all attributes to this solution in the specified {@link Map}.
	 * 
	 * @param attributes the {@code Map} containing the attributes to be added to this solution
	 */
	public void addAttributes(Map<String, Serializable> attributes) {
		this.attributes.putAll(attributes);
	}

	/**
	 * Removes all keys and values from this solution's attributes.
	 */
	public void clearAttributes() {
		attributes.clear();
	}
	
	/**
	 * Computes the Euclidean distance (or L<sub>2</sub>)-norm) between two solutions in objective space.  
	 * 
	 * @param otherSolution the other solution
	 * @return the Euclidean distance in objective space
	 * @throws IllegalArgumentException if the solutions have differing numbers of objectives
	 */
	public double euclideanDistance(Solution otherSolution) {
		return distanceTo(otherSolution, 2.0);
	}
	
	/**
	 * Computes the Manhattan distance (or L<sub>1</sub>-norm) between two solutions in objective space.
	 * 
	 * @param otherSolution the other solution
	 * @return the Manhattan distance in objective space
	 * @throws IllegalArgumentException if the solutions have differing numbers of objectives
	 */
	public double manhattanDistance(Solution otherSolution) {
		return distanceTo(otherSolution, 1.0);
	}
	
	/**
	 * Computes the Chebyshev distance (or L<sub>inf</sub>-norm) between two solutions in objective space.
	 * 
	 * @param otherSolution the other solution
	 * @return the Chebyshev distance in objective space
	 * @throws IllegalArgumentException if the solutions have differing numbers of objectives
	 */
	public double chebyshevDistance(Solution otherSolution) {
		Validate.that("otherSolution.getNumberOfObjectives()", otherSolution.getNumberOfObjectives())
			.isEqualTo("this.getNumberOfObjectives()", getNumberOfObjectives());
		
		double max = 0.0;
		
		for (int i = 0; i < getNumberOfObjectives(); i++) {
			max = Math.max(max, Math.abs(getObjectiveValue(i) - otherSolution.getObjectiveValue(i)));
		}
		
		return max;
	}

	/**
	 * Calculates the distance, in objective space, between this solution and another.  This calculation is
	 * commutative, meaning {@code s1.distanceTo(s2) == s2.distanceTo(s1)}.
	 * 
	 * @param otherSolution the other solution
	 * @param power the power ({@code 1.0} for Manhattan distance, {@code 2.0} for Euclidean distance)
	 * @return the distance, in objective space, between the two solutions
	 * @throws IllegalArgumentException if the solutions have differing numbers of objectives
	 */
	private double distanceTo(Solution otherSolution, double power) {
		Validate.that("otherSolution.getNumberOfObjectives()", otherSolution.getNumberOfObjectives())
			.isEqualTo("this.getNumberOfObjectives()", getNumberOfObjectives());
		
		double distance = 0.0;

		for (int i = 0; i < getNumberOfObjectives(); i++) {
			distance += Math.pow(Math.abs(getObjectiveValue(i) - otherSolution.getObjectiveValue(i)), power);
		}

		return Math.pow(distance, 1.0 / power);
	}
	
	/**
	 * Returns the Euclidean distance, in objective space, from this solution to the nearest solution in the
	 * population.
	 * 
	 * @param population the population
	 * @return the Euclidean distance
	 */
	public double distanceToNearestSolution(Population population) {
		return distanceToNearestSolution(population, Solution::euclideanDistance);
	}
	
	/**
	 * Returns the distance, as calculated by the given distance measure, from this solution to the nearest solution in
	 * the population.
	 * 
	 * @param population the population
	 * @param distanceMeasure the measure of distance between two solutions
	 * @return the distance
	 */
	public double distanceToNearestSolution(Population population, DistanceMeasure<Solution> distanceMeasure) {
		double minimum = Double.POSITIVE_INFINITY;

		for (Solution otherSolution : population) {
			minimum = Math.min(minimum, distanceMeasure.compute(this, otherSolution));
		}

		return minimum;
	}
	
	@Override
	public TabularData<Solution> asTabularData() {
		TabularData<Solution> data = new TabularData<Solution>(List.of(this));

		for (int i = 0; i < getNumberOfVariables(); i++) {
			final int index = i;
			data.addColumn(new Column<Solution, Variable>("Var" + (index+1), s -> s.getVariable(index)));
		}
			
		for (int i = 0; i < getNumberOfObjectives(); i++) {
			final int index = i;
			data.addColumn(new Column<Solution, Objective>("Obj" + (index+1), s -> s.getObjective(index)));
		}
			
		for (int i = 0; i < getNumberOfConstraints(); i++) {
			final int index = i;
			data.addColumn(new Column<Solution, Constraint>("Constr" + (index+1), s -> s.getConstraint(index)));
		}
		
		return data;
	}

}
