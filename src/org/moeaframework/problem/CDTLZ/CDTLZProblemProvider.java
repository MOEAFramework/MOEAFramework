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
package org.moeaframework.problem.CDTLZ;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.spi.ProblemProvider;

/**
 * Problem provider for the constrained DTLZ test problems.
 */
public class CDTLZProblemProvider extends ProblemProvider {

	/**
	 * Constructs and registers the constrained DTLZ problems.
	 */
	public CDTLZProblemProvider() {
		super();
	}
	
	@Override
	public Problem getProblem(String name) {		
		try {
			Pattern pattern = Pattern.compile("^((?:CONVEX_)?C[0-9]_DTLZ[0-9])_([0-9]+)$", Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(name);
			
			if (matcher.matches()) {
				String instance = matcher.group(1);
				int numberOfObjectives = Integer.parseInt(matcher.group(2));
				
				return switch (instance.toUpperCase()) {
					case "C1_DTLZ1" -> new C1_DTLZ1(numberOfObjectives);
					case "C1_DTLZ3" -> new C1_DTLZ3(numberOfObjectives);
					case "C2_DTLZ2" -> new C2_DTLZ2(numberOfObjectives);
					case "C3_DTLZ1" -> new C3_DTLZ1(numberOfObjectives);
					case "C3_DTLZ4" -> new C3_DTLZ4(numberOfObjectives);
					case "CONVEX_C2_DTLZ2" -> new ConvexC2_DTLZ2(numberOfObjectives);
					default -> null;
				};
			}
		} catch (NumberFormatException e) {
			return null;
		}
		
		return null;
	}

	@Override
	public NondominatedPopulation getReferenceSet(String name) {
		return null;
	}
	
}
