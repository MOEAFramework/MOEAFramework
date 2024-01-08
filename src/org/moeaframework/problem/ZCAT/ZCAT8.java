package org.moeaframework.problem.ZCAT;

public class ZCAT8 extends ZCAT {
	
	public ZCAT8(int numberOfObjectives) {
		this(numberOfObjectives, true);
	}
	
	public ZCAT8(int numberOfObjectives, boolean complicatedPS) {
		super(numberOfObjectives, 1, false, false, PFShapeFunction.F8,
				complicatedPS ? PSShapeFunction.G2 : PSShapeFunction.G0);
	}

}
