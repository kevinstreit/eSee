package esee.core.data;

import java.util.HashMap;

public class RevisionInfo {
    public final static long UNCACHED = -1;

    public final long cacheVersionId;
    public final LineRevisionInfo[] lines;

    public final String revisionID;

    private final HashMap<String, Object> properties = new HashMap<String, Object>();

    public RevisionInfo(LineRevisionInfo[] lineInfos, String revID) {
	this(lineInfos, revID, RevisionInfo.UNCACHED);
    }

    public RevisionInfo(LineRevisionInfo[] info, String revID, long cacheVersionId) {
	lines = info;
	this.cacheVersionId = cacheVersionId;
	revisionID = revID;
    }

    public void setProperty(String key, Object value) {
	properties.put(key, value);
    }

    public Object getProperty(String key) {
	return properties.get(key);
    }
}
