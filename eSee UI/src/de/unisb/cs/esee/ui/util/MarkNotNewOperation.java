package de.unisb.cs.esee.ui.util;

import java.util.Date;
import java.util.Observable;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.PlatformUI;

import de.unisb.cs.esee.core.annotate.EseeAnnotations;
import de.unisb.cs.esee.core.annotate.Annotator.Location;
import de.unisb.cs.esee.core.data.SingleRevisionInfo;
import de.unisb.cs.esee.core.exception.EseeException;
import de.unisb.cs.esee.ui.ApplicationManager;
import de.unisb.cs.esee.ui.decorators.NewestResourcesDecorator;
import de.unisb.cs.esee.ui.markers.RevMarker;

public class MarkNotNewOperation extends Observable {
    public static final MarkNotNewOperation INSTANCE = new MarkNotNewOperation();

    public void markResourceNotNew(IResource resource, boolean recursive,
	    boolean updateParents) {
	try {
	    if (resource != null) {

		if (recursive) {
		    resource.accept(new IResourceVisitor() {
			public boolean visit(IResource resource)
				throws CoreException {
			    try {
				SingleRevisionInfo revInfo = EseeAnnotations
					.getResourceRevisionInfo(resource,
						Location.Local, null);
				Date curRevDate = new Date(revInfo.stamp);

				resource
					.setPersistentProperty(
						IRevisionHighlighter.lastCheckedDateProp,
						Long.toString(curRevDate
							.getTime()));
				resource.deleteMarkers(RevMarker.ID_NEW_LINE,
					false, IResource.DEPTH_ZERO);

				return true;
			    } catch (Exception e) {
				return false;
			    }
			}
		    });
		} else {
		    try {
			SingleRevisionInfo revInfo = EseeAnnotations
				.getResourceRevisionInfo(resource,
					Location.Local, null);
			Date curRevDate = new Date(revInfo.stamp);

			resource.setPersistentProperty(
				IRevisionHighlighter.lastCheckedDateProp, Long
					.toString(curRevDate.getTime()));
			resource.deleteMarkers(RevMarker.ID_NEW_LINE, false,
				IResource.DEPTH_ZERO);
		    } catch (EseeException e) {
			// ignore
		    }
		}

		if (updateParents) {
		    checkParents(resource, ApplicationManager.getDefault()
			    .getHighlighter());
		}

		setChanged();
		notifyObservers(resource);
	    }
	} catch (CoreException e) {
	    e.printStackTrace();
	}

	PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
	    public void run() {
		PlatformUI.getWorkbench().getDecoratorManager().update(
			NewestResourcesDecorator.ID);
	    }
	});
    }

    private void checkParents(IResource resource,
	    final IRevisionHighlighter highlighter) {
	final IResource parent = resource.getParent();

	if (parent instanceof IWorkspaceRoot)
	    return;

	try {
	    final AtomicBoolean shouldResourceBeMarked = new AtomicBoolean(true);
	    parent.accept(new IResourceVisitor() {
		public boolean visit(IResource res) throws CoreException {
		    if (res == parent)
			return true;

		    try {
			SingleRevisionInfo revInfo = EseeAnnotations
				.getResourceRevisionInfo(res, Location.Local,
					null);
			Date childRevDate = new Date(revInfo.stamp);

			if (highlighter.isChangeOfInterest(res, childRevDate,
				revInfo.author)) {
			    shouldResourceBeMarked.set(false);
			}
		    } catch (Exception e) {
			// ignore resource
		    }

		    return false;
		}
	    }, IResource.DEPTH_ONE, false);

	    if (shouldResourceBeMarked.get()) {
		markResourceNotNew(parent, false, true);
	    }
	} catch (CoreException e) {
	    // ignore resource
	}
    }
}
