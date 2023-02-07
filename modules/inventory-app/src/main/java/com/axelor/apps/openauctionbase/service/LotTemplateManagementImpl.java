package com.axelor.apps.openauctionbase.service;

import com.axelor.apps.base.db.Partner;
import com.axelor.apps.base.db.Product;
import com.axelor.apps.openauction.db.ActivityHeader;
import com.axelor.apps.openauction.db.ActivityLine;
import com.axelor.apps.openauction.db.Lot;
import com.axelor.apps.openauction.db.LotInputJournal;
import com.axelor.apps.openauction.db.LotQuickInputJournal;
import com.axelor.apps.openauction.db.LotTemplate;
import com.axelor.apps.openauction.db.LotValueJournal;
import com.axelor.apps.openauction.db.MissionHeader;
import com.axelor.apps.openauction.db.MissionLine;
import com.axelor.apps.openauction.db.ServiceTemplateLine;
import com.axelor.apps.openauction.db.repo.LotInputJournalRepository;
import com.axelor.apps.openauction.db.repo.LotQuickInputJournalRepository;
import com.axelor.apps.openauction.db.repo.LotRepository;
import com.axelor.apps.openauction.db.repo.LotValueJournalRepository;
import com.axelor.apps.openauctionbase.validate.LotValidate;
import com.axelor.apps.openauctiontemplate.service.TransferFields;
import com.axelor.exception.AxelorException;
import com.axelor.exception.db.repo.TraceBackRepository;
import com.axelor.inject.Beans;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import java.math.BigDecimal;
import java.util.Date;

public class LotTemplateManagementImpl implements LotTemplateManagement {
  /*
  * MissionLineManagement@1000000008 : Codeunit 8011367;
   ActivityManagement@1000000011 : Codeunit 8011374;
   ContactLotManagement@1000000005 : Codeunit 8011385;
   StatusManagement@1000000010 : Codeunit 8011380;
  */
  ActivityManagement activityManagement;
  ContactLotManagement contactLotManagement;
  MissionStatusManagement statusManagement;
  MissionLineManagement missionLineManagement;
  LotRepository lotRepository;
  LotInputJournalRepository lotInputJournalRepository;
  LotQuickInputJournalRepository lotQuickInputJournalRepository;
  LotValueJournalPostLine lotValueJnlPostLine;
  Lot lot;
  LotValidate lotValidate;

  @Inject
  public LotTemplateManagementImpl(
      LotRepository lotRepository,
      LotInputJournalRepository lotInputJournalRepository,
      LotQuickInputJournalRepository lotQuickInputJournalRepository) {
    activityManagement = Beans.get(ActivityManagement.class);
    contactLotManagement = Beans.get(ContactLotManagement.class);
    statusManagement = Beans.get(MissionStatusManagement.class);
    missionLineManagement = Beans.get(MissionLineManagement.class);
    lotValueJnlPostLine = Beans.get(LotValueJournalPostLine.class);
    this.lotRepository = lotRepository;
    this.lotInputJournalRepository = lotInputJournalRepository;
    this.lotQuickInputJournalRepository = lotQuickInputJournalRepository;
    this.lot = new Lot();
    this.lotValidate = Beans.get(LotValidate.class);
  }

  @Override
  public Lot createLot(LotQuickInputJournal pLotQuickInputJournal, Partner pContactNo)
      throws AxelorException {
    // lLot@1000000002 : Record 8011404;
    Lot lLot;
    LotTemplate lLotTemplate;
    lLotTemplate = pLotQuickInputJournal.getLotTemplateCode();
    if (lLotTemplate == null) {
      return null;
    }
    lLot = new Lot();

    lLot.setLotTemplateCode(pLotQuickInputJournal.getLotTemplateCode());
    // TODO lLotTemplate.CheckBeforeUsage;
    lLot = (Lot) TransferFields.transferFields(lLotTemplate, lLot);
    lLot.setDescription(pLotQuickInputJournal.getDescription());
    lLot.setSearchDescription(lLot.getDescription());
    if (pLotQuickInputJournal.getLotNo() != null) {
      lLot.setNo(pLotQuickInputJournal.getLotNo().getNo());
    }
    lLot.setQuantity(pLotQuickInputJournal.getQuantity());
    lLot.setLotCategorie1Code1(pLotQuickInputJournal.getLotCategorie1Code());
    lLot.setLotCategorie2Code1(pLotQuickInputJournal.getLotCategorie2Code());
    lLot.setLotCategorie3Code1(pLotQuickInputJournal.getLotCategorie3Code());
    lLot.setLotCategorie4Code1(pLotQuickInputJournal.getLotCategorie4Code());
    lLot.setValuationAtBest(pLotQuickInputJournal.getValuationAtBest());
    lLot.setOriginCountryCode(pLotQuickInputJournal.getOriginCountryCode());
    lLot.setAuctionProductFamily(pLotQuickInputJournal.getAuctionProductFamily());
    lLot.setResponsibilityCenter(pLotQuickInputJournal.getResponsibilityCenter());

    lLot.setLawyerBusNo(pLotQuickInputJournal.getLawyerBusNo());
    lLot.setInvLocation(pLotQuickInputJournal.getInvLocation());
    lLot.setInvSubLocation(pLotQuickInputJournal.getInvSubLocation());
    lLot.setInvClassification(pLotQuickInputJournal.getInvClassification());
    lLot.setInvSubClassification(pLotQuickInputJournal.getInvSubClassification());
    lLot.setInvMissionNo(pLotQuickInputJournal.getInvMissionNo());
    lLot.setInvMissionLineNo(pLotQuickInputJournal.getInvMissionLineNo());
    lLot.setLeasingContactNo(pLotQuickInputJournal.getLeasingContactNo());
    lLot.setLeasingContractNo(pLotQuickInputJournal.getLeasingContractNo());
    lLot.setLotConditionCode(pLotQuickInputJournal.getLotConditionCode());
    lLot.setExternalNo(pLotQuickInputJournal.getExternalNo());
    lLot.setInterlocutor(pLotQuickInputJournal.getInterlocutor());
    //   //>> AP14 isat.sf

    //   IF lLot.Vehicle THEN
    //     lLot.VALIDATE("Lot Inventory Status", lLot."Lot Inventory Status"::"To Pick");
    if (lLot.getVehicle()) {
      lLot.setLotInventoryStatus(LotRepository.LOTINVENTORYSTATUS_TO_PICK);
    }

    //   lLot."Date To Auction From" := pLotQuickInputJournal."Date To Auction From";   //AP15
    // isat.zw
    lLot.setDateToAuctionFrom(pLotQuickInputJournal.getDateToAuctionFrom());
    //   //error('++'+format(lLot));

    //   lLot.INSERT(TRUE);

    lLot = lotValidate.onInsert(lLot);
    lotRepository.save(lLot);
    //   LotNo := lLot."No.";

    // TODO LotUnitofMeasure
    //   lLotUnitofMeasure.SETRANGE("No.",lLotTemplate.Code);
    //   IF lLotUnitofMeasure.FINDSET THEN BEGIN
    //     REPEAT
    //       lLotUnitofMeasure2."No." := lLot."No.";
    //       lLotUnitofMeasure2.Code := lLotUnitofMeasure.Code;
    //       lLotUnitofMeasure2."Qty. per Unit of Measure" := lLotUnitofMeasure."Qty. per Unit of
    // Measure";
    //       IF lLotUnitofMeasure2.INSERT(TRUE) THEN;  // debug 290110 isat.sf
    //     UNTIL lLotUnitofMeasure.NEXT = 0;
    //   END;

    contactLotManagement.insertSellerContactbyLot(pContactNo, lLot);

    lot = lLot;
    return lLot;
  }

