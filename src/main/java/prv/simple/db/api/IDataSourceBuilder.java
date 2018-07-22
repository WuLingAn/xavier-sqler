package prv.simple.db.api;

import java.util.Map;

import javax.sql.DataSource;

@FunctionalInterface
public interface IDataSourceBuilder {
    public Map<String, DataSource> initDataSource();
}