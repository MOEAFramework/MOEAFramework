/* Copyright 2009-2014 David Hadka
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

import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;

/**
 * Factory for creating optimization problem instances and their corresponding
 * reference sets, if known. See {@link ProblemProvider} for details on adding
 * new providers.
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
		Iterator<ProblemProvider> ps = PROVIDERS.iterator();

		while (ps.hasNext()) {
			try {
				ProblemProvider provider = ps.next();
				Problem problem = provider.getProblem(name);
	
				if (problem != null) {
					return problem;
				}
			} catch (ServiceConfigurationError e) {
				System.err.println(e.getMessage());
			}
		}

		throw new ProviderNotFoundException(name);
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
	public synchronized NondominatedPopulation getReferenceSet(
			String name) {
		Iterator<ProblemProvider> ps = PROVIDERS.iterator();

		while (ps.hasNext()) {
			ProblemProvider provider = ps.next();
			NondominatedPopulation referenceSet = provider
					.getReferenceSet(name);

			if (referenceSet != null) {
				return referenceSet;
			}
		}

		return null;
	}

}