  @Override
  public void createLotFromContact(LotQuickInputJournal pLotQuickInputJournal, Partner pContact)
      throws AxelorException {
    Lot lLot = new Lot();
    lLot = this.createLot(pLotQuickInputJournal, pContact);
    this.createLotValueEntry(
        pLotQuickInputJournal, lLot, lLot.getCurrentMissionNo(), lLot.getMissionLine());
  }

  @Override
  public Lot createLotFromMission(
      LotQuickInputJournal pLotQuickInputJournal, MissionHeader pMissionHeader)
      throws AxelorException {

    postLotQuickInputFromMission(pLotQuickInputJournal, pMissionHeader);
    // IF LotNo <> '' THEN BEGIN
    if (lot != null) {

      if (!lot.getLotGeneralStatus().equals(LotRepository.LOTGENERALSTATUS_ONMISSION)) {
        lot =
            Beans.get(LotValidate.class)
                .validateLotGeneralStatus(lot, LotRepository.LOTGENERALSTATUS_ONMISSION);
        lot = lotValidate.onInsert(lot);
        lotRepository.save(lot);
      }
    }
    return lot;
  }

  @Override
  public String createLotFromAuction(LotQuickInputJournal pLotQuickInputJournal) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void createLotTemplate(Lot pLotTemplate, String pTypeOrigin) {
    // TODO Auto-generated method stub

  }

