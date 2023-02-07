package com.axelor.apps.openauctionbase.service;

import com.axelor.apps.openauction.db.Lot;
import com.axelor.apps.openauction.db.LotQuickInputJournal;
import com.axelor.apps.openauction.db.MissionHeader;
import com.axelor.apps.openauction.db.MissionLine;
import com.axelor.apps.openauction.db.repo.MissionHeaderRepository;
import com.axelor.apps.openauction.db.repo.MissionLineRepository;
import com.axelor.apps.openauctionbase.validate.MissionLineValidate;
import com.axelor.exception.AxelorException;
import com.axelor.exception.db.repo.TraceBackRepository;
import com.google.inject.Inject;
import java.math.BigDecimal;
import java.util.List;

public class MissionLineManagementImpl implements MissionLineManagement {

  Integer missionLineNo;
  Integer sortingSequenceNo;
  MissionLineRepository missionLineRepo;

  @Inject
  public MissionLineManagementImpl(MissionLineRepository missionLineRepo) {
    this.missionLineRepo = missionLineRepo;
  }

  @Override
  public void sendLotToAuction(MissionLine pMissionLine) {
    // TODO Auto-generated method stub

  }

  @Override
  public Integer lotInsertMission(
      MissionHeader pMissionHeader,
      Lot pLot,
      MissionLine pMissionLine,
      LotQuickInputJournal pLotQuickInputJournal) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void duplicateMissionLine(MissionLine pMissionLine, Boolean pSameLotNo)
      throws AxelorException {
    /*
     *
    VAR
      lMissionHeader@1000000003 : Record 8011402;
      lMissionLine@1000000005 : Record 8011403;
      lTempLotQuickInputJournal@1000000004 : TEMPORARY Record 8011467;
      lLot@1000000001 : Record 8011404;
      lNewLot@1000000002 : Record 8011404;
      lTextWh@1180113000 : Record 8011363;
    BEGIN
      //AP01.isat.PC
      WITH pMissionLine DO BEGIN
        IF FINDFIRST THEN BEGIN
          REPEAT
            IF Type = Type::Lot THEN BEGIN
              lMissionHeader.GET("Mission No.");
              IF lMissionHeader."Mission Status" >= lMissionHeader."Mission Status"::Canceled THEN BEGIN
                ERROR(Text8011400);
              END;

              lLot.GET("No.");

              lTempLotQuickInputJournal.INIT;
              IF pSameLotNo THEN BEGIN
                lTempLotQuickInputJournal."Lot No." := pMissionLine."No.";
              END ELSE BEGIN
                lTempLotQuickInputJournal."Lot No." := '';
              END;
              lTempLotQuickInputJournal.VALIDATE("Lot Template Code", lLot."Lot Template Code");
              lTempLotQuickInputJournal.Quantity := Quantity;
              lTempLotQuickInputJournal.Description := Description;
              lTempLotQuickInputJournal."Origin Country Code" := lLot."Origin Country Code";
              lTempLotQuickInputJournal."Lot Categorie 1 Code" := lLot."Lot Categorie 1 Code";
              lTempLotQuickInputJournal."Lot Categorie 2 Code" := lLot."Lot Categorie 2 Code";
              lTempLotQuickInputJournal."Lot Categorie 3 Code" := lLot."Lot Categorie 3 Code";
              lTempLotQuickInputJournal."Lot Categorie 4 Code" := lLot."Lot Categorie 4 Code";
              lTempLotQuickInputJournal."Valuation At Best" := lLot."Valuation At Best";
              lTempLotQuickInputJournal."Auction Prod. Posting Group" := lLot."Auction Prod. Posting Group";
              lTempLotQuickInputJournal."VAT Prod. Posting Group" := lLot."VAT Prod. Posting Group";
              lLot.CALCFIELDS("Main Text Entry No.");
              IF lLot."Main Text Entry No." <> 0 THEN
                IF lTextWh.GET(lLot."Main Text Entry No.") THEN BEGIN
                  lTextWh.CALCFIELDS(Attachment);
                  lTempLotQuickInputJournal."Temporary Blob" := lTextWh.Attachment;

                END;
              lTempLotQuickInputJournal.INSERT(FALSE);
              APLotTemplateMgt.CreateLot(lTempLotQuickInputJournal,lNewLot,lMissionHeader."Master Contact No.");
              CLEAR(lMissionLine);
              lMissionLine.RESET;
              lMissionLine.COPY(pMissionLine);
              LotInsertMission(lMissionHeader,lNewLot,lMissionLine,lTempLotQuickInputJournal);
            END;
          UNTIL NEXT = 0;
          MESSAGE(Text8011402)
        END;
      END;
    END;
     */
    MissionHeader lMissionHeader = pMissionLine.getMissionNo();
    if (lMissionHeader.getMissionStatus().equals(MissionHeaderRepository.MISSIONSTATUS_CANCELED)
        || lMissionHeader
            .getMissionStatus()
            .equals(MissionHeaderRepository.MISSIONSTATUS_FINISHED)) {
      throw new AxelorException(
          TraceBackRepository.CATEGORY_CONFIGURATION_ERROR,
          "Le statut de la mission ne doit pas être annulé ou terminé");
    }
    Lot lLot = pMissionLine.getNoLot();
    LotQuickInputJournal lTempLotQuickInputJournal = new LotQuickInputJournal();
    Lot lNewLot = new Lot();
    if (pSameLotNo) {
      lTempLotQuickInputJournal.setLotNo(pMissionLine.getNoLot());
    } else {
      lTempLotQuickInputJournal.setLotNo(null);
    }
    // TODO finish this method

  }

