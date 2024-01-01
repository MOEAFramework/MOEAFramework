/* Copyright 2009-2023 David Hadka
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
package org.moeaframework.problem.LSMOP;

import java.util.Locale;

import org.moeaframework.core.Problem;
import org.moeaframework.core.spi.RegisteredProblemProvider;

/**
 * Problem provider for the LSMOP test problems.
 */
public class LSMOPProblemProvider extends RegisteredProblemProvider {

	/**
	 * Constructs and registers the LSMOP problems.
	 */
	public LSMOPProblemProvider() {
		super();
		
		register("LSMOP1_2", () -> new LSMOP1(2), "pf/LSMOP1.2D.pf");
		register("LSMOP2_2", () -> new LSMOP2(2), "pf/LSMOP2.2D.pf");
		register("LSMOP3_2", () -> new LSMOP3(2), "pf/LSMOP3.2D.pf");
		register("LSMOP4_2", () -> new LSMOP4(2), "pf/LSMOP4.2D.pf");
		register("LSMOP5_2", () -> new LSMOP5(2), "pf/LSMOP5.2D.pf");
		register("LSMOP6_2", () -> new LSMOP6(2), "pf/LSMOP6.2D.pf");
		register("LSMOP7_2", () -> new LSMOP7(2), "pf/LSMOP7.2D.pf");
		register("LSMOP8_2", () -> new LSMOP8(2), "pf/LSMOP8.2D.pf");
		register("LSMOP9_2", () -> new LSMOP9(2), "pf/LSMOP9.2D.pf");
	}
	
	@Override
	public Problem getProblem(String name) {
		Problem problem = super.getProblem(name);
		
		if (problem != null) {
			return problem;
		}
		
		// allow creating any number of objectives, but these will not have
		// reference sets
		name = name.toUpperCase(Locale.ROOT);
		
		try {
			if (name.startsWith("LSMOP1_")) {
				return new LSMOP1(Integer.parseInt(name.substring(7)));
			} else if (name.startsWith("LSMOP2_")) {
				return new LSMOP2(Integer.parseInt(name.substring(7)));
			} else if (name.startsWith("LSMOP3_")) {
				return new LSMOP3(Integer.parseInt(name.substring(7)));
			} else if (name.startsWith("LSMOP4_")) {
				return new LSMOP4(Integer.parseInt(name.substring(7)));
			} else if (name.startsWith("LSMOP5_")) {
				return new LSMOP5(Integer.parseInt(name.substring(7)));
			} else if (name.startsWith("LSMOP6_")) {
				return new LSMOP6(Integer.parseInt(name.substring(7)));
			} else if (name.startsWith("LSMOP7_")) {
				return new LSMOP7(Integer.parseInt(name.substring(7)));
			} else if (name.startsWith("LSMOP8_")) {
				return new LSMOP8(Integer.parseInt(name.substring(7)));
			} else if (name.startsWith("LSMOP9_")) {
				return new LSMOP9(Integer.parseInt(name.substring(7)));
			}
		} catch (NumberFormatException e) {
			return null;
		}
		
		return null;
	}
	
}
