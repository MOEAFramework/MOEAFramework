/* Copyright 2009-2022 David Hadka
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
 * Methods for calculating the hypervolume using different implementations.
 */
public class Hypervolume {
	
	private Hypervolume() {
		super();
	}
	
	public static Indicator getInstance(Problem problem, NondominatedPopulation referenceSet) {
		if (Settings.getHypervolume() != null) {
			if (Settings.getHypervolume().equalsIgnoreCase("PISA")) {
				return new PISAHypervolume(problem, referenceSet);
			} else {
				return new NativeHypervolume(problem, referenceSet);
			}
		} else {
			return new WFGNormalizedHypervolume(problem, referenceSet);
		}
	}
	
	public static Indicator getInstance(Problem problem, NondominatedPopulation referenceSet, double[] referencePoint) {
		if (Settings.getHypervolume() != null) {
			if (Settings.getHypervolume().equalsIgnoreCase("PISA")) {
				return new PISAHypervolume(problem, referenceSet, referencePoint);
			} else {
				return new NativeHypervolume(problem, referenceSet, referencePoint);
			}
		} else {
			return new WFGNormalizedHypervolume(problem, referenceSet, referencePoint);
		}
	}

	public static Indicator getInstance(Problem problem, double[] minimum, double[] maximum) {
		if (Settings.getHypervolume() != null) {
			if (Settings.getHypervolume().equalsIgnoreCase("PISA")) {
				return new PISAHypervolume(problem, minimum, maximum);
			} else {
				return new NativeHypervolume(problem, minimum, maximum);
			}
		} else {
			return new WFGNormalizedHypervolume(problem, minimum, maximum);
		}
	}
	
	/**
	 * Computes the hypervolume of a normalized approximation set.
	 * 
	 * @param problem the problem
	 * @param approximationSet the normalized approximation set
	 * @return the hypervolume value
	 */
	public static double evaluate(Problem problem, NondominatedPopulation approximationSet) {
		if (Settings.getHypervolume() != null) {
			if (Settings.getHypervolume().equalsIgnoreCase("PISA")) {
				return PISAHypervolume.evaluate(problem, approximationSet);
			} else {
				return NativeHypervolume.evaluate(problem, approximationSet);
			}
		} else {
			return WFGNormalizedHypervolume.evaluate(problem, approximationSet);
		}
	}
	
}
