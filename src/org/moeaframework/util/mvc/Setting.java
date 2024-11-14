package org.moeaframework.util.mvc;

import java.util.Objects;

import org.apache.commons.lang3.event.EventListenerSupport;
import org.moeaframework.util.validate.Validate;

/**
 * Setting that can fire events when the value changes.
 *  
 * @param <T> the underlying type of the setting
 */
public class Setting<T> {
		
	private final T defaultValue;
	
	private T value;
	
	private final EventListenerSupport<SettingChangedListener> listeners;

	public Setting(T defaultValue) {
		super();
		this.defaultValue = defaultValue;
		this.listeners = EventListenerSupport.create(SettingChangedListener.class);
		
		Validate.that("defaultValue", defaultValue).isNotNull();
	}
	
	public T getDefaultValue() {
		return defaultValue;
	}
	
	public void set(T newValue) {
		if (Objects.equals(value, newValue)) {
			return;
		}
		
		value = newValue;
		listeners.fire().settingChanged(new SettingChangedEvent(this));
	}
	
	public T get() {
		return value != null ? value : defaultValue;
	}
	
	public void addSettingChangedListener(SettingChangedListener listener) {
		listeners.addListener(listener);
	}
	
	public void removeSettingChangedListener(SettingChangedListener listener) {
		listeners.removeListener(listener);
	}
	
}