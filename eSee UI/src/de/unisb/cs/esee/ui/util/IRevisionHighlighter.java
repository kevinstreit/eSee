package de.unisb.cs.esee.ui.util;

import java.util.Date;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.QualifiedName;

public interface IRevisionHighlighter {
    public static final QualifiedName lastCheckedDateProp = new QualifiedName(
	    "de.unisb.cs.esee.ui.decorator", "lastCheckedDate");
    public static final QualifiedName curDateProp = new QualifiedName(
	    "de.unisb.cs.esee.ui.decorator", "curDate");

    public boolean isChangeOfInterest(IResource resource, Date date,
	    String author);
}
