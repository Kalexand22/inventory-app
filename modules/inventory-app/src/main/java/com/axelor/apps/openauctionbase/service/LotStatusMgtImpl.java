package com.axelor.apps.openauctionbase.service;

import com.axelor.apps.openauction.db.AuctionLine;
import com.axelor.apps.openauction.db.Lot;
import com.axelor.apps.openauction.db.LotBOMHistory;
import com.axelor.apps.openauction.db.repo.AuctionLineRepository;
import com.axelor.apps.openauction.db.repo.LotBOMHistoryRepository;
import com.axelor.apps.openauction.db.repo.LotRepository;
import com.axelor.apps.openauctionbase.validate.LotValidate;
import com.axelor.inject.Beans;
import com.google.inject.Inject;
import java.util.List;

public class LotStatusMgtImpl implements LotStatusMgt {

  LotValidate lotValidate;
  AuctionLineRepository auctionLineRepo;

  @Inject
  public LotStatusMgtImpl(AuctionLineRepository auctionLineRepo) {
    this.lotValidate = Beans.get(LotValidate.class);
    this.auctionLineRepo = auctionLineRepo;
  }

  @Override
  public Lot CheckGeneralStatus(Lot pLot) {
    /*
    *
       CASE "Lot General Status" OF
         "Lot General Status"::Return : BEGIN
           "Auction Status" := "Auction Status"::NoAuction;  //ap08 isat.zw
         END;
         "Lot General Status"::Canceled : BEGIN
           "Auction Status" := "Auction Status"::NoAuction;  //ap08 isat.zw
         END;
       END;

       Closed := "Lot General Status" IN ["Lot General Status"::Sold, "Lot General Status"::Return, "Lot General Status"::Canceled];
       IF Closed THEN
         UpdRemainAuctionLines(CurrLot."No."); // mise à jour des lignes en invendable
    */
    switch (pLot.getLotGeneralStatus()) {
      case LotRepository.LOTGENERALSTATUS_RETURN:
        pLot.setAuctionStatus(LotRepository.AUCTIONSTATUS_NOAUCTION);
        break;

      case LotRepository.LOTGENERALSTATUS_CANCELED:
        pLot.setAuctionStatus(LotRepository.AUCTIONSTATUS_NOAUCTION);
        break;
    }
    boolean closed =
        pLot.getLotGeneralStatus().equals(LotRepository.LOTGENERALSTATUS_SOLD)
            || pLot.getLotGeneralStatus().equals(LotRepository.LOTGENERALSTATUS_RETURN)
            || pLot.getLotGeneralStatus().equals(LotRepository.LOTGENERALSTATUS_CANCELED);
    if (closed) {
      updRemainAuctionLines(pLot);
    }
    return pLot;
  }

  private void updRemainAuctionLines(Lot no) {
    /*
     * lAuctionLine@1100481001 : Record 8011401;
    BEGIN
      //AP02.ST
      WITH lAuctionLine DO BEGIN
        SETCURRENTKEY("Lot No.");
        SETRANGE("Lot No.", pLotNo);
        SETFILTER("Auction Status", '<%1',lAuctionLine."Auction Status"::Finished);
        SETRANGE("Is Auctionned", FALSE);
        SETFILTER("Line Status", '<>%1&<>%2', "Line Status"::Retired, "Line Status"::OutOfAuction);
        IF FINDSET(TRUE) THEN BEGIN
          REPEAT
            "Line Status" := "Line Status"::OutOfAuction;
            MODIFY;
          UNTIL NEXT=0;
        END;
      END;
    END;
     */
    List<AuctionLine> lAuctionLineList =
        auctionLineRepo
            .all()
            .filter(
                "self.lotNo = ?1 AND self.auctionStatus NOT IN ( ?2 , ?3 , ?4 ) AND self.auctionNo.isAuctionned = ?5 AND self.lineStatus NOT IN (?6 , ?7)",
                no,
                AuctionLineRepository.AUCTIONSTATUS_FINISHED,
                AuctionLineRepository.AUCTIONSTATUS_CANCELED,
                AuctionLineRepository.AUCTIONSTATUS_CLOSED,
                false,
                AuctionLineRepository.LINESTATUS_RETIRED,
                AuctionLineRepository.LINESTATUS_OUTOFAUCTION)
            .fetch();

    for (AuctionLine lAuctionLine : lAuctionLineList) {
      lAuctionLine.setLineStatus(AuctionLineRepository.LINESTATUS_OUTOFAUCTION);
      auctionLineRepo.save(lAuctionLine);
    }
  }

