package org.moeaframework.problem.ZCAT;

public class ZCAT3 extends ZCAT {
	
	public ZCAT3(int numberOfObjectives) {
		this(numberOfObjectives, true);
	}
	
	public ZCAT3(int numberOfObjectives, boolean complicatedPS) {
		super(numberOfObjectives, 1, false, false, PFShapeFunction.F3,
				complicatedPS ? PSShapeFunction.G2 : PSShapeFunction.G0);
	}

}
