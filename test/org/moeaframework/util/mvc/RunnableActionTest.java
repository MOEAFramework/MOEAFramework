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
package org.moeaframework.util.mvc;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.CallCounter;
import org.moeaframework.util.Localization;

public class RunnableActionTest {
	
	@Test
	public void test() {
		CallCounter<Runnable> counter = CallCounter.mockRunnable();
		RunnableAction action = new RunnableAction("foo", Localization.getLocalization(RunnableActionTest.class), counter.getProxy());

		Assert.assertEquals(0, counter.getTotalCallCount());
		action.actionPerformed(new ActionEvent(action, 0, "clicked"));
		Assert.assertEquals(1, counter.getTotalCallCount());
	}
	
	@Test
	public void testLocalization() {
		RunnableAction action = new RunnableAction("foo", Localization.getLocalization(RunnableActionTest.class), () -> {});
		
		Assert.assertEquals("Localized name", action.getValue(Action.NAME));
		Assert.assertEquals("Localized description", action.getValue(Action.SHORT_DESCRIPTION));
	}

}
