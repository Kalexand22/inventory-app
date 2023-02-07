package com.axelor.apps.openauctionbase.csv;

import com.axelor.apps.base.db.repo.ProductRepository;
import com.axelor.apps.openauction.db.MissionServiceLine;
import com.axelor.apps.openauction.db.repo.AuctionHeaderRepository;
import com.axelor.apps.openauction.db.repo.LotRepository;
import com.google.inject.Inject;
import java.util.Map;

public class ImportMissionServiceLine {
  LotRepository lotRepository;
  AuctionHeaderRepository auctionHeaderRepository;
  ProductRepository productRepository;

  @Inject
  public ImportMissionServiceLine(
      LotRepository lotRepository,
      AuctionHeaderRepository auctionHeaderRepository,
      ProductRepository productRepository) {
    this.lotRepository = lotRepository;
    this.auctionHeaderRepository = auctionHeaderRepository;
    this.productRepository = productRepository;
  }

  public Object importMissionServiceLine(Object bean, Map<String, Object> values) {

    MissionServiceLine missionServiceLine = (MissionServiceLine) bean;
    String productNo = (String) values.get("productNo");
    if (!productNo.isEmpty() && productRepository.findByCode(productNo) == null) {
      return null;
    }
    String auctionNo = (String) values.get("auctionNo");
    if (!auctionNo.isEmpty() && auctionHeaderRepository.findByNo(productNo) == null) {
      return null;
    }
    String lotNo = (String) values.get("lotNo");
    if (!lotNo.isEmpty() && lotRepository.findByNo(lotNo) == null) {
      return null;
    }

    if (!productNo.isEmpty())
      missionServiceLine.setProductNo(productRepository.findByCode(productNo));
    if (!auctionNo.isEmpty())
      missionServiceLine.setAuctionNo(auctionHeaderRepository.findByNo(auctionNo));
    if (!lotNo.isEmpty()) missionServiceLine.setLotNo(lotRepository.findByNo(lotNo));

    return missionServiceLine;
  }
}
