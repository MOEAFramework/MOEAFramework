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
package org.moeaframework.problem.LSMOP;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.moeaframework.core.Problem;
import org.moeaframework.core.spi.RegisteredProblemProvider;

/**
 * Problem provider for the LSMOP test problems.  Similar to other scalable problem suites, append {@code _M} to the
 * problem name, where {@code M} is the number of objectives, when creating each instance.  For example,
 * {@code "LSMOP2_3"} creates the 3-objective version of the LSMOP2 problem.
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
		
		registerDiagnosticToolProblems(getRegisteredProblems());
	}
	
	@Override
	public Problem getProblem(String name) {
		Problem problem = super.getProblem(name);
		
		if (problem != null) {
			return problem;
		}
		
		// allow creating any number of objectives, but these will not have reference sets
		try {
			Pattern pattern = Pattern.compile("^LSMOP([0-9])_([0-9]+)$", Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(name);
			
			if (matcher.matches()) {
				int instance = Integer.parseInt(matcher.group(1));
				int numberOfObjectives = Integer.parseInt(matcher.group(2));
				
				return switch (instance) {
					case 1 -> new LSMOP1(numberOfObjectives);
					case 2 -> new LSMOP2(numberOfObjectives);
					case 3 -> new LSMOP3(numberOfObjectives);
					case 4 -> new LSMOP4(numberOfObjectives);
					case 5 -> new LSMOP5(numberOfObjectives);
					case 6 -> new LSMOP6(numberOfObjectives);
					case 7 -> new LSMOP7(numberOfObjectives);
					case 8 -> new LSMOP8(numberOfObjectives);
					case 9 -> new LSMOP9(numberOfObjectives);
					default -> null;
				};
			}
		} catch (NumberFormatException e) {
			return null;
		}
		
		return null;
	}
	
}
