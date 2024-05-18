package org.moeaframework.util.distributed;

import org.moeaframework.Assert;
import org.moeaframework.core.Solution;

public class TestableSynchronizedProblem extends TestableFutureProblem {
	
	private volatile boolean isInvoked;
	
	public TestableSynchronizedProblem() {
		super();
	}

	@Override
	public synchronized void evaluate(Solution solution) {
		Assert.assertFalse("Expected synchronized problem to serialize evaluations", isInvoked);
		
		isInvoked = true;
		super.evaluate(solution);
		isInvoked = false;
	}
	
}