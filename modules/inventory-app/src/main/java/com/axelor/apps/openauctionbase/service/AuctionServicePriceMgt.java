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

public interface AuctionServicePriceMgt {
  // PROCEDURE FindAuctionServicePrice@1000000001(VAR pMissionServiceLine@1000000000 : Record
  // 8011449;pBidPrice@1000000006 : Decimal;pEstimated@1000000004 : Boolean);
  public void findAuctionServicePrice(
      MissionServiceLine pMissionServiceLine, BigDecimal pBidPrice, Boolean pEstimated);
  // PROCEDURE UpdateAuctionServicePrice@1100281003(VAR pMissionServiceLine@1000000000 : Record
  // 8011449;pEstimated@1000000004 : Boolean);
  public MissionServiceLine updateAuctionServicePrice(
      MissionServiceLine pMissionServiceLine, Boolean pEstimated);
  // PROCEDURE TestPrice@1000000008(pServicePrice@1000000008 : Record 8011427;DocTemplate@1000000006
  // : Code[20];DocNo@1000000005 : Code[20];LotGroup@1000000004 : Code[10];LotCode@1000000003 :
  // Code[20];ContactGroup@1000000002 : Code[10];ContactCode@1000000001 : Code[20]) : Boolean;
  public Boolean testPrice(
      Object pServicePrice,
      String DocTemplate,
      String DocNo,
      String LotGroup,
      String LotCode,
      String ContactGroup,
      String ContactCode);
  // PROCEDURE GetAmount@1000000005(pMissionHeaderNo@1000000001 : Code[20];pLotNo@1000000000 :
  // Code[20]);
  public void getAmount(MissionHeader pMissionHeaderNo, Lot pLotNo);
  // PROCEDURE GetTempServiceMissionPrice@1000000006(VAR pMissionServicePrice@1000000000 : Record
  // 8011469);
  public void getTempServiceMissionPrice(MissionServicePrice pMissionServicePrice);
  // PROCEDURE GetTariffScaleAmount@1000000009(pAmount@1000000002 : Decimal;pTariffScale@1000000001
  // : Code[20];VAR pAccesBuffer@1000000006 : Record 8011477);
  public void getTariffScaleAmount(
      BigDecimal pAmount, TariffScale pTariffScale, Object pAccesBuffer);
  // PROCEDURE GetTariffScaleDetail@1000000002(VAR pAccesBuffer@1000000001 : Record 8011477);
  public void getTariffScaleDetail(Object pAccesBuffer);
  // PROCEDURE GetBaseAMount@1000000004() : Decimal;
  public BigDecimal getBaseAMount();
  // PROCEDURE SearchBidLine@1000000007(pAuctionHeaderNo@1000000002 : Code[20];pLineNo@1100481000 :
  // Integer;pLotNo@1000000000 : Code[20];VAR pBidMissionServiceLine@1000000001 : Record 8011449);
  public void searchBidLine(
      AuctionHeader pAuctionHeaderNo,
      Integer pLineNo,
      Lot pLotNo,
      MissionServiceLine pBidMissionServiceLine);
  // PROCEDURE ModifyPrice@1100481002(VAR pRec@1100481000 : Record 8011427;VAR pXRec@1100481001 :
  // Record 8011427;pAction@1100481002 : 'OnModify,OnInsert,OnDelete,OnRename');
  public void modifyPrice(AuctionServicePrice pRec, AuctionServicePrice pXRec, String pAction);
  // PROCEDURE ApplyNewPrice@1100481000(VAR pAuctionServicePrice@1100481000 : Record
  // 8011427;pAction@1100481001 : 'OnModify,OnInsert,OnDelete,OnRename');
  public void applyNewPrice(AuctionServicePrice pAuctionServicePrice, String pAction);
  // PROCEDURE GetUsedServPriceForAuction@1100481003(pAuctionNo@1100481001 : Code[20];VAR
  // pAuctionServicePrice@1100481000 : Record 8011427);
  public void getUsedServPriceForAuction(
      AuctionHeader pAuctionNo, AuctionServicePrice pAuctionServicePrice);
  // PROCEDURE MarkAuctionServPrice@1100481016(VAR pMissionServiceLine@1100481004 : Record
  // 8011449;VAR pAuctionServicePrice@1100481001 : Record 8011427);
  public void markAuctionServPrice(
      MissionServiceLine pMissionServiceLine, AuctionServicePrice pAuctionServicePrice);
  // PROCEDURE GetServPriceByMissServLine@1100481004(VAR pMissionServiceLine@1100481000 : Record
  // 8011449;VAR pAuctionServicePrice@1100481001 : Record 8011427);
  public void getServPriceByMissServLine(
      MissionServiceLine pMissionServiceLine, AuctionServicePrice pAuctionServicePrice);
  // PROCEDURE GetServPriceByLotNoItemNo@1100481005(pLotNo@1100481000 : Code[20];pItemNo@1100481001
  // : Code[20];VAR pAuctionServicePrice@1100481002 : Record 8011427);
  public void getServPriceByLotNoItemNo(
      Lot pLotNo, String pItemNo, AuctionServicePrice pAuctionServicePrice);
  // PROCEDURE GetPriceDate@1100481007(pMissServLine@1100481000 : Record 8011449) ReturnValue :
  // Date;
  public java.util.Date getPriceDate(MissionServiceLine pMissServLine);
  // PROCEDURE VATConvert@1180113000(pItem@1180113000 : Record 27;VAR pMissServLine@1180113002 :
  // Record 8011449);
  public void VATConvert(Product pItem, MissionServiceLine pMissServLine);
  // PROCEDURE GetBuyerCommisionByAuctionLine@1180113001(VAR pAuctionLine@1180113000 : Record
  // 8011401;pReturnTariffScaleCode@1180113005 : Boolean;pReturnIncVAT@1180113006 : Boolean)
  // rReturnText : Text[250];
  public String getBuyerCommisionByAuctionLine(
      AuctionLine pAuctionLine, Boolean pReturnTariffScaleCode, Boolean pReturnIncVAT);
  // PROCEDURE CalcIncVATPrice@1180113002(pReturnIncVAT@1180113000 : Boolean;pPriceIncVAT@1180113001
  // : Boolean;pPrice@1180113002 : Decimal;"p%VAT"@1180113003 : Decimal) rReturnValue : Decimal;
  public BigDecimal calcIncVATPrice(
      Boolean pReturnIncVAT, Boolean pPriceIncVAT, BigDecimal pPrice, BigDecimal pVAT);
  // PROCEDURE GetBuyerCommisionVATRate@1180113003(VAR pAuctionLine@1180113000 : Record 8011401)
  // rVATRate : Decimal;
  public BigDecimal getBuyerCommisionVATRate(AuctionLine pAuctionLine);
  // PROCEDURE CalcBuyerAuctionComVATRate@1180113004(VAR pLot@1180113001 : Record 8011404) :
  // Decimal;
  public BigDecimal calcBuyerAuctionComVATRate(Lot pLot);
}
