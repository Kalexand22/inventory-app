package com.axelor.apps.openauctionbase.service;

import com.axelor.apps.base.db.Partner;
import com.axelor.apps.base.db.Product;
import com.axelor.apps.openauction.db.AccesBuffer;
import com.axelor.apps.openauction.db.Lot;
import com.axelor.apps.openauction.db.MissionContactPriceGroup;
import com.axelor.apps.openauction.db.MissionHeader;
import com.axelor.apps.openauction.db.MissionLotPriceGroup;
import com.axelor.apps.openauction.db.MissionServiceLine;
import com.axelor.apps.openauction.db.MissionServicePrice;
import com.axelor.apps.openauction.db.MissionTemplate;
import com.axelor.apps.openauction.db.TariffScale;
import com.axelor.exception.AxelorException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface MissionServicePriceManagement {
  // PROCEDURE FindMissionServicePrice@1000000001(VAR pMissionServiceLine@1000000000 : Record
  // 8011449;pEstimated@1000000004 : Boolean);
  public MissionServiceLine findMissionServicePrice(
      MissionServiceLine pMissionServiceLine, Boolean pEstimated) throws AxelorException;
  // PROCEDURE FindMissServPWithBaseAmount@1100481001(VAR pMissionServiceLine@1000000000 : Record
  // 8011449;pBaseAmount@1100481000 : Decimal;pEstimated@1000000004 : Boolean);
  public void findMissServPWithBaseAmount(
      MissionServiceLine pMissionServiceLine, Double pBaseAmount, Boolean pEstimated);
  // PROCEDURE TestPrice@1000000008(VAR pServiceMissionPrice@1000000008 : Record
  // 8011469;DocTemplate@1000000006 : Code[20];DocNo@1000000005 : Code[20];LotGroup@1000000004 :
  // Code[10];LotCode@1000000003 : Code[20];ContactGroup@1000000002 :
  // Code[10];ContactCode@1000000001 : Code[20]) : Boolean;
  public Boolean testPrice(
      MissionServicePrice pServiceMissionPrice,
      MissionTemplate DocTemplate,
      MissionHeader DocNo,
      MissionLotPriceGroup LotGroup,
      Lot LotCode,
      MissionContactPriceGroup ContactGroup,
      Partner ContactCode);
  // PROCEDURE GetLotAmount@1000000005(pMissionHeaderNo@1000000001 : Code[20];pLotNo@1000000000 :
  // Code[20]);
  public void getLotAmount(MissionHeader pMissionHeaderNo, Lot pLotNo) throws AxelorException;
  // PROCEDURE GetMissionAmount@1100481008(pMissionHeaderNo@1100481000 : Code[20]);
  public void getMissionAmount(MissionHeader pMissionHeaderNo) throws AxelorException;
  // PROCEDURE GetTempServiceMissionPrice@1000000006(VAR pMissionServicePrice@1000000000 : Record
  // 8011469);
  public void getTempServiceMissionPrice(MissionServiceLine pMissionServicePrice);
  // PROCEDURE GetTariffScaleAmount@1000000009(pAmount@1000000002 : Decimal;pTariffScale@1000000001
  // : Code[20];VAR pAccesBuffer@1000000000 : Record 8011477);
  public List<AccesBuffer> getTariffScaleAmount(BigDecimal pAmount, TariffScale pTariffScale);
  // PROCEDURE GetTariffScaleDetail@1000000002(VAR pAccesBuffer@1000000000 : Record 8011477);
  public List<AccesBuffer> getTariffScaleDetail();
  // PROCEDURE GetBaseAMount@1000000004() : Decimal;
  public Double getBaseAMount();
  // PROCEDURE SearchBidLine@1000000007(pMissionHeaderNo@1000000002 : Code[20];pLotNo@1000000000 :
  // Code[20];VAR pBidMissionServiceLine@1000000001 : Record 8011449);
  public MissionServiceLine searchBidLine(MissionHeader pMissionHeaderNo, Lot pLotNo);
  // PROCEDURE ModifyPrice@1100481002(VAR pRec@1100481000 : Record 8011469;VAR pXRec@1100481001 :
  // Record 8011469;pAction@1100481002 : 'OnModify,OnInsert,OnDelete,OnRename');
  public void modifyPrice(MissionServiceLine pRec, MissionServiceLine pXRec, String pAction);
  // PROCEDURE ApplyNewPrice@1100481000(VAR pMissionServicePrice@1100481000 : Record
  // 8011469;pAction@1100481001 : 'OnModify,OnInsert,OnDelete,OnRename');
  public void applyNewPrice(MissionServiceLine pMissionServicePrice, String pAction);
  // PROCEDURE GetUsedServPriceForMission@1100481003(pMissionNo@1100481001 : Code[20];VAR
  // pMissionServicePrice@1100481000 : Record 8011469);
  public void getUsedServPriceForMission(
      MissionHeader pMissionNo, MissionServiceLine pMissionServicePrice);
  // PROCEDURE MarkMissionServPrice@1100481016(VAR pMissionServiceLine@1100481004 : Record
  // 8011449;VAR pMissionServicePrice@1100481001 : Record 8011469);
  public void markMissionServPrice(
      MissionServiceLine pMissionServiceLine, MissionServiceLine pMissionServicePrice);
  // PROCEDURE GetServPriceByMissServLine@1100481004(VAR pMissionServiceLine@1100481000 : Record
  // 8011449;VAR pMissionServicePrice@1100481001 : Record 8011469);
  public void getServPriceByMissServLine(
      MissionServiceLine pMissionServiceLine, MissionServiceLine pMissionServicePrice);
  // PROCEDURE GetServPriceByLotNoItemNo@1100481005(pLotNo@1100481000 : Code[20];pItemNo@1100481001
  // : Code[20];VAR pMissionServicePrice@1100481002 : Record 8011469);
  public void getServPriceByLotNoItemNo(
      Lot pLotNo, Product pItemNo, MissionServiceLine pMissionServicePrice);
  // PROCEDURE GetPriceDate@1100481007(pMissServLine@1100481000 : Record 8011449) ReturnValue :
  // Date;
  public Date getPriceDate(MissionServiceLine pMissServLine);
  // PROCEDURE VATConvert@1180113000(pItem@1180113000 : Record 27;VAR pMissServLine@1180113002 :
  // Record 8011449);
  public MissionServiceLine VATConvert(Product pItem, MissionServiceLine pMissServLine);
  // PROCEDURE SetHideDialog@1180113001(pHideDialog@1180113000 : Boolean);

  // PROCEDURE GetSellerComByAuctionLine@1180113002(VAR pAuctionLine@1180113000 : Record 8011401;VAR
  // pCalcType@1180113005 : 'UnitPrice,Service%,Commission Scale';VAR "pService%"@1180113006 :
  // Decimal;VAR pUnitPrice@1180113008 : Decimal) rReturnText : Text[250];
  public String getSellerComByAuctionLine(
      MissionServiceLine pAuctionLine, String pCalcType, Double pService, Double pUnitPrice);
}
