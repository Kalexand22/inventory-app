package com.axelor.apps.openauctionbase.csv;

import com.axelor.apps.openauction.db.AuctionHeader;
import com.axelor.apps.openauction.db.Lot;
import com.axelor.apps.openauction.db.repo.AuctionHeaderRepository;
import com.axelor.apps.openauction.db.repo.LotRepository;
import com.axelor.apps.openauction.db.repo.MissionHeaderRepository;
import com.google.inject.Inject;
import java.util.Map;

public class ImportLot {
  AuctionHeaderRepository auctionHeaderRepository;
  LotRepository lotRepository;
  MissionHeaderRepository missionHeaderRepository;

  @Inject
  public ImportLot(
      AuctionHeaderRepository auctionHeaderRepository,
      LotRepository lotRepository,
      MissionHeaderRepository missionHeaderRepository) {
    this.auctionHeaderRepository = auctionHeaderRepository;
    this.lotRepository = lotRepository;
    this.missionHeaderRepository = missionHeaderRepository;
  }

  public Object importLot(Object bean, Map<String, Object> values) {

    Lot lot = (Lot) bean;

    String currentMissionNo = (String) values.get("currentMissionNo");
    if (currentMissionNo != null && missionHeaderRepository.findByNo(currentMissionNo) == null) {
      return null;
    }

    String currentAuctionNo = (String) values.get("currentAuctionNo");
    AuctionHeader auctionHeader = auctionHeaderRepository.findByNo(currentAuctionNo);
    if (auctionHeader != null) {
      lot.setCurrentAuctionNo(auctionHeader);

    } else {
      lot.setCurrentAuctionNo(null);
    }

    return lot;
  }
}
