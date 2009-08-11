package de.unisb.cs.esee.ui.markers;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.texteditor.IAnnotationImageProvider;

public class RevisionAnnotationImageProvider implements
	IAnnotationImageProvider {

    public RevisionAnnotationImageProvider() {
	// System.out.println("created");
    }

    public ImageDescriptor getImageDescriptor(String imageDescritporId) {
	// System.out.println("getImageDesc");
	return null;
    }

    public String getImageDescriptorId(Annotation annotation) {
	// System.out.println("getImageDescId");
	return null;
    }

    public Image getManagedImage(Annotation annotation) {
	// System.out.println("getManagedImage");
	return null;
    }

}
