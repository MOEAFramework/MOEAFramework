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