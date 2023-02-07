package com.axelor.apps.openauctionbase.service;

import com.axelor.apps.base.db.Product;
import com.axelor.apps.openauction.db.AuctionHeader;
import com.axelor.apps.openauction.db.AuctionLine;
import com.axelor.apps.openauction.db.AuctionServicePrice;
import com.axelor.apps.openauction.db.Lot;
import com.axelor.apps.openauction.db.MissionHeader;
import com.axelor.apps.openauction.db.MissionServiceLine;
import com.axelor.apps.openauction.db.MissionServicePrice;
import com.axelor.apps.openauction.db.TariffScale;
import java.math.BigDecimal;
import java.util.Date;

public class AuctionServicePriceMgtImpl implements AuctionServicePriceMgt {

  @Override
  public void findAuctionServicePrice(
      MissionServiceLine pMissionServiceLine, BigDecimal pBidPrice, Boolean pEstimated) {
    // TODO Auto-generated method stub

  }

  @Override
  public MissionServiceLine updateAuctionServicePrice(
      MissionServiceLine pMissionServiceLine, Boolean pEstimated) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Boolean testPrice(
      Object pServicePrice,
      String DocTemplate,
      String DocNo,
      String LotGroup,
      String LotCode,
      String ContactGroup,
      String ContactCode) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void getAmount(MissionHeader pMissionHeaderNo, Lot pLotNo) {
    // TODO Auto-generated method stub

  }

  @Override
  public void getTempServiceMissionPrice(MissionServicePrice pMissionServicePrice) {
    // TODO Auto-generated method stub

  }

  @Override
  public void getTariffScaleAmount(
      BigDecimal pAmount, TariffScale pTariffScale, Object pAccesBuffer) {
    // TODO Auto-generated method stub

  }

  @Override
  public void getTariffScaleDetail(Object pAccesBuffer) {
    // TODO Auto-generated method stub

  }

  @Override
  public BigDecimal getBaseAMount() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void searchBidLine(
      AuctionHeader pAuctionHeaderNo,
      Integer pLineNo,
      Lot pLotNo,
      MissionServiceLine pBidMissionServiceLine) {
    // TODO Auto-generated method stub

  }

  @Override
  public void modifyPrice(AuctionServicePrice pRec, AuctionServicePrice pXRec, String pAction) {
    // TODO Auto-generated method stub

  }

  @Override
  public void applyNewPrice(AuctionServicePrice pAuctionServicePrice, String pAction) {
    // TODO Auto-generated method stub

  }

  @Override
  public void getUsedServPriceForAuction(
      AuctionHeader pAuctionNo, AuctionServicePrice pAuctionServicePrice) {
    // TODO Auto-generated method stub

  }

  @Override
  public void markAuctionServPrice(
      MissionServiceLine pMissionServiceLine, AuctionServicePrice pAuctionServicePrice) {
    // TODO Auto-generated method stub

  }

  @Override
  public void getServPriceByMissServLine(
      MissionServiceLine pMissionServiceLine, AuctionServicePrice pAuctionServicePrice) {
    // TODO Auto-generated method stub

  }

  @Override
  public void getServPriceByLotNoItemNo(
      Lot pLotNo, String pItemNo, AuctionServicePrice pAuctionServicePrice) {
    // TODO Auto-generated method stub

  }

  @Override
  public Date getPriceDate(MissionServiceLine pMissServLine) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void VATConvert(Product pItem, MissionServiceLine pMissServLine) {
    // TODO Auto-generated method stub

  }

  @Override
  public String getBuyerCommisionByAuctionLine(
      AuctionLine pAuctionLine, Boolean pReturnTariffScaleCode, Boolean pReturnIncVAT) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public BigDecimal calcIncVATPrice(
      Boolean pReturnIncVAT, Boolean pPriceIncVAT, BigDecimal pPrice, BigDecimal pVAT) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public BigDecimal getBuyerCommisionVATRate(AuctionLine pAuctionLine) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public BigDecimal calcBuyerAuctionComVATRate(Lot pLot) {
    // TODO Auto-generated method stub
    return null;
  }
}
