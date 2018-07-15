package prv.simple.db.api;

import com.zaxxer.hikari.HikariConfig;

public interface IConfigAdapter {

    public String getAlias();

    public HikariConfig toHikariConfig();
}
