package org.moeaframework.problem.ZCAT;

public class ZCAT7 extends ZCAT {
	
	public ZCAT7(int numberOfObjectives) {
		this(numberOfObjectives, true);
	}
	
	public ZCAT7(int numberOfObjectives, boolean complicatedPS) {
		super(numberOfObjectives, 1, false, false, PFShapeFunction.F7,
				complicatedPS ? PSShapeFunction.G5 : PSShapeFunction.G0);
	}

}
