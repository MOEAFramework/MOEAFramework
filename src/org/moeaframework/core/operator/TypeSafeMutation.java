package org.moeaframework.core.operator;

import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.configuration.Property;

/**
 * An abstract mutation class that validates the types of each variable before
 * applying the mutation operation with a given probability.
 * 
 * @param <T> the type of decision variable this operator supports
 */
public abstract class TypeSafeMutation<T extends Variable> implements Mutation {
	
	/**
	 * The probability of mutating each decision variable.
	 */
	protected double probability;
	
	/**
	 * The type of decision variable this operator supports.
	 */
	private Class<T> type;

	/**
	 * Constructs a new mutation operator for the given type.
	 * 
	 * @param type the type of decision variable this operator supports
	 * @param probability the probability of mutating each decision variable
	 */
	public TypeSafeMutation(Class<T> type, double probability) {
		super();
		this.type = type;
		this.probability = probability;
	}
	
	/**
	 * Returns the probability of mutating each decision variable
	 * 
	 * @return the probability between 0.0 and 1.0, inclusive
	 */
	public double getProbability() {
		return probability;
	}

	/**
	 * Sets the probability of mutating each decision variable.
	 * 
	 * @param probability the probability between 0.0 and 1.0, inclusive
	 */
	@Property("rate")
	public void setProbability(double probability) {
		this.probability = probability;
	}

	@Override
	public Solution mutate(Solution parent) {
		Solution result = parent.copy();

		for (int i = 0; i < result.getNumberOfVariables(); i++) {
			Variable variable = result.getVariable(i);

			if ((PRNG.nextDouble() <= probability) && type.isInstance(variable)) {
				mutate(type.cast(variable));
			}
		}

		return result;
	}
	
	public abstract void mutate(T variable);

}
