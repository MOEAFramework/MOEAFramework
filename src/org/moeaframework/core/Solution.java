/* Copyright 2009-2018 David Hadka
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
import java.util.Map;

import org.apache.commons.lang3.SerializationUtils;

/**
 * A solution to an optimization problem, storing the decision variables,
 * objectives, constraints and attributes. Attributes are arbitrary {@code
 * (key, value)} pairs; they are instance-specific and are not carried over in
 * the copy constructor.
 * <p>
 * Solutions should only be constructed in {@link Problem#newSolution()} or 
 * cloned from an existing solution with {@link #copy()}.  This ensures the
 * solutions and configured correctly for the given optimization problem.
 */
public class Solution implements Serializable {

	private static final long serialVersionUID = -1192586435663892479L;

	/**
	 * The decision variables of this solution.
	 */
	private final Variable[] variables;

	/**
	 * The objectives of this solution.
	 */
	private final double[] objectives;

	/**
	 * The constraints of this solution.
	 */
	private final double[] constraints;

	/**
	 * The attributes of this solutions.
	 */
	private final Map<String, Serializable> attributes;

	/**
	 * Constructs a solution with the specified number of variables and 
	 * objectives with no constraints.
	 * 
	 * @param numberOfVariables the number of variables defined by this solution
	 * @param numberOfObjectives the number of objectives defined by this
	 *        solution
	 */
	public Solution(int numberOfVariables, int numberOfObjectives) {
		this(numberOfVariables, numberOfObjectives, 0);
	}

	/**
	 * Constructs a solution with the specified number of variables, objectives
	 * and constraints.
	 * 
	 * @param numberOfVariables the number of variables defined by this solution
	 * @param numberOfObjectives the number of objectives defined by this
	 *        solution
	 * @param numberOfConstraints the number of constraints defined by this
	 *        solution
	 */
	public Solution(int numberOfVariables, int numberOfObjectives,
			int numberOfConstraints) {
		variables = new Variable[numberOfVariables];
		objectives = new double[numberOfObjectives];
		constraints = new double[numberOfConstraints];
		attributes = new HashMap<String, Serializable>();
	}

	/**
	 * Constructs a solution with no variables and the specified objectives.
	 * This is intended for creating reference set solutions.
	 * 
	 * @param objectives the objectives to be stored in this solution
	 */
	public Solution(double[] objectives) {
		this(0, objectives.length, 0);

		for (int i = 0; i < objectives.length; i++) {
			setObjective(i, objectives[i]);
		}
	}

	/**
	 * Copy constructor.
	 * 
	 * @param solution the solution being copied
	 */
	protected Solution(Solution solution) {
		this(solution.getNumberOfVariables(), solution.getNumberOfObjectives(),
				solution.getNumberOfConstraints());

		for (int i = 0; i < solution.getNumberOfVariables(); i++) {
			setVariable(i, solution.getVariable(i).copy());
		}

		for (int i = 0; i < getNumberOfObjectives(); i++) {
			setObjective(i, solution.getObjective(i));
		}

		for (int i = 0; i < getNumberOfConstraints(); i++) {
			setConstraint(i, solution.getConstraint(i));
		}
	}

	/**
	 * Returns an independent copy of this solution. It is required that
	 * {@code x.copy()} is completely independent from {@code x} . This means
	 * any method invoked on {@code x.copy()} in no way alters the state of
	 * {@code x} and vice versa. It is typically the case that
	 * {@code x.copy().getClass() == x.getClass()} and
	 * {@code x.copy().equals(x)}
	 * <p>
	 * Note that a solution's attributes are not copied, as the attributes are
	 * generally specific to each instance.
	 * 
	 * @return an independent copy of this solution
	 */
	public Solution copy() {
		return new Solution(this);
	}
	
