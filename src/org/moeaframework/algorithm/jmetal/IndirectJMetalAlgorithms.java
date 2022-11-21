package org.moeaframework.algorithm.jmetal;

import org.moeaframework.core.spi.IndirectAlgorithmProvider;

/**
 * Allows dynamically loading JMetal algorithms without creating a runtime dependency on JMetal.
 */
public class IndirectJMetalAlgorithms extends IndirectAlgorithmProvider {

	public IndirectJMetalAlgorithms() {
		super("org.moeaframework.algorithm.jmetal.JMetalAlgorithms");
	}
	
}

