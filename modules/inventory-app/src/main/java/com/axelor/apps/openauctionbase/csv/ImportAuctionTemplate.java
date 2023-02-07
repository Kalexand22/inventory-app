package com.axelor.apps.openauctionbase.csv;

import com.axelor.apps.openauction.db.AuctionTemplate;
import com.axelor.apps.openauction.db.repo.AuctionTemplateRepository;
import com.axelor.apps.openauction.db.repo.ContactTemplateRepository;
import com.google.inject.Inject;
import java.util.Map;

public class ImportAuctionTemplate {
  ContactTemplateRepository contactTemplateRepository;
  AuctionTemplateRepository auctionTemplateRepository;

  @Inject
  public ImportAuctionTemplate(
      ContactTemplateRepository contactTemplateRepository,
      AuctionTemplateRepository auctionTemplateRepository) {
    this.contactTemplateRepository = contactTemplateRepository;
    this.auctionTemplateRepository = auctionTemplateRepository;
  }

  public Object importLot(Object bean, Map<String, Object> values) {

    AuctionTemplate auctionTemplate = (AuctionTemplate) bean;

    if (auctionTemplate.getDefaultBuyerContactTemplate() != null
        && auctionTemplate.getDefaultBuyerContactTemplate().getDescription().equals("")) {
      auctionTemplate.setDefaultBuyerContactTemplate(null);
      auctionTemplateRepository.save(auctionTemplate);
      contactTemplateRepository.remove(auctionTemplate.getDefaultBuyerContactTemplate());
    }

    return auctionTemplate;
  }
}
