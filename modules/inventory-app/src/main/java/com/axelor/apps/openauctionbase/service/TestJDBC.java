package com.axelor.apps.openauctionbase.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestJDBC {

  public static void test() {
    try {
      Class.forName("com.pervasive.jdbc.v2.Driver").newInstance();
      Connection conn = DriverManager.getConnection("jdbc:pervasive://172.24.240.1:1583/DEMO");

      Statement stmt =
          conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

      ResultSet rs = stmt.executeQuery("SELECT * FROM requisitions");
      String tmp = "";
      while (rs.next()) {
        tmp = rs.getString(1);
        tmp = rs.getString(2);
      }

      rs.close();
      stmt.close();
      conn.close();
    } catch (Exception e) {

    }
  }
}
