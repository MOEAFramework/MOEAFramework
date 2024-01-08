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

import java.util.Locale;

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
		
		//TODO:
		//register("ZCAT1_2", () -> new ZCAT1(2), null);
		
		registerDiagnosticToolProblems(getRegisteredProblems());
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
			if (name.startsWith("ZCAT1_")) {
				return new ZCAT1(Integer.parseInt(name.substring(6)));
			} else if (name.startsWith("ZCAT2_")) {
				return new ZCAT2(Integer.parseInt(name.substring(6)));
			} else if (name.startsWith("ZCAT3_")) {
				return new ZCAT3(Integer.parseInt(name.substring(6)));
			} else if (name.startsWith("ZCAT4_")) {
				return new ZCAT4(Integer.parseInt(name.substring(6)));
			} else if (name.startsWith("ZCAT5_")) {
				return new ZCAT5(Integer.parseInt(name.substring(6)));
			} else if (name.startsWith("ZCAT6_")) {
				return new ZCAT6(Integer.parseInt(name.substring(6)));
			} else if (name.startsWith("ZCAT7_")) {
				return new ZCAT7(Integer.parseInt(name.substring(6)));
			} else if (name.startsWith("ZCAT8_")) {
				return new ZCAT8(Integer.parseInt(name.substring(6)));
			} else if (name.startsWith("ZCAT9_")) {
				return new ZCAT9(Integer.parseInt(name.substring(6)));
			} else if (name.startsWith("ZCAT10_")) {
				return new ZCAT10(Integer.parseInt(name.substring(6)));
			} else if (name.startsWith("ZCAT11_")) {
				return new ZCAT11(Integer.parseInt(name.substring(6)));
			} else if (name.startsWith("ZCAT12_")) {
				return new ZCAT12(Integer.parseInt(name.substring(6)));
			} else if (name.startsWith("ZCAT13_")) {
				return new ZCAT13(Integer.parseInt(name.substring(6)));
			} else if (name.startsWith("ZCAT14_")) {
				return new ZCAT14(Integer.parseInt(name.substring(6)));
			} else if (name.startsWith("ZCAT15_")) {
				return new ZCAT15(Integer.parseInt(name.substring(6)));
			} else if (name.startsWith("ZCAT16_")) {
				return new ZCAT16(Integer.parseInt(name.substring(6)));
			} else if (name.startsWith("ZCAT17_")) {
				return new ZCAT17(Integer.parseInt(name.substring(6)));
			} else if (name.startsWith("ZCAT18_")) {
				return new ZCAT18(Integer.parseInt(name.substring(6)));
			} else if (name.startsWith("ZCAT19_")) {
				return new ZCAT19(Integer.parseInt(name.substring(6)));
			} else if (name.startsWith("ZCAT20_")) {
				return new ZCAT20(Integer.parseInt(name.substring(6)));
			}
		} catch (NumberFormatException e) {
			return null;
		}
		
		return null;
	}
	
}
