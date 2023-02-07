package com.axelor.apps.openauctionbase.csv;

import com.axelor.apps.openauction.db.Lot;
import com.axelor.apps.openauction.db.MissionLine;
import com.axelor.apps.openauction.db.repo.LotRepository;
import com.axelor.apps.openauction.db.repo.MissionLineRepository;
import com.google.inject.Inject;
import java.util.Map;

public class ImportMissionLine {
  MissionLineRepository missionLineRepository;
  LotRepository lotRepository;

  @Inject
  public ImportMissionLine(
      MissionLineRepository missionLineRepository, LotRepository lotRepository) {
    this.missionLineRepository = missionLineRepository;
    this.lotRepository = lotRepository;
  }

  public Object importMissionLine(Object bean, Map<String, Object> values) {

    MissionLine missionLine = (MissionLine) bean;

    String lotNo = (String) values.get("no");

    Lot lot = lotRepository.findByNo(lotNo);

    if (lot != null) {
      missionLine.setNoLot(lot);
    } else {
      missionLine.setNoLot(null);
    }

    return missionLine;
  }
}
