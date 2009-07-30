package esee.ui.markers;

import java.util.Date;

import org.eclipse.jface.text.revisions.Revision;
import org.eclipse.jface.text.source.LineRange;
import org.eclipse.swt.graphics.RGB;

public class RevisionAnnotation extends Revision {
    public static final int END_LINE = -1;

    private final String id;
    private final String author;
    private int startLine;
    private int stopLine;
    private final RGB color;
    private final Date time;

    public RevisionAnnotation(String id, String author, RGB color, long time) {
	this.id = id;
	this.author = author;
	this.color = color;
	this.time = new Date(time);
	startLine = stopLine = RevisionAnnotation.END_LINE;
    }

    @Override
    public RGB getColor() {
	return color;
    }

    @Override
    public Date getDate() {
	return time;
    }

    @Override
    public Object getHoverInfo() {
	String info = "Revision: " + id;

	if (author != null) {
	    info += ", Author: " + author;
	}

	if (getDate() != null) {
	    info += ", Date: " + getDate().toString();
	}

	return info;
    }

    @Override
    public String getId() {
	return id;
    }

    @Override
    public String getAuthor() {
	String author = this.author == null ? "unknown" : this.author;
	return author + " ";
    }

    public int getStartLine() {
	return startLine;
    }

    public int getStopLine() {
	return stopLine;
    }

    public void addLine(int line) {
	if (startLine == RevisionAnnotation.END_LINE) {
	    startLine = stopLine = line;
	}
	else if (line == RevisionAnnotation.END_LINE) {
	    addRange(new LineRange(startLine - 1, stopLine - startLine + 1));
	}
	else if (line - stopLine == 1) {
	    stopLine = line;
	}
	else {
	    addRange(new LineRange(startLine - 1, stopLine - startLine + 1));
	    startLine = stopLine = line;
	}
    }
}
