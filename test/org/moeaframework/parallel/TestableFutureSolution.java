/* Copyright 2009-2025 David Hadka
 *
 * This file is part of the MOEA Framework.
 *
 * The MOEA Framework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * The MOEA Framework is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the MOEA Framework.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.moeaframework.parallel;

import java.util.concurrent.Future;

import org.moeaframework.Assert;
import org.moeaframework.core.Solution;

public class TestableFutureSolution extends FutureSolution {

	private static final long serialVersionUID = 2833940082100144051L;
	
	private boolean isUpdated;
	
	public TestableFutureSolution(Solution solution) {
		super(solution);
	}

	@Override
	public synchronized void update() {
		super.update();
		isUpdated = true;
	}
	
	@Override
	public synchronized void setFuture(Future<Solution> future) {
		super.setFuture(future);
		isUpdated = false;
	}
	
	public void randomize() {
		for (int i = 0; i < getNumberOfVariables(); i++) {
			getVariable(i).randomize();
		}
	}
	
	public void assertUpdated() {
		Assert.assertTrue("Expected future solution to be updated", isUpdated);
	}
	
	public void assertNotUpdated() {
		Assert.assertFalse("Expected future solution to not be updated", isUpdated);
	}
	
	public void assertEqualsTo(Solution other) {
		Assert.assertEquals(this, other);
	}
	
}