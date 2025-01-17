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
package org.moeaframework.analysis.tools;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.Assume;
import org.moeaframework.TempFiles;
import org.moeaframework.TestThresholds;
import org.moeaframework.analysis.io.ResultFileReader;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Settings;
import org.moeaframework.core.constraint.Between;
import org.moeaframework.core.constraint.Constraint;
import org.moeaframework.core.constraint.Equal;
import org.moeaframework.core.constraint.GreaterThan;
import org.moeaframework.core.constraint.GreaterThanOrEqual;
import org.moeaframework.core.constraint.LessThan;
import org.moeaframework.core.constraint.LessThanOrEqual;
import org.moeaframework.core.constraint.NotEqual;
import org.moeaframework.core.constraint.Outside;
import org.moeaframework.core.objective.Maximize;
import org.moeaframework.core.objective.Minimize;
import org.moeaframework.core.objective.Objective;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.core.variable.BinaryIntegerVariable;
import org.moeaframework.core.variable.BinaryVariable;
import org.moeaframework.core.variable.Permutation;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.core.variable.Subset;
import org.moeaframework.core.variable.Variable;
import org.moeaframework.problem.Problem;

public class SolveTest {
	
	private static final File SCRIPT = new File("examples/org/moeaframework/examples/solve/dtlz2.py");
	
	private Solve solve;
	
	@Before
	public void setUp() {
		if (!SCRIPT.exists()) {
			Assert.fail("Missing script " + SCRIPT);
		}
		
		solve = new Solve();
	}
	
	@After
	public void tearDown() {
		solve = null;
	}

	@Test
	public void testParseVariablesWithLowerAndUpperBounds() throws ParseException {
		double[] lowerBounds = new double[] { 0, -10.5, 10 };
		double[] upperBounds = new double[] { 10, 10.5, 20 };
		
		CommandLine commandLine = createCommandLine(
				"--lowerBounds", convert(lowerBounds, ','),
				"--upperBounds", convert(upperBounds, ','));
		
		List<Variable> variables = solve.parseVariables(commandLine);
		Assert.assertEquals(3, variables.size());
		
		for (int i = 0; i < 3; i++) {
			RealVariable realVariable = (RealVariable)variables.get(i);
			Assert.assertEquals(lowerBounds[i], realVariable.getLowerBound(), TestThresholds.HIGH_PRECISION);
			Assert.assertEquals(upperBounds[i], realVariable.getUpperBound(), TestThresholds.HIGH_PRECISION);
		}
	}
	
