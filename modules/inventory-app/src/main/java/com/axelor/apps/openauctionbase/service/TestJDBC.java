package com.axelor.apps.openauctionbase.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.lang.Class;
public class TestJDBC {
    
    public static void test()
    {
        try{
            Class.forName("com.pervasive.jdbc.v2.Driver");
            Connection conn=
            DriverManager.getConnection("jdbc:pervasive://localhost:1583/DEMO");
            
            Statement stmt =
            conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            
            ResultSet rs = stmt.executeQuery("SELECT * FROM requisitions");
            
            
            
            
            rs.close();
            stmt.close();
            conn.close();
        }
        catch (Exception e)
        {

        }
    }
}
