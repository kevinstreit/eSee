package de.unisb.cs.esee.core.data;

import java.util.HashMap;

public class RevisionInfo {
    public final static long UNCACHED = -1;

    public final long cacheVersionId;
    public final SingleRevisionInfo[] lines;
    public final long lastCommitDate;

    public final String revisionID;

    private final HashMap<String, Object> properties = new HashMap<String, Object>();

    public RevisionInfo(SingleRevisionInfo[] lineInfos, String revID, long lastCommitDate) {
	this(lineInfos, revID, RevisionInfo.UNCACHED, lastCommitDate);
    }

    public RevisionInfo(SingleRevisionInfo[] info, String revID, long lastCommitDate, long cacheVersionId) {
	lines = info;
	this.cacheVersionId = cacheVersionId;
	revisionID = revID;
	this.lastCommitDate = lastCommitDate;
    }

    public void setProperty(String key, Object value) {
	properties.put(key, value);
    }

    public Object getProperty(String key) {
	return properties.get(key);
    }
}
