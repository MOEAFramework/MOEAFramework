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

import java.util.ServiceConfigurationError;
import java.util.Set;
import java.util.TreeSet;

import org.moeaframework.core.Algorithm;
import org.moeaframework.core.Problem;
import org.moeaframework.util.TypedProperties;

/**
 * Factory for creating algorithm instances. See {@link AlgorithmProvider} for details on adding new providers.
 * <p>
 * This class is thread safe.
 */
public class AlgorithmFactory extends AbstractFactory<AlgorithmProvider> {

	/**
	 * The default algorithm factory.
	 */
	private static AlgorithmFactory INSTANCE;

	/**
	 * Instantiates the static {@code INSTANCE} object.
	 */
	static {
		INSTANCE = new AlgorithmFactory();
	}
	
	/**
	 * Returns the default algorithm factory.
	 * 
	 * @return the default algorithm factory
	 */
	public static synchronized AlgorithmFactory getInstance() {
		return INSTANCE;
	}

	/**
	 * Sets the default algorithm factory.
	 * 
	 * @param instance the default algorithm factory
	 */
	public static synchronized void setInstance(AlgorithmFactory instance) {
		AlgorithmFactory.INSTANCE = instance;
	}
	
	/**
	 * Constructs a new algorithm factory.
	 */
	public AlgorithmFactory() {
		super(AlgorithmProvider.class);
	}
	
	/**
	 * Searches through all discovered {@code AlgorithmProvider} instances, returning an instance of the algorithm
	 * with the registered name.  The algorithm is initialized using implementation-specific properties.  This
	 * method must throw an {@link ProviderNotFoundException} if no suitable algorithm is found.
	 * 
	 * @param name the name identifying the algorithm
	 * @param problem the problem to be solved
	 * @return an instance of the algorithm with the registered name
	 * @throws ProviderNotFoundException if no provider for the algorithm is available
	 */
	public Algorithm getAlgorithm(String name, Problem problem) {
		return getAlgorithm(name, new TypedProperties(), problem);
	}

	/**
	 * Searches through all discovered {@code AlgorithmProvider} instances, returning an instance of the algorithm
	 * with the registered name. The algorithm is initialized using implementation-specific properties.  This
	 * method must throw an {@link ProviderNotFoundException} if no suitable algorithm is found.
	 * 
	 * @param name the name identifying the algorithm
	 * @param properties the implementation-specific properties
	 * @param problem the problem to be solved
	 * @return an instance of the algorithm with the registered name
	 * @throws ProviderNotFoundException if no provider for the algorithm is available
	 */
	public synchronized Algorithm getAlgorithm(String name, TypedProperties properties, Problem problem) {
		for (AlgorithmProvider provider : this) {
			Algorithm algorithm = instantiateAlgorithm(provider, name, properties, problem);
			
			if (algorithm != null) {
				return algorithm;
			}
		}

		throw new ProviderNotFoundException(name);
	}
	
	/**
	 * Attempts to instantiate the given algorithm using the given provider.
	 * 
	 * @param provider the algorithm provider
	 * @param name the name identifying the algorithm
	 * @param properties the implementation-specific properties
	 * @param problem the problem to be solved
	 * @return an instance of the algorithm with the registered name; or {@code null} if the provider does not
	 *         implement the algorithm
	 */
	private Algorithm instantiateAlgorithm(AlgorithmProvider provider, String name, TypedProperties properties,
			Problem problem) {
		try {
			return provider.getAlgorithm(name, properties, problem);
		} catch (ServiceConfigurationError e) {
			System.err.println(e.getMessage());
		}
		
		return null;
	}
	
	/**
	 * Returns the names of all algorithms that have been registered to appear in the diagnostic tool.
	 * 
	 * @return all diagnostic tool algorithm names
	 */
	public synchronized Set<String> getAllDiagnosticToolAlgorithms() {
		Set<String> result = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		
		for (AlgorithmProvider provider : this) {
			result.addAll(provider.getDiagnosticToolAlgorithms());
		}
		
		return result;
	}

}