  /*
   *
   */
  @Override
  public void createLotValueEntry(
      LotQuickInputJournal pLotQuickInputJournal,
      Lot pLot,
      MissionHeader pMissionHeader,
      MissionLine pMissionLine)
      throws AxelorException {

    /*
       * //Reserve
    IF (pLotQuickInputJournal."Gross Reserve Price" <> 0) OR
       (pLotQuickInputJournal."Net Reserve Price" <> 0)
    THEN BEGIN
      lLotValueJournal.INIT;
      lLotValueJournal."Lot No." := pLot."No.";
      lLotValueJournal."Entry Type" := lLotValueJournal."Entry Type"::"Reserve Price";
      lLotValueJournal."Document Date" := pLotQuickInputJournal."Document Date";
      lLotValueJournal."Posting Date" := pLotQuickInputJournal."Posting Date";
      lLotValueJournal."Mission No." := pMissionHeader."No.";
      lLotValueJournal."Mission Line No." := pMissionLine."Line No.";
      lLotValueJournal."Source Type" := lLotValueJournal."Source Type"::Mission;
      lLotValueJournal.Quantity := pLotQuickInputJournal.Quantity;
      lLotValueJournal.Amount := pLotQuickInputJournal."Gross Reserve Price";
      lLotValueJournal."Min Amount" := pLotQuickInputJournal."Net Reserve Price";
      lLotValueJournal."Currency Code" := pLotQuickInputJournal."Currency Code";
      lLotValueJournal."Currency Factor" := pLotQuickInputJournal."Currency Factor";
      lLotValueJournal."Contact No." :=  pLotQuickInputJournal."Expert Contact No."; // AP13 isat.sf
      lLotValueJnlPostLine.RUN(lLotValueJournal);
    END;
       */

    if (!pLotQuickInputJournal.getGrossReservePrice().equals(BigDecimal.ZERO)
        || !pLotQuickInputJournal.getNetReservePrice().equals(BigDecimal.ZERO)) {
      LotValueJournal lotValueJournal = new LotValueJournal();
      lotValueJournal.setLotNo(pLot);
      lotValueJournal.setEntryType(LotValueJournalRepository.ENTRYTYPE_RESERVEPRICE5);
      lotValueJournal.setDocumentDate(pLotQuickInputJournal.getDocumentDate());
      lotValueJournal.setPostingDate(pLotQuickInputJournal.getPostingDate());
      lotValueJournal.setMissionNo(pMissionHeader);
      lotValueJournal.setMissionLineNo(pMissionLine);
      lotValueJournal.setSourceType(LotValueJournalRepository.SOURCETYPE_MISSION0);
      lotValueJournal.setQuantity(BigDecimal.valueOf(pLotQuickInputJournal.getQuantity()));
      lotValueJournal.setAmount(pLotQuickInputJournal.getGrossReservePrice());
      lotValueJournal.setMinAmount(pLotQuickInputJournal.getNetReservePrice());
      lotValueJournal.setCurrencyCode(pLotQuickInputJournal.getCurrencyCode());
      lotValueJournal.setCurrencyFactor(pLotQuickInputJournal.getCurrencyFactor());
      lotValueJournal.setContactNo(pLotQuickInputJournal.getExpertContactNo());
      lotValueJnlPostLine.run(lotValueJournal);
    }

    /*
       * //Expertise
    IF (pLotQuickInputJournal."Appraisal Value" <> 0) OR
       (pLotQuickInputJournal."Min. Appraisal Value" <> 0) OR
       (pLotQuickInputJournal."Max. Appraisal Value" <> 0)
    THEN BEGIN
      lLotValueJournal.INIT;
      lLotValueJournal."Lot No." := pLot."No.";
      IF pLotQuickInputJournal."Value Type" = pLotQuickInputJournal."Value Type"::Estimate THEN BEGIN
        lLotValueJournal."Entry Type" := lLotValueJournal."Entry Type"::Estimate;
      END ELSE BEGIN
        lLotValueJournal."Entry Type" := lLotValueJournal."Entry Type"::Appraisal;
      END;
      lLotValueJournal."Document Date" := pLotQuickInputJournal."Document Date";
      lLotValueJournal."Posting Date" := pLotQuickInputJournal."Posting Date";
      lLotValueJournal."Mission No." := pMissionHeader."No.";
      lLotValueJournal."Mission Line No." := pMissionLine."Line No.";
      lLotValueJournal."Source Type" := lLotValueJournal."Source Type"::Mission;
      lLotValueJournal.Quantity := pLotQuickInputJournal.Quantity;
      lLotValueJournal.Amount := pLotQuickInputJournal."Appraisal Value";
      lLotValueJournal."Min Amount" := pLotQuickInputJournal."Min. Appraisal Value";
      lLotValueJournal."Max Amount" := pLotQuickInputJournal."Max. Appraisal Value";
      lLotValueJournal."Currency Code" := pLotQuickInputJournal."Currency Code";
      lLotValueJournal."Currency Factor" := pLotQuickInputJournal."Currency Factor";
      lLotValueJournal."Contact No." :=  pLotQuickInputJournal."Expert Contact No."; // AP13 isat.sf
      lLotValueJnlPostLine.RUN(lLotValueJournal);
    END;
       */
    if (!pLotQuickInputJournal.getAppraisalValue().equals(BigDecimal.ZERO)
        || !pLotQuickInputJournal.getMinAppraisalValue().equals(BigDecimal.ZERO)
        || !pLotQuickInputJournal.getMaxAppraisalValue().equals(BigDecimal.ZERO)) {
      LotValueJournal lotValueJournal = new LotValueJournal();
      lotValueJournal.setLotNo(pLot);
      if (pLotQuickInputJournal
          .getValueType()
          .equals(LotQuickInputJournalRepository.VALUETYPE_ESTIMATE)) {
        lotValueJournal.setEntryType(LotValueJournalRepository.ENTRYTYPE_ESTIMATE0);
      } else {
        lotValueJournal.setEntryType(LotValueJournalRepository.ENTRYTYPE_APPRAISAL2);
      }
      lotValueJournal.setDocumentDate(pLotQuickInputJournal.getDocumentDate());
      lotValueJournal.setPostingDate(pLotQuickInputJournal.getPostingDate());
      lotValueJournal.setMissionNo(pMissionHeader);
      lotValueJournal.setMissionLineNo(pMissionLine);
      lotValueJournal.setSourceType(LotValueJournalRepository.SOURCETYPE_MISSION0);
      lotValueJournal.setQuantity(BigDecimal.valueOf(pLotQuickInputJournal.getQuantity()));
      lotValueJournal.setAmount(pLotQuickInputJournal.getAppraisalValue());
      lotValueJournal.setMinAmount(pLotQuickInputJournal.getMinAppraisalValue());
      lotValueJournal.setMaxAmount(pLotQuickInputJournal.getMaxAppraisalValue());
      lotValueJournal.setCurrencyCode(pLotQuickInputJournal.getCurrencyCode());
      lotValueJournal.setCurrencyFactor(pLotQuickInputJournal.getCurrencyFactor());
      lotValueJournal.setContactNo(pLotQuickInputJournal.getExpertContactNo());
      lotValueJnlPostLine.run(lotValueJournal);
    }

    /*
    *
     //Estimation
     IF (pLotQuickInputJournal."Estimate Value" <> 0) OR
        (pLotQuickInputJournal."Min. Estimate Value" <> 0) OR
        (pLotQuickInputJournal."Max. Estimate Value" <> 0)
     THEN BEGIN
       lLotValueJournal.INIT;
       lLotValueJournal."Lot No." := pLot."No.";
       lLotValueJournal."Entry Type" := lLotValueJournal."Entry Type"::Estimate;
       lLotValueJournal."Document Date" := pLotQuickInputJournal."Document Date";
       lLotValueJournal."Posting Date" := pLotQuickInputJournal."Posting Date";
       lLotValueJournal."Mission No." := pMissionHeader."No.";
       lLotValueJournal."Mission Line No." := pMissionLine."Line No.";
       lLotValueJournal."Source Type" := lLotValueJournal."Source Type"::Mission;
       lLotValueJournal.Quantity := pLotQuickInputJournal.Quantity;
       lLotValueJournal.Amount := pLotQuickInputJournal."Estimate Value";
       lLotValueJournal."Min Amount" := pLotQuickInputJournal."Min. Estimate Value";
       lLotValueJournal."Max Amount" := pLotQuickInputJournal."Max. Estimate Value";
       lLotValueJournal."Currency Code" := pLotQuickInputJournal."Currency Code";
       lLotValueJournal."Currency Factor" := pLotQuickInputJournal."Currency Factor";
       lLotValueJournal."Contact No." :=  pLotQuickInputJournal."Expert Contact No."; // AP13 isat.sf
       lLotValueJnlPostLine.RUN(lLotValueJournal);
     END;
    */
    if (!pLotQuickInputJournal.getEstimateValue().equals(BigDecimal.ZERO)
        || !pLotQuickInputJournal.getMinEstimateValue().equals(BigDecimal.ZERO)
        || !pLotQuickInputJournal.getMaxEstimateValue().equals(BigDecimal.ZERO)) {
      LotValueJournal lotValueJournal = new LotValueJournal();
      lotValueJournal.setLotNo(pLot);
      lotValueJournal.setEntryType(LotValueJournalRepository.ENTRYTYPE_ESTIMATE0);
      lotValueJournal.setDocumentDate(pLotQuickInputJournal.getDocumentDate());
      lotValueJournal.setPostingDate(pLotQuickInputJournal.getPostingDate());
      lotValueJournal.setMissionNo(pMissionHeader);
      lotValueJournal.setMissionLineNo(pMissionLine);
      lotValueJournal.setSourceType(LotValueJournalRepository.SOURCETYPE_MISSION0);
      lotValueJournal.setQuantity(BigDecimal.valueOf(pLotQuickInputJournal.getQuantity()));
      lotValueJournal.setAmount(pLotQuickInputJournal.getEstimateValue());
      lotValueJournal.setMinAmount(pLotQuickInputJournal.getMinEstimateValue());
      lotValueJournal.setMaxAmount(pLotQuickInputJournal.getMaxEstimateValue());
      lotValueJournal.setCurrencyCode(pLotQuickInputJournal.getCurrencyCode());
      lotValueJournal.setCurrencyFactor(pLotQuickInputJournal.getCurrencyFactor());
      lotValueJournal.setContactNo(pLotQuickInputJournal.getExpertContactNo());
      lotValueJnlPostLine.run(lotValueJournal);
    }
    /*
    * IF (pLotQuickInputJournal."Min Auction Estim. Value" <> 0) OR
      (pLotQuickInputJournal."Max Auction Estim. Value" <> 0) THEN
     CreateLotValueEntryAuctEstim(pLotQuickInputJournal, pLot, pMissionHeader, pMissionLine);
    */
    if (!pLotQuickInputJournal.getMinAuctionEstimValue().equals(BigDecimal.ZERO)
        || !pLotQuickInputJournal.getMaxAuctionEstimValue().equals(BigDecimal.ZERO)) {
      this.createLotValueEntryAuctEstim(pLotQuickInputJournal, pLot, pMissionHeader, pMissionLine);
    }
  }

