package esee.ui.actions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.PlatformUI;

import esee.core.annotate.EseeAnnotations;
import esee.core.annotate.Annotator.Location;
import esee.ui.ApplicationManager;
import esee.ui.decorators.NewestResourcesDecorator;

public class AnnotateResourcesAction extends Thread {
    public AnnotateResourcesAction() {
	super("Resource Annotation");
    }

    @Override
    public void run() {
	try {
	    // wait for event propagation (10 seconds)
	    Thread.sleep(10 * 1000);

	    while (true) {
		if (ApplicationManager.getDefault().isHighlightingActive()) {
		    AnnotateResourcesAction.annotateWorkspace();
		}

		// TODO: replace by preference value
		Thread.sleep(30 * 1000);
	    }
	} catch (InterruptedException ex) {
	    // ending the "endless" loop
	}
    }

    private static void annotateWorkspace() {
	IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

	final TreeMap<Date, ArrayList<IResource>> revMap = new TreeMap<Date, ArrayList<IResource>>(new Comparator<Date>() {
	    public int compare(Date o1, Date o2) {
		return o2.compareTo(o1);
	    }
	});

	for (IProject project : root.getProjects()) {
	    try {
		revMap.clear();

		project.accept(new IResourceVisitor() {
		    public boolean visit(IResource resource) throws CoreException {
			try {
			    resource.setSessionProperty(NewestResourcesDecorator.decorationTypeProperty, null);

			    Date rDate = EseeAnnotations.getResourceDateAttribute(resource, Location.Local, null);
			    ArrayList<IResource> dateResources = revMap.get(rDate);

			    if (dateResources == null) {
				dateResources = new ArrayList<IResource>();
				revMap.put(rDate, dateResources);
			    }

			    dateResources.add(resource);

			    return true;
			} catch (Exception e) {
			    return false;
			}
		    }
		});

		int top = 0;

		for (Entry<Date, ArrayList<IResource>> entry : revMap.entrySet()) {
		    for (IResource resource : entry.getValue()) {
			AnnotateResourcesAction.markResourceAndParents(resource, top);
		    }

		    if (++top >= 3) {
			break;
		    }
		}
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

    private static void markResourceAndParents(IResource resource, int top) {
	try {
	    if (resource == null) {
		return;
	    }

	    Object decoratorPropObject = resource.getSessionProperty(NewestResourcesDecorator.decorationTypeProperty);
	    if (decoratorPropObject != null) {
		return;
	    }

	    resource.setSessionProperty(
		    NewestResourcesDecorator.decorationTypeProperty,
		    NewestResourcesDecorator.NewsDecorationType.values()[top]
	    );

	    AnnotateResourcesAction.markResourceAndParents(resource.getParent(), top);
	} catch (CoreException e) {}
    }
}
