package com.axelor.apps.openauctionbase.service;

import com.axelor.apps.openauction.db.MissionHeader;
import com.axelor.apps.openauction.db.repo.MissionHeaderRepository;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class EncheresJDBC {
  Connection conn = null;
  MissionHeaderRepository missionHeaderRepo;

  @Inject
  public EncheresJDBC(MissionHeaderRepository missionHeaderRepo) {

    this.missionHeaderRepo = missionHeaderRepo;
  }

  @Transactional
  public void selectDossierInv() throws Exception {

    Class.forName("com.pervasive.jdbc.v2.Driver").newInstance();
    conn = DriverManager.getConnection("jdbc:pervasive://localhost:1583/DEMO");

    Statement stmt =
        conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

    ResultSet rs = stmt.executeQuery("SELECT * FROM dossiers_inv");
    // ResultSetMetaData metadata = rs.getMetaData();
    while (rs.next()) {
      MissionHeader missionHeader = missionHeaderRepo.findByNo(rs.getString(1));
      if (missionHeader == null) {
        missionHeader = new MissionHeader();
      } else {
        continue;
      }
      missionHeader.setNo(rs.getString(1));
      missionHeader.setDescription(rs.getString(4));
      missionHeaderRepo.save(missionHeader);
    }

    rs.close();
    stmt.close();
    conn.close();
  }
}