  @Override
  public void createLotValueEntryReserve(
      LotQuickInputJournal pLotQuickInputJournal,
      Lot pLot,
      MissionHeader pMissionHeader,
      MissionLine pMissionLine)
      throws AxelorException {
    /*
    * lLotValueJournal.INIT;
     lLotValueJournal."Lot No." := pLot."No.";
     lLotValueJournal."Entry Type" := lLotValueJournal."Entry Type"::"Reserve Price";
     lLotValueJournal."Document Date" := pLotQuickInputJournal."Document Date";
     lLotValueJournal."Posting Date" := pLotQuickInputJournal."Posting Date";
     lLotValueJournal."Mission No." := pMissionHeader."No.";
     lLotValueJournal."Mission Line No." := pMissionLine."Line No.";
     lLotValueJournal."Source Type" := lLotValueJournal."Source Type"::Mission;
     lLotValueJournal.Quantity := pLotQuickInputJournal.Quantity;
     lLotValueJournal.Amount := pLotQuickInputJournal."Gross Reserve Price";
     lLotValueJournal."Min Amount" := pLotQuickInputJournal."Net Reserve Price";
     lLotValueJournal."Currency Code" := pLotQuickInputJournal."Currency Code";
     lLotValueJournal."Currency Factor" := pLotQuickInputJournal."Currency Factor";
     lLotValueJournal."Contact No." :=  pLotQuickInputJournal."Expert Contact No."; // AP13 isat.sf
     lLotValueJnlPostLine.RUN(lLotValueJournal);
    */
    LotValueJournal lotValueJournal = new LotValueJournal();
    lotValueJournal.setLotNo(pLot);
    lotValueJournal.setEntryType(LotValueJournalRepository.ENTRYTYPE_RESERVEPRICE5);
    lotValueJournal.setDocumentDate(pLotQuickInputJournal.getDocumentDate());
    lotValueJournal.setPostingDate(pLotQuickInputJournal.getPostingDate());
    lotValueJournal.setMissionNo(pMissionHeader);
    lotValueJournal.setMissionLineNo(pMissionLine);
    lotValueJournal.setSourceType(LotValueJournalRepository.SOURCETYPE_MISSION0);
    lotValueJournal.setQuantity(BigDecimal.valueOf(pLotQuickInputJournal.getQuantity()));
    lotValueJournal.setAmount(pLotQuickInputJournal.getGrossReservePrice());
    lotValueJournal.setMinAmount(pLotQuickInputJournal.getNetReservePrice());
    lotValueJournal.setCurrencyCode(pLotQuickInputJournal.getCurrencyCode());
    lotValueJournal.setCurrencyFactor(pLotQuickInputJournal.getCurrencyFactor());
    lotValueJournal.setContactNo(pLotQuickInputJournal.getExpertContactNo());
    lotValueJnlPostLine.run(lotValueJournal);
  }

  @Override
  public void createLotValueEntryEstimate(
      LotQuickInputJournal pLotQuickInputJournal,
      Lot pLot,
      MissionHeader pMissionHeader,
      MissionLine pMissionLine)
      throws AxelorException {
    /*
    * //Estimation
     lLotValueJournal.INIT;
     lLotValueJournal."Lot No." := pLot."No.";
     lLotValueJournal."Entry Type" := lLotValueJournal."Entry Type"::Estimate;
     lLotValueJournal."Document Date" := pLotQuickInputJournal."Document Date";
     lLotValueJournal."Posting Date" := pLotQuickInputJournal."Posting Date";
     lLotValueJournal."Mission No." := pMissionHeader."No.";
     lLotValueJournal."Mission Line No." := pMissionLine."Line No.";
     lLotValueJournal."Source Type" := lLotValueJournal."Source Type"::Mission;
     lLotValueJournal.Quantity := pLotQuickInputJournal.Quantity;
     lLotValueJournal.Amount := pLotQuickInputJournal."Estimate Value";
     lLotValueJournal."Min Amount" := pLotQuickInputJournal."Min. Estimate Value";
     lLotValueJournal."Max Amount" := pLotQuickInputJournal."Max. Estimate Value";
     lLotValueJournal."Currency Code" := pLotQuickInputJournal."Currency Code";
     lLotValueJournal."Currency Factor" := pLotQuickInputJournal."Currency Factor";
     lLotValueJournal."Contact No." :=  pLotQuickInputJournal."Expert Contact No."; // AP13 isat.sf
     lLotValueJnlPostLine.RUN(lLotValueJournal);
    */
    LotValueJournal lotValueJournal = new LotValueJournal();
    lotValueJournal.setLotNo(pLot);
    lotValueJournal.setEntryType(LotValueJournalRepository.ENTRYTYPE_ESTIMATE0);
    lotValueJournal.setDocumentDate(pLotQuickInputJournal.getDocumentDate());
    lotValueJournal.setPostingDate(pLotQuickInputJournal.getPostingDate());
    lotValueJournal.setMissionNo(pMissionHeader);
    lotValueJournal.setMissionLineNo(pMissionLine);
    lotValueJournal.setSourceType(LotValueJournalRepository.SOURCETYPE_MISSION0);
    lotValueJournal.setQuantity(BigDecimal.valueOf(pLotQuickInputJournal.getQuantity()));
    lotValueJournal.setAmount(pLotQuickInputJournal.getEstimateValue());
    lotValueJournal.setMinAmount(pLotQuickInputJournal.getMinEstimateValue());
    lotValueJournal.setMaxAmount(pLotQuickInputJournal.getMaxEstimateValue());
    lotValueJournal.setCurrencyCode(pLotQuickInputJournal.getCurrencyCode());
    lotValueJournal.setCurrencyFactor(pLotQuickInputJournal.getCurrencyFactor());
    lotValueJournal.setContactNo(pLotQuickInputJournal.getExpertContactNo());
    lotValueJnlPostLine.run(lotValueJournal);
  }

