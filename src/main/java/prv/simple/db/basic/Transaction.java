package prv.simple.db.basic;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import prv.simple.db.api.FetchException;
import prv.simple.db.dao.SqlManager;

public class Transaction {
    private static final Logger LOGGER = LoggerFactory.getLogger(Transaction.class);
    private String alias;
    private long threadId;

    private boolean validFlag = true;

    public Transaction(String alias) {
        this.threadId = Util.getCurrentId();
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    private void checkThreadId() {
        long thisId = Util.getCurrentId();
        if (threadId != Util.getCurrentId()) {
            throw new FetchException("要关闭的连接位于线程" + threadId + ",当前线程" + thisId);
        }
    }

    private void checkValid() {
        if (!isValid()) {
            throw new FetchException("当前事务已失效！");
        }
    }

    public boolean isValid() {
        return validFlag;
    }

    public void setInValid(String key) {
        this.validFlag = !getKey().equals(key);
        if (isValid()) {
            LOGGER.warn("变更事务{}状态失败，提供的key【{}】不合法！", getKey(), key);
        }
    }

    /**
     * 提交当前事务
     * 
     * @throws SQLException
     */
    public void commit() throws SQLException {
        checkValid();
        checkThreadId();
        SqlManager.commit(this);
    }

    /**
     * 回滚当前事务
     * 
     * @throws SQLException
     */
    public void rollback() throws SQLException {
        checkValid();
        checkThreadId();
        SqlManager.rollback(this);
    }

    /**
     * 获得事务信息
     * 
     * @return key
     */
    public String getKey() {
        checkThreadId();
        return Util.getCurrentId(alias);
    }
}
