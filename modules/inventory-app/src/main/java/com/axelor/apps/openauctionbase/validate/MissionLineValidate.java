package com.axelor.apps.openauctionbase.validate;

import com.axelor.apps.openauction.db.AuctionHeader;
import com.axelor.apps.openauction.db.Lot;
import com.axelor.apps.openauction.db.LotValueEntry;
import com.axelor.apps.openauction.db.MissionLine;
import com.axelor.apps.openauction.db.repo.LotRepository;
import com.axelor.apps.openauction.db.repo.LotValueEntryRepository;
import com.axelor.apps.openauction.db.repo.LotValueJournalRepository;
import com.axelor.apps.openauction.db.repo.MissionLineRepository;
import com.axelor.apps.openauctionbase.service.MissionLineManagement;
import com.axelor.exception.AxelorException;
import com.axelor.exception.db.repo.TraceBackRepository;
import com.axelor.inject.Beans;
import java.math.BigDecimal;

public class MissionLineValidate {
  /*
   * OnValidate=BEGIN
      //EVALUATE("Lot Mission No.","Lot No./Mission"); //AP10.ST

  //<<AP39 isat.sc
  // Bloquer modif si lot affecté à une vente
  IF Type = Type::Lot THEN BEGIN
      CALCFIELDS("Current Auction No.");
      //TESTFIELD("Current Auction No.",'')
  IF "Current Auction No." <> '' THEN
      ERROR(Text8011405);
      //"Lot No./Mission" := xRec."Lot No./Mission";
  END;
  //>>AP39 isat.sc
  END;
   */
  public MissionLine validateLotMissionNo(MissionLine missionLine, Integer lotNoMission)
      throws AxelorException {
    // EVALUATE("Lot Mission No.","Lot No./Mission"); //AP10.ST
    missionLine.setLotNoMission(lotNoMission);

    if (this.getCurrentAuctionNo(missionLine) != null) {
      throw new AxelorException(
          missionLine,
          TraceBackRepository.CATEGORY_CONFIGURATION_ERROR,
          "Vous ne pouvez pas modifier le numéro de Lot/Mission car ce lot est déjà affecté à une vente");
    }

    return missionLine;
  }

  public AuctionHeader getCurrentAuctionNo(MissionLine missionLine) throws AxelorException {
    if (missionLine.getNoLot() != null) {
      return missionLine.getNoLot().getCurrentAuctionNo();
    }
    return null;
  }
  /*
   * OnValidate=VAR
      lStandardText@1000000000 : Record 7;
      lMissionHeader@1000000002 : Record 8011402;
      lLot@1000000001 : Record 8011404;
      BEGIN
      CASE Type OF
          Type::" " : BEGIN
          lStandardText.GET("No.");
          Description := lStandardText.Description;
          "Lot Type" := "Lot Type"::"Origin Lot Component"; // AP06.ISAT.ST
          END;
          Type::Lot : BEGIN
          IF lLot.GET("No.") THEN BEGIN   // debug isat.sf 16/07/09
              lMissionHeader.GET("Mission No.");
              Description := COPYSTR(lLot.Description,1,MAXSTRLEN(Description));
              "Lot Template Code" := lLot."Lot Template Code";
              "Lot Type" := lLot."Lot Type"; // AP06.ISAT.ST
              "Lot Reference No." := lLot."Reference No."; //ap33 isat.zw

              lLot.VALIDATE("Current Mission No.", "Mission No."); //ap17 isat.zw
              lLot."Responsibility Center" := lMissionHeader."Responsibility Center";
              lLot.Judicial := lMissionHeader.Judicial;
              lLot."Current Mission Line No." := "Line No.";
              //lLot."Lot General Status" := lLot."Lot General Status"::"On Mission"; //ap22 isat.zw
              lLot.VALIDATE("Lot General Status", lLot."Lot General Status"::"On Mission");//ap22 isat.zw
              //<<ap30 isat.zw
              CALCFIELDS("Lawyer Bus. No.");
              IF lLot."Lawyer Bus. No." <> "Lawyer Bus. No." THEN
              lLot.VALIDATE("Lawyer Bus. No.", "Lawyer Bus. No.");
              //>>ap30 isat.zw
              lLot.MODIFY;
          END;
          END;
      END;
      END;
  */
  public MissionLine validateNoLot(MissionLine missionLine, Lot lot) {
    LotValidate lotValidate = Beans.get(LotValidate.class);
    missionLine.setNoLot(lot);
    if (missionLine.getType() == null
        || missionLine.getType().equals(MissionLineRepository.TYPE_EMPTY)) {
      missionLine.setDescription(lot.getDescription());
      missionLine.setLotType(LotRepository.LOTTYPE_ORIGINLOTCOMPONENT);
    } else {
      if (lot != null) {
        missionLine.setDescription(lot.getDescription());
        missionLine.setLotTemplateCode(lot.getLotTemplateCode());
        missionLine.setLotType(lot.getLotType());
        missionLine.setLotReferenceNo(lot.getReferenceNo());

        // validating lot
        lot = lotValidate.validateCurrentMissionNo(lot, missionLine.getMissionNo());
        lot.setResponsibilityCenter(missionLine.getMissionNo().getResponsibilityCenter());
        lot.setJudicial(missionLine.getMissionNo().getJudicial());
        lot.setCurrentMissionLineNo(missionLine);
        lot = lotValidate.validateLotGeneralStatus(lot, LotRepository.LOTGENERALSTATUS_ONMISSION);

        // TODO Laywer Bus. No.
      }
    }
    return missionLine;
  }

