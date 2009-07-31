package de.unisb.cs.esee.core.exception;

public class BrokenConnectionException extends EseeException {

    private static final long serialVersionUID = 1882993670334465642L;

    public BrokenConnectionException() {
	super();
    }

    public BrokenConnectionException(String msg) {
	super(msg);
    }

}
