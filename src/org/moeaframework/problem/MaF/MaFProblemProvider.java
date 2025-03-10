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
package org.moeaframework.problem.MaF;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.moeaframework.core.spi.RegisteredProblemProvider;
import org.moeaframework.problem.Problem;

/**
 * Problem provider for the MaF test problems.
 */
public class MaFProblemProvider extends RegisteredProblemProvider {
	
	private final Pattern pattern = Pattern.compile("^MaF([0-9]+)_([0-9]+)$", Pattern.CASE_INSENSITIVE);

	/**
	 * Constructs and registers the MaF problems.
	 */
	public MaFProblemProvider() {
		super();
	}
	
	@Override
	public Problem getProblem(String name) {
		Problem problem = super.getProblem(name);
		
		if (problem != null) {
			return problem;
		}
		
		// allow creating any number of objectives
		Matcher matcher = pattern.matcher(name);
			
		if (matcher.matches()) {
			int instance = Integer.parseInt(matcher.group(1));
			int numberOfObjectives = Integer.parseInt(matcher.group(2));
				
			return switch (instance) {
				case 1 -> new MaF1(numberOfObjectives);
				case 2 -> new MaF2(numberOfObjectives);
				case 3 -> new MaF3(numberOfObjectives);
				case 4 -> new MaF4(numberOfObjectives);
				case 5 -> new MaF5(numberOfObjectives);
				case 6 -> new MaF6(numberOfObjectives);
				case 7 -> new MaF7(numberOfObjectives);
				case 8 -> new MaF8(numberOfObjectives);
				case 9 -> new MaF9(numberOfObjectives);
				case 10 -> new MaF10(numberOfObjectives);
				case 11 -> new MaF11(numberOfObjectives);
				case 12 -> new MaF12(numberOfObjectives);
				case 13 -> new MaF13(numberOfObjectives);
				case 14 -> new MaF14(numberOfObjectives);
				case 15 -> new MaF15(numberOfObjectives);
				default -> null;
			};
		}
		
		return null;
	}
	
}
