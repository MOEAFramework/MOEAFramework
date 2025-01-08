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

import java.util.HashSet;
import java.util.Set;

import org.moeaframework.algorithm.Algorithm;
import org.moeaframework.core.TypedProperties;
import org.moeaframework.problem.Problem;

/**
 * Defines a SPI for algorithms.
 * <p>
 * To create a custom {@code AlgorithmProvider}:
 * <ol>
 *   <li>Extend this class and implement the abstract methods.
 *   <li>Create the file {@code META-INF/services/org.moeaframework.core.spi.AlgorithmProvider} with a line identifying
 *       the fully-qualified class name of the custom provider.
 *   <li>Compile and bundle the {@code .class} file(s) along with the {@code META-INF} folder into a JAR.
 *   <li>Include this JAR on the classpath.
 * </ol>
 * Providers can also be registered directly with {@link AlgorithmFactory#addProvider(AlgorithmProvider)}.
 * <p>
 * As algorithm names are often used in file names, it is best to avoid characters which are not compatible with the
 * file system.  It is suggested that names match the following regular expression: {@code ^[a-zA-Z0-9()\-,]+$}.
 */
public abstract class AlgorithmProvider {

	/**
	 * Constructs an algorithm provider.
	 */
	public AlgorithmProvider() {
		super();
	}
	
	/**
	 * Returns the algorithms names to appear in the diagnostic tool.  If there are multiple aliases for the same
	 * algorithm, provide only the canonical name.
	 * 
	 * @return the algorithm names
	 */
	public Set<String> getDiagnosticToolAlgorithms() {
		return new HashSet<>();
	}

	/**
	 * Returns the algorithm with the specified name, or {@code null} if this provider does not support the algorithm.
	 * An optional set of properties may be provided to further define the algorithm; however, the provider is
	 * expected to supply default properties if none are provided.
	 * 
	 * @param name the algorithm name
	 * @param properties optional properties for the algorithm
	 * @param problem the problem
	 * @return the algorithm with the specified name, or {@code null} if this provider does not support the algorithm
	 * @throws ProviderException if the creation of the algorithm failed for any reason
	 */
	public abstract Algorithm getAlgorithm(String name, TypedProperties properties, Problem problem);

}