  @Override
  public void createLotValueEntryAuctEstim(
      LotQuickInputJournal pLotQuickInputJournal,
      Lot pLot,
      MissionHeader pMissionHeader,
      MissionLine pMissionLine)
      throws AxelorException {
    /*
    *
     //Estimation vente
     lLotValueJournal.INIT;
     lLotValueJournal."Lot No." := pLot."No.";
     lLotValueJournal."Entry Type" := lLotValueJournal."Entry Type"::"Auction Estimate";
     lLotValueJournal."Document Date" := pLotQuickInputJournal."Document Date";
     lLotValueJournal."Posting Date" := pLotQuickInputJournal."Posting Date";
     lLotValueJournal."Mission No." := pMissionHeader."No.";
     lLotValueJournal."Mission Line No." := pMissionLine."Line No.";
     lLotValueJournal."Source Type" := lLotValueJournal."Source Type"::Mission;
     lLotValueJournal.Quantity := pLotQuickInputJournal.Quantity;
     lLotValueJournal."Min Amount" := pLotQuickInputJournal."Min Auction Estim. Value";
     lLotValueJournal."Max Amount" := pLotQuickInputJournal."Max Auction Estim. Value";
     lLotValueJournal."Currency Code" := pLotQuickInputJournal."Currency Code";
     lLotValueJournal."Currency Factor" := pLotQuickInputJournal."Currency Factor";
     lLotValueJournal."Contact No." :=  pLotQuickInputJournal."Expert Contact No."; // AP13 isat.sf
     lLotValueJnlPostLine.RUN(lLotValueJournal);
    */
    LotValueJournal lotValueJournal = new LotValueJournal();
    lotValueJournal.setLotNo(pLot);
    lotValueJournal.setEntryType(LotValueJournalRepository.ENTRYTYPE_AUCTIONESTIMATE10);
    lotValueJournal.setDocumentDate(pLotQuickInputJournal.getDocumentDate());
    lotValueJournal.setPostingDate(pLotQuickInputJournal.getPostingDate());
    lotValueJournal.setMissionNo(pMissionHeader);
    lotValueJournal.setMissionLineNo(pMissionLine);
    lotValueJournal.setSourceType(LotValueJournalRepository.SOURCETYPE_MISSION0);
    lotValueJournal.setQuantity(BigDecimal.valueOf(pLotQuickInputJournal.getQuantity()));
    lotValueJournal.setMinAmount(pLotQuickInputJournal.getMinAuctionEstimValue());
    lotValueJournal.setMaxAmount(pLotQuickInputJournal.getMaxAuctionEstimValue());
    lotValueJournal.setCurrencyCode(pLotQuickInputJournal.getCurrencyCode());
    lotValueJournal.setCurrencyFactor(pLotQuickInputJournal.getCurrencyFactor());
    lotValueJournal.setContactNo(pLotQuickInputJournal.getExpertContactNo());
    lotValueJnlPostLine.run(lotValueJournal);
  }

  @Override
  public void createLotValueEntryAppraisal(
      LotQuickInputJournal pLotQuickInputJournal,
      Lot pLot,
      MissionHeader pMissionHeader,
      MissionLine pMissionLine)
      throws AxelorException {
    /*
    * //Expertise
     lLotValueJournal.INIT;
     lLotValueJournal."Lot No." := pLot."No.";
     IF pLotQuickInputJournal."Value Type" = pLotQuickInputJournal."Value Type"::Estimate THEN BEGIN
       lLotValueJournal."Entry Type" := lLotValueJournal."Entry Type"::Estimate;
     END ELSE BEGIN
       lLotValueJournal."Entry Type" := lLotValueJournal."Entry Type"::Appraisal;
     END;
     lLotValueJournal."Document Date" := pLotQuickInputJournal."Document Date";
     lLotValueJournal."Posting Date" := pLotQuickInputJournal."Posting Date";
     lLotValueJournal."Mission No." := pMissionHeader."No.";
     lLotValueJournal."Mission Line No." := pMissionLine."Line No.";
     lLotValueJournal."Source Type" := lLotValueJournal."Source Type"::Mission;
     lLotValueJournal.Quantity := pLotQuickInputJournal.Quantity;
     lLotValueJournal.Amount := pLotQuickInputJournal."Appraisal Value";
     lLotValueJournal."Min Amount" := pLotQuickInputJournal."Min. Appraisal Value";
     lLotValueJournal."Max Amount" := pLotQuickInputJournal."Max. Appraisal Value";
     lLotValueJournal."Currency Code" := pLotQuickInputJournal."Currency Code";
     lLotValueJournal."Currency Factor" := pLotQuickInputJournal."Currency Factor";
     lLotValueJournal."Contact No." :=  pLotQuickInputJournal."Expert Contact No."; // AP13 isat.sf
     lLotValueJnlPostLine.RUN(lLotValueJournal);
    */
    LotValueJournal lotValueJournal = new LotValueJournal();
    lotValueJournal.setLotNo(pLot);
    if (pLotQuickInputJournal.getValueType() == LotQuickInputJournalRepository.VALUETYPE_ESTIMATE) {
      lotValueJournal.setEntryType(LotValueJournalRepository.ENTRYTYPE_ESTIMATE0);
    } else {
      lotValueJournal.setEntryType(LotValueJournalRepository.ENTRYTYPE_APPRAISAL2);
    }
    lotValueJournal.setDocumentDate(pLotQuickInputJournal.getDocumentDate());
    lotValueJournal.setPostingDate(pLotQuickInputJournal.getPostingDate());
    lotValueJournal.setMissionNo(pMissionHeader);
    lotValueJournal.setMissionLineNo(pMissionLine);
    lotValueJournal.setSourceType(LotValueJournalRepository.SOURCETYPE_MISSION0);
    lotValueJournal.setQuantity(BigDecimal.valueOf(pLotQuickInputJournal.getQuantity()));
    lotValueJournal.setAmount(pLotQuickInputJournal.getAppraisalValue());
    lotValueJournal.setMinAmount(pLotQuickInputJournal.getMinAppraisalValue());
    lotValueJournal.setMaxAmount(pLotQuickInputJournal.getMaxAppraisalValue());
    lotValueJournal.setCurrencyCode(pLotQuickInputJournal.getCurrencyCode());
    lotValueJournal.setCurrencyFactor(pLotQuickInputJournal.getCurrencyFactor());
    lotValueJournal.setContactNo(pLotQuickInputJournal.getExpertContactNo());
    lotValueJnlPostLine.run(lotValueJournal);
  }

