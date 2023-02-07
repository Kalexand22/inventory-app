package com.axelor.apps.openauctionbase.service;

import com.axelor.apps.base.db.Partner;
import com.axelor.apps.openauction.db.ActivityHeader;
import com.axelor.apps.openauction.db.MissionHeader;
import com.axelor.apps.openauction.db.MissionTemplate;
import com.axelor.exception.AxelorException;

public interface MissionTemplateManagement {
  public MissionHeader createMissionFromMission(
      MissionHeader pMissionHeader,
      MissionTemplate pMissionTemplate,
      Boolean pJudicialFilter,
      String pLawyerBusNo)
      throws AxelorException;

  public Boolean createMissionFromContact(Partner pContact);

  public void getCustomerNo();

  public void setAuctionFromInv(Boolean pTrue);

  public void setSkipActivityCreation(Boolean pSkipActivity);

  public void createActivity2(MissionHeader pMissionHeader, ActivityHeader pActivityCodeToHeader)
      throws AxelorException;
}
