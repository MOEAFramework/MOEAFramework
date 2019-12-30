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
package org.moeaframework;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.junit.Assert;
import org.junit.Assume;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.util.io.CommentedLineReader;

/**
 * Utility methods for testing.
 */
public class TestUtils {
	
	/**
	 * Default floating-point equality allowing 5% relative error.
	 */
	public static final FloatingPointError DEFAULT_ERROR = 
			new RelativeError(0.05);

	/**
	 * Private constructor to prevent instantiation.
	 */
	private TestUtils() {
		super();
	}
	
	/**
	 * Asserts that two boolean arrays are equal.
	 * 
	 * @param expected the expected array
	 * @param actual the actual array
	 */
	public static void assertEquals(boolean[] expected, boolean[] actual) {
		for (int i=0; i<expected.length; i++) {
			Assert.assertEquals(expected[i], actual[i]);
		}
	}
	
	/**
	 * Asserts that two double matrices are equal.
	 * 
	 * @param expected the expected matrix
	 * @param actual the actual matrix
	 */
	public static void assertEquals(double[][] expected, double[][] actual) {
	    Assert.assertEquals(expected.length, actual.length);
	    
	    for (int i=0; i<expected.length; i++) {
	        Assert.assertEquals(expected[i].length, actual[i].length);
	        
	        for (int j=0; j<expected[i].length; j++) {
	            assertEquals(expected[i][j], actual[i][j]);
	        }
	    }
	}

	/**
	 * Asserts that two populations are equal using the
	 * {@link #equals(Population, Population)} method.
	 * 
	 * @param p1 the first population
	 * @param p2 the second population
	 */
	public static void assertEquals(Population p1, Population p2) {
		Assert.assertTrue(equals(p1, p2));
	}

	/**
	 * Returns {@code true} if the two populations are equal; otherwise
	 * {@code false}. Two populations are equal if all solutions contained
	 * in one population are contained in the other population.
	 * 
	 * @param p1 the first population
	 * @param p2 the second population
	 * @return {@code true} if the two populations are equal; otherwise
	 *         {@code false}
	 */
	public static boolean equals(Population p1, Population p2) {
		if (p1.size() != p2.size()) {
			return false;
		}

		BitSet matched1 = new BitSet(p1.size());
		BitSet matched2 = new BitSet(p2.size());

		for (int i = 0; i < p1.size(); i++) {
			for (int j = 0; j < p2.size(); j++) {
				if (equals(p1.get(i), p2.get(j))) {
					matched1.set(i);
					matched2.set(j);
				}
			}
		}
		
		return (matched1.cardinality() == p1.size())
				&& (matched2.cardinality() == p2.size());
	}

	/**
	 * Asserts that the two solutions are equal using the
	 * {@link #equals(Solution, Solution)} method.
	 * 
	 * @param s1 the first solution
	 * @param s2 the second solution
	 */
	public static void assertEquals(Solution s1, Solution s2) {
		Assert.assertTrue(equals(s1, s2));
	}

	/**
	 * Returns {@code true} if the two solutions are equal; {@code false}
	 * otherwise. This method is not supported in the core library as the
	 * process of comparing solutions is often domain-specific.
	 * 
	 * @param s1 the first solution
	 * @param s2 the second solution
	 * @return {@code true} if the two solutions are equal; {@code false}
	 *         otherwise
	 */
	public static boolean equals(Solution s1, Solution s2) {
		if (s1.getNumberOfVariables() != s2.getNumberOfVariables()) {
			return false;
		}

		if (s1.getNumberOfObjectives() != s2.getNumberOfObjectives()) {
			return false;
		}

		if (s1.getNumberOfConstraints() != s2.getNumberOfConstraints()) {
			return false;
		}

		for (int i = 0; i < s1.getNumberOfVariables(); i++) {
			if (!s1.getVariable(i).equals(s2.getVariable(i))) {
				return false;
			}
		}

		for (int i = 0; i < s1.getNumberOfObjectives(); i++) {
			if (Math.abs(s1.getObjective(i) - s2.getObjective(i)) >= 
					TestThresholds.SOLUTION_EPS) {
				return false;
			}
		}

		for (int i = 0; i < s1.getNumberOfConstraints(); i++) {
			if (Math.abs(s1.getConstraint(i) - s2.getConstraint(i)) >= 
					TestThresholds.SOLUTION_EPS) {
				return false;
			}
		}
		
		//TODO: the attributes check does not work correctly if it contains
		//arrays since it's using an identity check and not a value check
		//return s1.getAttributes().equals(s2.getAttributes());
		return true;
	}

