package prv.simple.db.basic;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import com.zaxxer.hikari.HikariDataSource;

import prv.simple.db.api.IConfigAdapter;
import prv.simple.db.api.IDataSourceBuilder;

public class DataSourceBuilder implements IDataSourceBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(DSManager.class);
    String confFileName;

    /**
     * 
     * @param confFileName
     *            数据库连接信息配置文件名
     */
    public DataSourceBuilder(String confFileName) {
        this.confFileName = confFileName;
    }

    /**
     * 通过配置文件初始化数据库配置信息
     */
    public Map<String, DataSource> initDataSource() {
        Map<String, DataSource> dataSources = new ConcurrentHashMap<>();
        InputStream is = Thread.currentThread().getClass().getClassLoader().getResourceAsStream(confFileName);
        if (is != null) {
            LOGGER.info("读取配置文件{}，获取数据库配置信息!", confFileName);
            Yaml yaml = new Yaml();
            try (is) {
                Iterable<Object> loadAll = yaml.loadAll(is);

                for (Object oconfig : loadAll) {
                    IConfigAdapter config = (IConfigAdapter) oconfig;

                    dataSources.put(config.getAlias(), build(config));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            LOGGER.warn("配置文件{}获取失败，请确认！", confFileName);
        }
        return dataSources;
    }

    /**
     * 使用IConfigAdapter创建HikariDataSource
     * 
     * @param config
     *            IConfigAdapter
     * @return HikariDataSource
     */
    private DataSource build(IConfigAdapter config) {
        return new HikariDataSource(config.toHikariConfig());
    }
}