	/**
	 * Similar to {@link #copy()} except all attributes are also copied.  As a
	 * result, this method tends to be significantly slower than {@code copy()}
	 * if many large objects are stored as attributes.
	 * 
	 * @return an independent copy of this solution
	 */
	public Solution deepCopy() {
		Solution copy = copy();
		
		for (Map.Entry<String, Serializable> entry : getAttributes().entrySet()) {
			copy.setAttribute(
					entry.getKey(),
					SerializationUtils.clone(entry.getValue()));
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
	 * Returns the objective at the specified index.
	 * 
	 * @param index index of the objective to return
	 * @return the objective at the specified index
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         {@code (index < 0) || (index >= getNumberOfObjectives())}
	 */
	public double getObjective(int index) {
		return objectives[index];
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
	 * Sets the objective at the specified index.
	 * 
	 * @param index index of the objective to set
	 * @param objective the new value of the objective being set
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         {@code (index < 0) || (index >= getNumberOfObjectives())}
	 */
	public void setObjective(int index, double objective) {
		objectives[index] = objective;
	}

	/**
	 * Sets all objectives of this solution.
	 * 
	 * @param objectives the new objectives for this solution
	 * @throws IllegalArgumentException if {@code objectives.length !=
	 *         getNumberOfObjectives()}
	 */
	public void setObjectives(double[] objectives) {
		if (objectives.length != this.objectives.length) {
			throw new IllegalArgumentException("invalid number of objectives");
		}

		for (int i = 0; i < objectives.length; i++) {
			this.objectives[i] = objectives[i];
		}
	}

	/**
	 * Returns an array containing the objectives of this solution. Modifying
	 * the returned array will not modify the internal state of this solution.
	 * 
	 * @return an array containing the objectives of this solution
	 */
	public double[] getObjectives() {
		return objectives.clone();
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

	/**
	 * Returns {@code true} if any of the constraints are violated;
	 * {@code false} otherwise.
	 * 
	 * @return {@code true} if any of the constraints are violated;
	 *         {@code false} otherwise
	 */
	public boolean violatesConstraints() {
		for (int i = 0; i < constraints.length; i++) {
			if (constraints[i] != 0.0) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns the constraint at the specified index.
	 * 
	 * @param index index of the variable to be returned
	 * @return the constraint at the specified index
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         {@code (index < 0) || (index >= getNumberOfConstraints())}
	 */
	public double getConstraint(int index) {
		return constraints[index];
	}

	/**
	 * Sets all constraints of this solution.
	 * 
	 * @param constraints the new constraints for this solution
	 * @throws IllegalArgumentException if {@code constraints.length !=
	 *         getNumberOfConstraints()}
	 */
	public void setConstraints(double[] constraints) {
		if (constraints.length != this.constraints.length) {
			throw new IllegalArgumentException("invalid number of constraints");
		}

		for (int i = 0; i < constraints.length; i++) {
			this.constraints[i] = constraints[i];
		}
	}

	/**
	 * Returns an array containing the constraints of this solution. Modifying
	 * the returned array will not modify the internal state of this solution.
	 * 
	 * @return an array containing the constraints of this solution
	 */
	public double[] getConstraints() {
		return constraints.clone();
	}

	/**
	 * Sets the constraint at the specified index.
	 * 
	 * @param index the index of the constraint being set
	 * @param constraint the new value of the constraint being set
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         {@code (index < 0) || (index >= getNumberOfConstraints())}
	 */
	public void setConstraint(int index, double constraint) {
		constraints[index] = constraint;
	}

	/**
	 * Returns the value of the attribute that is associated with the specified
	 * key, or {@code null} if no value has been associated with the key.
	 * 
	 * @param key the key whose associated value is to be returned
	 * @return the value associated with the specified key, or {@code null} if
	 *         no value has been associated with the key
	 */
	public Object getAttribute(String key) {
		return attributes.get(key);
	}

	/**
	 * Associates the specified value with the specified key. Returns the old
	 * value associated with the key, or {@code null} if no prior value has been
	 * associated with the key.
	 * 
	 * @param key the key with which the specified value is to be associated
	 * @param value the value to be associated with the specified key
	 * @return the old value associated with the key, or {@code null} if no
	 *         prior value has been associated with the key
	 */
	public Object setAttribute(String key, Serializable value) {
		return attributes.put(key, value);
	}

	/**
	 * Removes the specified key and its associated value from this solution.
	 * Returns the old value associated with the key, or {@code null} if no
	 * prior value has been associated with the key.
	 * 
	 * @param key the key to be removed
	 * @return the old value associated with the key, or {@code null} if no
	 *         prior value has been associated with the key
	 */
	public Object removeAttribute(String key) {
		return attributes.remove(key);
	}

	/**
	 * Returns {@code true} if the specified key exists in this solution's
	 * attributes; {@code false} otherwise.
	 * 
	 * @param key the key whose presence is being tested
	 * @return {@code true} if the specified key exists in this solution's
	 *         attributes; {@code false} otherwise
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
	 * @param attributes the {@code Map} containing the attributes to be added
	 *        to this solution
	 */
	public void addAttributes(Map<String, Object> attributes) {
		attributes.putAll(attributes);
	}

	/**
	 * Removes all keys and values from this solution's attributes.
	 */
	public void clearAttributes() {
		attributes.clear();
	}

}
