package org.moeaframework.util.mvc;

/**
 * A togglable setting representing a binary state (on/off, true/false, enabled/disabled).
 */
public class Toggle extends Setting<Boolean> {

	public Toggle(boolean defaultValue) {
		super(defaultValue);
	}
	
	public void flip() {
		set(!get());
	}
	
}