  @Override
  public void createLotValueEntryQuotation(
      LotQuickInputJournal pLotQuickInputJournal,
      Lot pLot,
      MissionHeader pMissionHeader,
      MissionLine pMissionLine)
      throws AxelorException {
    /*
    * //Expertise
     lLotValueJournal.INIT;
     lLotValueJournal."Lot No." := pLot."No.";
     lLotValueJournal."Entry Type" := lLotValueJournal."Entry Type"::Quotation;
     lLotValueJournal."Document Date" := pLotQuickInputJournal."Document Date";
     lLotValueJournal."Posting Date" := pLotQuickInputJournal."Posting Date";
     lLotValueJournal."Mission No." := pMissionHeader."No.";
     lLotValueJournal."Mission Line No." := pMissionLine."Line No.";
     lLotValueJournal."Source Type" := lLotValueJournal."Source Type"::Mission;
     lLotValueJournal.Quantity := pLotQuickInputJournal.Quantity;
     lLotValueJournal.Amount := pLotQuickInputJournal."Quotation 1";
     lLotValueJournal."Min Amount" := pLotQuickInputJournal."Quotation 2";
     lLotValueJournal."Max Amount" := pLotQuickInputJournal."Quotation 3";
     lLotValueJournal."Currency Code" := pLotQuickInputJournal."Currency Code";
     lLotValueJournal."Currency Factor" := pLotQuickInputJournal."Currency Factor";
     lLotValueJournal."Contact No." :=  pLotQuickInputJournal."Expert Contact No."; // AP13 isat.sf
     lLotValueJnlPostLine.RUN(lLotValueJournal);
    */
    LotValueJournal lotValueJournal = new LotValueJournal();
    lotValueJournal.setLotNo(pLot);
    lotValueJournal.setEntryType(LotValueJournalRepository.ENTRYTYPE_QUOTATION8);
    lotValueJournal.setDocumentDate(pLotQuickInputJournal.getDocumentDate());
    lotValueJournal.setPostingDate(pLotQuickInputJournal.getPostingDate());
    lotValueJournal.setMissionNo(pMissionHeader);
    lotValueJournal.setMissionLineNo(pMissionLine);
    lotValueJournal.setSourceType(LotValueJournalRepository.SOURCETYPE_MISSION0);
    lotValueJournal.setQuantity(BigDecimal.valueOf(pLotQuickInputJournal.getQuantity()));
    lotValueJournal.setAmount(pLotQuickInputJournal.getQuotation1());
    lotValueJournal.setMinAmount(pLotQuickInputJournal.getQuotation2());
    lotValueJournal.setMaxAmount(pLotQuickInputJournal.getQuotation3());
    lotValueJournal.setCurrencyCode(pLotQuickInputJournal.getCurrencyCode());
    lotValueJournal.setCurrencyFactor(pLotQuickInputJournal.getCurrencyFactor());
    lotValueJournal.setContactNo(pLotQuickInputJournal.getExpertContactNo());
    lotValueJnlPostLine.run(lotValueJournal);
  }

  @Override
  public void createLotInventoryEntry(LotQuickInputJournal pLotQuickInputJournal, Lot pLot) {
    /*
    * IF (pLotQuickInputJournal."Location Code" = '') AND
        (pLotQuickInputJournal."Bin Code" = '') AND
        (pLotQuickInputJournal."Auction Room Code" = '')
     THEN BEGIN
       EXIT;
     END;
     WITH lLotInventoryJournal DO BEGIN
       INIT;
       "Journal Template Name" := '';
       "Journal Batch Name" := '';
       "Line No." := 0;
       "Lot No." := pLot."No.";
       "Entry Type" := "Entry Type"::Deposit;
       "Posting Date" := WORKDATE;
       "Document Date" := WORKDATE;
       "Location Code" := pLotQuickInputJournal."Location Code";
       "Bin Code" := pLotQuickInputJournal."Bin Code";
       //"Auction Room Code" := pLotQuickInputJournal."Auction Room Code";
       Quantity := pLotQuickInputJournal.Quantity;
       //"Responsibility Center" := pLot."Responsibility Center";
       "Property Register" := pLot."Property Register";
       "Noble Metal Register" := pLot."Noble Metal Register";
       "Weapon Register" := pLot."Weapon Register";
       "Lot Template Code" := pLotQuickInputJournal."Lot Template Code";
       "Responsibility Center" := pLot."Responsibility Center";
       Rubbish := FALSE;
       "Document No." := lText001;
       "External Document No." := lText001;
       lLotInvtJnlPostLine.RUN(lLotInventoryJournal);
       lLogisticMgt.UpdateLotStatus(pLot."No.",1);
     END;
    */
    if (pLotQuickInputJournal.getLocationCode().isEmpty()
        && pLotQuickInputJournal.getBinCode().isEmpty()
        && pLotQuickInputJournal.getAuctionRoomCode() == null) {
      return;
    }
    // TODO : LotInventoryJournal

  }