  @Override
  public Lot UpdLotInventoryStatus(Lot pLot) {
    /*
    *
     WITH pLot DO BEGIN
       lGeneralStatus := "Lot General Status";
     //<<AP05.ST
       CASE "Lot Inventory Status" OF
         "Lot Inventory Status"::Unknown, "Lot Inventory Status"::"To Pick" : ; //ap07 isat.zw
         "Lot Inventory Status"::"In Stock" :
            IF lGeneralStatus = "Lot General Status"::Return THEN BEGIN
              lGeneralStatus := "Lot General Status"::"On Mission";
              "Auction Status" := "Auction Status"::Initial; //ap08 isat.zw
            END;
         "Lot Inventory Status"::Delivered : ;
         "Lot Inventory Status"::Restitued :
           lGeneralStatus := "Lot General Status"::Return ;
         "Lot Inventory Status"::Ungroup : ;
         "Lot Inventory Status"::"In Transfer" : ;
       END; //CASE
     //>>AP05.ST
       IF "Lot General Status" <> lGeneralStatus THEN
         VALIDATE("Lot General Status", lGeneralStatus);
     END;
    */
    Integer lGeneralStatus = pLot.getLotGeneralStatus();
    switch (pLot.getLotInventoryStatus()) {
      case LotRepository.LOTINVENTORYSTATUS_UNKNOWN:
      case LotRepository.LOTINVENTORYSTATUS_TO_PICK:
        break;
      case LotRepository.LOTINVENTORYSTATUS_INSTOCK:
        if (lGeneralStatus.equals(LotRepository.LOTGENERALSTATUS_RETURN)) {
          lGeneralStatus = LotRepository.LOTGENERALSTATUS_ONMISSION;
          pLot.setAuctionStatus(LotRepository.AUCTIONSTATUS_INITIAL);
        }
        break;
      case LotRepository.LOTINVENTORYSTATUS_DELIVERED:
        break;
      case LotRepository.LOTINVENTORYSTATUS_RESTITUED:
        lGeneralStatus = LotRepository.LOTGENERALSTATUS_RETURN;
        break;
      case LotRepository.LOTINVENTORYSTATUS_UNGROUPED:
        break;
      case LotRepository.LOTINVENTORYSTATUS_INTRANSIT:
        break;
    }
    if (!pLot.getLotGeneralStatus().equals(lGeneralStatus)) {
      pLot = lotValidate.validateLotGeneralStatus(pLot, lGeneralStatus);
    }
    return pLot;
  }

  @Override
  public Lot UpdLotOperationStatus(Lot pLot) {
    /*
    * WITH pLot DO BEGIN
       lGeneralStatus := "Lot General Status";
       CASE "Cur. Mis. Lot Operation Status" OF
       END; //CASE
       IF "Lot General Status" <> lGeneralStatus THEN
         VALIDATE("Lot General Status", lGeneralStatus);
     END;
    */
    Integer lGeneralStatus = pLot.getLotGeneralStatus();
    if (!pLot.getLotGeneralStatus().equals(lGeneralStatus)) {
      pLot = lotValidate.validateLotGeneralStatus(pLot, lGeneralStatus);
    }
    return pLot;
  }

  @Override
  public Lot UpdLotMissionStatus(Lot pLot) {
    /*
    * WITH pLot DO BEGIN
       lGeneralStatus := "Lot General Status";
       CASE "Current Mission Lot Doc Status" OF
       END; //CASE
       IF "Lot General Status" <> lGeneralStatus THEN
         VALIDATE("Lot General Status", lGeneralStatus);
     END;
    */
    Integer lGeneralStatus = pLot.getLotGeneralStatus();
    if (!pLot.getLotGeneralStatus().equals(lGeneralStatus)) {
      pLot = lotValidate.validateLotGeneralStatus(pLot, lGeneralStatus);
    }
    return pLot;
  }

