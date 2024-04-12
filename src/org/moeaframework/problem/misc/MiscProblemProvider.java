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
package org.moeaframework.problem.misc;

import org.moeaframework.core.Epsilons;
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
		
		registerDiagnosticToolProblems(getRegisteredProblems());
		
		registerEpsilons("Belegundu", Epsilons.of(0.1));
		registerEpsilons("Binh", Epsilons.of(0.25));
		registerEpsilons("Binh2", Epsilons.of(0.35));
		registerEpsilons("Binh3", Epsilons.of(0.01));
		registerEpsilons("Binh4", Epsilons.of(0.01));
		registerEpsilons("Fonseca", Epsilons.of(0.0025));
		registerEpsilons("Fonseca2", Epsilons.of(0.007));
		registerEpsilons("Jimenez", Epsilons.of(0.05));
		registerEpsilons("Kita", Epsilons.of(0.01));
		registerEpsilons("Kursawe", Epsilons.of(0.035));
		registerEpsilons("Laumanns", Epsilons.of(0.02));
		registerEpsilons("Lis", Epsilons.of(0.004));
		registerEpsilons("Murata", Epsilons.of(0.02));
		registerEpsilons("Obayashi", Epsilons.of(0.006));
		registerEpsilons("OKA1", Epsilons.of(0.0145));
		registerEpsilons("OKA2", Epsilons.of(0.000001));
		registerEpsilons("Osyczka", Epsilons.of(0.0012));
		registerEpsilons("Osyczka2", Epsilons.of(0.5));
		registerEpsilons("Poloni", Epsilons.of(0.04));
		registerEpsilons("Quagliarella", Epsilons.of(0.03));
		registerEpsilons("Rendon", Epsilons.of(0.01));
		registerEpsilons("Rendon2", Epsilons.of(0.027));
		registerEpsilons("Schaffer", Epsilons.of(0.02));
		registerEpsilons("Schaffer2", Epsilons.of(0.017));
		registerEpsilons("Srinivas", Epsilons.of(1.5));
		registerEpsilons("Tamaki", Epsilons.of(0.06));
		registerEpsilons("Tanaka", Epsilons.of(0.0045));
		registerEpsilons("Viennet", Epsilons.of(0.1));
		registerEpsilons("Viennet2", Epsilons.of(0.0062));
		registerEpsilons("Viennet3", Epsilons.of(0.011));
		registerEpsilons("Viennet4", Epsilons.of(0.085));
	}
}