  @Override
  public Boolean isAffectedLotInMission(MissionHeader pMissionHeader, Lot pLot) {
    /*
     * VAR
      lMissionLine@1000000000 : Record 8011403;
      lMissionHeader@1000000004 : Record 8011402;
    BEGIN
      //AP02.ISAT.PC
      rIsAffected := FALSE;

      lMissionLine.SETCURRENTKEY(Type,"No.");
      lMissionLine.SETRANGE(Type,lMissionLine.Type::Lot);
      lMissionLine.SETRANGE("No.",pLot."No.");
      lMissionLine.SETRANGE("Transfered-To Mission No.",''); //AP07 isat.sf

      IF lMissionLine.FINDFIRST THEN BEGIN
        REPEAT
          IF lMissionHeader.GET(lMissionLine."Mission No.") THEN BEGIN
            IF (lMissionHeader."Mission Status" <> lMissionHeader."Mission Status"::Done) AND
               (lMissionHeader."Mission Status" <> lMissionHeader."Mission Status"::Finished) AND
               (lMissionHeader."Mission Status" <> lMissionHeader."Mission Status"::Canceled)
            THEN BEGIN
              rIsAffected := pMissionHeader."Auction Mission" = lMissionHeader."Auction Mission";
            END;
          END;
        UNTIL (lMissionLine.NEXT = 0) OR rIsAffected;
      END;
    END;
     */
    List<MissionLine> missionLineList =
        missionLineRepo
            .all()
            .filter(
                "self.type = ?1 AND self.noLot = ?2 AND (self.transferedToMissionNo = ?3 OR self.transferedToMissionNo = ?4)",
                MissionLineRepository.TYPE_LOT,
                pLot,
                null,
                "")
            .fetch();
    for (MissionLine missionLine : missionLineList) {

      MissionHeader missionHeader = missionLine.getMissionNo();
      if (missionHeader != null) {
        if (missionHeader.getMissionStatus() != MissionHeaderRepository.MISSIONSTATUS_DONE
            && missionHeader.getMissionStatus() != MissionHeaderRepository.MISSIONSTATUS_FINISHED
            && missionHeader.getMissionStatus() != MissionHeaderRepository.MISSIONSTATUS_CANCELED) {
          return pMissionHeader.getAuctionMission() == missionHeader.getAuctionMission();
        }
      }
    }
    return false;
  }

