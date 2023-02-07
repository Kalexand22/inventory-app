package com.axelor.apps.openauctionbase.service;

import com.axelor.apps.openauction.db.AuctionHeader;
import com.axelor.apps.openauction.db.AuctionLine;

public class AuctionManagementImpl implements AuctionManagement {

  @Override
  public void addAuctionLine(AuctionLine pAuctionLine) {
    // TODO Auto-generated method stub

  }

  @Override
  public void removeAuctionLine(AuctionLine pAuctionLine) {
    // TODO Auto-generated method stub

  }

  @Override
  public void getSetup() {
    // TODO Auto-generated method stub

  }

  @Override
  public void changeAuctionLot(AuctionLine pAuctionLine, AuctionLine pXAuctionLine) {
    // TODO Auto-generated method stub

  }

  @Override
  public void changeActivity(
      String pActivityCode,
      String pMissionNo,
      String pAuctionNo,
      String pLotNo,
      String pLotTemplate,
      Boolean Add,
      Integer pAuctionLineNo) {
    // TODO Auto-generated method stub

  }

  @Override
  public Boolean createContact(AuctionLine pAuctionLine, String NewContactNo) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void generateDefaultPassingOrderNo(AuctionHeader pAuctionHeader) {
    // TODO Auto-generated method stub

  }

  @Override
  public void createDimCode(AuctionHeader pAuctionHeader) {
    // TODO Auto-generated method stub

  }

  @Override
  public void createDimValueDimCode(AuctionHeader pAuctionHeader) {
    // TODO Auto-generated method stub

  }

  @Override
  public void sendLotToAuction(AuctionLine pLot, Boolean pAskConfirm) {
    // TODO Auto-generated method stub

  }

  @Override
  public Boolean selectAuctionNo(String pAuctionNo) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void createAuctionLine(
      String pAuctionNo, String pLotNo, Integer pPassingOrderNo, String pPassingIndexNo) {
    // TODO Auto-generated method stub

  }

  @Override
  public void retrieveLotCurrentAuction(
      AuctionLine pLot, AuctionLine pAuctionLine, Boolean pExcludeLine) {
    // TODO Auto-generated method stub

  }

  @Override
  public void passAbsBidConfirm(Boolean pPass) {
    // TODO Auto-generated method stub

  }

  @Override
  public Double calcAuctSalesPersonCommission(String pAuctionNo) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setToAuctionNo(String NewToAuctionNo) {
    // TODO Auto-generated method stub

  }
}
