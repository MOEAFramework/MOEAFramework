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

import java.io.File;
import java.io.IOException;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.spi.ProblemProvider;
import org.moeaframework.core.spi.ProviderNotFoundException;

/**
 * Legacy problem provider that loads problems defined in {@value Settings#DEFAULT_CONFIGURATION_FILE}.
 * For example, we could add the following to the properties file:
 * <pre>
 *   org.moeaframework.problem.TestLZ1.class = org.moeaframework.problem.LZ.LZ1
 *   org.moeaframework.problem.TestLZ1.referenceSet = ./pf/LZ09_F1.pf
 * </pre>
 * And then instantiate the problem with:
 * <pre> 
 *   ProblemFactory.getInstance().getProblem("TestLZ1");
 * </pre>
 * Defining problems this way is no longer recommended.  Instead, we recommend using a
 * {@code RegisteredProblemProvider} to register new problems with this framework.
 */
public class PropertiesProblems extends ProblemProvider {
	
	/**
	 * Constructs the problem provider for problems enumerated in {@value Settings#DEFAULT_CONFIGURATION_FILE}.
	 */
	public PropertiesProblems() {
		super();
	}

	@Override
	public Problem getProblem(String name) {
		if (name != null) {
			String className = Settings.getProblemClass(name);
			
			if (className != null) {
				try {
					return (Problem)Class.forName(className).getConstructor().newInstance();
				} catch (Exception e) {
					throw new ProviderNotFoundException(name, e);
				}
			}
		}

		return null;
	}

	@Override
	public NondominatedPopulation getReferenceSet(String name) {
		if (name != null) {
			String fileName = Settings.getProblemReferenceSet(name);
			
			if (fileName != null) {
				try {
					return NondominatedPopulation.loadReferenceSet(new File(fileName));
				} catch (IOException e) {
					return null;
				}
			}
		}

		return null;
	}

}
