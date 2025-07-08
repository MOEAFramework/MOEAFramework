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
package org.moeaframework.problem.BBOB2016;

import java.io.PrintWriter;
import java.util.function.IntToDoubleFunction;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.moeaframework.core.PRNG;
import org.moeaframework.util.cli.CommandLineUtility;

public class BBOB2016TestGenerator extends CommandLineUtility {
	
	private static final long SEED = 12345;
	
	private static final int N = 3;

	@Override
	public Options getOptions() {
		Options options = super.getOptions();
		
		options.addOption(Option.builder("o")
				.longOpt("output")
				.hasArg()
				.argName("file")
				.build());
		
		return options;
	}
	
	@Override
	public void start(String[] args) throws Exception {
		try {
			super.start(args);
		} catch (UnsatisfiedLinkError e) {
			System.err.println("WARNING: CocoJNI not found, skipping generation of tests. Please compile and include");
			System.err.println("on the Java library path.");
			return;
		}
	}

	@Override
	public void run(CommandLine commandLine) throws Exception {
		PRNG.setSeed(SEED);
		CocoJNI.cocoSetLogLevel("error");
		
		String observerOptions = String.join(" ",
				"result_folder: Testing_on_bbob-biobj",
				"algorithm_name: Testing",
				"algorithm_info: \"MOEA Framework Testing\"");

		Suite suite = new Suite("bbob-biobj", "year: 2016", "dimensions: 2,3,5,10,20,40");
		Observer observer = new Observer("bbob-biobj", observerOptions);
		Benchmark benchmark = new Benchmark(suite, observer);
		Problem problem = null;
		
		String outputPath = commandLine.getOptionValue("output", "test/org/moeaframework/problem/BBOB2016/BBOB2016.Test.dat");
		System.out.println("Writing output to " + outputPath);
		
		try (PrintWriter out = createOutputWriter(outputPath)) {
			while ((problem = benchmark.getNextProblem()) != null) {
				evaluate(problem, generateLowerBounds(problem), out);
				evaluate(problem, generateUpperBounds(problem), out);
				
				for (int i = 0; i < N - 2; i++) {
					evaluate(problem, generateRandom(problem), out);
				}
			}
		}
		
		benchmark.finalizeBenchmark();
		
		// These cause issues when running in JUnit, since it kills the running process
		// observer.finalizeObserver();
		// suite.finalizeSuite();
	}
	
	private double[] generateLowerBounds(Problem problem) {
		return generate(problem, (i) -> -5.0);
	}
	
	private double[] generateUpperBounds(Problem problem) {
		return generate(problem, (i) -> 5.0);
	}
	
	private double[] generateRandom(Problem problem) {
		return generate(problem, (i) -> -5.0 + PRNG.nextDouble() * 10.0);
	}
	
	private double[] generate(Problem problem, IntToDoubleFunction generator) {
		double[] x = new double[problem.getDimension()];
		
		for (int i = 0; i < problem.getDimension(); i++) {
			x[i] = generator.applyAsDouble(i);
		}
		
		return x;
	}
	
	private void evaluate(Problem problem, double[] x, PrintWriter out) {
		double[] fx = problem.evaluateFunction(x);
		double[] cx = problem.getNumberOfConstraints() > 0 ? problem.evaluateConstraint(x) : new double[0];
		
		out.print(problem.getName());
		out.print(" ");
		out.print(problem.getDimension());
		out.print(" ");
		out.print(problem.getNumberOfObjectives());
		out.print(" ");
		out.print(problem.getNumberOfConstraints());
		out.print(" ");
		out.print(DoubleStream.of(x).mapToObj(Double::toString).collect(Collectors.joining(" ")));
		out.print(" ");
		out.print(DoubleStream.of(fx).mapToObj(Double::toString).collect(Collectors.joining(" ")));
		out.print(" ");
		out.print(DoubleStream.of(cx).mapToObj(Double::toString).collect(Collectors.joining(" ")));
		out.println();
	}
	
	public static void main(String[] args) throws Exception {
		new BBOB2016TestGenerator().start(args);
	}

}
