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
package org.moeaframework.problem.WFG;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.moeaframework.core.Problem;
import org.moeaframework.core.spi.RegisteredProblemProvider;

/**
 * Problem provider for the WFG test problems.
 */
public class WFGProblemProvider extends RegisteredProblemProvider {

	/**
	 * Constructs and registers the WFG problems.
	 */
	public WFGProblemProvider() {
		super();
		
		register("WFG1",   () -> new WFG1(2), "pf/WFG1.2D.pf");
		register("WFG1_2", () -> new WFG1(2), "pf/WFG1.2D.pf");
		register("WFG1_3", () -> new WFG1(3), "pf/WFG1.3D.pf");

		register("WFG2",   () -> new WFG2(2), "pf/WFG2.2D.pf");
		register("WFG2_2", () -> new WFG2(2), "pf/WFG2.2D.pf");
		register("WFG2_3", () -> new WFG2(3), "pf/WFG2.3D.pf");
		
		register("WFG3",   () -> new WFG3(2), "pf/WFG3.2D.pf");
		register("WFG3_2", () -> new WFG3(2), "pf/WFG3.2D.pf");
		register("WFG3_3", () -> new WFG3(3), "pf/WFG3.3D.pf");
		
		register("WFG4",   () -> new WFG4(2), "pf/WFG4.2D.pf");
		register("WFG4_2", () -> new WFG4(2), "pf/WFG4.2D.pf");
		register("WFG4_3", () -> new WFG4(3), "pf/WFG4.3D.pf");
		
		register("WFG5",   () -> new WFG5(2), "pf/WFG5.2D.pf");
		register("WFG5_2", () -> new WFG5(2), "pf/WFG5.2D.pf");
		register("WFG5_3", () -> new WFG5(3), "pf/WFG5.3D.pf");
		
		register("WFG6",   () -> new WFG6(2), "pf/WFG6.2D.pf");
		register("WFG6_2", () -> new WFG6(2), "pf/WFG6.2D.pf");
		register("WFG6_3", () -> new WFG6(3), "pf/WFG6.3D.pf");
		
		register("WFG7",   () -> new WFG7(2), "pf/WFG7.2D.pf");
		register("WFG7_2", () -> new WFG7(2), "pf/WFG7.2D.pf");
		register("WFG7_3", () -> new WFG7(3), "pf/WFG7.3D.pf");
		
		register("WFG8",   () -> new WFG8(2), "pf/WFG8.2D.pf");
		register("WFG8_2", () -> new WFG8(2), "pf/WFG8.2D.pf");
		register("WFG8_3", () -> new WFG8(3), "pf/WFG8.3D.pf");
		
		register("WFG9",   () -> new WFG9(2), "pf/WFG9.2D.pf");
		register("WFG9_2", () -> new WFG9(2), "pf/WFG9.2D.pf");
		register("WFG9_3", () -> new WFG9(3), "pf/WFG9.3D.pf");
		
		registerDiagnosticToolProblem("WFG1_2");
		registerDiagnosticToolProblem("WFG2_2");
		registerDiagnosticToolProblem("WFG3_2");
		registerDiagnosticToolProblem("WFG4_2");
		registerDiagnosticToolProblem("WFG5_2");
		registerDiagnosticToolProblem("WFG6_2");
		registerDiagnosticToolProblem("WFG7_2");
		registerDiagnosticToolProblem("WFG8_2");
		registerDiagnosticToolProblem("WFG9_2");
	}
	
	@Override
	public Problem getProblem(String name) {
		Problem problem = super.getProblem(name);
		
		if (problem != null) {
			return problem;
		}
		
		// allow creating any number of objectives, but these will not have reference sets
		try {
			Pattern pattern = Pattern.compile("^WFG([0-9])_([0-9]+)$", Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(name);
			
			if (matcher.matches()) {
				int instance = Integer.parseInt(matcher.group(1));
				int numberOfObjectives = Integer.parseInt(matcher.group(2));
				
				return switch (instance) {
					case 1 -> new WFG1(numberOfObjectives);
					case 2 -> new WFG2(numberOfObjectives);
					case 3 -> new WFG3(numberOfObjectives);
					case 4 -> new WFG4(numberOfObjectives);
					case 5 -> new WFG5(numberOfObjectives);
					case 6 -> new WFG6(numberOfObjectives);
					case 7 -> new WFG7(numberOfObjectives);
					case 8 -> new WFG8(numberOfObjectives);
					case 9 -> new WFG9(numberOfObjectives);
					default -> null;
				};
			}
		} catch (NumberFormatException e) {
			return null;
		}
		
		return null;
	}
	
}
