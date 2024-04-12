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
package org.moeaframework.core.indicator;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.commons.lang3.tuple.Pair;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.util.Vector;

/**
 * Class for constructing normalizers, allowing users to override defaults for specific problems.  The search order is:
 * <ol>
 *   <li>A normalizer configured by the user by calling {@link #override}
 *   <li>A normalizer configured by the user in the properties file {@value Settings#DEFAULT_CONFIGURATION_FILE}
 *   <li>The default normalizer computed from the provided reference set
 * </ol>
 * If using the properties file, set the following two values:
 * <pre>
 *   org.moeaframework.problem.NAME.normalization.minimum = 0,0,0
 *   org.moeaframework.problem.NAME.normalization.maximum = 1,1,1
 * </pre>
 * Normalization can also be disabled on specific problems by setting:
 * <pre>
 *   org.moeaframework.problem.NAME.normalization.disabled = true
 * </pre>
 * In previous versions, hypervolume supported configuring an ideal and and reference point, which are analogous to the
 * new minimum and maximum settings.  These older settings are still supported.  Additionally, hypervolume can also be
 * configured using either a problem-specific or a global delta:
 * <pre>
 *   org.moeaframework.problem.NAME.normalization.delta = 0.2
 *   org.moeaframework.core.indicator.hypervolume.delta = 0.2
 * </pre>
 */
public class DefaultNormalizer {
	
	private static DefaultNormalizer INSTANCE;
	
	static {
		INSTANCE = new DefaultNormalizer();
	}
	
	/**
	 * Returns the default normalizer used when computing performance indicators.
	 * 
	 * @return the default normalizer
	 */
	public static synchronized DefaultNormalizer getInstance() {
		return INSTANCE;
	}
	
	/**
	 * Sets the default normalizer used when computing performance indicators.
	 * 
	 * @param instance the default normalizer
	 */
	public static synchronized void setInstance(DefaultNormalizer instance) {
		INSTANCE = instance;
	}
	
	private final List<Pair<Predicate<Problem>, Function<Problem, Normalizer>>> overrides;

	private DefaultNormalizer() {
		super();
		overrides = new LinkedList<>();
	}
	
	/**
	 * Removes any overrides.
	 */
	public void clearOverrides() {
		overrides.clear();
	}
	
	/**
	 * Returns the normalizer configured for the given problem and reference set.
	 * 
	 * @param problem the problem
	 * @param referenceSet the reference set
	 * @return the normalizer
	 */
	public Normalizer getNormalizer(Problem problem, NondominatedPopulation referenceSet) {
		Normalizer normalizer = findOverride(problem);
		
		if (normalizer != null) {
			if (Settings.isVerbose()) {
				if (normalizer instanceof NullNormalizer) {
					System.err.println("Normalization is disabled by user for " + problem.getName());
				} else {
					System.err.println("Using user-provided normalizer for " + problem.getName());
				}
			}
			
			return normalizer;
		}
		
		return new Normalizer(problem, referenceSet);
	}
	
	/**
	 * Returns the normalizer used for hypervolume calculations for the given problem and reference set.
	 * 
	 * @param problem the problem
	 * @param referenceSet the reference set
	 * @return the hypervolume normalizer
	 */
	public Normalizer getHypervolumeNormalizer(Problem problem, NondominatedPopulation referenceSet) {
		Normalizer normalizer = findOverride(problem);
		
		if (normalizer != null) {
			if (Settings.isVerbose()) {
				if (normalizer instanceof NullNormalizer) {
					System.err.println("Normalization is disabled by user for " + problem.getName());
				} else {
					System.err.println("Using user-provided normalizer for " + problem.getName());
				}
			}
			
			return normalizer;
		}
		
		// Hypervolume-specific options.  The call to findOverride already handles the case when the minimum (ideal)
		// and maximum (reference) points are both given.
		double[] maximum = Settings.getProblemSpecificMaximumBounds(problem.getName());

		if (maximum != null) {
			if (Settings.isVerbose()) {
				System.err.println("Using custom normalizer based on reference / maximum point for " +
						problem.getName());
			}
			
			return new Normalizer(problem, referenceSet, maximum);
		}
		
		return new Normalizer(problem, referenceSet, Settings.getProblemSpecificHypervolumeDelta(problem.getName()));
	}
	
	/**
	 * Locates the overridden normalizer, if one has been configured.
	 * 
	 * @param problem the problem
	 * @return the overridden normalizer, or {@code null} if no override was found
	 */
	private Normalizer findOverride(Problem problem) {
		for (Pair<Predicate<Problem>, Function<Problem, Normalizer>> override : overrides) {
			if (override.getKey().test(problem)) {
				Normalizer normalizer = override.getValue().apply(problem);
				
				if (normalizer != null) {					
					return normalizer;
				}
			}
		}
		
		if (Settings.isNormalizationDisabled(problem.getName())) {
			return new NullNormalizer(problem);
		}
		
		double[] minimum = Settings.getProblemSpecificMinimumBounds(problem.getName());
		double[] maximum = Settings.getProblemSpecificMaximumBounds(problem.getName());

		if (minimum != null && maximum != null) {
			return new Normalizer(problem, minimum, maximum);
		}
		
		return null;
	}
	
	/**
	 * Provides specific minimum and maximum bounds used for normalizing the given problem.
	 * 
	 * @param problem the problem
	 * @param minimum the minimum bounds, the last value will be repeated if the problem has more objectives
	 * @param maximum the maximum bounds, the last value will be repeated if the problem has more objectives
	 */
	public void override(Problem problem, double[] minimum, double[] maximum) {
		override(problem.getName(), minimum, maximum);
	}
	
	/**
	 * Provides specific minimum and maximum bounds used for normalizing the given problem.
	 * 
	 * @param problemName the problem name
	 * @param minimum the minimum bounds, the last value will be repeated if the problem has more objectives
	 * @param maximum the maximum bounds, the last value will be repeated if the problem has more objectives
	 */
	public void override(String problemName, double[] minimum, double[] maximum) {
		overrides.add(0, Pair.of(
				(p) -> p.getName().equalsIgnoreCase(problemName),
				(p) -> new Normalizer(p, minimum, maximum)));
	}

	/**
	 * Disables normalization for the given problem.  The given name must match the value of {@link Problem#getName()}.
	 * 
	 * @param problem the problem
	 */
	public void disableNormalization(Problem problem) {
		disableNormalization(problem.getName());
	}
	
	/**
	 * Disables normalization for the given problem.  The given name must match the value of {@link Problem#getName()}.
	 * 
	 * @param problemName the problem name
	 */
	public void disableNormalization(String problemName) {
		overrides.add(0, Pair.of(
				(p) -> p.getName().equalsIgnoreCase(problemName),
				(p) -> new NullNormalizer(p)));
	}
	
	/**
	 * The "null" normalizer, which is used to disable normalization.  The {@link #normalize} methods simply return
	 * the provided population unchanged.
	 */
	private static class NullNormalizer extends Normalizer {

		public NullNormalizer(Problem problem) {
			super(problem,
					// The minimum / maximum bounds provided below will be ignored
					Vector.of(problem.getNumberOfObjectives(), 0.0),
					Vector.of(problem.getNumberOfObjectives(), 1.0));
		}

		@Override
		public NondominatedPopulation normalize(NondominatedPopulation population) {
			return population;
		}

		@Override
		public Population normalize(Population population) {
			return population;
		}
		
	}

}
