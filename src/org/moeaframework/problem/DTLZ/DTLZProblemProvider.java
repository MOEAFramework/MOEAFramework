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
package org.moeaframework.problem.DTLZ;

import java.util.Locale;

import org.moeaframework.core.Problem;
import org.moeaframework.core.spi.RegisteredProblemProvider;

/**
 * Problem provider for the DTLZ test problems.
 */
public class DTLZProblemProvider extends RegisteredProblemProvider {

	/**
	 * Constructs and registers the DTLZ problems.
	 */
	public DTLZProblemProvider() {
		super();
		
		register("DTLZ1",   () -> new DTLZ1(2), "pf/DTLZ1.2D.pf");
		register("DTLZ1_2", () -> new DTLZ1(2), "pf/DTLZ1.2D.pf");
		register("DTLZ1_3", () -> new DTLZ1(3), "pf/DTLZ1.3D.pf");
		register("DTLZ1_4", () -> new DTLZ1(4), "pf/DTLZ1.4D.pf");
		register("DTLZ1_6", () -> new DTLZ1(6), "pf/DTLZ1.6D.pf");
		register("DTLZ1_8", () -> new DTLZ1(8), "pf/DTLZ1.8D.pf");
		
		register("DTLZ2",   () -> new DTLZ2(2), "pf/DTLZ2.2D.pf");
		register("DTLZ2_2", () -> new DTLZ2(2), "pf/DTLZ2.2D.pf");
		register("DTLZ2_3", () -> new DTLZ2(3), "pf/DTLZ2.3D.pf");
		register("DTLZ2_4", () -> new DTLZ2(4), "pf/DTLZ2.4D.pf");
		register("DTLZ2_6", () -> new DTLZ2(6), "pf/DTLZ2.6D.pf");
		register("DTLZ2_8", () -> new DTLZ2(8), "pf/DTLZ2.8D.pf");
		
		register("DTLZ3",   () -> new DTLZ3(2), "pf/DTLZ3.2D.pf");
		register("DTLZ3_2", () -> new DTLZ3(2), "pf/DTLZ3.2D.pf");
		register("DTLZ3_3", () -> new DTLZ3(3), "pf/DTLZ3.3D.pf");
		register("DTLZ3_4", () -> new DTLZ3(4), "pf/DTLZ3.4D.pf");
		register("DTLZ3_6", () -> new DTLZ3(6), "pf/DTLZ3.6D.pf");
		register("DTLZ3_8", () -> new DTLZ3(8), "pf/DTLZ3.8D.pf");
		
		register("DTLZ4",   () -> new DTLZ4(2), "pf/DTLZ4.2D.pf");
		register("DTLZ4_2", () -> new DTLZ4(2), "pf/DTLZ4.2D.pf");
		register("DTLZ4_3", () -> new DTLZ4(3), "pf/DTLZ4.3D.pf");
		register("DTLZ4_4", () -> new DTLZ4(4), "pf/DTLZ4.4D.pf");
		register("DTLZ4_6", () -> new DTLZ4(6), "pf/DTLZ4.6D.pf");
		register("DTLZ4_8", () -> new DTLZ4(8), "pf/DTLZ4.8D.pf");
		
		register("DTLZ7",   () -> new DTLZ7(2), "pf/DTLZ7.2D.pf");
		register("DTLZ7_2", () -> new DTLZ7(2), "pf/DTLZ7.2D.pf");
		register("DTLZ7_3", () -> new DTLZ7(3), "pf/DTLZ7.3D.pf");
		register("DTLZ7_4", () -> new DTLZ7(4), "pf/DTLZ7.4D.pf");
		register("DTLZ7_6", () -> new DTLZ7(6), "pf/DTLZ7.6D.pf");
		register("DTLZ7_8", () -> new DTLZ7(8), "pf/DTLZ7.8D.pf");
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
			if (name.startsWith("DTLZ1_")) {
				return new DTLZ1(Integer.parseInt(name.substring(6)));
			} else if (name.startsWith("DTLZ2_")) {
				return new DTLZ2(Integer.parseInt(name.substring(6)));
			} else if (name.startsWith("DTLZ3_")) {
				return new DTLZ3(Integer.parseInt(name.substring(6)));
			} else if (name.startsWith("DTLZ4_")) {
				return new DTLZ4(Integer.parseInt(name.substring(6)));
			} else if (name.startsWith("DTLZ7_")) {
				return new DTLZ7(Integer.parseInt(name.substring(6)));
			}
		} catch (NumberFormatException e) {
			return null;
		}
		
		return null;
	}
	
}
