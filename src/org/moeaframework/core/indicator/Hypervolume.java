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
	 * Constructs a hypervolume evaluator for the specified problem and reference set.  See {@link DefaultNormalizer}
	 * for details on configuring normalization.
	 * 
	 * @param problem the problem
	 * @param referenceSet the reference set
	 */
	public Hypervolume(Problem problem, NondominatedPopulation referenceSet) {
		this(problem, DefaultNormalizer.getInstance().getHypervolumeNormalizer(problem, referenceSet));
	}
	
	/**
	 * Constructs a hypervolume evaluator for the specified problem using the given reference set and reference point.
	 * 
	 * @param problem the problem
	 * @param referenceSet the reference set
	 * @param referencePoint the reference point
	 */
	public Hypervolume(Problem problem, NondominatedPopulation referenceSet, double[] referencePoint) {
		this(problem, new Normalizer(problem, referenceSet, referencePoint));
	}

	/**
	 * Constructs a hypervolume evaluator for the specified problem using the given minimum and maximum bounds.
	 * 
	 * @param problem the problem
	 * @param minimum the minimum bounds of the set
	 * @param maximum the maximum bounds of the set
	 */
	public Hypervolume(Problem problem, double[] minimum, double[] maximum) {
		this(problem, new Normalizer(problem, minimum, maximum));
	}
	
	/**
	 * Constructs a hypervolume evaluator with a user-provided normalizer.
	 * 
	 * @param problem the problem
	 * @param normalizer the user-provided normalizer
	 */
	public Hypervolume(Problem problem, Normalizer normalizer) {
		String selection = Settings.getHypervolume();
		
		if (selection != null) {
			instance = switch (selection.toLowerCase()) {
				case "pisa" -> new PISAHypervolume(problem, normalizer);
				case "wfg" -> new WFGNormalizedHypervolume(problem, normalizer);
				default -> new NativeHypervolume(problem, normalizer);
			};
		} else {
			instance = new WFGNormalizedHypervolume(problem, normalizer);
		}
	}
	
	@Override
	public double evaluate(NondominatedPopulation approximationSet) {
		return instance.evaluate(approximationSet);
	}
	
}
