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
	
	public Toggle invert() {
		return new InvertedToggle(this);
	}
	
	private static class InvertedToggle extends Toggle {
		
		private final Toggle toggle;

		public InvertedToggle(Toggle toggle) {
			super(!toggle.getDefaultValue());
			this.toggle = toggle;
		}
		
		@Override
		public void set(Boolean newValue) {
			toggle.set(!newValue);
		}
		
		@Override
		public Boolean get() {
			return !toggle.get();
		}
		
		@Override
		public Toggle invert() {
			return toggle;
		}
		
	}
	
}