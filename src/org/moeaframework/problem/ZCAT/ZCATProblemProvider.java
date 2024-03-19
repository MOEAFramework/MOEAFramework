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
package org.moeaframework.problem.ZCAT;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.moeaframework.core.Problem;
import org.moeaframework.core.spi.RegisteredProblemProvider;

/**
 * Problem provider for the ZCAT test problems.
 */
public class ZCATProblemProvider extends RegisteredProblemProvider {

	/**
	 * Constructs and registers the ZCAT problems.
	 */
	public ZCATProblemProvider() {
		super();
		
		register("ZCAT1_2", () -> new ZCAT1(2), "pf/ZCAT1.2D.pf");
		register("ZCAT2_2", () -> new ZCAT2(2), "pf/ZCAT2.2D.pf");
		register("ZCAT3_2", () -> new ZCAT3(2), "pf/ZCAT3.2D.pf");
		register("ZCAT4_2", () -> new ZCAT4(2), "pf/ZCAT4.2D.pf");
		register("ZCAT5_2", () -> new ZCAT5(2), "pf/ZCAT5.2D.pf");
		register("ZCAT6_2", () -> new ZCAT6(2), "pf/ZCAT6.2D.pf");
		register("ZCAT7_2", () -> new ZCAT7(2), "pf/ZCAT7.2D.pf");
		register("ZCAT8_2", () -> new ZCAT8(2), "pf/ZCAT8.2D.pf");
		register("ZCAT9_2", () -> new ZCAT9(2), "pf/ZCAT9.2D.pf");
		register("ZCAT10_2", () -> new ZCAT10(2), "pf/ZCAT10.2D.pf");
		register("ZCAT11_2", () -> new ZCAT11(2), "pf/ZCAT11.2D.pf");
		register("ZCAT12_2", () -> new ZCAT12(2), "pf/ZCAT12.2D.pf");
		register("ZCAT13_2", () -> new ZCAT13(2), "pf/ZCAT13.2D.pf");
		register("ZCAT14_2", () -> new ZCAT14(2), "pf/ZCAT14.2D.pf");
		register("ZCAT15_2", () -> new ZCAT15(2), "pf/ZCAT15.2D.pf");
		register("ZCAT16_2", () -> new ZCAT16(2), "pf/ZCAT16.2D.pf");
		register("ZCAT17_2", () -> new ZCAT17(2), "pf/ZCAT17.2D.pf");
		register("ZCAT18_2", () -> new ZCAT18(2), "pf/ZCAT18.2D.pf");
		register("ZCAT19_2", () -> new ZCAT19(2), "pf/ZCAT19.2D.pf");
		register("ZCAT20_2", () -> new ZCAT20(2), "pf/ZCAT20.2D.pf");
		
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
			Pattern pattern = Pattern.compile("^ZCAT([0-9]+)_([0-9]+)$", Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(name);
			
			if (matcher.matches()) {
				int instance = Integer.parseInt(matcher.group(1));
				int numberOfObjectives = Integer.parseInt(matcher.group(2));
				
				return switch (instance) {
					case 1 -> new ZCAT1(numberOfObjectives);
					case 2 -> new ZCAT2(numberOfObjectives);
					case 3 -> new ZCAT3(numberOfObjectives);
					case 4 -> new ZCAT4(numberOfObjectives);
					case 5 -> new ZCAT5(numberOfObjectives);
					case 6 -> new ZCAT6(numberOfObjectives);
					case 7 -> new ZCAT7(numberOfObjectives);
					case 8 -> new ZCAT8(numberOfObjectives);
					case 9 -> new ZCAT9(numberOfObjectives);
					case 10 -> new ZCAT10(numberOfObjectives);
					case 11 -> new ZCAT11(numberOfObjectives);
					case 12 -> new ZCAT12(numberOfObjectives);
					case 13 -> new ZCAT13(numberOfObjectives);
					case 14 -> new ZCAT14(numberOfObjectives);
					case 15 -> new ZCAT15(numberOfObjectives);
					case 16 -> new ZCAT16(numberOfObjectives);
					case 17 -> new ZCAT17(numberOfObjectives);
					case 18 -> new ZCAT18(numberOfObjectives);
					case 19 -> new ZCAT19(numberOfObjectives);
					case 20 -> new ZCAT20(numberOfObjectives);
					default -> null;
				};
			}
		} catch (NumberFormatException e) {
			return null;
		}
		
		return null;
	}
	
}
