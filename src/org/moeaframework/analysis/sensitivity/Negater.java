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
package org.moeaframework.analysis.sensitivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.moeaframework.util.CommandLineUtility;
import org.moeaframework.util.TypedProperties;

/**
 * Command line utility for negating objective values in result files.  As the
 * MOEA Framework only operates on minimization objectives, maximization
 * objectives must be negated prior to their use.  This utility can be used to
 * either apply or revert any negation.
 * <p>
 * This utility modifies the file in place.  Avoid killing the process as doing
 * so may leave the file(s) in a corrupted state.
 * <p>
 * Usage: {@code java -cp "..." org.moeaframework.analysis.sensitivity.Negater <options> <files>}
 * 
 * <table>
 *   <caption style="text-align: left">Arguments:</caption>
 *   <tr>
 *     <td>{@code -d, --direction}</td>
 *     <td>The optimization direction (required).  A comma-separated list with
 *         1 if the objective should be negated (maximized) and 0 otherwise
 *         (e.g., {@code -d 1,0,1} to negate the first and third objective).
 *         </td>
 *   </tr>
 *   <tr>
 *     <td>{@code <files>}</td>
 *     <td>The files to be negated.</td>
 *   </tr>
 * </table>
 */
public class Negater extends CommandLineUtility {
    
    /**
     * Constructs the command line utility for negating the objectives in
     * result files.
     */
    public Negater() {
        super();
    }

    @Override
    public Options getOptions() {
        Options options = super.getOptions();

        options.addOption(Option.builder("d")
        		.longOpt("direction")
        		.hasArg()
                .argName("d1,d2,...")
                .required()
                .build());

        return options;
    }

    @Override
    public void run(CommandLine commandLine) throws Exception {
        TypedProperties properties = TypedProperties.withProperty("direction",
                commandLine.getOptionValue("direction"));
        int[] directions = properties.getIntArray("direction");

        outer: for (String filename : commandLine.getArgs()) {
            List<String> lines = new ArrayList<String>();
            String entry = null;

            // read the entire file
            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                while ((entry = reader.readLine()) != null) {
                    lines.add(entry);
                }
            }
            
            // validate the file to detect any errors prior to overwriting
            for (String line : lines) {
                try {
                    if (!line.startsWith("#") && !line.startsWith("//")) {
                        String[] tokens = line.split("\\s+");
                        
                        if (tokens.length != directions.length) {
                            System.err.println("unable to negate values in " + filename + ", incorrect number of values in a row");
                            continue outer;
                        }
                        
                        for (int j = 0; j < tokens.length; j++) {
                            if (directions[j] != 0) {
                                Double.parseDouble(tokens[j]);
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    System.err.println("unable to negate values in " + filename + ", unable to parse number");
                    continue outer;
                }
            }

            // overwrite the file
            try (PrintStream writer = new PrintStream(new File(filename))) {
                for (String line : lines) {
                    if (line.startsWith("#") || line.startsWith("//")) {
                        writer.println(line);
                    } else {
                        String[] tokens = line.split("\\s+");

                        for (int j = 0; j < tokens.length; j++) {
                            if (j > 0) {
                                writer.print(' ');
                            }

                            if (directions[j] == 0) {
                                writer.print(tokens[j]);
                            } else {
                                double value = Double.parseDouble(tokens[j]);
                                writer.print(-value);
                            }
                        }

                        writer.println();
                    }
                }
            }
        }
    }
    
    /**
     * Starts the command line utility for negating the objectives in
     * result files.
     * 
     * @param args the command line arguments
	 * @throws Exception if an error occurred
     */
    public static void main(String[] args) throws Exception {
        new Negater().start(args);
    }

}
