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

import java.lang.reflect.InvocationTargetException;

import org.moeaframework.core.Algorithm;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Problem;
import org.moeaframework.util.TypedProperties;

/**
 * Algorithm provider that avoids creating runtime dependencies on external libraries.  It accomplishes
 * this by attempting to load the class dynamically and, if unable to do so, treats it as if
 * the provider simply does not exist.
 */
public class IndirectAlgorithmProvider extends AlgorithmProvider {
	
	private final String className;
	
	private AlgorithmProvider provider;
	
	/**
	 * Creates an indirect provider for the given algorithm provider.
	 * 
	 * @param className the fully-qualified class name for the algorithm provider we want to
	 *        reference without creating runtime dependencies
	 */
	public IndirectAlgorithmProvider(String className) {
		super();
		this.className = className;
	}

	@Override
	public Algorithm getAlgorithm(String name, TypedProperties properties, Problem problem) {
		if (provider == null) {
			try {
				Class<?> providerType = Class.forName(className);
				provider = (AlgorithmProvider)providerType.getConstructor().newInstance();
			} catch (ClassNotFoundException | NoClassDefFoundError e) {
				return null;
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException |
					InvocationTargetException | NoSuchMethodException | SecurityException e) {
				throw new FrameworkException(e);
			}
		}
		
		return provider.getAlgorithm(name, properties, problem);
	}
	
}

