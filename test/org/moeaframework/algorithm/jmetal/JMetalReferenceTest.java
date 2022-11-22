/* Copyright 2009-2022 David Hadka
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
package org.moeaframework.algorithm.jmetal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.Assert;
import org.junit.Test;

/**
 * Scans the src/ tree for any references to JMetal outside of designated packages.
 */
public class JMetalReferenceTest {

	@Test
	public void testForJMetalReferences() throws IOException {
		Files.walk(new File("src/").toPath()).forEach((path) -> {
			if (path.startsWith("src/org/moeaframework/algorithm/jmetal") ||
					path.startsWith("src/main/java/org/moeaframework/algorithm/jmetal") ||
					path.startsWith("src/test/")) {
				return;
			}
			
			try {
				assertNoJMetalReference(path.toFile());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}
	
	private void assertNoJMetalReference(File file) throws FileNotFoundException, IOException {
		if (file.isFile() && file.getName().endsWith(".java")) {
			 try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
				 String line = null;
				 
				 while ((line = reader.readLine()) != null) {
					 if (line.contains("org.uma.jmetal")) {
						 Assert.fail("Found JMetal reference in " + file);
					 }
				 }
			 }
		}
	}

}
