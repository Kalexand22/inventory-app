package com.axelor.apps.openauctionbase.controller;

import com.axelor.apps.openauctionbase.service.EncheresJDBC;
import com.axelor.exception.service.TraceBackService;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;

public class MissionHeaderController {

  public void loadMissionHeader(ActionRequest request, ActionResponse response) {

    try {

      EncheresJDBC lotService = Beans.get(EncheresJDBC.class);
      lotService.selectDossierInv();

      response.setReload(true);

    } catch (Exception e) {
      TraceBackService.trace(response, e);
    }
  }
}
