package org.moeaframework.problem.ZCAT;

public class ZCAT17 extends ZCAT {
	
	public ZCAT17(int numberOfObjectives) {
		this(numberOfObjectives, true);
	}
	
	public ZCAT17(int numberOfObjectives, boolean complicatedPS) {
		super(numberOfObjectives, 1, false, false, PFShapeFunction.F17,
				complicatedPS ? PSShapeFunction.G1 : PSShapeFunction.G0);
	}

}
