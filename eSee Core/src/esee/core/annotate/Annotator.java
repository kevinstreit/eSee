package esee.core.annotate;


import java.util.Date;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;

import esee.core.data.RevisionInfo;
import esee.core.exception.BrokenConnectionException;
import esee.core.exception.NotVersionedException;

public interface Annotator {
    public static final String INVALID_REVISION = "INVALID";

    public static enum Location {
	Local,
	Repository
    }

    public RevisionInfo getRevisionInfo(IResource resource, IProgressMonitor monitor) throws NotVersionedException;
    public String getLocalRevisionID(IResource resource, IProgressMonitor monitor) throws NotVersionedException;
    public String getRemoteNewestRevisionID(IResource resource, IProgressMonitor monitor) throws BrokenConnectionException;
    public Date getLocalRevisionDate(IResource resource, IProgressMonitor monitor) throws NotVersionedException;
    public Date getRemoteNewestRevisionDate(IResource resource, IProgressMonitor monitor) throws BrokenConnectionException;
}
