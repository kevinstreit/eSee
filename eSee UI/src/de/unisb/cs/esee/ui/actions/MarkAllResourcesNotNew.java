package de.unisb.cs.esee.ui.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.action.Action;

import de.unisb.cs.esee.ui.ApplicationManager;
import de.unisb.cs.esee.ui.util.MarkNotNewOperation;

public class MarkAllResourcesNotNew extends Action {
    public MarkAllResourcesNotNew() {
	setText("Mark all resources as not new");
	setToolTipText("Marks ALL resources in the workspace as not new.");

	setImageDescriptor(ApplicationManager
		.getImageDescriptor("/resources/images/markOkActionIcon.png"));
    }

    @Override
    public void run() {
	for (IProject project : ResourcesPlugin.getWorkspace().getRoot()
		.getProjects()) {
	    MarkNotNewOperation.INSTANCE.markResourceNotNew(project, true,
		    false);
	}
    }
}
