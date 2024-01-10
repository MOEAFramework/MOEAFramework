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
package org.moeaframework.problem.RWMOP;

import org.moeaframework.core.spi.RegisteredProblemProvider;

/**
 * Problem provider for the RWMOP problems.  Note that problems are named {@code RCM<N>} where {@code <N>}
 * is the instance id.
 */
public class RWMOPProblemProvider extends RegisteredProblemProvider {

	/**
	 * Constructs and registers the RWMOP problems.
	 */
	public RWMOPProblemProvider() {
		super();
		
		register("RCM01", () -> new RCM01(), null);
		register("RCM02", () -> new RCM02(), null);
		register("RCM03", () -> new RCM03(), null);
		register("RCM04", () -> new RCM04(), null);
		register("RCM05", () -> new RCM05(), null);
		register("RCM06", () -> new RCM06(), null);
		register("RCM07", () -> new RCM07(), null);
		register("RCM08", () -> new RCM08(), null);
		register("RCM09", () -> new RCM09(), null);
		register("RCM10", () -> new RCM10(), null);
		register("RCM11", () -> new RCM11(), null);
		register("RCM12", () -> new RCM12(), null);
		register("RCM13", () -> new RCM13(), null);
		
		registerDiagnosticToolProblems(getRegisteredProblems());
	}
}
