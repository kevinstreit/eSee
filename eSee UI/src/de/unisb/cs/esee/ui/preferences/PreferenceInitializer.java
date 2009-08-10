package de.unisb.cs.esee.ui.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import de.unisb.cs.esee.ui.ApplicationManager;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	public void initializeDefaultPreferences() {
		IPreferenceStore store = ApplicationManager.getDefault().getPreferenceStore();
		
		store.setDefault(PreferenceConstants.P_AUTO_MARK_NOT_NEW, false);
		store.setDefault(PreferenceConstants.P_TEXT_HIGHLIGHTING_MODE, PreferenceConstants.HighlightingMode.Unchecked.name());
		store.setDefault(PreferenceConstants.P_TEXT_HIGHLIGHTING_IGNORE, "");
	}

}
