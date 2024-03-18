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

import java.util.ServiceConfigurationError;

import org.moeaframework.core.Problem;
import org.moeaframework.core.Variation;
import org.moeaframework.core.operator.CompoundMutation;
import org.moeaframework.core.operator.CompoundVariation;
import org.moeaframework.core.operator.Mutation;
import org.moeaframework.util.TypedProperties;

/**
 * Factory for creating and variation (e.g., crossover and mutation) operator instances.
 * <p>
 * Operators can be combined by joining the two operator names with the plus sign, such as {@code "sbx+pm"}.  Not all
 * operators can be joined this way.  See {@link CompoundVariation} for the restrictions.
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
	 * Returns the suggested mutation operator for the given problem.
	 * 
	 * @param problem the problem
	 * @return an instance of the mutation operator
	 */
	public Mutation getMutation(Problem problem) {
		return getMutation(null, new TypedProperties(), problem);
	}
	
	/**
	 * Returns the suggested mutation operator for the given problem.  If {@code name} is {@code null}, this will
	 * attempt to return a mutation operator appropriate for the problem.
	 * 
	 * @param name the name of the mutation operator
	 * @param properties the implementation-specific properties
	 * @param problem the problem
	 * @return an instance of the mutation operator
	 */
	public Mutation getMutation(String name, TypedProperties properties, Problem problem) {
		if (name == null) {
			name = properties.getString("operator", null);
		}
		
		if (name == null) {
			name = lookupMutationHint(problem);
		}
		
		Variation variation = getVariation(name, properties, problem);
		
		if (variation == null) {
			return null;
		} else if (variation instanceof Mutation mutation) {
			return mutation;
		} else {
			throw new ProviderLookupException("the operator '" + name + "' is not a mutation operator");
		}
	}
	
	/**
	 * Returns the suggested variation operator for the given problem.
	 * 
	 * @param problem the problem to be solved
	 * @return an instance of the variation operator
	 */
	public Variation getVariation(Problem problem) {
		return getVariation(null, problem);
	}
	
	/**
	 * Returns the named variation operator using default settings.
	 * 
	 * @param name the name identifying the variation operator
	 * @param problem the problem to be solved
	 * @return an instance of the variation operator
	 */
	public Variation getVariation(String name, Problem problem) {
		return getVariation(name, new TypedProperties(), problem);
	}

	/**
	 * Returns an instance of the variation operator with the specified name.  This method must throw a
	 * {@link ProviderNotFoundException} if no suitable operator is found.  If {@code name} is null, the factory should
	 * return a default variation operator appropriate for the problem.
	 * 
	 * @param name the name of the variation operator
	 * @param properties the implementation-specific properties
	 * @param problem the problem to be solved
	 * @return an instance of the variation operator
	 * @throws ProviderNotFoundException if no provider for the algorithm is available
	 */
	public Variation getVariation(String name, TypedProperties properties, Problem problem) {
		if (name == null) {
			name = properties.getString("operator", null);
		}
		
		if (name == null) {
			name = lookupVariationHint(problem);
		}
		
		if (name == null) {
			return null;
		}
			
		if (name.contains("+")) {
			String[] entries = name.split("\\+");
			Variation[] operators = new Variation[entries.length];
			
			for (int i = 0; i < entries.length; i++) {
				operators[i] = getVariation(entries[i].trim(), properties, problem);
			}
			
			return createCompoundOperator(operators);
		} else {
			return instantiateVariation(name, properties, problem);
		}
	}
	
	private Variation createCompoundOperator(Variation[] operators) {
		boolean isMutation = true;
		
		for (int i = 0; i < operators.length; i++) {
			if (!(operators[i] instanceof Mutation)) {
				isMutation = false;
				break;
			}
		}
		
		if (isMutation) {
			Mutation[] mutation = new Mutation[operators.length];

			for (int i = 0; i < operators.length; i++) {
				mutation[i] = (Mutation)operators[i];
			}
			
			return new CompoundMutation(mutation);
		} else {
			return new CompoundVariation(operators);
		}
	}
	
	private Variation instantiateVariation(OperatorProvider provider, String name, TypedProperties properties,
			Problem problem) {
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
		
		return null;
	}
	
	private String lookupVariationHint(Problem problem) {
		for (OperatorProvider provider : this) {
			String hint = provider.getVariationHint(problem);

			if (hint != null) {
				return hint;
			}
		}
		
		return null;
	}
	
}
