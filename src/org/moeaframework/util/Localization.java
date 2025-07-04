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
package org.moeaframework.util;

import java.awt.Toolkit;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Facilitates internalization (i18n) and localization (l10n) of strings.  Locale-specific strings are stored in a
 * single file in each package named {@code LocalStrings.properties}, {@code LocalStrings_{locale}.properties} or any
 * other format supported by {@link ResourceBundle}.  This class supports two modes of localization:
 * 
 * <h2>Package-specific</h2>
 * This is useful when classes in a package share common resources.  All classes have access to all stored resources.
 * Use the {@link #getLocalization(Class)} method to create instances of the {@code Localization} class for a specific
 * package.  Here, you use the {@link #getString(String)} and {@link #getString(String, Object...)} methods on the
 * {@code Localization} instance you previously created.
 * 
 * <h2>Class-specific</h2>
 * The {@link #getString(Class, String)} and {@link #getString(Class, String, Object...)} static methods do not require
 * you to explicitly create a new {@code Localization} instance.  In addition, the keys are automatically prefixed with
 * the class name.  For example, if your property file contains the following lines:
 * <pre>
 *   WindowA.title = First Title
 *   WindowB.title = Second Title
 * </pre>
 * You can read each entry with {@code Localization.getString(WindowA.class, "title")} and
 * {@code Localization.getString(WindowB.class, "title")}.  This is convenient for providing localization in subclasses
 * by using the {@code getClass()} method.
 */
public class Localization {
	
	/**
	 * The underlying resource bundle storing the key-value resource mapping.
	 */
	private final ResourceBundle bundle;
	
	/**
	 * The owner class, which is used to locate the resources relative to this class.
	 */
	private final Class<?> owner;

	/**
	 * Constructs a new localization instance based on the specified resource bundle.  If this resource bundle is
	 * {@code null}, then the {@code getString} methods return the {@code key}.
	 * 
	 * @param bundle the resource bundle storing the key-value resource mappings used by this localization object
	 * @param owner the class that owns this localalization instance, which is used to locate relative resources
	 */
	private Localization(ResourceBundle bundle, Class<?> owner) {
		super();
		this.bundle = bundle;
		this.owner = owner;
	}

	/**
	 * Returns the underlying resource bundle.
	 * 
	 * @return the underlying resource bundle
	 */
	public ResourceBundle getBundle() {
		return bundle;
	}

	/**
	 * Returns the locale of this localization.
	 * 
	 * @return the locale of this localization
	 */
	public Locale getLocale() {
		return bundle.getLocale();
	}

	/**
	 * Returns the localized string for the given key.  If the key is not found, then this methods returns the key
	 * itself.
	 * 
	 * @param key the key for the desired string
	 * @return the localized string for the given key
	 */
	public String getString(String key) {
		try {
			if (bundle == null) {
				return key;
			} else {
				return bundle.getString(key);
			}
		} catch (MissingResourceException e) {
			return key;
		}
	}
	
	/**
	 * Returns the icon defined by the given key.  If the key is not found, then {@code null} is returned.
	 * 
	 * @param key the key for the desired icon
	 * @return the icon
	 */
	public Icon getIcon(String key) {
		if (!containsKey(key)) {
			return null;
		}
		
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(owner.getResource(getString(key))));
	}
	
	/**
	 * Returns {@code true} if a localized string exists for the given key; {@code false} otherwise.
	 * 
	 * @param key the key for the desired string
	 * @return {@code true} if a localized string exists for the given key; {@code false} otherwise
	 */
	public boolean containsKey(String key) {
		if (bundle == null) {
			return false;
		}
			
		return bundle.containsKey(key);
	}

	/**
	 * Returns the localized string for the given key and formatting arguments.  This method uses {@link MessageFormat}
	 * for formatting the arguments.  If the key is not found, then this methods returns the key itself.
	 * 
	 * @param key the key for the desired string
	 * @param arguments the formatting arguments
	 * @return the localized string for the given key and formatting arguments
	 */
	public String getString(String key, Object... arguments) {
		MessageFormat format = new MessageFormat(getString(key), getLocale());
		return format.format(arguments, new StringBuffer(), null).toString();
	}
	
	/**
	 * Returns the localization object for the given class.
	 * 
	 * @param type the class requesting the localization object
	 * @return the localization object for the given class
	 */
	public static Localization getLocalization(Class<?> type) {
		return getLocalization(type, Locale.getDefault());
	}

	/**
	 * Returns the localization object for the given class and locale.
	 * 
	 * @param type the class requesting the localization object
	 * @param locale the target locale
	 * @return the localization object for the given class
	 */
	public static Localization getLocalization(Class<?> type, Locale locale) {
		ResourceBundle bundle = null;

		try {
			bundle = ResourceBundle.getBundle(type.getPackageName() + ".LocalStrings", locale);
		} catch (MissingResourceException e) {
			//bundle remains null, so the localization returns the key to provide a visual clue for debugging
		}

		return new Localization(bundle, type);
	}
	
	/**
	 * Returns {@code true} if a localized string exists for the given key; {@code false} otherwise.  This method
	 * automatically finds the correct resource bundle and key prefix appropriate for the class.
	 * 
	 * @param type the class requesting the localized string
	 * @param key the key (minus the class prefix)
	 * @return {@code true} if a localized string exists for the given key; {@code false} otherwise
	 */
	public static boolean containsKey(Class<?> type, String key) {
		return getLocalization(type).containsKey(type.getSimpleName() + "." + key);
	}

	/**
	 * Returns the localized string for the given key.  This method automatically finds the correct resource bundle and
	 * key prefix appropriate for the class.  For example, calling
	 * {@code Localization.getString(MainGUI.class, "title")} returns the localized string for the key
	 * {@code "MainGUI.title"}.
	 * 
	 * @param type the class requesting the localized string
	 * @param key the key (minus the class prefix)
	 * @return the localized string for the given key
	 */
	public static String getString(Class<?> type, String key) {
		return getLocalization(type).getString(type.getSimpleName() + "." + key);
	}

	/**
	 * Returns the localized string for the given key and formatting arguments.  This method automatically finds the
	 * correct resource bundle and key prefix appropriate for the class.  For example, calling
	 * {@code Localization.getString(MainGUI.class, "title")} returns the localized string for the key
	 * {@code "MainGUI.title"}.
	 * 
	 * @param type the class requesting the localized string
	 * @param key the key (minus the class prefix)
	 * @param arguments the formatting arguments
	 * @return the localized string for the given key
	 */
	public static String getString(Class<?> type, String key, Object... arguments) {
		return getLocalization(type).getString(type.getSimpleName() + "." + key, arguments);
	}

}
