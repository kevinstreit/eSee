package de.unisb.cs.esee.ui.views;

import java.util.Date;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import de.unisb.cs.esee.core.annotate.EseeAnnotations;
import de.unisb.cs.esee.core.annotate.Annotator.Location;
import de.unisb.cs.esee.core.data.SingleRevisionInfo;
import de.unisb.cs.esee.ui.util.IRevisionHighlighter;

public class NewsViewSorter extends ViewerSorter {
    private int propertyIndex;
    private static final int DESCENDING = 1;

    private int direction = DESCENDING;

    public NewsViewSorter() {
	this.propertyIndex = 2;
	direction = DESCENDING;
    }

    public void setColumn(int column) {
	if (column == this.propertyIndex) {
	    direction = 1 - direction;
	} else {
	    this.propertyIndex = column;
	    direction = DESCENDING;
	}
    }

    @Override
    public int compare(Viewer viewer, Object e1, Object e2) {
	int result = 0;

	if (e1 instanceof IFile && e2 instanceof IFile) {
	    IFile f1 = (IFile) e1;
	    IFile f2 = (IFile) e2;

	    SingleRevisionInfo revInfo1;
	    try {
		revInfo1 = EseeAnnotations.getResourceRevisionInfo(f1,
			Location.Local, null);
	    } catch (Exception e) {
		return (direction == DESCENDING) ? -1 : 1;
	    }

	    Date curRevDate1 = new Date(revInfo1.stamp);

	    SingleRevisionInfo revInfo2;
	    try {
		revInfo2 = EseeAnnotations.getResourceRevisionInfo(f2,
			Location.Local, null);
	    } catch (Exception e) {
		return (direction == DESCENDING) ? 1 : -1;
	    }

	    Date curRevDate2 = new Date(revInfo2.stamp);

	    switch (propertyIndex) {
	    case 0:
		result = f1.getName().compareTo(f2.getName());
		break;
	    case 1:
		result = revInfo1.author.compareTo(revInfo2.author);
		break;
	    case 2:
		result = curRevDate1.compareTo(curRevDate2);
		break;
	    case 3:
		try {
		    String stamp1 = f1
			    .getPersistentProperty(IRevisionHighlighter.lastCheckedDateProp);
		    String stamp2 = f2
			    .getPersistentProperty(IRevisionHighlighter.lastCheckedDateProp);

		    if (stamp1 == null)
			result = -1;

		    if (stamp2 == null)
			result = 1;

		    long lcdStamp1 = Long.parseLong(stamp1);
		    Date lcd1 = new Date(lcdStamp1);

		    long lcdStamp2 = Long.parseLong(stamp2);
		    Date lcd2 = new Date(lcdStamp2);

		    result = lcd1.compareTo(lcd2);
		} catch (CoreException e) {
		    result = 0;
		}
		break;
	    }

	    return (direction == DESCENDING) ? -result : result;
	} else {
	    return 0;
	}
    }
}
