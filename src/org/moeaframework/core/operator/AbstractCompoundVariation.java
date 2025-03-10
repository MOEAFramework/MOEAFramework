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
package org.moeaframework.core.operator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Solution;
import org.moeaframework.core.TypedProperties;

/**
 * Constructs a variation operator applying two or more variations sequentially.  This is often used for mixed-type
 * decision variables by supplying a <strong>type-safe</strong> operator for each type.
 * <p>
 * The number of parents (arity) of an operator must equal or evenly divide the number of offspring produced by the
 * previous operator.  When equal, the offspring are passed directly into the next operator.  When divisible, the
 * offspring are partitioned and multiple calls made to the next operator.  Therefore, we typically want to put all
 * crossover operators first followed by any mutation operators.
 * <p>
 * Consequently, not all combinations of operators are valid and may result in a runtime exception being thrown.
 * 
 * @param <T> the type of variation operators
 */
public class AbstractCompoundVariation<T extends Variation> implements Variation {

	/**
	 * The variation operators in the order they are applied.
	 */
	protected final List<T> operators;
	
	/**
	 * The name of this variation operator.
	 */
	private String name;

	/**
	 * Constructs a compound variation operator with no variation operators.
	 */
	public AbstractCompoundVariation() {
		super();

		operators = new ArrayList<>();
	}
	
	/**
	 * Returns {@code true} if one or more of the operators are the given type.
	 * 
	 * @param type the variation type
	 * @return {@code true} if any operators are the given type
	 */
	public boolean contains(Class<? extends Variation> type) {
		for (T operator : operators) {
			if (type.isInstance(operator)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Returns the name of this variation operator.  If no name has been assigned through {@link #setName(String)},
	 * a name is generated which identifies the underlying operators.
	 * 
	 * @return the name of this variation operator
	 */
	@Override
	public String getName() {
		if (name == null) {
			StringBuilder sb = new StringBuilder();
			
			for (T operator : operators) {
				if (sb.length() > 0) {
					sb.append('+');
				}
				
				sb.append(operator.getName());
			}
			
			return sb.toString();
		} else {
			return name;
		}
	}
	
	/**
	 * Sets the name of this variation operator.
	 * 
	 * @param name the name of this variation operator
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Appends the specified variation operator to this compound operator.
	 * 
	 * @param variation the variation operator to append
	 */
	void appendOperator(T variation) {
		operators.add(variation);
	}

	@Override
	public Solution[] evolve(Solution[] parents) {
		Solution[] solutions = Arrays.copyOf(parents, parents.length);
		
		for (T operator : operators) {
			List<Solution> result = new ArrayList<>();
			
			if (operator instanceof Mutation mutation) {
				for (int i = 0; i < solutions.length; i++) {
					result.add(mutation.mutate(solutions[i]));
				}
			} else if (solutions.length % operator.getArity() == 0) {
				for (int i = 0; i < solutions.length; i += operator.getArity()) {
					result.addAll(List.of(operator.evolve(Arrays.copyOfRange(solutions, i, i + operator.getArity()))));
				}
			} else {
				throw new FrameworkException("Invalid operator combination, " + operator.getName() + " expected " +
						operator.getArity() + " parents (or a multiple thereof) but given " + solutions.length);
			}
			
			solutions = result.toArray(Solution[]::new);
		}

		return solutions;
	}

	@Override
	public int getArity() {
		return operators.get(0).getArity();
	}
	
	/**
	 * Returns an unmodifiable list of the operations included in this compound variation instance.
	 * 
	 * @return the operators
	 */
	public List<T> getOperators() {
		return Collections.unmodifiableList(operators);
	}
	
	@Override
	public void applyConfiguration(TypedProperties properties) {
		for (T operator : operators) {
			operator.applyConfiguration(properties);
		}
	}
	
	@Override
	public TypedProperties getConfiguration() {
		TypedProperties result = new TypedProperties();
		
		for (T operator : operators) {
			result.addAll(operator.getConfiguration());
		}
		
		return result;
	}

}
