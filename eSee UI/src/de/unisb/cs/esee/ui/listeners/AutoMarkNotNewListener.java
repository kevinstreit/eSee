package de.unisb.cs.esee.ui.listeners;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import de.unisb.cs.esee.core.annotate.EseeAnnotations;
import de.unisb.cs.esee.core.annotate.Annotator.Location;
import de.unisb.cs.esee.core.data.SingleRevisionInfo;
import de.unisb.cs.esee.ui.util.IRevisionHighlighter;
import de.unisb.cs.esee.ui.util.StdRevisionHighlighter;

public class AutoMarkNotNewListener implements IPropertyListener {
    private IRevisionHighlighter highlighter = new StdRevisionHighlighter();
    
    public AutoMarkNotNewListener() {
	final IPartListener2 partListener = new IPartListener2() {
	    public void partOpened(IWorkbenchPartReference partRef) {
		IWorkbenchPart part = partRef.getPart(false);
		
		if (part != null && part instanceof IEditorPart) {
		    part.addPropertyListener(AutoMarkNotNewListener.this);
		}
	    }
	    
	    public void partClosed(IWorkbenchPartReference partRef) {
		IWorkbenchPart part = partRef.getPart(false);
		
		if (part != null && part instanceof IEditorPart) {
		    part.removePropertyListener(AutoMarkNotNewListener.this);
		}
	    }
	    
	    public void partInputChanged(IWorkbenchPartReference partRef) {}
	    public void partHidden(IWorkbenchPartReference partRef) {}
	    public void partDeactivated(IWorkbenchPartReference partRef) {}
	    public void partVisible(IWorkbenchPartReference partRef) {}
	    public void partBroughtToTop(IWorkbenchPartReference partRef) {}
	    public void partActivated(IWorkbenchPartReference partRef) {}
	};
	
	final IPageListener pageListener = new IPageListener() {
	    public void pageOpened(IWorkbenchPage page) {
		page.addPartListener(partListener);
	    }
	    
	    public void pageClosed(IWorkbenchPage page) {
		page.removePartListener(partListener);
	    }
	    
	    public void pageActivated(IWorkbenchPage page) {}
	};
	
	PlatformUI.getWorkbench().addWindowListener(new IWindowListener() {    
	    public void windowOpened(IWorkbenchWindow window) {
		window.addPageListener(pageListener);
	    }
	    
	    public void windowDeactivated(IWorkbenchWindow window) {}
	    
	    public void windowClosed(IWorkbenchWindow window) {
		window.removePageListener(pageListener);
	    }
	    
	    public void windowActivated(IWorkbenchWindow window) {}
	});
	
	// adding listeners to already opened windows, pages and editors
	for (IWorkbenchWindow window : PlatformUI.getWorkbench().getWorkbenchWindows()) {
	    window.addPageListener(pageListener);
	    
	    for (IWorkbenchPage page : window.getPages()) {
		page.addPartListener(partListener);
		
		for (IEditorReference editorRef : page.getEditorReferences()) {
		    IEditorPart editor = editorRef.getEditor(false);
		    if (editor != null)
			editor.addPropertyListener(AutoMarkNotNewListener.this);
		}
	    }
	}
    }
    
    public void propertyChanged(Object source, int propId) {
	if (source instanceof IEditorPart && propId == IEditorPart.PROP_DIRTY) {
	    IEditorPart editor = (IEditorPart) source;
	    
	    if (!editor.isDirty()) {
		// state changed from dirty to not dirty
		// => assuming the user willingly changed the file
		
		IFile file = (IFile) editor.getEditorInput().getAdapter(IFile.class);
		if (file != null) {
		    mark(file);
		}
	    }
	}
    }
    
    private void mark(final IResource resource) {
	try {
	    SingleRevisionInfo revInfo = EseeAnnotations.getResourceRevisionInfo(resource, Location.Local, null);
	    Date curRevDate = new Date(revInfo.stamp);
	    resource.setPersistentProperty(IRevisionHighlighter.lastCheckedDateProp, Long.toString(curRevDate.getTime()));
	    checkParents(resource, highlighter);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private void checkParents(IResource resource, final IRevisionHighlighter highlighter) {
	final IResource parent = resource.getParent();
	
	if (parent instanceof IWorkspaceRoot) return;
	
	try {
	    final AtomicBoolean shouldResourceBeMarked = new AtomicBoolean(true);
	    parent.accept(new IResourceVisitor() {   
		public boolean visit(IResource res) throws CoreException {
		    if (res == parent) return true;
        		
		    try {
			SingleRevisionInfo revInfo = EseeAnnotations.getResourceRevisionInfo(res, Location.Local, null);
			Date childRevDate = new Date(revInfo.stamp);
        		
        		if (highlighter.isChangeOfInterest(res, childRevDate, revInfo.author)) {
       			    shouldResourceBeMarked.set(false);
       			}
		    } catch (Exception e) {
       		    	// ignore resource
		    }
       		
		    return false;
       	    	}
	    }, IResource.DEPTH_ONE, false);
	    
	    if (shouldResourceBeMarked.get()) {
		mark(parent);
	    }
	} catch (CoreException e) {
	    // ignore resource
	}
    }
}
