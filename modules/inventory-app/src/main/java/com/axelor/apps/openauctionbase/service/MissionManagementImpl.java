package com.axelor.apps.openauctionbase.service;

import com.axelor.apps.openauction.db.ActivityHeader;
import com.axelor.apps.openauction.db.AuctionHeader;
import com.axelor.apps.openauction.db.Lot;
import com.axelor.apps.openauction.db.MissionHeader;
import com.axelor.apps.openauction.db.MissionLine;
import com.axelor.apps.openauction.db.MissionServiceLine;
import com.axelor.apps.openauction.db.MissionTemplate;

public class MissionManagementImpl implements MissionManagement {

  @Override
  public void transferToAuctionMission(MissionHeader pMissionHeader, MissionLine pMissionLine) {
    // TODO Auto-generated method stub

  }

  @Override
  public void duplicatMission(MissionHeader pMissionHeader) {
    // TODO Auto-generated method stub

  }

  @Override
  public Integer changeActivity(
      ActivityHeader pActivityCode,
      MissionHeader pMissionNo,
      AuctionHeader pAuctionNo,
      Lot pLotNo,
      MissionTemplate pMissionTemplate,
      Boolean Add,
      Integer pTransactionLineNo) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Boolean ifAllMissionServiceInvoiced(MissionHeader pMissionHeader) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Boolean ifAllTodoMissionCompleted(MissionHeader pMissionHeader) {
    // TODO Auto-generated method stub

    return null;
  }

  @Override
  public void saveChangeStatus(MissionHeader pXMissionHeader, MissionHeader pMissionHeader) {
    // TODO Auto-generated method stub

  }

  @Override
  public String transferToOtherMission(MissionHeader pMissionHeader, MissionLine pMissionLine) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Boolean allowTransferMissionLine(MissionLine pMissionLine) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Integer searchMissionSameContact(MissionHeader pMissionHeader) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String transToNewMissionWithTemplate(
      MissionHeader pMissionHeader, MissionHeader pNewMissionTemplate) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void createMissLineActWithTemplate(MissionHeader pMissionHeader) {
    // TODO Auto-generated method stub

  }

  @Override
  public void createContactByMission(
      MissionHeader pOldMissionHeader, MissionHeader pNewMissionheader) {
    // TODO Auto-generated method stub

  }

  @Override
  public void transferMissLineAct(MissionLine pOldMissionLine, MissionLine pNewMissionLine) {
    // TODO Auto-generated method stub

  }

  @Override
  public Boolean hasLotCustomsImport(String pMissionNo) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getInvTariffScale(MissionHeader pMissionHeader) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void renumLotNoInMission(MissionHeader pMissionHeader) {
    // TODO Auto-generated method stub

  }

  @Override
  public MissionServiceLine calcJudInvService(MissionServiceLine pMissionServiceLine) {
    // TODO prioritaire
    return pMissionServiceLine;
  }

  @Override
  public void loadJudInvServiceBuffer(
      MissionHeader pMissionHeader, Double[] pRoof, String pMidValueCalc) {
    // TODO Auto-generated method stub

  }

  @Override
  public void loadJudInvTotalPerc(MissionHeader pTariffScaleLevels, Double[] pTotalPerc) {
    // TODO Auto-generated method stub

  }

  @Override
  public void loadJudInvRoof(MissionHeader pTariffScaleLevels, Double[] pRoof) {
    // TODO Auto-generated method stub

  }

  @Override
  public void loadJudInvTitleCols(MissionHeader pTariffScaleLevels, String[] pTitleCols) {
    // TODO Auto-generated method stub

  }

  @Override
  public Boolean judInvDuplicateMission(
      MissionHeader pMissionHeader, Boolean pOpenForm, Boolean pRecol) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public MissionLine createInvAuctionMission(
      MissionHeader pMissionHeader, Boolean pShowForm, MissionLine pMissionLineTemp) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public MissionLine createInvEquipmentMission(
      MissionHeader pMissionHeader, Boolean pShowForm, MissionLine pMissionLineTemp) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void dupInvMissionWitOtherTempl(MissionHeader pMissionHeader, Boolean pOpenForm) {
    // TODO Auto-generated method stub

  }

  @Override
  public Boolean checkIfInvoice(String pMissionHeaderNo) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void changeActivityForMissionHeader(MissionHeader pMissionHeader) {
    // TODO Auto-generated method stub

  }

  @Override
  public Double getOperatingBase(MissionLine pMissionLine, String pValueCalc, String pRetExplain) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void movesLines(
      MissionLine pMissionLineSelection, MissionLine pToMissionLine, Boolean pUp) {
    // TODO Auto-generated method stub

  }

  @Override
  public void getMissionJudInvScale(String pMissionNo, MissionServiceLine pMissionServiceLine) {
    // TODO Auto-generated method stub

  }

  @Override
  public void transferInvLines(String pOriginMissionNo, String pDestMissionNo) {
    // TODO Auto-generated method stub

  }

  @Override
  public void excludeClass(MissionLine pMissionLine) {
    // TODO Auto-generated method stub

  }

  @Override
  public void transferInvDescription(MissionLine pXMissionLine, MissionLine pMissionLine) {
    // TODO Auto-generated method stub

  }

  @Override
  public Double calcSalespersonCommission(
      String pSalespersonCode, String pAuctionNo, String pMissionNo) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void loadJudInvTitleCols2(MissionHeader pTariffScaleLevels, String[] pTitleCols) {
    // TODO Auto-generated method stub

  }
}
