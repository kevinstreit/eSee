package de.unisb.cs.esee.core.data;

public class LineRevisionInfo {
    public static final int NONE = -1;

    public final String author;
    public final long stamp;
    public final String revision;
    public int startPos = LineRevisionInfo.NONE;
    public int endPos = LineRevisionInfo.NONE;

    public LineRevisionInfo(long stamp, String revision, String author) {
	this.stamp = stamp;
	this.revision = revision;
	this.author = author;
    }
}
