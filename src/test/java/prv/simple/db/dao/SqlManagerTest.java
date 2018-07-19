package prv.simple.db.dao;

import org.junit.Test;

public class SqlManagerTest {

    @Test
    public void getSql() {
        Sql sql = SqlManager.getSql("test1");
    }
}
