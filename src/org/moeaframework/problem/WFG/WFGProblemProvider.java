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
package org.moeaframework.problem.WFG;

import java.util.Locale;

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
		
		register("WFG1",   () -> new WFG1(1, 10, 2), "pf/WFG1.2D.pf");
		register("WFG1_2", () -> new WFG1(1, 10, 2), "pf/WFG1.2D.pf");
		register("WFG1_3", () -> new WFG1(2, 10, 3), "pf/WFG1.3D.pf");

		register("WFG2",   () -> new WFG2(1, 10, 2), "pf/WFG2.2D.pf");
		register("WFG2_2", () -> new WFG2(1, 10, 2), "pf/WFG2.2D.pf");
		register("WFG2_3", () -> new WFG2(2, 10, 3), "pf/WFG2.3D.pf");
		
		register("WFG3",   () -> new WFG3(1, 10, 2), "pf/WFG3.2D.pf");
		register("WFG3_2", () -> new WFG3(1, 10, 2), "pf/WFG3.2D.pf");
		register("WFG3_3", () -> new WFG3(2, 10, 3), "pf/WFG3.3D.pf");
		
		register("WFG4",   () -> new WFG4(1, 10, 2), "pf/WFG4.2D.pf");
		register("WFG4_2", () -> new WFG4(1, 10, 2), "pf/WFG4.2D.pf");
		register("WFG4_3", () -> new WFG4(2, 10, 3), "pf/WFG4.3D.pf");
		
		register("WFG5",   () -> new WFG5(1, 10, 2), "pf/WFG5.2D.pf");
		register("WFG5_2", () -> new WFG5(1, 10, 2), "pf/WFG5.2D.pf");
		register("WFG5_3", () -> new WFG5(2, 10, 3), "pf/WFG5.3D.pf");
		
		register("WFG6",   () -> new WFG6(1, 10, 2), "pf/WFG6.2D.pf");
		register("WFG6_2", () -> new WFG6(1, 10, 2), "pf/WFG6.2D.pf");
		register("WFG6_3", () -> new WFG6(2, 10, 3), "pf/WFG6.3D.pf");
		
		register("WFG7",   () -> new WFG7(1, 10, 2), "pf/WFG7.2D.pf");
		register("WFG7_2", () -> new WFG7(1, 10, 2), "pf/WFG7.2D.pf");
		register("WFG7_3", () -> new WFG7(2, 10, 3), "pf/WFG7.3D.pf");
		
		register("WFG8",   () -> new WFG8(1, 10, 2), "pf/WFG8.2D.pf");
		register("WFG8_2", () -> new WFG8(1, 10, 2), "pf/WFG8.2D.pf");
		register("WFG8_3", () -> new WFG8(2, 10, 3), "pf/WFG8.3D.pf");
		
		register("WFG9",   () -> new WFG9(1, 10, 2), "pf/WFG9.2D.pf");
		register("WFG9_2", () -> new WFG9(1, 10, 2), "pf/WFG9.2D.pf");
		register("WFG9_3", () -> new WFG9(2, 10, 3), "pf/WFG9.3D.pf");
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
			if (name.startsWith("WFG1_")) {
				int numberOfObjectives = Integer.parseInt(name.substring(5));
				return new WFG1(numberOfObjectives - 1, 10, numberOfObjectives);
			} else if (name.startsWith("WFG2_")) {
				int numberOfObjectives = Integer.parseInt(name.substring(5));
				return new WFG2(numberOfObjectives - 1, 10, numberOfObjectives);
			} else if (name.startsWith("WFG3_")) {
				int numberOfObjectives = Integer.parseInt(name.substring(5));
				return new WFG3(numberOfObjectives - 1, 10, numberOfObjectives);
			} else if (name.startsWith("WFG4_")) {
				int numberOfObjectives = Integer.parseInt(name.substring(5));
				return new WFG4(numberOfObjectives - 1, 10, numberOfObjectives);
			} else if (name.startsWith("WFG5_")) {
				int numberOfObjectives = Integer.parseInt(name.substring(5));
				return new WFG5(numberOfObjectives - 1, 10, numberOfObjectives);
			} else if (name.startsWith("WFG6_")) {
				int numberOfObjectives = Integer.parseInt(name.substring(5));
				return new WFG6(numberOfObjectives - 1, 10, numberOfObjectives);
			} else if (name.startsWith("WFG7_")) {
				int numberOfObjectives = Integer.parseInt(name.substring(5));
				return new WFG7(numberOfObjectives - 1, 10, numberOfObjectives);
			} else if (name.startsWith("WFG8_")) {
				int numberOfObjectives = Integer.parseInt(name.substring(5));
				return new WFG8(numberOfObjectives - 1, 10, numberOfObjectives);
			} else if (name.startsWith("WFG9_")) {
				int numberOfObjectives = Integer.parseInt(name.substring(5));
				return new WFG9(numberOfObjectives - 1, 10, numberOfObjectives);
			}
		} catch (NumberFormatException e) {
			return null;
		}
		
		return null;
	}
	
}
