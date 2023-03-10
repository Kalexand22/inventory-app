package com.axelor.apps.openauctionbase.listener;

import com.axelor.apps.openauction.db.MissionLine;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class MissionLineListener {
  @PostPersist
  private void onPostPersist(MissionLine missionLine) {

    try{
      Class.forName("com.pervasive.jdbc.v2.Driver").newInstance();
      Connection conn = DriverManager.getConnection("jdbc:pervasive://localhost:1583/DEMO");

      Statement stmt =
          conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

      //insert data into table inventaire
      stmt.execute("null");
      

      
      stmt.close();
      conn.close();
    }
    catch(Exception e){
      System.out.println(e);
    }
  }

  @PostUpdate
  private void onPostUpdate(MissionLine missionLine) {
    System.out.println("Contact saved");
  }

  @PostRemove
  private void onPostRemove(MissionLine missionLine) {
    System.out.println("Contact saved");
  }
}
