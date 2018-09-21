/* Copyright 2009-2018 David Hadka
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

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;

/**
 * Defines an SPI for instantiation optimization problems. Problems are
 * identified by a unique name. The methods of the provider must return {@code
 * null} if the problem is not supported by the provider.
 * <p>
 * If the provider can supply the problem but an error occurred during
 * instantiation, the provider may throw a {@link ProviderNotFoundException}
 * along with the details causing the exception.
 * <p>
 * To provide a custom {@code ProblemProvider}, first extend this class and
 * implement the two abstract methods. Next, build a JAR file containing the
 * custom provider. Within the JAR file, create the file
 * {@code META-INF/services/org.moeaframework.core.spi.ProblemProvider}
 * containing on a single line the class name of the custom provider. Lastly,
 * add this JAR file to the classpath. Once these steps are completed, the
 * problem(s) are now accessible via the
 * {@link ProblemFactory#getProblem(String)} and
 * {@link ProblemFactory#getReferenceSet(String)} methods.
 * <p>
 * As problems names are often used in file names, it is best to avoid
 * characters which are not compatible with the file system.  It is suggested
 * that names match the following regular expression:  
 * {@code ^[a-zA-Z0-9()\-,]+$}.
 */
public abstract class ProblemProvider {

	/**
	 * Constructs a problem provider.
	 */
	public ProblemProvider() {
		super();
	}

	/**
	 * Returns the problem with the specified name, or {@code null} if this
	 * provider does not support the problem.
	 * 
	 * @param name the problem name
	 * @return the problem with the specified name, or {@code null} if this
	 *         provider does not support the problem
	 */
	public abstract Problem getProblem(String name);

	/**
	 * Returns the reference set for the specified problem, or {@code null} if
	 * this provider does not support the problem or no reference set is
	 * available.
	 * 
	 * @param name the problem name
	 * @return the reference set for the specified problem, or {@code null} if
	 *         this provider does not support the problem or no reference set
	 *         is available
	 */
	public abstract NondominatedPopulation getReferenceSet(String name);

}