  @Override
  public Lot UpdLotAuctionStatus(Lot pLot) {
    /*
    * WITH pLot DO BEGIN
       lGeneralStatus := "Lot General Status";
       CASE "Auction Status" OF
         "Auction Status"::Initial, "Auction Status"::ToSign, "Auction Status"::ToPrepare,
         "Auction Status"::ReadyToAuction : ; // ne controle pas le statut général
         "Auction Status"::Auction :
           lGeneralStatus := "Lot General Status"::"On Sale";
         "Auction Status"::Auctionned :
           lGeneralStatus := "Lot General Status"::Sold;
         "Auction Status"::Retired :
     //<<AP06.ST
           IF pLot."Lot Type" IN [pLot."Lot Type"::"Meeting Sale", pLot."Lot Type"::"Grouping On Sale"] THEN BEGIN
             lGeneralStatus := "Lot General Status"::Canceled
             // Sortie du stock ? -> suppression ?
           END ELSE
     //>>AP06.ST
             lGeneralStatus := "Lot General Status"::"On Mission";
         "Auction Status"::NoAuction : ;
     //<<AP03.ST
         "Auction Status"::AuctionInGroup :
           lGeneralStatus := "Lot General Status"::Sold;
         "Auction Status"::Ungrouped : BEGIN
           //"Lot Inventory Status" := "Lot Inventory Status"::Ungroup;
           lGeneralStatus := "Lot General Status"::Canceled;
         END;
     //>>AP03.ST
       END;
       IF "Lot General Status" <> lGeneralStatus THEN
         VALIDATE("Lot General Status", lGeneralStatus);
     //<<AP04.ST
       IF pLot."Lot Type" IN [pLot."Lot Type"::"Meeting Sale", pLot."Lot Type"::"Grouping On Sale"] THEN
         UpdateComponentLotStatus(pLot);
     //>><AP04.ST
     END;
    */
    Integer lGeneralStatus = pLot.getLotGeneralStatus();
    switch (pLot.getAuctionStatus()) {
      case LotRepository.AUCTIONSTATUS_INITIAL:
      case LotRepository.AUCTIONSTATUS_TOSIGN:
      case LotRepository.AUCTIONSTATUS_TOPREPARE:
      case LotRepository.AUCTIONSTATUS_READYTOAUCTION:
        break;
      case LotRepository.AUCTIONSTATUS_AUCTION:
        lGeneralStatus = LotRepository.LOTGENERALSTATUS_ONSALE;
        break;
      case LotRepository.AUCTIONSTATUS_AUCTIONNED:
        lGeneralStatus = LotRepository.LOTGENERALSTATUS_SOLD;
        break;
      case LotRepository.AUCTIONSTATUS_RETIRED:
        if (pLot.getLotType().equals(LotRepository.LOTTYPE_MEETINGSALE)
            || pLot.getLotType().equals(LotRepository.LOTTYPE_GROUPINGONSALE)) {
          lGeneralStatus = LotRepository.LOTGENERALSTATUS_CANCELED;
        } else {
          lGeneralStatus = LotRepository.LOTGENERALSTATUS_ONMISSION;
        }
        break;
      case LotRepository.AUCTIONSTATUS_NOAUCTION:
        break;
      case LotRepository.AUCTIONSTATUS_AUCTIONINGROUP:
        lGeneralStatus = LotRepository.LOTGENERALSTATUS_SOLD;
        break;
      case LotRepository.AUCTIONSTATUS_UNGROUPED:
        lGeneralStatus = LotRepository.LOTGENERALSTATUS_CANCELED;
        break;
    }
    if (!pLot.getLotGeneralStatus().equals(lGeneralStatus)) {
      pLot = lotValidate.validateLotGeneralStatus(pLot, lGeneralStatus);
    }
    if (pLot.getLotType().equals(LotRepository.LOTTYPE_MEETINGSALE)
        || pLot.getLotType().equals(LotRepository.LOTTYPE_GROUPINGONSALE)) {
      updateComponentLotStatus(pLot);
    }
    return pLot;
  }

