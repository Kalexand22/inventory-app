package com.axelor.apps.openauctionbase.service;

import com.axelor.apps.openauction.db.LotValueEntry;
import com.axelor.apps.openauction.db.LotValueJournal;
import com.axelor.apps.openauction.db.repo.LotValueEntryRepository;
import com.axelor.apps.openauction.db.repo.LotValueJournalRepository;
import com.axelor.apps.openauctionbase.validate.LotValueEntryValidate;
import com.axelor.apps.openauctiontemplate.service.TransferFields;
import com.axelor.exception.AxelorException;
import com.axelor.exception.db.repo.TraceBackRepository;
import com.google.inject.Inject;
import java.math.BigDecimal;

public class LotValueJournalPostLineImpl implements LotValueJournalPostLine {

  LotValueEntryRepository lotValueEntryRepo;

  @Inject
  public LotValueJournalPostLineImpl(LotValueEntryRepository lotValueEntryRepo) {
    this.lotValueEntryRepo = lotValueEntryRepo;
  }

  @Override
  public void run(LotValueJournal pLotValueJnl) throws AxelorException {

    code(pLotValueJnl, true);
  }

  @Override
  public void runWithCheck(LotValueJournal pLotValueJnl) throws AxelorException {

    code(pLotValueJnl, true);
  }

  @Override
  public void runWithoutCheck(LotValueJournal pLotValueJnl) throws AxelorException {

    code(pLotValueJnl, false);
  }

  /*
  * LOCAL PROCEDURE Code@1000000002(CheckLine@1000000000 : Boolean);
   VAR
     lLotValueCheckLine@1000000001 : Codeunit 8011360;
     lLotDescMgt@1180113002 : Codeunit 8011723;
     lLotValueEntry@1000000002 : Record 8011461;
     lLot@1180113000 : Record 8011404;
     lVehiDescTmp@1180113001 : TEMPORARY Record 8011438;
   BEGIN
     WITH LotValueJnl DO BEGIN
       IF CheckLine THEN BEGIN
         lLotValueCheckLine.RunCheck(LotValueJnl);
       END;

       lLotValueEntry.TRANSFERFIELDS(LotValueJnl);


       IF FindDuplicate(lLotValueEntry) THEN EXIT;
       lLotValueEntry."Entry No." := 0;
       CheckReplaced(lLotValueEntry);
       lLotValueEntry.INSERT(TRUE);
     //<<AP05.ST
       IF "Entry Type" IN ["Entry Type"::"Reserve Price"] THEN
         CreateAttachedValues(LotValueJnl);
     //>>AP05.ST

       //<<ap04 isat.zw
       IF lLotValueEntry."Entry Type" IN [lLotValueEntry."Entry Type"::Estimate,
                                          lLotValueEntry."Entry Type"::"Reserve Price",
                                          lLotValueEntry."Entry Type"::Quotation] THEN
         IF lLot.GET(lLotValueEntry."Lot No.")  THEN BEGIN
           lVehiDescTmp.INIT;
           lVehiDescTmp.INSERT;
           lLotDescMgt.UpdateDescripAuctionVehi(lLot."No.", lVehiDescTmp);
           lLotDescMgt.UpdateDescripPurInv(lLot."No.");
         END;
       //>>ap04 isat.zw
     END;
   END;
  */
  private void code(LotValueJournal pLotValueJnl, Boolean checkLine) throws AxelorException {
    LotValueEntry lotValueEntry = new LotValueEntry();

    if (checkLine) {
      runCheck(pLotValueJnl);
    }
    lotValueEntry = (LotValueEntry) TransferFields.transferFields(pLotValueJnl, lotValueEntry);
    if (findDuplicate(lotValueEntry)) {
      return;
    }
    lotValueEntry.setEntryNo(0);
    checkReplaced(lotValueEntry);

    LotValueEntryValidate lotValueEntryValidate = new LotValueEntryValidate();
    lotValueEntry = lotValueEntryValidate.onInsert(lotValueEntry);
    lotValueEntryRepo.save(lotValueEntry);
    if (pLotValueJnl.getEntryType() == LotValueJournalRepository.ENTRYTYPE_RESERVEPRICE5) {
      createAttachedValues(pLotValueJnl);
    }
  }

