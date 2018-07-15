package prv.simple.db.basic;

import java.util.Map;
import java.util.Map.Entry;

import com.zaxxer.hikari.HikariConfig;

import prv.simple.db.api.IConfigAdapter;

public class DefaultConfigAdapter implements IConfigAdapter {
    static final String DEFAULT_ALIAS = "q_db";

    private String alias = DEFAULT_ALIAS;
    private String jdbcUrl;
    private String driverClassName;

    private String userName;

    private String password;
    private int maxLifetime;
    private int minimumIdle;
    private int maximumPoolSize;

    private int idleTimeout;
    private int connectionTimeout;
    private Map<String, String> dataSource;

    @Override
    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getMaxLifetime() {
        return maxLifetime;
    }

    public void setMaxLifetime(int maxLifetime) {
        this.maxLifetime = maxLifetime;
    }

    public int getMinimumIdle() {
        return minimumIdle;
    }

    public void setMinimumIdle(int minimumIdle) {
        this.minimumIdle = minimumIdle;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public int getIdleTimeout() {
        return idleTimeout;
    }

    public void setIdleTimeout(int idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public Map<String, String> getDataSource() {
        return dataSource;
    }

    public void setDataSource(Map<String, String> dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public HikariConfig toHikariConfig() {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(jdbcUrl);
        config.setDriverClassName(driverClassName);
        config.setUsername(userName);
        config.setPassword(password);
        config.setAutoCommit(false);

        config.setMaxLifetime(maxLifetime);
        config.setMinimumIdle(minimumIdle);
        config.setMaximumPoolSize(maximumPoolSize);
        config.setIdleTimeout(idleTimeout);
        config.setConnectionTimeout(connectionTimeout);

        for (Entry<String, String> entry : dataSource.entrySet()) {
            config.addDataSourceProperty(entry.getKey(), entry.getValue());
        }

        return config;
    }

    @Override
    public String toString() {
        return "ConfigAdapter [alias=" + alias + ", jdbcUrl=" + jdbcUrl + ", driverClassName=" + driverClassName
                + ", userName=" + userName + ", password=" + password + ", maxlifetime=" + maxLifetime
                + ", minimumIdle=" + minimumIdle + ", maximumPoolSize=" + maximumPoolSize + ", idleTimeout="
                + idleTimeout + ", connectionTimeout=" + connectionTimeout + ", dataSource=" + dataSource + "]";
    }
}
