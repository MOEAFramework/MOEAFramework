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
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.moeaframework.analysis.store.Blob;
import org.moeaframework.analysis.store.Container;
import org.moeaframework.analysis.store.DataStore;
import org.moeaframework.analysis.store.DataStoreFactory;
import org.moeaframework.analysis.store.DataStoreURI;
import org.moeaframework.analysis.store.Intent;
import org.moeaframework.analysis.store.fs.FileSystemDataStore;
import org.moeaframework.analysis.store.fs.HashFileMap;
import org.moeaframework.analysis.store.fs.HierarchicalFileMap;
import org.moeaframework.analysis.store.http.DataStoreHttpServer;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.util.cli.Command;
import org.moeaframework.util.cli.CommandLineUtility;

/**
 * Command line utility for accessing a {@link DataStore}.
 */
public class DataStoreTool extends CommandLineUtility {
		
	private DataStoreTool() {
		super();
	}
	
	@Override
	public List<Command> getCommands() {
		List<Command> commands = super.getCommands();
		
		commands.add(Command.of("type", DataStoreTypeCommand.class));
		commands.add(Command.of("list", DataStoreListCommand.class));
		commands.add(Command.of("details", DataStoreDetailsCommand.class));
		commands.add(Command.of("delete", DataStoreDeleteCommand.class));
		commands.add(Command.of("exists", DataStoreExistsCommand.class));
		commands.add(Command.of("create", DataStoreCreateCommand.class));
		commands.add(Command.of("get", DataStoreGetCommand.class));
		commands.add(Command.of("set", DataStoreSetCommand.class));
		commands.add(Command.of("copy", DataStoreCopyCommand.class));
		commands.add(Command.of("server", DataStoreServerCommand.class));
		commands.add(Command.of("lock", DataStoreLockCommand.class));
		commands.add(Command.of("unlock", DataStoreUnlockCommand.class));
		
		return commands;
	}
	
