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

	/**
	 * Constructs a setting with the default value.
	 * 
	 * @param defaultValue the default value
	 */
	public Setting(T defaultValue) {
		super();
		this.defaultValue = defaultValue;
		this.listeners = EventListenerSupport.create(SettingChangedListener.class);
		
		Validate.that("defaultValue", defaultValue).isNotNull();
	}
	
	/**
	 * Returns the default value for this setting.
	 * 
	 * @return the default value
	 */
	public T getDefaultValue() {
		return defaultValue;
	}
	
	/**
	 * Sets the value.  Any registered listeners are notified if the value changes.
	 * 
	 * @param newValue the new value
	 */
	public void set(T newValue) {
		if (Objects.equals(value, newValue)) {
			return;
		}
		
		value = newValue;
		listeners.fire().settingChanged(new SettingChangedEvent(this));
	}
	
	/**
	 * Gets the value.  If no value has been set, returns the default value.
	 * 
	 * @return the value
	 */
	public T get() {
		return value != null ? value : defaultValue;
	}
	
	/**
	 * Registers a listener to be notified when this setting value changes.
	 * 
	 * @param listener the listener to add
	 */
	public void addSettingChangedListener(SettingChangedListener listener) {
		listeners.addListener(listener);
	}
	
	/**
	 * Removes a listener so it is no longer notified of changes.
	 * 
	 * @param listener the listener to remove
	 */
	public void removeSettingChangedListener(SettingChangedListener listener) {
		listeners.removeListener(listener);
	}
	
}