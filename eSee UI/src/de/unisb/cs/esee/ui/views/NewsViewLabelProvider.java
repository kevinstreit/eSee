package de.unisb.cs.esee.ui.views;

import java.util.Date;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.model.IWorkbenchAdapter;

import de.unisb.cs.esee.core.annotate.EseeAnnotations;
import de.unisb.cs.esee.core.annotate.Annotator.Location;
import de.unisb.cs.esee.core.data.SingleRevisionInfo;
import de.unisb.cs.esee.core.exception.EseeException;
import de.unisb.cs.esee.ui.util.IRevisionHighlighter;

class NewsViewLabelProvider extends LabelProvider implements
	ITableLabelProvider {
    private ResourceManager resourceManager;

    public String getColumnText(Object obj, int index) {
	if (obj instanceof IResource) {
	    IResource resource = (IResource) obj;

	    try {
		SingleRevisionInfo revInfo = EseeAnnotations
			.getResourceRevisionInfo(resource, Location.Local, null);
		Date curRevDate = new Date(revInfo.stamp);

		switch (index) {
		case 0:
		    return resource.getName();
		case 1:
		    return resource.getProject() != null ? resource
			    .getProject().getName() : "none";
		case 2:
		    return revInfo.author;
		case 3:
		    return curRevDate.toString();
		case 4:
		    try {
			String stamp = resource
				.getPersistentProperty(IRevisionHighlighter.lastCheckedDateProp);

			if (stamp == null)
			    return "never";

			long lcdStamp = Long.parseLong(stamp);
			Date lcd = new Date(lcdStamp);

			return lcd.toString();
		    } catch (CoreException e) {
			return "unknown";
		    }
		}
	    } catch (EseeException e) {
		return "Error: " + e.getMessage();
	    }
	}

	return getText(obj);
    }

    public Image getColumnImage(Object obj, int index) {
	if (obj instanceof IResource && index == 0) {
	    IResource resource = (IResource) obj;

	    IWorkbenchAdapter adapter = getAdapter(resource);
	    if (adapter == null) {
		return null;
	    }
	    ImageDescriptor descriptor = adapter.getImageDescriptor(resource);
	    if (descriptor == null) {
		return null;
	    }

	    return (Image) getResourceManager().get(descriptor);
	}

	return null;
    }

    private ResourceManager getResourceManager() {
	if (resourceManager == null) {
	    resourceManager = new LocalResourceManager(JFaceResources
		    .getResources());
	}

	return resourceManager;
    }

    protected final IWorkbenchAdapter getAdapter(Object o) {
	return (IWorkbenchAdapter) getAdapter(o, IWorkbenchAdapter.class);
    }

    public Object getAdapter(Object sourceObject, Class<?> adapterType) {
	Assert.isNotNull(adapterType);
	if (sourceObject == null) {
	    return null;
	}
	if (adapterType.isInstance(sourceObject)) {
	    return sourceObject;
	}

	if (sourceObject instanceof IAdaptable) {
	    IAdaptable adaptable = (IAdaptable) sourceObject;

	    Object result = adaptable.getAdapter(adapterType);
	    if (result != null) {
		// Sanity-check
		Assert.isTrue(adapterType.isInstance(result));
		return result;
	    }
	}

	if (!(sourceObject instanceof PlatformObject)) {
	    Object result = Platform.getAdapterManager().getAdapter(
		    sourceObject, adapterType);
	    if (result != null) {
		return result;
	    }
	}

	return null;
    }
}