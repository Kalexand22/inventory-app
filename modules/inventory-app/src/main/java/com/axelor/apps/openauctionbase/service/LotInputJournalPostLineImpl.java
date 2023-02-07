package com.axelor.apps.openauctionbase.service;

import com.axelor.apps.base.service.app.AppBaseService;
import com.axelor.apps.openauction.db.Lot;
import com.axelor.apps.openauction.db.LotInputJournal;
import com.axelor.apps.openauction.db.LotQuickInputJournal;
import com.axelor.apps.openauction.db.MissionHeader;
import com.axelor.apps.openauction.db.repo.LotInputJournalRepository;
import com.axelor.apps.openauction.db.repo.LotRepository;
import com.axelor.apps.openauctionbase.validate.LotQuickJournalInputValidate;
import com.axelor.apps.openauctiontemplate.service.TransferFields;
import com.axelor.auth.AuthUtils;
import com.axelor.exception.AxelorException;
import com.axelor.inject.Beans;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import java.time.LocalDate;

public class LotInputJournalPostLineImpl implements LotInputJournalPostLine {
  // LotInputJournal@1000000000 : Record 8011498;
  LotInputJournal lotInputJournal;
  LotRepository lotRepo;

  @Inject
  public LotInputJournalPostLineImpl(LotRepository lotRepo) {
    this.lotRepo = lotRepo;
  }

  @Override
  @Transactional(rollbackOn = {AxelorException.class})
  public void runMissionHeader(MissionHeader pMissionHeader) throws AxelorException {
    for (LotInputJournal lLotInputJournal : pMissionHeader.getLotInputJournalList()) {
      lotInputJournal = lLotInputJournal;
      code();
    }
  }

  private void code() throws AxelorException {
    /*
       * lMissionHeader@1000000002 : Record 8011402;
    lTempLotQuickInputJournal@1000000003 : TEMPORARY Record 8011467;
    lAPLotTemplateMgt@1000000004 : Codeunit 8011377;
    lTextWarehouse@1000000012 : Record 8011363;
    lLotNoCreated@1000000011 : Code[20];
    lOutStream@1000000013 : OutStream;
       */
    MissionHeader lMissionHeader = lotInputJournal.getDocumentNo();
    LotQuickInputJournal lTempLotQuickInputJournal = new LotQuickInputJournal();
    LotTemplateManagement lAPLotTemplateMgt = Beans.get(LotTemplateManagement.class);
    LotQuickJournalInputValidate lLotQuickJournalInputValidate =
        Beans.get(LotQuickJournalInputValidate.class);

    lTempLotQuickInputJournal.setMissionNo(lotInputJournal.getDocumentNo());
    lTempLotQuickInputJournal.setMissionLineNo(lotInputJournal.getDocumentLineNo());
    lTempLotQuickInputJournal.setValuationAtBest(lotInputJournal.getValuationAtBest());
    lTempLotQuickInputJournal =
        lLotQuickJournalInputValidate.validateLotTemplateCode(
            lTempLotQuickInputJournal, lotInputJournal.getLotTemplateCode());
    lTempLotQuickInputJournal =
        lLotQuickJournalInputValidate.validateLotTemplateCode(
            lTempLotQuickInputJournal, lotInputJournal.getLotTemplateCode());
    // lTempLotQuickInputJournal.setLotTemplateCode(lotInputJournal.getLotTemplateCode());
    lTempLotQuickInputJournal.setDescription(lotInputJournal.getDescription());
    lTempLotQuickInputJournal.setQuantity(lotInputJournal.getQuantity());
    lTempLotQuickInputJournal.setAppraisalValue(lotInputJournal.getValue());
    lTempLotQuickInputJournal.setMinAppraisalValue(lotInputJournal.getMinimumValue());
    lTempLotQuickInputJournal.setMaxAppraisalValue(lotInputJournal.getMaximumValue());
    lTempLotQuickInputJournal.setOriginCountryCode(lotInputJournal.getOriginCountryCode());
    lTempLotQuickInputJournal.setValueType(lotInputJournal.getValueType());
    lTempLotQuickInputJournal.setGrossReservePrice(lotInputJournal.getGrossReservePrice());
    lTempLotQuickInputJournal.setNetReservePrice(lotInputJournal.getNetReservePrice());
    lTempLotQuickInputJournal.setMinAuctionEstimValue(lotInputJournal.getMinAuctionEstimValue());
    lTempLotQuickInputJournal.setMaxAuctionEstimValue(lotInputJournal.getMaxAuctionEstimValue());
    LocalDate workDate =
        Beans.get(AppBaseService.class).getTodayDate(AuthUtils.getUser().getActiveCompany());
    lTempLotQuickInputJournal.setPostingDate(workDate);
    lTempLotQuickInputJournal.setDocumentDate(workDate);
    lTempLotQuickInputJournal.setDescription(lotInputJournal.getDescription());
    lTempLotQuickInputJournal.setCurrencyCode(lotInputJournal.getCurrencyCode());
    lTempLotQuickInputJournal.setCurrencyFactor(lotInputJournal.getCurrencyFactor());
    lTempLotQuickInputJournal.setIndentation(lotInputJournal.getIndentation());
    lTempLotQuickInputJournal.setLineType(lotInputJournal.getLineType());
    lTempLotQuickInputJournal.setLocationCode(lotInputJournal.getLocationCode());
    lTempLotQuickInputJournal.setBinCode(lotInputJournal.getBinCode());
    lTempLotQuickInputJournal.setAuctionRoomCode(lotInputJournal.getAuctionRoomCode());
    lTempLotQuickInputJournal.setOriginCountryCode(lotInputJournal.getOriginCountryCode());
    lTempLotQuickInputJournal.setResponsibilityCenter(lMissionHeader.getResponsibilityCenter());
    lTempLotQuickInputJournal.setMainTextEntryNo(lotInputJournal.getMainTextEntryNo());
    lTempLotQuickInputJournal.setTemporaryBlob(lotInputJournal.getTemporaryBlob());
    lTempLotQuickInputJournal.setLotNo(lotInputJournal.getLotNo());
    lTempLotQuickInputJournal.setDateToAuctionFrom(lotInputJournal.getDateToAuctionfrom());
    lTempLotQuickInputJournal.setLotConditionCode(lotInputJournal.getLotConditionCode());
    lTempLotQuickInputJournal.setLotNoMission(lotInputJournal.getLotNoMission());
    lTempLotQuickInputJournal.setExternalNo(lotInputJournal.getExternalNo());
    lTempLotQuickInputJournal.setInterlocutor(lotInputJournal.getInterlocutor());
    /*
     * lAPLotTemplateMgt.CreateLotFromMission(lTempLotQuickInputJournal,lMissionHeader);
    lAPLotTemplateMgt.GetLotNoCreated(lLotNoCreated);
    LotInputJournal."Created Lot No." := lLotNoCreated; //Ap03 isat.zw
     */
    Lot lot = lAPLotTemplateMgt.createLotFromMission(lTempLotQuickInputJournal, lMissionHeader);

    lot = lAPLotTemplateMgt.getLotNoCreated();
    lotInputJournal.setCreatedLotNo(lot.getNo());
    // lot = transferFields(lotInputJournal, lot);
    lot = (Lot) TransferFields.transferFields(lotInputJournal, lot);
    lotRepo.save(lot);

    LotInputJournalRepository lotInputJournalRepo = Beans.get(LotInputJournalRepository.class);
    lotInputJournalRepo.remove(lotInputJournal);
  }
}
