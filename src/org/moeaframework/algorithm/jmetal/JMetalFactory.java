/* Copyright 2009-2020 David Hadka
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
package org.moeaframework.algorithm.jmetal;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.util.TypedProperties;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.crossover.PMXCrossover;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.crossover.SinglePointCrossover;
import org.uma.jmetal.operator.impl.mutation.BitFlipMutation;
import org.uma.jmetal.operator.impl.mutation.PermutationSwapMutation;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;

/**
 * Factory class for creating JMetal crossover and mutation operators.
 */
public class JMetalFactory {

	/**
	 * The default JMetal operator factory.
	 */
	private static JMetalFactory instance;

	/**
	 * Instantiates the static {@code instance} object.
	 */
	static {
		instance = new JMetalFactory();
	}

	/**
	 * Returns the default JMetal operator factory.
	 * 
	 * @return the default JMetal operator factory
	 */
	public static synchronized JMetalFactory getInstance() {
		return instance;
	}

	/**
	 * Sets the default JMetal operator factory.
	 * 
	 * @param instance the default JMetal operator factory
	 */
	public static synchronized void setInstance(JMetalFactory instance) {
		JMetalFactory.instance = instance;
	}

	/**
	 * Mapping from problem type to the default crossover operator.
	 */
	private final Map<Class<? extends ProblemAdapter<?>>, OperatorDescriptor<CrossoverOperator<?>>> crossoverOperators;
	
	/**
	 * Mapping from problem type to the default mutation operator.
	 */
	private final Map<Class<? extends ProblemAdapter<?>>, OperatorDescriptor<MutationOperator<?>>> mutationOperators;

	/**
	 * Constructs a new JMetal operator factory.
	 */
	public JMetalFactory() {
		super();

		crossoverOperators = new HashMap<Class<? extends ProblemAdapter<?>>, OperatorDescriptor<CrossoverOperator<?>>>();
		mutationOperators = new HashMap<Class<? extends ProblemAdapter<?>>, OperatorDescriptor<MutationOperator<?>>>();
		
		
		registerCrossoverOperator(DoubleProblemAdapter.class,
				SBXCrossover.class,
				new DoubleParameterDescriptor("sbx.rate", 1.0),
				new DoubleParameterDescriptor("sbx.distributionIndex", 15.0));
		registerCrossoverOperator(BinaryProblemAdapter.class,
				SinglePointCrossover.class,
				new DoubleParameterDescriptor("1x.rate", 1.0));
		registerCrossoverOperator(PermutationProblemAdapter.class,
				PMXCrossover.class,
				new DoubleParameterDescriptor("pmx.rate", 1.0));
		

		registerMutationOperator(DoubleProblemAdapter.class,
				PolynomialMutation.class,
				new MutationRateParameterDescriptor("pm.rate"),
				new DoubleParameterDescriptor("pm.distributionIndex", 20.0));
		registerMutationOperator(BinaryProblemAdapter.class,
				BitFlipMutation.class,
				new MutationRateParameterDescriptor("pf.rate"));
		registerMutationOperator(PermutationProblemAdapter.class,
				PermutationSwapMutation.class,
				new DoubleParameterDescriptor("swap.rate", 0.35));
	}

	/**
	 * Registers a crossover operator with this factory.  If a mapping already exists for the
	 * problem type, it will be overridden.
	 * 
	 * @param problemType the JMetal problem type
	 * @param crossoverType the crossover operator class
	 * @param parameters list of parameters used when initializing the crossover operator
	 */
	public <T extends CrossoverOperator<?>> void registerCrossoverOperator(
			Class<? extends ProblemAdapter<?>> problemType, Class<T> crossoverType,
			ParameterDescriptor<?>... parameters) {
		crossoverOperators.put(problemType,
				new OperatorDescriptor<CrossoverOperator<?>>(crossoverType, parameters));
	}
	
	/**
	 * Registers a mutation operator with this factory.  If a mapping already exists for the
	 * problem type, it will be overridden.
	 * 
	 * @param problemType the JMetal problem type
	 * @param mutationType the crossover operator class
	 * @param parameters list of parameters used when initializing the mutation operator
	 */
	public <T extends MutationOperator<?>> void registerMutationOperator(
			Class<? extends ProblemAdapter<?>> problemType, Class<T> mutationType,
			ParameterDescriptor<?>... parameters) {
		mutationOperators.put(problemType,
				new OperatorDescriptor<MutationOperator<?>>(mutationType, parameters));
	}
	
	/**
	 * Evaluates the list of parameters to resolve their value.  The value will either
	 * be the user-defined property value, if set, or the default value.
	 * 
	 * @param parameters the list of parameters
	 * @param problem the problem adapter
	 * @param properties the user-provided properties
	 * @return the resolved parameter values
	 */
	public Object[] toArguments(List<ParameterDescriptor<?>> parameters, ProblemAdapter<?> problem,
			TypedProperties properties) {
		Object[] result = new Object[parameters.size()];
		
		for (int i = 0; i < parameters.size(); i++) {
			result[i] = parameters.get(i).getValue(problem, properties);
		}
		
		return result;
	}

