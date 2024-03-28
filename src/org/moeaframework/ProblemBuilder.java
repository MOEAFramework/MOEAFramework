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
package org.moeaframework;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.Epsilons;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.comparator.ParetoDominanceComparator;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.problem.ProblemWrapper;

/**
 * Provides builder methods for instantiating problems and their reference sets.  This is an internal class with all
 * package-private methods, allowing subclasses to selectively expose the methods.
 */
class ProblemBuilder {

	/**
	 * The problem name.  If {@code null}, the problem is specified by {@code problemClass} or {@code problemInstance}.
	 */
	String problemName;
	
	/**
	 * The problem class.  If {@code null}, the problem is specified by {@code problemName} or {@code problemInstance}.
	 */
	Class<?> problemClass;
	
	/**
	 * The specific instance of the problem.  If {@code null}, the problem is specified by {@code problemName} or
	 * {@code problemClass}.
	 */
	UserProvidedProblemInstance problemInstance;
	
	/**
	 * The arguments used when constructing an instance of the problem class.  If {@code null}, then the empty
	 * constructor is used.
	 */
	Object[] problemArguments;
	
	/**
	 * The problem provider for creating problem instances; or {@code null} if the default problem factory should be
	 * used.
	 */
	ProblemFactory problemFactory;
	
	/**
	 * The &epsilon; values used by this builder.
	 */
	Epsilons epsilons;
	
	/**
	 * The reference set used by this builder; or {@code null} if no reference set was configured.  
	 */
	NondominatedPopulation referenceSet;
	
	/**
	 * Constructs a new problem builder.
	 */
	ProblemBuilder() {
		super();
	}
	
	/**
	 * Configures this builder to use the same problem, reference set, &epsilon; values and {@link ProblemFactory} as
	 * the specified instance.
	 * 
	 * @param builder the instance whose problem, reference set, &epsilon; values and {@code ProblemFactory} are to
	 *        be copied
	 * @return a reference to this builder
	 */
	ProblemBuilder withSameProblemAs(ProblemBuilder builder) {
		this.problemName = builder.problemName;
		this.problemClass = builder.problemClass;
		this.problemInstance = builder.problemInstance;
		this.problemArguments = builder.problemArguments;
		this.problemFactory = builder.problemFactory;
		this.epsilons = builder.epsilons;
		this.referenceSet = builder.referenceSet;
		
		return this;
	}
	
	/**
	 * Sets the problem factory used by this builder.
	 * 
	 * @param problemFactory the problem factory
	 * @return a reference to this builder
	 */
	ProblemBuilder usingProblemFactory(ProblemFactory problemFactory) {
		this.problemFactory = problemFactory;
		return this;
	}
	
	/**
	 * Sets the problem used by this builder.
	 * 
	 * @param problemName the problem name
	 * @return a reference to this builder
	 */
	ProblemBuilder withProblem(String problemName) {
		this.problemName = problemName;
		this.problemArguments = null;
		this.problemClass = null;
		this.problemInstance = null;
		
		return this;
	}
	
	/**
	 * Sets the problem instance used by this builder.  Unlike the other {@code withProblem} methods, using a problem
	 * instance will not close the problem.  It is the responsibility of the user to ensure any problems holding
	 * resources are properly closed.
	 * 
	 * @param problemInstance the problem instance
	 * @return a reference to this builder
	 */
	ProblemBuilder withProblem(Problem problemInstance) {
		this.problemInstance = problemInstance instanceof UserProvidedProblemInstance userProvidedProblemInstance ?
				userProvidedProblemInstance : new UserProvidedProblemInstance(problemInstance);
		this.problemArguments = null;
		this.problemName = null;
		this.problemClass = null;
		
		return this;
	}
	
	/**
	 * Sets the problem used by this builder.  Instances of the problem are created using the
	 * {@link Class#newInstance()} method.  Ensure the problem class satisfies all the conditions of
	 * {@code newInstance()}, otherwise exceptions may be thrown when attempting to create an instance of the
	 * problem.
	 * 
	 * @param problemClass the problem class
	 * @param problemArguments the arguments passed to the problem constructor
	 * @return a reference to this builder
	 */
	ProblemBuilder withProblemClass(Class<?> problemClass, Object... problemArguments) {
		this.problemClass = problemClass;
		this.problemArguments = problemArguments;
		this.problemName = null;
		this.problemInstance = null;
		
		return this;
	}
	
	/**
	 * Sets the problem used by this builder.  Instances of the problem are created using the
	 * {@link Class#newInstance()} method.  Ensure the problem class satisfies all the conditions of
	 * {@code newInstance()}, otherwise exceptions may be thrown when attempting to create an instance of the
	 * problem.
	 * 
	 * @param problemClassName the problem class name
	 * @param problemArguments the arguments passed to the problem constructor
	 * @return a reference to this builder
	 * @throws ClassNotFoundException if the specified problem class name could not be found
	 */
	ProblemBuilder withProblemClass(String problemClassName, Object... problemArguments) throws ClassNotFoundException {
		withProblemClass(Class.forName(problemClassName), problemArguments);
		return this;
	}
	
