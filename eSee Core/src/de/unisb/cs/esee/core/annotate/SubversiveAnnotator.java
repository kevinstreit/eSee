package de.unisb.cs.esee.core.annotate;


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

import de.unisb.cs.esee.core.data.RevisionInfo;
import de.unisb.cs.esee.core.data.SingleRevisionInfo;
import de.unisb.cs.esee.core.exception.BrokenConnectionException;
import de.unisb.cs.esee.core.exception.NotVersionedException;


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
		SingleRevisionInfo[] changes = new SingleRevisionInfo[data.length];

		int p = 0;
		for (SVNAnnotationData annotation : data) {
		    changes[p++] = new SingleRevisionInfo(annotation.date, "" + annotation.revision, annotation.author);
		}

		return new RevisionInfo(changes, Long.toString(local.getRevision()), local.getLastCommitDate());
	    } else {
		return new RevisionInfo(new SingleRevisionInfo[0], Long.toString(local.getRevision()), local.getLastCommitDate());
	    }
	} else {
	    throw new NotVersionedException();
	}
    }

    public SingleRevisionInfo getLocalResourceRevisionInfo(IResource resource, IProgressMonitor monitor) throws NotVersionedException {
	ILocalResource local = SVNRemoteStorage.instance().asLocalResource(resource);
	boolean onRepo = IStateFilter.SF_ONREPOSITORY.accept(local);

	if (!onRepo) {
	    throw new NotVersionedException();
	}

	final SVNRevision revision = (local.getRevision() == SVNRevision.INVALID_REVISION_NUMBER ? SVNRevision.HEAD : SVNRevision.fromNumber(local.getRevision()));
	SVNLocalResourceRevision rev = new SVNLocalResourceRevision(local, revision);
	
	SingleRevisionInfo revInfo = new SingleRevisionInfo(
		rev.getTimestamp(), 
		Long.toString(local.getRevision()),
		rev.getAuthor()
	);

	return revInfo;
    }

    public SingleRevisionInfo getRemoteResourceRevisionInfo(IResource resource, IProgressMonitor monitor) throws BrokenConnectionException {
	IRepositoryResource remote = SVNRemoteStorage.instance().asRepositoryResource(resource);
	
	SingleRevisionInfo revInfo;
	try {
	    revInfo = new SingleRevisionInfo(
	    	remote.getInfo().lastChangedDate, 
	    	Long.toString(remote.getRevision()),
	    	remote.getInfo().lastAuthor
	    );
	} catch (SVNConnectorException e) {
	    throw new BrokenConnectionException();
	}

	return revInfo;
    }
}
