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
 * problem(s) are now accessible via the methods in this class.
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
