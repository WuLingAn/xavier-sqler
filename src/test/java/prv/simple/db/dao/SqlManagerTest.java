package prv.simple.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.Test;

import prv.simple.db.basic.DSManager;

public class SqlManagerTest {

    @Test
    public void getSql() throws Exception {
        Connection con = DSManager.getConnection("test1");

        PreparedStatement pst = con.prepareStatement("select * from abc");
        PreparedStatement inpst = con.prepareStatement("insert into bcd values(?,?,?)");
        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            inpst.setInt(1, rs.getInt(1));
            inpst.setInt(2, rs.getInt(2));
            inpst.setInt(3, rs.getInt(3));
            inpst.addBatch();
            inpst.executeBatch();
            con.commit();
        }
    }
}
