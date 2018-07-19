package prv.simple.db.basic;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import com.zaxxer.hikari.HikariDataSource;

import prv.simple.db.api.IConfigAdapter;

public class DSManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(DSManager.class);
    private static final Map<String, DataSource> dataSources = new ConcurrentHashMap<>();

    private DSManager() {
    }

    private static final String CONF_FILE_NAME = "dbs-conf.yaml";

    static {
        InputStream is = DataSourceBuilder.class.getClassLoader().getResourceAsStream(CONF_FILE_NAME);
        if (is != null) {
            LOGGER.info("读取配置文件{}，获取数据库配置信息!", CONF_FILE_NAME);
            Yaml yaml = new Yaml();
            Iterable<Object> loadAll = yaml.loadAll(is);

            for (Object oconfig : loadAll) {
                IConfigAdapter config = (IConfigAdapter) oconfig;

                dataSources.put(config.getAlias(), new HikariDataSource(config.toHikariConfig()));
            }
        } else {
            LOGGER.warn("配置文件{}获取失败，请确认！", CONF_FILE_NAME);
        }
    }

    public static DataSource getDataSources(String alias) {
        return dataSources.get(alias);
    }

    public static Connection getConnection(String alias) throws SQLException {
        return getDataSources(alias).getConnection();
    }

    public static Connection getConnection() throws SQLException {
        return getDataSources(DefaultConfigAdapter.DEFAULT_ALIAS).getConnection();
    }
}
