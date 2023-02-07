package com.axelor.apps.openauctionbase.repository;

import com.axelor.apps.base.service.administration.SequenceService;
import com.axelor.apps.openauction.db.MissionHeader;
import com.axelor.apps.openauction.db.repo.MissionHeaderRepository;
import com.axelor.inject.Beans;

public class MissionHeaderOpenAuctionRepo extends MissionHeaderRepository {

  @Override
  public MissionHeader save(MissionHeader entity) {
    if (entity.getNo() == null && entity.getMissionTemplateCode() != null) {

      entity.setNo(
          Beans.get(SequenceService.class)
              .getSequenceNumber(entity.getMissionTemplateCode().getNoSequence()));
    }
    return super.save(entity);
  }
}