	@Override
	public void run(CommandLine commandLine) throws Exception {
		runCommand(commandLine);
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
	
	private abstract static class AbstractDataStoreCommand extends CommandLineUtility {
		
		@Override
		public void run(CommandLine commandLine) throws Exception {
			if (commandLine.getArgs().length < 1) {
				throw new ParseException("Expected one argument containing the URI");
			}
			
			DataStoreURI uri = DataStoreURI.parse(commandLine.getArgs()[0]);
						
			try (PrintWriter output = createOutputWriter()) {
				if (uri.getName() != null && !uri.getName().isBlank()) {
					onBlob(DataStoreFactory.getInstance().resolveBlob(uri.getURI()), commandLine);
				} else if (!uri.getReference().isRoot()) {
					onContainer(DataStoreFactory.getInstance().resolveContainer(uri.getURI()), commandLine);
				} else {
					onDataStore(DataStoreFactory.getInstance().getDataStore(uri.getURI()), commandLine);
				}
			}
		}
		
		public void onDataStore(DataStore dataStore, CommandLine commandLine) throws Exception {
			throw new FrameworkException("Command can not be used with a URI referencing a data store");
		}
		
		public void onContainer(Container container, CommandLine commandLine) throws Exception {
			throw new FrameworkException("Command can not be used with a URI referencing a container");
		}
		
		public void onBlob(Blob blob, CommandLine commandLine) throws Exception {
			throw new FrameworkException("Command can not be used with a URI referencing a blob");
		}
		
	}
	
	private static class DataStoreTypeCommand extends AbstractDataStoreCommand {

		@Override
		public void onDataStore(DataStore dataStore, CommandLine commandLine) {
			try (PrintWriter output = createOutputWriter()) {
				output.println("datastore");
			}
		}
		
		@Override
		public void onContainer(Container container, CommandLine commandLine) {
			try (PrintWriter output = createOutputWriter()) {
				output.println("container");
			}
		}
		
		@Override
		public void onBlob(Blob blob, CommandLine commandLine) {
			try (PrintWriter output = createOutputWriter()) {
				output.println("blob");
			}
		}
		
	}
	
	private static class DataStoreListCommand extends AbstractDataStoreCommand {
		
		@Override
		public void onDataStore(DataStore dataStore, CommandLine commandLine) {
			try (PrintWriter output = createOutputWriter()) {
				try (Stream<Container> stream = dataStore.streamContainers()) {
					stream.forEach(x -> output.println(x.getURI()));
				}
			}
		}
		
		@Override
		public void onContainer(Container container, CommandLine commandLine) {
			try (PrintWriter output = createOutputWriter()) {
				try (Stream<Blob> stream = container.streamBlobs()) {
					stream.forEach(x -> output.println(x.getURI()));
				}
			}
		}
		
		@Override
		public void onBlob(Blob blob, CommandLine commandLine) {
			try (PrintWriter output = createOutputWriter()) {
				output.println(blob.getURI());
			}
		}
		
	}
	
	private static class DataStoreDetailsCommand extends AbstractDataStoreCommand {
		
		@Override
		public Options getOptions() {
			Options options = super.getOptions();
			
			options.addOption(Option.builder("f")
					.longOpt("full")
					.build());
			
			return options;
		}

		@Override
		public void onDataStore(DataStore dataStore, CommandLine commandLine) {
			try (PrintWriter output = createOutputWriter()) {
				DataStoreURI uri = DataStoreURI.parse(commandLine.getArgs()[0]);
				output.println(dataStore.toJSON(uri.getURI(), commandLine.hasOption("full")));
			}
		}
		
		@Override
		public void onContainer(Container container, CommandLine commandLine) {
			try (PrintWriter output = createOutputWriter()) {
				DataStoreURI uri = DataStoreURI.parse(commandLine.getArgs()[0]);
				output.println(container.toJSON(uri.getURI(), commandLine.hasOption("full")));
			}
		}
		
		@Override
		public void onBlob(Blob blob, CommandLine commandLine) {
			try (PrintWriter output = createOutputWriter()) {
				DataStoreURI uri = DataStoreURI.parse(commandLine.getArgs()[0]);
				output.println(blob.toJSON(uri.getURI(), commandLine.hasOption("full")));
			}
		}
		
	}
	
	private static class DataStoreDeleteCommand extends AbstractDataStoreCommand {

		@Override
		public Options getOptions() {
			Options options = super.getOptions();
			
			options.addOption(Option.builder("y")
					.longOpt("yes")
					.build());
			
			return options;
		}

		@Override
		public void run(CommandLine commandLine) throws Exception {
			setAcceptConfirmations(commandLine.hasOption("yes"));
			super.run(commandLine);
		}
		
		@Override
		public void onDataStore(DataStore dataStore, CommandLine commandLine) {
			if (dataStore.exists() && prompt("Are you sure you want to delete the entire data store?")) {
				dataStore.delete();
			}
		}
		
		@Override
		public void onContainer(Container container, CommandLine commandLine) {
			if (container.exists() && prompt("Are you sure you want to delete this container?")) {
				container.delete();
			}
		}
		
		@Override
		public void onBlob(Blob blob, CommandLine commandLine) {
			if (blob.exists() && prompt("Are you sure you want to delete this blob?")) {
				blob.delete();
			}
		}
		
	}
	
	private static class DataStoreExistsCommand extends AbstractDataStoreCommand {
		
		@Override
		public void onDataStore(DataStore dataStore, CommandLine commandLine) {
			try (PrintWriter output = createOutputWriter()) {
				output.println(dataStore.exists());
			}
		}
		
		@Override
		public void onContainer(Container container, CommandLine commandLine) {
			try (PrintWriter output = createOutputWriter()) {
				output.println(container.exists());
			}
		}
		
		@Override
		public void onBlob(Blob blob, CommandLine commandLine) {
			try (PrintWriter output = createOutputWriter()) {
				output.println(blob.exists());
			}
		}
		
	}
	
	private static class DataStoreCreateCommand extends AbstractDataStoreCommand {

		@Override
		public Options getOptions() {
			Options options = super.getOptions();
			
			options.addOption(Option.builder()
					.longOpt("hash")
					.build());
			
			return options;
		}
		
		@Override
		public void onDataStore(DataStore dataStore, CommandLine commandLine) {
			try (PrintWriter output = createOutputWriter()) {
				if (dataStore.exists()) {
					output.println("Data store already exists!");
				}
				
				DataStoreURI uri = DataStoreURI.parse(commandLine.getArgs()[0]);
				
				FileSystemDataStore newDataStore = new FileSystemDataStore(uri.getPath(),
						commandLine.hasOption("hash") ? new HashFileMap(2) : new HierarchicalFileMap());
				newDataStore.create();
				
				output.println("Data store created at " + newDataStore.getURI());
			}
		}
		
	}
	
	private static class DataStoreGetCommand extends AbstractDataStoreCommand {
		
		@Override
		public Options getOptions() {
			Options options = super.getOptions();
			
			options.addOption(Option.builder("o")
					.longOpt("output")
					.hasArg()
					.argName("file")
					.build());
			
			return options;
		}
		
		@Override
		public void onBlob(Blob blob, CommandLine commandLine) throws Exception {
			try (PrintWriter output = createOutputWriter(commandLine.getOptionValue("output"))) {
				blob.extractTo(output);
			}
		}
		
	}
	
	private static class DataStoreSetCommand extends AbstractDataStoreCommand {
		
		@Override
		public Options getOptions() {
			Options options = super.getOptions();
			
			options.addOption(Option.builder("i")
					.longOpt("input")
					.hasArg()
					.argName("file")
					.build());
			
			return options;
		}
		
		@Override
		public void onBlob(Blob blob, CommandLine commandLine) throws Exception {
			if (commandLine.hasOption("input")) {
				blob.storeFrom(new File(commandLine.getOptionValue("input")));
			} else {
				blob.storeFrom(System.in);
			}
		}
		
	}
	
	private static class DataStoreCopyCommand extends AbstractDataStoreCommand {
		
		@Override
		public void onDataStore(DataStore dataStore, CommandLine commandLine) throws Exception {
			if (commandLine.getArgs().length < 2) {
				throw new ParseException("Missing the destination URI");
			}
			
			DataStoreURI uri = DataStoreURI.parse(commandLine.getArgs()[1]);
			
			if ((uri.getName() != null && !uri.getName().isBlank()) || !uri.getReference().isRoot()) {
				throw new ParseException("Destination URI must reference a data store");
			}
			
			try (PrintWriter output = createOutputWriter()) {
				copy(dataStore, DataStoreFactory.getInstance().getDataStore(uri.getURI()), output);
			}
		}
		
		private void copy(DataStore src, DataStore dest, PrintWriter output) {
			copy(src.getRootContainer(), dest.getRootContainer(), output);
			
			try (Stream<Container> stream = src.streamContainers()) {
				stream.forEach(srcContainer -> {
					output.println("Copying container: " + srcContainer.getReference() + "...");

					Container destContainer = dest.getContainer(srcContainer.getReference());
					copy(srcContainer, destContainer, output);
				});
			}
		}
		
		private void copy(Container src, Container dest, PrintWriter output) {
			try (Stream<Blob> blobStream = src.streamBlobs()) {
				blobStream.forEach(srcBlob -> {
					output.print("  > Copying blob: " + srcBlob.getName() + "...");
					
					Blob destBlob = dest.getBlob(srcBlob.getName());
					
					destBlob.storeOutputStream(destStream -> {
						srcBlob.extractTo(destStream);
					});
					
					output.println("done.");
				});
			}
		}
		
	}
	
	private static class DataStoreServerCommand extends AbstractDataStoreCommand {
		
		@Override
		public Options getOptions() {
			Options options = super.getOptions();
			
			options.addOption(Option.builder()
					.longOpt("hostname")
					.hasArg()
					.build());
			options.addOption(Option.builder()
					.longOpt("port")
					.hasArg()
					.build());
			options.addOption(Option.builder("p")
					.longOpt("path")
					.hasArg()
					.build());
			
			return options;
		}
		
		@Override
		public void onDataStore(DataStore dataStore, CommandLine commandLine) throws Exception {
			DataStoreURI uri = DataStoreURI.parse(commandLine.getArgs()[0]);
			
			DataStoreHttpServer server = new DataStoreHttpServer(new InetSocketAddress(
					commandLine.getOptionValue("hostname", "localhost"),
					Integer.parseInt(commandLine.getOptionValue("port", "8080"))));
			server.registerShutdownHook();
			server.configure(
					commandLine.getOptionValue("path", Path.of(".").relativize(uri.getPath()).toString()),
					dataStore);
		}
		
	}
	
	private abstract static class DataStoreIntentCommand extends AbstractDataStoreCommand {
		
		private final Intent intent;
		
		public DataStoreIntentCommand(Intent intent) {
			super();
			this.intent = intent;
		}

		@Override
		public void onDataStore(DataStore dataStore, CommandLine commandLine) throws Exception {
			dataStore.setIntent(intent);
			
			try (PrintWriter output = createOutputWriter()) {
				output.println("Data store is now in " + dataStore.getIntent() + " mode");
			}
		}
		
	}
	
	private static class DataStoreLockCommand extends DataStoreIntentCommand {

		public DataStoreLockCommand() {
			super(Intent.READ_ONLY);
		}
		
	}
	
	private static class DataStoreUnlockCommand extends DataStoreIntentCommand {

		public DataStoreUnlockCommand() {
			super(Intent.READ_WRITE);
		}
		
	}

}
