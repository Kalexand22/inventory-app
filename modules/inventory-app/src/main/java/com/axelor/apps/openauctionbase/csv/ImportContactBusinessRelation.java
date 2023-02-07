package com.axelor.apps.openauctionbase.csv;

import com.axelor.apps.base.db.Partner;
import com.axelor.apps.base.db.PartnerRole;
import com.axelor.apps.base.db.repo.PartnerRepository;
import com.axelor.apps.base.db.repo.PartnerRoleRepository;
import com.axelor.apps.openauction.db.ContactBusinessRelation;
import com.google.inject.Inject;
import java.math.BigDecimal;
import java.util.Map;

public class ImportContactBusinessRelation {
  PartnerRepository partnerRepository;
  PartnerRoleRepository partnerRoleRepository;

  @Inject
  public ImportContactBusinessRelation(
      PartnerRepository partnerRepository, PartnerRoleRepository partnerRoleRepository) {
    this.partnerRepository = partnerRepository;
    this.partnerRoleRepository = partnerRoleRepository;
  }

  public Object importContactBusinessRelation(Object bean, Map<String, Object> values) {
    BigDecimal amount = (BigDecimal) values.get("amount");
    ContactBusinessRelation contactBusinessRelation = (ContactBusinessRelation) bean;

    if (!contactBusinessRelation.getContactNo().isEmpty()
        && !contactBusinessRelation.getBusinessRelationCode().isEmpty()) {
      Partner partner = partnerRepository.findByPartnerSeq(contactBusinessRelation.getContactNo());
      PartnerRole partnerRole =
          partnerRoleRepository.findByCode(contactBusinessRelation.getBusinessRelationCode());
      if (partner == null || partnerRole == null) {
        return contactBusinessRelation;
      }

      partner.addPartnerRoleSetItem(partnerRole);
      partnerRepository.save(partner);
    }
    return contactBusinessRelation;
  }
}
