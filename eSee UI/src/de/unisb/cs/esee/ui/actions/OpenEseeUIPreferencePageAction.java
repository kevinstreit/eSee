package de.unisb.cs.esee.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.ui.PlatformUI;

import de.unisb.cs.esee.ui.ApplicationManager;
import de.unisb.cs.esee.ui.preferences.PreferencePage;

public class OpenEseeUIPreferencePageAction extends Action {
    public OpenEseeUIPreferencePageAction() {
	setText("Open eSee UI preferences");
	setToolTipText("Opens the eSee UI preference page.");

	setImageDescriptor(ApplicationManager
		.getImageDescriptor("/resources/images/preferencesIcon.png"));
    }

    @Override
    public void run() {
	PreferenceDialog dialog = new PreferenceDialog(PlatformUI
		.getWorkbench().getActiveWorkbenchWindow().getShell(),
		PlatformUI.getWorkbench().getPreferenceManager());

	dialog.setSelectedNode(PreferencePage.ID);
	dialog.create();
	dialog.open();
    }
}
