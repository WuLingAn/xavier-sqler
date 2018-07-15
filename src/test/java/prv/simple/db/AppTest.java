package prv.simple.db;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Optional;
import java.util.Properties;

import javax.sql.DataSource;

import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.util.PropertyElf;

import prv.simple.db.basic.DSManager;
import prv.simple.db.basic.DefaultConfigAdapter;
import prv.simple.db.yml.HelloSnakeYAML;

public class AppTest {

    @Test
    public void loadAllByYaml() throws FileNotFoundException {
        Yaml yaml = new Yaml();
        URL url = HelloSnakeYAML.class.getClassLoader().getResource("dbs-conf.yaml");
        if (url != null) {
            Iterable<Object> is = yaml.loadAll(new FileInputStream(url.getFile()));

            for (Object po : is) {
                DefaultConfigAdapter ca = (DefaultConfigAdapter) po;

                HikariConfig hikariConfig = ca.toHikariConfig();

                new HikariDataSource(hikariConfig);
            }
        }
    }

    @Test
    public void getDataSource() throws SQLException {
        DataSource ds1 = DSManager.getDataSources("test1");
        System.out.println(ds1);

        Connection con1 = ds1.getConnection();
        PreparedStatement ps = con1.prepareStatement("insert into abc values(99,99,99)");
        ps.executeUpdate();

        con1.commit();
        ps.close();
        con1.close();

        DataSource ds2 = DSManager.getDataSources("test2");

        System.out.println(ds2);
        Connection con2 = ds2.getConnection();

        ps = con2.prepareStatement("insert into abc values(88,88,88)");
        ps.executeUpdate();

        con2.commit();
        ps.close();
        con2.close();
    }

    @Test
    public void test1() throws SQLException {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=utf8");
        config.setDriverClassName("org.mariadb.jdbc.Driver");
        // config.setDataSourceClassName("org.mariadb.jdbc.MariaDbDataSource");
        config.setUsername("root");
        config.setPassword("root");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        HikariDataSource ds = new HikariDataSource(config);

        Connection connection = ds.getConnection();
        PreparedStatement ps = connection.prepareStatement("select * from abc");

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            System.out.println(rs.getString(1) + "," + rs.getString(2) + "," + rs.getString(3));
        }
        ds.getConnection();

        rs.close();
        ds.close();
    }

    @Test
    public void test3() throws SQLException {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=utf8");
        config.setDriverClassName("org.mariadb.jdbc.Driver");
        // config.setDataSourceClassName("org.mariadb.jdbc.MariaDbDataSource");
        config.setUsername("root");
        config.setPassword("root");
        config.setMaxLifetime(30000);
        config.setMinimumIdle(5);
        config.setMaximumPoolSize(15);
        config.setIdleTimeout(10000);
        config.setConnectionTimeout(32000);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        HikariDataSource ds = new HikariDataSource(config);

        // try {
        // Thread.sleep(10000);
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // }
        for (int i = 0; i < 10; i++) {
            Connection connection = ds.getConnection();
            // System.out.print(connection + ":");
            // System.out.println(pconnection == connection);
            // pconnection = connection;
            // PreparedStatement ps = connection.prepareStatement("select * from abc");
            // ResultSet rs = ps.executeQuery();
            // while (rs.next()) {
            //// System.out.println(rs.getString(1) + "," + rs.getString(2) + "," +
            // rs.getString(3));
            // }
            // // if (i % 2 == 0)
            // connection.close();
            // rs.close();
            connection = null;
        }

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 5; i++) {
            Connection connection = ds.getConnection();
        }
        System.out.println("5");
        ds.close();
    }

    @Test
    public void test2() throws SQLException {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=utf8");
        config.setDriverClassName("org.mariadb.jdbc.Driver");
        // config.setDataSourceClassName("org.mariadb.jdbc.MariaDbDataSource");
        config.setUsername("root");
        config.setPassword("root");
        config.setAutoCommit(false);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        HikariDataSource ds = new HikariDataSource(config);

        Connection connection = ds.getConnection();
        PreparedStatement ps = connection.prepareStatement("insert into abc values(2,3,4)");
        ps.executeUpdate();

        connection.commit();
        ps.close();
        connection.close();

        ds.close();
    }

    @Test
    public void test4() throws Exception {
        Properties propfile2 = new Properties();
        propfile2.load(AppTest.class.getResourceAsStream("/propfile2.properties"));
        HikariConfig config = new HikariConfig(propfile2);
        config.validate();
        // com.zaxxer.hikari.mocks.StubDataSource
        Class<?> clazz = this.getClass().getClassLoader().loadClass(config.getDataSourceClassName());
        DataSource dataSource = (DataSource) clazz.newInstance();
        PropertyElf.setTargetFromProperties(dataSource, config.getDataSourceProperties());

        dataSource.getConnection();
    }

    @Test
    public void test5() {
        Properties props = new Properties();
        // props.setProperty("dataSourceClassName",
        // "org.postgresql.ds.PGSimpleDataSource");
        props.setProperty("dataSource.user", "root");
        props.setProperty("dataSource.password", "root");
        props.setProperty("dataSource.databaseName", "test");
        props.setProperty("dataSource.portNumber", "3306");
        props.setProperty("dataSource.serverName", "127.0.0.1");
        props.put("dataSource.logWriter", new PrintWriter(System.out));

        props.setProperty("jdbcUrl", "jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=utf8");
        // props.setProperty("driverClassName", "org.mariadb.jdbc.Driver");

        HikariConfig config = new HikariConfig(props);
        HikariDataSource ds = new HikariDataSource(config);
    }

    @Test
    public void matcher() {
        String line = "select * from aab where fdkfd= ? and fdk = ? and kfdk <> ?";
        String pattern = "?";

        int index = -1;
        int times = 0;
        while (true) {
            index = line.indexOf(pattern, index + 1);
            if (index > -1)
                times++;
            else
                break;
            System.out.println("times:" + times + ",index:" + index);
        }
        System.out.println();
    }

    @Test
    public void type() {
        Object[] params = new Object[10];
        params[0] = "123";
        params[1] = 123;
        params[2] = '5';
        params[3] = null;
        params[4] = 56l;
        params[5] = new Date();
        byte b = 5;
        params[6] = b;
        params[7] = true;
        params[8] = 5.99;
        params[9] = 5.9f;

        for (int i = 0; i < params.length; i++) {
            System.out.println(Optional.ofNullable(params[i]).orElse("666").getClass().getSimpleName());
        }

    }
}
