package de.unisb.cs.esee.ui.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import de.unisb.cs.esee.core.Activator;



/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		
		store.setDefault(PreferenceConstants.P_AUTO_MARK_NOT_NEW, true);
		store.setDefault(PreferenceConstants.P_TEXT_HIGHLIGHTING_MODE, PreferenceConstants.HighlightingMode.Unchecked.name());
		store.setDefault(PreferenceConstants.P_TEXT_HIGHLIGHTING_IGNORE, "");
	}

}