  @Override
  public void calcGrossReserveByNetReserve(LotInputJournal pLotInputJournal)
      throws AxelorException {
    if (pLotInputJournal.getLotTemplateCode() == null) {
      throw new AxelorException(
          pLotInputJournal,
          TraceBackRepository.CATEGORY_INCONSISTENCY,
          "Vous devez spécifier le modèle de lot pour calculer le prix de réserve");
    }
    if (pLotInputJournal.getNetReservePrice() != BigDecimal.ZERO) {
      BigDecimal reserveGrossPrice = pLotInputJournal.getNetReservePrice();
      reserveGrossPrice =
          reserveGrossPrice.add(
              calcCommissionWithBaseAmount(
                  pLotInputJournal, pLotInputJournal.getNetReservePrice()));
      reserveGrossPrice = reserveGrossPrice.setScale(0, BigDecimal.ROUND_HALF_UP);
      pLotInputJournal.setGrossReservePrice(reserveGrossPrice);
      lotInputJournalRepository.save(pLotInputJournal);
    }
  }

  @Override
  public void calcNetReserveByGrossReserve(LotInputJournal pLotInputJournal)
      throws AxelorException {
    if (pLotInputJournal.getLotTemplateCode() == null) {
      throw new AxelorException(
          TraceBackRepository.CATEGORY_INCONSISTENCY,
          "Vous devez spécifier le modèle de lot pour calculer le prix de réserve");
    }
    if (pLotInputJournal.getGrossReservePrice() != BigDecimal.ZERO) {
      BigDecimal reserveNetPrice = pLotInputJournal.getGrossReservePrice();
      reserveNetPrice =
          reserveNetPrice.subtract(
              calcCommissionWithBaseAmount(
                  pLotInputJournal, pLotInputJournal.getGrossReservePrice()));
      reserveNetPrice = reserveNetPrice.setScale(0, BigDecimal.ROUND_HALF_UP);
      pLotInputJournal.setNetReservePrice(reserveNetPrice);
      lotInputJournalRepository.save(pLotInputJournal);
    }
  }

  @Override
  public BigDecimal calcCommissionWithBaseAmount(
      LotInputJournal pLotInputJournal, BigDecimal pBaseAmount) {

    MissionHeader lMission = pLotInputJournal.getDocumentNo();
    LotTemplate lLotTemplate = pLotInputJournal.getLotTemplateCode();
    ActivityHeader lActivityHeader = lMission.getActivityCodeToHeader();

    Product lItem;
    MissionServicePriceManagement lMissionServPriceMgt =
        Beans.get(MissionServicePriceManagement.class);
    for (ActivityLine lActivityLine : lActivityHeader.getActivityLineList()) {
      if (lActivityLine.getServiceTemplateCode() != null) {
        for (ServiceTemplateLine lMissionServiceTemplateLine :
            lActivityLine.getServiceTemplateCode().getTemplateLineList()) {
          if (lMissionServiceTemplateLine.getProduct() != null) {
            lItem = lMissionServiceTemplateLine.getProduct();
            // IF lItem."Service Type" = lItem."Service Type"::Commission THEN BEGIN
          }

          //  WITH lMissionServiceLine DO BEGIN
          //                 INIT;
          //                 VALIDATE("Mission No.", pLotInputJournal."Document No.");
          //                 VALIDATE("Transaction Type", "Transaction Type"::Mission);

          //                 //VALIDATE("Lot No.",pLotNo);
          //                 "Lot Template Code" := pLotInputJournal."Lot Template Code";
          //                 "Responsibility Center" := lMission."Responsibility Center";
          //                 CASE "Transaction Type" OF
          //                   "Transaction Type"::Mission : BEGIN
          //                      "Lot Price Group" := lLotTemplate."Lot Mission Price Group";
          //                      //"Transaction Line No." := Lot."Current Mission Line No.";
          //                   END;
          //                 END;
          //                 VALIDATE(Type,lMissionServiceTemplateLine.Type);
          //                 VALIDATE("No.",lMissionServiceTemplateLine."No.");
          //                 IF lItem."Use Lot VAT Posting Group" THEN
          //                   VALIDATE("VAT Prod. Posting Group", lLotTemplate."VAT Prod. Posting
          // Group");
          // //                VALIDATE("Chargeable Contact No.",lMission."Master Contact No.");
          //                 IF lMissionServiceTemplateLine."Unit of Measure Code" <> '' THEN BEGIN
          //                   "Unit of Measure Code" := lMissionServiceTemplateLine."Unit of
          // Measure Code";
          //                 END;
          //                 "Mis. Service Template Code" := lMissionServiceTemplateLine."Service
          // Template Code";
          //                 VALIDATE(Quantity,1);
          //                 "Accept To Invoice" := TRUE;
          //                 "Activity Header" := lActivityLine."Activity Code";
          //                 "Activity Line" := lActivityLine."Line No.";
          //                 lMissionServPriceMgt.FindMissServPWithBaseAmount(lMissionServiceLine,
          // pBaseAmount, FALSE);
          //                 rAmountCommission += lMissionServiceLine."Amount Incl. VAT";
          //               END;
          //             END;
        }
      }
    }

    // Ap09 isat.zw 17/11/08

    // IF NOT lActivityHeader.GET(lMission."Activity Code To Lines") THEN
    //   CLEAR(lActivityHeader);
    // lActivityLine.SETRANGE(lActivityLine."Activity Code", lActivityHeader.Code);
    // IF lActivityLine.FINDSET(FALSE, FALSE) THEN BEGIN
    //   REPEAT
    //     IF lActivityLine."Service Template Code" <> '' THEN BEGIN
    //       lMissionServiceTemplateLine.RESET;

    //       lMissionServiceTemplateLine.SETRANGE("Service Template Code",lActivityLine."Service
    // Template Code");
    //       IF lMissionServiceTemplateLine.FIND('-') THEN BEGIN
    //         REPEAT
    //
    //           END;
    //         UNTIL lMissionServiceTemplateLine.NEXT = 0;
    //       END;
    //     END;
    //   UNTIL lActivityLine.NEXT = 0;
    // END;
    return null;
  }

  @Override
  public String createLotFromJudicialMission(
      MissionLine pOriginMissionLine, LotQuickInputJournal pLotQuickInputJournal) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void postLotQuickInputFromJuMission(
      LotQuickInputJournal pLotQuickInputJournal, MissionLine pOriginMissionLine) {
    // TODO Auto-generated method stub

  }

  @Override
  public void createLotInvValueEntry(MissionLine pMissionLine) {
    // TODO Auto-generated method stub

  }

