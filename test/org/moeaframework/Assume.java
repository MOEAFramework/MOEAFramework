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
package org.moeaframework;

import java.io.File;

import org.apache.commons.lang3.SystemUtils;
import org.moeaframework.core.Settings;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.util.io.RedirectStream;

public class Assume extends org.junit.Assume {
	
	@SuppressWarnings("deprecation")
	private Assume() {
		super();
	}
	
	public static void skip(String message) {
		assumeTrue(message, false);
	}
	
	public static <T> T assumeInstanceOf(Class<T> cls, Object object) {
		assumeTrue("Expected object of type " + cls.getName() + " but found " + object.getClass().getName(),
				cls.isInstance(object));
		
		return cls.cast(object);
	}
	
	public static void assumeFile(File file) {
		assumeTrue(file + " does not exist", file.exists());
	}
	
	public static void assumePOSIX() {
		assumeTrue("System is not POSIX-compliant",
				SystemUtils.IS_OS_UNIX || SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_MAC);
	}
	
	public static void assumeCommand(String... args) {
		try {
			ProcessBuilder processBuilder = new ProcessBuilder(args);
			RedirectStream.pipe(processBuilder, System.out);
		} catch (Exception e) {
			assumeNoException(args[0] + " is not available", e);
		}
	}
	
	public static void assumeMake() {
		assumeTrue("Make is not available", Make.isMakeAvailable());
	}
	
	public static void assumeFortran() {
		assumeCommand("gfortran", "--version");
	}
	
	public static void assumePython() {
		assumeCommand(Settings.getPythonCommand(), "--version");
	}
	
	public static void assumePythonModule(String module) {
		try {
			ProcessBuilder processBuilder = new ProcessBuilder(Settings.getPythonCommand(), "-c", "import " + module);
			RedirectStream.pipe(processBuilder, System.out);
		} catch (Exception e) {
			assumeNoException(module + " is not installed", e);
		}
	}
	
	public static void assumeMatlab() {
		assumeCommand("matlab", "-batch", "version");
	}
	
	public static void assumeGitHubActions() {
		assumeTrue("Must run on GitHub Actions", TestEnvironment.isGitHubActions());
	}
	
	public static void assumeHasDisplay() {
		assumeTrue("No display available or running headless", !TestEnvironment.isHeadless());
	}
	
	public static void assumeJMetalPlugin() {
		Assume.assumeTrue("JMetal-Plugin not available on classpath",
				AlgorithmFactory.getInstance().hasProvider("org.moeaframework.algorithm.jmetal.JMetalAlgorithms"));
	}

}