	/**
	 * Constructs the JMetal crossover operator for the given problem.
	 * 
	 * @param problem the problem adapter
	 * @param properties the user-provided properties
	 * @return the crossover operator
	 * @throws FrameworkException if no operator was registered for the given
	 *         problem type or an error occurred while constructing the instance
	 */
	public CrossoverOperator<?> createCrossoverOperator(ProblemAdapter<?> problem, TypedProperties properties) {
		OperatorDescriptor<? extends CrossoverOperator<?>> operator = crossoverOperators.get(problem.getClass());
		
		if (operator == null) {
			throw new FrameworkException("No crossover operator defined for problem type " + problem.getClass());
		} else {
			try {
				return ConstructorUtils.invokeConstructor(operator.getType(),
						toArguments(operator.getParameters(), problem, properties));
			} catch (InstantiationException e) {
				throw new FrameworkException(e);
			} catch (IllegalAccessException e) {
				throw new FrameworkException(e);
			} catch (InvocationTargetException e) {
				throw new FrameworkException(e);
			} catch (NoSuchMethodException e) {
				throw new FrameworkException(e);
			}
		}
	}
	
	/**
	 * Constructs the JMetal mutation operator for the given problem.
	 * 
	 * @param problem the problem adapter
	 * @param properties the user-provided properties
	 * @return the mutation operator
	 * @throws FrameworkException if no operator was registered for the given
	 *         problem type or an error occurred while constructing the instance
	 */
	public MutationOperator<?> createMutationOperator(ProblemAdapter<?> problem, TypedProperties properties) {
		OperatorDescriptor<? extends MutationOperator<?>> operator = mutationOperators.get(problem.getClass());
		
		if (operator == null) {
			throw new FrameworkException("No mutation operator defined for problem type " + problem.getClass());
		} else {
			try {
				return ConstructorUtils.invokeConstructor(operator.getType(),
						toArguments(operator.getParameters(), problem, properties));
			} catch (InstantiationException e) {
				throw new FrameworkException(e);
			} catch (IllegalAccessException e) {
				throw new FrameworkException(e);
			} catch (InvocationTargetException e) {
				throw new FrameworkException(e);
			} catch (NoSuchMethodException e) {
				throw new FrameworkException(e);
			}
		}
	}

	/**
	 * Defines a crossover or mutation operator along with its parameters.  The
	 * type and order of the parameters will determine which constructor is invoked.
	 *
	 * @param <T> the base type of the operator
	 */
	public class OperatorDescriptor<T> {

		/**
		 * The operator type.
		 */
		private final Class<? extends T> type;

		/**
		 * The parameters used to call the constructor.
		 */
		private final List<ParameterDescriptor<?>> parameters;

		/**
		 * Defines a crossover or mutation operator.
		 * 
		 * @param type the operator type
		 * @param parameters the parameters used to call the constructor
		 */
		public OperatorDescriptor(Class<? extends T> type, ParameterDescriptor<?>... parameters) {
			this.type = type;
			this.parameters = Arrays.asList(parameters);
		}

		/**
		 * Returns the operator type.
		 * 
		 * @return the operator type
		 */
		public Class<? extends T> getType() {
			return type;
		}

		/**
		 * Returns the parameters used to call the constructor.
		 * 
		 * @return the parameters used to call the constructor
		 */
		public List<ParameterDescriptor<?>> getParameters() {
			return parameters;
		}

	}

	/**
	 * Abstract class for defining a parameter used when invoking the constructor.
	 *
	 * @param <T> the type of the parameter
	 */
	public abstract class ParameterDescriptor<T> {

		/**
		 * The name of the parameter that is provided by the user, such as
		 * {@code "sbx.rate"}.
		 */
		private final String name;

		/**
		 * The default value of the parameter.
		 */
		private final T defaultValue;

		/**
		 * Defines a parameter.
		 * 
		 * @param name the name of the parameter
		 * @param defaultValue the default value
		 */
		public ParameterDescriptor(String name, T defaultValue) {
			this.name = name;
			this.defaultValue = defaultValue;
		}

		/**
		 * Returns the name of the parameter.
		 * 
		 * @return the name of the parameter
		 */
		public String getName() {
			return name;
		}

		/**
		 * Returns the default value.
		 * 
		 * @return the default value
		 */
		public T getDefaultValue() {
			return defaultValue;
		}
		
		/**
		 * Resolves the value of this parameter given the user-defined properties.  Should either
		 * return the user-defined value or the default value.
		 * 
		 * @param problem the problem adapter, providing information about the problem itself
		 * @param properties the user-defined properties
		 * @return the resolved value
		 */
		public abstract T getValue(ProblemAdapter<?> problem, TypedProperties properties);

	}
	
	/**
	 * Defines a parameter of type {@code Double}.
	 */
	public class DoubleParameterDescriptor extends ParameterDescriptor<Double> {
		
		/**
		 * Creates a new parameter of type {@code Double}.
		 * 
		 * @param name the name of the parameter
		 * @param defaultValue the default value
		 */
		public DoubleParameterDescriptor(String name, Double defaultValue) {
			super(name, defaultValue);
		}
		
		@Override
		public Double getValue(ProblemAdapter<?> problem, TypedProperties properties) {
			return properties.getDouble(getName(), getDefaultValue());
		}
		
	}
	
	/**
	 * Defines a parameter of type used to represent a mutation rate. The default value
	 * is {@code 1 / getNumberOfMutationIndices()}.
	 */
	public class MutationRateParameterDescriptor extends DoubleParameterDescriptor {
		
		/**
		 * Creates a new parameter that represents a mutation rate.
		 * 
		 * @param name the name of the parameter
		 */
		public MutationRateParameterDescriptor(String name) {
			super(name, 0.0);
		}
		
		@Override
		public Double getValue(ProblemAdapter<?> problem, TypedProperties properties) {
			return properties.getDouble(getName(), 1.0 / problem.getNumberOfMutationIndices());
		}
		
	}

}
