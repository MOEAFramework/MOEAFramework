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
import org.moeaframework.util.Localization;

public class SelectValueActionTest {
	
	@Test
	public void test() {
		Setting<Integer> setting = new Setting<>(5);
		SelectValueAction<Integer> action = new SelectValueAction<>("foo", Localization.getLocalization(SelectValueActionTest.class), setting, 10);

		Assert.assertEquals(5, setting.get());
		Assert.assertEquals(false, action.getValue(Action.SELECTED_KEY));
		
		setting.set(10);
		Assert.assertEquals(true, action.getValue(Action.SELECTED_KEY));
		
		setting.set(5);
		Assert.assertEquals(false, action.getValue(Action.SELECTED_KEY));
		
		action.actionPerformed(new ActionEvent(action, 0, "clicked"));
		Assert.assertEquals(10, setting.get());
	}
	
	@Test
	public void testLocalization() {
		Setting<Integer> setting = new Setting<>(5);
		SelectValueAction<Integer> action = new SelectValueAction<>("foo", Localization.getLocalization(SelectValueActionTest.class), setting, 10);
		
		Assert.assertEquals("Localized name", action.getValue(Action.NAME));
		Assert.assertEquals("Localized description", action.getValue(Action.SHORT_DESCRIPTION));
	}

}
