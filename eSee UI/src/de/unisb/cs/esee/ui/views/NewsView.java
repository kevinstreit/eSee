package de.unisb.cs.esee.ui.views;

import java.util.Observable;
import java.util.Observer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.ide.ResourceUtil;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;

import de.unisb.cs.esee.ui.actions.EnableHighlightingAction;
import de.unisb.cs.esee.ui.actions.MarkAllResourcesNotNew;
import de.unisb.cs.esee.ui.actions.OpenEseeUIPreferencePageAction;
import de.unisb.cs.esee.ui.util.EseeUIUtil;
import de.unisb.cs.esee.ui.util.MarkNotNewOperation;

public class NewsView extends ViewPart implements Observer,
	IResourceChangeListener {

    public static final String ID = "de.unisb.cs.esee.ui.views.NewsView";

    private TableViewer viewer;

    private Action markAllAsNotNewAction;
    private Action openPreferencesAction;
    private Action openEditorAction;
    private Action enableTetxHighlightingAction;

    class NameSorter extends ViewerSorter {
    }

    public NewsView() {
    }

    public void createPartControl(Composite parent) {
	viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
		| SWT.V_SCROLL);
	final NewsViewSorter sorter = new NewsViewSorter();

	String[] titles = { "File", "Project", "Last Author",
		"Last Change Date", "Last Checked Date" };
	int[] bounds = { 250, 150, 120, 200, 200 };

	for (int i = 0; i < titles.length; i++) {
	    final TableViewerColumn column = new TableViewerColumn(viewer,
		    SWT.NONE);
	    column.getColumn().setText(titles[i]);
	    column.getColumn().setWidth(bounds[i]);
	    column.getColumn().setResizable(true);
	    column.getColumn().setMoveable(true);

	    final int index = i;
	    column.getColumn().addSelectionListener(new SelectionAdapter() {
		public void widgetSelected(SelectionEvent e) {
		    sorter.setColumn(index);
		    int dir = viewer.getTable().getSortDirection();
		    if (viewer.getTable().getSortColumn() == column.getColumn()) {
			dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
		    } else {
			dir = SWT.DOWN;
		    }
		    viewer.getTable().setSortDirection(dir);
		    viewer.getTable().setSortColumn(column.getColumn());
		    viewer.refresh();
		}
	    });
	}

	Table table = viewer.getTable();
	table.setHeaderVisible(true);
	table.setLinesVisible(true);
	table.setSortColumn(table.getColumn(2));
	table.setSortDirection(SWT.DOWN);

	viewer.setSorter(sorter);
	viewer.setContentProvider(new NewsViewContentProvider());
	viewer.setLabelProvider(new NewsViewLabelProvider());
	viewer.setInput(ResourcesPlugin.getWorkspace().getRoot());

	MarkNotNewOperation.INSTANCE.addObserver(this);
	ResourcesPlugin.getWorkspace().addResourceChangeListener(this);

	makeActions();
	hookDoubleClickAction();
	contributeToActionBars();
	hookContextMenu();
    }

    private void hookContextMenu() {
	MenuManager menuMgr = new MenuManager("#eSeeNewsViewPopup");
	menuMgr.setRemoveAllWhenShown(true);
	Menu menu = menuMgr.createContextMenu(viewer.getControl());
	viewer.getControl().setMenu(menu);
	getSite().registerContextMenu(menuMgr, viewer);
    }

    private void contributeToActionBars() {
	IActionBars bars = getViewSite().getActionBars();
	fillLocalToolBar(bars.getToolBarManager());
    }

    private void fillLocalToolBar(IToolBarManager manager) {
	manager.add(enableTetxHighlightingAction);
	manager.add(markAllAsNotNewAction);
	manager.add(new Separator());
	manager.add(openPreferencesAction);
    }

    private void makeActions() {
	markAllAsNotNewAction = new MarkAllResourcesNotNew();
	openPreferencesAction = new OpenEseeUIPreferencePageAction();
	enableTetxHighlightingAction = new EnableHighlightingAction();

	openEditorAction = new Action() {
	    public void run() {
		ISelection selection = viewer.getSelection();
		Object obj = ((IStructuredSelection) selection)
			.getFirstElement();

		if (obj instanceof IFile) {
		    IFile file = (IFile) obj;
		    try {
			openEditor(file);
		    } catch (PartInitException e) {
			// ignore
		    }
		}
	    }
	};
    }

    private void hookDoubleClickAction() {
	viewer.addDoubleClickListener(new IDoubleClickListener() {
	    public void doubleClick(DoubleClickEvent event) {
		openEditorAction.run();
	    }
	});
    }

    public void setFocus() {
	viewer.getControl().setFocus();
    }

    public void resourceChanged(IResourceChangeEvent event) {
	IResourceDelta delta = event.getDelta();

	try {
	    delta.accept(new IResourceDeltaVisitor() {
		public boolean visit(IResourceDelta delta) throws CoreException {
		    if ((delta.getFlags() & IResourceDelta.CONTENT) != 0
			    && delta.getResource() != null
			    && delta.getResource() instanceof IFile) {
			update(null, delta.getResource());
		    }

		    return true;
		}
	    });
	} catch (CoreException e) {
	    // ignore
	}
    }

    protected IEditorPart openEditor(IFile resource) throws PartInitException {
	IWorkbenchPage page = EseeUIUtil.getActivePage();
	IEditorPart part = ResourceUtil.findEditor(page, resource);

	if (part != null && part instanceof AbstractDecoratedTextEditor) {
	    page.activate(part);
	    return part;
	}

	return IDE.openEditor(page, resource, EditorsUI.DEFAULT_TEXT_EDITOR_ID);
    }

    public void update(Observable o, Object arg) {
	// todo: refresh only neccessary resources

	PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
	    public void run() {
		NewsView.this.viewer.refresh(true);
	    }
	});
    }

    @Override
    public void dispose() {
	ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
	MarkNotNewOperation.INSTANCE.deleteObserver(this);
    }
}