	/**
	 * Creates a temporary file for testing purposes. The temporary file will
	 * be deleted on exit.
	 * 
	 * @return the temporary file
	 * @throws IOException if an I/O error occurred
	 */
	public static File createTempFile() throws IOException {
		File file = File.createTempFile("test", null);
		file.deleteOnExit();
		return file;
	}

	/**
	 * Creates a temporary file containing the specified data. The temporary
	 * file will be deleted on exit.
	 * 
	 * @param data the contents of the temporary file
	 * @return the temporary file
	 * @throws IOException if an I/O error occurred
	 */
	public static File createTempFile(String data) throws IOException {
		File file = createTempFile();

		Writer writer = null;

		try {
			writer = new BufferedWriter(new FileWriter(file));
			writer.write(data);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}

		return file;
	}
	
	/**
	 * Returns a new solution with the specified objective values.
	 * 
	 * @param objectives the objective values
	 * @return a new solution with the specified objective values
	 */
	public static Solution newSolution(double... objectives) {
		return new Solution(objectives);
	}
	
	/**
	 * Returns the solution resulting from evaluating the problem with the
	 * specified decision variables.
	 * 
	 * @param problem the problem
	 * @param variables the decision variable values
	 * @return the solution resulting from evaluating the problem with the
	 *         specified decision variables
	 */
	public static Solution evaluateAt(Problem problem, 
			double... variables) {
		Solution solution = problem.newSolution();
		EncodingUtils.setReal(solution, variables);
		problem.evaluate(solution);
		return solution;
	}
	
