package org.moeaframework.analysis.sensitivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.MultivariateRealFunction;
import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestThresholds;
import org.moeaframework.TestUtils;
import org.moeaframework.util.sequence.Saltelli;

public class SobolAnalysisTest {
	
	@Test
	public void testNoInteraction1() throws FunctionEvaluationException, IOException {
		File outputFile = TestUtils.createTempFile();
		
		test(outputFile, new MultivariateRealFunction() {

			@Override
			public double value(double[] variables)
					throws FunctionEvaluationException {
				return variables[0];
			}
			
		});
		
		assertEntryEquals(outputFile, "Variable1", 1.0);
		assertEntryEquals(outputFile, "Variable2", 0.0);
		assertEntryEquals(outputFile, "Variable3", 0.0);
		assertEntryEquals(outputFile, "Variable1 \\* Variable2", 0.0);
		assertEntryEquals(outputFile, "Variable1 \\* Variable3", 0.0);
		assertEntryEquals(outputFile, "Variable2 \\* Variable3", 0.0);
	}
	
	@Test
	public void testNoInteraction12() throws FunctionEvaluationException, IOException {
		File outputFile = TestUtils.createTempFile();
		
		test(outputFile, new MultivariateRealFunction() {

			@Override
			public double value(double[] variables)
					throws FunctionEvaluationException {
				return variables[0] + variables[1];
			}
			
		});
		
		assertEntryEquals(outputFile, "Variable1", 0.5);
		assertEntryEquals(outputFile, "Variable2", 0.5);
		assertEntryEquals(outputFile, "Variable3", 0.0);
		assertEntryEquals(outputFile, "Variable1 \\* Variable2", 0.0);
		assertEntryEquals(outputFile, "Variable1 \\* Variable3", 0.0);
		assertEntryEquals(outputFile, "Variable2 \\* Variable3", 0.0);
	}
	
	@Test
	public void testNoInteraction123() throws FunctionEvaluationException, IOException {
		File outputFile = TestUtils.createTempFile();
		
		test(outputFile, new MultivariateRealFunction() {

			@Override
			public double value(double[] variables)
					throws FunctionEvaluationException {
				return variables[0] + variables[1] + variables[2];
			}
			
		});
		
		assertEntryEquals(outputFile, "Variable1", 0.333);
		assertEntryEquals(outputFile, "Variable2", 0.333);
		assertEntryEquals(outputFile, "Variable3", 0.333);
		assertEntryEquals(outputFile, "Variable1 \\* Variable2", 0.0);
		assertEntryEquals(outputFile, "Variable1 \\* Variable3", 0.0);
		assertEntryEquals(outputFile, "Variable2 \\* Variable3", 0.0);
	}
	
	@Test
	public void testInteraction() throws FunctionEvaluationException, IOException {
		File outputFile = TestUtils.createTempFile();
		
		test(outputFile, new MultivariateRealFunction() {

			@Override
			public double value(double[] variables)
					throws FunctionEvaluationException {
				return variables[0]*variables[1] + variables[2];
			}
			
		});
		
		assertEntryNotEquals(outputFile, "Variable1", 0.0);
		assertEntryNotEquals(outputFile, "Variable2", 0.0);
		assertEntryNotEquals(outputFile, "Variable3", 0.0);
		assertEntryNotEquals(outputFile, "Variable1 \\* Variable2", 0.0);
		assertEntryEquals(outputFile, "Variable1 \\* Variable3", 0.0);
		assertEntryEquals(outputFile, "Variable2 \\* Variable3", 0.0);
	}
	
	protected void test(File outputFile, MultivariateRealFunction function) throws
	FunctionEvaluationException, IOException {
		double[][] input = new Saltelli().generate(10000*6, 3);
		double[] output = evaluate(function, input);
		
		File parameterFile = TestUtils.createTempFile();
		File inputFile = TestUtils.createTempFile();
		
		createParameterFile(parameterFile, 3);
		save(inputFile, output);
		
		SobolAnalysis.main(new String[] {
			"--parameterFile", parameterFile.getPath(),
			"--input", inputFile.getPath(),
			"--metric", "0",
			"--output", outputFile.getPath()
		});
	}
	
	protected void assertEntryEquals(File file, String key, double expected) 
			throws IOException {
		Assert.assertEquals(expected, getEntryValue(file, key), 
				TestThresholds.STATISTICS_EPS);
	}
	
	protected void assertEntryNotEquals(File file, String key, double expected)
			throws IOException {
		Assert.assertTrue(Math.abs(expected - getEntryValue(file, key)) > 
				TestThresholds.STATISTICS_EPS);
	}
	
	protected double getEntryValue(File file, String key) throws 
	NumberFormatException, IOException {
		BufferedReader reader = null;
		String line = null;
		Pattern pattern = Pattern.compile("^\\s*" + key + "\\s+" + 
				TestUtils.getNumericPattern(1) + "\\s+\\[" + 
				TestUtils.getNumericPattern(1) + "\\]\\s*$");
		
		try {
			reader = new BufferedReader(new FileReader(file));
			
			while ((line = reader.readLine()) != null) {
				Matcher matcher = pattern.matcher(line);
				
				if (matcher.matches()) {
					return Double.parseDouble(matcher.group(1));
				}
			}
			
			return Double.POSITIVE_INFINITY;
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}
	
	protected double[] evaluate(MultivariateRealFunction function, double[][] input) throws FunctionEvaluationException { 
		double[] output = new double[input.length];
		
		for (int i=0; i<input.length; i++) {
			output[i] = function.value(input[i]);
		}
		
		return output;
	}
	
	protected void save(File file, double[] data) throws IOException {
		PrintWriter writer = null;
		
		try {
			writer = new PrintWriter(file);
			
			for (int i=0; i<data.length; i++) {
				writer.print(data[i]);
				
				//pad with 0's to look like metric file
				for (int j=1; j<MetricFileWriter.NUMBER_OF_METRICS; j++) {
					writer.print(" 0.0");
				}
				
				writer.println();
			}
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
	
	protected void createParameterFile(File file, int dimension) throws IOException {
		PrintWriter writer = null;
		
		try {
			writer = new PrintWriter(file);
			
			for (int i=0; i<dimension; i++) {
				writer.print("Variable");
				writer.print(i+1);
				writer.print(' ');
				writer.print(0.0);
				writer.print(' ');
				writer.println(1.0);
			}
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

}
