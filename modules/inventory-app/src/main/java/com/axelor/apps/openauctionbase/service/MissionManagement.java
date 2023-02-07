package com.axelor.apps.openauctionbase.service;

import com.axelor.apps.openauction.db.ActivityHeader;
import com.axelor.apps.openauction.db.AuctionHeader;
import com.axelor.apps.openauction.db.Lot;
import com.axelor.apps.openauction.db.MissionHeader;
import com.axelor.apps.openauction.db.MissionLine;
import com.axelor.apps.openauction.db.MissionServiceLine;
import com.axelor.apps.openauction.db.MissionTemplate;

public interface MissionManagement {
  // PROCEDURE TransferToAuctionMission@1000000006(VAR pMissionHeader@1000000006 : Record
  // 8011402;VAR pMissionLine@1000000003 : Record 8011403);
  public void transferToAuctionMission(MissionHeader pMissionHeader, MissionLine pMissionLine);
  // PROCEDURE DuplicatMission@1000000007(VAR pMissionHeader@1000000000 : Record 8011402);
  public void duplicatMission(MissionHeader pMissionHeader);
  // PROCEDURE ChangeActivity@1000000008(pActivityCode@1000000002 : Code[20];pMissionNo@1000000003 :
  // Code[20];pAuctionNo@1000000004 : Code[20];pLotNo@1000000005 :
  // Code[20];pMissionTemplate@1000000007 : Code[10];Add@1000000006 :
  // Boolean;pTransactionLineNo@1100481000 : Integer);
  public Integer changeActivity(
      ActivityHeader pActivityCode,
      MissionHeader pMissionNo,
      AuctionHeader pAuctionNo,
      Lot pLotNo,
      MissionTemplate pMissionTemplate,
      Boolean Add,
      Integer pTransactionLineNo);
  // PROCEDURE IfAllMissionServiceInvoiced@1000000013(pMissionHeader@1000000000 : Record 8011402)
  // rValue : Boolean;
  public Boolean ifAllMissionServiceInvoiced(MissionHeader pMissionHeader);
  // PROCEDURE IfAllTodoMissionCompleted@1000000015(pMissionHeader@1000000000 : Record 8011402)
  // rValue : Boolean;
  public Boolean ifAllTodoMissionCompleted(MissionHeader pMissionHeader);
  // PROCEDURE SaveChangeStatus@1000000014(pXMissionHeader@1000000000 : Record
  // 8011402;pMissionHeader@1000000002 : Record 8011402);
  public void saveChangeStatus(MissionHeader pXMissionHeader, MissionHeader pMissionHeader);

