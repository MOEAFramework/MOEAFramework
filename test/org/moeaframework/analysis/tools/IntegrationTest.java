package org.moeaframework.analysis.tools;

import java.io.File;

import org.junit.Test;
import org.moeaframework.TestUtils;

/**
 * Integration tests for the command line utilities.  These tests only automate
 * checks to ensure the command line utilities interoperate and that their
 * command line interfaces function appropriately; not that their internal
 * behavior is valid.  Unit tests of the internal components ensure validity.
 */
public class IntegrationTest {

	@Test
	public void testARFFConverter() throws Exception {
		File resultFile = TestUtils.createTempFile();
		File arffFile = TestUtils.createTempFile();
		
		Solve.main(new String[] {
				"-a", "NSGAII",
				"-b", "DTLZ2_2",
				"-n", "1000",
				"-f", resultFile.getPath() });
		
		ARFFConverter.main(new String[] {
				"-b", "DTLZ2_2",
				"-i", resultFile.getPath(),
				"-o", arffFile.getPath() });
		
		TestUtils.assertLinePattern(arffFile, "^([@%].*)|(" +
				TestUtils.getCommaSeparatedNumericPattern(13) + ")$");
	}
	
	@Test
	public void testAerovisConverter() throws Exception {
		File resultFile = TestUtils.createTempFile();
		File aerovisFile = TestUtils.createTempFile();
		
		Solve.main(new String[] {
				"-a", "NSGAII",
				"-b", "DTLZ2_2",
				"-n", "1000",
				"-f", resultFile.getPath() });
		
		AerovisConverter.main(new String[] {
				"-b", "DTLZ2_2",
				"-i", resultFile.getPath(),
				"-o", aerovisFile.getPath() });
		
		TestUtils.assertLinePattern(aerovisFile,
				"^(#.*)|(" +
				TestUtils.getSpaceSeparatedNumericPattern(2) +
				")|(" +
				TestUtils.getSpaceSeparatedNumericPattern(13) +
				")$");
	}
	
}
