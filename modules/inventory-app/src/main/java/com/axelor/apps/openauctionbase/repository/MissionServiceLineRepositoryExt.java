package com.axelor.apps.openauctionbase.repository;

import com.axelor.apps.base.service.app.AppBaseService;
import com.axelor.apps.openauction.db.AuctionLine;
import com.axelor.apps.openauction.db.MissionServiceLine;
import com.axelor.apps.openauction.db.repo.LotRepository;
import com.axelor.apps.openauction.db.repo.MissionServiceLineRepository;
import com.axelor.apps.openauction.db.repo.ServiceTemplateLineRepository;
import com.axelor.auth.AuthUtils;
import com.axelor.inject.Beans;

public class MissionServiceLineRepositoryExt extends MissionServiceLineRepository {

  @Override
  public MissionServiceLine save(MissionServiceLine entity) {
    MissionServiceLine lMissServLine = null;
    MissionServiceLine lMissServLine2;
    if (entity.getId() == null) {
      if (entity.getAuctionBid() != null && !entity.getAuctionBid()) {
        if (entity.getAuctionNo() != null) {
          entity.setTransactionType(MissionServiceLineRepository.TRANSACTIONTYPE_VENTE);
        } else {
          entity.setTransactionType(MissionServiceLineRepository.TRANSACTIONTYPE_MISSION);
        }
      }
      if (entity.getDocumentNo() == null || entity.getDocumentNo().isEmpty()) {
        entity.setDocumentNo(
            entity.getTransactionType() == MissionServiceLineRepository.TRANSACTIONTYPE_VENTE
                ? entity.getAuctionNo().getId().toString()
                : entity.getMissionNo().getId().toString());
      }
      if (entity.getEntryNo() == null || entity.getEntryNo() == 0) {
        /*
        * //gestion de n° sequence de la clé
           IF "Entry No." = 0 THEN BEGIN
               lMissServLine2.LOCKTABLE;
               lMissServLine2.SETRANGE("Document No.", "Document No.");
               IF lMissServLine2.FINDLAST THEN
               "Entry No." := lMissServLine2."Entry No." + 1
               ELSE
               "Entry No." := 1;
           END;
           //>>ap46 isat.zw
        */
        lMissServLine2 =
            all()
                .filter("self.documentNo = ?1", entity.getDocumentNo())
                .order("-entryNo")
                .fetchOne();
        if (lMissServLine2 != null) {
          entity.setEntryNo(lMissServLine2.getEntryNo() + 1);
        } else {
          entity.setEntryNo(1);
        }
      }
      if (entity.getPriceDate() == null) {
        entity.setPriceDate(
            Beans.get(AppBaseService.class).getTodayDate(AuthUtils.getUser().getActiveCompany()));
      }
      if (entity.getBuyerFiscalPosition() == null || entity.getSellerFiscalPosition() == null)
        lMissServLine = FindBidLine(entity);
      if (lMissServLine != null) {
        if (entity.getBuyerFiscalPosition() == null) {
          entity.setBuyerFiscalPosition(lMissServLine.getBuyerFiscalPosition());
        }
        if (entity.getSellerFiscalPosition() == null) {
          entity.setSellerFiscalPosition(lMissServLine.getSellerFiscalPosition());
        }
      }

      entity.setOutstandingAmount(entity.getAmountInclVAT());
    }
    IsChargeable(entity);
    // TODO LawyerAnalysisMgt.SetSynchroRecord(Rec."Mission No.",'',Rec."Entry No.",8011449,0);

    return super.save((MissionServiceLine) entity);
  }

  private MissionServiceLine FindBidLine(MissionServiceLine rec) {
    MissionServiceLine lMissServLine = new MissionServiceLine();
    return lMissServLine;
  }

  private void IsChargeable(MissionServiceLine entity) {
    if (entity.getAuctionBid()) {
      entity.setChargeable(true);
      return;
    }
    if (entity.getCancelled()) {
      entity.setChargeable(false);
      return;
    }
    if (entity.getType().equals(ServiceTemplateLineRepository.TYPE_SERVICE)) {
      if (entity.getInvoicingType() == null) {
        entity.setChargeable(false);
        return;
      }
      switch (entity.getInvoicingType()) {
        case MissionServiceLineRepository.INVOICINGTYPE_BILLABLE:
          if (!entity.getChargeable()) entity.setChargeable(true);
          return;
        case MissionServiceLineRepository.INVOICINGTYPE_BILLABLEONBID:
          if (entity.getTransactionType() == MissionServiceLineRepository.TRANSACTIONTYPE_VENTE) {
            AuctionLine lAuctionLine = getAuctionLine(entity);
            if (lAuctionLine != null) {
              if (lAuctionLine.getIsAuctionned() != entity.getChargeable()) {
                entity.setChargeable(lAuctionLine.getIsAuctionned());
              }
              return;
            }
          } else {
            if (entity.getChargeable()
                != (entity.getLotNo().getAuctionStatus() == LotRepository.AUCTIONSTATUS_AUCTIONNED))
              entity.setChargeable(
                  entity.getLotNo().getAuctionStatus() == LotRepository.AUCTIONSTATUS_AUCTIONNED);
            return;
          }
        case MissionServiceLineRepository.INVOICINGTYPE_UNBILLABLE:
          if (entity.getChargeable()) entity.setChargeable(false);
          return;
        default:
          entity.setChargeable(false);
          return;
      }
    }

    entity.setChargeable(false);
  }

  private AuctionLine getAuctionLine(MissionServiceLine entity) {
    /*LOCAL PROCEDURE GetAuctionLine@1000000010() : Boolean;
    BEGIN
    //AP02.ISAT.ST
    IF ("Auction No." = '') OR ("Lot No." = '') OR ("Transaction Line No." = 0) THEN BEGIN
        CLEAR(AuctionLine);
        EXIT(FALSE);
    END;

    IF (AuctionLine."Auction No." = "Auction No.") AND (AuctionLine."Lot No." = "Lot No.") AND
        (AuctionLine."Line No." = "Transaction Line No.") THEN
        EXIT(TRUE);

    AuctionLine.SETRANGE("Auction No.","Auction No.");
    AuctionLine.SETRANGE("Line No.","Transaction Line No."); // isat.sf
    AuctionLine.SETRANGE("Lot No.","Lot No.");
    IF AuctionLine.FINDFIRST THEN
        EXIT(TRUE)
    ELSE BEGIN
        CLEAR(AuctionLine);
        EXIT(FALSE);
    END;
    END; */
    return null;
  }
}
