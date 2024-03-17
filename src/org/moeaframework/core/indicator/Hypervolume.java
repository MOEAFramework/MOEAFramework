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
 * Hypervolume indicator. Represents the volume of objective space dominated by solutions in the approximation set.
 */
public class Hypervolume implements Indicator {
	
	private final Indicator instance;
	
	/**
	 * Constructs a hypervolume evaluator for the specified problem and reference set.
	 * 
	 * @param problem the problem
	 * @param referenceSet the reference set
	 */
	public Hypervolume(Problem problem, NondominatedPopulation referenceSet) {
		String selection = Settings.getHypervolume();
		
		if (selection != null) {
			if (selection.equalsIgnoreCase(PISAHypervolume.class.getSimpleName())) {
				instance = new PISAHypervolume(problem, referenceSet);
			} else if (selection.equalsIgnoreCase(WFGHypervolume.class.getSimpleName())) {
				instance = new WFGHypervolume(problem, referenceSet);
			} else if (selection.equalsIgnoreCase(WFGNormalizedHypervolume.class.getSimpleName())) {
				instance = new WFGNormalizedHypervolume(problem, referenceSet);
			} else {
				instance = new NativeHypervolume(problem, referenceSet);
			}
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
			if (selection.equalsIgnoreCase(PISAHypervolume.class.getSimpleName())) {
				instance = new PISAHypervolume(problem, referenceSet, referencePoint);
			} else if (selection.equalsIgnoreCase(WFGHypervolume.class.getSimpleName())) {
				instance = new WFGHypervolume(problem, referencePoint);
			} else if (selection.equalsIgnoreCase(WFGNormalizedHypervolume.class.getSimpleName())) {
				instance = new WFGNormalizedHypervolume(problem, referenceSet, referencePoint);
			} else {
				instance = new NativeHypervolume(problem, referenceSet, referencePoint);
			}
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
			if (selection.equalsIgnoreCase(PISAHypervolume.class.getSimpleName())) {
				instance = new PISAHypervolume(problem, minimum, maximum);
			} else if (selection.equalsIgnoreCase(WFGHypervolume.class.getSimpleName())) {
				instance = new WFGHypervolume(problem, maximum);
			} else if (selection.equalsIgnoreCase(WFGNormalizedHypervolume.class.getSimpleName())) {
				instance = new WFGNormalizedHypervolume(problem, minimum, maximum);
			} else {
				instance = new NativeHypervolume(problem, minimum, maximum);
			}
		} else {
			instance = new WFGNormalizedHypervolume(problem, minimum, maximum);
		}
	}
	
	@Override
	public double evaluate(NondominatedPopulation approximationSet) {
		return instance.evaluate(approximationSet);
	}

	/**
	 * Computes the hypervolume of a normalized approximation set.
	 * 
	 * @param problem the problem
	 * @param approximationSet the normalized approximation set
	 * @return the hypervolume value
	 */
	static double evaluate(Problem problem, NondominatedPopulation approximationSet) {
		String selection = Settings.getHypervolume();
		
		if (selection != null) {
			if (selection.equalsIgnoreCase(PISAHypervolume.class.getSimpleName())) {
				return PISAHypervolume.evaluate(problem, approximationSet);
			} else if (selection.equalsIgnoreCase(WFGHypervolume.class.getSimpleName()) ||
					selection.equalsIgnoreCase(WFGNormalizedHypervolume.class.getSimpleName())) {
				return WFGNormalizedHypervolume.evaluate(problem, approximationSet);
			} else {
				return NativeHypervolume.evaluate(problem, approximationSet);
			}
		} else {
			return WFGNormalizedHypervolume.evaluate(problem, approximationSet);
		}
	}
	
}
