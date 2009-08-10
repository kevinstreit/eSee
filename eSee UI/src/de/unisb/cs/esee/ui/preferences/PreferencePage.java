package de.unisb.cs.esee.ui.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.unisb.cs.esee.ui.ApplicationManager;

public class PreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public PreferencePage() {
		super(GRID);
		
		setPreferenceStore(ApplicationManager.getDefault().getPreferenceStore());
		setDescription("eSee user interface preferences");
	}
	
	public void createFieldEditors() {
	    	addField(
			new BooleanFieldEditor(
				PreferenceConstants.P_AUTO_MARK_NOT_NEW,
				"&Automatically mark files as 'not new' on saving them",
				getFieldEditorParent()
			)
		);

		addField(
			new RadioGroupFieldEditor(
				PreferenceConstants.P_TEXT_HIGHLIGHTING_MODE,
				"Highlighting mode for text editors",
				1,
				new String[][] {
					{ "Highlight all unchecked lines", PreferenceConstants.HighlightingMode.Unchecked.name() }, 
					{ "Highlight all lines contained in the last 5 commits", PreferenceConstants.HighlightingMode.Top5.name() }
				}, 
				getFieldEditorParent()
			)
		);
		
		addField(
			new StringFieldEditor(
				PreferenceConstants.P_TEXT_HIGHLIGHTING_IGNORE, 
				"Don't highlight revisions committed by the following users:", 
				getFieldEditorParent()
			)
		);
	}

	public void init(IWorkbench workbench) {
	}
	
}