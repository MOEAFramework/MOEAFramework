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
package org.moeaframework.core;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringTokenizer;
import org.moeaframework.analysis.sensitivity.OutputWriter;
import org.moeaframework.core.NondominatedPopulation.DuplicateMode;
import org.moeaframework.core.indicator.Hypervolume;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.util.PropertyScope;
import org.moeaframework.util.TypedProperties;

/**
 * Global settings used by this framework.
 * <p>
 * The settings are loaded from the system properties, set when starting Java using
 * {@code java -Dorg.moeaframework.core.foo=bar ...}, or the properties file.
 * <p>
 * The default properties file is {@value DEFAULT_CONFIGURATION_FILE} and should be located in the working directory
 * where Java is started.  This can be overridden by setting {@code org.moeaframework.configuration=<file>}.
 */
public class Settings {

	/**
	 * Level of significance or machine precision.
	 */
	public static final double EPS = 1e-10;
	
	/**
	 * The default buffer size.  Currently set to 4096 bytes.
	 */
	public static final int BUFFER_SIZE = 0x1000;
	
	/**
	 * The default population size.
	 */
	public static final int DEFAULT_POPULATION_SIZE = 100;
	
	/**
	 * Store the new line character to prevent repetitive calls to {@code System.getProperty("line.separator")}.
	 * 
	 * @deprecated Use {@code System.lineSeparator()} instead
	 */
	@Deprecated
	public static final String NEW_LINE = System.lineSeparator();
	
	/**
	 * The default configuration file.
	 */
	public static final String DEFAULT_CONFIGURATION_FILE = "moeaframework.properties";

	/**
	 * The global properties object.
	 */
	public static final TypedProperties PROPERTIES = new TypedProperties();
	
	/**
	 * The prefix for all property keys.
	 */
	static final String KEY_PREFIX = createKey("org", "moeaframework");
	
	/**
	 * The property key for setting the configuration file.  This defaults to
	 * {@value DEFAULT_CONFIGURATION_FILE} if unset.
	 */
	public static final String KEY_CONFIGURATION_FILE = createKey(KEY_PREFIX, "configuration");
	
	/**
	 * The property key for setting a global PRNG seed, which can be used to make results
	 * reproducible.  Note, however, that the seed is set once during initialization.
	 */
	public static final String KEY_PRNG_SEED = createKey(KEY_PREFIX, "core", "prng", "seed");
	
	/**
	 * The property key for how to handle duplicate solutions in a nondominated population.
	 */
	public static final String KEY_DUPLICATE_MODE = createKey(KEY_PREFIX, "core", "duplicate_mode");
	
	/**
	 * The property key for the power used in the generational distance calculation.
	 */
	public static final String KEY_GD_POWER = createKey(KEY_PREFIX, "core", "indicator", "gd_power");
	
	/**
	 * The property key for the power used in the inverted generational distance calculation.
	 */
	public static final String KEY_IGD_POWER = createKey(KEY_PREFIX, "core", "indicator", "igd_power");
	
	/**
	 * The property key to indicate that fast non-dominated sorting should be used.
	 */
	public static final String KEY_FAST_NONDOMINATED_SORTING = createKey(KEY_PREFIX, "core", "fast_nondominated_sorting");
	
	/**
	 * The property key to indicate that truncation warnings should be suppressed.
	 */
	public static final String KEY_SUPPRESS_TRUNCATION_WARNING = createKey(KEY_PREFIX, "core", "suppress_truncation_warning");
	
	/**
	 * The property key for the continuity correction flag.
	 */
	public static final String KEY_CONTINUITY_CORRECTION = createKey(KEY_PREFIX, "util", "statistics", "continuity_correction");
	
	/**
	 * The property key for the hypervolume delta when determining the reference point.
	 */
	public static final String KEY_HYPERVOLUME_DELTA = createKey(KEY_PREFIX, "core", "indicator", "hypervolume_delta");
	
	/**
	 * The prefix for specifying custom ideal points for different problems.
	 */
	public static final String KEY_IDEALPT_PREFIX = createKey(KEY_PREFIX, "core", "indicator", "hypervolume_idealpt");
	
	/**
	 * The prefix for specifying custom reference points for different problems.
	 */
	public static final String KEY_REFPT_PREFIX = createKey(KEY_PREFIX, "core", "indicator", "hypervolume_refpt");
	
	/**
	 * The property key for the hypervolume command.
	 */
	public static final String KEY_HYPERVOLUME = createKey(KEY_PREFIX, "core", "indicator", "hypervolume");
	
	/**
	 * The property key for the hypervolume inversion flag.
	 */
	public static final String KEY_HYPERVOLUME_INVERTED = createKey(KEY_PREFIX, "core", "indicator", "hypervolume_inverted");
	
