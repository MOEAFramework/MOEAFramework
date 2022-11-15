package org.moeaframework.core.spi;

import java.util.TreeMap;
import java.util.function.BiFunction;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.Problem;
import org.moeaframework.util.TypedProperties;

/**
 * Algorithm provider that lets callers register algorithms by name.
 */
public class RegisteredAlgorithmProvider extends AlgorithmProvider {
	
	/**
	 * Mapping of algorithm names to a constructor function.
	 */
	private final TreeMap<String, BiFunction<TypedProperties, Problem, Algorithm>> constructorMap;
	
	/**
	 * Creates a new, empty problem provider.
	 */
	public RegisteredAlgorithmProvider() {
		super();
		constructorMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
	}
	
	/**
	 * Registers a new algorithm with this provider.
	 * 
	 * @param constructor the function that creates a new instance of the algorithm
	 * @param names the name or names for this algorithm
	 */
	public final void register(BiFunction<TypedProperties, Problem, Algorithm> constructor,
			String... names) {
		for (String name : names) {
			constructorMap.put(name, constructor);
		}
	}

	@Override
	public Algorithm getAlgorithm(String name, TypedProperties properties, Problem problem) {
		BiFunction<TypedProperties, Problem, Algorithm> constructor = constructorMap.get(name);
		
		if (constructor != null) {
			return constructor.apply(properties, problem);
		}
		
		return null;
	}

}
