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

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

/**
 * Utility for compiling Java source files.  The compiler runs within the context of the current Java environment, thus
 * the classpath and other properties are inherited.
 */
public class JavaBuilder {
	
	private static final JavaCompiler COMPILER;
	
	static {
		COMPILER = ToolProvider.getSystemJavaCompiler();
	}
	
	private final StandardJavaFileManager fileManager;
	
	private boolean clean;
	
	private Writer output;
	
	/**
	 * Creates a new Java builder instance.
	 * 
	 * @throws UnsupportedOperationException if the Java compiler is not available on the system
	 */
	public JavaBuilder() {
		super();
		
		if (COMPILER == null) {
			throw new UnsupportedOperationException("Java compiler not available on system");
		}
		
		fileManager = COMPILER.getStandardFileManager(null, null, null);
	}
	
	/**
	 * Returns {@code true} if the Java compiler is available on this system.  If unavailable, an exception will be
	 * thrown when trying to use this class.
	 * 
	 * @return {@code true} if the Java compiler is available; {@code false} otherwise
	 */
	public static boolean isAvailable() {
		return COMPILER != null;
	}
	
	/**
	 * Indicates if existing compiled class files should be forcefully deleted and rebuilt.
	 * 
	 * @param clean if {@code true}, files are always recompiled; if {@code false}, the file modification time is
	 *        checked to determine if recompilation is necessary
	 * @return a reference to this instance
	 */
	public JavaBuilder clean(boolean clean) {
		this.clean = clean;
		return this;
	}
	
	/**
	 * Provides a writer for any messages or errors emitted by the compiler.  If unset or {@code null},
	 * {@link System#err} is used.
	 * 
	 * @param output the output writer
	 * @return a reference to this instance
	 */
	public JavaBuilder output(Writer output) {
		this.output = output;
		return this;
	}
	
	/**
	 * Sets the directory where compiled class files are created.  If unset or {@code null}, the class files are
	 * typically stored alongside the originating source files.
	 * <p>
	 * As a side-effect, this method will create the build directory if it does not exist.
	 * 
	 * @param buildPath the build directory
	 * @return a reference to this instance
	 * @throws IOException if an I/O error occurred creating the directory
	 */
	public JavaBuilder buildPath(File buildPath) throws IOException {
		if (!buildPath.exists()) {
			FileUtils.forceMkdir(buildPath);
		}
		
		fileManager.setLocation(StandardLocation.CLASS_OUTPUT, buildPath == null ? null : List.of(buildPath));
		return this;
	}
	
	/**
	 * Sets the source path(s) the compiler searches for referenced source code.
	 * 
	 * @param sourcePath the source path(s)
	 * @return a reference to this instance
	 * @throws IOException if an I/O error occurred (should never happen)
	 */
	public JavaBuilder sourcePath(File... sourcePath) throws IOException {
		fileManager.setLocation(StandardLocation.SOURCE_PATH, sourcePath == null ? null : List.of(sourcePath));
		return this;
	}
	
	/**
	 * Returns a class loader intended for classes compiled using this tool.
	 * 
	 * @return the class loader
	 * @throws IOException if an I/O error occurred constructing the classpath
	 */
	public ClassLoader getClassLoader() throws IOException {
		List<URL> urls = new ArrayList<>();
		
		Iterable<? extends File> classOutputPaths = fileManager.getLocation(StandardLocation.CLASS_OUTPUT);
		Iterable<? extends File> classPaths = fileManager.getLocation(StandardLocation.CLASS_PATH);
		
		if (classOutputPaths != null) {
			for (File file : classOutputPaths) {
				urls.add(file.toURI().toURL());
			}
		}
		
		if (classPaths != null) {
			for (File file : classOutputPaths) {
				urls.add(file.toURI().toURL());
			}
		}

		return new URLClassLoader(urls.toArray(URL[]::new));
	}
	
	/**
	 * Compiles the Java source file.
	 * 
	 * @param file the Java source file
	 * @return {@code true} if compilation succeeded; {@code false} otherwise
	 * @throws IOException if an I/O error occurred during compilation
	 */
	public boolean compile(File file) throws IOException {
		return compile(
				Iterators.materialize(fileManager.getJavaFileObjects(file)).get(0),
				fileManager.getJavaFileForOutput(StandardLocation.CLASS_OUTPUT, getFullyQualifiedClassName(file),
						JavaFileObject.Kind.CLASS, null));
	}
	
	/**
	 * Compiles the named Java class.
	 * 
	 * @param className the class name
	 * @return {@code true} if compilation succeeded; {@code false} otherwise
	 * @throws IOException if an I/O error occurred during compilation
	 */
	public boolean compile(String className) throws IOException {
		return compile(
				fileManager.getJavaFileForInput(StandardLocation.SOURCE_PATH, className, JavaFileObject.Kind.SOURCE),
				fileManager.getJavaFileForOutput(StandardLocation.CLASS_OUTPUT, className, JavaFileObject.Kind.CLASS, null));
	}
	
	private boolean compile(JavaFileObject sourceFile, JavaFileObject classFile) {
		if (classFile.getLastModified() > sourceFile.getLastModified() && !clean) {
			return true;
		}
		
		return COMPILER.getTask(output, fileManager, null, null, null, List.of(sourceFile)).call();
	}
	
	/**
	 * Returns the fully-qualified class name for the Java source file.  The class name is derived from the path and
	 * file name only, avoiding any parsing of the source file itself.  Any leading path segments matching a source
	 * path are excluded.
	 * 
	 * @param sourceFile the source file
	 * @return the fully-qualified class name
	 */
	public String getFullyQualifiedClassName(File sourceFile) {
		Path path = Path.of(FilenameUtils.removeExtension(sourceFile.getPath()));
		Iterable<? extends Path> sourcePaths = fileManager.getLocationAsPaths(StandardLocation.SOURCE_PATH);
		
		if (sourcePaths != null) {
			for (Path sourcePath : sourcePaths) {
				if (Files.isDirectory(sourcePath) && path.startsWith(sourcePath)) {
					path = path.subpath(sourcePath.getNameCount(), path.getNameCount());
					break;
				}
			}
		}
		
		return path.toString().replaceAll("[\\\\/]", ".");
	}

}
