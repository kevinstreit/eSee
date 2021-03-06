package de.unisb.cs.esee.ui.actions;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import de.unisb.cs.esee.ui.util.MarkNotNewOperation;

public class MarkResourceNotNewObjectActionDelegate implements
	IObjectActionDelegate {
    private IStructuredSelection selection = null;

    public MarkResourceNotNewObjectActionDelegate() {
	super();
    }

    public void setActivePart(IAction action, IWorkbenchPart targetPart) {

    }

    public void run(IAction action) {
	if (this.selection != null) {
	    for (Object obj : this.selection.toArray()) {
		IResource selectedResource = null;

		if (obj instanceof IResource)
		    selectedResource = (IResource) obj;
		else if (obj instanceof IJavaElement) {
		    try {
			selectedResource = ((IJavaElement) obj)
				.getCorrespondingResource();
		    } catch (JavaModelException e) {
			selectedResource = null;
		    }
		}

		if (selectedResource != null)
		    MarkNotNewOperation.INSTANCE.markResourceNotNew(
			    selectedResource, true, true);
	    }
	}
    }

    public void selectionChanged(IAction action, ISelection selection) {
	if (selection instanceof IStructuredSelection) {
	    this.selection = (IStructuredSelection) selection;
	} else
	    this.selection = null;
    }

}
