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
package org.moeaframework.problem;

import java.lang.reflect.InvocationTargetException;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.spi.ProblemProvider;
import org.moeaframework.core.spi.ProviderNotFoundException;

/**
 * Legacy problem provider that locates problems using their fully-qualified class name.  For example:
 * <pre>
 *   ProblemFactory.getInstance().getProblem("org.moeaframework.problem.LZ.LZ1");
 * </pre>
 * There are a few limitations of this provider, including:
 * <ol>
 *   <li>Problems must provide an empty constructor; and
 *   <li>A reference set can not be defined, so any analysis will need an explicit reference set
 * </ol>
 * Defining problems this way is no longer recommended.  Instead, we recommend using a
 * {@code RegisteredProblemProvider} to register new problems with this framework.
 */
public class ClassLoaderProblems extends ProblemProvider {
	
	/**
	 * Constructs the problem provider that locates problems using their fully-qualified class name.
	 */
	public ClassLoaderProblems() {
		super();
	}

	@Override
	public Problem getProblem(String name) {
		try {
			return (Problem)Class.forName(name).getConstructor().newInstance();
		} catch (ClassNotFoundException e) {
			return null;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new ProviderNotFoundException(name, e);
		}
	}

	@Override
	public NondominatedPopulation getReferenceSet(String name) {
		return null;
	}

}
