package com.axelor.apps.openauctionbase.service;

import com.axelor.apps.base.db.Partner;
import com.axelor.apps.base.db.TradingName;
import com.axelor.apps.openauction.db.MissionHeader;

public class ToolsMissionChangeImpl implements ToolsMissionChange {

  @Override
  public Boolean isFormLocked(MissionHeader pMissionNo) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Boolean allowMissionVATBusGrpChange(MissionHeader pMissionHeader, Boolean pRaiseError) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Boolean allowMissionLineVATBusGrChange(MissionHeader pMissionHeader, Boolean pRaiseError) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Boolean allowMasterContactChange(MissionHeader pMissionHeader, Boolean pRaiseError) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Boolean allowMissionGenBusGrpChange(MissionHeader pMissionHeader, Boolean pRaiseError) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Boolean allowRespCenterChange(MissionHeader pMissionHeader, Boolean pRaiseError) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public MissionHeader updateMissionVATBusGroup(MissionHeader pMissionHeader) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public MissionHeader updateMissionLineVATBusGroup(
      MissionHeader pMissionHeader, String pNewVATGroup) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public MissionHeader updateMissionMasterContact(
      MissionHeader pMissionHeader, Partner pNewContact) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public MissionHeader updateMissionGenBusGrpChange(
      MissionHeader pMissionHeader, String pNewGenBusGrp) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public MissionHeader updateRespCenterChange(
      MissionHeader pMissionHeader, TradingName pNewRespCenter) {
    // TODO Auto-generated method stub
    return null;
  }
}
