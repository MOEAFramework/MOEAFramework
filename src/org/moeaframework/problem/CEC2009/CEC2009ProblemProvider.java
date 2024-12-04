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
package org.moeaframework.problem.CEC2009;

import org.moeaframework.core.Epsilons;
import org.moeaframework.core.spi.RegisteredProblemProvider;

/**
 * Constructs and configures the CEC2009 problems.
 */
public class CEC2009ProblemProvider extends RegisteredProblemProvider {

	/**
	 * Constructs and registers the CEC2009 problems.
	 */
	public CEC2009ProblemProvider() {
		super();
		
		register("CF1", CF1::new, "pf/CF1.pf");
		register("CF2", CF2::new, "pf/CF2.pf");
		register("CF3", CF3::new, "pf/CF3.pf");
		register("CF4", CF4::new, "pf/CF4.pf");
		register("CF5", CF5::new, "pf/CF5.pf");
		register("CF6", CF6::new, "pf/CF6.pf");
		register("CF7", CF7::new, "pf/CF7.pf");
		register("CF8", CF8::new, "pf/CF8.pf");
		register("CF9", CF9::new, "pf/CF9.pf");
		register("CF10", CF10::new, "pf/CF10.pf");
		
		register("UF1", UF1::new, "pf/UF1.pf");
		register("UF2", UF2::new, "pf/UF2.pf");
		register("UF3", UF3::new, "pf/UF3.pf");
		register("UF4", UF4::new, "pf/UF4.pf");
		register("UF5", UF5::new, "pf/UF5.pf");
		register("UF6", UF6::new, "pf/UF6.pf");
		register("UF7", UF7::new, "pf/UF7.pf");
		register("UF8", UF8::new, "pf/UF8.pf");
		register("UF9", UF9::new, "pf/UF9.pf");
		register("UF10", UF10::new, "pf/UF10.pf");
		register("UF11", UF11::new, "pf/R2_DTLZ2_M5.pf");
		register("UF12", UF12::new, "pf/R3_DTLZ3_M5.pf");
		register("UF13", UF13::new, "pf/WFG1_M5.pf");
		
		registerDiagnosticToolProblems(getRegisteredProblems());
		
		registerEpsilons("UF1", Epsilons.of(0.001));
		registerEpsilons("UF2", Epsilons.of(0.005));
		registerEpsilons("UF3", Epsilons.of(0.0008));
		registerEpsilons("UF4", Epsilons.of(0.005));
		registerEpsilons("UF5", Epsilons.of(0.000001));
		registerEpsilons("UF6", Epsilons.of(0.000001));
		registerEpsilons("UF7", Epsilons.of(0.005));
		registerEpsilons("UF8", Epsilons.of(0.0045));
		registerEpsilons("UF9", Epsilons.of(0.008));
		registerEpsilons("UF10", Epsilons.of(0.001));
		registerEpsilons("UF11", Epsilons.of(0.2));
		registerEpsilons("UF12", Epsilons.of(0.2));
		registerEpsilons("UF13", Epsilons.of(0.2));
	}
}