  @Override
  public void createLotValueEntryFromLot(
      String pLotNo,
      Date pPostingDate,
      String pEntryType,
      BigDecimal pAmount,
      BigDecimal pMinAmount,
      BigDecimal pMaxAmount,
      Partner pContactNo) {
    // TODO Auto-generated method stub

  }

  @Override
  public void setToRegistIntegrationLot(
      Boolean pToRegistIntegrationLot, Integer pRegistIntegrationEntryNo) {
    // TODO Auto-generated method stub

  }

  @Override
  public Lot getLotNoCreated() {
    return lot;
  }

  /*
  * PROCEDURE PostLotQuickInputFromMission@1000000017(VAR pLotQuickInputJournal@1000000006 : Record 8011467;pMissionHeader@1000000005 : Record 8011402);
   VAR
     lFileInterfacePostData@1180113000 : Codeunit 8011645;
     lCountryRegion@1000000011 : Record 9;
     lLot@1000000002 : Record 8011404;
     lActivityHeader@1000000003 : Record 8011405;
     lMissionLine@1000000001 : Record 8011403;
     lContact@1000000004 : Record 5050;
     lMissionLineNo@1000000000 : Integer;
     lLotNoMission@1100481000 : Integer;
   BEGIN
     //AP04.isat.SC
     lLotNoMission := pLotQuickInputJournal."Lot No./Mission"; // AP18 isat.sf
     IF pLotQuickInputJournal."Line Type" = pLotQuickInputJournal."Line Type"::" " THEN BEGIN
       IF NOT HideMessage THEN BEGIN
         WDialog.UPDATE(3,Text8011401);
       END;

       CreateLot(pLotQuickInputJournal,lLot,pMissionHeader."Master Contact No.");
       IF NOT HideMessage THEN BEGIN
         WDialog.UPDATE(3,Text8011401);
       END;

     //  IF lLot."Lot General Status" <> lLot."Lot General Status"::"On Mission" THEN BEGIN
     //    lLot."Lot General Status" := lLot."Lot General Status"::"On Mission";
     //    lLot.MODIFY(TRUE);
     //  END;

       pLotQuickInputJournal."Lot No./Mission" := lLotNoMission; // AP18 isat.sf

       MissionLineManagement.CreateMissionLine(pMissionHeader,lMissionLine,lLot,pLotQuickInputJournal);
       IF NOT HideMessage THEN BEGIN
         WDialog.UPDATE(3,Text8011406);
       END;
       CreateLotValueEntry(pLotQuickInputJournal,lLot,pMissionHeader,lMissionLine);
       IF NOT HideMessage THEN BEGIN
         WDialog.UPDATE(3,Text8011404);
       END;
       IF lContact.GET(pMissionHeader."Master Contact No.") THEN BEGIN
         ContactLotManagement.InsertSellerContactbyLot(lContact."No.",lLot."No.");
       END;
       IF NOT HideMessage THEN BEGIN
         WDialog.UPDATE(3,Text8011403);
       END;
       IF ToRegistIntegrationLot THEN                                                      //ap21 isat.zw
         lFileInterfacePostData.RegisterIntegrationLot(lLot."No.", 0, '', 0, RegistIntegrationEntryNo);     //ap21 isat.zw

       IF lActivityHeader.GET(pMissionHeader."Activity Code To Lines") THEN BEGIN
         ActivityManagement.CreateActivityLineFromMission(lActivityHeader,pMissionHeader,lMissionLine,FALSE);
       END ELSE BEGIN
         StatusManagement.CheckStatus(lMissionLine);
       END;
       //<<AP03.isat.pC
       IF pLotQuickInputJournal."Origin Country Code" <> '' THEN
         IF lCountryRegion.GET(pLotQuickInputJournal."Origin Country Code") THEN BEGIN
           IF (lCountryRegion."EU Country/Region Code" = '') OR
              (lCountryRegion."Intrastat Code" = '')
           THEN BEGIN
             ActivityManagement.CreateCustomDues(pMissionHeader."No.",'',lLot."No.",pMissionHeader."Master Contact No.",
                    lMissionLine."Line No."); //AP08
           END;
         END;
       //>>AP03.isat.pC
       //<<AP05.isat.PC
       IF NOT HideMessage THEN BEGIN
         WDialog.UPDATE(3,Text8011405);
       END;
       //CreateLotInventoryEntry(pLotQuickInputJournal,lLot); //Ap12 isat.zw
       //>>AP05.isat.PC
     END ELSE BEGIN
       MissionLineManagement.CreateCommentMissionLine(pMissionHeader,pLotQuickInputJournal);
     END;
     IF pLotQuickInputJournal.FINDFIRST THEN BEGIN
       pLotQuickInputJournal.DELETE(TRUE);
     END;
   END;
  */
  @Override
  @Transactional(rollbackOn = {AxelorException.class, Exception.class})
  public void postLotQuickInputFromMission(
      LotQuickInputJournal pLotQuickInputJournal, MissionHeader pMissionHeader)
      throws AxelorException {
    Lot lLot = new Lot();
    MissionLine lMissionLine = new MissionLine();

    if (pLotQuickInputJournal.getLineType() == null) {

      lLot = this.createLot(pLotQuickInputJournal, pMissionHeader.getMasterContactNo());

      lMissionLine =
          missionLineManagement.createMissionLine(pMissionHeader, lLot, pLotQuickInputJournal);

      this.createLotValueEntry(pLotQuickInputJournal, lLot, pMissionHeader, lMissionLine);

      if (pMissionHeader.getMasterContactNo() != null) {
        contactLotManagement.insertSellerContactbyLot(pMissionHeader.getMasterContactNo(), lLot);
      }

      if (pMissionHeader.getActivityCodeToLines() != null) {
        activityManagement.createActivityLineFromMission(
            pMissionHeader.getActivityCodeToLines(), pMissionHeader, lMissionLine, false);
      } else {
        statusManagement.checkStatus(lMissionLine);
      }

    } else {
      missionLineManagement.createCommentMissionLine(pMissionHeader, pLotQuickInputJournal);
    }
    // pMissionHeader.getLotInputJournalList().remove(pLotQuickInputJournal);
    // Beans.get(MissionHeaderRepository.class).save(pMissionHeader);
    // lotQuickInputJournalRepository.remove(pLotQuickInputJournal);
  }
}
