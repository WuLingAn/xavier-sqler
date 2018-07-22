package prv.simple.db.dao;

import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import prv.simple.db.api.DBException;
import prv.simple.db.basic.DSManager;
import prv.simple.db.basic.Util;

public class SqlManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(SqlManager.class);
    private static final Map<String, Connection> cons = new ConcurrentHashMap<>();

    public static Sql getSql(String alias) {

        return new Sql(getConnection(alias));
    }

    public static Connection getConnection(String alias) {
        String key = Util.getCurrentId(alias);
        LOGGER.debug("create a new Sql with {}", key);
        Connection con = cons.get(key);
        try {
            if (con == null || con.isClosed()) {
                con = DSManager.getConnection(alias);
                cons.put(key, con);
            }
        } catch (SQLException e) {
            LOGGER.debug("创建sql获取连接时出现问题！", e);
        }
        return con;
    }

    /**
     * 关闭连接
     * 
     * @param alias
     * @throws SQLException
     */
    public static void closeConnection(String alias) throws SQLException {
        String key = Util.getCurrentId(alias);
        Connection con = cons.remove(key);
        if (con != null) {
            con.close();
        }
    }

    public static class Sql {
        private static final Logger LOGGER = LoggerFactory.getLogger(Sql.class);

        private Connection con;
        private String alias;
        private PreparedStatement pst;
        private volatile String sqlStr;
        private Object[] params;
        private ResultSet rs;
        private int queryTimeout = 0;
        private volatile AtomicBoolean batchFlag = new AtomicBoolean(false);

        private volatile AtomicBoolean execWithTransaction = new AtomicBoolean(false);

        Sql(Connection con) {
            this.con = con;
        }

        public Connection getConnection() {
            return con;
        }

        protected boolean isClosed() {
            return con == null;
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
            int[] chgNums = pst.executeBatch();
            return chgNums;
        }

        public void addBatch() throws DBException {
            try {
                checkBatchParam();
                this.prepare(true);
                pst.addBatch();
            } catch (SQLException e) {
                throw new DBException(e);
            } catch (DBException e) {
                throw e;
            }
        }

        public void setSql(String sqlstr) {
            this.sqlStr = sqlstr;
            params = new Object[paramNum()];
            // 设置新sql意味着重新来过
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
         * 获取数据库连接，初始化prepareStatement
         * 
         * @throws SQLException
         */
        private void prepare(boolean isBatch) throws SQLException {
            // 批处理不进行
            if (!isBatch) {
                close();
            }
            // 每次执行不同sql，生成不同的PreparedStatement
            pst = con.prepareStatement(sqlStr);
            if (queryTimeout >= 100) {
                pst.setQueryTimeout(queryTimeout);
            }
            setParams();
        }

        /**
         * 获取数据库连接，初始化prepareStatement
         * 
         * @throws SQLException
         */
        private void prepareBatch() throws SQLException {
            // 每次执行不同sql，生成不同的PreparedStatement
            pst = con.prepareStatement(sqlStr);
            if (queryTimeout >= 100) {
                pst.setQueryTimeout(queryTimeout);
            }
            setParams();
        }

        public void close() {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                rs = null;
            }

            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                pst = null;
            }
        }

        /**
         * 释放资源
         */
        public void release() {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                rs = null;
            }
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                pst = null;
            }
        }

        public void closeConnection() throws SQLException {
            SqlManager.closeConnection(alias);
        }

        private void setParams() throws SQLException {
            for (int i = 0; i < params.length; i++) {
                Object o = params[i];

                if (o instanceof String) {
                    pst.setString(i + 1, (String) o);
                } else if (o instanceof Integer) {
                    pst.setInt(i + 1, (Integer) o);
                } else if (o instanceof Double) {
                    pst.setDouble(i + 1, (Double) o);
                } else if (o instanceof Float) {
                    pst.setFloat(i + 1, (Float) o);
                } else if (o instanceof Boolean) {
                    pst.setBoolean(i + 1, (Boolean) o);
                } else if (o instanceof nullType) {
                    pst.setNull(i + 1, ((nullType) o).getType());
                } else if (o instanceof Long) {
                    pst.setLong(i + 1, (Long) o);
                } else if (o instanceof Blob) {
                    pst.setBlob(i + 1, (Blob) o);
                } else if (o instanceof InputStream) {
                    pst.setBlob(i + 1, (InputStream) o);
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

        class nullType {
            int typeCode;

            nullType(int typeCode) {
                this.typeCode = typeCode;
            }

            public int getType() {
                return typeCode;
            }
        }
    }
}