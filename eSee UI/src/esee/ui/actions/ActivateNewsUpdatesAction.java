package esee.ui.actions;

import esee.core.data.RevisionInfoCache;
import esee.core.exception.NotVersionedException;
import esee.core.exception.UnsupportedSCMException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

import esee.ui.ApplicationManager;
import esee.ui.decorators.NewestResourcesDecorator;
import esee.ui.markers.RevMarker;

public class ActivateNewsUpdatesAction implements IWorkbenchWindowActionDelegate {
    private IWorkbenchWindow window = null;
    private IFile selectedFile;
    private Job updater;

    public void dispose() {

    }

    public void init(IWorkbenchWindow window) {
	this.window = window;
    }

    public void run(IAction action) {
	ApplicationManager.getDefault().setHighlightingActive(action.isChecked());

	if (!ApplicationManager.getDefault().isHighlightingActive()) {
	    selectedFile = null;

	    RevisionInfoCache.INSTANCE.invalidate();

	    for (String id : RevMarker.ID) {
		try {
		    ResourcesPlugin.getWorkspace().getRoot().deleteMarkers(
			    id,
			    true,
			    IResource.DEPTH_INFINITE
		    );

		    PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
			    PlatformUI.getWorkbench().getDecoratorManager().update(NewestResourcesDecorator.ID);
			}
		    });
		} catch (CoreException e) {
		    e.printStackTrace();
		}
	    }
	}
    }

    public void selectionChanged(IAction action, ISelection selection) {
	if (ApplicationManager.getDefault().isHighlightingActive()) {
	    IFile file = null;

	    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

	    if (page != null) {
		IEditorPart editor = page.getActiveEditor();

		if (editor != null) {
		    Object obj = editor.getEditorInput().getAdapter(IFile.class);
		    if (obj != null && obj instanceof IFile) {
			file = (IFile) obj;
		    }
		}
	    }

	    if (file != null && file != selectedFile) {
		if (updater != null) {
		    updater.cancel();
		}

		selectedFile = file;

		updater = new Job("Annotating Resource '" + selectedFile.getName() + "'") {
		    @Override
		    protected IStatus run(IProgressMonitor monitor) {
			while (true) {
			    try {
				new AnnotateFileAction(window.getShell(), selectedFile, false).run(monitor);
				//TODO: replace by preference
				Thread.sleep(60000);
			    } catch (InterruptedException ex) {
				return Status.CANCEL_STATUS;
			    } catch (NotVersionedException e) {
				return Status.CANCEL_STATUS;
			    } catch (UnsupportedSCMException e) {
				return Status.CANCEL_STATUS;
			    } catch (CoreException e) {
				return Status.CANCEL_STATUS;
			    }
			}
		    }
		};

		updater.setSystem(true);
		updater.setUser(false);
		updater.schedule();
	    }
	}
    }

}
