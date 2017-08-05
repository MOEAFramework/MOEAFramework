/* Copyright 2009-2016 David Hadka
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

import java.util.Properties;

import org.moeaframework.core.Problem;
import org.moeaframework.core.Variation;

/**
 * Defines an SPI for initializing different operators.  Operators are
 * identified by a unique name.  The methods of the provider must return {@code
 * null} if the operator is not supported by the provider.
 * <p>
 * If the provider can supply the operator but an error occurred during
 * instantiation, the provider may throw a {@link ProviderNotFoundException}
 * along with the details causing the exception.
 * <p>
 * To provide a custom {@code OperatorProvider}, first extend this class and
 * implement the two abstract methods. Next, build a JAR file containing the
 * custom provider. Within the JAR file, create the file
 * {@code META-INF/services/org.moeaframework.core.spi.OperatorProvider}
 * containing on a single line the class name of the custom provider. Lastly,
 * add this JAR file to the classpath. Once these steps are completed, the
 * operators(s) are now accessible via the methods in this class.
 */
public abstract class OperatorProvider {
	
	/**
	 * Constructs an operator provider.
	 */
	public OperatorProvider() {
		super();
	}
	
	/**
	 * Returns the name of the suggested mutation operator for the given
	 * problem.  Mixed types are currently not supported.  Returns {@code null}
	 * if no mutation operators support the given problem.
	 * 
	 * @param problem the problem
	 * @return the name of the suggested mutation operator for the given problem
	 */
	public abstract String getMutationHint(Problem problem);
	
	/**
	 * Returns the name of the suggested variation operator for the given
	 * problem.  Mixed types are currently not supported.  Returns {@code null}
	 * if no variation operators support the given problem.
	 * 
	 * @param problem the problem
	 * @return the name of the suggested variation operator for the given
	 *         problem
	 */
	public abstract String getVariationHint(Problem problem);
	
	/**
	 * Returns an instance of the variation operator with the specified name.
	 * This method must return {@code null} if no suitable operator is found.
	 * 
	 * @param name the name identifying the variation operator
	 * @param properties the implementation-specific properties
	 * @param problem the problem to be solved
	 * @return an instance of the variation operator with the specified name
	 */
	public abstract Variation getVariation(String name, Properties properties, 
			Problem problem);

}
