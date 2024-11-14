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
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;

import org.moeaframework.util.Localization;

public class InvertedToggleAction extends LocalizedAction implements SettingChangedListener {
	
	private static final long serialVersionUID = -3694809042542129384L;
	
	private final Toggle toggle;
	
	public InvertedToggleAction(String id, Localization localization, Toggle toggle) {
		super(id, localization);
		this.toggle = toggle;
		
		putValue(Action.SELECTED_KEY, !toggle.get());
		toggle.addSettingChangedListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		toggle.set(!(Boolean)getValue(Action.SELECTED_KEY));
	}
	
	@Override
	public void settingChanged(SettingChangedEvent event) {
		if (event.getSource().equals(toggle)) {
			putValue(Action.SELECTED_KEY, !toggle.get());
		}
	}
	
	public JMenuItem toMenuItem() {
		return new JCheckBoxMenuItem(this);
	}
	
}