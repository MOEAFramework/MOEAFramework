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
		
		register("CF1", () -> new CF1(), "pf/CF1.pf");
		register("CF2", () -> new CF2(), "pf/CF2.pf");
		register("CF3", () -> new CF3(), "pf/CF3.pf");
		register("CF4", () -> new CF4(), "pf/CF4.pf");
		register("CF5", () -> new CF5(), "pf/CF5.pf");
		register("CF6", () -> new CF6(), "pf/CF6.pf");
		register("CF7", () -> new CF7(), "pf/CF7.pf");
		register("CF8", () -> new CF8(), "pf/CF8.pf");
		register("CF9", () -> new CF9(), "pf/CF9.pf");
		register("CF10", () -> new CF10(), "pf/CF10.pf");
		
		register("UF1", () -> new UF1(), "pf/UF1.pf");
		register("UF2", () -> new UF2(), "pf/UF2.pf");
		register("UF3", () -> new UF3(), "pf/UF3.pf");
		register("UF4", () -> new UF4(), "pf/UF4.pf");
		register("UF5", () -> new UF5(), "pf/UF5.pf");
		register("UF6", () -> new UF6(), "pf/UF6.pf");
		register("UF7", () -> new UF7(), "pf/UF7.pf");
		register("UF8", () -> new UF8(), "pf/UF8.pf");
		register("UF9", () -> new UF9(), "pf/UF9.pf");
		register("UF10", () -> new UF10(), "pf/UF10.pf");
		register("UF11", () -> new UF11(), "pf/R2_DTLZ2_M5.pf");
		register("UF12", () -> new UF12(), "pf/R3_DTLZ3_M5.pf");
		register("UF13", () -> new UF13(), "pf/WFG1_M5.pf");
		
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
