package de.unisb.cs.esee.core.data;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;

import de.unisb.cs.esee.core.annotate.Annotator;
import de.unisb.cs.esee.core.exception.NotVersionedException;


// really enforcing a singleton with an enum
public enum RevisionInfoCache {
    INSTANCE;

    private static long curCacheVersionId = 0;
    private final Map<IResource, RevisionInfo> revInfo = new ConcurrentHashMap<IResource, RevisionInfo>();

    public RevisionInfo getRevisionInfo(IResource resource, Annotator annotator, IProgressMonitor monitor) throws NotVersionedException {
	if (resource != null) {
	    synchronized (resource) {
		RevisionInfo info;

		synchronized (resource) {
		    info = revInfo.get(resource);
		}

		if (info != null) {
		    String curRevID = annotator.getLocalResourceRevisionInfo(resource, monitor).revision;

		    if (curRevID.equals(info.revisionID)) {
			return info;
		    }
		}

		info = annotator.getRevisionInfo(resource, monitor);
		RevisionInfo newInfo = new RevisionInfo(info.lines, info.revisionID, new Long(RevisionInfoCache.curCacheVersionId++));

		synchronized (resource) {
		    revInfo.put(resource, newInfo);
		}

		return newInfo;
	    }
	}

	return null;
    }

    public void invalidateFor(IResource resource) {
	synchronized (revInfo) {
	    revInfo.remove(resource);
	}
    }

    public void invalidate() {
	synchronized (revInfo) {
	    revInfo.clear();
	}
    }

}
