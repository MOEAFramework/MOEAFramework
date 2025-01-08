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

import org.moeaframework.core.TypedProperties;
import org.moeaframework.core.operator.Variation;
import org.moeaframework.problem.Problem;

/**
 * Defines a SPI for variation operators.
 * <p>
 * To create a custom {@code OperatorProvider}:
 * <ol>
 *   <li>Extend this class and implement the abstract methods.
 *   <li>Create the file {@code META-INF/services/org.moeaframework.core.spi.OperatorProvider} with a line identifying
 *       the fully-qualified class name of the custom provider.
 *   <li>Compile and bundle the {@code .class} file(s) along with the {@code META-INF} folder into a JAR.
 *   <li>Include this JAR on the classpath.
 * </ol>
 * Providers can also be registered directly with {@link OperatorFactory#addProvider(OperatorProvider)}.
 */
public abstract class OperatorProvider {
	
	/**
	 * Constructs an operator provider.
	 */
	public OperatorProvider() {
		super();
	}
	
	/**
	 * Returns the name of the suggested mutation operator for the given problem, or {@code null} if no hint is
	 * available.
	 * 
	 * @param problem the problem
	 * @return the name of the suggested mutation operator for the given problem
	 */
	public abstract String getMutationHint(Problem problem);
	
	/**
	 * Returns the name of the suggested variation operator for the given problem, or {@code null} if no hint is
	 * available.
	 * 
	 * @param problem the problem
	 * @return the name of the suggested variation operator for the given problem
	 */
	public abstract String getVariationHint(Problem problem);
	
	/**
	 * Returns an instance of the variation operator with the specified name.  This method must return {@code null}
	 * if the named operator is not supported, or no suitable operator could be identified using hints.
	 * 
	 * @param name the name identifying the variation operator, or {@code null} to select a default operator based on
	 *        the hints
	 * @param properties the implementation-specific properties
	 * @param problem the problem to be solved
	 * @return an instance of the variation operator with the specified name
	 * @throws ProviderException if the creation of the operator failed for any reason
	 */
	public abstract Variation getVariation(String name, TypedProperties properties, Problem problem);

}
