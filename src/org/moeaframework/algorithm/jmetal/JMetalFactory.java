/* Copyright 2009-2019 David Hadka
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

public class JMetalFactory {

	/**
	 * The default JMetal object factory.
	 */
	private static JMetalFactory instance;

	/**
	 * Instantiates the static {@code instance} object.
	 */
	static {
		instance = new JMetalFactory();
	}

	/**
	 * Returns the default JMetal object factory.
	 * 
	 * @return the default JMetal object factory
	 */
	public static synchronized JMetalFactory getInstance() {
		return instance;
	}

	/**
	 * Sets the default JMetal object factory.
	 * 
	 * @param instance
	 *            the default JMetal object factory
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
	 * Constructs a new JMetal object factory.
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

	public <T extends CrossoverOperator<?>> void registerCrossoverOperator(
			Class<? extends ProblemAdapter<?>> problemType, Class<T> crossoverType,
			ParameterDescriptor<?>... parameters) {
		crossoverOperators.put(problemType,
				new OperatorDescriptor<CrossoverOperator<?>>(crossoverType, parameters));
	}
	
	public <T extends MutationOperator<?>> void registerMutationOperator(
			Class<? extends ProblemAdapter<?>> problemType, Class<T> mutationType,
			ParameterDescriptor<?>... parameters) {
		mutationOperators.put(problemType,
				new OperatorDescriptor<MutationOperator<?>>(mutationType, parameters));
	}
	
	public Object[] toArguments(List<ParameterDescriptor<?>> parameters, ProblemAdapter<?> problem,
			TypedProperties properties) {
		Object[] result = new Object[parameters.size()];
		
		for (int i = 0; i < parameters.size(); i++) {
			result[i] = parameters.get(i).getValue(problem, properties);
		}
		
		return result;
	}

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

	public class OperatorDescriptor<T> {

		private final Class<? extends T> type;

		private final List<ParameterDescriptor<?>> parameters;

		public OperatorDescriptor(Class<? extends T> type, ParameterDescriptor<?>... parameters) {
			this.type = type;
			this.parameters = Arrays.asList(parameters);
		}

		public Class<? extends T> getType() {
			return type;
		}

		public List<ParameterDescriptor<?>> getParameters() {
			return parameters;
		}

	}

	public abstract class ParameterDescriptor<T> {

		private final String name;

		private final T defaultValue;

		public ParameterDescriptor(String name, T defaultValue) {
			this.name = name;
			this.defaultValue = defaultValue;
		}

		public String getName() {
			return name;
		}

		public T getDefaultValue() {
			return defaultValue;
		}
		
		public abstract T getValue(ProblemAdapter<?> problem, TypedProperties properties);

	}
	
	public class DoubleParameterDescriptor extends ParameterDescriptor<Double> {
		
		public DoubleParameterDescriptor(String name, Double defaultValue) {
			super(name, defaultValue);
		}
		
		@Override
		public Double getValue(ProblemAdapter<?> problem, TypedProperties properties) {
			return properties.getDouble(getName(), getDefaultValue());
		}
		
	}
	
	public class MutationRateParameterDescriptor extends DoubleParameterDescriptor {
		
		public MutationRateParameterDescriptor(String name) {
			super(name, 0.0);
		}
		
		@Override
		public Double getValue(ProblemAdapter<?> problem, TypedProperties properties) {
			return properties.getDouble(getName(), 1.0 / problem.getNumberOfMutationIndices());
		}
		
	}

}
