package prv.simple.db;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Test;

import prv.simple.db.api.DBException;

public class SqlTest {
    @Test
    public void testInsertUpdate() throws DBException {
        try (SqlHelper sqler = new SqlHelper("test1");) {

            String sql = "insert into abc values(?,?,?)";
            sqler.setSql(sql);

            sqler.setInt(1, 23);
            sqler.setInt(2, 24);
            sqler.setInt(3, 25);
            sqler.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testInsert2() throws DBException {
        SqlHelper sqler = new SqlHelper("test1");
        try (sqler) {
            sqler.begin();
            String sql = "delete from abc";
            sqler.setSql(sql);
            sqler.executeUpdate();

            sql = "insert into abc values(?,?,?)";
            sqler.setSql(sql);

            sqler.setInt(1, 73);
            sqler.setInt(2, 74);
            sqler.setInt(3, 75);
            sqler.executeUpdate();

            int i = 1 / 0;
            sqler.commit();
        } catch (Exception e) {
            e.printStackTrace();
            sqler.rollBack();
        }
    }

    /**
     * 批处理测试
     */
    @Test
    public void testBatch() {
        SqlHelper sqler = new SqlHelper("test1");
        String sql = "delete from abc";
        sqler.setSql(sql);
        try (sqler) {
            sqler.executeUpdate();

            sqler.setSql("insert into abc values(?,?,?)");

            for (int j = 0; j < 15; j++) {
                sqler.setInt(1, j * 10 + 1);
                sqler.setInt(2, j * 10 + 2);
                sqler.setInt(3, j * 10 + 3);
                sqler.addBatch();
                sqler.executeBatch();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void query() {
        SqlHelper sqler = new SqlHelper("test1");
        sqler.setSql("select * from abc where a > ?");
        sqler.setInt(1, 33);
        ResultSet rs = null;
        try {
            rs = sqler.executeQuery();

            while (rs.next()) {
                System.out.print(rs.getInt(1));
                System.out.print(",");
                System.out.print(rs.getInt(2));
                System.out.print(",");
                System.out.println(rs.getInt(3));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (sqler != null) {
                try {
                    sqler.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
