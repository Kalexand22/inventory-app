package com.axelor.apps.openauctionbase.service;

import com.axelor.apps.openauction.db.AuctionHeader;
import com.axelor.apps.openauction.db.AuctionLine;

public interface AuctionManagement {

  // PROCEDURE AddAuctionLine@1000000001(pAuctionLine@1000000000 : Record 8011401);
  void addAuctionLine(AuctionLine pAuctionLine);
  // PROCEDURE RemoveAuctionLine@1000000004(pAuctionLine@1000000000 : Record 8011401);
  void removeAuctionLine(AuctionLine pAuctionLine);
  // PROCEDURE GetSetup@1000000010();
  void getSetup();
  // PROCEDURE ChangeAuctionLot@1000000005(pAuctionLine@1000000000 : Record
  // 8011401;pXAuctionLine@1000000001 : Record 8011401);
  void changeAuctionLot(AuctionLine pAuctionLine, AuctionLine pXAuctionLine);
  // PROCEDURE ChangeActivity@1000000006(pActivityCode@1000000002 : Code[20];pMissionNo@1000000003 :
  // Code[20];pAuctionNo@1000000004 : Code[20];pLotNo@1000000005 : Code[20];pLotTemplate@1000000007
  // : Code[10];Add@1000000006 : Boolean;pAuctionLineNo@1100481000 : Integer);
  void changeActivity(
      String pActivityCode,
      String pMissionNo,
      String pAuctionNo,
      String pLotNo,
      String pLotTemplate,
      Boolean Add,
      Integer pAuctionLineNo);

  // PROCEDURE CreateContact@1000000003(pAuctionLine@1000000000 : Record 8011401;VAR
  // NewContactNo@1000000005 : Code[20]) : Boolean;
  Boolean createContact(AuctionLine pAuctionLine, String NewContactNo);
  // PROCEDURE GenerateDefaultPassingOrderNo@1000000011(pAuctionHeader@1000000000 : Record 8011400);
  void generateDefaultPassingOrderNo(AuctionHeader pAuctionHeader);
  // PROCEDURE CreateDimCode@1100281000(pAuctionHeader@1100281001 : Record 8011400);
  void createDimCode(AuctionHeader pAuctionHeader);
  // PROCEDURE CreateDimValueDimCode@1100481000(pAuctionHeader@1100281001 : Record 8011400);
  void createDimValueDimCode(AuctionHeader pAuctionHeader);
  // PROCEDURE SendLotToAuction@1100281003(VAR pLot@1000000000 : Record
  // 8011404;pAskConfirm@1100281000 : Boolean);
  void sendLotToAuction(AuctionLine pLot, Boolean pAskConfirm);
  // PROCEDURE SelectAuctionNo@1100281002(VAR pAuctionNo@1100281000 : Code[20]) : Boolean;
  Boolean selectAuctionNo(String pAuctionNo);
  // PROCEDURE CreateAuctionLine@1100281004(pAuctionNo@1100281001 : Code[20];pLotNo@1100281000 :
  // Code[20];pPassingOrderNo@1100481000 : Integer;pPassingIndexNo@1100481001 : Code[10]);
  void createAuctionLine(
      String pAuctionNo, String pLotNo, Integer pPassingOrderNo, String pPassingIndexNo);
  // PROCEDURE RetrieveLotCurrentAuction@1100481001(pLot@1100481000 : Record
  // 8011404;pAuctionLine@1100481001 : Record 8011401;pExcludeLine@1100481002 : Boolean);
  void retrieveLotCurrentAuction(AuctionLine pLot, AuctionLine pAuctionLine, Boolean pExcludeLine);
  // PROCEDURE PassAbsBidConfirm@1100481002(pPass@1100481000 : Boolean);
  void passAbsBidConfirm(Boolean pPass);
  // PROCEDURE CalcAuctSalesPersonCommission@1180113000(pAuctionNo@1180113000 : Code[20]) rAmount :
  // Decimal;
  Double calcAuctSalesPersonCommission(String pAuctionNo);
  // PROCEDURE SetToAuctionNo@1180113001(NewToAuctionNo@1180113000 : Code[20]);
  void setToAuctionNo(String NewToAuctionNo);
}
