/* Copyright 2009-2022 David Hadka
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
package org.moeaframework.core.spi;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiFunction;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.Variation;
import org.moeaframework.util.TypedProperties;

/**
 * Operator provider that lets callers register operators by name.
 */
public class RegisteredOperatorProvider extends OperatorProvider {
	
	/**
	 * Mapping of decision variable types to the suggested mutation operator.
	 */
	private final Map<Class<? extends Variable>, String> mutationHints;
	
	/**
	 * Mapping of decision variable types to the suggested variation operator.
	 */
	private final Map<Class<? extends Variable>, String> variationHints;
	
	/**
	 * Mapping of operators names to a constructor function.
	 */
	private final TreeMap<String, BiFunction<TypedProperties, Problem, Variation>> constructorMap;
	
	/**
	 * Constructs a new, empty operator provider.
	 */
	public RegisteredOperatorProvider() {
		super();
		mutationHints = new HashMap<>();
		variationHints = new HashMap<>();
		constructorMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
	}
	
	/**
	 * Sets the mutation hint for the given decision variable type, overwriting any existing hint.
	 * 
	 * @param type the decision variable type
	 * @param operator the suggested operator
	 */
	protected final void setMutationHint(Class<? extends Variable> type, String operator) {
		mutationHints.put(type, operator);
	}
	
	/**
	 * Sets the variation hint for the given decision variable type, overwriting any existing hint.
	 * 
	 * @param type the decision variable type
	 * @param operator the suggested operator
	 */
	protected final void setVariationHint(Class<? extends Variable> type, String operator) {
		variationHints.put(type, operator);
	}
	
	/**
	 * Registers a new operator with this provider.
	 * 
	 * @param name the operator name
	 * @param constructor the function that creates a new instance of the operator
	 */
	protected final void register(String name, BiFunction<TypedProperties, Problem, Variation> constructor) {
		constructorMap.put(name, constructor);
	}
	
	/**
	 * For testing only.  Returns the set of all testable operators.
	 * 
	 * @return all testable operators
	 */
	public Set<String> getTestableOperators() {
		Set<String> result = new HashSet<String>();
		result.addAll(mutationHints.values());
		result.addAll(variationHints.values());
		result.addAll(constructorMap.keySet());
		return result;
	}
	
	/**
	 * Determines the decision variable type for the problem.  This only supports
	 * a single type, but will work with compatible types.  For example, the
	 * {@link BinaryIntegerVariable} type is compatible with {@code BinaryVariable},
	 * but not vice-versa.
	 * 
	 * @param problem the problem
	 * @return the single type contained in this problem, or {@code null} if the type
	 *         could not be determined or there were multiple types
	 */
	private Class<? extends Variable> getProblemType(Problem problem) {
		Class<? extends Variable> type = null;
		Solution solution = problem.newSolution();
		
		for (int i=0; i<solution.getNumberOfVariables(); i++) {
			Variable variable = solution.getVariable(i);
			
			if (variable == null) {
				throw new ProviderLookupException("variable is null");
			}
			
			if (type == null) {
				type = variable.getClass();
			} else if (type.isAssignableFrom(variable.getClass())) {
				// the current type is compatible with the variable
			} else if (variable.getClass().isAssignableFrom(type)) {
				// the variable has a more generalized type - use that instead
				type = variable.getClass();
			} else {
				// the types are incompatible
				return null;
			}
		}

		return type;
	}
	
	@Override
	public String getMutationHint(Problem problem) {
		Class<? extends Variable> type = getProblemType(problem);
		
		if (type == null) {
			return null;
		}
		
		// prioritize exact matches
		String result = mutationHints.get(type);
		
		// fall back to checking type compatibility
		if (result == null) {
			for (Entry<Class<? extends Variable>, String> entry : mutationHints.entrySet()) {
				if (entry.getKey().isAssignableFrom(type)) {
					result = entry.getValue();
				}
			}
		}
		
		return result;
	}
	
	@Override
	public String getVariationHint(Problem problem) {
		Class<? extends Variable> type = getProblemType(problem);
		
		if (type == null) {
			return null;
		}
		
		// prioritize exact matches
		String result = variationHints.get(type);
		
		// fall back to checking type compatibility
		if (result == null) {
			for (Entry<Class<? extends Variable>, String> entry : variationHints.entrySet()) {
				if (entry.getKey().isAssignableFrom(type)) {
					result = entry.getValue();
				}
			}
		}
		
		return result;
	}

	@Override
	public Variation getVariation(String name, TypedProperties properties, Problem problem) {
		BiFunction<TypedProperties, Problem, Variation> constructor = constructorMap.get(name);
		
		if (constructor != null) {
			return constructor.apply(properties, problem);
		}
		
		return null;
	}

}
