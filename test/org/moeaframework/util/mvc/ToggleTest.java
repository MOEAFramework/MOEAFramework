/* Copyright 2009-2024 David Hadka
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
import org.moeaframework.util.Localization;

public class ToggleTest {
	
	@Test
	public void test() {
		Toggle toggle = new Toggle(false);
		ToggleAction action = new ToggleAction("foo", Localization.getLocalization(ToggleTest.class), toggle);

		Assert.assertEquals(false, toggle.get());
		Assert.assertEquals(false, action.getValue(Action.SELECTED_KEY));
		
		toggle.set(true);
		Assert.assertEquals(true, action.getValue(Action.SELECTED_KEY));
		
		action.putValue(Action.SELECTED_KEY, false);
		action.actionPerformed(new ActionEvent(action, 0, "clicked"));
		Assert.assertEquals(false, toggle.get());
	}
	
	@Test
	public void testInverted() {
		Toggle toggle = new Toggle(false);
		InvertedToggleAction action = new InvertedToggleAction("foo", Localization.getLocalization(ToggleTest.class), toggle);

		Assert.assertEquals(false, toggle.get());
		Assert.assertEquals(true, action.getValue(Action.SELECTED_KEY));
		
		toggle.set(true);
		Assert.assertEquals(false, action.getValue(Action.SELECTED_KEY));
		
		action.putValue(Action.SELECTED_KEY, false);
		action.actionPerformed(new ActionEvent(action, 0, "clicked"));
		Assert.assertEquals(true, toggle.get());
	}
	
	@Test
	public void testLocalization() {
		Toggle toggle = new Toggle(false);
		ToggleAction action = new ToggleAction("foo", Localization.getLocalization(ToggleTest.class), toggle);
		
		Assert.assertEquals("Localized name", action.getValue(Action.NAME));
		Assert.assertEquals("Localized description", action.getValue(Action.SHORT_DESCRIPTION));
	}

}
