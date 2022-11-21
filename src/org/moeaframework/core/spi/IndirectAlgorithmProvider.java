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

