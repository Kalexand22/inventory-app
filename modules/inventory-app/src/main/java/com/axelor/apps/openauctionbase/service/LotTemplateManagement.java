package com.axelor.apps.openauctionbase.service;

import com.axelor.apps.base.db.Partner;
import com.axelor.apps.openauction.db.Lot;
import com.axelor.apps.openauction.db.LotInputJournal;
import com.axelor.apps.openauction.db.LotQuickInputJournal;
import com.axelor.apps.openauction.db.MissionHeader;
import com.axelor.apps.openauction.db.MissionLine;
import com.axelor.exception.AxelorException;
import java.util.Date;

public interface LotTemplateManagement {
  // PROCEDURE CreateLot@1000000016(VAR pLotQuickInputJournal@1000000019 : Record 8011467;VAR
  // pNewLot@1000000006 : Record 8011404;pContactNo@1000000004 : Code[20]);
  public Lot createLot(LotQuickInputJournal pLotQuickInputJournal, Partner pContactNo)
      throws AxelorException;
  // PROCEDURE CreateLotFromContact@1000000005(VAR pLotQuickInputJournal@1000000001 : Record
  // 8011467;pContact@1000000000 : Record 5050);
  public void createLotFromContact(LotQuickInputJournal pLotQuickInputJournal, Partner pContact)
      throws AxelorException;
  // PROCEDURE CreateLotFromMission@1000000003(VAR pLotQuickInputJournal@1000000006 : Record
  // 8011467;pMissionHeader@1000000005 : Record 8011402);
  public Lot createLotFromMission(
      LotQuickInputJournal pLotQuickInputJournal, MissionHeader pMissionHeader)
      throws AxelorException;
  // PROCEDURE PostLotQuickInputFromMission@1000000017(VAR pLotQuickInputJournal@1000000006 : Record
  // 8011467;pMissionHeader@1000000005 : Record 8011402);
  public void postLotQuickInputFromMission(
      LotQuickInputJournal pLotQuickInputJournal, MissionHeader pMissionHeader)
      throws AxelorException;
  // PROCEDURE CreateLotFromAuction@1000000015(VAR pLotQuickInputJournal@1000000001 : Record
  // 8011467) : Code[20];
  public String createLotFromAuction(LotQuickInputJournal pLotQuickInputJournal);
  // PROCEDURE CreateLotTemplate@1000000000(VAR pLotTemplate@1000000004 : Record
  // 8011411;pTypeOrigin@1000000008 : 'List,Card');
  public void createLotTemplate(Lot pLotTemplate, String pTypeOrigin);
  // PROCEDURE CreateLotValueEntry@1000000018(VAR pLotQuickInputJournal@1000000000 : Record
  // 8011467;pLot@1000000004 : Record 8011404;pMissionHeader@1000000001 : Record
  // 8011402;pMissionLine@1000000002 : Record 8011403);
  public void createLotValueEntry(
      LotQuickInputJournal pLotQuickInputJournal,
      Lot pLot,
      MissionHeader pMissionHeader,
      MissionLine pMissionLine)
      throws AxelorException;
  // PROCEDURE CreateLotValueEntryReserve@1100481000(VAR pLotQuickInputJournal@1000000000 : Record
  // 8011467;pLot@1000000004 : Record 8011404;pMissionHeader@1000000001 : Record
  // 8011402;pMissionLine@1000000002 : Record 8011403);
  public void createLotValueEntryReserve(
      LotQuickInputJournal pLotQuickInputJournal,
      Lot pLot,
      MissionHeader pMissionHeader,
      MissionLine pMissionLine)
      throws AxelorException;
  // PROCEDURE CreateLotValueEntryEstimate@1100481001(VAR pLotQuickInputJournal@1000000000 : Record
  // 8011467;pLot@1000000004 : Record 8011404;pMissionHeader@1000000001 : Record
  // 8011402;pMissionLine@1000000002 : Record 8011403);
  public void createLotValueEntryEstimate(
      LotQuickInputJournal pLotQuickInputJournal,
      Lot pLot,
      MissionHeader pMissionHeader,
      MissionLine pMissionLine)
      throws AxelorException;
  // PROCEDURE CreateLotValueEntryAuctEstim@1180113000(VAR pLotQuickInputJournal@1000000000 : Record
  // 8011467;pLot@1000000004 : Record 8011404;pMissionHeader@1000000001 : Record
  // 8011402;pMissionLine@1000000002 : Record 8011403);
  public void createLotValueEntryAuctEstim(
      LotQuickInputJournal pLotQuickInputJournal,
      Lot pLot,
      MissionHeader pMissionHeader,
      MissionLine pMissionLine)
      throws AxelorException;
  // PROCEDURE CreateLotValueEntryAppraisal@1100481002(VAR pLotQuickInputJournal@1000000000 : Record
  // 8011467;pLot@1000000004 : Record 8011404;pMissionHeader@1000000001 : Record
  // 8011402;pMissionLine@1000000002 : Record 8011403);
  public void createLotValueEntryAppraisal(
      LotQuickInputJournal pLotQuickInputJournal,
      Lot pLot,
      MissionHeader pMissionHeader,
      MissionLine pMissionLine)
      throws AxelorException;
  // PROCEDURE CreateLotValueEntryQuotation@1100481010(VAR pLotQuickInputJournal@1000000000 : Record
  // 8011467;pLot@1000000004 : Record 8011404;pMissionHeader@1000000001 : Record
  // 8011402;pMissionLine@1000000002 : Record 8011403);
  public void createLotValueEntryQuotation(
      LotQuickInputJournal pLotQuickInputJournal,
      Lot pLot,
      MissionHeader pMissionHeader,
      MissionLine pMissionLine)
      throws AxelorException;
  // PROCEDURE CreateLotInventoryEntry@1000000004(VAR pLotQuickInputJournal@1000000003 : Record
  // 8011467;pLot@1000000002 : Record 8011404);
  public void createLotInventoryEntry(LotQuickInputJournal pLotQuickInputJournal, Lot pLot);
  // PROCEDURE CalcGrossReserveByNetReserve@1100481003(VAR pLotInputJournal@1100481000 : Record
  // 8011498);
  public void calcGrossReserveByNetReserve(LotInputJournal pLotInputJournal) throws Exception;
  // PROCEDURE CalcNetReserveByGrossReserve@1100481004(VAR pLotInputJournal@1100481000 : Record
  // 8011498);
  public void calcNetReserveByGrossReserve(LotInputJournal pLotInputJournal) throws AxelorException;
  // PROCEDURE CalcCommissionWithBaseAmount@1100481005(VAR pLotInputJournal@1100481000 : Record
  // 8011498;pBaseAmount@1100481001 : Decimal) rAmountCommission : Decimal;
  public java.math.BigDecimal calcCommissionWithBaseAmount(
      LotInputJournal pLotInputJournal, java.math.BigDecimal pBaseAmount);
  // PROCEDURE CreateLotFromJudicialMission@1100481006(VAR pOriginMissionLine@1100481000 : Record
  // 8011403;VAR pLotQuickInputJournal@1000000001 : Record 8011467) : Code[20];
  public String createLotFromJudicialMission(
      MissionLine pOriginMissionLine, LotQuickInputJournal pLotQuickInputJournal);
  // PROCEDURE PostLotQuickInputFromJuMission@1100481009(VAR pLotQuickInputJournal@1000000006 :
  // Record 8011467;VAR pOriginMissionLine@1000000005 : Record 8011403);
  public void postLotQuickInputFromJuMission(
      LotQuickInputJournal pLotQuickInputJournal, MissionLine pOriginMissionLine);
  // PROCEDURE CreateLotInvValueEntry@1100481007(pMissionLine@1000000002 : Record 8011403);
  public void createLotInvValueEntry(MissionLine pMissionLine);
  // PROCEDURE CreateLotValueEntryFromLot@1100481008(pLotNo@1000000004 :
  // Code[20];pPostingDate@1000000001 : Date;pEntryType@1000000002 : 'Estimate,Seller
  // Estimate,Appraisal,Bid Price,Inventory,Reserve
  // Price,Auction,Acquisition,Quotation,GuaranteedPrice';pAmount@1000000000 :
  // Decimal;pMinAmount@1000000005 : Decimal;pMaxAmount@1000000007 : Decimal;pContactNo@1100481001 :
  // Code[20]);
  public void createLotValueEntryFromLot(
      String pLotNo,
      Date pPostingDate,
      String pEntryType,
      java.math.BigDecimal pAmount,
      java.math.BigDecimal pMinAmount,
      java.math.BigDecimal pMaxAmount,
      Partner pContactNo);
  // PROCEDURE SetToRegistIntegrationLot@1180113001(pToRegistIntegrationLot@1180113000 :
  // Boolean;pRegistIntegrationEntryNo@1180113001 : Integer);
  public void setToRegistIntegrationLot(
      Boolean pToRegistIntegrationLot, Integer pRegistIntegrationEntryNo);

  public Lot getLotNoCreated();
}
