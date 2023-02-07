package com.axelor.apps.openauctionbase.validate;

import com.axelor.apps.openauction.db.LotValueEntry;
import com.axelor.apps.openauction.db.repo.LotValueEntryRepository;
import com.axelor.inject.Beans;

public class LotValueEntryValidate {
  /*
   *
  OnInsert=VAR
             lValueEntry@1000000000 : Record 8011461;
           BEGIN
             IF "Entry No."=0 THEN BEGIN
               lValueEntry.LOCKTABLE;
               IF lValueEntry.FINDLAST THEN
                 "Entry No." := lValueEntry."Entry No."+1
               ELSE
                 "Entry No." := 1;
             END;
             TouchRecord(TRUE);
           END;
   */
  public LotValueEntry onInsert(LotValueEntry lotValueEntry) {
    LotValueEntry lValueEntry =
        Beans.get(LotValueEntryRepository.class).all().order("-entryNo").fetchOne();
    if (lValueEntry != null) {
      lotValueEntry.setEntryNo(lValueEntry.getEntryNo() + 1);
    } else {
      lotValueEntry.setEntryNo(1);
    }
    return lotValueEntry;
  }
}