	@Test
	public void testParseVariablesWithShortAlias() throws ParseException {
		CommandLine commandLine = createCommandLine(
				"--variables", "R(0.0,1.0);B(2);I(-3,3);P(4);S(5,6)");
		
		List<Variable> variables = solve.parseVariables(commandLine);
		Assert.assertEquals(5, variables.size());
		
		RealVariable realVariable = (RealVariable)variables.get(0);
		Assert.assertEquals(0.0, realVariable.getLowerBound(), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(1.0, realVariable.getUpperBound(), TestThresholds.HIGH_PRECISION);
		
		BinaryVariable binaryVariable = (BinaryVariable)variables.get(1);
		Assert.assertEquals(2, binaryVariable.getNumberOfBits());
		
		BinaryIntegerVariable binaryIntegerVariable = (BinaryIntegerVariable)variables.get(2);
		Assert.assertEquals(-3, binaryIntegerVariable.getLowerBound());
		Assert.assertEquals(3, binaryIntegerVariable.getUpperBound());
		
		Permutation permutation = (Permutation)variables.get(3);
		Assert.assertEquals(4, permutation.size());
		
		Subset subset = (Subset)variables.get(4);
		Assert.assertEquals(5, subset.getL());
		Assert.assertEquals(5, subset.getU());
		Assert.assertEquals(6, subset.getN());
	}
	
	@Test
	public void testParseVariablesWithAlias() throws ParseException {
		CommandLine commandLine = createCommandLine(
				"--variables", "Real(0.0,1.0);Binary(2);Integer(-3,3);Permutation(4);Subset(5,6)");
		
		List<Variable> variables = solve.parseVariables(commandLine);
		Assert.assertEquals(5, variables.size());
		
		RealVariable realVariable = (RealVariable)variables.get(0);
		Assert.assertEquals(0.0, realVariable.getLowerBound(), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(1.0, realVariable.getUpperBound(), TestThresholds.HIGH_PRECISION);
		
		BinaryVariable binaryVariable = (BinaryVariable)variables.get(1);
		Assert.assertEquals(2, binaryVariable.getNumberOfBits());
		
		BinaryIntegerVariable binaryIntegerVariable = (BinaryIntegerVariable)variables.get(2);
		Assert.assertEquals(-3, binaryIntegerVariable.getLowerBound());
		Assert.assertEquals(3, binaryIntegerVariable.getUpperBound());
		
		Permutation permutation = (Permutation)variables.get(3);
		Assert.assertEquals(4, permutation.size());
		
		Subset subset = (Subset)variables.get(4);
		Assert.assertEquals(5, subset.getL());
		Assert.assertEquals(5, subset.getU());
		Assert.assertEquals(6, subset.getN());
	}
	
	@Test(expected = ParseException.class)
	public void testParseVariablesMissingOption() throws ParseException {
		solve.parseVariables(createCommandLine());
	}
	
	@Test(expected = ParseException.class)
	public void testParseVariablesMissingLowerBounds() throws ParseException {
		solve.parseVariables(createCommandLine(
				"--upperBounds", convert(new double[] { 10, 10.5, 20 }, ',')));
	}
	
	@Test(expected = ParseException.class)
	public void testParseVariablesMissingUpperBounds() throws ParseException {
		solve.parseVariables(createCommandLine(
				"--lowerBounds", convert(new double[] { 0, -10.5, 10 }, ',')));
	}
	
	@Test(expected = ParseException.class)
	public void testParseVariablesWithMismatchLength() throws ParseException {
		solve.parseVariables(createCommandLine(
				"--lowerBounds", convert(new double[] { 0, -10.5, 10 }, ','),
				"--upperBounds", convert(new double[] { 10, 10.5 }, ',')));
	}
	
	@Test(expected = FrameworkException.class)
	public void testParseVariablesWithInvalidType() throws ParseException {
		solve.parseVariables(createCommandLine(
				"--variables", "Foo(0,1)"));
	}
	
	@Test
	public void testParseObjectivesWithDefaults() throws ParseException {
		CommandLine commandLine = createCommandLine(
				"--objectives", "2");
		
		List<Objective> objectives = solve.parseObjectives(commandLine);
		Assert.assertEquals(2, objectives.size());
		Assert.assertInstanceOf(Minimize.class, objectives.get(0));
		Assert.assertInstanceOf(Minimize.class, objectives.get(1));
	}
	
	@Test
	public void testParseObjectivesWithAlias() throws ParseException {
		CommandLine commandLine = createCommandLine(
				"--objectives", "Min;Max");
		
		List<Objective> objectives = solve.parseObjectives(commandLine);
		Assert.assertEquals(2, objectives.size());
		Assert.assertInstanceOf(Minimize.class, objectives.get(0));
		Assert.assertInstanceOf(Maximize.class, objectives.get(1));
	}
	
	@Test
	public void testParseConstraintsWithMissingOption() throws ParseException {
		CommandLine commandLine = createCommandLine();
		
		List<Constraint> constraints = solve.parseConstraints(commandLine);
		Assert.assertEquals(0, constraints.size());
	}
	
	@Test
	public void testParseConstraintsWithNoConstraints() throws ParseException {
		CommandLine commandLine = createCommandLine(
				"--constraints", "0");
		
		List<Constraint> constraints = solve.parseConstraints(commandLine);
		Assert.assertEquals(0, constraints.size());
	}
	
	@Test
	public void testParseConstraintsWithDefault() throws ParseException {
		CommandLine commandLine = createCommandLine(
				"--constraints", "2");
		
		List<Constraint> constraints = solve.parseConstraints(commandLine);
		Assert.assertEquals(2, constraints.size());
		Assert.assertInstanceOf(Equal.class, constraints.get(0));
		Assert.assertInstanceOf(Equal.class, constraints.get(1));
	}
	
	@Test
	public void testParseConstraintsWithAlias() throws ParseException {
		CommandLine commandLine = createCommandLine(
				"--constraints", "EQ(0.0);NEQ(1.0);LT(3.0);LEQ(4.0);GT(5.0);GEQ(6.0);Between(7.0,8.0);Outside(9.0,10.0)");
		
		List<Constraint> constraints = solve.parseConstraints(commandLine);
		Assert.assertEquals(8, constraints.size());
		
		Assert.assertInstanceOf(Equal.class, constraints.get(0));
		Assert.assertEquals(0.0, ((Equal)constraints.get(0)).getThreshold());
		
		Assert.assertInstanceOf(NotEqual.class, constraints.get(1));
		Assert.assertEquals(1.0, ((NotEqual)constraints.get(1)).getThreshold());
		
		Assert.assertInstanceOf(LessThan.class, constraints.get(2));
		Assert.assertEquals(3.0, ((LessThan)constraints.get(2)).getThreshold());
		
		Assert.assertInstanceOf(LessThanOrEqual.class, constraints.get(3));
		Assert.assertEquals(4.0, ((LessThanOrEqual)constraints.get(3)).getThreshold());
		
		Assert.assertInstanceOf(GreaterThan.class, constraints.get(4));
		Assert.assertEquals(5.0, ((GreaterThan)constraints.get(4)).getThreshold());
		
		Assert.assertInstanceOf(GreaterThanOrEqual.class, constraints.get(5));
		Assert.assertEquals(6.0, ((GreaterThanOrEqual)constraints.get(5)).getThreshold());
		
		Assert.assertInstanceOf(Between.class, constraints.get(6));
		Assert.assertEquals(7.0, ((Between)constraints.get(6)).getLower());
		Assert.assertEquals(8.0, ((Between)constraints.get(6)).getUpper());
		
		Assert.assertInstanceOf(Outside.class, constraints.get(7));
		Assert.assertEquals(9.0, ((Outside)constraints.get(7)).getLower());
		Assert.assertEquals(10.0, ((Outside)constraints.get(7)).getUpper());
	}

	@Test
	public void testInternalProblem() throws Exception {
		CommandLine commandLine = createCommandLine(
				"--problem", "DTLZ2_2",
				"--numberOfEvaluations", "1000");
		
		solve.run(commandLine);
		checkOutput(commandLine, 10);
	}
	
	@Test(expected = ParseException.class)
	public void testInternalProblemWithArgs() throws Exception {
		CommandLine commandLine = createCommandLine(
				"--problem", "DTLZ2_2",
				"--numberOfEvaluations", "1000",
				"--",
				Settings.getPythonCommand(),
				SCRIPT.getAbsolutePath());
		
		solve.run(commandLine);
	}

	@Test
	public void testExternalProblem() throws Exception {
		Assume.assumePythonExists();
		
		CommandLine commandLine = createCommandLine(
				"--numberOfEvaluations", "1000",
				"--variables", "R(0,1);R(0,1);R(0,1);R(0,1);R(0,1);R(0,1);R(0,1);R(0,1);R(0,1);R(0,1);R(0,1)",
				"--objectives", "2",
				"--test",
				"--",
				Settings.getPythonCommand(),
				SCRIPT.getAbsolutePath());
		
		solve.run(commandLine);
		
		commandLine = createCommandLine(
				"--numberOfEvaluations", "1000",
				"--variables", "R(0,1);R(0,1);R(0,1);R(0,1);R(0,1);R(0,1);R(0,1);R(0,1);R(0,1);R(0,1);R(0,1)",
				"--objectives", "2",
				"--",
				Settings.getPythonCommand(),
				SCRIPT.getAbsolutePath());
		
		solve.run(commandLine);
		checkOutput(commandLine, 10);
	}
	
	@Test
	public void testExternalProblemWithSockets() throws Exception {
		Assume.assumePythonExists();
		
		CommandLine commandLine = createCommandLine(
				"--numberOfEvaluations", "1000",
				"--variables", "R(0,1);R(0,1);R(0,1);R(0,1);R(0,1);R(0,1);R(0,1);R(0,1);R(0,1);R(0,1);R(0,1)",
				"--objectives", "2",
				"--useSocket",
				"--test",
				"--",
				Settings.getPythonCommand(),
				SCRIPT.getAbsolutePath(),
				"--sockets");
		
		solve.run(commandLine);
		
		commandLine = createCommandLine(
				"--numberOfEvaluations", "1000",
				"--variables", "R(0,1);R(0,1);R(0,1);R(0,1);R(0,1);R(0,1);R(0,1);R(0,1);R(0,1);R(0,1);R(0,1)",
				"--objectives", "2",
				"--useSocket",
				"--",
				Settings.getPythonCommand(),
				SCRIPT.getAbsolutePath(),
				"--sockets");
		
		solve.run(commandLine);
		checkOutput(commandLine, 10);
	}
	
	private CommandLine createCommandLine(String... args) throws ParseException {
		List<String> completeArgs = new ArrayList<>();
		
		if (Stream.of(args).noneMatch(x -> x.equals("--algorithm") || x.equals("-a"))) {
			completeArgs.add("--algorithm");
			completeArgs.add("NSGAII");
		}
		
		if (Stream.of(args).noneMatch(x -> x.equals("--numberOfEvaluations") || x.equals("-n"))) {
			completeArgs.add("--numberOfEvaluations");
			completeArgs.add("10000");
		}
		
		if (Stream.of(args).noneMatch(x -> x.equals("--output") || x.equals("-f"))) {
			try {
				File tempFile = TempFiles.createFile();
				completeArgs.add("--output");
				completeArgs.add(tempFile.getAbsolutePath());
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}

		completeArgs.addAll(Arrays.asList(args));
		
		return new DefaultParser().parse(solve.getOptions(), completeArgs.toArray(String[]::new), true);
	}
	
	private String convert(double[] values, char separator) {
		return convert(Arrays.stream(values).mapToObj(Double::toString).toArray(String[]::new), separator);
	}
	
	private String convert(String[] values, char separator) {
		return Arrays.stream(values).collect(Collectors.joining(String.valueOf(separator)));
	}
	
	private void checkOutput(CommandLine commandLine, int expectedEntries) throws IOException {
		int count = 0;
		Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		File outputFile = new File(commandLine.getOptionValue("output"));
		
		try (ResultFileReader reader = ResultFileReader.open(problem, outputFile)) {
			while (reader.hasNext()) {
				Assert.assertNotEmpty(reader.next().getPopulation());
				count++;
			}
		}
		
		Assert.assertEquals(expectedEntries, count);
	}

}