  private void updateComponentLotStatus(Lot pLotParent) {
    /*
     * lSubLot@1100481002 : Record 8011404;
      lNewLotStatus@1100481000 : Record 8011404;
      lLotBOMHist@1100481003 : Record 8011444;
    BEGIN
      //AP04.ST
      IF NOT (pLotParent."Lot Type" IN [pLotParent."Lot Type"::"Meeting Sale",pLotParent."Lot Type"::"Grouping On Sale"]) THEN
        EXIT;

      lNewLotStatus := pLotParent;
      IF pLotParent."Lot General Status" = pLotParent."Lot General Status"::Canceled THEN
        lNewLotStatus."Auction Status" := lNewLotStatus."Auction Status"::ReadyToAuction
      ELSE BEGIN
        CASE pLotParent."Auction Status" OF
          pLotParent."Auction Status"::Auctionned : BEGIN
            lNewLotStatus."Auction Status" := lNewLotStatus."Auction Status"::AuctionInGroup;
          END;
          pLotParent."Auction Status"::Auction : BEGIN
            lNewLotStatus."Auction Status" := lNewLotStatus."Auction Status"::NoAuction;
          END;
          pLotParent."Auction Status"::Retired : BEGIN
            lNewLotStatus."Auction Status" := lNewLotStatus."Auction Status"::ReadyToAuction;
          END;
        END; //CASE
      END;

      lLotBOMHist.SETCURRENTKEY("Lot No.");
      lLotBOMHist.SETRANGE("Lot No.", pLotParent."No.");
      lSubLot.SETRANGE("Parent BOM Lot", pLotParent."No.");
      IF lSubLot.FINDSET(TRUE) THEN BEGIN
        REPEAT
      //<<AP06.ST
          IF (pLotParent."Lot General Status" = pLotParent."Lot General Status"::Canceled) AND
             (pLotParent."Lot Type" IN [pLotParent."Lot Type"::"Meeting Sale",pLotParent."Lot Type"::"Grouping On Sale"]) THEN BEGIN
            // Annulation du regroupement
            lSubLot.VALIDATE("Lot Type", lSubLot."Lot Type"::Lot);
            lSubLot."Parent BOM Lot" := '';
            lLotBOMHist.SETRANGE("Child Lot No.", lSubLot."No.");
            IF NOT lLotBOMHist.ISEMPTY THEN
              lLotBOMHist.MODIFYALL(Cancelled,TRUE);
          END;
      //>>AP06.ST
          lSubLot.VALIDATE("Auction Status", lNewLotStatus."Auction Status");
          IF lSubLot."Auction Status" = lSubLot."Auction Status"::AuctionInGroup THEN
            lSubLot."Current Auction No." := pLotParent."Current Auction No.";
          lSubLot.MODIFY(TRUE);
        UNTIL lSubLot.NEXT=0;
      END;
     */
    if (!pLotParent.getLotType().equals(LotRepository.LOTTYPE_MEETINGSALE)
        && !pLotParent.getLotType().equals(LotRepository.LOTTYPE_GROUPINGONSALE)) {
      return;
    }
    Lot lNewLotStatus = pLotParent;
    if (pLotParent.getLotGeneralStatus().equals(LotRepository.LOTGENERALSTATUS_CANCELED)) {
      lNewLotStatus.setAuctionStatus(LotRepository.AUCTIONSTATUS_READYTOAUCTION);
    } else {
      switch (pLotParent.getAuctionStatus()) {
        case LotRepository.AUCTIONSTATUS_AUCTIONNED:
          lNewLotStatus.setAuctionStatus(LotRepository.AUCTIONSTATUS_AUCTIONINGROUP);
          break;
        case LotRepository.AUCTIONSTATUS_AUCTION:
          lNewLotStatus.setAuctionStatus(LotRepository.AUCTIONSTATUS_NOAUCTION);
          break;
        case LotRepository.AUCTIONSTATUS_RETIRED:
          lNewLotStatus.setAuctionStatus(LotRepository.AUCTIONSTATUS_READYTOAUCTION);
          break;
      }
    }
    LotRepository lLotRepository = Beans.get(LotRepository.class);
    LotBOMHistoryRepository lLotBOMHistoryRepository = Beans.get(LotBOMHistoryRepository.class);

    List<Lot> lSubLotList =
        lLotRepository.all().filter("self.parentBomLot = ?1", pLotParent.getNo()).fetch();

    for (Lot lLot : lSubLotList) {
      if (pLotParent.getLotGeneralStatus().equals(LotRepository.LOTGENERALSTATUS_CANCELED)
          && (pLotParent.getLotType().equals(LotRepository.LOTTYPE_MEETINGSALE)
              || pLotParent.getLotType().equals(LotRepository.LOTTYPE_GROUPINGONSALE))) {

        LotValidate lLotValidate = Beans.get(LotValidate.class);
        lLot = lLotValidate.validateLotType(lLot, LotRepository.LOTTYPE_LOT);
        lLot.setParentBOMLot(null);
        List<LotBOMHistory> lLotBOMHistList =
            lLotBOMHistoryRepository
                .all(LotBOMHistory.class)
                .filter("self.lotNo = ?1 AND self.childLotNo = ?2 ", pLotParent, lLot)
                .fetch();
        for (LotBOMHistory lLotBOMHistory : lLotBOMHistList) {
          if (lLotBOMHistory.getChildLotNo().equals(lLot)) {
            lLotBOMHistory.setCancelled(true);
            lLotBOMHistoryRepository.save(lLotBOMHistory);
          }
        }
      }
      lLot = lotValidate.validateAuctionStatus(lLot, lNewLotStatus.getAuctionStatus());
      if (lLot.getAuctionStatus().equals(LotRepository.AUCTIONSTATUS_AUCTIONINGROUP)) {
        lLot.setCurrentAuctionNo(pLotParent.getCurrentAuctionNo());
      }
      lLotRepository.save(lLot);
    }
  }

  @Override
  public Lot UpdateLotStatusByTodo() {
    // TODO Auto-generated method stub
    return null;
  }
}
