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
package org.moeaframework.analysis.tools;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.moeaframework.analysis.store.Blob;
import org.moeaframework.analysis.store.Container;
import org.moeaframework.analysis.store.DataStore;
import org.moeaframework.analysis.store.DataStoreFactory;
import org.moeaframework.analysis.store.DataStoreURI;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.util.cli.CommandLineUtility;

/**
 * Command line utility for accessing a {@link DataStore}.
 */
public class DataStoreTool extends CommandLineUtility {

	private DataStoreTool() {
		super();
	}

	@Override
	public Options getOptions() {
		Options options = super.getOptions();
		
		OptionGroup operationGroup = new OptionGroup();
		operationGroup.addOption(Option.builder()
				.longOpt("list")
				.build());
		operationGroup.addOption(Option.builder()
				.longOpt("get")
				.build());
		operationGroup.addOption(Option.builder()
				.longOpt("set")
				.build());
		operationGroup.addOption(Option.builder()
				.longOpt("delete")
				.build());
		
		// TODO: copy. create, clone?
		
		options.addOptionGroup(operationGroup);
		
		options.addOption(Option.builder("u")
				.longOpt("uri")
				.hasArg()
				.required()
				.build());
		options.addOption(Option.builder("i")
				.longOpt("input")
				.hasArg()
				.argName("file")
				.build());
		options.addOption(Option.builder("o")
				.longOpt("output")
				.hasArg()
				.argName("file")
				.build());

		return options;
	}

	@Override
	public void run(CommandLine commandLine) throws IOException {
		try (PrintWriter output = createOutputWriter(commandLine.getOptionValue("output"))) {
			DataStoreURI dsUri = DataStoreURI.parse(commandLine.getOptionValue("uri"));

			if (commandLine.hasOption("list")) {
				if (dsUri.getFragment() != null && !dsUri.getFragment().isBlank()) {
					throw new FrameworkException("--list can only be used with a URI referencing a data store or container");
				} else if (!dsUri.getQuery().isEmpty()) {
					Container container = DataStoreFactory.getInstance().resolveContainer(dsUri.getURI());
					
					for (Blob blob : container.listBlobs()) {
						output.println(blob.getURI());
					}
				} else {
					DataStore dataStore = DataStoreFactory.getInstance().getDataStore(dsUri.getURI());
				
					for (Container container : dataStore.listContainers()) {
						output.println(container.getURI());
					}
				}
			} else if (commandLine.hasOption("delete")) {
				if (dsUri.getFragment() != null && !dsUri.getFragment().isBlank()) {
					if (prompt("Are you sure you want to delete this blob?")) {
						Blob blob = DataStoreFactory.getInstance().resolveBlob(dsUri.getURI());
						blob.delete();
					}
				} else if (!dsUri.getQuery().isEmpty()) {
					if (prompt("Are you sure you want to delete this container?")) {
						Container container = DataStoreFactory.getInstance().resolveContainer(dsUri.getURI());
						
						for (Blob blob : container.listBlobs()) {
							blob.delete();
						}
					}
				} else {
					if (prompt("Are you sure you want to delete the entire data store?")) {
						DataStore dataStore = DataStoreFactory.getInstance().getDataStore(dsUri.getURI());
					
						for (Container container : dataStore.listContainers()) {
							for (Blob blob : container.listBlobs()) {
								blob.delete();
							}
						}
					}
				}
			} else if (commandLine.hasOption("get")) {
				if (dsUri.getFragment() == null || dsUri.getFragment().isBlank()) {
					throw new FrameworkException("--get can only be used with a URI referencing a blob");
				} else {
					Blob blob = DataStoreFactory.getInstance().resolveBlob(dsUri.getURI());
					blob.extractTo(output);
				}
			} else if (commandLine.hasOption("set")) {
				if (dsUri.getFragment() == null || dsUri.getFragment().isBlank()) {
					throw new FrameworkException("--set can only be used with a URI referencing a blob");
				} else {
					Blob blob = DataStoreFactory.getInstance().resolveBlob(dsUri.getURI());
					
					if (commandLine.hasOption("input")) {
						blob.storeFrom(new File(commandLine.getOptionValue("input")));
					} else {
						blob.storeFrom(System.in);
					}
				}
			} else {
				throw new FrameworkException("Unknown operation, pick one of --list, --get, or --set");
			}
		}
	}
	
	private boolean prompt(String prompt) {
		try (Scanner scanner = new Scanner(System.in)) {
			System.out.print(prompt + " [y/N] ");
			String response = scanner.nextLine();
			return response.equalsIgnoreCase("y") || response.equalsIgnoreCase("yes");
		}
	}

	/**
	 * The main entry point for this command line utility.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new DataStoreTool().start(args);
	}

}
