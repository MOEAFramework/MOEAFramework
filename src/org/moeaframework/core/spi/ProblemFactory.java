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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.problem.ClassLoaderProblems;
import org.moeaframework.problem.PropertiesProblems;
import org.moeaframework.problem.RotatedProblems;
import org.moeaframework.problem.StandardProblems;

/**
 * Factory for creating optimization problem instances and their corresponding
 * reference sets, if known.  Problems are referenced by name.  For example,
 * {@code getProblem("DTLZ2_2")} will create an instance of the 2-objective
 * DTLZ2 problem.  See {@link ProblemProvider} for details on adding
 * new providers.
 * <p>
 * <a href="https://i.imgur.com/y41pi4n.jpg">Obligatory Link</a>
 * <p>
 * This class is thread safe.
 */
public class ProblemFactory {
	
	/**
	 * The static service loader for loading problem providers.
	 */
	private static final ServiceLoader<ProblemProvider> PROVIDERS;
	
	/**
	 * The default problem factory.
	 */
	private static ProblemFactory instance;
	
	/**
	 * Collection of providers that have been manually added.
	 */
	private List<ProblemProvider> customProviders;
	
	/**
	 * Instantiates the static {@code PROVIDERS} and {@code instance} objects.
	 */
	static {
		PROVIDERS = ServiceLoader.load(ProblemProvider.class);
		instance = new ProblemFactory();
	}
	
	/**
	 * Returns the default problem factory.
	 * 
	 * @return the default problem factory
	 */
	public static synchronized ProblemFactory getInstance() {
		return instance;
	}

	/**
	 * Sets the default problem factory.
	 * 
	 * @param instance the default problem factory
	 */
	public static synchronized void setInstance(ProblemFactory instance) {
		ProblemFactory.instance = instance;
	}
	
	/**
	 * Constructs a new problem factory.
	 */
	public ProblemFactory() {
		super();
		
		customProviders = new ArrayList<ProblemProvider>();
	}
	
	/**
	 * Adds a problem provider to this problem factory.  Subsequent calls
	 * to {@link #getProblem(String)} or {@link #getReferenceSet(String)} will
	 * search the given provider for a match.
	 * 
	 * @param provider the new problem provider
	 */
	public void addProvider(ProblemProvider provider) {
		customProviders.add(provider);
	}

	/**
	 * Searches through all discovered {@code ProblemProvider} instances,
	 * returning an instance of the problem with the registered name.  This
	 * method must throw an {@link ProviderNotFoundException} if no matching
	 * problem is found.
	 * 
	 * @param name the name identifying the problem
	 * @return an instance of the problem with the registered name
	 * @throws ProviderNotFoundException if no provider for the problem is 
	 *         available
	 */
	public synchronized Problem getProblem(String name) {
		// loop over all providers that have been manually added
		for (ProblemProvider provider : customProviders) {
			Problem problem = instantiateProblem(provider, name);
			
			if (problem != null) {
				return problem;
			}
		}
		
		// loop over all providers available via the SPI
		Iterator<ProblemProvider> iterator = PROVIDERS.iterator();
		
		while (iterator.hasNext()) {
			Problem problem = instantiateProblem(iterator.next(), name);
			
			if (problem != null) {
				return problem;
			}
		}
		
		// ensure standard problems can always be found
		Problem problem = instantiateProblem(new StandardProblems(), name);
			
		if (problem == null) {
			problem = instantiateProblem(new PropertiesProblems(), name);
		}
			
		if (problem == null) {
			problem = instantiateProblem(new ClassLoaderProblems(), name);
		}
			
		if (problem == null) {
			problem = instantiateProblem(new RotatedProblems(), name);
		}
			
		if (problem != null) {
			return problem;
		}

		// throw an exception if no match found
		throw new ProviderNotFoundException(name);
	}
	
	/**
	 * Attempts to instantiate the given problem using the given provider.
	 * 
	 * @param provider the problem provider
	 * @param name the name identifying the problem
	 * @return an instance of the problem with the registered name; or
	 *         {@code null} if the provider does not implement the problem
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
	 * Searches through all discovered {@code ProblemProvider} instances,
	 * returning the reference set of the problem with the registered name.
	 * Returns {@code null} if no reference set is available for the specified 
	 * problem.
	 * 
	 * @param name the name identifying the problem
	 * @return the reference set of the problem with the registered name; or
	 *         {@code null} if no reference set is available
	 */
	public synchronized NondominatedPopulation getReferenceSet(String name) {
		// loop over all providers that have been manually added
		for (ProblemProvider provider : customProviders) {
			NondominatedPopulation referenceSet =
					provider.getReferenceSet(name);
					
			if (referenceSet != null) {
				return referenceSet;
			}
		}
		
		// loop over all providers available via the SPI
		Iterator<ProblemProvider> iterator = PROVIDERS.iterator();
		
		while (iterator.hasNext()) {
			ProblemProvider provider = iterator.next();
			NondominatedPopulation referenceSet =
					provider.getReferenceSet(name);

			if (referenceSet != null) {
				return referenceSet;
			}
		}
		
		// ensure standard problems can always be found
		NondominatedPopulation referenceSet =
				new StandardProblems().getReferenceSet(name);

		if (referenceSet == null) {
			referenceSet = new PropertiesProblems().getReferenceSet(name);
		}
			
		if (referenceSet == null) {
			referenceSet = new ClassLoaderProblems().getReferenceSet(name);
		}
			
		if (referenceSet == null) {
			referenceSet = new RotatedProblems().getReferenceSet(name);
		}
			
		if (referenceSet != null) {
			return referenceSet;
		}

		// return null if no match is found
		return null;
	}

}
