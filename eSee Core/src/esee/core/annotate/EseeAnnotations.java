package esee.core.annotate;


import java.util.Date;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;

import esee.core.SCMSystem;
import esee.core.annotate.Annotator.Location;
import esee.core.data.RevisionInfo;
import esee.core.data.RevisionInfoCache;
import esee.core.exception.BrokenConnectionException;
import esee.core.exception.NotVersionedException;
import esee.core.exception.UnsupportedSCMException;
import esee.core.util.SCMDeterminer;

public class EseeAnnotations {
    public static RevisionInfo getRevisionInfo(IResource resource, IProgressMonitor monitor) throws NotVersionedException, UnsupportedSCMException {
	SCMSystem system = SCMDeterminer.getSystem(resource);
	Annotator annotator = null;

	switch (system) {
	case Subversive :
	    annotator = new SubversiveAnnotator();
	    break;
	default :
	    throw new UnsupportedSCMException();
	}

	return RevisionInfoCache.INSTANCE.getRevisionInfo(resource, annotator, monitor);
    }

    public static Date getResourceDateAttribute(
	    IResource resource,
	    Location location,
	    IProgressMonitor monitor
    ) throws UnsupportedSCMException, BrokenConnectionException, NotVersionedException {
	SCMSystem system = SCMDeterminer.getSystem(resource);
	Annotator annotator = null;

	switch (system) {
	case Subversive :
	    annotator = new SubversiveAnnotator();
	    break;
	default :
	    throw new UnsupportedSCMException();
	}

	switch (location) {
	case Local:
	    return annotator.getLocalRevisionDate(resource, monitor);
	case Repository:
	    return annotator.getRemoteNewestRevisionDate(resource, monitor);
	}

	return null;
    }
}
