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
package org.moeaframework.builder;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.Assume;
import org.moeaframework.Capture;
import org.moeaframework.Capture.CaptureResult;
import org.moeaframework.Make;
import org.moeaframework.TempFiles;

public class BuildProblemTest {

	@Test
	public void testC() throws Exception {
		test("c");
	}

	@Test
	public void testCPP() throws Exception {
		test("cpp");
	}

	@Test
	public void testFortran() throws Exception {
		Assume.assumeFortranExists();
		test("fortran");
	}

	@Test
	public void testJava() throws Exception {
		test("java");
	}
	
	@Test
	public void testPython() throws Exception {
		Assume.assumePythonExists();
		test("python");
	}
	
	@Test
	public void testMatlabPartial() throws Exception {
		Assume.assumeMatlabExists();
		
		if (Assume.isGitHubActions()) {
			// Note: The licenses imported by the setup-matlab action does not enable MatlabEngine.  We can compile
			// the example but can't run to end-to-end.  See https://github.com/matlab-actions/setup-matlab/issues/13.
			File directory = test("matlab", false);
			
			CaptureResult result = Capture.output(new ProcessBuilder()
					.command("matlab", "-batch", "[objs, constrs] = evaluate(zeros(1, 10))")
					.directory(directory));
			
			result.assertSuccessful();
		} else {
			test("matlab");
		}
	}

	@Test
	public void testExternal() throws Exception {
		test("external");
	}

	@Test(expected = Exception.class)
	public void testDisallowExample() throws Exception {
		File directory = TempFiles.createDirectory();

		BuildProblem.main(new String[] {
				"--problemName", "Example",
				"--language", "c",
				"--numberOfVariables", "10",
				"--numberOfObjectives", "2",
				"--directory", directory.toString()
		});
	}
	
	@Test(expected = Exception.class)
	public void testInvalidProblemName() throws Exception {
		File directory = TempFiles.createDirectory();

		BuildProblem.main(new String[] {
				"--problemName", "Foo Bar",
				"--language", "c",
				"--numberOfVariables", "10",
				"--numberOfObjectives", "2",
				"--directory", directory.toString()
		});
	}
	
	@Test(expected = Exception.class)
	public void testInvalidFunctionName() throws Exception {
		File directory = TempFiles.createDirectory();

		BuildProblem.main(new String[] {
				"--problemName", "Test",
				"--functionName", "evaluate!",
				"--language", "c",
				"--numberOfVariables", "10",
				"--numberOfObjectives", "2",
				"--directory", directory.toString()
		});
	}
	
	private File test(String language) throws Exception {
		return test(language, true);
	}

	private File test(String language, boolean run) throws Exception {
		File directory = TempFiles.createDirectory();
		File testDirectory = new File(directory, "Test");

		BuildProblem.main(new String[] {
				"--problemName", "Test",
				"--language", language,
				"--numberOfVariables", "10",
				"--numberOfObjectives", "2",
				"--directory", directory.toString(),
				"--classpath", System.getProperty("java.class.path") + File.pathSeparator + "Test.jar" +
						File.pathSeparator + "."
		});

		Assume.assumeMakeExists();
		Make.runMake(testDirectory);
		
		if (run) {
			// remove any compiled files to verify they are packaged correctly in the JAR
			Set<String> extensionsToRemove = Set.of("exe", "dll", "so", "py", "m", "class");
			
			try (Stream<Path> stream = Files.walk(testDirectory.toPath())) {
				stream
					.filter(x -> extensionsToRemove.contains(FilenameUtils.getExtension(x.getFileName().toString())))
					.map(Path::toFile)
					.forEach(File::delete);
			}
			
			CaptureResult result = Capture.output(() -> Make.runMake(testDirectory, "run"));
			result.assertSuccessful();
	
			List<String> lines = result.toString().lines().skip(2).toList(); // skip first two lines
			Assert.assertEquals(3, lines.size());
			Assert.assertStringMatches(lines.get(0), "(\\bVar[0-9]+\\b\\s*){10}(\\bObj[0-9]+\\b\\s*){2}");
			Assert.assertStringMatches(lines.get(1), "([\\-]+\\s*){12}");
			Assert.assertStringMatches(lines.get(2), "(\\-?[0-9]+\\.[0-9]+\\b\\s*){12}");
		}
		
		return testDirectory;
	}

}
