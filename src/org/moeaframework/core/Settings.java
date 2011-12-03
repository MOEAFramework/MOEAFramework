/* Copyright 2009-2011 David Hadka
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

import org.moeaframework.util.TypedProperties;
import org.moeaframework.util.io.FileProtection;
import org.moeaframework.core.indicator.Hypervolume;

/**
 * Global settings used by this framework.  The {@code PROPERTIES} object
 * contains the system properties and optionally the contents of a 
 * configuration file (properties in the configuration file take precedence).
 * By default, the {@code global.properties} file is loaded, but can be
 * specified using the {@code org.moeaframework.configuration} system
 * property.
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
	 * Store the new line character to prevent repetitive calls to
	 * {@code System.getProperty("line.separator")}.
	 */
	public static final String NEW_LINE = System.getProperty("line.separator");

	/**
	 * The global properties object.
	 */
	public static final TypedProperties PROPERTIES;
	
	/**
	 * The prefix for all property keys.
	 */
	public static final String KEY_PREFIX = "org.moeaframework.";
	
	/**
	 * The property key for the continuity correction flag.
	 */
	public static final String KEY_CONTINUITY_CORRECTION = KEY_PREFIX +
			"util.statistics.continuity_correction";
	
	/**
	 * The property key for the hypervolume delta when determining the
	 * reference point.
	 */
	public static final String KEY_HYPERVOLUME_DELTA = KEY_PREFIX +
			"core.indicator.hypervolume_delta";
	
	/**
	 * The property key for the hypervolume command.
	 */
	public static final String KEY_HYPERVOLUME = KEY_PREFIX +
			"core.indicator.hypervolume";
	
	/**
	 * The property key for the hypervolume flag.
	 */
	public static final String KEY_HYPERVOLUME_ENABLED = KEY_PREFIX +
			"core.indicator.hypervolume_enabled";
	
	/**
	 * The property key for the JNLP Web Start flag.
	 */
	public static final String KEY_JNLP_ENABLED = KEY_PREFIX + "jnlp_enabled";
	
	/**
	 * The prefix for all problem property keys.
	 */
	public static final String KEY_PROBLEM_PREFIX = KEY_PREFIX + "problem.";
	
	/**
	 * The property key for the list of available problems.
	 */
	public static final String KEY_PROBLEM_LIST = KEY_PROBLEM_PREFIX +
			"problems";
	
	/**
	 * The prefix for all PISA property keys.
	 */
	public static final String KEY_PISA_PREFIX = KEY_PREFIX + "algorithm.pisa.";
	
	/**
	 * The property key for the list of available PISA algorithms.
	 */
	public static final String KEY_PISA_ALGORITHMS = KEY_PISA_PREFIX + 
			"algorithms";
	
	/**
	 * The property key for the poll rate.
	 */
	public static final String KEY_PISA_POLL = KEY_PISA_PREFIX + "poll";
	
	/**
	 * The prefix for PBS property keys.
	 */
	public static final String KEY_PBS_PREFIX = KEY_PREFIX + "util.pbs.";
	
	/**
	 * The property key for the PBS qsub command.
	 */
	public static final String KEY_PBS_QSUB = KEY_PBS_PREFIX + "qsub";
	
	/**
	 * The property key for the PBS script format.
	 */
	public static final String KEY_PBS_SCRIPT = KEY_PBS_PREFIX + "script";
	
	/**
	 * The property key for the PBS qstat command.
	 */
	public static final String KEY_PBS_QSTAT = KEY_PBS_PREFIX + "qstat";
	
	/**
	 * The property key for the PBS qdel command.
	 */
	public static final String KEY_PBS_QDEL = KEY_PBS_PREFIX + "qdel";

	/**
	 * The property key for the regular expression for identifying queued PBS
	 * jobs.
	 */
	public static final String KEY_PBS_QUEUED_REGEX = KEY_PBS_PREFIX + 
			"queued_regex";
	
	/**
	 * The property key for the regular expression for extracting the PBS job
	 * id from the qstat command.
	 */
	public static final String KEY_PBS_JOBID_REGEX = KEY_PBS_PREFIX + 
			"jobid_regex";
	
	/**
	 * The property key for the file protection mode.
	 */
	public static final String KEY_FILE_PROTECTION_MODE = KEY_PREFIX + 
			"util.io.file_protection_mode";
	
	/**
	 * The property key for the file protection file name format.
	 */
	public static final String KEY_FILE_PROTECTION_FORMAT = KEY_PREFIX +
			"util.io.file_protection_format";
	
	/**
	 * The property key for the algorithms available in the diagnostic tool.
	 */
	public static final String KEY_DIAGNOSTIC_TOOL_ALGORITHMS = KEY_PREFIX +
			"analysis.diagnostics.algorithms";
	
	/**
	 * The property key for the problems available in the diagnostic tool.
	 */
	public static final String KEY_DIAGNOSTIC_TOOL_PROBLEMS = KEY_PREFIX +
			"analysis.diagnostics.problems";
	
	/**
	 * Loads the properties.
	 */
	static {
		File file = new File(System.getProperty(KEY_PREFIX + "configuration", 
				"global.properties"));
		
		Properties properties = new Properties(System.getProperties());
		
		if (file.exists()) {
			try {
				loadProperties(file, properties);
			} catch (IOException e) {
				throw new FrameworkException(e);
			}
		}
		
		PROPERTIES = new TypedProperties(properties);
	}
	
	/**
	 * Private constructor to prevent instantiation.
	 */
	private Settings() {
		super();
	}
	
	/**
	 * Loads the properties from the specified file.
	 * 
	 * @param file the properties file
	 * @param properties the properties object where the properties are stored
	 * @throws IOException if an I/O error occurred
	 */
	private static void loadProperties(File file, Properties properties) 
	throws IOException {
		Reader reader = null;
		
		try {
			reader = new BufferedReader(new FileReader(file));
			properties.load(reader);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
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
	 * Returns the delta applied to the nadir point of the reference set when 
	 * calculating the hypervolume.  Having a non-zero delta is necessary to 
	 * ensure extremal solutions contribute to the hypervolume.
	 * 
	 * @return the delta applied to the nadir point of the reference set when 
	 *         calculating the hypervolume
	 */
	public static double getHypervolumeDelta() {
		return PROPERTIES.getDouble(KEY_HYPERVOLUME_DELTA, Settings.EPS);
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
	 * Returns [@code true} if JNLP Web Start mode is enabled; {@code false}
	 * otherwise.  When enabled, built-in files must be treated as resources,
	 * and accessed through a {@link ClassLoader}.
	 * 
	 * @return [@code true} if JNLP Web Start mode is enabled; {@code false}
	 *         otherwise
	 */
	public static boolean isJNLPEnabled() {
		return PROPERTIES.getBoolean(KEY_JNLP_ENABLED, false);
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
		return PROPERTIES.getString(KEY_PROBLEM_PREFIX + name + ".class", 
				null);
	}
	
	/**
	 * Returns the reference set filename for the specified problem.
	 * 
	 * @param name the problem name
	 * @return the reference set filename for the specified problem
	 */
	public static String getProblemReferenceSet(String name) {
		return PROPERTIES.getString(KEY_PROBLEM_PREFIX + name + ".referenceSet",
				null);
	}
	
	/**
	 * Returns the list of available PISA selectors.
	 * 
	 * @return the list of available PISA selectors
	 */
	public static String[] getPISAAlgorithms() {
		return PROPERTIES.getStringArray(KEY_PISA_ALGORITHMS, new String[0]);
	}
	
	/**
	 * Returns the poll rate, in milliseconds, for how often PISA checks the
	 * state file.
	 * 
	 * @return the poll rate, in milliseconds, for how often PISA checks the
	 *         state file
	 */
	public static int getPISAPollRate() {
		return PROPERTIES.getInt(KEY_PISA_POLL, 100);
	}
	
	/**
	 * Returns the command, invokable through {@link Runtime#exec(String)}, for
	 * starting the PISA selector.
	 * 
	 * @param name the name of the PISA selector
	 * @return the command, invokable through {@link Runtime#exec(String)}, for
	 *         starting the PISA selector
	 */
	public static String getPISACommand(String name) {
		return PROPERTIES.getString(KEY_PISA_PREFIX + name + ".command", null);
	}
	
	/**
	 * Returns the configuration file for the PISA selector.
	 * 
	 * @param name the name of the PISA selector
	 * @return the configuration file for the PISA selector
	 */
	public static String getPISAConfiguration(String name) {
		return PROPERTIES.getString(KEY_PISA_PREFIX + name + ".configuration",
				null);
	}

	/**
	 * Returns the command used for submitting PBS jobs.  The job script is 
	 * submitted by generating the script using {@link #getPBSScript()} and 
	 * piping the result to this command.
	 * 
	 * @return the command used for submitting PBS jobs
	 */
	public static String getPBSQsubCommand() {
		return PROPERTIES.getString(KEY_PBS_QSUB, "qsub");
	}
	
	/**
	 * Returns the PBS script format.  The following variable substitutions are
	 * provided:
	 * <ul>
	 *   <li>{0} job name
	 *   <li>{1} number of nodes
	 *   <li>{2} walltime hours
	 *   <li>{3} commands
	 * </ul>
	 *   
	 * @return the PBS script format
	 */
	public static String getPBSScript() {
		return PROPERTIES.getString(KEY_PBS_SCRIPT, 
				"#PBS -N {0}\r\n" +
				"#PBS -l nodes={1}\r\n" +
				"#PBS -l walltime={2}:00:00\r\n" +
				"#PBS -o output/{0}\r\n" +
				"#PBS -e error/{0}\r\n" +
				"{3}");
	}
	
	/**
	 * Returns the command used for retrieving PBS jobs for the current user.  
	 * The following variable substitutions are provided:
	 * <ul>
	 *   <li>{0} user id
	 * </ul>
	 *   
	 * @return the command used for retrieving PBS jobs for the current user
	 */
	public static String getPBSQstatCommand() {
		return PROPERTIES.getString(KEY_PBS_QSTAT, "qstat -u {0}");
	}

	/**
	 * Returns the command used for killing PBS jobs.  The following variable 
	 * substitutions are provided:
	 * <ul>
	 *   <li>{0} job id
	 * </ul>
	 *   
	 * @return the command used for killing PBS jobs
	 */
	public static String getPBSQdelCommand() {
		return PROPERTIES.getString(KEY_PBS_QDEL, "qdel {0}");
	}
	
	/**
	 * Returns the regular expression for detecting if a job is queued.  This 
	 * regular expression is applied to each output line from qstat.
	 * 
	 * @return the regular expression for detecting if a job is queued
	 */
	public static String getPBSQueuedRegex() {
		return PROPERTIES.getString(KEY_PBS_QUEUED_REGEX, "^.* Q   -- $");
	}
	
	/**
	 * Returns the regular expression for extracting the job id.  This regular 
	 * expression is applied to each output line from qstat, and should contain
	 * a single captured group containing the job id.  This regular expression
	 * should fail to match any line that does not contain a valid job id.
	 * 
	 * @return the regular expression for extracting the job id
	 */
	public static String getPBSJobIdRegex() {
		return PROPERTIES.getString(KEY_PBS_JOBID_REGEX, "(\\d+)\\..*");
	}
	
	/**
	 * Returns the file protection mode.  Valid modes include
	 * <ul>
	 *   <li>STRICT
	 *   <li>SAFE
	 *   <li>DISABLED
	 * </ul>
	 * 
	 * @return the file protection mode
	 */
	public static String getFileProtectionMode() {
		return PROPERTIES.getString(KEY_FILE_PROTECTION_MODE, 
				FileProtection.SAFE_MODE);
	}
	
	/**
	 * Returns the file protection file name format.  The following variable 
	 * substitutions are provided:
	 * <ul>
	 *   <li>{0} the filename of the file being validated
	 * </ul>
	 * 
	 * @return the file protection file name format
	 */
	public static String getFileProtectionFormat() {
		return PROPERTIES.getString(KEY_FILE_PROTECTION_FORMAT, 
				".{0}.md5");
	}
	
	public static String[] getDiagnosticToolAlgorithms() {
		return PROPERTIES.getStringArray(KEY_DIAGNOSTIC_TOOL_ALGORITHMS, 
				new String[] { "NSGAII", "GDE3", "eMOEA", "eNSGAII", 
				"MOEAD", "Random" });
	}
	
	public static String[] getDiagnosticToolProblems() {
		return PROPERTIES.getStringArray(KEY_DIAGNOSTIC_TOOL_PROBLEMS, 
				new String[] { 
				"DTLZ1_2", "DTLZ2_2", "DTLZ3_2", "DTLZ4_2", "DTLZ7_2", 
				"ROT_DTLZ1_2", "ROT_DTLZ2_2", "ROT_DTLZ3_2", "ROT_DTLZ4_2", "ROT_DTLZ7_2", 
				"UF1", "UF2", "UF3", "UF4", "UF5", "UF6", "UF7", "UF8", "UF9", "UF10", "UF11", "UF12", "UF13",
				"CF1", "CF2", "CF3", "CF4", "CF5", "CF6", "CF7", "CF8", "CF9", "CF10",
				"LZ1", "LZ2", "LZ3", "LZ4", "LZ5", "LZ6", "LZ7", "LZ8", "LZ9",
				"WFG1_2", "WFG2_2", "WFG3_2", "WFG4_2", "WFG5_2", "WFG6_2", "WFG7_2", "WFG8_2", "WFG9_2",
				"ZDT1", "ZDT2", "ZDT3", "ZDT4", "ZDT5", "ZDT6" });
	}
	
}
