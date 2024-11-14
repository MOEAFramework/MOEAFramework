package org.moeaframework.util.mvc;

import java.awt.event.ActionEvent;
import java.util.function.Supplier;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;

import org.moeaframework.util.Localization;

public class PopupAction extends LocalizedAction {
		
	private static final long serialVersionUID = 4516883983249801132L;
	
	private final Supplier<JPopupMenu> menuSupplier;
	
	public PopupAction(String id, Localization localization, Supplier<JPopupMenu> menuSupplier) {
		this(id, localization, new Object[0], menuSupplier);
	}
	
	public PopupAction(String id, Localization localization, Object[] args, Supplier<JPopupMenu> menuSupplier) {
		super(id, localization, args);
		this.menuSupplier = menuSupplier;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		JPopupMenu menu = menuSupplier.get();
		
		if (e.getSource() instanceof JComponent component) {
			menu.show(component, 0, component.getHeight());
		}
	}
	
	public JButton toButton() {
		return new JButton(this);
	}

}