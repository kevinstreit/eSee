package de.unisb.cs.esee.ui.views;

import java.util.ArrayList;
import java.util.Date;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.unisb.cs.esee.core.annotate.EseeAnnotations;
import de.unisb.cs.esee.core.annotate.Annotator.Location;
import de.unisb.cs.esee.core.data.SingleRevisionInfo;
import de.unisb.cs.esee.core.exception.EseeException;
import de.unisb.cs.esee.ui.ApplicationManager;
import de.unisb.cs.esee.ui.util.IRevisionHighlighter;

class NewsViewContentProvider implements IStructuredContentProvider {
    public void inputChanged(Viewer v, Object oldInput, Object newInput) {
    }

    public void dispose() {
    }

    public Object[] getElements(Object parent) {
	final ArrayList<IResource> resourcesOfInterest = new ArrayList<IResource>();
	final IRevisionHighlighter highlighter = ApplicationManager
		.getDefault().getHighlighter();

	try {
	    ResourcesPlugin.getWorkspace().getRoot().accept(
		    new IResourceVisitor() {
			public boolean visit(IResource resource)
				throws CoreException {
			    SingleRevisionInfo revInfo;
			    try {
				if (resource instanceof IFile) {
				    revInfo = EseeAnnotations
					    .getResourceRevisionInfo(resource,
						    Location.Local, null);
				    Date curRevDate = new Date(revInfo.stamp);

				    if (highlighter.isChangeOfInterest(
					    resource, curRevDate,
					    revInfo.author)) {
					resourcesOfInterest.add(resource);
				    }
				}
			    } catch (EseeException e) {
				// ignore resource
			    }

			    return true;
			}
		    });
	} catch (CoreException e) {
	    e.printStackTrace();
	    return new Object[0];
	}

	return resourcesOfInterest.toArray();
    }
}