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
package org.moeaframework.analysis.sensitivity;

import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.comparator.EpsilonBoxDominanceComparator;

/**
 * Provides &epsilon; values for algorithms using &epsilon;-dominance archives
 * on the standard test problems.
 */
public class EpsilonHelper {
	
	/**
	 * Private constructor to prevent instantiation.
	 */
	private EpsilonHelper() {
		super();
	}

	/**
	 * Returns the &epsilon; value used for the standard problems in sensitivity
	 * analysis.  If the problem is not recognized, the default &epsilon; value
	 * of {@code 0.01} is returned.
	 * 
	 * @param problem the problem
	 * @return the &epsilon; value used for the standard problems in sensitivity
	 *         analysis
	 */
	public static double getEpsilon(Problem problem) {
		String name = problem.getName();
		int numberOfObjectives = problem.getNumberOfObjectives();
		
		if (name.startsWith("DTLZ")) {
			return numberOfObjectives == 2 ? 0.01 : 
					numberOfObjectives == 3 ? 0.05 :
						0.1 + 0.05*(numberOfObjectives-3);
		} else if (name.startsWith("UF")) {
			if (name.equals("UF1")) {
				return 0.001;
			} else if (name.equals("UF3")) {
				return 0.0008;
			} else if (name.equals("UF5")) {
				return 0.000001;
			} else if (name.equals("UF6")) {
				return 0.000001;
			} else if (name.equals("UF8")) {
				return 0.0045;
			} else if (name.equals("UF9")) {
				return 0.008;
			} else if (name.equals("UF10")) {
				return 0.001;
			} else {
				if (numberOfObjectives == 2) {
					return 0.005;
				} else if (numberOfObjectives == 3) {
					return 0.07;
				} else if (numberOfObjectives == 5) {
					return 0.2;
				} else {
					throw new IllegalArgumentException(
							"number of objectives not supported");
				}
			}
		} else if (name.equalsIgnoreCase("Belegundu")) {
			return 0.1;
		} else if (name.equalsIgnoreCase("Binh")) {
			return 0.25;
		} else if (name.equalsIgnoreCase("Binh2")) {
			return 0.35;
		} else if (name.equalsIgnoreCase("Binh3")) {
			return 0.01;
		} else if (name.equalsIgnoreCase("Binh4")) {
			return 0.01;
		} else if (name.equalsIgnoreCase("Fonseca")) {
			return 0.0025;
		} else if (name.equalsIgnoreCase("Fonseca2")) {
			return 0.007;
		} else if (name.equalsIgnoreCase("Jimenez")) {
			return 0.05;
		} else if (name.equalsIgnoreCase("Kita")) {
			return 0.01;
		} else if (name.equalsIgnoreCase("Kursawe")) {
			return 0.035;
		} else if (name.equalsIgnoreCase("Laumanns")) {
			return 0.02;
		} else if (name.equalsIgnoreCase("Lis")) {
			return 0.004;
		} else if (name.equalsIgnoreCase("Murata")) {
			return 0.02;
		} else if (name.equalsIgnoreCase("Obayashi")) {
			return 0.006;
		} else if (name.equalsIgnoreCase("OKA1")) {
			return 0.0145;
		} else if (name.equalsIgnoreCase("OKA2")) {
			return 0.000001;
		} else if (name.equalsIgnoreCase("Osyczka")) {
			return 0.0012;
		} else if (name.equalsIgnoreCase("Osyczka2")) {
			return 0.5;
		} else if (name.equalsIgnoreCase("Poloni")) {
			return 0.04;
		} else if (name.equalsIgnoreCase("Quagliarella")) {
			return 0.03;
		} else if (name.equalsIgnoreCase("Rendon")) {
			return 0.01;
		} else if (name.equalsIgnoreCase("Rendon2")) {
			return 0.027;
		} else if (name.equalsIgnoreCase("Schaffer")) {
			return 0.02;
		} else if (name.equalsIgnoreCase("Schaffer2")) {
			return 0.017;
		} else if (name.equalsIgnoreCase("Srinivas")) {
			return 1.5;
		} else if (name.equalsIgnoreCase("Tamaki")) {
			return 0.06;
		} else if (name.equalsIgnoreCase("Tanaka")) {
			return 0.0045;
		} else if (name.equalsIgnoreCase("Viennet")) {
			return 0.1;
		} else if (name.equalsIgnoreCase("Viennet2")) {
			return 0.0062;
		} else if (name.equalsIgnoreCase("Viennet3")) {
			return 0.011;
		} else if (name.equalsIgnoreCase("Viennet4")) {
			return 0.085;
		} else {
			return 0.01;
		}
	}
	
	/**
	 * Converts the population to an {@link EpsilonBoxDominanceArchive} with
	 * the given &epsilon; values.  To prevent unnecessary computations, this
	 * conversion only occurs if the original population is not an
	 * {@code EpsilonBoxDominanceArchive} and does not have matching &epsilon;
	 * values.
	 * 
	 * @param population the population to convert
	 * @param epsilon the &epsilon; values
	 * @return the population converted to an {@code EpsilonBoxDominanceArchive}
	 *         with the given &epsilon; values
	 */
	public static EpsilonBoxDominanceArchive convert(Population population, 
			double[] epsilon) {
		boolean isSameEpsilon = false;

		//check if population already has the correct epsilons
		if (population instanceof EpsilonBoxDominanceArchive) {
			EpsilonBoxDominanceArchive result =
					(EpsilonBoxDominanceArchive)population;
			EpsilonBoxDominanceComparator comparator = 
					result.getComparator();

			isSameEpsilon = true;

			for (int i=0; i<epsilon.length; i++) {
				if (epsilon[i] != comparator.getEpsilon(i)) {
					isSameEpsilon = false;
					break;
				}
			}
		}

		//apply epsilons only if necessary
		if (isSameEpsilon) {
			return (EpsilonBoxDominanceArchive)population;
		} else {
			return new EpsilonBoxDominanceArchive(epsilon, population);
		}	
	}
}
