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

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.BiFunction;

import org.moeaframework.algorithm.Algorithm;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Settings;
import org.moeaframework.core.TypedProperties;
import org.moeaframework.problem.Problem;

/**
 * Algorithm provider that lets callers register algorithms by name.
 */
public class RegisteredAlgorithmProvider extends AlgorithmProvider {
	
	/**
	 * Mapping of algorithm names to a constructor function.
	 */
	private final Map<String, BiFunction<TypedProperties, Problem, Algorithm>> constructorMap;
	
	/**
	 * Mapping of algorithm name aliases to the registered algorithm name.
	 */
	private final Map<String, String> aliasMap;
	
	/**
	 * Collection of algorithms to appear in the diagnostic tool.
	 */
	private final Set<String> diagnosticToolAlgorithms;
	
	/**
	 * Creates a new, empty problem provider.
	 */
	public RegisteredAlgorithmProvider() {
		super();
		constructorMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		aliasMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		diagnosticToolAlgorithms = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
	}
	
	/**
	 * Returns all algorithms that have been registered with this provider.  Note that this does not necessarily
	 * include all algorithms that can be instantiated by the provider, only those that have been explicitly
	 * registered.
	 * 
	 * @return the algorithm names
	 */
	public Set<String> getRegisteredAlgorithms() {
		return Collections.unmodifiableSet(constructorMap.keySet());
	}
	
	@Override
	public Set<String> getDiagnosticToolAlgorithms() {
		return Collections.unmodifiableSet(diagnosticToolAlgorithms);
	}
	
	/**
	 * Registers the given algorithm to appear in the diagnostic tool.
	 * 
	 * @param name the algorithm name
	 */
	protected final void registerDiagnosticToolAlgorithm(String name) {
		diagnosticToolAlgorithms.add(name);
	}
	
	/**
	 * Registers all of the given algorithms to appear in the diagnostic tool.
	 * 
	 * @param names the algorithm names
	 */
	protected final void registerDiagnosticToolAlgorithms(Collection<String> names) {
		diagnosticToolAlgorithms.addAll(names);
	}
	
	/**
	 * Registers a new algorithm with this provider.
	 * 
	 * @param constructor the function that creates a new instance of the algorithm
	 * @param name the registered name for this algorithm
	 * @param aliases optional aliases for this algorithm
	 */
	protected final void register(BiFunction<TypedProperties, Problem, Algorithm> constructor, String name,
			String... aliases) {
		if (constructorMap.containsKey(name) && Settings.isVerbose()) {
			System.err.println("WARNING: Previously registered algorithm '" + name + "' is being redefined by " +
					getClass().getSimpleName());
		}
		
		constructorMap.put(name, constructor);
		
		for (String alias : aliases) {
			if (aliasMap.containsKey(alias) && Settings.isVerbose()) {
				System.err.println("WARNING: Previously registered alias '" + alias + "' is being redefined by " +
						getClass().getSimpleName());
			}
			
			aliasMap.put(alias, name);
		}
	}

	@Override
	public Algorithm getAlgorithm(String name, TypedProperties properties, Problem problem) {
		BiFunction<TypedProperties, Problem, Algorithm> constructor = constructorMap.get(name);
		
		if (constructor == null && aliasMap.containsKey(name)) {
			constructor = constructorMap.get(aliasMap.get(name));
		}
		
		if (constructor == null) {
			return null;
		}
		
		try {
			return constructor.apply(properties, problem);
		} catch (FrameworkException | IllegalArgumentException e) {
			throw new ProviderNotFoundException(name, e);
		}
	}

}
