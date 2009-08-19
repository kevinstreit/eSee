package de.unisb.cs.esee.ui.util;

import java.util.ArrayList;
import java.util.Date;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import de.unisb.cs.esee.core.annotate.EseeAnnotations;
import de.unisb.cs.esee.ui.ApplicationManager;
import de.unisb.cs.esee.ui.preferences.PreferenceConstants;

public class StdRevisionHighlighter implements IRevisionHighlighter {
    private String[] namesToIgnore;
    private boolean ignoreOwnUser = true;

    public StdRevisionHighlighter() {
	initNamesToIgnore();
	IPreferenceStore prefs = ApplicationManager.getDefault()
		.getPreferenceStore();
	this.ignoreOwnUser = prefs
		.getBoolean(PreferenceConstants.P_TEXT_HIGHLIGHTING_IGNORE_OWN);

	ApplicationManager.getDefault().getPreferenceStore()
		.addPropertyChangeListener(new IPropertyChangeListener() {
		    public void propertyChange(PropertyChangeEvent event) {
			if (event.getProperty().equals(
				PreferenceConstants.P_TEXT_HIGHLIGHTING_IGNORE))
			    initNamesToIgnore();
			else if (event
				.getProperty()
				.equals(
					PreferenceConstants.P_TEXT_HIGHLIGHTING_IGNORE_OWN)) {
			    IPreferenceStore prefs = ApplicationManager
				    .getDefault().getPreferenceStore();
			    StdRevisionHighlighter.this.ignoreOwnUser = prefs
				    .getBoolean(PreferenceConstants.P_TEXT_HIGHLIGHTING_IGNORE_OWN);
			}
		    }
		});
    }

    private void initNamesToIgnore() {
	IPreferenceStore prefs = ApplicationManager.getDefault()
		.getPreferenceStore();
	String usersFilterStr = prefs
		.getString(PreferenceConstants.P_TEXT_HIGHLIGHTING_IGNORE);

	ArrayList<String> namesToIgnore = new ArrayList<String>();

	for (String name : usersFilterStr.split(",")) {
	    String trimmed = name.trim();
	    if (trimmed.length() > 0)
		namesToIgnore.add(trimmed);
	}

	this.namesToIgnore = new String[namesToIgnore.size()];
	namesToIgnore.toArray(this.namesToIgnore);
    }

    public boolean isChangeOfInterest(IResource resource, Date date,
	    String author) {
	try {
	    String resUser = EseeAnnotations.getResourceRepoUsername(resource);
	    if (this.ignoreOwnUser && resUser.equals(author)) {
		return false;
	    }

	    for (String cmpUser : this.namesToIgnore) {
		if (cmpUser.equalsIgnoreCase(author))
		    return false;
	    }

	    String lcdStr = resource.getPersistentProperty(lastCheckedDateProp);

	    if (lcdStr == null) {
		return true;
	    } else {
		long lcdStamp = Long.parseLong(lcdStr);
		Date lcd = new Date(lcdStamp);

		return date.after(lcd);
	    }
	} catch (Exception e) {
	    return false;
	}
    }
}