  // PROCEDURE TransferToOtherMission@1100481006(VAR pMissionHeader@1000000006 : Record 8011402;VAR
  // pMissionLine@1000000003 : Record 8011403) "rMission Header" : Code[20];
  public String transferToOtherMission(MissionHeader pMissionHeader, MissionLine pMissionLine);
  // PROCEDURE AllowTransferMissionLine@1100481000(VAR pMissionLine@1100481000 : Record 8011403)
  // rValue : Boolean;
  public Boolean allowTransferMissionLine(MissionLine pMissionLine);
  // PROCEDURE SearchMissionSameContact@1100481002(VAR pMissionHeader@1100481000 : Record 8011402)
  // rCounterNb : Integer;
  public Integer searchMissionSameContact(MissionHeader pMissionHeader);
  // PROCEDURE TransToNewMissionWithTemplate@1100481001(VAR pMissionHeader@1100481000 : Record
  // 8011402;VAR pNewMissionTemplate@1100481001 : Record 8011419) rNewMissionNo : Code[20];
  public String transToNewMissionWithTemplate(
      MissionHeader pMissionHeader, MissionHeader pNewMissionTemplate);
  // PROCEDURE CreateMissLineActWithTemplate@1100481003(VAR pMissionHeader@1100481001 : Record
  // 8011402);
  public void createMissLineActWithTemplate(MissionHeader pMissionHeader);
  // PROCEDURE CreateContactByMission@1100481005(VAR pOldMissionHeader@1100481000 : Record
  // 8011402;VAR pNewMissionheader@1100481003 : Record 8011402);
  public void createContactByMission(
      MissionHeader pOldMissionHeader, MissionHeader pNewMissionheader);
  // PROCEDURE TransferMissLineAct@1100481010(VAR pOldMissionLine@1100481003 : Record 8011403;VAR
  // pNewMissionLine@1100481000 : Record 8011403);
  public void transferMissLineAct(MissionLine pOldMissionLine, MissionLine pNewMissionLine);
  // PROCEDURE HasLotCustomsImport@1100481004(pMissionNo@1100481000 : Code[20]) : Boolean;
  public Boolean hasLotCustomsImport(String pMissionNo);
  // PROCEDURE GetInvTariffScale@1100481007(pMissionHeader@1100481000 : Record 8011402) rScale :
  // Code[10];
  public String getInvTariffScale(MissionHeader pMissionHeader);
  // PROCEDURE RenumLotNoInMission@1100481008(VAR pMissionHeader@1100481000 : Record 8011402);
  public void renumLotNoInMission(MissionHeader pMissionHeader);
  // PROCEDURE CalcJudInvService@1180113001(VAR pMissionServiceLine@1100481000 : Record 8011449);
  public MissionServiceLine calcJudInvService(MissionServiceLine pMissionServiceLine);
  // PROCEDURE LoadJudInvServiceBuffer@1180113002(VAR pMissionHeader@1100481000 : Record 8011402;VAR
  // pRoof@1100481001 : ARRAY [4] OF Decimal;VAR pUnivBuffer@1100481002 : TEMPORARY Record
  // 8011709;pMidValueCalc@1180113000 : '0,1,2,3,4');
  public void loadJudInvServiceBuffer(
      MissionHeader pMissionHeader, Double[] pRoof, String pMidValueCalc);
  // PROCEDURE LoadJudInvTotalPerc@1100481023(VAR pTariffScaleLevels@1100481000 : Record 8011426;VAR
  // pTotalPerc@1100481002 : ARRAY [4] OF Decimal);
  public void loadJudInvTotalPerc(MissionHeader pTariffScaleLevels, Double[] pTotalPerc);
  // PROCEDURE LoadJudInvRoof@1100481035(VAR pTariffScaleLevels@1100481000 : Record 8011426;VAR
  // pRoof@1100481001 : ARRAY [4] OF Decimal);
  public void loadJudInvRoof(MissionHeader pTariffScaleLevels, Double[] pRoof);
  // PROCEDURE LoadJudInvTitleCols@1100481017(VAR pTariffScaleLevels@1100481000 : Record 8011426;VAR
  // pTitleCols@1100481001 : ARRAY [4] OF Text[50]);
  public void loadJudInvTitleCols(MissionHeader pTariffScaleLevels, String[] pTitleCols);
  // PROCEDURE JudInvDuplicateMission@1100481011(pMissionHeader@1100481000 : Record
  // 8011402;pOpenForm@1100481006 : Boolean;pRecol@1100481007 : Boolean) : Boolean;
  public Boolean judInvDuplicateMission(
      MissionHeader pMissionHeader, Boolean pOpenForm, Boolean pRecol);
  // PROCEDURE CreateInvAuctionMission@1100481013(pMissionHeader@1100481000 : Record
  // 8011402;pShowForm@1100481007 : Boolean;VAR pMissionLineTemp@1180113000 : TEMPORARY Record
  // 8011403);
  public MissionLine createInvAuctionMission(
      MissionHeader pMissionHeader, Boolean pShowForm, MissionLine pMissionLineTemp);
  // PROCEDURE CreateInvEquipmentMission@1180113004(pMissionHeader@1100481000 : Record
  // 8011402;pShowForm@1100481007 : Boolean;VAR pMissionLineTemp@1180113002 : TEMPORARY Record
  // 8011403);
  public MissionLine createInvEquipmentMission(
      MissionHeader pMissionHeader, Boolean pShowForm, MissionLine pMissionLineTemp);
  // PROCEDURE DupInvMissionWitOtherTempl@1100481014(pMissionHeader@1100481001 : Record
  // 8011402;pOpenForm@1100481000 : Boolean);
  public void dupInvMissionWitOtherTempl(MissionHeader pMissionHeader, Boolean pOpenForm);
  // PROCEDURE CheckIfInvoice@1100481042(pMissionHeaderNo@1100481000 : Code[20]) rExistInvoice :
  // Boolean;
  public Boolean checkIfInvoice(String pMissionHeaderNo);
  // PROCEDURE ChangeActivityForMissionHeader@1100481015(VAR pMissionHeader@1100481001 : Record
  // 8011402);
  public void changeActivityForMissionHeader(MissionHeader pMissionHeader);
  // PROCEDURE GetOperatingBase@1100481019(VAR pMissionLine@1100481000 : Record
  // 8011403;pValueCalc@1180113000 : '0,1,2,3,4';VAR pRetExplain@1180113001 : Text[250])
  // rOperatingBase : Decimal;
  public Double getOperatingBase(MissionLine pMissionLine, String pValueCalc, String pRetExplain);
  // PROCEDURE MovesLines@1100481016(VAR pMissionLineSelection@1100481000 : Record 8011403;VAR
  // pToMissionLine@1100481001 : Record 8011403;pUp@1180113011 : Boolean);
  public void movesLines(
      MissionLine pMissionLineSelection, MissionLine pToMissionLine, Boolean pUp);
  // PROCEDURE GetMissionJudInvScale@1180113003(VAR pMissionNo@1180113002 : Code[20];VAR
  // pMissionServiceLine@1180113000 : Record 8011449);
  public void getMissionJudInvScale(String pMissionNo, MissionServiceLine pMissionServiceLine);
  // PROCEDURE TransferInvLines@1180113005(pOriginMissionNo@1180113000 :
  // Code[20];pDestMissionNo@1180113001 : Code[20]);
  public void transferInvLines(String pOriginMissionNo, String pDestMissionNo);
  // PROCEDURE ExcludeClass@1180113008(VAR pMissionLine@1180113000 : Record 8011403);
  public void excludeClass(MissionLine pMissionLine);
  // PROCEDURE TransferInvDescription@1100281001(VAR pXMissionLine@1100481002 : Record 8011403;VAR
  // pMissionLine@1180113000 : Record 8011403);
  public void transferInvDescription(MissionLine pXMissionLine, MissionLine pMissionLine);
  // PROCEDURE CalcSalespersonCommission@1180113006(pSalespersonCode@1180113000 :
  // Code[20];pAuctionNo@1180113001 : Code[20];pMissionNo@1180113002 : Code[20]) rAmount : Decimal;
  public Double calcSalespersonCommission(
      String pSalespersonCode, String pAuctionNo, String pMissionNo);
  // PROCEDURE LoadJudInvTitleCols2@1180113007(VAR pTariffScaleLevels@1100481000 : Record
  // 8011426;VAR pTitleCols@1100481001 : ARRAY [4] OF Text[50]);
  public void loadJudInvTitleCols2(MissionHeader pTariffScaleLevels, String[] pTitleCols);
}