  @Override
  public Boolean deleteMissionLine(MissionLine pMissionLine) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void sortMissionLine(MissionHeader pMission) {
    // TODO Auto-generated method stub

  }

  @Override
  public MissionLine createMissionLine(
      MissionHeader pMissionHeader, Lot pLot, LotQuickInputJournal pLotQuickInputJournal)
      throws AxelorException {
    /*
         *
    PROCEDURE CreateMissionLine@1000000004(VAR pMissionHeader@1000000000 : Record 8011402;VAR pMissionLine@1000000002 : Record 8011403;VAR pLot@1000000001 : Record 8011404;pLotQuickInputJournal@1000000003 : Record 8011467) rValue : Boolean;
    VAR
      lMissionLine@1000000007 : Record 8011403;
    BEGIN
      //AP01.isat.PC
      CLEAR(pMissionLine);

      IF pMissionHeader."Mission Status" >= pMissionHeader."Mission Status"::Canceled THEN BEGIN
        ERROR(Text8011400);
      END;

      rValue := NOT IsAffectedLotInMission(pMissionHeader,pLot);
      IF NOT rValue THEN BEGIN
        EXIT;
      END;*/
    if (pMissionHeader.getMissionStatus().equals(MissionHeaderRepository.MISSIONSTATUS_CANCELED)
        || pMissionHeader
            .getMissionStatus()
            .equals(MissionHeaderRepository.MISSIONSTATUS_FINISHED)) {
      throw new AxelorException(
          pMissionHeader,
          TraceBackRepository.CATEGORY_INCONSISTENCY,
          "Le statut de la mission ne doit pas être annulé ou terminé");
    }
    if (isAffectedLotInMission(pMissionHeader, pLot)) {
      return null;
    }

    /*
       *
    MissionLineNo := 0;
    //>>AP01.ISAT.SC
    GetMissionLineNo(pMissionHeader."No.");
       */
    missionLineNo = 0;
    getMissionLineNo(pMissionHeader);

    /*
       * lMissionLine.INIT;
    lMissionLine."Mission No." := pMissionHeader."No.";
    lMissionLine."Line No." := MissionLineNo;
    //<<AP14.ST
    //lMissionLine."Lot No./Mission" := LotNoMission;
    //<< AP18 isat.sf
    IF pLotQuickInputJournal."Lot No./Mission" <> 0 THEN BEGIN
      lMissionLine."Lot No./Mission"  := pLotQuickInputJournal."Lot No./Mission";
    END ELSE BEGIN
    //>> AP18 isat.sf
      lMissionLine."Lot No./Mission" := GetNewLotMissionNo(pMissionHeader."No.");
    END;

    //>>AP14.ST
    lMissionLine."Contact No." := pMissionHeader."Master Contact No.";
    lMissionLine.Type := lMissionLine.Type::Lot;
    lMissionLine.VALIDATE("No.",pLot."No.");
    lMissionLine."Activity Code" := pMissionHeader."Activity Code To Lines";
    lMissionLine."Lot Template Code" := pLot."Lot Template Code";
    lMissionLine."Lot Nature Code" := pLot."Lot Nature Code";
    lMissionLine."Auction Mission" := pMissionHeader."Auction Mission";
    lMissionLine."Mission Type" := pMissionHeader."Mission Type";
    lMissionLine."Inventory Indentation" := pLotQuickInputJournal.Indentation;
    lMissionLine.Quantity := pLotQuickInputJournal.Quantity;
    lMissionLine."Sorting Sequence No." := SortingSequenceNo;
    lMissionLine."Consig. Agreement Accepted" := pMissionHeader."Global Cons. Agreem. Accepted";
    lMissionLine."Consig. Agreement Refused" := pMissionHeader."Global Cons. Agreem. Refused";
    //<<AP02.ISAT.SC
    lMissionLine."Location Code" := pLot."Current Location Code";
    lMissionLine."Bin Code" := pLot."Current Bin Code";
    //>>AP02.ISAT.SC

    lMissionLine.INSERT(TRUE);
       */
    MissionLineValidate missionLineValidate = new MissionLineValidate();
    MissionLine lMissionLine = new MissionLine();
    lMissionLine.setMissionNo(pMissionHeader);
    lMissionLine.setLineNo(missionLineNo);
    if (pLotQuickInputJournal.getLotNoMission() != 0) {
      lMissionLine.setLotNoMission(pLotQuickInputJournal.getLotNoMission());
    } else {
      lMissionLine.setLotNoMission(getNewLotMissionNo(pMissionHeader));
    }
    lMissionLine.setContactNo(pMissionHeader.getMasterContactNo());
    lMissionLine.setType(MissionLineRepository.TYPE_LOT);

    // validate
    lMissionLine = missionLineValidate.validateNoLot(lMissionLine, pLot);
    // lMissionLine.setNoLot(pLot);
    lMissionLine.setActivityCode(pMissionHeader.getActivityCodeToLines());
    lMissionLine.setLotTemplateCode(pLot.getLotTemplateCode());
    lMissionLine.setLotNatureCode(pLot.getLotNatureCode());
    lMissionLine.setAuctionMission(pMissionHeader.getAuctionMission());
    lMissionLine.setMissionType(pMissionHeader.getMissionType());
    lMissionLine.setInventoryIndentation(pLotQuickInputJournal.getIndentation());
    lMissionLine.setQuantity(BigDecimal.valueOf(pLotQuickInputJournal.getQuantity()));
    lMissionLine.setSortingSequenceNo(sortingSequenceNo);
    lMissionLine.setConsigAgreementAccepted(pMissionHeader.getGlobalConsAgreemAccepted());
    lMissionLine.setConsigAgreementRefused(pMissionHeader.getGlobalConsAgreemRefused());

    // TODO setLocationCode setBinCode
    // lMissionLine.setLocationCode(pLot.getCurr());
    // lMissionLine.setBinCode(pLot.getCurrentBinCode());

    lMissionLine = missionLineValidate.onInsert(lMissionLine);
    missionLineRepo.save(lMissionLine);
    return lMissionLine;
  }

