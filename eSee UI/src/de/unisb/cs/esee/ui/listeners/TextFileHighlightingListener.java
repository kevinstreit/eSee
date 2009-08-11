package de.unisb.cs.esee.ui.listeners;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import de.unisb.cs.esee.core.data.RevisionInfoCache;
import de.unisb.cs.esee.core.exception.NotVersionedException;
import de.unisb.cs.esee.core.exception.UnsupportedSCMException;
import de.unisb.cs.esee.ui.ApplicationManager;
import de.unisb.cs.esee.ui.actions.AnnotateFileAction;
import de.unisb.cs.esee.ui.markers.RevMarker;
import de.unisb.cs.esee.ui.preferences.PreferenceConstants;

public class TextFileHighlightingListener {
    private IFile selectedFile;
    private Job updater;

    public TextFileHighlightingListener() {
	ApplicationManager.getDefault().getPreferenceStore()
		.addPropertyChangeListener(new IPropertyChangeListener() {
		    public void propertyChange(PropertyChangeEvent event) {
			if (event.getProperty().equals(
				PreferenceConstants.P_TEXT_HIGHLIGHTING_ACTIVE)) {
			    if (!ApplicationManager.getDefault()
				    .isHighlightingActive()) {
				selectedFile = null;

				if (updater != null) {
				    updater.cancel();
				}

				RevisionInfoCache.INSTANCE.invalidate();

				for (String id : RevMarker.ID) {
				    try {
					ResourcesPlugin
						.getWorkspace()
						.getRoot()
						.deleteMarkers(
							id,
							true,
							IResource.DEPTH_INFINITE);
				    } catch (CoreException e) {
					// ignore
				    }
				}

				try {
				    ResourcesPlugin.getWorkspace().getRoot()
					    .deleteMarkers(
						    RevMarker.ID_NEW_LINE,
						    true,
						    IResource.DEPTH_INFINITE);
				} catch (CoreException e) {
				    // ignore
				}
			    }
			}
		    }
		});

	final IPartListener2 partListener = new IPartListener2() {
	    public void partOpened(IWorkbenchPartReference partRef) {
		setFileToHighlight(partRef);
	    }

	    public void partClosed(IWorkbenchPartReference partRef) {
	    }

	    public void partInputChanged(IWorkbenchPartReference partRef) {
	    }

	    public void partHidden(IWorkbenchPartReference partRef) {
	    }

	    public void partDeactivated(IWorkbenchPartReference partRef) {
	    }

	    public void partVisible(IWorkbenchPartReference partRef) {
	    }

	    public void partBroughtToTop(IWorkbenchPartReference partRef) {
	    }

	    public void partActivated(IWorkbenchPartReference partRef) {
		setFileToHighlight(partRef);
	    }

	    private void setFileToHighlight(IWorkbenchPartReference partRef) {
		if (ApplicationManager.getDefault().isHighlightingActive()) {
		    IWorkbenchPart part = partRef.getPart(false);

		    if (part != null && part instanceof IEditorPart) {
			IFile file = null;
			IEditorPart editor = (IEditorPart) part;
			Object obj = editor.getEditorInput().getAdapter(
				IFile.class);
			if (obj != null && obj instanceof IFile) {
			    file = (IFile) obj;
			}

			if (file != null && file != selectedFile) {
			    selectedFile = file;
			}
		    }
		}
	    }
	};

	final IPageListener pageListener = new IPageListener() {
	    public void pageOpened(IWorkbenchPage page) {
		page.addPartListener(partListener);
	    }

	    public void pageClosed(IWorkbenchPage page) {
		page.removePartListener(partListener);
	    }

	    public void pageActivated(IWorkbenchPage page) {
	    }
	};

	PlatformUI.getWorkbench().addWindowListener(new IWindowListener() {
	    public void windowOpened(IWorkbenchWindow window) {
		window.addPageListener(pageListener);
	    }

	    public void windowDeactivated(IWorkbenchWindow window) {
	    }

	    public void windowClosed(IWorkbenchWindow window) {
		window.removePageListener(pageListener);
	    }

	    public void windowActivated(IWorkbenchWindow window) {
	    }
	});

	// adding listeners to already opened windows and pages
	for (IWorkbenchWindow window : PlatformUI.getWorkbench()
		.getWorkbenchWindows()) {
	    window.addPageListener(pageListener);

	    for (IWorkbenchPage page : window.getPages()) {
		page.addPartListener(partListener);
	    }
	}

	updater = new Job("Annotating Resources") {
	    @Override
	    protected IStatus run(IProgressMonitor monitor) {
		try {
		    while (true) {
			if (selectedFile != null) {
			    new AnnotateFileAction(selectedFile, false)
				    .run(monitor);
			}
			Thread.sleep(10000);
		    }
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
	};

	updater.setSystem(true);
	updater.setUser(false);
	updater.schedule();
    }
}
