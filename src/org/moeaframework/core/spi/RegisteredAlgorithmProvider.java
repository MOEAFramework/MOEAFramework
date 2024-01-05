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
package org.moeaframework.core.spi;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.BiFunction;

import org.moeaframework.core.Algorithm;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Problem;
import org.moeaframework.util.TypedProperties;

/**
 * Algorithm provider that lets callers register algorithms by name.
 */
public class RegisteredAlgorithmProvider extends AlgorithmProvider {
	
	/**
	 * Mapping of algorithm names to a constructor function.
	 */
	private final TreeMap<String, BiFunction<TypedProperties, Problem, Algorithm>> constructorMap;
	
	/**
	 * Collection of algorithms to appear in the diagnostic tool.
	 */
	private final TreeSet<String> diagnosticToolAlgorithms;
	
	/**
	 * Creates a new, empty problem provider.
	 */
	public RegisteredAlgorithmProvider() {
		super();
		constructorMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		diagnosticToolAlgorithms = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
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
	 * @param names the name or names for this algorithm
	 */
	protected final void register(BiFunction<TypedProperties, Problem, Algorithm> constructor, String... names) {
		for (String name : names) {
			constructorMap.put(name, constructor);
		}
	}

	@Override
	public Algorithm getAlgorithm(String name, TypedProperties properties, Problem problem) {
		BiFunction<TypedProperties, Problem, Algorithm> constructor = constructorMap.get(name);
		
		if (constructor != null) {
			try {
				return constructor.apply(properties, problem);
			} catch (FrameworkException | IllegalArgumentException e) {
				throw new ProviderNotFoundException(name, e);
			}
		}
		
		return null;
	}

}
