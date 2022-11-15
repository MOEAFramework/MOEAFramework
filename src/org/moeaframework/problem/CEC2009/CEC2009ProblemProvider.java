package org.moeaframework.problem.CEC2009;

import org.moeaframework.core.spi.RegisteredProblemProvider;

/**
 * Constructs and configures the CEC2009 problems.
 */
public class CEC2009ProblemProvider extends RegisteredProblemProvider {

	/**
	 * Constructs and registers the CEC2009 problems.
	 */
	public CEC2009ProblemProvider() {
		super();
		
		register("CF1", () -> new CF1(), "pf/CF1.dat");
		register("CF2", () -> new CF2(), "pf/CF2.dat");
		register("CF3", () -> new CF3(), "pf/CF3.dat");
		register("CF4", () -> new CF4(), "pf/CF4.dat");
		register("CF5", () -> new CF5(), "pf/CF5.dat");
		register("CF6", () -> new CF6(), "pf/CF6.dat");
		register("CF7", () -> new CF7(), "pf/CF7.dat");
		register("CF8", () -> new CF8(), "pf/CF8.dat");
		register("CF9", () -> new CF9(), "pf/CF9.dat");
		register("CF10", () -> new CF10(), "pf/CF10.dat");
		
		register("UF1", () -> new UF1(), "pf/UF1.dat");
		register("UF2", () -> new UF2(), "pf/UF2.dat");
		register("UF3", () -> new UF3(), "pf/UF3.dat");
		register("UF4", () -> new UF4(), "pf/UF4.dat");
		register("UF5", () -> new UF5(), "pf/UF5.dat");
		register("UF6", () -> new UF6(), "pf/UF6.dat");
		register("UF7", () -> new UF7(), "pf/UF7.dat");
		register("UF8", () -> new UF8(), "pf/UF8.dat");
		register("UF9", () -> new UF9(), "pf/UF9.dat");
		register("UF10", () -> new UF10(), "pf/UF10.dat");
		register("UF11", () -> new UF11(), "pf/R2_DTLZ2_M5.dat");
		register("UF12", () -> new UF12(), "pf/R3_DTLZ3_M5.dat");
		register("UF13", () -> new UF13(), "pf/WFG1_M5.dat");
	}
}
