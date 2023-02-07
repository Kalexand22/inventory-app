package com.axelor.apps.openauctionbase.repository;

import com.axelor.apps.openauction.db.Lot;
import com.axelor.apps.openauction.db.MissionHeader;
import com.axelor.apps.openauction.db.MissionServiceLine;
import java.math.BigDecimal;
import javax.persistence.Entity;

@Entity
public class MissionServiceLineExt extends MissionServiceLine {

  public void setChargeable(boolean chargeable) {
    if (this.getAcceptToInvoice() != chargeable) {
      if (this.getOutstandingAmount().compareTo(BigDecimal.ZERO) != 0) {
        this.setAcceptToInvoice(chargeable);
      } else {
        this.setAcceptToInvoice(false);
      }
    }
    super.setChargeable(chargeable);
  }

  public void setAcceptToInvoice(boolean acceptToInvoice) {
    if (acceptToInvoice) {
      // TODO TESTFIELD Chargeable
      this.setQtytoInvoice(this.getOutstandingQuantity());
    } else {
      this.setQtytoInvoice(BigDecimal.ZERO);
    }
    super.setAcceptToInvoice(acceptToInvoice);
  }

  @Override
  public void setMissionNo(MissionHeader missionNo) {
    this.setMissionTemplateCode(missionNo.getMissionTemplateCode());
    // TODO MissionHeader.TESTFIELD("VAT Business Posting Group");
    if (this.getLotNo() == null) {
      this.setSellerFiscalPosition(missionNo.getFiscalPosition());
    }
    // TODO GetMissVATBus;
    // IF "No." <> '' THEN
    // TODO  VALIDATE("Contact Imputation Type");
    if (missionNo.getResponsibilityCenter() != null) {
      this.setResponsibilityCenter(missionNo.getResponsibilityCenter());
    }
    super.setMissionNo(missionNo);
  }

  @Override
  public void setLotNo(Lot lotNo) {
    super.setLotNo(lotNo);
  }
}
