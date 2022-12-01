package com.axelor.apps.openauctionbase.csv;

import com.axelor.apps.openauction.db.Lot;
import com.axelor.apps.openauction.db.LotValueEntry;
import com.axelor.apps.openauction.db.repo.AuctionHeaderRepository;
import com.axelor.apps.openauction.db.repo.LotRepository;
import com.google.inject.Inject;
import java.util.Map;

public class ImportLotValueEntry {
  AuctionHeaderRepository auctionHeaderRepository;
  LotRepository lotRepository;

  @Inject
  public ImportLotValueEntry(LotRepository lotRepository) {

    this.lotRepository = lotRepository;
  }

  public Object importLotValueEntry(Object bean, Map<String, Object> values) {

    LotValueEntry lotValueEntry = (LotValueEntry) bean;
    Lot lot;

    if (lotValueEntry.getLotNo() != null) {
      lot = lotValueEntry.getLotNo();
      switch (lotValueEntry.getEntryType()) {
        case 0:
          lot.setEstimationValueEntry(lotValueEntry);
          break;
        case 1:
          lot.setEstimationSellerValueEntry(lotValueEntry);
          break;
        case 2:
          lot.setExpertiseValueEntry(lotValueEntry);
          break;
        case 3:
          lot.setAdjudicationValueEntry(lotValueEntry);
          break;
        case 4:
          lot.setInventoryValueEntry(lotValueEntry);
          break;
        case 5:
          lot.setReserveValueEntry(lotValueEntry);
          break;
        case 6:
          lot.setAuctionValueEntry(lotValueEntry);
          break;
        case 7:
          lot.setAcquisitionValueEntry(lotValueEntry);
          break;
        case 8:
          lot.setValuationValueEntry(lotValueEntry);
          break;
        case 9:
          lot.setGuaranteedPriceValueEntry(lotValueEntry);
          break;
        case 10:
          lot.setSaleEstimationValueEntry(lotValueEntry);
          break;
        default:
          break;
      }

      lotRepository.save(lot);
    }

    return lotValueEntry;
  }
}
