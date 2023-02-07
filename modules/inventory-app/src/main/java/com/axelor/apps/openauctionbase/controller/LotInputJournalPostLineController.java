package com.axelor.apps.openauctionbase.controller;

import com.axelor.apps.openauction.db.MissionHeader;
import com.axelor.apps.openauction.db.repo.MissionHeaderRepository;
import com.axelor.apps.openauctionbase.service.LotInputJournalPostLine;
import com.axelor.exception.service.TraceBackService;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;

public class LotInputJournalPostLineController {
  public void validate(ActionRequest request, ActionResponse response) {

    try {

      MissionHeader missionHeader = request.getContext().asType(MissionHeader.class);

      MissionHeaderRepository missionHeaderRepo = Beans.get(MissionHeaderRepository.class);
      missionHeader = missionHeaderRepo.find(missionHeader.getId());

      LotInputJournalPostLine lotInputJournalPostLine = Beans.get(LotInputJournalPostLine.class);
      lotInputJournalPostLine.runMissionHeader(missionHeader);

      response.setCanClose(true);

    } catch (Exception e) {
      TraceBackService.trace(response, e);
    }
  }
}
