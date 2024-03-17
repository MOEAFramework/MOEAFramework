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

import java.util.HashSet;
import java.util.Set;

import org.moeaframework.core.Algorithm;
import org.moeaframework.core.Problem;
import org.moeaframework.util.TypedProperties;

/**
 * Defines an SPI for algorithms. Algorithms are identified by a unique name and may be given optional
 * {@link TypedProperties}.  The methods of the provider must return {@code null} if the algorithm is not supported by
 * the provider.
 * <p>
 * If the provider can supply the algorithm but an error occurred during instantiation, the provider may throw a
 * {@link ProviderNotFoundException} along with the details causing the exception.
 * <p>
 * To provide a custom {@code AlgorithmProvider}, first extend this class and implement the abstract method. Next,
 * build a JAR file containing the custom provider. Within the JAR file, create the file
 * {@code META-INF/services/org.moeaframework.core.spi.AlgorithmProvider} containing on a single line the class name
 * of the custom provider. Lastly, add this JAR file to the classpath. Once these steps are completed, the
 * algorithms(s) are now accessible via the {@link AlgorithmFactory#getAlgorithm} methods.
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
		return new HashSet<String>();
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
	 */
	public abstract Algorithm getAlgorithm(String name, TypedProperties properties, Problem problem);

}
