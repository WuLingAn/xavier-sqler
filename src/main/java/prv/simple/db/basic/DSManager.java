package prv.simple.db.basic;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import prv.simple.db.api.FetchException;

public class DSManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(DSManager.class);
    private static final Map<String, DataSource> dataSources;

    private DSManager() {
    }

    static final String CONF_FILE_NAME = "dbs-conf.yaml";

    static {
        dataSources = new DataSourceBuilder(CONF_FILE_NAME).initDataSource();
    }

    public static DataSource getDataSources(String alias) {
        DataSource dataSource = dataSources.get(alias);
        if (dataSource == null) {
            LOGGER.warn("获取数据源失败，无法识别的数据库配置标志:{}", alias);
            throw new FetchException("获取数据源失败，无法识别的数据库配置标志:" + alias);
        }
        return dataSource;
    }

    public static Connection getConnection(String alias) throws SQLException {
        return getDataSources(alias).getConnection();
    }

    /**
     * 使用默认连接名获得数据库连接{@link DefaultConfigAdapter.DEFAULT_ALIAS}
     * 
     * @return Connection
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        return getDataSources(DefaultConfigAdapter.DEFAULT_ALIAS).getConnection();
    }
}