	/**
	 * Sets the &epsilon; values used by this builder, specifying the archive returned by {@link #newArchive()}.
	 * 
	 * @param epsilon the &epsilon; values
	 * @return a reference to this builder
	 */
	ProblemBuilder withEpsilon(double... epsilon) {
		return withEpsilons(new Epsilons(epsilon));
	}
	
	/**
	 * Sets the &epsilon; values used by this builder, specifying the archive returned by {@link #newArchive()}.
	 * 
	 * @param epsilons the &epsilon; values
	 * @return a reference to this builder
	 */
	ProblemBuilder withEpsilons(Epsilons epsilons) {
		this.epsilons = epsilons;
		return this;
	}
	
	/**
	 * Loads a reference set from file.
	 * 
	 * @param referenceSetFile the reference set file
	 * @return a reference to this builder
	 */
	ProblemBuilder withReferenceSet(File referenceSetFile) {
		try {
			return withReferenceSet(NondominatedPopulation.loadReferenceSet(referenceSetFile));
		} catch (IOException e) {
			throw new IllegalArgumentException("unable to load reference set", e);
		}
	}
	
	/**
	 * Configures a reference set to use for this problem.  If {@code null}, then the default reference set, if
	 * available, is used.
	 * 
	 * @param referenceSet the reference set
	 * @return a reference to this builder
	 */
	ProblemBuilder withReferenceSet(NondominatedPopulation referenceSet) {
		this.referenceSet = referenceSet;
		return this;
	}
	
	/**
	 * Returns an empty non-dominated population or &epsilon;-box dominance archive, depending on whether the
	 * {@code epsilon} field is set.  This is the archive used to store the reference set.
	 * 
	 * @return an empty non-dominated population or &epsilon;-box dominance archive, depending on whether the
	 *         {@code epsilon} field is set.
	 */
	NondominatedPopulation newArchive() {
		if (epsilons == null) {
			return new NondominatedPopulation(new ParetoDominanceComparator());
		} else {
			return new EpsilonBoxDominanceArchive(epsilons);
		}
	}
	
	/**
	 * Returns the reference set used by this builder.  The reference set is generated as follows:
	 * <ol>
	 *   <li>The custom reference set configured by {@link #withReferenceSet(NondominatedPopulation)} or
	 *       {@link #withReferenceSet(File)};
	 *   <li>The default reference set specified by the {@link ProblemFactory#getReferenceSet(String)} method;
	 *   <li>Otherwise, an exception is thrown.
	 * </ol>
	 * 
	 * @return the reference set used by this builder
	 * @throws IllegalArgumentException if no reference set is available or could not be loaded
	 */
	NondominatedPopulation getReferenceSet() {
		NondominatedPopulation result = newArchive();
		
		if (referenceSet == null) {
			//determine if the problem factory provides a reference set
			NondominatedPopulation factorySet = null;
			
			if (problemName != null) {
				if (problemFactory == null) {
					factorySet = ProblemFactory.getInstance().getReferenceSet(problemName);
				} else {
					factorySet = problemFactory.getReferenceSet(problemName);
				}
			}
			
			if (factorySet == null) {
				throw new IllegalArgumentException("no reference set available");
			}
			
			result.addAll(factorySet);
		} else {
			result.addAll(referenceSet);
		}
		
		return result;
	}
	
	/**
	 * Returns {@code true} if a problem has been configured.
	 * 
	 * @return {@code true} if a problem has been configured
	 */
	boolean isProblemConfigured() {
		return problemName != null || problemClass != null || problemInstance != null;
	}
	
	/**
	 * Returns a new instance of the problem used by this builder, or throws an exception if no problem has been
	 * defined.  The code requesting the problem instance is expected to close the problem when finished.
	 * 
	 * @return a new instance of the problem used by this builder, or throws an exception if no problem has been
	 *         defined
	 * @throws IllegalArgumentException if no problem has been defined
	 * @throws FrameworkException if an error occurred invoking the constructor caused by an
	 *         {@link InstantiationException}, 
	 *         {@link IllegalAccessException}, 
	 *         {@link InvocationTargetException} or
	 *         {@link NoSuchMethodException}.
	 */
	Problem getProblemInstance() {
		if (!isProblemConfigured()) {
			throw new IllegalArgumentException("no problem specified");
		}
		
		if (problemInstance != null) {
			return problemInstance;
		} else if (problemClass != null) {
			try {
				return (Problem)ConstructorUtils.invokeConstructor(problemClass, problemArguments);
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException |
					NoSuchMethodException e) {
				throw new FrameworkException(e);
			}
		} else if (problemFactory == null) {
			return ProblemFactory.getInstance().getProblem(problemName);
		} else {
			return problemFactory.getProblem(problemName);
		}
	}
	
	/**
	 * Internal problem wrapper used on user-defined problems (see {@link ProblemBuilder#withProblem(Problem)}) that
	 * does not automatically close the problem.
	 */
	private class UserProvidedProblemInstance extends ProblemWrapper {
		
		public UserProvidedProblemInstance(Problem problem) {
			super(problem);
		}

		@Override
		public void close() {
			//do nothing
		}
		
	}
	
}
