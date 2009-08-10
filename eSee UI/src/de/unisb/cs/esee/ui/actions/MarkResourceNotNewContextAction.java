package de.unisb.cs.esee.ui.actions;

import java.util.Date;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import de.unisb.cs.esee.core.annotate.EseeAnnotations;
import de.unisb.cs.esee.core.annotate.Annotator.Location;
import de.unisb.cs.esee.core.data.SingleRevisionInfo;
import de.unisb.cs.esee.ui.decorators.NewestResourcesDecorator;
import de.unisb.cs.esee.ui.markers.RevMarker;
import de.unisb.cs.esee.ui.util.IRevisionHighlighter;

public class MarkResourceNotNewContextAction implements IObjectActionDelegate {
    private IResource selectedResource = null;

    public MarkResourceNotNewContextAction() {
	super();
    }

    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	
    }

    public void run(IAction action) {
	markResourceAndChildren(selectedResource);
    }

    private void markResourceAndChildren(IResource res) {
	if (res != null) {
	    try {
		res.accept(new IResourceVisitor() {
		    public boolean visit(IResource resource) throws CoreException {
			try {
			    SingleRevisionInfo revInfo = EseeAnnotations.getResourceRevisionInfo(resource, Location.Local, null);
			    Date curRevDate = new Date(revInfo.stamp);

			    resource.setPersistentProperty(
				IRevisionHighlighter.lastCheckedDateProp,
				Long.toString(curRevDate.getTime())
			    );
			    resource.deleteMarkers(RevMarker.ID_NEW_LINE, false, IResource.DEPTH_ZERO);

			    return true;
			} catch (Exception e) {
			    return false;
			}
		    }
		});
	    } catch (CoreException e) {
		e.printStackTrace();
	    }
	}
	
	PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
	    public void run() {
		PlatformUI.getWorkbench().getDecoratorManager().update(NewestResourcesDecorator.ID);
	    }
	});
    }

    public void selectionChanged(IAction action, ISelection selection) {
	if (selection instanceof IStructuredSelection) {
	    IStructuredSelection sel = (IStructuredSelection) selection;
	    Object obj = sel.getFirstElement();

	    if (obj instanceof IResource) {
		selectedResource = (IResource) obj;
	    } else if (obj instanceof IJavaElement) {
		try {
		    selectedResource = ((IJavaElement) obj).getCorrespondingResource();
		} catch (JavaModelException e) {
		    selectedResource = null;
		}
	    } else {
		selectedResource = null;
	    }
	}
    }

}
