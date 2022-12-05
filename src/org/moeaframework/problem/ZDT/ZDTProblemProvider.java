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
package org.moeaframework.problem.ZDT;

import org.moeaframework.core.spi.RegisteredProblemProvider;

/**
 * Problem provider for the ZDT test problems.
 */
public class ZDTProblemProvider extends RegisteredProblemProvider {

	/**
	 * Constructs and registers the ZDT problems.
	 */
	public ZDTProblemProvider() {
		super();
		
		register("ZDT1", () -> new ZDT1(), "pf/ZDT1.pf");
		register("ZDT2", () -> new ZDT2(), "pf/ZDT2.pf");
		register("ZDT3", () -> new ZDT3(), "pf/ZDT3.pf");
		register("ZDT4", () -> new ZDT4(), "pf/ZDT4.pf");
		register("ZDT5", () -> new ZDT5(), "pf/ZDT5.pf");
		register("ZDT6", () -> new ZDT6(), "pf/ZDT6.pf");
	}
}
