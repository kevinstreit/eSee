package de.unisb.cs.esee.ui.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import de.unisb.cs.esee.core.exception.NotVersionedException;
import de.unisb.cs.esee.core.exception.UnsupportedSCMException;

public class ShowNewsContextAction implements IObjectActionDelegate {
    private Shell shell;
    private IFile selectedFile = null;

    public ShowNewsContextAction() {
	super();
    }

    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	shell = targetPart.getSite().getShell();
    }

    public void run(IAction action) {
	Job annotationJob = new Job("Annotating Resource") {
	    @Override
	    protected IStatus run(IProgressMonitor monitor) {
		try {
		    return new AnnotateFileAction(shell, selectedFile, true)
			    .run(monitor);
		} catch (NotVersionedException e) {
		    return Status.CANCEL_STATUS;
		} catch (UnsupportedSCMException e) {
		    return Status.CANCEL_STATUS;
		} catch (CoreException e) {
		    return Status.CANCEL_STATUS;
		}
	    }
	};

	annotationJob.setUser(true);
	annotationJob.schedule();
    }

    public void selectionChanged(IAction action, ISelection selection) {
	selectedFile = null;

	if (selection instanceof IStructuredSelection) {
	    IStructuredSelection sel = (IStructuredSelection) selection;
	    Object obj = sel.getFirstElement();

	    if (obj instanceof IFile) {
		selectedFile = (IFile) obj;
	    } else if (obj instanceof ICompilationUnit) {
		ICompilationUnit compUnit = (ICompilationUnit) obj;
		try {
		    IResource res = compUnit.getCorrespondingResource();

		    if (res instanceof IFile) {
			selectedFile = (IFile) res;
		    }
		} catch (JavaModelException e) {
		    e.printStackTrace();
		}
	    }
	}
    }

}
