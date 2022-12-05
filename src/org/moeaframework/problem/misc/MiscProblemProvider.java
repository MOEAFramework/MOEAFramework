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
package org.moeaframework.problem.misc;

import org.moeaframework.core.spi.RegisteredProblemProvider;

/**
 * Problem provider for the miscellaneous test problems.
 */
public class MiscProblemProvider extends RegisteredProblemProvider {

	/**
	 * Constructs and registers the miscellaneous problems.
	 */
	public MiscProblemProvider() {
		super();
		
		register("Belegundu", () -> new Belegundu(), "pf/Belegundu.pf");
		register("Binh", () -> new Binh(), "pf/Binh.pf");
		register("Binh2", () -> new Binh2(), "pf/Binh2.pf");
		register("Binh3", () -> new Binh3(), "pf/Binh3.pf");
		register("Fonseca", () -> new Fonseca(), "pf/Fonseca.pf");
		register("Fonseca2", () -> new Fonseca2(), "pf/Fonseca2.pf");
		register("Jimenez", () -> new Jimenez(), "pf/Jimenez.pf");
		register("Kita", () -> new Kita(), "pf/Kita.pf");
		register("Kursawe", () -> new Kursawe(), "pf/Kursawe.pf");
		register("Laumanns", () -> new Laumanns(), "pf/Laumanns.pf");
		register("Lis", () -> new Lis(), "pf/Lis.pf");
		register("Murata", () -> new Murata(), "pf/Murata.pf");
		register("Obayashi", () -> new Obayashi(), "pf/Obayashi.pf");
		register("OKA1", () -> new OKA1(), "pf/OKA1.pf");
		register("OKA2", () -> new OKA2(), "pf/OKA2.pf");
		register("Osyczka", () -> new Osyczka(), "pf/Osyczka.pf");
		register("Osyczka2", () -> new Osyczka2(), "pf/Osyczka2.pf");
		register("Poloni", () -> new Poloni(), "pf/Poloni.pf");
		register("Quagliarella", () -> new Quagliarella(), "pf/Quagliarella.pf");
		register("Rendon", () -> new Rendon(), "pf/Rendon.pf");
		register("Rendon2", () -> new Rendon2(), "pf/Rendon2.pf");
		register("Schaffer", () -> new Schaffer(), "pf/Schaffer.pf");
		register("Schaffer2", () -> new Schaffer2(), "pf/Schaffer2.pf");
		register("Srinivas", () -> new Srinivas(), "pf/Srinivas.pf");
		register("Tamaki", () -> new Tamaki(), "pf/Tamaki.pf");
		register("Tanaka", () -> new Tanaka(), "pf/Tanaka.pf");
		register("Viennet", () -> new Viennet(), "pf/Viennet.pf");
		register("Viennet2", () -> new Viennet2(), "pf/Viennet2.pf");
		register("Viennet3", () -> new Viennet3(), "pf/Viennet3.pf");
		register("Viennet4", () -> new Viennet4(), "pf/Viennet4.pf");
		register("Osyczka2", () -> new Osyczka2(), "pf/Osyczka2.pf");
		register("Osyczka2", () -> new Osyczka2(), "pf/Osyczka2.pf");
	}
}
