package org.moeaframework;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.moeaframework.util.io.RedirectStream;

public class Make {
	
	private Make() {
		super();
	}
	
	/**
	 * Run make in the given folder.
	 * 
	 * @param folder the folder in which make is executed
	 * @param args any additional arguments passed to make
	 * @return the captured output from make
	 */
	public static String runMake(File folder, String... args) throws IOException {
		List<String> command = new ArrayList<String>();
		command.add("make");
		command.addAll(Arrays.asList(args));
		
		System.out.println("Running '" + command.stream().collect(Collectors.joining(" ")) + "' from folder " + folder);
		
		try {
			ProcessBuilder processBuilder = new ProcessBuilder(command);
			processBuilder.directory(folder);
			
			return RedirectStream.capture(processBuilder);
		} catch (InterruptedException e) {
			throw new IOException(e);
		}
	}
	
	/**
	 * Returns {@code true} if the 'make' command exists on the system.
	 * 
	 * @return {@code true} if the 'make' command exists; {@code false} otherwise
	 */
	public static boolean verifyMakeExists() {
		try {
			runMake(new File("."), "--version");
			return true;
		} catch (IOException e) {
			System.err.println(e);
			return false;
		}
	}

}
