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
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

import org.moeaframework.util.Localization;

public class SelectValueAction<T> extends LocalizedAction implements SettingChangedListener {
	
	private static final long serialVersionUID = 3472097803101404661L;

	private final Setting<T> setting;
	
	private final T value;
	
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
	
	public JMenuItem toMenuItem() {
		return new JRadioButtonMenuItem(this);
	}
	
}