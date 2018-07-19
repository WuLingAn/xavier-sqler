package prv.simple.db;

import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import prv.simple.db.api.DBException;
import prv.simple.db.api.FetchException;
import prv.simple.db.basic.DSManager;

/**
 * 非线程安全的sql操作类
 * 
 * @deprecated 升个级吧
 * @author Xavier
 *
 */
public class SqlHelper implements AutoCloseable {
    private static final Logger LOGGER = LoggerFactory.getLogger(SqlHelper.class);

    private Connection con;
    private String alias;
    private PreparedStatement ps;
    private volatile String sqlStr;
    private Object[] params;
    private ResultSet rs;
    private int queryTimeout = 0;

    private volatile AtomicBoolean execWithTransaction = new AtomicBoolean(false);

    public SqlHelper(String alias) {
        this.alias = alias;
    }

    public void setSql(CharSequence stb) {
        setSql(stb.toString());
    }

    public ResultSet executeQuery() throws SQLException {
        this.prepare();

        return ps.executeQuery();
    }

    public int executeUpdate() throws DBException {
        int chgNum = 0;

        try {
            this.prepare();
            chgNum = ps.executeUpdate();
            // 事物开启状态下，不执行提交
            if (!execWithTransaction.get()) {
                con.commit();
            }
        } catch (Exception e) {
            throw new DBException(e);
        } finally {
            // 事务状态下，不进行资源关闭
            if (!execWithTransaction.get()) {
                try {
                    close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return chgNum;
    }

    public void rollBack() {
        if (con != null) {
            try {
                con.rollback();
            } catch (SQLException e) {
                LOGGER.error("回滚发生异常", e);
            }
            try {
                close();
            } catch (Exception e) {
                LOGGER.error("回滚中资源关闭方法发生异常", e);
            }
        }
    }

    public void begin() {
        execWithTransaction.set(true);
    }

    /**
     * <p>
     * 事务结束，提交整个事务
     * <p>
     * 提交完成后需要注意资源释放
     * 
     * <pre>
     * close();
     * </pre>
     * <p>
     * 使用独立对象包装begin()和commit()，使其含义独立
     * 
     * @throws SQLException
     */
    public void commit() throws SQLException {
        // 标志事务结束
        if (execWithTransaction.getAndSet(false)) {
            throw new FetchException("发生了预期外的事务提交操作");
        }
        // 在连接上执行commit操作。
        con.commit();
        con.close();
        con = null;
        ps.close();
        ps = null;
    }

    public void addBatch() throws DBException {
        try {
            checkBatchParam();
            this.prepare();
            ps.addBatch();
        } catch (SQLException e) {
            throw new DBException(e);
        } catch (DBException e) {
            throw e;
        }
    }

    /**
     * 批处理方法至少需要一个参数
     * 
     * @throws DBException
     */
    private void checkBatchParam() throws DBException {
        if (params == null || params.length < 1) {
            throw new DBException("添加的批处理参数无效,请确认!");
        }
    }

    public int[] executeBatch() throws SQLException {
        int[] chgNums = ps.executeBatch();
        return chgNums;
    }

    public void setSql(String sqlstr) {
        this.sqlStr = sqlstr;
        params = new Object[paramNum()];
    }

    /**
     * 获取数据库连接，初始化prepareStatement
     * 
     * @throws SQLException
     */
    private void prepare() throws SQLException {
        // 事务执行中需要持久化持有数据库连接信息
        if (con == null) {
            con = DSManager.getConnection(alias);
        }
        // 事物状态下，取消自动提交，非开启事物状态下是否需要开启自动提交功能？
        con.setAutoCommit(!execWithTransaction.get());

        // 每次执行不同sql，生成不同的PreparedStatement
        ps = con.prepareStatement(sqlStr);
        if (queryTimeout >= 100) {
            ps.setQueryTimeout(queryTimeout);
        }
        setParams();
    }

    /*
     * 将set的各种参数设置到PreparedStatement中
     */
    private void setParams() throws SQLException {
        for (int i = 0; i < params.length; i++) {
            Object o = params[i];

            if (o instanceof String) {
                ps.setString(i + 1, (String) o);
            } else if (o instanceof Integer) {
                ps.setInt(i + 1, (Integer) o);
            } else if (o instanceof Double) {
                ps.setDouble(i + 1, (Double) o);
            } else if (o instanceof Float) {
                ps.setFloat(i + 1, (Float) o);
            } else if (o instanceof Boolean) {
                ps.setBoolean(i + 1, (Boolean) o);
            } else if (o instanceof nullType) {
                ps.setNull(i + 1, ((nullType) o).getType());
            } else if (o instanceof Long) {
                ps.setLong(i + 1, (Long) o);
            } else if (o instanceof Blob) {
                ps.setBlob(i + 1, (Blob) o);
            } else if (o instanceof InputStream) {
                ps.setBlob(i + 1, (InputStream) o);
            } else {
                throw new SQLException("不支持的参数格式：" + o.getClass().getName());
            }
        }
    }

    public void setString(int index, String string) {
        checkIndex(index);
        Object currValue = string == null ? new nullType(Types.VARCHAR) : string;

        params[index - 1] = currValue;
    }

    public void setInt(int index, Integer i) {
        checkIndex(index);
        Object currValue = i == null ? new nullType(Types.INTEGER) : i;
        params[index - 1] = currValue;
    }

    public void setDouble(int index, Double d) {
        checkIndex(index);
        Object currValue = d == null ? new nullType(Types.DOUBLE) : d;
        params[index - 1] = currValue;
    }

    public void setFloat(int index, Float f) {
        checkIndex(index);
        Object currValue = f == null ? new nullType(Types.FLOAT) : f;
        params[index - 1] = currValue;
    }

    public void setBoolean(int index, Boolean b) {
        checkIndex(index);
        Object currValue = b == null ? new nullType(Types.BOOLEAN) : b;
        params[index - 1] = currValue;
    }

    public void setBlob(int index, Blob b) {
        checkIndex(index);
        Object currValue = b == null ? new nullType(Types.BLOB) : b;
        params[index - 1] = currValue;
    }

    public void setBlob(int index, InputStream in) {
        checkIndex(index);
        Object currValue = in == null ? new nullType(Types.BLOB) : in;
        params[index - 1] = currValue;
    }

    private void checkIndex(int index) {
        if (index < 1 || index > params.length) {
            String message = String.format("不合法的索引值- %d", index);
            throw new RuntimeException(message);
        }
    }

    @Override
    public void close() throws Exception {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            rs = null;
        }

        try {
            if (ps != null) {
                ps.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ps = null;
        }

        try {
            if (con != null) {
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            con = null;
        }

    }

    class nullType {
        int typeCode;

        nullType(int typeCode) {
            this.typeCode = typeCode;
        }

        public int getType() {
            return typeCode;
        }
    }

    /**
     * 计算需执行sql中有多少占位符<code>?</code>
     * 
     * @return sql中占位符<code>?</code>的个数
     */
    private int paramNum() {
        int number = 0;

        int index = -1;

        while (true) {
            index = sqlStr.indexOf("?", index + 1);
            if (index > -1)
                number++;
            else
                break;
        }

        return number;
    }
    
    /**
     * 设置超时执行时间，当设置值小于100时，设置无效
     * 
     * @param timeout
     *            超时时间
     */
    public void setExecuteTimeOut(int timeout) {
        if (timeout < 100) {
            LOGGER.warn("你设置的值{}小于100ms，设置无效！", timeout);
            return;
        }
        this.queryTimeout = timeout;
    }
}
