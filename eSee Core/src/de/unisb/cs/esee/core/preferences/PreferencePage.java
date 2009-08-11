package de.unisb.cs.esee.core.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.unisb.cs.esee.core.Activator;

public class PreferencePage extends FieldEditorPreferencePage implements
	IWorkbenchPreferencePage {

    public PreferencePage() {
	super(GRID);
	setPreferenceStore(Activator.getDefault().getPreferenceStore());
	setDescription("General eSee preferences.");
    }

    public void createFieldEditors() {
    }

    public void init(IWorkbench workbench) {
    }

}