  @Override
  public void createCommentMissionLine(
      MissionHeader pMissionHeader, LotQuickInputJournal pLotQuickInputJournal)
      throws AxelorException {
    /*VAR
      lMissionLine@1000000007 : Record 8011403;
    BEGIN
      //AP01.isat.PC
      IF pMissionHeader."Mission Status" >= pMissionHeader."Mission Status"::Canceled THEN BEGIN
        ERROR(Text8011400);
      END;

      GetMissionLineNo(pMissionHeader."No.");

      lMissionLine.INIT;
      lMissionLine."Mission No." := pMissionHeader."No.";
      lMissionLine."Line No." := MissionLineNo;
      lMissionLine.Type := lMissionLine.Type::" ";
      lMissionLine.Description := pLotQuickInputJournal.Description;
      lMissionLine."Inventory Line Type" := pLotQuickInputJournal."Line Type";
      lMissionLine."Inventory Indentation" := pLotQuickInputJournal.Indentation;
      lMissionLine."Sorting Sequence No." := SortingSequenceNo;
      lMissionLine.Quantity := pLotQuickInputJournal.Quantity;
      lMissionLine.INSERT(TRUE);
    END; */
    MissionLineValidate missionLineValidate = new MissionLineValidate();
    if (pMissionHeader.getMissionStatus().equals(MissionHeaderRepository.MISSIONSTATUS_CANCELED)
        || pMissionHeader
            .getMissionStatus()
            .equals(MissionHeaderRepository.MISSIONSTATUS_FINISHED)) {
      throw new AxelorException(
          pMissionHeader,
          TraceBackRepository.CATEGORY_INCONSISTENCY,
          "Le statut de la mission ne doit pas être annulé ou terminé");
    }

    getMissionLineNo(pMissionHeader);
    MissionLine lMissionLine = new MissionLine();
    lMissionLine.setMissionNo(pMissionHeader);
    lMissionLine.setLineNo(missionLineNo);
    lMissionLine.setType(MissionLineRepository.TYPE_EMPTY);
    lMissionLine.setDescription(pLotQuickInputJournal.getDescription());
    lMissionLine.setInventoryLineType(pLotQuickInputJournal.getLineType());
    lMissionLine.setInventoryIndentation(pLotQuickInputJournal.getIndentation());
    lMissionLine.setSortingSequenceNo(sortingSequenceNo);

    lMissionLine.setQuantity(BigDecimal.valueOf(pLotQuickInputJournal.getQuantity()));
    lMissionLine = missionLineValidate.onInsert(lMissionLine);
    missionLineRepo.save(lMissionLine);
  }

