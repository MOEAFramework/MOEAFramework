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

import org.moeaframework.core.Epsilons;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.problem.Problem;

/**
 * Defines a SPI for creating named optimization problems.
 * <p>
 * To create a custom {@code ProblemProvider}:
 * <ol>
 *   <li>Extend this class and implement the abstract methods.
 *   <li>Create the file {@code META-INF/services/org.moeaframework.core.spi.ProblemProvider} with a line identifying
 *       the fully-qualified class name of the custom provider.
 *   <li>Compile and bundle the {@code .class} file(s) along with the {@code META-INF} folder into a JAR.
 *   <li>Include this JAR on the classpath.
 * </ol>
 * Providers can also be registered directly with {@link ProblemFactory}.
 * <p>
 * As problems names are often used in file names, it is best to avoid characters which are not compatible with the
 * file system.  It is suggested that names match the following regular expression: {@code ^[a-zA-Z0-9()\-,]+$}.
 */
public abstract class ProblemProvider {

	/**
	 * Constructs a problem provider.
	 */
	public ProblemProvider() {
		super();
	}
	
	/**
	 * Returns the problem names to appear in the diagnostic tool.  For best results, only include problems with two
	 * objectives and have a defined reference set.
	 * 
	 * @return the problem names to appear in the diagnostic tool
	 */
	public Set<String> getDiagnosticToolProblems() {
		return new HashSet<>();
	}

	/**
	 * Returns the problem with the specified name, or {@code null} if this provider does not support the problem.
	 * 
	 * @param name the problem name
	 * @return the problem instance or {@code null}
	 * @throws ProviderException if the creation of the problem failed for any reason
	 */
	public abstract Problem getProblem(String name);

	/**
	 * Returns the reference set for the specified problem, or {@code null} if this provider does not support the
	 * problem or no reference set is available.
	 * 
	 * @param name the problem name
	 * @return the reference set or {@code null}
	 */
	public abstract NondominatedPopulation getReferenceSet(String name);
	
	/**
	 * Returns the recommended or default &epsilon; values for the specified problem, or {@code null} if this provider
	 * does not support the problem or no defaults are provided.
	 * 
	 * @param name the problem name
	 * @return the &epsilon; values for the specified problem or {@code null}
	 */
	public Epsilons getEpsilons(String name) {
		return null;
	}

}
