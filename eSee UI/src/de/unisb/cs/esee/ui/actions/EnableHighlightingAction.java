package de.unisb.cs.esee.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import de.unisb.cs.esee.ui.ApplicationManager;
import de.unisb.cs.esee.ui.preferences.PreferenceConstants;

public class EnableHighlightingAction extends Action {
    public EnableHighlightingAction() {
	super("Enable text highlighting", IAction.AS_CHECK_BOX);
	setToolTipText("Enables or disables text highlighting for SCM managed text files.");

	final IPreferenceStore prefs = ApplicationManager.getDefault()
		.getPreferenceStore();
	setChecked(prefs
		.getBoolean(PreferenceConstants.P_TEXT_HIGHLIGHTING_ACTIVE));

	setImageDescriptor(ApplicationManager
		.getImageDescriptor("/resources/images/activateNewsIcon.png"));

	prefs.addPropertyChangeListener(new IPropertyChangeListener() {
	    public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(
			PreferenceConstants.P_TEXT_HIGHLIGHTING_MODE))
		    setChecked(prefs
			    .getBoolean(PreferenceConstants.P_TEXT_HIGHLIGHTING_ACTIVE));
	    }
	});
    }

    @Override
    public void run() {
	IPreferenceStore prefs = ApplicationManager.getDefault()
		.getPreferenceStore();
	prefs.setValue(PreferenceConstants.P_TEXT_HIGHLIGHTING_ACTIVE,
		isChecked());
    }
}
