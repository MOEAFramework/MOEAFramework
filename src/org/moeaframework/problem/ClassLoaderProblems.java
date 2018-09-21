/* Copyright 2009-2018 David Hadka
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
package org.moeaframework.problem;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.spi.ProblemProvider;
import org.moeaframework.core.spi.ProviderNotFoundException;

/**
 * Problem provider for problems accessible through the current class loader.
 * The name of the problem should be the fully-qualified class name (including
 * the containing package, if any).  Problems instantiated this way must provide
 * an empty constructor.  No reference sets are provided; see 
 * {@link PropertiesProblems} for a way to define problems with reference sets.
 */
public class ClassLoaderProblems extends ProblemProvider {

	@Override
	public Problem getProblem(String name) {
		try {
			return (Problem)Class.forName(name).newInstance();
		} catch (ClassNotFoundException e) {
			return null;
		} catch (Exception e) {
			throw new ProviderNotFoundException(name, e);
		}
	}

	@Override
	public NondominatedPopulation getReferenceSet(String name) {
		return null;
	}

}
