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

import java.util.TreeMap;
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
	 * Creates a new, empty problem provider.
	 */
	public RegisteredAlgorithmProvider() {
		super();
		constructorMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
	}
	
	/**
	 * Registers a new algorithm with this provider.
	 * 
	 * @param constructor the function that creates a new instance of the algorithm
	 * @param names the name or names for this algorithm
	 */
	protected final void register(BiFunction<TypedProperties, Problem, Algorithm> constructor,
			String... names) {
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
