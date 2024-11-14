package org.moeaframework.util.mvc;

import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JMenuItem;

import org.moeaframework.util.Localization;

public class RunnableAction extends LocalizedAction {
	
	private static final long serialVersionUID = 3633238192124429111L;
	
	private final Runnable runnable;
	
	public RunnableAction(String id, Localization localization, Runnable runnable) {
		super(id, localization);
		this.runnable = runnable;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		runnable.run();
	}
	
	public JButton toButton() {
		return new JButton(this);
	}
	
	public JMenuItem toMenuItem() {
		return new JMenuItem(this);
	}
	
}