  /*
  *
   OnInsert=VAR
              lMissionHeader@1000000001 : Record 8011402;
              lLot@1000000002 : Record 8011404;
            BEGIN

              IF Type = Type::Lot THEN BEGIN
                IF NOT lMissionHeader.GET("Mission No.") THEN
                  CLEAR(lMissionHeader);
                IF NOT lLot.GET("No.") THEN
                  CLEAR(lLot);
                IF MissionLineManagement.IsAffectedLotInMission(lMissionHeader,lLot) THEN BEGIN
                  ERROR(STRSUBSTNO(Text8011400,"No."));
                END;
              END;

              GetMission;
              "Responsibility Center" := MissionHeader."Responsibility Center";
              "Template Mission Code" := MissionHeader."Mission Template Code";
              //<<AP10.ST
              //IF "Lot No./Mission" <> '' THEN
              //  EVALUATE("Lot Mission No.","Lot No./Mission");
              //>>AP10.ST

              TouchRecord(TRUE);
            END;
  */
  public MissionLine onInsert(MissionLine missionLine) throws AxelorException {
    if (missionLine.getType() != null
        && missionLine.getType().equals(MissionLineRepository.TYPE_LOT)) {

      MissionLineManagement missionLineManagement = Beans.get(MissionLineManagement.class);
      if (missionLineManagement.isAffectedLotInMission(
          missionLine.getMissionNo(), missionLine.getNoLot())) {
        throw new AxelorException(
            missionLine,
            TraceBackRepository.CATEGORY_INCONSISTENCY,
            String.format(
                "Le lot %s est déjà affecté à une mission", missionLine.getNoLot().getNo()));
      }
    }
    missionLine.setResponsibilityCenter(missionLine.getMissionNo().getResponsibilityCenter());
    missionLine.setTemplateMissionCode(missionLine.getMissionNo().getMissionTemplateCode());
    return missionLine;
  }

  public MissionLine calcFields(MissionLine missionLine) {

    LotValueEntryRepository lotValueEntryRepository = Beans.get(LotValueEntryRepository.class);

    LotValueEntry lotValueEntry =
        lotValueEntryRepository
            .all()
            .filter(
                "self.lotNo = ?1 AND ( self.entryType = ?2 OR  self.entryType = ?3 ) AND self.replaced = false",
                missionLine.getNoLot(),
                LotValueJournalRepository.ENTRYTYPE_ESTIMATE0,
                LotValueJournalRepository.ENTRYTYPE_APPRAISAL2)
            .fetchOne();

    missionLine.setEstimateMinValue(BigDecimal.ZERO);
    missionLine.setEstimateMaxValue(BigDecimal.ZERO);
    if (lotValueEntry != null) {
      missionLine.setEstimateMinValue(lotValueEntry.getMinAmount());
      missionLine.setEstimateMaxValue(lotValueEntry.getMaxAmount());
    }

    lotValueEntry =
        lotValueEntryRepository
            .all()
            .filter(
                "self.lotNo = ?1 AND ( self.entryType = ?2 ) AND self.replaced = false",
                missionLine.getNoLot(),
                LotValueJournalRepository.ENTRYTYPE_RESERVEPRICE5)
            .fetchOne();
    missionLine.setReservePrice(BigDecimal.ZERO);
    if (lotValueEntry != null) {
      missionLine.setReservePrice(lotValueEntry.getAmount());
    }

    lotValueEntry =
        lotValueEntryRepository
            .all()
            .filter(
                "self.lotNo = ?1 AND ( self.entryType = ?2 ) AND self.replaced = false",
                missionLine.getNoLot(),
                LotValueJournalRepository.ENTRYTYPE_RESERVEPRICE5)
            .fetchOne();
    missionLine.setNetReservePrice(BigDecimal.ZERO);
    if (lotValueEntry != null) {
      missionLine.setNetReservePrice(lotValueEntry.getMinAmount());
    }
    return missionLine;
  }
}
