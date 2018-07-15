package prv.simple.db.api;

public class DBException extends Exception {

    private static final long serialVersionUID = 6305686977771252081L;

    public DBException(Exception e) {
        super(e);
    }

    public DBException(String msg) {
        super(msg);
    }

}
