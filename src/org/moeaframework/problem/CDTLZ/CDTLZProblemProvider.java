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
package org.moeaframework.problem.CDTLZ;

import java.util.Locale;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.spi.ProblemProvider;

/**
 * Problem provider for the constrained DTLZ test problems.
 */
public class CDTLZProblemProvider extends ProblemProvider {

	/**
	 * Constructs and registers the DTLZ problems.
	 */
	public CDTLZProblemProvider() {
		super();
	}
	
	@Override
	public Problem getProblem(String name) {
		name = name.toUpperCase(Locale.ROOT);
		
		try {
			if (name.startsWith("C1_DTLZ1_")) {
				return new C1_DTLZ1(Integer.parseInt(name.substring(9)));
			} else if (name.startsWith("C1_DTLZ3_")) {
				return new C1_DTLZ3(Integer.parseInt(name.substring(9)));
			} else if (name.startsWith("C2_DTLZ2_")) {
				return new C2_DTLZ2(Integer.parseInt(name.substring(9)));
			} else if (name.startsWith("C3_DTLZ1_")) {
				return new C3_DTLZ1(Integer.parseInt(name.substring(9)));
			} else if (name.startsWith("C3_DTLZ4_")) {
				return new C3_DTLZ4(Integer.parseInt(name.substring(9)));
			} else if (name.startsWith("CONVEX_C2_DTLZ2_")) {
				return new ConvexC2_DTLZ2(Integer.parseInt(name.substring(15)));
			}
		} catch (NumberFormatException e) {
			return null;
		}
		
		return null;
	}

	@Override
	public NondominatedPopulation getReferenceSet(String name) {
		return null;
	}
	
}
