package prv.simple.db;

import org.junit.Test;

import prv.simple.db.SqlHelper;
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
}
