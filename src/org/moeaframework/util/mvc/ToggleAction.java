package org.moeaframework.util.mvc;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;

import org.moeaframework.util.Localization;

public class ToggleAction extends LocalizedAction implements SettingChangedListener {
	
	private static final long serialVersionUID = -992336279525967638L;

	private final Toggle toggle;
	
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
	
	public JMenuItem toMenuItem() {
		return new JCheckBoxMenuItem(this);
	}
	
}