  private void createAttachedValues(LotValueJournal pLotValueJnl) {}

  /*
  * LOCAL PROCEDURE CheckReplaced@1000000004(pLotValueEntry@1000000000 : Record 8011461);
   VAR
     lLotValueEntry2@1000000002 : Record 8011461;
   BEGIN
     WITH LotValueEntry DO BEGIN
       RESET;
       SETCURRENTKEY("Lot No.","Entry Type",Replaced);
       SETRANGE("Lot No.",pLotValueEntry."Lot No.");
     //<<AP03.ST
     // Une expertise remplace une estimation, une estimation ne remplace par l'expertise
     //  IF (pLotValueEntry."Entry Type" = pLotValueEntry."Entry Type"::Estimate) OR
     //     (pLotValueEntry."Entry Type" = pLotValueEntry."Entry Type"::Appraisal) THEN
       IF (pLotValueEntry."Entry Type" = pLotValueEntry."Entry Type"::Appraisal) THEN
     //>>AP03.ST
         SETFILTER("Entry Type",'%1|%2',"Entry Type"::Estimate,"Entry Type"::Appraisal)
       ELSE
         SETRANGE("Entry Type",pLotValueEntry."Entry Type");
       SETRANGE(Replaced,FALSE);
       IF (pLotValueEntry."Entry Type"=pLotValueEntry."Entry Type"::Auction) AND
          (pLotValueEntry."Auction Type"=pLotValueEntry."Auction Type"::"Absentee Bid Form") THEN
         pLotValueEntry.SETRANGE("Contact No.",pLotValueEntry."Contact No.");

       IF FINDSET(TRUE,TRUE) THEN
         REPEAT
           lLotValueEntry2 := LotValueEntry;
           lLotValueEntry2.Replaced := TRUE;
           lLotValueEntry2.MODIFY;
         UNTIL NEXT=0;
     END;
   END;
  */
  private void checkReplaced(LotValueEntry lotValueEntry) {

    Integer entryType = lotValueEntry.getEntryType();
    lotValueEntryRepo
        .all()
        .filter(
            "self.lotNo = :lot AND self.entryType IN ( :entryType, :entryTypeEstimate) AND self.replaced = :replaced")
        .bind("lot", lotValueEntry.getLotNo())
        .bind("entryType", entryType)
        .bind(
            "entryTypeEstimate",
            entryType.equals(LotValueJournalRepository.ENTRYTYPE_APPRAISAL2)
                ? LotValueJournalRepository.ENTRYTYPE_ESTIMATE0
                : entryType)
        .bind("replaced", false)
        .fetch()
        .forEach(
            lotValueEntry2 -> {
              lotValueEntry2.setReplaced(true);
              lotValueEntryRepo.save(lotValueEntry2);
            });

    // TODO : check if the following code is needed
    /*
        *  IF (pLotValueEntry."Entry Type"=pLotValueEntry."Entry Type"::Auction) AND
     (pLotValueEntry."Auction Type"=pLotValueEntry."Auction Type"::"Absentee Bid Form") THEN
    pLotValueEntry.SETRANGE("Contact No.",pLotValueEntry."Contact No.");
        */
  }

