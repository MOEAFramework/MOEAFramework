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
package org.moeaframework.analysis;

import java.util.Map.Entry;
import java.util.TreeMap;

import org.moeaframework.core.Epsilons;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.util.format.Column;
import org.moeaframework.util.format.Formattable;
import org.moeaframework.util.format.TabularData;

/**
 * Provides &epsilon; values for algorithms and archives using &epsilon;-dominance.  The search order is:
 * <ol>
 *   <li>Epsilons configured by the user by calling {@link #override}
 *   <li>Epsilons configured by the user in the properties file {@value Settings#DEFAULT_CONFIGURATION_FILE}
 *   <li>Epsilons defined by the problem provider (i.e., our recommended defaults)
 *   <li>The global default of {@link #DEFAULT}
 * </ol>
 */
public class DefaultEpsilons implements Formattable<Entry<String, Epsilons>> {
	
	/**
	 * The default &epsilon; value that is returned for any problem without an explicitly configured value.
	 */
	public static final Epsilons DEFAULT = Epsilons.of(0.01);
	
	private static DefaultEpsilons INSTANCE;
	
	static {
		INSTANCE = new DefaultEpsilons();
	}
	
	/**
	 * Returns the default &epsilon; provider.
	 * 
	 * @return the default &epsilon; provider
	 */
	public static synchronized DefaultEpsilons getInstance() {
		return INSTANCE;
	}
	
	/**
	 * Sets the default &epsilon; provider.
	 * 
	 * @param instance default &epsilon; provider
	 */
	public static synchronized void setInstance(DefaultEpsilons instance) {
		INSTANCE = instance;
	}
		
	private final TreeMap<String, Epsilons> overrides;
	
	private DefaultEpsilons() {
		super();
		overrides = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
	}
	
	/**
	 * Clears any overrides.
	 */
	public void clearOverrides() {
		overrides.clear();
	}
	
	/**
	 * Returns the default &epsilon; values for the given problem.  If the problem is not recognized, the default
	 * &epsilon; value of {@link #DEFAULT} is returned.
	 * 
	 * @param problem the problem
	 * @return the &epsilon; values
	 */
	public Epsilons getEpsilons(Problem problem) {
		return getEpsilons(problem.getName());
	}
	
	/**
	 * Returns the default &epsilon; values for the given problem.  If the problem is not recognized, the default
	 * &epsilon; value of {@link #DEFAULT} is returned.
	 * 
	 * @param problemName the problem name
	 * @return the &epsilon; values
	 */
	public Epsilons getEpsilons(String problemName) {
		Epsilons value = findOverride(problemName);
		
		if (value != null) {
			if (Settings.isVerbose()) {
				System.err.println("Using user-provided epsilons for " + problemName + ": " + value);
			}
			
			return value;
		}
		
		value = ProblemFactory.getInstance().getEpsilons(problemName);
		
		if (value != null) {
			if (Settings.isVerbose()) {
				System.err.println("Using problem-specific epsilons for " + problemName + ": " + value);
			}
			
			return value;
		}
		
		if (Settings.isVerbose()) {
			System.err.println("Using default epsilons for " + problemName + ": " + value);
		}
		
		return DEFAULT;
	}
	
	/**
	 * Finds the overridden &epsilon; values for the given problem, either one set by calling {@link #override} or
	 * from {@link Settings#getProblemSpecificEpsilons(String)}.
	 * 
	 * @param problemName the problem name
	 * @return the &epsilon; values, or {@code null} if no match was found
	 */
	protected Epsilons findOverride(String problemName) {
		Epsilons epsilons = overrides.get(problemName);
		
		if (epsilons == null) {
			epsilons = Settings.getProblemSpecificEpsilons(problemName);
		}
		
		return epsilons;
	}	
	
	/**
	 * Overrides the &epsilon; value for the given problem.  This expects the problem name to uniquely determine the
	 * &epsilon; values, typically implying problems with varying numbers of objectives provide distinct names.
	 * 
	 * @param problem the problem
	 * @param epsilons the &epsilon; values
	 */
	public void override(Problem problem, Epsilons epsilons) {
		override(problem.getName(), epsilons);
	}
	
	/**
	 * Overrides the &epsilon; value for the given problem name.  This expects the problem name to uniquely determine
	 * the &epsilon; values, typically implying problems with varying numbers of objectives provide distinct names.
	 * 
	 * @param problemName the problem name
	 * @param epsilons the &epsilon; values
	 */
	public void override(String problemName, Epsilons epsilons) {
		overrides.put(problemName, epsilons);
	}

	@Override
	public TabularData<Entry<String, Epsilons>> asTabularData() {
		TreeMap<String, Epsilons> orderedData = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		
		for (String problemName : ProblemFactory.getInstance().getAllRegisteredProblems()) {
			orderedData.put(problemName, DefaultEpsilons.getInstance().getEpsilons(problemName));
		}
		
		TabularData<Entry<String, Epsilons>> result = new TabularData<>(orderedData.entrySet());
		
		result.addColumn(new Column<Entry<String, Epsilons>, String>("Problem", x -> x.getKey()));
		result.addColumn(new Column<Entry<String, Epsilons>, Epsilons>("Epsilons", x -> x.getValue()));
		result.addColumn(new Column<Entry<String, Epsilons>, Boolean>("IsOverridden", x -> overrides.containsKey(x.getKey())));
		
		return result;
	}

}
