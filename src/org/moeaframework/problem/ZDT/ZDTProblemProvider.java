package org.moeaframework.problem.ZDT;

import org.moeaframework.core.spi.RegisteredProblemProvider;

public class ZDTProblemProvider extends RegisteredProblemProvider {

	public ZDTProblemProvider() {
		super();
		
		register("ZDT1", () -> new ZDT1(), "pf/ZDT1.pf");
		register("ZDT2", () -> new ZDT2(), "pf/ZDT2.pf");
		register("ZDT3", () -> new ZDT3(), "pf/ZDT3.pf");
		register("ZDT4", () -> new ZDT4(), "pf/ZDT4.pf");
		register("ZDT5", () -> new ZDT5(), "pf/ZDT5.pf");
		register("ZDT6", () -> new ZDT6(), "pf/ZDT6.pf");
	}
}
