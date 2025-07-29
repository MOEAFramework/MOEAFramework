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

import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.moeaframework.core.Settings;
import org.moeaframework.util.io.Resources;
import org.moeaframework.util.io.Resources.ResourceOption;

/**
 * Configuration for the testing environment.
 */
public class TestEnvironment {

	private TestEnvironment() {
		super();
	}
	
	/**
	 * The default number of retries for {@link Retryable}.
	 */
	public static final int RETRIES = 5;

	/**
	 * The number of samples or trials to use in testing.
	 */
	public static final int SAMPLES = 10000;

	/**
	 * The floating-point threshold for low precision / low fidelity comparisons.
	 */
	public static final double LOW_PRECISION = 0.05;
	
	/**
	 * The floating-point threshold for high precision / high fidelity comparisons.
	 */
	public static final double HIGH_PRECISION = Settings.EPS;
	
	/**
	 * Locates the resource while handling different file structures and, if necessary, extracting the resource to a
	 * file on disk.
	 * 
	 * @param resource the resource name
	 * @return the resource file
	 * @throws IOException if an I/O error occurred or the resource was not found
	 */
	public static File getResourceAsFile(String resource) throws IOException {
		Path path = Path.of(resource);
		
		if (Files.exists(path)) {
			return path.toFile();
		}
		
		if (path.startsWith("src")) {
			path = Path.of("src/main/resources", resource);
			
			if (Files.exists(path)) {
				return path.toFile();
			}
		}
		
		if (path.startsWith("test")) {
			path = Path.of("src/test/resources", resource);
			
			if (Files.exists(path)) {
				return path.toFile();
			}
		}
		
		return Resources.asFile(TestEnvironment.class, resource, ResourceOption.REQUIRED, ResourceOption.ABSOLUTE,
					ResourceOption.TEMPORARY);
	}
	
	public static boolean getProperty(String propertyName, boolean defaultValue) {
		String rawValue = System.getProperty(propertyName);
		
		if (rawValue == null) {
			rawValue = System.getenv(propertyName);
		}
		
		if (rawValue == null) {
			return defaultValue;
		}
		
		return Boolean.parseBoolean(rawValue);
	}
	
	public static boolean isCI() {
		return getProperty("CI", false);
	}
	
	public static boolean isGitHubActions() {
		return getProperty("GITHUB_ACTIONS", false);
	}
	
	public static boolean isHeadless() {
		return GraphicsEnvironment.isHeadless() ||
				GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices().length == 0;
	}
	
	public static void setHeadless(boolean value) {
		System.setProperty("java.awt.headless", Boolean.toString(value));
	}
	
	public static void setVerbose(boolean value) {
		Settings.PROPERTIES.setBoolean(Settings.KEY_VERBOSE, value);
	}

}
