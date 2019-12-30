/* Copyright 2009-2019 David Hadka
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
package org.moeaframework.problem;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.core.spi.ProblemFactory;

/**
 * Tests the {@link StandardProblems} class.
 */
public class StandardProblemsTest {
	
	/**
	 * The names of the standard problems.
	 */
	private final String[] problems = { 
			"ZDT1", "ZDT2", "ZDT3", "ZDT4", "ZDT6",
			"DTLZ1_2", "DTLZ1_4", "DTLZ1_6", "DTLZ1_8",
			"DTLZ2_2", "DTLZ2_4", "DTLZ2_6", "DTLZ2_8",
			"DTLZ3_2", "DTLZ3_4", "DTLZ3_6", "DTLZ3_8",
			"DTLZ4_2", "DTLZ4_4", "DTLZ4_6", "DTLZ4_8",
			"DTLZ7_2", "DTLZ7_4", "DTLZ7_6", "DTLZ7_8",
			"WFG1_2", "WFG1_3", "WFG2_2", "WFG2_3", "WFG3_2", "WFG3_3",
			"WFG4_2", "WFG4_3", "WFG5_2", "WFG5_3", "WFG6_2", "WFG6_3",
			"WFG7_2", "WFG7_3", "WFG8_2", "WFG8_3", "WFG9_2", "WFG9_3",
			"UF1", "UF2", "UF3", "UF4", "UF5", "UF6", "UF7", "UF8", "UF9",
			"UF10", "UF11", "UF12", "UF13", "CF1", "CF2", "CF3", "CF4", "CF5",
			"CF6", "CF7", "CF8", "CF9", "CF10", "LZ1", "LZ2", "LZ3", "LZ4",
			"LZ5", "LZ6", "LZ7", "LZ8", "LZ9",
			"Belegundu", "Binh", "Binh2", "Binh3", "Binh4", "Fonseca", 
			"Fonseca2", "Jimenez", "Kita", "Kursawe", "Laumanns", "Lis", 
			"Murata", "Obayashi", "OKA1", "OKA2", "Osyczka", "Osyczka2", 
			"Poloni", "Quagliarella", "Rendon", "Rendon2", "Schaffer", 
			"Schaffer2", "Srinivas", "Tamaki", "Tanaka", "Viennet", 
			"Viennet2", "Viennet3", "Viennet4"
	};
	
	/**
	 * Ensures all the standard test problems can be instantiated and their
	 * reference sets exist.
	 */
	@Test
	public void test() {
		for (String name : problems) {
			Assert.assertNotNull("no problem for " + name, 
					ProblemFactory.getInstance().getProblem(name));
			Assert.assertNotNull("no reference set for " + name,
					ProblemFactory.getInstance().getReferenceSet(name));
		}
	}
	
	/**
	 * Ensures the names are not case sensitive.
	 */
	@Test
	public void testCaseInsensitivity() {
		for (String name : problems) {
			String swapCaseName = StringUtils.swapCase(name);

			Assert.assertNotNull("no problem for " + swapCaseName, 
					ProblemFactory.getInstance().getProblem(swapCaseName));
			Assert.assertNotNull("no reference set for " + swapCaseName,
					ProblemFactory.getInstance().getReferenceSet(swapCaseName));
		}
	}

}
