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

import org.moeaframework.core.Indicator;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;

/**
 * Hypervolume indicator.  Represents the volume of objective space dominated by solutions in the approximation set.
 * The hypervolume implementation can be configured using the {@link Settings#getHypervolume()}.
 */
public class Hypervolume implements Indicator {
	
	final Indicator instance;
	
	/**
	 * Constructs a hypervolume evaluator for the specified problem and reference set.  See
	 * {@link #getNormalizer(Problem, NondominatedPopulation)} for details on configuring normalization.
	 * 
	 * @param problem the problem
	 * @param referenceSet the reference set
	 */
	public Hypervolume(Problem problem, NondominatedPopulation referenceSet) {
		String selection = Settings.getHypervolume();
		
		if (selection != null) {
			instance = switch (selection.toLowerCase()) {
				case "pisa" -> new PISAHypervolume(problem, referenceSet);
				case "wfg" -> new WFGNormalizedHypervolume(problem, referenceSet);
				default -> new NativeHypervolume(problem, referenceSet);
			};
		} else {
			instance = new WFGNormalizedHypervolume(problem, referenceSet);
		}
	}
	
	/**
	 * Constructs a hypervolume evaluator for the specified problem using the given reference set and reference point.
	 * 
	 * @param problem the problem
	 * @param referenceSet the reference set
	 * @param referencePoint the reference point
	 */
	public Hypervolume(Problem problem, NondominatedPopulation referenceSet, double[] referencePoint) {
		String selection = Settings.getHypervolume();
		
		if (selection != null) {
			instance = switch (selection.toLowerCase()) {
				case "pisa" -> new PISAHypervolume(problem, referenceSet, referencePoint);
				case "wfg" -> new WFGNormalizedHypervolume(problem, referenceSet, referencePoint);
				default -> new NativeHypervolume(problem, referenceSet, referencePoint);
			};
		} else {
			instance = new WFGNormalizedHypervolume(problem, referenceSet, referencePoint);
		}
	}

	/**
	 * Constructs a hypervolume evaluator for the specified problem using the given minimum and maximum bounds.
	 * 
	 * @param problem the problem
	 * @param minimum the minimum bounds of the set
	 * @param maximum the maximum bounds of the set
	 */
	public Hypervolume(Problem problem, double[] minimum, double[] maximum) {
		String selection = Settings.getHypervolume();
		
		if (selection != null) {
			instance = switch (selection.toLowerCase()) {
				case "pisa" -> new PISAHypervolume(problem, minimum, maximum);
				case "wfg" -> new WFGNormalizedHypervolume(problem, minimum, maximum);
				default -> new NativeHypervolume(problem, minimum, maximum);
			};
		} else {
			instance = new WFGNormalizedHypervolume(problem, minimum, maximum);
		}
	}
	
	@Override
	public double evaluate(NondominatedPopulation approximationSet) {
		return instance.evaluate(approximationSet);
	}
	
	/**
	 * Returns the normalizer for calculating hypervolume using the following rules:
	 * <ol>
	 *   <li>Normalize using the {@code idealpt} and {@code refpt} settings
	 *   <li>Normalize using the {@code refpt} setting, with the ideal point based on the reference set
	 *   <li>Normalize using the bounds of the reference set plus the configured hypervolume delta
	 * </ol>
	 * 
	 * @param problem the problem
	 * @param referenceSet the reference set
	 * @return the normalize for calculating hypervolume
	 * @see Settings#getIdealPoint(String)
	 * @see Settings#getReferencePoint(String)
	 * @see Settings#getHypervolumeDelta()
	 */
	static Normalizer getNormalizer(Problem problem, NondominatedPopulation referenceSet) {
		double[] idealPoint = Settings.getIdealPoint(problem.getName());
		double[] referencePoint = Settings.getReferencePoint(problem.getName());
		
		if ((idealPoint != null) && (referencePoint != null)) {
			return new Normalizer(problem, idealPoint, referencePoint);
		} else if (referencePoint != null) {
			return new Normalizer(problem, referenceSet, referencePoint);
		} else {
			return new Normalizer(problem, referenceSet, Settings.getHypervolumeDelta());
		}
	}
	
}
