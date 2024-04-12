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
import java.util.Set;
import java.util.TreeSet;

import org.moeaframework.core.Epsilons;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;

/**
 * Factory for creating optimization problem instances and their corresponding reference sets, if known.  Problems
 * are referenced by name.  For example, {@code getProblem("DTLZ2_2")} will create an instance of the 2-objective
 * DTLZ2 problem.  See {@link ProblemProvider} for details on adding new providers.
 * <p>
 * <a href="https://i.imgur.com/y41pi4n.jpg">Obligatory Link</a>
 * <p>
 * This class is thread safe.
 */
public class ProblemFactory extends AbstractFactory<ProblemProvider> {
	
	/**
	 * The default problem factory.
	 */
	private static ProblemFactory INSTANCE;
	
	/**
	 * Instantiates the static {@code INSTANCE} object.
	 */
	static {
		INSTANCE = new ProblemFactory();
	}
	
	/**
	 * Returns the default problem factory.
	 * 
	 * @return the default problem factory
	 */
	public static synchronized ProblemFactory getInstance() {
		return INSTANCE;
	}

	/**
	 * Sets the default problem factory.
	 * 
	 * @param instance the default problem factory
	 */
	public static synchronized void setInstance(ProblemFactory instance) {
		ProblemFactory.INSTANCE = instance;
	}
	
	/**
	 * Constructs a new problem factory.
	 */
	public ProblemFactory() {
		super(ProblemProvider.class);
	}
	
	/**
	 * Searches through all discovered {@code ProblemProvider} instances, returning an instance of the problem with
	 * the registered name.  This method must throw an {@link ProviderNotFoundException} if no matching problem is
	 * found.
	 * 
	 * @param name the name identifying the problem
	 * @return an instance of the problem with the registered name
	 * @throws ProviderNotFoundException if no provider for the problem is available
	 */
	public synchronized Problem getProblem(String name) {
		for (ProblemProvider provider : this) {
			Problem problem = instantiateProblem(provider, name);
			
			if (problem != null) {
				return problem;
			}
		}

		throw new ProviderNotFoundException(name);
	}
	
	/**
	 * Attempts to instantiate the given problem using the given provider.
	 * 
	 * @param provider the problem provider
	 * @param name the name identifying the problem
	 * @return an instance of the problem with the registered name; or {@code null} if the provider does not implement
	 *         the problem
	 */
	private Problem instantiateProblem(ProblemProvider provider, String name) {
		try {
			return provider.getProblem(name);
		} catch (ServiceConfigurationError e) {
			System.err.println(e.getMessage());
		}
		
		return null;
	}

	/**
	 * Searches through all discovered {@code ProblemProvider} instances, returning the reference set of the problem
	 * with the registered name.  Returns {@code null} if no reference set is available for the specified problem.
	 * 
	 * @param name the name identifying the problem
	 * @return the reference set of the problem with the registered name; or {@code null} if no reference set is
	 *         available
	 */
	public synchronized NondominatedPopulation getReferenceSet(String name) {
		for (ProblemProvider provider : this) {
			NondominatedPopulation referenceSet = provider.getReferenceSet(name);

			if (referenceSet != null) {
				return referenceSet;
			}
		}

		return null;
	}
	
	/**
	 * Searches through all discovered {@code ProblemProvider} instances, returning the &epsilon; values of the problem
	 * with the registered name.  Returns {@code null} if no values are available for the specified problem.
	 * 
	 * @param name the name identifying the problem
	 * @return the &epsilon; values of the problem with the registered name; or {@code null} if no values are available
	 */
	public synchronized Epsilons getEpsilons(String name) {
		for (ProblemProvider provider : this) {
			Epsilons epsilons = provider.getEpsilons(name);
			
			if (epsilons != null) {
				return epsilons;
			}
		}
		
		return null;
	}
	
	/**
	 * Returns the names of all problems that have been registered by the providers.  Note that this does not include
	 * a comprehensive list of all problems that can be constructed, only those that have been registered explicitly
	 * with a name.
	 * 
	 * @return all registered problem names
	 */
	public synchronized Set<String> getAllRegisteredProblems() {
		Set<String> result = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		
		for (ProblemProvider provider : this) {
			if (provider instanceof RegisteredProblemProvider registeredProvider) {
				result.addAll(registeredProvider.getRegisteredProblems());
			}
		}
		
		return result;
	}
	
	/**
	 * Returns the names of all problems that have been registered to display in the diagnostic tool.
	 * 
	 * @return all diagnostic tool problem names
	 */
	public synchronized Set<String> getAllDiagnosticToolProblems() {
		Set<String> result = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		
		for (ProblemProvider provider : this) {
			result.addAll(provider.getDiagnosticToolProblems());
		}
		
		return result;
	}

}
