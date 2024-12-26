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
package org.moeaframework.core.spi;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.moeaframework.core.Solution;
import org.moeaframework.core.TypedProperties;
import org.moeaframework.core.operator.CompoundVariation;
import org.moeaframework.core.operator.Variation;
import org.moeaframework.core.variable.BinaryIntegerVariable;
import org.moeaframework.core.variable.Variable;
import org.moeaframework.problem.Problem;

/**
 * Operator provider that lets callers register operators by name.
 */
public class RegisteredOperatorProvider extends OperatorProvider {
	
	/**
	 * Mapping of decision variable types to the suggested mutation operator.
	 */
	private final Map<Class<? extends Variable>, String> mutationHints;
	
	/**
	 * Mapping of decision variable types to the suggested crossover operator.
	 */
	private final Map<Class<? extends Variable>, String> crossoverHints;
	
	/**
	 * Mapping of operators names to a constructor function.
	 */
	private final Map<String, BiFunction<TypedProperties, Problem, Variation>> constructorMap;
	
	/**
	 * Constructs a new, empty operator provider.
	 */
	public RegisteredOperatorProvider() {
		super();
		mutationHints = new HashMap<>();
		crossoverHints = new HashMap<>();
		constructorMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
	}
	
	/**
	 * Sets the mutation hint for the given decision variable type, overwriting any existing hint.  By convention,
	 * all mutation operators should accept a single parent and produce a single offspring.
	 * 
	 * @param type the decision variable type
	 * @param operator the suggested operator
	 */
	protected final void setMutationHint(Class<? extends Variable> type, String operator) {
		mutationHints.put(type, operator);
	}
	
	/**
	 * Sets the crossover hint for the given decision variable type, overwriting any existing hint.  While not strictly
	 * required, we recommend only configuring crossover operators that accept two parents and produce two offspring.
	 * This convention guarantees the crossover operators can be combined safely.
	 * 
	 * @param type the decision variable type
	 * @param operator the suggested operator
	 */
	protected final void setCrossoverHint(Class<? extends Variable> type, String operator) {
		crossoverHints.put(type, operator);
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
	 * Returns the names of all operators registered with this provider.
	 * 
	 * @return the names of all registered operators
	 */
	public Set<String> getRegisteredOperators() {
		Set<String> result = new HashSet<>();
		result.addAll(mutationHints.values());
		result.addAll(crossoverHints.values());
		result.addAll(constructorMap.keySet());
		return result;
	}
	
	/**
	 * Generates the operator hint for the given solution.  Exact matching between types is always preferred, but will
	 * fall back to type compatibility.  For example, {@link BinaryIntegerVariable} is compatible with
	 * {@code BinaryVariable}, but not vice-versa.
	 * <p>
	 * For solutions with mixed types, the resulting hint can include two or more operators separated by {@code '+'}.
	 * This method does not guarantee the compatibility of operators.  See {@link CompoundVariation} for details on
	 * operator compatibility.
	 * 
	 * @param solution the solution, which describes the number of types of decision variables
	 * @param hints the operator hints
	 * @return the hint, or {@code null} if no hint available
	 */
	private static final String getHint(Solution solution, Map<Class<? extends Variable>, String> hints) {
		Set<String> operators = new LinkedHashSet<>();
		
		for (int i = 0; i < solution.getNumberOfVariables(); i++) {
			Variable variable = solution.getVariable(i);
			
			if (variable == null) {
				throw new ProviderLookupException("variable is null");
			}
			
			Class<? extends Variable> type = variable.getClass();
			String result = hints.get(type);
			
			// check assignment compatibility if an exact match not found
			if (result == null) {
				for (Entry<Class<? extends Variable>, String> entry : hints.entrySet()) {
					if (entry.getKey().isAssignableFrom(type)) {
						result = entry.getValue();
					}
				}
			}
			
			if (result == null) {
				// no hint for the given type, bail out
				return null;
			}
			
			operators.add(result);
		}
		
		if (operators.size() == 0) {
			return null;
		}
		
		return operators.stream().collect(Collectors.joining("+"));
	}
	
	@Override
	public String getMutationHint(Problem problem) {
		return getHint(problem.newSolution(), mutationHints);
	}
	
	@Override
	public String getVariationHint(Problem problem) {
		Solution solution = problem.newSolution();
		String crossoverHint = getHint(solution, crossoverHints);
		String mutationHint = getHint(solution, mutationHints);
		
		if (crossoverHint == null || mutationHint == null) {
			return null;
		}
		
		return crossoverHint + "+" + mutationHint;
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
