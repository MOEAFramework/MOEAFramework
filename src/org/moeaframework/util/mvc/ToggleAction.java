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
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;

import org.moeaframework.util.Localization;

/**
 * Action backed by a {@link Toggle} setting.  GUI components based on this action are typically represented by a
 * check box.
 */
public class ToggleAction extends LocalizedAction implements SettingChangedListener {
	
	private static final long serialVersionUID = -992336279525967638L;

	/**
	 * The underlying toggle setting.
	 */
	private final Toggle toggle;
	
	/**
	 * Constructs a new toggle action.
	 * 
	 * @param id the id for localization
	 * @param localization the source for localization strings
	 * @param toggle the underlying toggle setting
	 */
	public ToggleAction(String id, Localization localization, Toggle toggle) {
		super(id, localization);
		this.toggle = toggle;
		
		putValue(Action.SELECTED_KEY, toggle.get());
		toggle.addSettingChangedListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		toggle.set((Boolean)getValue(Action.SELECTED_KEY));
	}
	
	@Override
	public void settingChanged(SettingChangedEvent event) {
		if (event.getSource().equals(toggle)) {
			putValue(Action.SELECTED_KEY, toggle.get());
		}
	}
	
	/**
	 * Convenience method to create a {@link JMenuItem} based on this action.
	 * 
	 * @return the menu item
	 */
	public JMenuItem toMenuItem() {
		return new JCheckBoxMenuItem(this);
	}
	
}