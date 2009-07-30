package esee.core.annotate;


import java.util.Date;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.team.svn.core.IStateFilter;
import org.eclipse.team.svn.core.connector.SVNAnnotationData;
import org.eclipse.team.svn.core.connector.SVNConnectorException;
import org.eclipse.team.svn.core.connector.SVNRevision;
import org.eclipse.team.svn.core.history.SVNLocalResourceRevision;
import org.eclipse.team.svn.core.operation.remote.GetResourceAnnotationOperation;
import org.eclipse.team.svn.core.resource.ILocalResource;
import org.eclipse.team.svn.core.resource.IRepositoryResource;
import org.eclipse.team.svn.core.svnstorage.SVNRemoteStorage;

import esee.core.data.LineRevisionInfo;
import esee.core.data.RevisionInfo;
import esee.core.exception.BrokenConnectionException;
import esee.core.exception.NotVersionedException;

public class SubversiveAnnotator implements Annotator {

    public RevisionInfo getRevisionInfo(IResource resource, IProgressMonitor monitor) throws NotVersionedException {
	ILocalResource local = SVNRemoteStorage.instance().asLocalResource(resource);
	boolean onRepo = IStateFilter.SF_ONREPOSITORY.accept(local);
	final SVNRevision revision = (local.getRevision() == SVNRevision.INVALID_REVISION_NUMBER ? SVNRevision.HEAD : SVNRevision.fromNumber(local.getRevision()));

	//TODO: handle changes/diffs
	if (onRepo) {
	    final IRepositoryResource remote = SVNRemoteStorage.instance().asRepositoryResource(resource);
	    remote.setSelectedRevision(revision);

	    GetResourceAnnotationOperation annotateOp = new GetResourceAnnotationOperation(remote);
	    annotateOp.run(monitor);

	    SVNAnnotationData[] data = annotateOp.getAnnotatedLines();

	    if (data != null) {
		LineRevisionInfo[] changes = new LineRevisionInfo[data.length];

		int p = 0;
		for (SVNAnnotationData annotation : data) {
		    changes[p++] = new LineRevisionInfo(annotation.date, "" + annotation.revision, annotation.author);
		}

		return new RevisionInfo(changes, "" + local.getRevision());
	    } else {
		return new RevisionInfo(new LineRevisionInfo[0], "" + local.getRevision());
	    }
	} else {
	    throw new NotVersionedException();
	}
    }

    public String getLocalRevisionID(IResource resource, IProgressMonitor monitor) throws NotVersionedException {
	ILocalResource local = SVNRemoteStorage.instance().asLocalResource(resource);
	boolean onRepo = IStateFilter.SF_ONREPOSITORY.accept(local);
	if (!onRepo) {
	    throw new NotVersionedException();
	}

	return (local.getRevision() == SVNRevision.INVALID_REVISION_NUMBER ? Annotator.INVALID_REVISION : "" + local.getRevision());
    }

    public String getRemoteNewestRevisionID(IResource resource, IProgressMonitor monitor) throws BrokenConnectionException {
	IRepositoryResource remote = SVNRemoteStorage.instance().asRepositoryResource(resource);
	try {
	    return (remote.getRevision() == SVNRevision.INVALID_REVISION_NUMBER ? Annotator.INVALID_REVISION : "" + remote.getRevision());
	} catch (SVNConnectorException e) {
	    throw new BrokenConnectionException(e.getMessage());
	}
    }

    public Date getLocalRevisionDate(IResource resource, IProgressMonitor monitor) throws NotVersionedException {
	ILocalResource local = SVNRemoteStorage.instance().asLocalResource(resource);
	boolean onRepo = IStateFilter.SF_ONREPOSITORY.accept(local);

	if (!onRepo) {
	    throw new NotVersionedException();
	}

	final SVNRevision revision = (local.getRevision() == SVNRevision.INVALID_REVISION_NUMBER ? SVNRevision.HEAD : SVNRevision.fromNumber(local.getRevision()));
	SVNLocalResourceRevision rev = new SVNLocalResourceRevision(local, revision);

	return new Date(rev.getTimestamp());
    }

    public Date getRemoteNewestRevisionDate(IResource resource, IProgressMonitor monitor) throws BrokenConnectionException {
	IRepositoryResource remote = SVNRemoteStorage.instance().asRepositoryResource(resource);
	return new Date(remote.getInfo().lastChangedDate);
    }
}
