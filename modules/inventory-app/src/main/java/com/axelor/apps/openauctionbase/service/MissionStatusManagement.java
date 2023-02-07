package com.axelor.apps.openauctionbase.service;

import com.axelor.apps.openauction.db.MissionActivityLine;
import com.axelor.apps.openauction.db.MissionHeader;
import com.axelor.apps.openauction.db.MissionLine;

public interface MissionStatusManagement {

  // PROCEDURE CheckStatus@1000000007(VAR pMissionLine@1000000000 : Record 8011403);
  public void checkStatus(MissionLine pMissionLine);
  // PROCEDURE ActivityStatusFromActivity@1000000000(VAR pMissionActivityLine@1000000000 : Record
  // 8011457);
  public void activityStatusFromActivity(MissionActivityLine pMissionActivityLine);
  // PROCEDURE DemandActivityLineStatus@1000000006(VAR pMissionActivityLine@1000000002 : Record
  // 8011457);
  public void demandActivityLineStatus(MissionActivityLine pMissionActivityLine);
  // PROCEDURE ChangeStatusMission@1000000002(VAR pMissionHeader@1000000000 : Record 8011402);
  public void changeStatusMission(MissionHeader pMissionHeader);
  // PROCEDURE ChangeToSpecificStatusMission@1000000008(VAR pMissionHeader@1000000000 : Record
  // 8011402;pStatus@1000000002 : 'Initial,Current,Done,Waiting,Canceled,Finished');
  public void changeToSpecificStatusMission(MissionHeader pMissionHeader, String pStatus);
  // PROCEDURE SetNotPreviewOffice@1180113000(pNotPreviewOffice@1180113000 : Boolean);
  public void setNotPreviewOffice(Boolean pNotPreviewOffice);
  // PROCEDURE CancelMission@1180113001(pMission@1180113000 : Record 8011402);
  public void cancelMission(MissionHeader pMission);
  // PROCEDURE CancelCancelledMission@1180113002(pMission@1180113000 : Record 8011402);
  public void cancelCancelledMission(MissionHeader pMission);
}