  /*
  *
   PROCEDURE FindDuplicate@1100281000(pLotValueEntry@1100281000 : Record 8011461) : Boolean;
   BEGIN
     // Evite les doublons sur vente (retrait ou adj)
     WITH pLotValueEntry DO BEGIN
       IF ("Auction No." = '') THEN EXIT(FALSE);
       CASE "Entry Type" OF
         "Entry Type"::Auction :
           IF NOT (pLotValueEntry."Auction Type" IN [pLotValueEntry."Auction Type"::"Selling-off price",
                                                     pLotValueEntry."Auction Type"::"Retired Price"])
           THEN EXIT(FALSE);
         "Entry Type"::"Bid Price" : BEGIN
           // Dans tous les cas
         END;
         ELSE
           EXIT(FALSE);
       END; // CASE
     END; // WITH
     WITH LotValueEntry DO BEGIN
       RESET;
       SETCURRENTKEY("Lot No.","Entry Type",Replaced);
       SETRANGE("Lot No.",pLotValueEntry."Lot No.");
       SETRANGE("Entry Type", pLotValueEntry."Entry Type");
       SETRANGE("Auction Type", pLotValueEntry."Auction Type");
       SETRANGE("Auction No.", pLotValueEntry."Auction No.");
       SETRANGE(Replaced,FALSE);
       SETRANGE("Base Amount", pLotValueEntry."Base Amount");
       SETRANGE("Contact No.", pLotValueEntry."Contact No.");
       EXIT(NOT ISEMPTY);
     END;
   END;

  */
  @Override
  public Boolean findDuplicate(LotValueEntry pLotValueEntry) {
    if (pLotValueEntry.getAuctionNo() == null) {
      return false;
    }
    if (pLotValueEntry.getEntryType() == LotValueJournalRepository.ENTRYTYPE_AUCTION6) {
      if (!pLotValueEntry
              .getAuctionType()
              .equals(LotValueJournalRepository.AUCTIONTYPE_SELLOFFPRICE1)
          && !pLotValueEntry
              .getAuctionType()
              .equals(LotValueJournalRepository.AUCTIONTYPE_RETIREDPRICE)) {
        return false;
      }
      if (!pLotValueEntry.getEntryType().equals(LotValueJournalRepository.ENTRYTYPE_BIDPRICE3)) {
        return false;
      }
    }
    return lotValueEntryRepo
            .all()
            .filter(
                "self.lot = ?1 AND self.entryType = ?2 AND self.auctionType = ?3 AND self.auctionNo = ?4 AND self.replaced = ?5 AND self.baseAmount = ?6 AND self.contactNo = ?7",
                pLotValueEntry.getLotNo(),
                pLotValueEntry.getEntryType(),
                pLotValueEntry.getAuctionType(),
                pLotValueEntry.getAuctionNo(),
                false,
                pLotValueEntry.getBaseAmount(),
                pLotValueEntry.getContactNo())
            .fetch()
            .size()
        > 0;
  }

  /*
  * PROCEDURE CancelLotValue@1100481000(pLotValueEntry@1100481000 : Record 8011461);
   BEGIN
     //AP02.ST
     WITH pLotValueEntry DO BEGIN
       Replaced := TRUE;
       MODIFY;
     END;
   END;
  */
  @Override
  public void cancelLotValue(LotValueEntry pLotValueEntry) {
    pLotValueEntry.setReplaced(true);
    lotValueEntryRepo.save(pLotValueEntry);
  }

  /*
  *
   PROCEDURE RunCheck@1000000000(pLotValueJournal@1000000000 : Record 8011460);
   BEGIN
     WITH pLotValueJournal DO BEGIN
       IF "Entry Type" IN ["Entry Type"::Estimate,"Entry Type"::"Seller Estimate","Entry Type"::Appraisal] THEN
         IF ("Min Amount" <> 0) AND ("Max Amount" <> 0) AND ("Min Amount" > "Max Amount") THEN
           ERROR(STRSUBSTNO(Text8011400,FIELDCAPTION("Max Amount"),FIELDCAPTION("Min Amount")));

     END;
   END;
  */
  private void runCheck(LotValueJournal pLotValueJournal) throws AxelorException {
    if (pLotValueJournal.getEntryType().equals(LotValueJournalRepository.ENTRYTYPE_ESTIMATE0)
        || pLotValueJournal
            .getEntryType()
            .equals(LotValueJournalRepository.ENTRYTYPE_SELLERESTIMATE1)
        || pLotValueJournal.getEntryType().equals(LotValueJournalRepository.ENTRYTYPE_APPRAISAL2)) {
      if (pLotValueJournal.getMinAmount() != BigDecimal.ZERO
          && pLotValueJournal.getMaxAmount() != BigDecimal.ZERO
          && pLotValueJournal.getMinAmount().compareTo(pLotValueJournal.getMaxAmount()) > 0) {
        throw new AxelorException(
            pLotValueJournal,
            TraceBackRepository.CATEGORY_INCONSISTENCY,
            "Le montant minimum doit être inférieur au montant maximum");
      }
    }
  }
}
