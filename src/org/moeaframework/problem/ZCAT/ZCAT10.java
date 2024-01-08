package org.moeaframework.problem.ZCAT;

public class ZCAT10 extends ZCAT {
	
	public ZCAT10(int numberOfObjectives) {
		this(numberOfObjectives, true);
	}
	
	public ZCAT10(int numberOfObjectives, boolean complicatedPS) {
		super(numberOfObjectives, 1, false, false, PFShapeFunction.F10,
				complicatedPS ? PSShapeFunction.G9 : PSShapeFunction.G0);
	}

}