  private void getMissionLineNo(MissionHeader pMissionHeaderNo) {
    /*
     *
    VAR
      lMissionLine@1000000000 : Record 8011403;
    BEGIN
      IF MissionLineNo = 0 THEN BEGIN
        lMissionLine.SETRANGE("Mission No.",pMissionHeaderNo);
        IF NOT lMissionLine.ISEMPTY THEN BEGIN
          lMissionLine.LOCKTABLE;
          lMissionLine.FINDLAST;
          MissionLineNo := lMissionLine."Line No.";
          SortingSequenceNo := lMissionLine."Sorting Sequence No.";
      //    LotNoMission := lMissionLine."Lot No./Mission"; AP14.ST
        END ELSE BEGIN
          MissionLineNo := 0;
          SortingSequenceNo := 0;
      //    LotNoMission := 0;  //AP14.ST
        END;
      END;
      MissionLineNo += 10000;
      SortingSequenceNo += 1;
     */
    MissionLine lMissionLine;
    if (missionLineNo == 0) {
      lMissionLine =
          missionLineRepo
              .all()
              .filter("self.missionNo = ?1", pMissionHeaderNo)
              .order("-lineNo")
              .fetchOne();
      if (lMissionLine != null) {
        missionLineNo = lMissionLine.getLineNo();
        sortingSequenceNo = lMissionLine.getSortingSequenceNo();
      } else {
        missionLineNo = 0;
        sortingSequenceNo = 0;
      }
    }
    missionLineNo += 10000;
    sortingSequenceNo += 1;
  }

  @Override
  public String existPostedBuyerInvoice(MissionHeader pMissionNo, Lot pLotNo) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String existPostedSellerInvoice(MissionHeader pMissionNo, Lot pLotNo) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String existPostedMissionInvoice(MissionHeader pMissionNo, Lot pLotNo) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Integer lotInsertMissionNotAuction(
      MissionHeader pMissionHeader,
      Lot pLot,
      MissionLine pMissionLine,
      LotQuickInputJournal pLotQuickInputJournal) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Boolean createMissionLineNotAuction(
      MissionHeader pMissionHeader,
      MissionLine pMissionLine,
      Lot pLot,
      LotQuickInputJournal pLotQuickInputJournal) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Integer getNewLotMissionNo(MissionHeader pMissionNo) {
    /*
     * lMissionLine@1100481001 : Record 8011403;
    BEGIN
      //AP14.ST
      WITH lMissionLine DO BEGIN
        SETCURRENTKEY("Mission No.","Lot No./Mission");
        SETRANGE("Mission No.", pMissionNo);
        IF FINDLAST THEN
          EXIT("Lot No./Mission"+1)
        ELSE
          EXIT(1);
      END;
    END;
     */
    MissionLine lMissionLine =
        missionLineRepo
            .all()
            .filter("self.missionNo = ?1", pMissionNo)
            .order("-lotNoMission")
            .fetchOne();
    if (lMissionLine != null) {
      return lMissionLine.getLotNoMission() + 1;
    } else {
      return 1;
    }
  }
}
