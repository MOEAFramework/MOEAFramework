package org.moeaframework.util.mvc;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.moeaframework.util.Localization;

public abstract class LocalizedAction extends AbstractAction {

	private static final long serialVersionUID = 4030882078395416151L;

	public LocalizedAction(String id, Localization localization) {
		super(id);
		
		putValue(Action.NAME, localization.getString("action." + id + ".name"));
		putValue(Action.SHORT_DESCRIPTION, localization.getString("action." + id + ".description"));
	}

}