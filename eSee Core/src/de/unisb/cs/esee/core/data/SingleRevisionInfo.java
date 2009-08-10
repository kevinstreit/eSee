package de.unisb.cs.esee.core.data;

public class SingleRevisionInfo {
    public static final int NONE = -1;

    public final String author;
    public final long stamp;
    public final String revision;
    public int startPos = SingleRevisionInfo.NONE;
    public int endPos = SingleRevisionInfo.NONE;

    public SingleRevisionInfo(long stamp, String revision, String author) {
	this.stamp = stamp;
	this.revision = revision;
	this.author = author;
    }
}
