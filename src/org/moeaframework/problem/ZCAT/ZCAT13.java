package org.moeaframework.problem.ZCAT;

public class ZCAT13 extends ZCAT {
	
	public ZCAT13(int numberOfObjectives) {
		this(numberOfObjectives, true);
	}
	
	public ZCAT13(int numberOfObjectives, boolean complicatedPS) {
		super(numberOfObjectives, 1, false, false, PFShapeFunction.F13,
				complicatedPS ? PSShapeFunction.G1 : PSShapeFunction.G0);
	}

}
