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
package org.moeaframework.core;

import java.awt.Toolkit;
import java.awt.image.BaseMultiResolutionImage;
import java.awt.image.MultiResolutionImage;
import java.io.IOException;
import java.io.Reader;
import java.time.Duration;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.moeaframework.core.indicator.Hypervolume;
import org.moeaframework.core.population.NondominatedPopulation.DuplicateMode;
import org.moeaframework.util.cli.OptionStyle;
import org.moeaframework.util.io.Resources;
import org.moeaframework.util.io.Resources.ResourceOption;

/**
 * Global settings used by this framework.
 * <p>
 * The settings are loaded from the system properties, set when starting Java using
 * {@code java -Dorg.moeaframework.core.foo=bar ...} or the properties file.
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
	 * The default number of function evaluations, typically used when not given explicitly.
	 */
	public static final int DEFAULT_MAX_FUNCTION_EVALUATIONS = 25000;
	
	/**
	 * The default number of iterations, typically used when not given explicitly.
	 */
	public static final int DEFAULT_MAX_ITERATIONS = DEFAULT_MAX_FUNCTION_EVALUATIONS / DEFAULT_POPULATION_SIZE;

	/**
	 * The default configuration file.
	 */
	public static final String DEFAULT_CONFIGURATION_FILE = "moeaframework.properties";

	/**
	 * The global properties object.
	 */
	public static final TypedProperties PROPERTIES = TypedProperties.newThreadSafeInstance();
	
	/**
	 * The prefix for all property keys.
	 */
	static final String KEY_PREFIX = createKey("org", "moeaframework");
	
	/**
	 * The property key for setting the configuration file.  This defaults to {@value DEFAULT_CONFIGURATION_FILE}
	 * if unset.
	 */
	public static final String KEY_CONFIGURATION_FILE = createKey(KEY_PREFIX, "configuration");
	
	/**
	 * The property key storing the version.
	 */
	public static final String KEY_VERSION = createKey(KEY_PREFIX, "version");
	
	/**
	 * The property key storing the major version.
	 */
	public static final String KEY_MAJOR_VERSION = createKey(KEY_PREFIX, "major_version");
	
	/**
	 * The property key for enabling verbose logging.
	 */
	public static final String KEY_VERBOSE = createKey(KEY_PREFIX, "core", "verbose");
	
	/**
	 * The property key for specifying the Python interpreter command.
	 */
	public static final String KEY_PYTHON_INTERPRETER = createKey(KEY_PREFIX, "tools", "python");
	
	/**
	 * The property key for setting a global PRNG seed, which can be used to make results reproducible.  Note,
	 * however, that the seed is set once during initialization.
	 */
	public static final String KEY_PRNG_SEED = createKey(KEY_PREFIX, "core", "prng", "seed");
	
	/**
	 * The property key for setting the display width of help messages from command line tools.
	 */
	public static final String KEY_HELP_WIDTH = createKey(KEY_PREFIX, "core", "help", "width");
	
	/**
	 * The property key for how to handle duplicate solutions in a nondominated population.
	 */
	public static final String KEY_DUPLICATE_MODE = createKey(KEY_PREFIX, "core", "duplicate_mode");
	
	/**
	 * The property key for the power used in the generational distance calculation.
	 */
	public static final String KEY_GD_POWER = createKey(KEY_PREFIX, "core", "indicator", "gd", "power");
	
	/**
	 * The property key for the power used in the inverted generational distance calculation.
	 */
	public static final String KEY_IGD_POWER = createKey(KEY_PREFIX, "core", "indicator", "igd", "power");
	
	/**
	 * The property key to indicate that fast non-dominated sorting should be used.
	 */
	public static final String KEY_FAST_NONDOMINATED_SORTING = createKey(KEY_PREFIX, "core", "fast_nondominated_sorting");
	
	/**
	 * The property key storing the command or executable used to start Java.
	 */
	public static final String KEY_CLI_EXECUTABALE = createKey(KEY_PREFIX, "util", "cli", "executable");
	
	/**
	 * The property key storing the option formatting style.
	 */
	public static final String KEY_CLI_OPTION_STYLE = createKey(KEY_PREFIX, "util", "cli", "style");
	
	/**
	 * The property key for the continuity correction flag.
	 */
	public static final String KEY_CONTINUITY_CORRECTION = createKey(KEY_PREFIX, "util", "statistics", "continuity_correction");
	
	/**
	 * The property key for the hypervolume delta when determining the reference point.
	 */
	public static final String KEY_HYPERVOLUME_DELTA = createKey(KEY_PREFIX, "core", "indicator", "hypervolume", "delta");

	/**
	 * The property key for the hypervolume command.
	 */
	public static final String KEY_HYPERVOLUME = createKey(KEY_PREFIX, "core", "indicator", "hypervolume");
	
	/**
	 * The property key for the hypervolume inversion flag.
	 */
	public static final String KEY_HYPERVOLUME_INVERTED = createKey(KEY_PREFIX, "core", "indicator", "hypervolume", "inverted");
	
	/**
	 * The property key for the hypervolume flag.
	 */
	public static final String KEY_HYPERVOLUME_ENABLED = createKey(KEY_PREFIX, "core", "indicator", "hypervolume", "enabled");
	
	/**
	 * The prefix for all problem property keys.
	 */
	public static final String KEY_PROBLEM_PREFIX = createKey(KEY_PREFIX, "problem");
	
	/**
	 * The property key for enabling consistency checks in the CMA-ES algorithm.
	 */
	public static final String KEY_CMAES_CHECK_CONSISTENCY = createKey(KEY_PREFIX, "algorithm", "cmaes", "check_consistency");
	
	/**
	 * The property key for the genetic programming protected functions flag.
	 */
	public static final String KEY_GP_PROTECTED_FUNCTIONS = createKey(KEY_PREFIX, "util", "tree", "protected_functions");
	
	/**
	 * The property key for enabling debugging info when running external problems.
	 */
	public static final String KEY_EXTERNAL_PROBLEM_DEBUGGING = createKey(KEY_PROBLEM_PREFIX, "external", "enable_debugging");
	
	/**
	 * The property key for configuring the number of retry attempts when running external problems.
	 */
	public static final String KEY_EXTERNAL_RETRY_ATTEMPTS = createKey(KEY_PROBLEM_PREFIX, "external", "retry_attempts");
	
	/**
	 * The property key for configuring the retry delay when running external problems.
	 */
	public static final String KEY_EXTERNAL_RETRY_DELAY = createKey(KEY_PROBLEM_PREFIX, "external", "retry_delay");
	
	/**
	 * The property key for configuring the shutdown timeout when running external problems.
	 */
	public static final String KEY_EXTERNAL_SHUTDOWN_TIMEOUT = createKey(KEY_PROBLEM_PREFIX, "external", "shutdown_timeout");
	
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
			System.err.println("WARNING: Unable to read system properties: " + e);
		}
		
		//properties file
		String propertiesResource = PROPERTIES.getString(KEY_CONFIGURATION_FILE, DEFAULT_CONFIGURATION_FILE);
		
		try {
			try (Reader reader = Resources.asReader(Settings.class, propertiesResource, ResourceOption.FILE,
					ResourceOption.ABSOLUTE)) {
				if (reader != null) {
					PROPERTIES.load(reader);
				}
			}
		} catch (IOException e) {
			System.err.println("WARNING: Unable to read properties file '" + propertiesResource + "': " + e);
		}
		
		// inject build properties
		try {
			String version = TypedProperties.loadBuildProperties().getString("version");
			
			if (version == null) {
				throw new InvalidPropertyException("version",
						"Not found, please ensure META-INF/ is included in the build path");
			}
			
			PROPERTIES.setString(KEY_VERSION, version);
			
			Pattern pattern = Pattern.compile("([0-9]+)\\.([0-9]+)(-SNAPSHOT)?");
			Matcher matcher = pattern.matcher(version);
			
			if (!matcher.matches()) {
				throw new InvalidPropertyException("version", "Must be in 'major.minor' format");
			}
			
			PROPERTIES.setString(KEY_MAJOR_VERSION, matcher.group(1));
		} catch (IOException e) {
			System.err.println("WARNING: Unable to build properties: " + e);
		}
	}
	
	/**
	 * Private constructor to prevent instantiation.
	 */
	private Settings() {
		super();
	}
	
	/**
	 * Returns {@code true} if verbose logging is enabled; {@code false} otherwise.
	 * 
	 * @return {@code true} if verbose logging is enabled; {@code false} otherwise
	 */
	public static boolean isVerbose() {
		return PROPERTIES.getBoolean(KEY_VERBOSE, false);
	}
	
	/**
	 * Returns {@code true} if debugging mode is enabled.  This is primarily intended for internal use as a way to
	 * enable debug tracing on CI.
	 * 
	 * @return {@code true} if debugging mode is enabled; {@code false} otherwise
	 */
	public static boolean isDebug() {
		return System.getenv("RUNNER_DEBUG") != null || System.getenv("MOEA_DEBUG") != null;
	}
	
	/**
	 * Returns the current version.
	 * 
	 * @return the version
	 */
	public static String getVersion() {
		return PROPERTIES.getString(KEY_VERSION);
	}
	
	/**
	 * Returns the major version number.
	 * 
	 * @return the major version
	 */
	public static int getMajorVersion() {
		return PROPERTIES.getInt(KEY_MAJOR_VERSION);
	}
	
	/**
	 * Returns the configured Python interpreter.  This must either be found on the system path or an absolute
	 * path to the executable.
	 * 
	 * @return the python command
	 */
	public static String getPythonCommand() {
		return PROPERTIES.getString(KEY_PYTHON_INTERPRETER, "python3");
	}
	
	/**
	 * Returns the command or executable used to start Java.  This is primarily used when generating help messages to
	 * show a usage string matching the original command.
	 * 
	 * @return the command or executable used to start Java
	 */
	public static String getCLIExecutable() {
		return PROPERTIES.getString(KEY_CLI_EXECUTABALE, "java -classpath \"lib/*\"");
	}
	
	/**
	 * Returns the formatting style for CLI options.
	 * 
	 * @return the formatting style for CLI options
	 */
	public static OptionStyle getCLIOptionStyle() {
		return PROPERTIES.getEnum(KEY_CLI_OPTION_STYLE, OptionStyle.class, OptionStyle.NONE);
	}
	
	/**
	 * Returns {@code true} if continuity correction is enabled; {@code false} otherwise.  Rank-based statistical
	 * inference methods, such as the Mann-Whitney U test and the Wilcoxon Signed-Ranks test, approximate the
	 * test's discrete distribution with a continuous distribution for computing the p-value.  It has been recommended
	 * but not often employed in practice to apply a continuity correction.
	 * 
	 * @return {@code true} if continuity correction is enabled; {@code false} otherwise
	 */
	public static boolean isContinuityCorrection() {
		return PROPERTIES.getBoolean(KEY_CONTINUITY_CORRECTION, false);
	}
	
	/**
	 * Returns the strategy used for handling duplicate solutions in a nondominated population.
	 * 
	 * @return the strategy for handling duplicate solutions
	 */
	public static DuplicateMode getDuplicateMode() {
		return PROPERTIES.getEnum(KEY_DUPLICATE_MODE, DuplicateMode.class, DuplicateMode.NO_DUPLICATE_OBJECTIVES);
	}
	
	/**
	 * Returns the power used in the generational distance calculation.  The default value is 2.0.
	 * 
	 * @return the power used in the generational distance calculation
	 */
	public static double getGDPower() {
		return PROPERTIES.getDouble(KEY_GD_POWER, 2.0);
	}
	
	/**
	 * Returns the power used in the inverted generational distance calculation.  The default value is 1.0.
	 * 
	 * @return the power used in the inverted generational distance calculation
	 */
	public static double getIGDPower() {
		return PROPERTIES.getDouble(KEY_IGD_POWER, 1.0);
	}

	/**
	 * Returns {@code true} if fast non-dominated sorting should be used; or {@code false} if the naive non-dominated
	 * sorting implementation is preferred.  The default is {@code false} since while the fast version has better
	 * worst-case time complexity, the naive version tends to run faster except for a small number of edge cases.
	 * 
	 * @return {@code true} if fast non-dominated sorting should be used; or {@code false} if the naive non-dominated
	 *         sorting implementation is preferred
	 */
	public static boolean useFastNondominatedSorting() {
		return PROPERTIES.getBoolean(KEY_FAST_NONDOMINATED_SORTING, false);
	}
	
	/**
	 * Returns the delta applied to the nadir point of the reference set when calculating the hypervolume.  Having a
	 * non-zero delta is necessary to  ensure extremal solutions contribute to the hypervolume.
	 * 
	 * @return the delta applied to the nadir point of the reference set when calculating the hypervolume
	 */
	public static double getHypervolumeDelta() {
		return PROPERTIES.getDouble(KEY_HYPERVOLUME_DELTA, 0.0);
	}
	
	/**
	 * Returns the native hypervolume command; or {@code null} if the default hypervolume implementation is used.
	 * The default hypervolume implementation may become computationally prohibitive on large approximation sets or
	 * at high dimensions.  See {@link org.moeaframework.core.indicator.NativeHypervolume} for more details on
	 * formatting the command.
	 * 
	 * @return the native hypervolume command; or {@code null} if the default hypervolume implementation is used
	 */
	public static String getHypervolume() {
		return PROPERTIES.getString(KEY_HYPERVOLUME, null);
	}
	
	/**
	 * Returns {@code true} if the approximation set is inverted prior to being passed to the custom hypervolume
	 * implementation; otherwise {@code false}.
	 * 
	 * @return {@code true} if the approximation set is inverted prior to being passed to the custom hypervolume
	 *         implementation; otherwise {@code false}
	 */
	public static boolean isHypervolumeInverted() {
		return PROPERTIES.getBoolean(KEY_HYPERVOLUME_INVERTED, false);
	}
	
	/**
	 * Returns {@code true} if hypervolume calculation is enabled; {@code false} otherwise.  When disabled, the
	 * hypervolume should be reported as {@code Double.NaN}.  Direct use of the {@link Hypervolume} class remains
	 * unaffected by this option.
	 * 
	 * @return {@code true} if hypervolume calculation is enabled; {@code false} otherwise
	 */
	public static boolean isHypervolumeEnabled() {
		return PROPERTIES.getBoolean(KEY_HYPERVOLUME_ENABLED, true);
	}
	
	/**
	 * Returns the configured minimum bounds (ideal point) used for normalization, or {@code null} if not configured.
	 * 
	 * @param problem the problem name
	 * @return the minimum bounds or {@code null}
	 */
	public static double[] getProblemSpecificMinimumBounds(String problem) {
		double[] result = PROPERTIES.getDoubleArray(
				createKey(Settings.KEY_PROBLEM_PREFIX, problem, "normalization", "minimum"), null);
		
		if (result == null) {
			result = PROPERTIES.getDoubleArray(
					createKey(KEY_PREFIX, "core", "indicator", "hypervolume", "idealpt", problem), null);
		}
		
		return result;
	}
	
	/**
	 * Returns the configured maximum bounds (reference point) used for normalization, or {@code null} if not
	 * configured.
	 * 
	 * @param problem the problem name
	 * @return the maximum bounds or {@code null}
	 */
	public static double[] getProblemSpecificMaximumBounds(String problem) {
		double[] result = PROPERTIES.getDoubleArray(
				createKey(Settings.KEY_PROBLEM_PREFIX, problem, "normalization", "maximum"), null);
		
		if (result == null) {
			result = PROPERTIES.getDoubleArray(
					createKey(KEY_PREFIX, "core", "indicator", "hypervolume", "refpt", problem), null);
		}
		
		return result;
	}
	
	/**
	 * Returns a problem-specific delta used for hypervolume calculations, or the default from
	 * {@link #getHypervolumeDelta()} if no problem-specific value is configured.
	 * 
	 * @param problem the problem name
	 * @return the hypervolume delta
	 */
	public static double getProblemSpecificHypervolumeDelta(String problem) {
		String key = createKey(Settings.KEY_PROBLEM_PREFIX, problem, "normalization", "delta");
		
		if (PROPERTIES.contains(key)) {
			return PROPERTIES.getDouble(key);
		}
		
		return getHypervolumeDelta();
	}
	
	/**
	 * Returns a problem-specific &epsilon; value used in algorithms, archives, and other objects based on &epsilon;
	 * dominance.
	 * 
	 * @param problem the problem name
	 * @return the &epsilon; values or {@code null} if no override provided
	 */
	public static Epsilons getProblemSpecificEpsilons(String problem) {
		String key = createKey(Settings.KEY_PROBLEM_PREFIX, problem, "epsilons");
		
		if (PROPERTIES.contains(key)) {
			return new Epsilons(PROPERTIES.getDoubleArray(key));
		}
		
		return null;
	}
	
	/**
	 * Returns {@code true} if normalization has been disabled for the given problem instance.
	 * 
	 * @param problem the problem name
	 * @return {@code true} if disabled; {@code false} otherwise
	 */
	public static boolean isNormalizationDisabled(String problem) {
		return PROPERTIES.getBoolean(createKey(Settings.KEY_PROBLEM_PREFIX, problem, "normalization", "disabled"),
				false);
	}
	
	/**
	 * Returns {@code true} if genetic programming functions should use protection against invalid arguments that
	 * would otherwise result in {@code NaN} or other invalid values; {@code false} otherwise.
	 * 
	 * @return {@code true} if genetic programming functions should use protection against invalid arguments that
	 *         would otherwise result in {@code NaN} or other invalid values; {@code false} otherwise
	 */
	public static boolean isProtectedFunctions() {
		return PROPERTIES.getBoolean(KEY_GP_PROTECTED_FUNCTIONS, true);
	}
	
	/**
	 * Returns {@code true} if debugging is enabled when running external problems.
	 * 
	 * @return {@code true} if debugging for external problems is enabled; {@code false} otherwise
	 */
	public static boolean isExternalProblemDebuggingEnabled() {
		return PROPERTIES.getBoolean(KEY_EXTERNAL_PROBLEM_DEBUGGING, false);
	}
	
	/**
	 * Returns the number of retry attempts when connecting to an external problem with sockets.
	 * 
	 * @return the number of retry attempts
	 */
	public static int getExternalProblemRetryAttempts() {
		return PROPERTIES.getInt(KEY_EXTERNAL_RETRY_ATTEMPTS, 5);
	}
	
	/**
	 * Returns the delay, given in seconds, between retry attempts when connecting to an external problem with sockets.
	 * 
	 * @return the retry delay
	 */
	public static Duration getExternalProblemRetryDelay() {
		return Duration.ofSeconds(PROPERTIES.getInt(KEY_EXTERNAL_RETRY_DELAY, 1));
	}
	
	/**
	 * Returns the timeout, given in seconds, the external process is given to shutdown cleanly before a forceful
	 * shutdown is attempted.
	 * 
	 * @return the shutdown timeout
	 */
	public static Duration getExternalProblemShutdownTimeout() {
		return Duration.ofSeconds(PROPERTIES.getInt(KEY_EXTERNAL_SHUTDOWN_TIMEOUT, 10));
	}
	
	/**
	 * Returns {@code true} if the CMA-ES algorithm has consistency checks enabled.
	 * 
	 * @return {@code true} if the CMA-ES algorithm has consistency checks enabled; {@code false} otherwise
	 */
	public static boolean isCMAESConsistencyCheckingEnabled() {
		return PROPERTIES.getBoolean(KEY_CMAES_CHECK_CONSISTENCY, false);
	}
	
	/**
	 * Returns the MOEA Framework icon, supporting a varient of sizes.
	 * 
	 * @return the MOEA Framework icon
	 */
	public static MultiResolutionImage getIcon() {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		
		return new BaseMultiResolutionImage(
				toolkit.getImage(Settings.class.getResource("logo16.png")),
				toolkit.getImage(Settings.class.getResource("logo24.png")),
				toolkit.getImage(Settings.class.getResource("logo32.png")),
				toolkit.getImage(Settings.class.getResource("logo48.png")),
				toolkit.getImage(Settings.class.getResource("logo64.png")),
				toolkit.getImage(Settings.class.getResource("logo128.png")),
				toolkit.getImage(Settings.class.getResource("logo256.png")));
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
