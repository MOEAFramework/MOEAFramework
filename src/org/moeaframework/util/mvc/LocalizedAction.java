package org.moeaframework.util.mvc;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.moeaframework.util.Localization;

public abstract class LocalizedAction extends AbstractAction {

	private static final long serialVersionUID = 4030882078395416151L;

	public LocalizedAction(String id, Localization localization, Object... args) {
		super();
		
		putValue(Action.NAME, localization.getString("action." + id + ".name", args));
		putValue(Action.SHORT_DESCRIPTION, localization.getString("action." + id + ".description", args));
		putValue(Action.SMALL_ICON, localization.getIcon("action." + id + ".icon"));
	}

}