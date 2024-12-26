/* Copyright 2009-2025 David Hadka
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
		
		register("Belegundu", Belegundu::new, "pf/Belegundu.pf");
		register("Binh", Binh::new, "pf/Binh.pf");
		register("Binh2", Binh2::new, "pf/Binh2.pf");
		register("Binh3", Binh3::new, "pf/Binh3.pf");
		register("Binh4", Binh3::new, "pf/Binh4.pf");
		register("Fonseca", Fonseca::new, "pf/Fonseca.pf");
		register("Fonseca2", Fonseca2::new, "pf/Fonseca2.pf");
		register("Jimenez", Jimenez::new, "pf/Jimenez.pf");
		register("Kita", Kita::new, "pf/Kita.pf");
		register("Kursawe", Kursawe::new, "pf/Kursawe.pf");
		register("Laumanns", Laumanns::new, "pf/Laumanns.pf");
		register("Lis", Lis::new, "pf/Lis.pf");
		register("Murata", Murata::new, "pf/Murata.pf");
		register("Obayashi", Obayashi::new, "pf/Obayashi.pf");
		register("OKA1", OKA1::new, "pf/OKA1.pf");
		register("OKA2", OKA2::new, "pf/OKA2.pf");
		register("Osyczka", Osyczka::new, "pf/Osyczka.pf");
		register("Osyczka2", Osyczka2::new, "pf/Osyczka2.pf");
		register("Poloni", Poloni::new, "pf/Poloni.pf");
		register("Quagliarella", Quagliarella::new, "pf/Quagliarella.pf");
		register("Rendon", Rendon::new, "pf/Rendon.pf");
		register("Rendon2", Rendon2::new, "pf/Rendon2.pf");
		register("Schaffer", Schaffer::new, "pf/Schaffer.pf");
		register("Schaffer2", Schaffer2::new, "pf/Schaffer2.pf");
		register("Srinivas", Srinivas::new, "pf/Srinivas.pf");
		register("Tamaki", Tamaki::new, "pf/Tamaki.pf");
		register("Tanaka", Tanaka::new, "pf/Tanaka.pf");
		register("Viennet", Viennet::new, "pf/Viennet.pf");
		register("Viennet2", Viennet2::new, "pf/Viennet2.pf");
		register("Viennet3", Viennet3::new, "pf/Viennet3.pf");
		register("Viennet4", Viennet4::new, "pf/Viennet4.pf");
		register("Osyczka2", Osyczka2::new, "pf/Osyczka2.pf");
		register("Osyczka2", Osyczka2::new, "pf/Osyczka2.pf");
		
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
