package de.unisb.cs.esee.ui.decorators;

import java.util.Date;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.ui.PlatformUI;

import de.unisb.cs.esee.core.annotate.EseeAnnotations;
import de.unisb.cs.esee.core.annotate.Annotator.Location;
import de.unisb.cs.esee.core.exception.BrokenConnectionException;
import de.unisb.cs.esee.core.exception.NotVersionedException;
import de.unisb.cs.esee.core.exception.UnsupportedSCMException;
import de.unisb.cs.esee.ui.ApplicationManager;

public class NewestResourcesDecorator implements ILightweightLabelDecorator {
    public static final String ID = "de.unisb.cs.esee.ui.newsdecorator";
    
    public static final QualifiedName lastCheckedDateProp = new QualifiedName("de.unisb.cs.esee.ui.decorator", "lastCheckedDate");
    public static final QualifiedName curDateProp = new QualifiedName("de.unisb.cs.esee.ui.decorator", "curDate");
    
    private Font defaultFont;
    private Font highlightFont;
    
    public NewestResourcesDecorator() {
	PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {    
	    public void run() {
		defaultFont = PlatformUI.getWorkbench().getDisplay().getSystemFont();
		FontData defaultData = defaultFont.getFontData()[0];
		highlightFont = new Font(
			defaultFont.getDevice(), 
			new FontData(defaultData.getName(), defaultData.getHeight(), SWT.BOLD)
		);
	    }
	});
    }

    public void decorate(Object element, IDecoration decoration) {
	if (element instanceof IResource) {
	    IResource resource = (IResource) element;
	    try {
		Date curRevDate = EseeAnnotations.getResourceDateAttribute(resource, Location.Local, null);
		
		if (ApplicationManager.getDefault().isHighlightingActive()) {
		    String lcdStr = resource.getPersistentProperty(lastCheckedDateProp);
		    
		    if (lcdStr == null) {
			decoration.setFont(highlightFont);
		    } else {
			long lcdStamp = Long.parseLong(lcdStr);
			Date lcd = new Date(lcdStamp);
			
			if (curRevDate.after(lcd)) {
			    decoration.setFont(highlightFont);
			}
		    }
		} else {
		    decoration.setFont(defaultFont);
		}
	    } catch (CoreException e) {
		// ignore resource
	    } catch (UnsupportedSCMException e) {
		// ignore resource
	    } catch (BrokenConnectionException e) {
		// ignore resource
	    } catch (NotVersionedException e) {
		// ignore resource
	    }
	}
    }

    public void addListener(ILabelProviderListener listener) {}

    public void dispose() {}

    public boolean isLabelProperty(Object element, String property) {
	return false;
    }

    public void removeListener(ILabelProviderListener listener) {}

}
