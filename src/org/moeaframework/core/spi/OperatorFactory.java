/* Copyright 2009-2022 David Hadka
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
import org.moeaframework.core.Problem;
import org.moeaframework.core.Variation;
import org.moeaframework.core.operator.CompoundVariation;
import org.moeaframework.util.TypedProperties;

/**
 * Factory for creating and variation (e.g., crossover and mutation) operator
 * instances.
 * <p>
 * Operators can be combined by joining the two operator names with the plus
 * sign, such as {@code "sbx+pm"}.  Not all operators can be joined this way.
 * See {@link CompoundVariation} for the restrictions.
 * <p>
 * This class is thread safe.
 */
public class OperatorFactory extends AbstractFactory<OperatorProvider> {
	
	/**
	 * The default operator factory.
	 */
	private static OperatorFactory INSTANCE;
	
	/**
	 * Instantiates the static {@code INSTANCE} object.
	 */
	static {
		INSTANCE = new OperatorFactory();
	}
	
	/**
	 * Returns the default operator factory.
	 * 
	 * @return the default operator factory
	 */
	public static synchronized OperatorFactory getInstance() {
		return INSTANCE;
	}

	/**
	 * Sets the default operator factory.
	 * 
	 * @param instance the default operator factory
	 */
	public static synchronized void setInstance(OperatorFactory instance) {
		OperatorFactory.INSTANCE = instance;
	}
	
	/**
	 * Constructs a new operator factory.
	 */
	public OperatorFactory() {
		super(OperatorProvider.class);
	}
	
	/**
	 * Returns the name of the default mutation operator for the given problem.
	 * Mixed types are currently not supported.
	 * 
	 * @param problem the problem
	 * @return the name of the default mutation operator for the given problem
	 * @throws ProviderLookupException if no default mutation operator could
	 *         be determined
	 */
	public String getDefaultMutation(Problem problem) {
		String result = lookupMutationHint(problem);
		
		if (result == null) {
			throw new ProviderLookupException("unsupported or unknown type");
		}
		
		return result;
	}
	
	/**
	 * Returns the name of the default variation operator (e.g., crossover with
	 * mutation) for the given problem.  Mixed types are currently not
	 * supported.
	 * 
	 * @param problem the problem
	 * @return the name of the default variation operator for the given problem
	 * @throws ProviderLookupException if no default variation operator could
	 *         be determined
	 */
	public String getDefaultVariation(Problem problem) {
		String result = lookupVariationHint(problem);
		
		if (result == null) {
			throw new ProviderLookupException("unsupported or unknown type");
		}
		
		return result;
	}

	/**
	 * Returns an instance of the variation operator with the specified name.
	 * This method must throw an {@link ProviderNotFoundException} if no 
	 * suitable operator is found.  If {@code name} is null, the factory should
	 * return a default variation operator appropriate for the problem.
	 * 
	 * @param name the name identifying the variation operator
	 * @param properties the implementation-specific properties
	 * @param problem the problem to be solved
	 * @return an instance of the variation operator with the specified name
	 * @throws ProviderNotFoundException if no provider for the algorithm is 
	 *         available
	 */
	public Variation getVariation(String name, TypedProperties properties, Problem problem) {
		if (name == null) {
			String operator = properties.getString("operator", null);
			
			if (operator == null) {
				String hint = lookupVariationHint(problem);
				return getVariation(hint, properties, problem);
			} else {
				return getVariation(operator, properties, problem);
			}
		} else if (name.contains("+")) {
			String[] entries = name.split("\\+");
			CompoundVariation variation = new CompoundVariation();
			
			for (String entry : entries) {
				variation.appendOperator(getVariation(entry.trim(), properties, problem));
			}
			
			return variation;
		} else {
			return instantiateVariation(name, properties, problem);
		}
	}
	
	private Variation instantiateVariation(OperatorProvider provider, String name,
			TypedProperties properties, Problem problem) {
		try {
			return provider.getVariation(name, properties, problem);
		} catch (ServiceConfigurationError e) {
			System.err.println(e.getMessage());
		}
		
		return null;
	}
	
	private Variation instantiateVariation(String name, TypedProperties properties, Problem problem) {
		for (OperatorProvider provider : this) {
			Variation variation = instantiateVariation(provider, name, properties, problem);
			
			if (variation != null) {
				return variation;
			}
		}

		throw new ProviderNotFoundException(name);
	}
	
	private String lookupMutationHint(Problem problem) {
		for (OperatorProvider provider : this) {
			String hint = provider.getMutationHint(problem);
			
			if (hint != null) {
				return hint;
			}
		}
		
		throw new ProviderLookupException("unable to find suitable variation operator");
	}
	
	private String lookupVariationHint(Problem problem) {
		for (OperatorProvider provider : this) {
			String hint = provider.getVariationHint(problem);

			if (hint != null) {
				return hint;
			}
		}
		
		throw new ProviderLookupException("unable to find suitable variation operator");
	}
	
}