	/**
	 * The property key for the hypervolume flag.
	 */
	public static final String KEY_HYPERVOLUME_ENABLED = createKey(KEY_PREFIX, "core", "indicator", "hypervolume_enabled");
	
	/**
	 * The prefix for all problem property keys.
	 */
	public static final String KEY_PROBLEM_PREFIX = createKey(KEY_PREFIX, "problem");
	
	/**
	 * The property key for the list of available problems.
	 */
	public static final String KEY_PROBLEM_LIST = createKey(KEY_PROBLEM_PREFIX, "problems");
	
	/**
	 * The property key for the algorithms available in the diagnostic tool.
	 */
	public static final String KEY_DIAGNOSTIC_TOOL_ALGORITHMS = createKey(KEY_PREFIX, "analysis", "diagnostics", "algorithms");
	
	/**
	 * The property key for the problems available in the diagnostic tool.
	 */
	public static final String KEY_DIAGNOSTIC_TOOL_PROBLEMS = createKey(KEY_PREFIX, "analysis", "diagnostics", "problems");
	
	/**
	 * The property key for the genetic programming protected functions flag.
	 */
	public static final String KEY_GP_PROTECTED_FUNCTIONS = createKey(KEY_PREFIX, "util", "tree", "protected_functions");
	
	/**
	 * The property key for the cleanup strategy when restarting from previous runs.
	 */
	public static final String KEY_CLEANUP_STRATEGY = createKey(KEY_PREFIX, "analysis", "sensitivity", "cleanup");
	
	/**
	 * The property key for enabling debugging info when running external problems.
	 */
	public static final String KEY_EXTERNAL_PROBLEM_DEBUGGING = createKey(KEY_PREFIX, "problem", "external_problem_debugging");
	
	static {
		reload();
	}
	
