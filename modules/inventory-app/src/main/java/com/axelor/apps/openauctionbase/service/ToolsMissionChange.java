package com.axelor.apps.openauctionbase.service;

import com.axelor.apps.base.db.Partner;
import com.axelor.apps.base.db.TradingName;
import com.axelor.apps.openauction.db.MissionHeader;

public interface ToolsMissionChange {

  // PROCEDURE IsFormLocked@1100481001(pMissionNo@1100481000 : Code[20]) : Boolean;
  public Boolean isFormLocked(MissionHeader pMissionNo);
  // PROCEDURE AllowMissionVATBusGrpChange@1100281001(pMissionHeader@1100281000 : Record
  // 8011402;pRaiseError@1100281002 : Boolean) ReturnValue : Boolean;
  public Boolean allowMissionVATBusGrpChange(MissionHeader pMissionHeader, Boolean pRaiseError);
  // PROCEDURE AllowMissionLineVATBusGrChange@1100281003(pMissionLine@1100281001 : Record
  // 8011403;pRaiseError@1100281000 : Boolean) ReturnValue : Boolean;
  public Boolean allowMissionLineVATBusGrChange(MissionHeader pMissionHeader, Boolean pRaiseError);
  // PROCEDURE AllowMasterContactChange@1100281004(pMissionHeader@1100281000 : Record
  // 8011402;pRaiseError@1100281004 : Boolean) ReturnValue : Boolean;
  public Boolean allowMasterContactChange(MissionHeader pMissionHeader, Boolean pRaiseError);
  // PROCEDURE AllowMissionGenBusGrpChange@1100481002(pMissionHeader@1100481001 : Record
  // 8011402;pRaiseError@1100481000 : Boolean) ReturnValue : Boolean;
  public Boolean allowMissionGenBusGrpChange(MissionHeader pMissionHeader, Boolean pRaiseError);
  // PROCEDURE AllowRespCenterChange@1100481004(pMissionHeader@1100481001 : Record
  // 8011402;pRaiseError@1100481000 : Boolean) ReturnValue : Boolean;
  public Boolean allowRespCenterChange(MissionHeader pMissionHeader, Boolean pRaiseError);
  // PROCEDURE UpdateMissionVATBusGroup@1100281000(VAR pMissionHeader@1100281000 : Record 8011402);
  public MissionHeader updateMissionVATBusGroup(MissionHeader pMissionHeader);
  // PROCEDURE UpdateMissionLineVATBusGroup@1100281002(pMissionLine@1100281000 : Record
  // 8011403;pNewVATGroup@1100281001 : Code[20]);
  public MissionHeader updateMissionLineVATBusGroup(
      MissionHeader pMissionHeader, String pNewVATGroup);
  // PROCEDURE UpdateMissionMasterContact@1100281005(VAR pMissionHeader@1100281000 : Record
  // 8011402;pNewContact@1100481000 : Code[20]);
  public MissionHeader updateMissionMasterContact(
      MissionHeader pMissionHeader, Partner pNewContact);
  // PROCEDURE UpdateMissionGenBusGrpChange@1100481003(VAR pMissionHeader@1100481000 : Record
  // 8011402;pNewGenBusGrp@1100481001 : Code[10]);
  public MissionHeader updateMissionGenBusGrpChange(
      MissionHeader pMissionHeader, String pNewGenBusGrp);
  // PROCEDURE UpdateRespCenterChange@1100481005(VAR pMissionHeader@1100481005 : Record
  // 8011402;pNewRespCenter@1100481002 : Code[10]);
  public MissionHeader updateRespCenterChange(
      MissionHeader pMissionHeader, TradingName pNewRespCenter);
}
