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

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

import org.apache.commons.lang3.tuple.Pair;
import org.moeaframework.core.Epsilons;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.spi.ProblemFactory;

/**
 * Provides &epsilon; values for algorithms using &epsilon;-dominance archives on the standard test problems.
 */
public class DefaultEpsilons {
	
	/**
	 * The default &epsilon; value that is returned for any problem without an explicitly configured value.
	 */
	public static final double DEFAULT = 0.01;
	
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
		
	private final List<Pair<Predicate<Problem>, Epsilons>> overrides;
	
	private DefaultEpsilons() {
		super();
		overrides = new LinkedList<>();
	}
	
	/**
	 * Clears any overrides.
	 */
	public void clearOverrides() {
		overrides.clear();
	}
	
	/**
	 * Returns the default &epsilon; values for the given problem.  If the problem is not recognized, the default
	 * &epsilon; value of {@value DEFAULT} is returned.
	 * 
	 * @param problem the problem
	 * @return the &epsilon; values
	 */
	public Epsilons getEpsilons(Problem problem) {
		Epsilons value = findOverride(problem);
		
		if (value != null) {
			if (Settings.isVerbose()) {
				System.err.println("Using user-provided epsilons for " + problem.getName() + ": " + value);
			}
			
			return value;
		}
		
		value = ProblemFactory.getInstance().getEpsilons(problem.getName());
		
		if (value != null) {
			if (Settings.isVerbose()) {
				System.err.println("Using problem-specific epsilons for " + problem.getName() + ": " + value);
			}
			
			return value;
		}
		
		if (Settings.isVerbose()) {
			System.err.println("Using default epsilons for " + problem.getName() + ": " + value);
		}
		
		return Epsilons.of(DEFAULT);
	}
	
	/**
	 * Finds the overridden &epsilon; values for the given problem, either one set by calling {@link #override} or
	 * from {@link Settings#getProblemSpecificEpsilons(String)}.
	 * 
	 * @param problem the problem
	 * @return the &epsilon; values, or {@code null} if no match was found
	 */
	protected Epsilons findOverride(Problem problem) {
		for (Pair<Predicate<Problem>, Epsilons> override : overrides) {
			if (override.getKey().test(problem)) {
				return override.getValue();
			}
		}
		
		return Settings.getProblemSpecificEpsilons(problem.getName());
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
		overrides.add(0, Pair.of(
				(p) -> p.getName().equalsIgnoreCase(problemName),
				epsilons));
	}

}