	/**
	 * Clears any existing settings and reloads the properties.
	 */
	public static void reload() {
		PROPERTIES.clear();
		
		//system properties
		try {
			Properties systemProperties = System.getProperties();
			
			for (String key : systemProperties.stringPropertyNames()) {
				if (StringUtils.startsWithIgnoreCase(key, Settings.KEY_PREFIX)) {
					PROPERTIES.setString(key, systemProperties.getProperty(key));
				}
			}
		} catch (SecurityException e) {
			System.err.println("Unable to read system properties: " + e);
		}
		
		//properties file
		try {
			String resource = PROPERTIES.getString(KEY_CONFIGURATION_FILE, DEFAULT_CONFIGURATION_FILE);			
			File file = new File(resource);
			
			if (file.exists()) {
				try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
					PROPERTIES.load(reader);
				}
			} else {
				try (InputStream stream = Settings.class.getResourceAsStream("/" + resource)) {
					if (stream != null) {
						try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
							PROPERTIES.load(reader);
						}
					}
				}
			}
		} catch (IOException e) {
			throw new FrameworkException(e);
		}
	}
	
	/**
	 * Private constructor to prevent instantiation.
	 */
	private Settings() {
		super();
	}
	
	/**
	 * Returns {@code true} if continuity correction is enabled; {@code false}
	 * otherwise.  Rank-based statistical inference methods, such as the 
	 * Mann-Whitney U test and the Wilcoxon Signed-Ranks test, approximate the 
	 * test's discrete distribution with a continuous distribution for 
	 * computing the p-value. It has been recommended but not often employed in
	 * practice to apply a continuity correction.
	 * 
	 * @return {@code true} if continuity correction is enabled; {@code false}
	 *         otherwise
	 */
	public static boolean isContinuityCorrection() {
		return PROPERTIES.getBoolean(KEY_CONTINUITY_CORRECTION, false);
	}
	
	/**
	 * Returns the strategy used for handling duplicate solutions in a
	 * nondominated population.
	 * 
	 * @return the strategy for handling duplicate solutions
	 */
	public static DuplicateMode getDuplicateMode() {
		return PROPERTIES.getEnum(KEY_DUPLICATE_MODE, DuplicateMode.class,
				DuplicateMode.NO_DUPLICATE_OBJECTIVES);
	}
	
	/**
	 * Returns the power used in the generational distance calculation.
	 * The default value is 2.0.
	 * 
	 * @return the power used in the generational distance calculation
	 */
	public static double getGDPower() {
		return PROPERTIES.getDouble(KEY_GD_POWER, 2.0);
	}
	
	/**
	 * Returns the power used in the inverted generational distance calculation.
	 * The default value is 1.0.
	 * 
	 * @return the power used in the inverted generational distance calculation
	 */
	public static double getIGDPower() {
		return PROPERTIES.getDouble(KEY_IGD_POWER, 1.0);
	}
	
	/**
	 * Returns the ideal point for the given problem, or {@code null} if
	 * one is not specified.
	 * 
	 * @param problem the problem name
	 * @return the ideal point
	 */
	public static double[] getIdealPoint(String problem) {
		return PROPERTIES.getDoubleArray(createKey(KEY_IDEALPT_PREFIX, problem), null);
	}
	
	/**
	 * Returns the reference point for the given problem, or {@code null} if
	 * one is not specified.
	 * 
	 * @param problem the problem name
	 * @return the reference point
	 */
	public static double[] getReferencePoint(String problem) {
		return PROPERTIES.getDoubleArray(createKey(KEY_REFPT_PREFIX, problem), null);
	}
	
	/**
	 * Returns {@code true} if fast non-dominated sorting should be used;
	 * or {@code false} if the naive non-dominated sorting implementation is
	 * preferred.  The default is {@code false} since while the fast version
	 * has better worst-case time complexity, the naive version tends to run
	 * faster except for a small number of edge cases.
	 * 
	 * @return {@code true} if fast non-dominated sorting should be used;
	 *         or {@code false} if the naive non-dominated sorting
	 *         implementation is preferred
	 */
	public static boolean useFastNondominatedSorting() {
		return PROPERTIES.getBoolean(KEY_FAST_NONDOMINATED_SORTING, false);
	}
	
	/**
	 * Returns {@code true} if truncation warnings, when implicitly converting a
	 * real-valued property to an integer and truncating the decimal value, should be
	 * suppressed.
	 * 
	 * @return {@code true} if truncation warnings are suppressed; {@code false} otherwise
	 */
	public static boolean isSuppressTruncationWarning() {
		return PROPERTIES.getBoolean(KEY_SUPPRESS_TRUNCATION_WARNING, false);
	}
	
	/**
	 * Returns the delta applied to the nadir point of the reference set when 
	 * calculating the hypervolume.  Having a non-zero delta is necessary to 
	 * ensure extremal solutions contribute to the hypervolume.
	 * 
	 * @return the delta applied to the nadir point of the reference set when 
	 *         calculating the hypervolume
	 */
	public static double getHypervolumeDelta() {
		return PROPERTIES.getDouble(KEY_HYPERVOLUME_DELTA, 0.0);
	}
	
	/**
	 * Returns the native hypervolume command; or {@code null} if the default
	 * hypervolume implementation is used.  The default hypervolume 
	 * implementation may become computationally prohibitive on large 
	 * approximation sets or at high dimensions.  The following variable 
	 * substitutions are provided:
	 * <ul>
	 *   <li>{0} number of objectives
	 *   <li>{1} approximation set size
	 *   <li>{2} file containing the approximation set
	 *   <li>{3} file containing the reference point
	 * </ul>
	 *   
	 * @return the native hypervolume command; or {@code null} if the default
	 *         hypervolume implementation is used
	 */
	public static String getHypervolume() {
		return PROPERTIES.getString(KEY_HYPERVOLUME, null);
	}
	
	/**
	 * Returns {@code true} if the approximation set is inverted prior to being
	 * passed to the custom hypervolume implementation; otherwise {@code false}.
	 * 
	 * @return {@code true} if the approximation set is inverted prior to being
	 *         passed to the custom hypervolume implementation; otherwise
	 *         {@code false}
	 */
	public static boolean isHypervolumeInverted() {
		return PROPERTIES.getBoolean(KEY_HYPERVOLUME_INVERTED, false);
	}
	
	/**
	 * Returns {@code true} if hypervolume calculation is enabled; {@code false}
	 * otherwise.  When disabled, the hypervolume should be reported as
	 * {@code Double.NaN}.  Direct use of the {@link Hypervolume} class remains
	 * unaffected by this option.
	 * 
	 * @return {@code true} if hypervolume calculation is enabled; {@code false}
	 *         otherwise
	 */
	public static boolean isHypervolumeEnabled() {
		return PROPERTIES.getBoolean(KEY_HYPERVOLUME_ENABLED, true);
	}
	
	/**
	 * Returns the list of available problems.  This allows enumerating
	 * additional problems without the need for defining and registering a 
	 * service provider on the classpath.
	 * 
	 * @return the list of available problems
	 */
	public static String[] getProblems() {
		return PROPERTIES.getStringArray(KEY_PROBLEM_LIST, new String[0]);
	}
	
	/**
	 * Returns the class for the specified problem.
	 * 
	 * @param name the problem name
	 * @return the class for the specified problem
	 */
	public static String getProblemClass(String name) {
		return PROPERTIES.getString(createKey(KEY_PROBLEM_PREFIX, name, "class"), null);
	}
	
	/**
	 * Returns the reference set filename for the specified problem.
	 * 
	 * @param name the problem name
	 * @return the reference set filename for the specified problem
	 */
	public static String getProblemReferenceSet(String name) {
		return PROPERTIES.getString(createKey(KEY_PROBLEM_PREFIX, name, "referenceSet"), null);
	}
	
	/**
	 * Returns the list of algorithms displayed in the diagnostic tool GUI.
	 * 
	 * @return the list of algorithms displayed in the diagnostic tool GUI
	 */
	public static String[] getDiagnosticToolAlgorithms() {
		String[] result = PROPERTIES.getStringArray(KEY_DIAGNOSTIC_TOOL_ALGORITHMS, null);
		
		if (result == null) {
			result = AlgorithmFactory.getInstance().getAllDiagnosticToolAlgorithms().toArray(new String[0]);
		}
		
		return result;
	}
	
	/**
	 * Returns the list of problems displayed in the diagnostic tool GUI.
	 * 
	 * @return the list of problems displayed in the diagnostic tool GUI
	 */
	public static String[] getDiagnosticToolProblems() {
		String[] result = PROPERTIES.getStringArray(KEY_DIAGNOSTIC_TOOL_PROBLEMS, null);
		
		if (result == null) {
			result = ProblemFactory.getInstance().getAllDiagnosticToolProblems().toArray(new String[0]);
		}
		
		return result;
	}
	
	/**
	 * Splits an executable command into its individual arguments.  This method
	 * allows quoted text ({@code "..."}) in properties to be treated as an
	 * individual argument as required by {@link ProcessBuilder}.
	 *  
	 * @param command the command represented in a single string
	 * @return the individual arguments comprising the command
	 * @deprecated method being moved out of Settings class
	 */
	@Deprecated
	public static String[] parseCommand(String command) {
		return new StringTokenizer(command).setQuoteChar('\"').getTokenArray();
	}
	
	/**
	 * Returns {@code true} if genetic programming functions should use
	 * protection against invalid arguments that would otherwise result in
	 * {@code NaN} or other invalid values; {@code false} otherwise.
	 * 
	 * @return {@code true} if genetic programming functions should use
	 *         protection against invalid arguments that would otherwise result
	 *         in {@code NaN} or other invalid values; {@code false} otherwise
	 */
	public static boolean isProtectedFunctions() {
		return PROPERTIES.getBoolean(KEY_GP_PROTECTED_FUNCTIONS, true);
	}
	
	/**
	 * Returns the cleanup strategy when restarting from a previous run.
	 * 
	 * @return the cleanup strategy when restarting from a previous run
	 */
	public static OutputWriter.CleanupStrategy getCleanupStrategy() {
		return PROPERTIES.getEnum(KEY_CLEANUP_STRATEGY, OutputWriter.CleanupStrategy.class,
				OutputWriter.CleanupStrategy.ERROR);
	}
	
	/**
	 * Returns {@code true} if debugging is enabled when running external
	 * problems.
	 * 
	 * @return {@code true} if debugging for external problems is enabled;
	 *         {@code false} otherwise
	 */
	public static boolean getExternalProblemDebuggingEnabled() {
		return PROPERTIES.getBoolean(KEY_EXTERNAL_PROBLEM_DEBUGGING, false);
	}
	
	/**
	 * Returns the MOEA Framework icons of various sizes.
	 * 
	 * @return the MOEA Framework icons
	 */
	public static List<Image> getIconImages() {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		List<Image> icons = new ArrayList<Image>();
		
		icons.add(toolkit.getImage(Settings.class.getResource("logo16.png")));
		icons.add(toolkit.getImage(Settings.class.getResource("logo24.png")));
		icons.add(toolkit.getImage(Settings.class.getResource("logo32.png")));
		icons.add(toolkit.getImage(Settings.class.getResource("logo48.png")));
		icons.add(toolkit.getImage(Settings.class.getResource("logo64.png")));
		icons.add(toolkit.getImage(Settings.class.getResource("logo128.png")));
		icons.add(toolkit.getImage(Settings.class.getResource("logo256.png")));

		return icons;
	}
	
	/**
	 * Creates the key for a property by concatenating an optional prefix with one or more parts.
	 * 
	 * @param prefix the prefix or first part of the key
	 * @param parts remaining parts of the key
	 * @return the full key
	 */
	public static String createKey(String prefix, String... parts) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(prefix, 0, prefix.endsWith(".") ? prefix.length()-1 : prefix.length());
		
		for (String part : parts) {
			sb.append(".");
			sb.append(part);
		}
		
		return sb.toString();
	}
	
	/**
	 * Creates a new scope wherein settings can be temporarily modified.  Upon closing the scope, the original
	 * settings are restored.
	 * 
	 * @return the scope
	 */
	public static PropertyScope createScope() {
		return new PropertyScope(PROPERTIES);
	}
	
}
