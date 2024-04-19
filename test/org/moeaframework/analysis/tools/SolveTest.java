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
package org.moeaframework.analysis.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.Assume;
import org.moeaframework.Make;
import org.moeaframework.TempFiles;
import org.moeaframework.TestThresholds;
import org.moeaframework.analysis.io.ResultFileReader;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Variable;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.core.variable.BinaryIntegerVariable;
import org.moeaframework.core.variable.BinaryVariable;
import org.moeaframework.core.variable.Permutation;
import org.moeaframework.core.variable.RealVariable;

public class SolveTest {

	@Test
	public void testParseVariablesLowerAndUpperBounds() throws ParseException {
		Solve solve = new Solve();
		double[] lowerBounds = new double[] { 0, -10.5, 10 };
		double[] upperBounds = new double[] { 10, 10.5, 20 };
		
		CommandLine commandLine = createCommandLine(solve, "-l", toOption(lowerBounds), "-u", toOption(upperBounds));
		
		List<Variable> variables = solve.parseVariables(commandLine);
		Assert.assertEquals(3, variables.size());
		
		for (int i = 0; i < 3; i++) {
			RealVariable realVariable = (RealVariable)variables.get(i);
			
			Assert.assertEquals(lowerBounds[i], realVariable.getLowerBound(), TestThresholds.HIGH_PRECISION);
			Assert.assertEquals(upperBounds[i], realVariable.getUpperBound(), TestThresholds.HIGH_PRECISION);
		}
	}
	
	@Test(expected = ParseException.class)
	public void testParseVariablesIncorrectLength() throws ParseException {
		Solve solve = new Solve();
		double[] lowerBounds = new double[] { 0, -10, 10, 0 };
		double[] upperBounds = new double[] { 10, 10, 20 };
		
		CommandLine commandLine = createCommandLine(solve, "-l", toOption(lowerBounds), "-u", toOption(upperBounds));
		
		solve.parseVariables(commandLine);
	}
	
	@Test
	public void testParseVariables() throws ParseException {
		Solve solve = new Solve();
		
		CommandLine commandLine = createCommandLine(solve, "-v", "R(-10.1:20.9),B(10),I(-5:5),P(5)");
		
		List<Variable> variables = solve.parseVariables(commandLine);
		Assert.assertEquals(4, variables.size());
		
		RealVariable realVariable = (RealVariable)variables.get(0);
		Assert.assertEquals(-10.1, realVariable.getLowerBound(), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(20.9, realVariable.getUpperBound(), TestThresholds.HIGH_PRECISION);
		
		BinaryVariable binaryVariable = (BinaryVariable)variables.get(1);
		Assert.assertEquals(10, binaryVariable.getNumberOfBits());
		
		BinaryIntegerVariable binaryIntegerVariable = (BinaryIntegerVariable)variables.get(2);
		Assert.assertEquals(-5, binaryIntegerVariable.getLowerBound());
		Assert.assertEquals(5, binaryIntegerVariable.getUpperBound());
		
		Permutation permutation = (Permutation)variables.get(3);
		Assert.assertEquals(5, permutation.size());
	}
	
	@Test(expected = ParseException.class)
	public void testNoVariableSpecification() throws ParseException {
		Solve solve = new Solve();
		CommandLine commandLine = createCommandLine(solve);
		
		solve.parseVariables(commandLine);
	}

	@Test
	public void testInternalProblem() throws Exception {
		File outputFile = TempFiles.createFile();
		
		Solve.main(new String[] {
				"-a", "NSGAII",
				"-b", "DTLZ2_2",
				"-n", "1000",
				"-f", outputFile.getPath() });
		
		checkOutput(outputFile);
	}
	
	@Test
	public void testExternalProblemWithLowerAndUpperBounds() throws Exception {
		Assume.assumeMakeExists();
		
		File executable = new File("./examples/dtlz2_stdio.exe");
		
		if (!executable.exists()) {
			Make.runMake(executable.getParentFile());
		}
		
		File outputFile = TempFiles.createFile();
		
		Solve.main(new String[] {
				"-a", "NSGAII",
				"-l", "0,0,0,0,0,0,0,0,0,0,0",
				"-u", "1,1,1,1,1,1,1,1,1,1,1",
				"-o", "2",
				"-n", "1000",
				"-f", outputFile.getPath(),
				executable.getPath() });
		
		checkOutput(outputFile);
	}
	
	@Test
	public void testExternalProblemWithVariables() throws Exception {
		Assume.assumeMakeExists();
		
		File executable = new File("./examples/dtlz2_stdio.exe");
		
		if (!executable.exists()) {
			Make.runMake(executable.getParentFile());
		}
		
		File outputFile = TempFiles.createFile();
		
		Solve.main(new String[] {
				"-a", "NSGAII",
				"-v", "R(0:1),R(0:1),R(0:1),R(0:1),R(0:1),R(0:1),R(0:1),R(0:1),R(0:1),R(0:1),R(0:1)",
				"-o", "2",
				"-n", "1000",
				"-f", outputFile.getPath(),
				executable.getPath() });
		
		checkOutput(outputFile);
	}
	
	private CommandLine createCommandLine(Solve solve, String... args) throws ParseException {
		List<String> completeArgs = new ArrayList<String>();
		completeArgs.add("-f");
		completeArgs.add("output.dat");
		completeArgs.add("-a");
		completeArgs.add("NSGAII");
		completeArgs.add("-n");
		completeArgs.add("10000");
		completeArgs.addAll(Arrays.asList(args));
		
		return new DefaultParser().parse(solve.getOptions(), completeArgs.toArray(String[]::new));
	}
	
	private String toOption(double[] values) {
		return Arrays.stream(values).mapToObj(Double::toString).collect(Collectors.joining(","));
	}
	
	private void checkOutput(File outputFile) throws IOException {
		int count = 0;
		Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		
		try (ResultFileReader reader = new ResultFileReader(problem, outputFile)) {
			while (reader.hasNext()) {
				Assert.assertNotEmpty(reader.next().getPopulation());
				count++;
			}
		}
		
		Assert.assertEquals(count, 10);
	}

}
