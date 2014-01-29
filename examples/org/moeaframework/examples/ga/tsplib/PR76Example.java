package org.moeaframework.examples.ga.tsplib;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PR76Example {
	
	public static final String RESOURCE =
			"examples/org/moeaframework/examples/ga/tsplib/pr76.tsp";

	public static void main(String[] args) throws IOException {
		File file = new File(RESOURCE);
		
		if (file.exists()) {
			TSPExample.solve(file);
		} else {
			InputStream input = PR76Example.class.getResourceAsStream(
					"/" + RESOURCE);
			
			if (input == null) {
				throw new FileNotFoundException(RESOURCE);
			} else {
				try {
					TSPExample.solve(new InputStreamReader(input));
				} finally {
					input.close();
				}
			}
		}
	}
	
}
