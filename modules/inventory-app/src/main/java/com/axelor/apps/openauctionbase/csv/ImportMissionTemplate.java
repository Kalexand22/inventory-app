package com.axelor.apps.openauctionbase.csv;

import com.axelor.apps.openauction.db.MissionTemplate;
import com.axelor.apps.openauction.db.repo.ContactTemplateRepository;
import com.axelor.apps.openauction.db.repo.MissionTemplateRepository;
import com.google.inject.Inject;
import java.util.Map;

public class ImportMissionTemplate {
  MissionTemplateRepository missionTemplateRepository;
  ContactTemplateRepository contactTemplateRepository;

  @Inject
  public ImportMissionTemplate(
      MissionTemplateRepository missionTemplateRepository,
      ContactTemplateRepository contactTemplateRepository) {
    this.missionTemplateRepository = missionTemplateRepository;
    this.contactTemplateRepository = contactTemplateRepository;
  }

  public Object importMissionTemplate(Object bean, Map<String, Object> values) {

    MissionTemplate missionTemplate = (MissionTemplate) bean;

    if (missionTemplate.getContactTemplateFilter() != null
        && missionTemplate.getContactTemplateFilter().getDescription().equals("")) {
      missionTemplate.setContactTemplateFilter(null);
      missionTemplateRepository.save(missionTemplate);
      contactTemplateRepository.remove(missionTemplate.getContactTemplateFilter());
    }

    return missionTemplate;
  }
}
