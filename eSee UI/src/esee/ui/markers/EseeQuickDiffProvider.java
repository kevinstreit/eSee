package esee.ui.markers;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.quickdiff.IQuickDiffReferenceProvider;

public class EseeQuickDiffProvider implements IQuickDiffReferenceProvider {
    protected String id;

    public void dispose() {
	System.out.println("dispose");
    }

    public String getId() {
	System.out.println("getId");
	return id;
    }

    public IDocument getReference(IProgressMonitor monitor)
    throws CoreException {
	System.out.println("getReference");
	return null;
    }

    public boolean isEnabled() {
	System.out.println("isEnabled");
	return true;
    }

    public void setActiveEditor(ITextEditor editor) {
	System.out.println("setActiveEditor");
    }

    public void setId(String id) {
	System.out.println("setId");
	this.id = id;
    }

}
