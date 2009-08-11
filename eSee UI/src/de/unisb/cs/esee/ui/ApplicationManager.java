package de.unisb.cs.esee.ui;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import de.unisb.cs.esee.core.data.RevisionInfoCache;
import de.unisb.cs.esee.ui.listeners.AutoMarkNotNewListener;
import de.unisb.cs.esee.ui.markers.RevMarker;
import de.unisb.cs.esee.ui.preferences.PreferenceConstants;
import de.unisb.cs.esee.ui.preferences.PreferenceConstants.HighlightingMode;

public class ApplicationManager extends AbstractUIPlugin {

    public static final String PLUGIN_ID = "de.unisb.cs.esee.ui";
    private static ApplicationManager plugin;
    private boolean highlightingActive = false;
    private HighlightingMode highlightingMode;

    public ApplicationManager() {
    }

    @Override
    public void start(BundleContext context) throws Exception {
	super.start(context);
	ApplicationManager.plugin = this;
	new AutoMarkNotNewListener();

	resetHighlightingMode();

	getPreferenceStore().addPropertyChangeListener(
		new IPropertyChangeListener() {
		    public void propertyChange(PropertyChangeEvent event) {
			if (event.getProperty().equals(
				PreferenceConstants.P_TEXT_HIGHLIGHTING_MODE))
			    resetHighlightingMode();
		    }
		});
    }

    private void resetHighlightingMode() {
	String mString = ApplicationManager.getDefault().getPreferenceStore()
		.getString(PreferenceConstants.P_TEXT_HIGHLIGHTING_MODE);
	highlightingMode = HighlightingMode.valueOf(mString);

	RevisionInfoCache.INSTANCE.invalidate();

	for (String id : RevMarker.ID) {
	    try {
		ResourcesPlugin.getWorkspace().getRoot().deleteMarkers(id,
			true, IResource.DEPTH_INFINITE);
	    } catch (CoreException e) {
		e.printStackTrace();
	    }
	}

	try {
	    ResourcesPlugin.getWorkspace().getRoot().deleteMarkers(
		    RevMarker.ID_NEW_LINE, true, IResource.DEPTH_INFINITE);
	} catch (CoreException e) {
	    // ignore
	}
    }

    @Override
    public void stop(BundleContext context) throws Exception {
	ApplicationManager.plugin = null;
	super.stop(context);
    }

    public static ApplicationManager getDefault() {
	return ApplicationManager.plugin;
    }

    public static ImageDescriptor getImageDescriptor(String path) {
	return AbstractUIPlugin.imageDescriptorFromPlugin(
		ApplicationManager.PLUGIN_ID, path);
    }

    public boolean isHighlightingActive() {
	return highlightingActive;
    }

    public void setHighlightingActive(boolean highlightingActive) {
	this.highlightingActive = highlightingActive;
    }

    public HighlightingMode getHighlightingMode() {
	return highlightingMode;
    }
}
