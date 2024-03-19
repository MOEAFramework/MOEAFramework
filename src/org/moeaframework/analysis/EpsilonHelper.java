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

import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.Epsilons;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;

/**
 * Provides &epsilon; values for algorithms using &epsilon;-dominance archives on the standard test problems.
 */
public class EpsilonHelper {
	
	/**
	 * The default &epsilon; value that is returned for any problem without an explicitly configured value.
	 */
	public static final double DEFAULT = 0.01;
	
	/**
	 * Private constructor to prevent instantiation.
	 */
	private EpsilonHelper() {
		super();
	}

	/**
	 * Returns the &epsilon; value used for the standard problems in sensitivity analysis.  If the problem is not
	 * recognized, the default &epsilon; value of {@code 0.01} is returned.
	 * 
	 * @param problem the problem
	 * @return the &epsilon; value used for the standard problems in sensitivity analysis
	 */
	public static double getEpsilon(Problem problem) {
		String name = problem.getName();
		int numberOfObjectives = problem.getNumberOfObjectives();
		
		return switch (name) {
			case "Belegundu" -> 0.1;
			case "Binh" -> 0.25;
			case "Binh2" -> 0.35;
			case "Binh3" -> 0.01;
			case "Binh4" -> 0.01;
			case "Fonseca" -> 0.0025;
			case "Fonseca2" -> 0.007;
			case "Jimenez" -> 0.05;
			case "Kita" -> 0.01;
			case "Kursawe" -> 0.035;
			case "Laumanns" -> 0.02;
			case "Lis" -> 0.004;
			case "Murata" -> 0.02;
			case "Obayashi" -> 0.006;
			case "OKA1" -> 0.0145;
			case "OKA2" -> 0.000001;
			case "Osyczka" -> 0.0012;
			case "Osyczka2" -> 0.5;
			case "Poloni" -> 0.04;
			case "Quagliarella" -> 0.03;
			case "Rendon" ->  0.01;
			case "Rendon2" -> 0.027;
			case "Schaffer" -> 0.02;
			case "Schaffer2" -> 0.017;
			case "Srinivas" -> 1.5;
			case "Tamaki" -> 0.06;
			case "Tanaka" -> 0.0045;
			case "Viennet" -> 0.1;
			case "Viennet2" -> 0.0062;
			case "Viennet3" -> 0.011;
			case "Viennet4" -> 0.085;
			case "UF1" -> 0.001;
			case "UF3" -> 0.0008;
			case "UF5" -> 0.000001;
			case "UF6" -> 0.000001;
			case "UF8" -> 0.0045;
			case "UF9" -> 0.008;
			case "UF10" -> 0.001;
			default -> {
				if (name.startsWith("DTLZ")) {
					yield numberOfObjectives == 2 ? 0.01 : numberOfObjectives == 3 ? 0.05 : 0.1 + 0.05*(numberOfObjectives-3);
				} else if (name.startsWith("UF")) {
					yield numberOfObjectives == 2 ? 0.005 : numberOfObjectives == 3 ? 0.07 : 0.2;
				} else {
					yield DEFAULT;
				}
			}
		};
	}
	
	/**
	 * Converts the population to an {@link EpsilonBoxDominanceArchive} with the given &epsilon; values.
	 * To prevent unnecessary computations, this conversion only occurs if the original population is not an
	 * {@code EpsilonBoxDominanceArchive} and does not have matching &epsilon; values.
	 * 
	 * @param population the population to convert
	 * @param epsilons the &epsilon; values
	 * @return the population converted to an {@code EpsilonBoxDominanceArchive} with the given &epsilon; values
	 */
	public static EpsilonBoxDominanceArchive convert(Population population, double[] epsilons) {
		return convert(population, new Epsilons(epsilons));
	}
	
	/**
	 * Converts the population to an {@link EpsilonBoxDominanceArchive} with the given &epsilon; values.
	 * To prevent unnecessary computations, this conversion only occurs if the original population is not an
	 * {@code EpsilonBoxDominanceArchive} and does not have matching &epsilon; values.
	 * 
	 * @param population the population to convert
	 * @param epsilons the &epsilon; values
	 * @return the population converted to an {@code EpsilonBoxDominanceArchive} with the given &epsilon; values
	 */
	public static EpsilonBoxDominanceArchive convert(Population population, Epsilons epsilons) {
		if (population instanceof EpsilonBoxDominanceArchive epsilonArchive &&
				epsilons.equals(epsilonArchive.getComparator().getEpsilons())) {
			return epsilonArchive;
		}

		return new EpsilonBoxDominanceArchive(epsilons, population);
	}
}
