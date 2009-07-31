package de.unisb.cs.esee.ui.actions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.revisions.Revision;
import org.eclipse.jface.text.revisions.RevisionInformation;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.ide.ResourceUtil;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;

import de.unisb.cs.esee.core.annotate.EseeAnnotations;
import de.unisb.cs.esee.core.data.LineRevisionInfo;
import de.unisb.cs.esee.core.data.RevisionInfo;
import de.unisb.cs.esee.core.exception.NotVersionedException;
import de.unisb.cs.esee.core.exception.UnsupportedSCMException;
import de.unisb.cs.esee.ui.markers.EseeQuickDiffProvider;
import de.unisb.cs.esee.ui.markers.RevMarker;
import de.unisb.cs.esee.ui.markers.RevisionAnnotation;
import de.unisb.cs.esee.ui.util.EseeUIUtil;


public class AnnotateFileAction {
    private final Shell shell;
    private final IFile file;
    private final boolean openEditorIfClosed;

    private static final QualifiedName MARKED_REV_VERSION_PROP = new QualifiedName("de.unisb.cs.esee.ui", "annotatedCacheVersion");
    private static final String CACHED_REVISION_INFORMATION_KEY = "de.unisb.cs.esee.ui.revInfoKey";

    public AnnotateFileAction(Shell shell, IFile file, boolean openEditorIfClosed) {
	this.shell = shell;
	this.file = file;
	this.openEditorIfClosed = openEditorIfClosed;
    }

    @SuppressWarnings("unchecked")
    public IStatus run(IProgressMonitor monitor) throws NotVersionedException, UnsupportedSCMException, CoreException {
	RevisionInfo revInfo = EseeAnnotations.getRevisionInfo(file, monitor);

	if (revInfo != null && file != null) {
	    Object prop = file.getSessionProperty(AnnotateFileAction.MARKED_REV_VERSION_PROP);

	    Long markedVersion = (Long) prop;
	    RevisionInformation info = null;

	    if (markedVersion == null || revInfo.cacheVersionId != markedVersion.longValue()) {
		LineRevisionInfo[] changes = revInfo.lines;

		info = new RevisionInformation();
		Map<String, RevisionAnnotation> revisions = new HashMap<String, RevisionAnnotation>();

		BufferedReader content = null;

		for (String mId : RevMarker.ID) {
		    file.deleteMarkers(mId, false, IResource.DEPTH_ZERO);
		}
		content = new BufferedReader(new InputStreamReader(file.getContents()));

		int curCharPos = 0;

		for (int line = 0; line < changes.length; ++line) {
		    RevisionAnnotation revision = revisions.get(changes[line].revision);

		    if (revision == null) {
			revisions.put(
				changes[line].revision,
				revision = new RevisionAnnotation(
					changes[line].revision,
					changes[line].author,
					new RGB(255, 0, 0),
					changes[line].stamp
				));
			info.addRevision(revision);
		    }

		    revision.addLine(line + 1);

		    try {
			changes[line].startPos = curCharPos;

			int r;
			while ((r = content.read()) != -1) {
			    char c = (char) r;
			    ++curCharPos;

			    if (c == System.getProperty("line.separator").charAt(0)) {
				break;
			    }
			}

			changes[line].endPos = curCharPos;
		    } catch (IOException e) {
			e.printStackTrace();
		    }
		}

		// finishing line range calculations in each revision
		for (RevisionAnnotation revision : revisions.values()) {
		    revision.addLine(RevisionAnnotation.END_LINE);
		}

		TreeSet<Revision> revs = new TreeSet<Revision>(new Comparator<Revision>() {
		    public int compare(Revision o1, Revision o2) {
			return o2.getDate().compareTo(o1.getDate());
		    }
		});
		revs.addAll(info.getRevisions());

		for (int line = 0; line < changes.length; ++line) {
		    try {
			int p = 0;
			for (Revision rev : revs) {
			    if (changes[line].revision.equals(rev.getId())) {
				IMarker m = file.createMarker(RevMarker.ID[p]);

				m.setAttributes(
					new String[] {IMarker.MESSAGE, IMarker.CHAR_START, IMarker.CHAR_END},
					new Object[] {rev.getHoverInfo(), changes[line].startPos, changes[line].endPos-1}
				);
			    }

			    if (++p == RevMarker.ID.length) {
				break;
			    }
			}
		    } catch (CoreException e) {
			e.printStackTrace();
		    }
		}

		revInfo.setProperty(AnnotateFileAction.CACHED_REVISION_INFORMATION_KEY, info);
		file.setSessionProperty(AnnotateFileAction.MARKED_REV_VERSION_PROP, new Long(revInfo.cacheVersionId));
	    } else {
		info = (RevisionInformation) revInfo.getProperty(AnnotateFileAction.CACHED_REVISION_INFORMATION_KEY);
	    }

	    final RevisionInformation rinfo = info;
	    shell.getDisplay().syncExec(new Runnable() {
		public void run() {
		    try {
			IWorkbenchPage page = EseeUIUtil.getActivePage();
			IEditorPart editor = findEditor(page, file);

			if (editor != null && editor instanceof AbstractDecoratedTextEditor && rinfo != null) {
			    AbstractDecoratedTextEditor textEditor = (AbstractDecoratedTextEditor)editor;
			    textEditor.showRevisionInformation(rinfo, EseeQuickDiffProvider.class.getName());
			}
		    } catch (PartInitException e) {
			e.printStackTrace();
		    }
		}
	    });

	    monitor.done();
	}

	return Status.OK_STATUS;
    }

    protected IEditorPart findEditor(IWorkbenchPage page, IFile resource) throws PartInitException {
	IEditorPart part = ResourceUtil.findEditor(page, resource);

	if (part != null && part instanceof AbstractDecoratedTextEditor) {
	    if (openEditorIfClosed) {
		page.activate(part);
	    }

	    return part;
	}

	return openEditorIfClosed ? IDE.openEditor(page, resource, EditorsUI.DEFAULT_TEXT_EDITOR_ID) : null;
    }
}
