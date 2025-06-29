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
package org.moeaframework.util;

import java.io.IOException;
import java.nio.file.Files;
import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TempFiles;

public class JavaBuilderTest {
	
	private static final String PROGRAM = """
		public class Test {
			public static void main(String[] args) {
				System.out.println("Hello world!");
			}
		}
		""";
	
	@Test
	public void testMissingBuildPath() throws IOException {
		File tempDirectory = TempFiles.createDirectory();
		FileUtils.deleteQuietly(tempDirectory);
		
		new JavaBuilder().buildPath(tempDirectory);
		
		Assert.assertFileExists(tempDirectory);
	}
	
	@Test
	public void testMissingSourcePath() throws IOException {
		File tempDirectory = TempFiles.createDirectory();
		FileUtils.deleteQuietly(tempDirectory);
		
		new JavaBuilder().sourcePath(tempDirectory);
		
		Assert.assertFileNotExists(tempDirectory);
	}
	
	@Test
	public void testGetFullyQualifiedClassName() throws IOException {
		Assert.assertEquals("Test",
				new JavaBuilder().getFullyQualifiedClassName(new File("Test.java")));
		
		Assert.assertEquals("org.moeaframework.Test",
				new JavaBuilder().getFullyQualifiedClassName(new File("org/moeaframework/Test.java")));
		
		Assert.assertEquals("org.moeaframework.Test",
				new JavaBuilder()
					.sourcePath(new File("examples/"))
					.getFullyQualifiedClassName(new File("examples/org/moeaframework/Test.java")));
	}
	
	@Test
	public void testCompile() throws IOException {
		File sourceDirectory = TempFiles.createDirectory();
		File sourceFile = new File(sourceDirectory, "Test.java");
		File classFile = new File(sourceDirectory, "Test.class");

		Files.writeString(sourceFile.toPath(), PROGRAM);
		
		Assert.assertTrue(new JavaBuilder().compile(sourceFile));
		Assert.assertFileExists(classFile);
	}
	
	@Test
	public void testCompileToBuildPath() throws IOException {
		File sourceDirectory = TempFiles.createDirectory();
		File buildDirectory = TempFiles.createDirectory();
		File sourceFile = new File(sourceDirectory, "Test.java");
		File classFile = new File(buildDirectory, "Test.class");

		Files.writeString(sourceFile.toPath(), PROGRAM);
		
		Assert.assertTrue(new JavaBuilder().buildPath(buildDirectory).compile(sourceFile));
		Assert.assertFileExists(classFile);
	}
	
	@Test
	public void testCompileWithClassName() throws IOException {
		File sourceDirectory = TempFiles.createDirectory();
		File sourceFile = new File(sourceDirectory, "Test.java");
		File classFile = new File(sourceDirectory, "Test.class");

		Files.writeString(sourceFile.toPath(), PROGRAM);
		
		Assert.assertTrue(new JavaBuilder().sourcePath(sourceDirectory).compile("Test"));
		Assert.assertFileExists(classFile);
	}
	
	@Test
	public void testClassLoader() throws IOException, ClassNotFoundException {
		File sourceDirectory = TempFiles.createDirectory();
		File buildDirectory = TempFiles.createDirectory();
		File sourceFile = new File(sourceDirectory, "Test.java");

		Files.writeString(sourceFile.toPath(), PROGRAM);
		
		JavaBuilder builder = new JavaBuilder().buildPath(buildDirectory);
		
		Assert.assertTrue(builder.compile(sourceFile));
		
		Class<?> compiledClass = Class.forName("Test", true, builder.getClassLoader());
		
		Assert.assertNotNull(compiledClass);
		Assert.assertEquals("Test", compiledClass.getName());
	}
	
}
