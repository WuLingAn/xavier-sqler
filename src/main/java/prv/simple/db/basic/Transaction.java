package prv.simple.db.basic;

import prv.simple.db.api.FetchException;

public class Transaction {
    private String alias;
    private long threadId;

    public Transaction(String alias) {
        this.threadId = Util.getCurrentId();
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    public void commit() {
        if (threadId != Util.getCurrentId()) {
            throw new FetchException("");
        }
    }
}