	/**
	 * Returns the number of lines in the specified file.
	 * 
	 * @param file the file
	 * @return the number of lines in the specified file
	 * @throws IOException if an I/O error occurred
	 */
	public static int lineCount(File file) throws IOException {
		BufferedReader reader = null;
		int count = 0;
		
		try {
			reader = new BufferedReader(new FileReader(file));
			
			while (reader.readLine() != null) {
				count++;
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		
		return count;
	}
	
	/**
	 * Asserts that every line on the file matches the specified regular
	 * expression pattern.  This method automatically ignores commented lines.
	 * 
	 * @param file the file
	 * @param regex the regular expression pattern
	 * @throws IOException if an I/O error occurred
	 */
	public static void assertLinePattern(File file, String regex) 
	throws IOException {
		CommentedLineReader reader = null;
		Pattern pattern = Pattern.compile(regex);
		
		try {
			reader = new CommentedLineReader(new FileReader(file));
			String line = null;
			
			while ((line = reader.readLine()) != null) {
				if (!pattern.matcher(line).matches()) {
					Assert.fail("line does not match pattern: " + line);
				}
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}
	
	/**
	 * Invokes the main method of the specified command line utility, 
	 * redirecting the output to the specified file.  As this method redirects
	 * the {@code System.out} stream, this must be the only process writing to 
	 * {@code System.out}.
	 * 
	 * @param output the file for output redirection
	 * @param tool the command line utility class
	 * @param args the command line arguments
	 * @throws Exception if any of the many exceptions for reflection or
	 *         file writing occurred
	 */
	public static void pipeCommandLine(File output, Class<?> tool, 
			String... args) throws Exception {
		PrintStream oldOut = System.out;
		PrintStream newOut = null;
		
		try {
			newOut = new PrintStream(new FileOutputStream(output));
			System.setOut(newOut);
		
			Method mainMethod = tool.getMethod("main", String[].class);
			mainMethod.invoke(null, (Object)args);
		} finally {
			if (newOut != null) {
				newOut.close();
			}
			
			System.setOut(oldOut);
		}
	}
	
	/**
	 * Invokes the main method of the specified command line utility, 
	 * redirecting the output and error streams to the specified files.  As 
	 * this method redirects the {@code System.out} and {@code System.err} 
	 * streams, this must be the only process writing to {@code System.out}
	 * and {@code System.err}.
	 * 
	 * @param output the file for output redirection
	 * @param error the file for error redirection
	 * @param tool the command line utility class
	 * @param args the command line arguments
	 * @throws Exception if any of the many exceptions for reflection or
	 *         file writing occurred
	 */
	public static void pipeCommandLine(File output, File error, Class<?> tool,
			String... args) throws Exception {
		PrintStream oldErr = System.err;
		PrintStream newErr = null;
		
		try {
			newErr = new PrintStream(new FileOutputStream(error));
			System.setErr(newErr);
		
			pipeCommandLine(output, tool, args);
		} finally {
			if (newErr != null) {
				newErr.close();
			}
			
			System.setErr(oldErr);
		}
	}
	
	/**
	 * Asserts that the two floating-point values are equal.
	 * 
	 * @param d1 the first floating-point value
	 * @param d2 the second floating-point value
	 */
	public static void assertEquals(double d1, double d2) {
		try {
			DEFAULT_ERROR.assertEquals(d1, d2);
		} catch (AssertionError e) {
			//relative equality breaks down when the values approach 0; this
			//is an attempt to correct using absolute difference
			if (((d1 != 0.0) && (d2 != 0.0)) || (Math.abs(d1 - d2) > 0.05)) {
				throw e;
			}
		}
	}
	
	/**
	 * Asserts that the two matrices are equal.
	 * 
	 * @param rm1 the first matrix
	 * @param rm2 the second matrix
	 * @param error the equality comparison used to assert pairwise values 
	 *        are equal
	 */
	public static void assertEquals(RealMatrix rm1, RealMatrix rm2, 
			FloatingPointError error) {
		Assert.assertEquals(rm1.getRowDimension(), rm2.getRowDimension());
		Assert.assertEquals(rm1.getColumnDimension(), rm2.getColumnDimension());
		
		for (int i = 0; i < rm1.getRowDimension(); i++) {
			for (int j = 0; j < rm2.getColumnDimension(); j++) {
				error.assertEquals(rm1.getEntry(i, j), rm2.getEntry(i, j));
			}
		}
	}
	
	/**
	 * Loads the contents of the specified file.
	 * 
	 * @param file the file to load
	 * @return the contents of the file
	 * @throws IOException if an I/O error occurred
	 */
	public static byte[] loadFile(File file) throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		FileInputStream fis = null;
		int length = 0;
		byte[] buffer = new byte[Settings.BUFFER_SIZE];
		
		try {
			fis = new FileInputStream(file);
			
			while ((length = fis.read(buffer)) != -1) {
				bytes.write(buffer, 0, length);
			}
			
			return bytes.toByteArray();
		} finally {
			if (fis != null) {
				fis.close();
			}
		}
	}
	
	/**
	 * Loads the contents of the specified file, returning the matrix of values
	 * stored in the file.
	 * 
	 * @param file the file to load
	 * @return the matrix of values stored in the file
	 * @throws IOException if an I/O error occurred
	 */
    public static double[][] loadMatrix(File file) throws IOException {
        List<double[]> data = new ArrayList<double[]>();
        CommentedLineReader reader = null;
        String line = null;
        
        try {
            reader = new CommentedLineReader(new FileReader(file));
            
            while ((line = reader.readLine()) != null) {
            	String[] tokens = line.split("\\s+");
            	double[] row = new double[tokens.length];
            	
            	for (int i=0; i<tokens.length; i++) {
            		row[i] = Double.parseDouble(tokens[i]);
            	}
            	
            	data.add(row);
            }
            
            return data.toArray(new double[data.size()][]);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
	
	/**
	 * Asserts that the statistical distribution satisfies the properties of an
	 * integer-valued uniform distribution between {@code min} and {@code max}.
	 * 
	 * @param min the minimum bounds of the uniform distribution
	 * @param max the maximum bounds of the uniform distribution
	 * @param statistics the captures statistics of a sampled distribution
	 */
	public static void assertUniformDistribution(int min, int max,
			DescriptiveStatistics statistics) {
		int n = max - min + 1;
		int nn = n * n;

		assertEquals((min + max) / 2.0, statistics.getMean());
		assertEquals((nn - 1) / 12.0, statistics.getVariance());
		assertEquals(0.0, statistics.getSkewness());
		assertEquals(-(6.0 * (nn + 1)) / (5.0 * (nn - 1)), 
				statistics.getKurtosis());
		assertEquals(min, statistics.getMin());
		assertEquals(max, statistics.getMax());
	}
	
	/**
	 * Returns the regular expression pattern for detecting any number of 
	 * numeric values separated by whitespace.
	 * 
	 * @param n the number of numeric values to detect
	 * @return  the regular expression pattern for detecting any number of 
	 *          numeric values separated by whitespace
	 */
	public static String getSpaceSeparatedNumericPattern(int n) {
		return "(-?[0-9]+(\\.[0-9]+(E-?[0-9]+)?)?\\b\\s*){" + n + "}";
	}
	
	/**
	 * Returns the regular expression pattern for detecting any number of 
	 * numeric values separated by a comma.
	 * 
	 * @param n the number of numeric values to detect
	 * @return  the regular expression pattern for detecting any number of 
	 *          numeric values separated by a comma
	 */
	public static String getCommaSeparatedNumericPattern(int n) {
		String pattern = "";
		
		if (n > 0) {
			pattern = "(-?[0-9]+(\\.[0-9]+(E-?[0-9]+)?)?)";
		}
		
		if (n > 1) {
			pattern += "(\\s*,\\s*-?[0-9]+(\\.[0-9]+(E-?[0-9]+)?)?){" +
					(n-1) +
					"}";
		}
		
		return pattern;
	}
	
	/**
	 * Extracts the data stored in a resource, saving its contents to a
	 * temporary file.  If the resource name contains an extension, the file
	 * will be created with the extension.
	 * 
	 * @param resource the name of the resource to extract
	 * @return the temporary file containing the resource data
	 * @throws IOException if an I/O error occurred
	 */
	public static File extractResource(String resource) throws IOException {
		InputStream input = null;
		OutputStream output = null;
		byte[] buffer = new byte[Settings.BUFFER_SIZE];
		int len = -1;
		
		//determine the file extension, if any
		File file = null;
		int position = resource.indexOf('.', resource.lastIndexOf('/'));
		
		if (position < 0) {
			file = TestUtils.createTempFile();
		} else {
			file = File.createTempFile("test", resource.substring(position));
			file.deleteOnExit();
		}
		
		//copy the resource contents to the file
		try {
			input = TestUtils.class.getResourceAsStream(resource);
			
			if (input == null) {
				throw new IOException("resource not found: " + resource);
			}
			
			try {
				output = new FileOutputStream(file);
				
				while ((len = input.read(buffer)) != -1) {
					output.write(buffer, 0, len);
				}
			} finally {
				if (output != null) {
					output.close();
				}
			}
		} finally {
			if (input != null) {
				input.close();
			}
		}
		
		return file;
	}
	
	/**
	 * Skips the current test if the file does not exist.
	 * 
	 * @param file the file to check
	 */
	public static void assumeFileExists(File file) {
		if (!file.exists()) {
			System.err.println(file + " does not exist, skipping test");
			Assume.assumeTrue(false);
		}
	}
	
	/**
	 * Skips the current test if the machine is not POSIX-compliant.
	 */
	public static void assumePOSIX() {
		if (!SystemUtils.IS_OS_UNIX) {
			System.err.println("system is not POSIX-compliant, skipping test");
			Assume.assumeTrue(false);
		}
	}
	
	/**
	 * Attempts to run make in the given folder.  If make is not successful,
	 * the test is skipped.
	 * 
	 * @param folder the folder in which make is executed
	 */
	public static void runMake(File folder) {
		System.out.println("Running make to build test executables");
		
		try {
			Process process = Runtime.getRuntime().exec("make", null, folder);
			
			if (process.waitFor() != 0) {
				System.err.println("make exited with an error status ("
						+ process.exitValue() + "), skipping test");
				Assume.assumeTrue(false);
			}
		} catch (InterruptedException e) {
			System.err.println("interrupted while waiting for make to " +
					"complete, skipping test");
			Assume.assumeTrue(false);
		} catch (IOException e) {
			System.err.println("unable to run make, skipping test");
			Assume.assumeTrue(false);
		}
	}

}
