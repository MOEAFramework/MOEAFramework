package org.moeaframework.problem.LZ;

import org.moeaframework.core.spi.RegisteredProblemProvider;

/**
 * Problem provider for the LZ test problems.
 */
public class LZProblemProvider extends RegisteredProblemProvider {

	/**
	 * Constructs and registers the LZ problems.
	 */
	public LZProblemProvider() {
		super();
		
		register("LZ1", () -> new LZ1(), "pf/LZ09_F1.pf");
		register("LZ2", () -> new LZ2(), "pf/LZ09_F2.pf");
		register("LZ3", () -> new LZ3(), "pf/LZ09_F3.pf");
		register("LZ4", () -> new LZ4(), "pf/LZ09_F4.pf");
		register("LZ5", () -> new LZ5(), "pf/LZ09_F5.pf");
		register("LZ6", () -> new LZ6(), "pf/LZ09_F6.pf");
		register("LZ7", () -> new LZ7(), "pf/LZ09_F7.pf");
		register("LZ8", () -> new LZ8(), "pf/LZ09_F8.pf");
		register("LZ9", () -> new LZ9(), "pf/LZ09_F9.pf");
	}
}
