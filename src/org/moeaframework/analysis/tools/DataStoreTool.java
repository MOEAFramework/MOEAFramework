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
import java.nio.file.Path;
import java.util.stream.Stream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.moeaframework.analysis.store.Blob;
import org.moeaframework.analysis.store.Container;
import org.moeaframework.analysis.store.DataStore;
import org.moeaframework.analysis.store.DataStoreFactory;
import org.moeaframework.analysis.store.DataStoreURI;
import org.moeaframework.analysis.store.http.DataStoreHttpServer;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.util.OptionCompleter;
import org.moeaframework.util.cli.CommandLineUtility;
import org.moeaframework.util.validate.Validate;

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
		
		options.addOption(Option.builder("u")
				.longOpt("uri")
				.hasArg()
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
		options.addOption(Option.builder("y")
				.longOpt("yes")
				.build());

		return options;
	}
	
	private String getOperation(CommandLine commandLine) {
		String[] args = commandLine.getArgs();
		OptionCompleter options = new OptionCompleter("type", "list", "details", "get", "set", "delete", "server");
		
		if (args.length < 1) {
			throw new IllegalArgumentException("Missing operation, please specify: " +
					String.join(", ", options.getOptions()));
		}
		
		String operation = options.lookup(args[0]);
		
		if (operation == null) {
			Validate.that("operation", operation).failUnsupportedOption(options.getOptions());
		}
		
		return operation;
	}
	
	private DataStoreURI getURI(CommandLine commandLine) {
		if (commandLine.hasOption("uri")) {
			return DataStoreURI.parse(commandLine.getOptionValue("uri"));
		}
		
		String[] args = commandLine.getArgs();
		
		if (args.length < 2) {
			throw new IllegalArgumentException("Missing URI, please specify after operation or with --uri");
		}
		
		return DataStoreURI.parse(args[1]);
	}

	@Override
	public void run(CommandLine commandLine) throws IOException {
		setAcceptConfirmations(commandLine.hasOption("yes"));
		
		String operation = getOperation(commandLine);
		DataStoreURI dsUri = getURI(commandLine);
		
		try (PrintWriter output = createOutputWriter(commandLine.getOptionValue("output"))) {
			if (operation.equalsIgnoreCase("type")) {
				if (dsUri.getName() != null && !dsUri.getName().isBlank()) {
					output.println("blob");
				} else if (!dsUri.getReference().isRoot()) {
					output.println("container");
				} else {
					output.println("datastore");
				}
			} else if (operation.equalsIgnoreCase("list")) {
				if (dsUri.getName() != null && !dsUri.getName().isBlank()) {
					Blob blob = DataStoreFactory.getInstance().resolveBlob(dsUri.getURI());
					output.println(blob.getURI());
				} else if (!dsUri.getReference().isRoot()) {
					Container container = DataStoreFactory.getInstance().resolveContainer(dsUri.getURI());
					
					try (Stream<Blob> stream = container.streamBlobs()) {
						stream.forEach(x -> output.println(x.getURI()));
					}
				} else {
					DataStore dataStore = DataStoreFactory.getInstance().getDataStore(dsUri.getURI());
				
					try (Stream<Container> stream = dataStore.streamContainers()) {
						stream.forEach(x -> output.println(x.getURI()));
					}
				}
			} else if (operation.equalsIgnoreCase("details")) {
				if (dsUri.getName() != null && !dsUri.getName().isBlank()) {
					Blob blob = DataStoreFactory.getInstance().resolveBlob(dsUri.getURI());
					output.println(blob.toJSON());
				} else if (!dsUri.getReference().isRoot()) {
					Container container = DataStoreFactory.getInstance().resolveContainer(dsUri.getURI());
					output.println(container.toJSON());
				} else {
					DataStore dataStore = DataStoreFactory.getInstance().getDataStore(dsUri.getURI());
					output.println(dataStore.toJSON());
				}
			} else if (operation.equalsIgnoreCase("delete")) {
				if (dsUri.getName() != null && !dsUri.getName().isBlank()) {
					Blob blob = DataStoreFactory.getInstance().resolveBlob(dsUri.getURI());

					if (blob.exists() && prompt("Are you sure you want to delete this blob?")) {
						blob.delete();
					}
				} else if (!dsUri.getReference().isRoot()) {
					Container container = DataStoreFactory.getInstance().resolveContainer(dsUri.getURI());

					if (container.exists() && prompt("Are you sure you want to delete this container?")) {
						container.delete();
					}
				} else {
					if (prompt("Are you sure you want to delete the entire data store?")) {
						DataStore dataStore = DataStoreFactory.getInstance().getDataStore(dsUri.getURI());
						
						try (Stream<Container> stream = dataStore.streamContainers()) {
							stream.forEach(Container::delete);
						}
						
						dataStore.getRootContainer().delete();
					}
				}
			} else if (operation.equalsIgnoreCase("get")) {
				if (dsUri.getName() == null || dsUri.getName().isBlank()) {
					throw new FrameworkException("--get can only be used with a URI referencing a blob");
				} else {
					Blob blob = DataStoreFactory.getInstance().resolveBlob(dsUri.getURI());
					blob.extractTo(output);
				}
			} else if (operation.equalsIgnoreCase("set")) {
				if (dsUri.getName() == null || dsUri.getName().isBlank()) {
					throw new FrameworkException("--set can only be used with a URI referencing a blob");
				} else {
					Blob blob = DataStoreFactory.getInstance().resolveBlob(dsUri.getURI());
					
					if (commandLine.hasOption("input")) {
						blob.storeFrom(new File(commandLine.getOptionValue("input")));
					} else {
						blob.storeFrom(System.in);
					}
				}
			} else if (operation.equalsIgnoreCase("server")) {
				DataStore dataStore = DataStoreFactory.getInstance().getDataStore(dsUri.getURI());
				String[] args = commandLine.getArgs();
				String path = args.length >= 3 ? args[2] : Path.of(".").relativize(dsUri.getPath()).toString();
				
				DataStoreHttpServer server = new DataStoreHttpServer();
				server.registerShutdownHook();
				server.configure(path, dataStore);
			} else {
				throw new IllegalArgumentException("Unsupported operation " + operation);
			}
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
