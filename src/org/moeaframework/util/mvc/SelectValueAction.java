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
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

import org.moeaframework.util.Localization;

/**
 * Action that assigns a fixed value to a setting.  GUI components based on this action are typically radio buttons.
 * This action handles selection, so the components do not need to be placed within a button group.
 * 
 * @param <T> the type of the setting
 */
public class SelectValueAction<T> extends LocalizedAction implements SettingChangedListener {
	
	private static final long serialVersionUID = 3472097803101404661L;

	private final Setting<T> setting;
	
	private final T value;
	
	/**
	 * Constructs a new action to assign a fixed value.
	 * 
	 * @param id the id for localization
	 * @param localization the source of localization strings
	 * @param setting the underlying setting
	 * @param value the fixed value
	 */
	public SelectValueAction(String id, Localization localization, Setting<T> setting, T value) {
		super(id, localization, value);
		this.setting = setting;
		this.value = value;
		
		putValue(Action.SELECTED_KEY, setting.get().equals(value));
		setting.addSettingChangedListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		setting.set(value);
	}
	
	@Override
	public void settingChanged(SettingChangedEvent event) {
		if (event.getSource().equals(setting)) {
			putValue(Action.SELECTED_KEY, setting.get().equals(value));
		}
	}
	
	/**
	 * Convenience method to create a {@link JMenuItem} based on this action.
	 * 
	 * @return the menu item
	 */
	public JMenuItem toMenuItem() {
		return new JRadioButtonMenuItem(this);
	}
	
}