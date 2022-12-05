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
package org.moeaframework.problem.LZ;

import org.moeaframework.core.spi.RegisteredProblemProvider;

/**
 * Problem provider for the LZ test problems.
 */
public class LZProblemProvider extends RegisteredProblemProvider {

	/**
	 * Constructs and registers the LZ problems.
	 */
	public LZProblemProvider() {
		super();
		
		register("LZ1", () -> new LZ1(), "pf/LZ09_F1.pf");
		register("LZ2", () -> new LZ2(), "pf/LZ09_F2.pf");
		register("LZ3", () -> new LZ3(), "pf/LZ09_F3.pf");
		register("LZ4", () -> new LZ4(), "pf/LZ09_F4.pf");
		register("LZ5", () -> new LZ5(), "pf/LZ09_F5.pf");
		register("LZ6", () -> new LZ6(), "pf/LZ09_F6.pf");
		register("LZ7", () -> new LZ7(), "pf/LZ09_F7.pf");
		register("LZ8", () -> new LZ8(), "pf/LZ09_F8.pf");
		register("LZ9", () -> new LZ9(), "pf/LZ09_F9.pf");
	}
}
