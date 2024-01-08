package org.moeaframework.problem.ZCAT;

public class ZCAT12 extends ZCAT {
	
	public ZCAT12(int numberOfObjectives) {
		this(numberOfObjectives, true);
	}
	
	public ZCAT12(int numberOfObjectives, boolean complicatedPS) {
		super(numberOfObjectives, 1, false, false, PFShapeFunction.F12,
				complicatedPS ? PSShapeFunction.G10 : PSShapeFunction.G0);
	}

}
