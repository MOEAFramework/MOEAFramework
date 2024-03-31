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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.util.io.RedirectStream;

public class CreateNativeProblemTest {

	@Test
	public void test() throws Exception {
		TestUtils.assumeMakeExists();
		
		Path directory = Files.createTempDirectory("test");
		
		CreateNativeProblem.main(new String[] {
				"--problemName", "Test",
				"--language", "C",
				"--numberOfVariables", "10",
				"--numberOfObjectives", "2",
				"--directory", directory.toString()
		});
		
		ProcessBuilder processBuilder = new ProcessBuilder("make");
		processBuilder.directory(directory.resolve("Test").toFile());
		RedirectStream.invoke(processBuilder);
		
		processBuilder = new ProcessBuilder("make", "run");
		processBuilder.directory(directory.resolve("Test").toFile());
		String output = RedirectStream.capture(processBuilder);
		
		List<String> lines = output.lines().toList();
		Assert.assertEquals(3, lines.size());
		Assert.assertTrue(lines.get(0).startsWith("Var1"));
		Assert.assertTrue(lines.get(1).startsWith("---"));
		Assert.assertTrue(lines.get(2).startsWith("0.") || lines.get(2).startsWith("1."));
	}

}
