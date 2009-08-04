package de.unisb.cs.esee.ui;


import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


public class ApplicationManager extends AbstractUIPlugin {

    public static final String PLUGIN_ID = "de.unisb.cs.esee.ui";
    private static ApplicationManager plugin;
    private boolean highlightingActive = false;

    public ApplicationManager() {
    }

    @Override
    public void start(BundleContext context) throws Exception {
	super.start(context);
	ApplicationManager.plugin = this;
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
	return AbstractUIPlugin.imageDescriptorFromPlugin(ApplicationManager.PLUGIN_ID, path);
    }

    public boolean isHighlightingActive() {
	return highlightingActive;
    }

    public void setHighlightingActive(boolean highlightingActive) {
	this.highlightingActive = highlightingActive;
    }
}
