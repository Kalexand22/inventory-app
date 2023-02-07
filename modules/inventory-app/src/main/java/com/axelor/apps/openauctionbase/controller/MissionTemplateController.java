package com.axelor.apps.openauctionbase.controller;

import com.axelor.apps.openauction.db.MissionHeader;
import com.axelor.apps.openauction.db.MissionHeaderWizard;
import com.axelor.apps.openauctionbase.service.MissionTemplateManagement;
import com.axelor.apps.openauctiontemplate.service.TransferFields;
import com.axelor.exception.service.TraceBackService;
import com.axelor.inject.Beans;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;

public class MissionTemplateController {
  public void createMissionFromTemplate(ActionRequest request, ActionResponse response) {

    try {

      MissionHeaderWizard missionHeaderWizard =
          request.getContext().asType(MissionHeaderWizard.class);

      MissionHeader pMissionHeader = new MissionHeader();
      pMissionHeader =
          (MissionHeader) TransferFields.transferFields(missionHeaderWizard, pMissionHeader);

      MissionTemplateManagement missionTemplateManagement =
          Beans.get(MissionTemplateManagement.class);
      pMissionHeader =
          missionTemplateManagement.createMissionFromMission(
              pMissionHeader, missionHeaderWizard.getMissionTemplateCode(), false, "");

      if (pMissionHeader != null) {
        // Open the generated mission in a new tab
        response.setView(
            ActionView.define("Mission")
                .model(MissionHeader.class.getName())
                .add("grid", "mission-header-grid")
                .add("form", "mission-header-form")
                .param("forceEdit", "true")
                .context("_showRecord", String.valueOf(pMissionHeader.getId()))
                .map());
        response.setCanClose(true);
      }
    } catch (Exception e) {
      TraceBackService.trace(response, e);
    }
  }
}
