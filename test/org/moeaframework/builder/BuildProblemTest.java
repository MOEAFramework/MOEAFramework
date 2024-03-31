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

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.util.io.RedirectStream;

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
		test("fortran");
	}
	
	@Test
	public void testJava() throws Exception {
		test("java");
	}

	@Test
	public void testExternal() throws Exception {
		test("external");
	}
	
	@Test(expected = Exception.class)
	public void testDisallowExample() throws Exception {
		Path directory = Files.createTempDirectory("test");

		BuildProblem.main(new String[] {
				"--problemName", "Example",
				"--language", "c",
				"--numberOfVariables", "10",
				"--numberOfObjectives", "2",
				"--directory", directory.toString()
		});
		
		BuildProblem.deleteDirectory(directory);
	}

	private void test(String language) throws Exception {		
		Path directory = Files.createTempDirectory("test");

		BuildProblem.main(new String[] {
				"--problemName", "Test",
				"--language", language,
				"--numberOfVariables", "10",
				"--numberOfObjectives", "2",
				"--directory", directory.toString(),
				"--classpath", System.getProperty("java.class.path") + File.pathSeparator + "."
		});

		try {
			TestUtils.assumeMakeExists();
	
			ProcessBuilder processBuilder = new ProcessBuilder("make");
			processBuilder.directory(directory.resolve("Test").toFile());
			RedirectStream.invoke(processBuilder);
	
			processBuilder = new ProcessBuilder("make", "run");
			processBuilder.directory(directory.resolve("Test").toFile());
			String output = RedirectStream.capture(processBuilder);
			
			System.out.println(output);
			
			List<String> lines = output.lines().toList();
			Assert.assertEquals(4, lines.size());
			TestUtils.assertLineMatches(lines.get(0), "(\\bVar[0-9]+\\b\\s*){10}(\\bObj[0-9]+\\b\\s*){2}");
			TestUtils.assertLineMatches(lines.get(1), "([\\-]+\\s*){12}");
			TestUtils.assertLineMatches(lines.get(2), "(\\-?[0-9]+\\.[0-9]+\\b\\s*){12}");
			TestUtils.assertLineBlank(lines.get(3));
		} finally {
			BuildProblem.deleteDirectory(directory);
		}
	}

}
