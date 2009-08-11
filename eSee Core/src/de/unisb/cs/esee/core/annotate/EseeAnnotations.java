package de.unisb.cs.esee.core.annotate;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;

import de.unisb.cs.esee.core.SCMSystem;
import de.unisb.cs.esee.core.annotate.Annotator.Location;
import de.unisb.cs.esee.core.data.RevisionInfo;
import de.unisb.cs.esee.core.data.RevisionInfoCache;
import de.unisb.cs.esee.core.data.SingleRevisionInfo;
import de.unisb.cs.esee.core.exception.BrokenConnectionException;
import de.unisb.cs.esee.core.exception.NotVersionedException;
import de.unisb.cs.esee.core.exception.UnsupportedSCMException;
import de.unisb.cs.esee.core.util.SCMDeterminer;

public class EseeAnnotations {
    public static RevisionInfo getRevisionInfo(IResource resource,
	    IProgressMonitor monitor) throws NotVersionedException,
	    UnsupportedSCMException {
	SCMSystem system = SCMDeterminer.getSystem(resource);
	Annotator annotator = null;

	switch (system) {
	case Subversive:
	    annotator = new SubversiveAnnotator();
	    break;
	default:
	    throw new UnsupportedSCMException();
	}

	return RevisionInfoCache.INSTANCE.getRevisionInfo(resource, annotator,
		monitor);
    }

    public static SingleRevisionInfo getResourceRevisionInfo(
	    IResource resource, Location location, IProgressMonitor monitor)
	    throws UnsupportedSCMException, BrokenConnectionException,
	    NotVersionedException {
	SCMSystem system = SCMDeterminer.getSystem(resource);
	Annotator annotator = null;

	switch (system) {
	case Subversive:
	    annotator = new SubversiveAnnotator();
	    break;
	default:
	    throw new UnsupportedSCMException();
	}

	switch (location) {
	case Local:
	    return annotator.getLocalResourceRevisionInfo(resource, monitor);
	case Repository:
	    return annotator.getRemoteResourceRevisionInfo(resource, monitor);
	}

	return null;
    }
}
