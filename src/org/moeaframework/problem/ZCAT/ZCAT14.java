package org.moeaframework.problem.ZCAT;

public class ZCAT14 extends ZCAT {
	
	public ZCAT14(int numberOfObjectives) {
		this(numberOfObjectives, true);
	}
	
	public ZCAT14(int numberOfObjectives, boolean complicatedPS) {
		super(numberOfObjectives, 1, false, false, PFShapeFunction.F14,
				complicatedPS ? PSShapeFunction.G6 : PSShapeFunction.G0);
	}

}
