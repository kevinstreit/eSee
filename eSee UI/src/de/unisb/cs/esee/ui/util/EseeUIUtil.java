package de.unisb.cs.esee.ui.util;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class EseeUIUtil {
    public static IWorkbenchPage getActivePage() {
	IWorkbenchWindow window = PlatformUI.getWorkbench()
		.getActiveWorkbenchWindow();
	if (window == null) {
	    IWorkbenchWindow[] ws = PlatformUI.getWorkbench()
		    .getWorkbenchWindows();
	    window = ws != null && ws.length > 0 ? ws[0] : null;
	}
	return window == null ? null : window.getActivePage();
    }
}
