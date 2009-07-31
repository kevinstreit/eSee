package de.unisb.cs.esee.ui.decorators;


import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.PlatformUI;

import de.unisb.cs.esee.ui.ApplicationManager;


public class NewestResourcesDecorator implements ILightweightLabelDecorator {
    public static final String ID = "de.unisb.cs.esee.ui.newsdecorator";
    public static final QualifiedName decorationTypeProperty = new QualifiedName("de.unisb.cs.esee.ui.decorator", "newest");

    public static enum NewsDecorationType {
	Newest,
	Second,
	Third
    }

    private static final Color white = new Color(PlatformUI.getWorkbench().getDisplay(), new RGB(255, 255, 255));

    private static final Color[] bgColors = new Color[] {
	new Color(PlatformUI.getWorkbench().getDisplay(), new RGB(216,170,117)),
	new Color(PlatformUI.getWorkbench().getDisplay(), new RGB(223,185,141)),
	new Color(PlatformUI.getWorkbench().getDisplay(), new RGB(234,204,168))
    };

    public void decorate(Object element, IDecoration decoration) {
	if (element instanceof IResource) {
	    IResource resource = (IResource) element;
	    try {
		NewsDecorationType type = (NewsDecorationType) resource.getSessionProperty(NewestResourcesDecorator.decorationTypeProperty);

		if (ApplicationManager.getDefault().isHighlightingActive() && type != null) {
		    decoration.setBackgroundColor(NewestResourcesDecorator.bgColors[type.ordinal()]);
		} else {
		    decoration.setBackgroundColor(NewestResourcesDecorator.white);
		}
	    } catch (CoreException e) {
		// ignore resource
	    }
	}
    }

    public void addListener(ILabelProviderListener listener) {}

    public void dispose() {}

    public boolean isLabelProperty(Object element, String property) {
	return false;
    }

    public void removeListener(ILabelProviderListener listener) {}

}
