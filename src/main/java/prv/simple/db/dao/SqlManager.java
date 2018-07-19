package prv.simple.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(SqlManager.class);
    private static final Map<String, Map<String, Sql>> sqls = new ConcurrentHashMap<>();

    public static Sql getSql(String alias) {
        return new Sql(null);
    }
    
    public Connection getConnection() {
        return null;
    }
}
class Sql {
    private static final Logger LOGGER = LoggerFactory.getLogger(Sql.class);

    private Connection con;
    private String alias;
    private PreparedStatement ps;
    private volatile String sqlStr;
    private Object[] params;
    private ResultSet rs;
    private int queryTimeout = 0;

    Sql(Connection con) {
        this.con = con;
    }

    public void close() throws Exception {
        
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