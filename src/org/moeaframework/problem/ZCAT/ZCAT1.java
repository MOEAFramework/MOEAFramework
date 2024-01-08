package org.moeaframework.problem.ZCAT;

public class ZCAT1 extends ZCAT {
	
	public ZCAT1(int numberOfObjectives) {
		this(numberOfObjectives, true);
	}
	
	public ZCAT1(int numberOfObjectives, boolean complicatedPS) {
		super(numberOfObjectives, 1, false, false, PFShapeFunction.F1,
				complicatedPS ? PSShapeFunction.G4 : PSShapeFunction.G0);
	}

}
