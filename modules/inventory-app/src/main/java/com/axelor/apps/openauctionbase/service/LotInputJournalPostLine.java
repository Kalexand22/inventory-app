package com.axelor.apps.openauctionbase.service;

import com.axelor.apps.openauction.db.MissionHeader;
import com.axelor.exception.AxelorException;

public interface LotInputJournalPostLine {
  public void runMissionHeader(MissionHeader pMissionHeader) throws AxelorException;
}
