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

import java.io.File;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PopulationIO;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.spi.ProblemProvider;
import org.moeaframework.core.spi.ProviderNotFoundException;

/**
 * Problem provider for problems enumerated in {@code global.properties}.
 * The problems are identified by name as listed in the {@code
 * org.moeaframework.problem.problems} property, with the class and optional
 * reference set defined by the {@code org.moeaframework.problem.NAME.class}
 * and {@code org.moeaframework.problem.NAME.referenceSet} properties.
 * Problems instantiated this way must provide an empty constructor.
 */
public class PropertiesProblems extends ProblemProvider {
	
	/**
	 * Constructs the problem provider for problems enumerated in {@code
	 * global.properties}.
	 */
	public PropertiesProblems() {
		super();
	}
	
	/**
	 * Returns the case-sensitive version of the problem name.  If the problem
	 * name was not specifically listed in the
	 * {@code org.moeaframework.problem.problems} property, {@code name} is
	 * returned unchanged.
	 * 
	 * @param name the case-insensitive name
	 * @return the case-sensitive name
	 */
	protected String getCaseSensitiveProblemName(String name) {
		for (String problem : Settings.getProblems()) {
			if (problem.equalsIgnoreCase(name)) {
				return problem;
			}
		}
		
		return name;
	}

	@Override
	public Problem getProblem(String name) {
		name = getCaseSensitiveProblemName(name);
		
		if (name != null) {
			String className = Settings.getProblemClass(name);
			
			if (className != null) {
				try {
					return (Problem)Class.forName(className).newInstance();
				} catch (Exception e) {
					throw new ProviderNotFoundException(name, e);
				}
			}
		}

		return null;
	}

	@Override
	public NondominatedPopulation getReferenceSet(String name) {
		name = getCaseSensitiveProblemName(name);
		
		if (name != null) {
			String fileName = Settings.getProblemReferenceSet(name);
			
			if (fileName != null) {
				try {
					return new NondominatedPopulation(
							PopulationIO.readObjectives(new File(fileName)));
				} catch (Exception e) {
					return null;
				}
			}
		}

		return null;
	}

}
