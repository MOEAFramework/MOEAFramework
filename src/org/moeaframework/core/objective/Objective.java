package org.moeaframework.core.objective;

import java.io.Serializable;

public interface Objective extends Comparable<Objective>, Serializable {
	
	public double getValue();
	
	public void setValue(double value);
	
	public Objective copy();
	
}
