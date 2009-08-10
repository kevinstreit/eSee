package de.unisb.cs.esee.core.annotate;


import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;

import de.unisb.cs.esee.core.data.RevisionInfo;
import de.unisb.cs.esee.core.data.SingleRevisionInfo;
import de.unisb.cs.esee.core.exception.BrokenConnectionException;
import de.unisb.cs.esee.core.exception.NotVersionedException;


public interface Annotator {
    public static final String INVALID_REVISION = "INVALID";

    public static enum Location {
	Local,
	Repository
    }

    public RevisionInfo getRevisionInfo(IResource resource, IProgressMonitor monitor) throws NotVersionedException;
    public SingleRevisionInfo getLocalResourceRevisionInfo(IResource resource, IProgressMonitor monitor) throws NotVersionedException;
    public SingleRevisionInfo getRemoteResourceRevisionInfo(IResource resource, IProgressMonitor monitor) throws BrokenConnectionException;
}
