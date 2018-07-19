package prv.simple.db.api;

public class FetchException extends RuntimeException{

    private static final long serialVersionUID = -1591473382235653702L;

    public FetchException(Exception e) {
        super(e);
    }

    public FetchException(String msg) {
        super(msg);
    }

}
