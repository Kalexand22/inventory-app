package com.axelor.apps.openauctionbase.validate;

import com.axelor.apps.base.db.Country;
import com.axelor.apps.base.db.ProductFamily;
import com.axelor.apps.base.service.administration.SequenceService;
import com.axelor.apps.openauction.db.AuctionHeader;
import com.axelor.apps.openauction.db.AuctionLotPriceGroup;
import com.axelor.apps.openauction.db.FreeReason;
import com.axelor.apps.openauction.db.Lot;
import com.axelor.apps.openauction.db.LotCondition;
import com.axelor.apps.openauction.db.LotNature;
import com.axelor.apps.openauction.db.LotSetup;
import com.axelor.apps.openauction.db.LotTemplate;
import com.axelor.apps.openauction.db.LotUnitofMeasure;
import com.axelor.apps.openauction.db.MissionHeader;
import com.axelor.apps.openauction.db.MissionLine;
import com.axelor.apps.openauction.db.MissionLotPriceGroup;
import com.axelor.apps.openauction.db.MissionServiceLine;
import com.axelor.apps.openauction.db.Sector;
import com.axelor.apps.openauction.db.repo.LotRepository;
import com.axelor.apps.openauction.db.repo.LotSetupRepository;
import com.axelor.apps.openauction.db.repo.MissionLineRepository;
import com.axelor.apps.openauction.db.repo.MissionServiceLineRepository;
import com.axelor.apps.openauctionbase.service.LotStatusMgt;
import com.axelor.apps.openauctionbase.service.TransportAnalysisUpdate;
import com.axelor.apps.openauctionbase.service.WebSynchroManagement;
import com.axelor.exception.AxelorException;
import com.axelor.exception.db.repo.TraceBackRepository;
import com.axelor.inject.Beans;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import java.math.BigDecimal;
import java.util.List;

public class LotValidate {
  MissionServiceLineRepository missionServiceLineRepo;
  MissionLineRepository missionLineRepo;
  LotSetup lotSetup;

  @Inject
  public LotValidate(
      MissionServiceLineRepository missionServiceLineRepo, MissionLineRepository missionLineRepo) {
    this.missionServiceLineRepo = missionServiceLineRepo;
    this.missionLineRepo = missionLineRepo;
  }

  /*//Sector_Code Code10
  //OnValidateVAR
  //lSector@1000000000 : Record 8011305;
                                                             BEGIN
  //IF NOT lSector.GET("Sector Code") THEN BEGIN
  //  lSector.INIT;
  //END;
  //Vehicle := lSector.Automotive;
  //Equipment := lSector.Equipment;
  //"Art Object" := lSector."Art Object";// */
  public Lot validateSectorCode(Lot lot, Sector sectorCode) {
    lot.setSectorCode(sectorCode);
    lot.setVehicle(sectorCode.getAutomotive());
    lot.setEquipment(sectorCode.getEquipment());
    lot.setArtObject(sectorCode.getArtObject());
    return lot;
  }
  /*
    * //Grouping_Authorization Boolean
  //OnValidateBEGIN
  //TESTFIELD("Lot Type", "Lot Type"::Lot); //AP32.ST//
    */
  public Lot validateGroupingAuthorization(Lot lot, Boolean groupingAuthorization)
      throws AxelorException {

    if (lot.getLotType() != null && !lot.getLotType().equals(LotRepository.LOTTYPE_LOT)) {
      throw new AxelorException(
          lot,
          TraceBackRepository.CATEGORY_CONFIGURATION_ERROR,
          "Le champ Autorisation de groupement ne peut être coché que si le type de lot est Lot");
    }
    lot.setGroupingAuthorization(groupingAuthorization);
    return lot;
  }
  // Ungrouping_Authorization Boolean
  // OnValidateBEGIN
  // TESTFIELD("Lot Type", "Lot Type"::Lot); //AP32.ST//
  public Lot validateUngroupingAuthorization(Lot lot, Boolean ungroupingAuthorization)
      throws AxelorException {

    if (lot.getLotType() != null && !lot.getLotType().equals(LotRepository.LOTTYPE_LOT)) {
      throw new AxelorException(
          lot,
          TraceBackRepository.CATEGORY_CONFIGURATION_ERROR,
          "Le champ Autorisation de dégroupement ne peut être coché que si le type de lot est Lot");
    }
    lot.setUngroupingAuthorization(ungroupingAuthorization);
    return lot;
  }

  /*
    * //Lot_Mission_Price_Group Code10
  //OnValidateVAR
  //lMissServLine@1100481000 : Record 8011449;
                                                             BEGIN
  ////<<AP52.ST
  //lMissServLine.SETCURRENTKEY("Lot No.");
  //lMissServLine.SETRANGE("Lot No.", "No.");
  //lMissServLine.SETRANGE("Transaction Type", lMissServLine."Transaction Type"::Mission);
  //lMissServLine.SETFILTER("Lot Price Group", '<>%1',"Lot Mission Price Group");
  //lMissServLine.SETFILTER("Invoiced Quantity", '=%1',0);
  ////<<ap62 isat.zw
  ////IF lMissServLine.FINDFIRST THEN
  //IF lMissServLine.FINDSET(TRUE) THEN
  ////>>ap62 isat.zw
  //  REPEAT
  //    lMissServLine.VALIDATE("Lot Price Group", "Lot Mission Price Group");
  //    lMissServLine.MODIFY(TRUE);
  //  UNTIL lMissServLine.NEXT=0;
  ////>>AP52.ST//
    */
  @Transactional
  public Lot validateLotMissionPriceGroup(Lot lot, MissionLotPriceGroup lotMissionPriceGroup)
      throws AxelorException {
    lot.setLotMissionPriceGroup(lotMissionPriceGroup);
    List<MissionServiceLine> missionServiceLineList;
    MissionServiceLineValidate missionServiceLineValidate = new MissionServiceLineValidate();

    missionServiceLineList =
        Beans.get(MissionServiceLineRepository.class)
            .all()
            .filter(
                "self.lotNo = ?1 AND self.transactionType = ?2 AND self.lotPriceGroup != ?3 AND self.invoicedQuantity = 0",
                lot,
                MissionServiceLineRepository.TRANSACTIONTYPE_MISSION,
                lotMissionPriceGroup)
            .fetch();

    for (MissionServiceLine missionServiceLine : missionServiceLineList) {
      missionServiceLine =
          missionServiceLineValidate.validateLotPriceGroup(
              missionServiceLine, lotMissionPriceGroup);
      // TODO MODIFY(TRUE)
      missionServiceLineRepo.save(missionServiceLine);
    }
    return lot;
  }

  /*
    * //Lot_Auction_Price_Group Code10
  //OnValidateVAR
  //lMissServLine@1100481000 : Record 8011449;
                                                             BEGIN
  ////<<AP52.ST
  //lMissServLine.SETCURRENTKEY("Lot No.");
  //lMissServLine.SETRANGE("Lot No.", "No.");
  //lMissServLine.SETRANGE("Transaction Type", lMissServLine."Transaction Type"::Auction);
  //lMissServLine.SETFILTER("Lot Price Group", '<>%1',"Lot Auction Price Group");
  //lMissServLine.SETFILTER("Invoiced Quantity", '=%1',0);
  ////<<ap62 isat.zw
  ////IF lMissServLine.FINDFIRST THEN
  //IF lMissServLine.FINDSET(TRUE) THEN
  ////>>ap62 isat.zw
  //  REPEAT
  //    lMissServLine.VALIDATE("Lot Price Group", "Lot Auction Price Group");
  //    lMissServLine.MODIFY(TRUE);
  //  UNTIL lMissServLine.NEXT=0;
  ////>>AP52.ST//
    */
  @Transactional
  public Lot validateLotAuctionPriceGroup(Lot lot, AuctionLotPriceGroup lotAuctionPriceGroup)
      throws AxelorException {
    lot.setLotAuctionPriceGroup(lotAuctionPriceGroup);
    List<MissionServiceLine> missionServiceLineList;
    MissionServiceLineValidate missionServiceLineValidate = new MissionServiceLineValidate();

    missionServiceLineList =
        Beans.get(MissionServiceLineRepository.class)
            .all()
            .filter(
                "self.lotNo = ?1 AND self.transactionType = ?2 AND self.lotPriceGroup != ?3 AND self.invoicedQuantity = 0",
                lot,
                MissionServiceLineRepository.TRANSACTIONTYPE_VENTE,
                lotAuctionPriceGroup)
            .fetch();

    for (MissionServiceLine missionServiceLine : missionServiceLineList) {
      missionServiceLine =
          missionServiceLineValidate.validateAuctionLotPriceGroup(
              missionServiceLine, lotAuctionPriceGroup);
      // TODO MODIFY(TRUE)
      missionServiceLineRepo.save(missionServiceLine);
    }
    return lot;
  }
  /*
    * //Added-Value_Type Option
  //OnValidateBEGIN
  ////<<AP63.ISAT.EBA
  //IF "Added-Value Type" <> "Added-Value Type"::Free THEN
  //  "Free Reason Code" := '';
  ////>>AP63.ISAT.EBA

  ////AP18
  //IF ("Added-Value Type" <> xRec."Added-Value Type") THEN BEGIN
  //  RecreateAddedValue(Rec);   // AP34 isat.sf
  //END;//
    */
  public Lot validateAddedValueType(Lot lot, String addedValueType) throws AxelorException {
    if (addedValueType != null && !addedValueType.equals(LotRepository.ADDEDVALUETYPE_FREE)) {
      lot.setFreeReasonCode(null);
    }
    if (!lot.getAddedValueType().equals(lot.getAddedValueType())) {
      lot = recreateAddedValue(lot);
    }
    return lot;
  }

  private Lot recreateAddedValue(Lot lot) {
    // TODO : recreateAddedValue
    return null;
  }

  // Free_Reason_Code Code10
  // OnValidateBEGIN
  //// <<AP63.ISAT.EBA
  // IF "Free Reason Code" <> '' THEN
  //  TESTFIELD("Added-Value Type","Added-Value Type"::Free);
  //// >>AP63.ISAT.EBA//
  public Lot validateFreeReasonCode(Lot lot, FreeReason freeReasonCode) throws AxelorException {
    if (freeReasonCode != null
        && lot.getAddedValueType() != null
        && !lot.getAddedValueType().equals(LotRepository.ADDEDVALUETYPE_FREE)) {
      throw new AxelorException(
          lot,
          TraceBackRepository.CATEGORY_INCONSISTENCY,
          "Le champs Type de plus-value doit être de type Exonéré");
    }
    lot.setFreeReasonCode(freeReasonCode);
    return lot;
  }

  /*
    * //Lot_General_Status Option
  //OnValidateVAR
  //lLotStatusMgt@1100481000 : Codeunit 8011424;
  //lVehicleLotDescrip@1100481001 : Record 8011438;
                                                             BEGIN
  //lLotStatusMgt.RUN(Rec);
  ////AP39 isat.zw synchro les statuts de la table vehicule
  //IF Vehicle THEN BEGIN
  //  IF lVehicleLotDescrip.GET("No.") THEN BEGIN
  //    lVehicleLotDescrip."Lot General Status" := "Lot General Status";
  //    lVehicleLotDescrip.MODIFY;
  //  END;
  //END;//
    */
  public Lot validateLotGeneralStatus(Lot lot, Integer lotGeneralStatus) {
    lot.setLotGeneralStatus(lotGeneralStatus);
    LotStatusMgt lotStatusMgt = Beans.get(LotStatusMgt.class);
    lot = lotStatusMgt.CheckGeneralStatus(lot);
    return lot;
  }

  /*
    * //Auction_Status Option
  //OnValidateVAR
  //lVehicleLotDescrip@1100481001 : Record 8011438;
  //lLotStatusMgt@1100481000 : Codeunit 8011424;
                                                             BEGIN
  //lLotStatusMgt.UpdLotAuctionStatus(Rec);
  ////AP39 isat.zw synchro les statuts de la table vehicule
  //IF Vehicle THEN BEGIN
  //  IF lVehicleLotDescrip.GET("No.") THEN BEGIN
  //    lVehicleLotDescrip."Lot Auction Status" := "Auction Status";
  //    lVehicleLotDescrip.MODIFY;
  //  END;
  //END;//
    */
  public Lot validateAuctionStatus(Lot lot, Integer auctionStatus) {
    lot.setAuctionStatus(auctionStatus);
    LotStatusMgt lotStatusMgt = Beans.get(LotStatusMgt.class);
    lot = lotStatusMgt.UpdLotAuctionStatus(lot);
    return lot;
  }

  /*
    * //Lot_Condition_Code Code10
  //OnValidateVAR
  //lMissionLine@1180113000 : Record 8011403;
                                                             BEGIN
  ////ap64 isat.sc
  //IF lMissionLine.GET("Current Mission No.", "Current Mission Line No.") THEN BEGIN
  //  lMissionLine."Lot Condition" := "Lot Condition Code";
  //  lMissionLine.MODIFY(FALSE);
  //END;//

    */
  @Transactional
  public Lot validateLotConditionCode(Lot lot, LotCondition lotConditionCode) {
    lot.setLotConditionCode(lotConditionCode);
    MissionLine lMissionLine = lot.getCurrentMissionLineNo();
    if (lMissionLine != null) {
      lMissionLine.setLotCondition(lotConditionCode);
      missionLineRepo.save(lMissionLine);
    }
    return lot;
  }

  /*
    * //Lot_Inventory_Status Option
  //OnValidateVAR
  //lLotStatusMgt@1100481000 : Codeunit 8011424;
  //lVehicleLotDescrip@1100481001 : Record 8011438;
                                                             BEGIN
  //lLotStatusMgt.UpdLotInventoryStatus(Rec);
  ////AP39 isat.zw synchro statut stock de la table vehicule
  //IF Vehicle THEN BEGIN
  //  IF lVehicleLotDescrip.GET("No.") THEN BEGIN
  //    lVehicleLotDescrip."Lot Inventory Status" := "Lot Inventory Status";
  //    lVehicleLotDescrip.MODIFY;
  //  END;
  //END;//
    */
  public Lot validateLotInventoryStatus(Lot lot, Integer lotInventoryStatus) {
    lot.setLotInventoryStatus(lotInventoryStatus);
    LotStatusMgt lotStatusMgt = Beans.get(LotStatusMgt.class);
    lot = lotStatusMgt.UpdLotInventoryStatus(lot);
    return lot;
  }

  /*
    * //Cur_Mis_Lot_Operation_Status Option
  //OnValidateVAR
  //lLotStatusMgt@1100481000 : Codeunit 8011424;
                                                             BEGIN
  //lLotStatusMgt.UpdLotOperationStatus(Rec);//
    */
  public Lot validateCurMisLotOperationStatus(Lot lot, Integer curMisLotOperationStatus) {
    lot.setCurMisLotOperationStatus(curMisLotOperationStatus);
    LotStatusMgt lotStatusMgt = Beans.get(LotStatusMgt.class);
    lot = lotStatusMgt.UpdLotOperationStatus(lot);
    return lot;
  }

  /*
    * //Current_Mission_Lot_Doc_Status Option
  //OnValidateVAR
  //lLotStatusMgt@1100481000 : Codeunit 8011424;
                                                             BEGIN
  //lLotStatusMgt.UpdLotMissionStatus(Rec);//
    */
  public Lot validateCurrentMissionLotDocStatus(Lot lot, Integer currentMissionLotDocStatus) {
    lot.setCurrentMissionLotDocStatus(currentMissionLotDocStatus);
    LotStatusMgt lotStatusMgt = Beans.get(LotStatusMgt.class);
    lot = lotStatusMgt.UpdLotMissionStatus(lot);
    return lot;
  }

  /*
    * //Lot_Template_Code Code10
  //OnValidateBEGIN
  //IF ("Lot Template Code" <> xRec."Lot Template Code") AND  (xRec."Lot Template Code" <> '') THEN
  //  IF NOT HideConfirmDialog THEN
  //    IF NOT CONFIRM(Text8011402) THEN ERROR('');

  //IF LotTemplate.GET("Lot Template Code") THEN BEGIN
  //  IF LotTemplate."Lot Nature Code" <> "Lot Nature Code" THEN
  //    VALIDATE("Lot Nature Code", LotTemplate."Lot Nature Code");
  //  IF LotTemplate."Auction Prod. Posting Group" <> "Auction Prod. Posting Group" THEN
  //    VALIDATE("Auction Prod. Posting Group", LotTemplate."Auction Prod. Posting Group");
  //  IF LotTemplate."VAT Prod. Posting Group" <> "VAT Prod. Posting Group" THEN
  //    VALIDATE("VAT Prod. Posting Group", LotTemplate."VAT Prod. Posting Group");
  //  IF LotTemplate."Unit of Measure" <> "Unit of Measure" THEN
  //    VALIDATE("Unit of Measure", LotTemplate."Unit of Measure");
  //END;//
    */
  public Lot validateLotTemplateCode(Lot lot, LotTemplate lotTemplateCode) {
    lot.setLotTemplateCode(lotTemplateCode);
    if (lotTemplateCode != null) {

      if (!lotTemplateCode.getLotNatureCode().equals(lot.getLotNatureCode())) {
        lot = validateLotNatureCode(lot, lotTemplateCode.getLotNatureCode());
      }
      if (!lotTemplateCode.getProductFamilyAdj().equals(lot.getAuctionProductFamily())) {
        lot = validateAuctionProductFamily(lot, lotTemplateCode.getProductFamilyAdj());
      }

      if (!lotTemplateCode.getUnitofMeasure().equals(lot.getUnitofMeasure())) {
        lot = validateUnitofMeasure(lot, lotTemplateCode.getUnitofMeasure());
      }
    }
    return lot;
  }

  private Lot validateUnitofMeasure(Lot lot, LotUnitofMeasure unitofMeasure) {
    return null;
  }

  private Lot validateAuctionProductFamily(Lot lot, ProductFamily productFamilyAdj) {
    return null;
  }

  private Lot validateLotNatureCode(Lot lot, LotNature lotNatureCode) {
    return null;
  }

  /*
    * //Origin_Country_Code Code10
  //OnValidateVAR
  //lMissServMgt@1100481000 : Codeunit 8011387;
                                                             BEGIN
  //lMissServMgt.UpdateCustomDues(Rec); //AP20//
    */
  public Lot validateOriginCountryCode(Lot lot, Country originCountryCode) {
    /*lot.setOriginCountryCode(originCountryCode);
    MissionServiceMgt missionServiceMgt = Beans.get(MissionServiceMgt.class);
    lot = missionServiceMgt.UpdateCustomDues(lot);
    */
    // TODO validateOriginCountryCode
    return lot;
  }

  /*
    * //Current_Auction_No Code20
  //OnValidateVAR
  //lVehiDescript@1100481000 : Record 8011438;
                                                             BEGIN
  //IF "Current Auction No." <> '' THEN BEGIN
  //  IF "Lot General Status" < "Lot General Status"::"On Sale" THEN
  //    VALIDATE("Lot General Status", "Lot General Status"::"On Sale"); //ap39 siat.zw
  //END;
  ////<<ap49 isat.zw
  //IF Vehicle THEN BEGIN
  //  IF lVehiDescript.GET("No.") THEN BEGIN
  //    lVehiDescript."Auction No." := "Current Auction No.";
  //    CALCFIELDS("Planned Auction Date");
  //    lVehiDescript."Planned Auction Date" := "Planned Auction Date";
  //    lVehiDescript.MODIFY(FALSE);
  //  END;
  //END;
  ////>>ap49 isat.zw//
    */
  public Lot validateCurrentAuctionNo(Lot lot, AuctionHeader currentAuctionNo) {
    lot.setCurrentAuctionNo(currentAuctionNo);
    if (currentAuctionNo != null) {
      if (lot.getLotGeneralStatus().equals(LotRepository.LOTGENERALSTATUS_IDENTIFIED)
          || lot.getLotGeneralStatus().equals(LotRepository.LOTGENERALSTATUS_ONMISSION)) {
        lot = validateLotGeneralStatus(lot, LotRepository.LOTGENERALSTATUS_ONSALE);
      }
    }
    return lot;
  }

  /*//Current_Mission_No Code20
  //OnValidateVAR
  //lVehicleLotDescription@1100481000 : Record 8011438;
  //lMission@1100481001 : Record 8011402;
                                                             BEGIN
  ////ap36 isat.zw
  //IF Vehicle THEN BEGIN
  //  IF NOT lMission.GET("Current Mission No.") THEN
  //    CLEAR(lMission);
  //  IF lVehicleLotDescription.GET("No.") THEN BEGIN
  //    IF lVehicleLotDescription."Master Contact No." <> lMission."Master Contact No." THEN BEGIN
  //      lVehicleLotDescription.VALIDATE("Master Contact No.", lMission."Master Contact No.");
  //    END;
  //    lVehicleLotDescription."Mission No." := "Current Mission No.";
  //    lVehicleLotDescription.MODIFY(FALSE);
  //  END;
  //END;// */
  public Lot validateCurrentMissionNo(Lot lot, MissionHeader currentMissionNo) {
    lot.setCurrentMissionNo(currentMissionNo);
    return lot;
  }

  /*
  * //Lot_Type Option
  //OnValidateVAR
  //lMissionLine@1000000000 : Record 8011403;
                                                             BEGIN
  //// Tout lot dégroupé devient inutilisable
  //IF "Lot Type" = "Lot Type"::"Ungrouping Origin Lot" THEN BEGIN
  //  VALIDATE("Auction Status", "Auction Status"::Ungrouped);
  //  //VALIDATE("Lot Inventory Status", "Lot Inventory Status"::Ungroup); désactivé isat.zw 21/04/10
  //END;


  //IF "Current Mission No." <> '' THEN BEGIN
  //  lMissionLine.SETCURRENTKEY("Mission No.",Type,"No.");
  //  lMissionLine.SETRANGE("Mission No.","Current Mission No.");
  //  lMissionLine.SETRANGE(Type,lMissionLine.Type::Lot);
  //  lMissionLine.SETRANGE("No.","No.");
  //  IF lMissionLine.FINDFIRST THEN BEGIN
  //    lMissionLine."Lot Type" := "Lot Type";
  //    lMissionLine.MODIFY;
  //  END;
  //END;//
  */
  public Lot validateLotType(Lot lot, Integer lotType) {
    lot.setLotType(lotType);
    if (lotType != null) {
      if (lotType.equals(LotRepository.LOTTYPE_UNGROUPINGORIGINLOT)) {
        lot = validateAuctionStatus(lot, LotRepository.AUCTIONSTATUS_UNGROUPED);
      }
    }
    if (lot.getCurrentMissionNo() != null) {
      MissionLine missionLine =
          Beans.get(MissionLineRepository.class)
              .all()
              .filter(
                  "self.missionHeader = ?1 AND self.typeSelect = ?2 AND self.lot = ?3",
                  lot.getCurrentMissionNo(),
                  MissionLineRepository.TYPE_LOT,
                  lot)
              .fetchOne();
      if (missionLine != null) {
        missionLine.setLotType(lotType);
        Beans.get(MissionLineRepository.class).save(missionLine);
      }
    }
    return lot;
  }

  /*
  *
   OnInsert=VAR
              lLotSetup@1180113000 : Record 8011418;
            BEGIN
              GetSetup(FALSE);
              IF "No." = '' THEN BEGIN
                LotSetup.TESTFIELD("Lot Nos.");
                NoSeriesMgt.InitSeries(LotSetup."Lot Nos.",xRec."No. Series",0D,"No.","No. Series");
              END;

              TESTFIELD("Lot Template Code");
              TESTFIELD("Lot Nature Code");

              InitFields;
              TouchRecord(TRUE);

              DimMgt.UpdateDefaultDim(
                DATABASE::Lot,"No.",
                "Global Dimension 1 Code",
                "Global Dimension 2 Code");

              //<<AP13.ISAT.SC
              //LotSetup."Last Lot Created" := "No.";
              IF Vehicle THEN BEGIN
                lLotSetup.FINDSET(TRUE);                                   //ap59 isat.zw
                lLotSetup."Last Vehicle Created" := "No.";       //ap59 isat.zw
                lLotSetup.MODIFY;
              END;
              //>>AP13.ISAT.SC

              AnalyseMgt.SetSynchroRecord(Rec."No.",8011404,0); // AP63 isat.sf

              IF "Web Auctionable" THEN BEGIN
                WebSynchroMgt.WriteSynchro("No.",8011404,gOption::Insertion,'');
              END;
            END;
  */
  public Lot onInsert(Lot lot) throws AxelorException {
    getSetup(false);
    if (lotSetup.getLotNos() == null) {
      throw new AxelorException(
          lotSetup,
          TraceBackRepository.CATEGORY_CONFIGURATION_ERROR,
          "La séquence des numéro de lots n'est pas configurée");
    }
    if (lot.getNo() == null) {
      lot.setNo(Beans.get(SequenceService.class).getSequenceNumber(lotSetup.getLotNos()));
    }
    if (lot.getLotTemplateCode() == null) {
      throw new AxelorException(
          TraceBackRepository.CATEGORY_MISSING_FIELD, "Le champ modèle de lot est obligatoire");
    }
    if (lot.getLotNatureCode() == null) {
      throw new AxelorException(
          TraceBackRepository.CATEGORY_MISSING_FIELD, "Le champ nature de lot est obligatoire");
    }

    lot = initFields(lot);

    if (lot.getVehicle()) {
      LotSetup lotSetup = Beans.get(LotSetupRepository.class).all().fetchOne();
      lotSetup.setLastVehicleCreated(lot.getNo());
      Beans.get(LotSetupRepository.class).save(lotSetup);
    }
    Beans.get(TransportAnalysisUpdate.class).setSynchroRecord(lot, 8011404, 0);
    if (lot.getWebAuctionable()) {
      Beans.get(WebSynchroManagement.class).writeSynchro(lot.getNo(), 8011404, "Insert", "");
    }
    return lot;
  }

  /*
  * PROCEDURE InitFields@1000000004();
   VAR
     lLotNature@1000000000 : Record 8011308;
   BEGIN
     "Lot Type" := "Lot Type"::Lot;
     Quantity := 1;
     "Outstanding Quantity" := 1;
     IF lLotNature.GET("Lot Nature Code") THEN BEGIN
       "Search Method" := lLotNature."Search Method";
       "Value Search" := lLotNature."Value Search";
     END;
   END;
  */
  private Lot initFields(Lot lot) {

    lot.setLotType(LotRepository.LOTTYPE_LOT);
    lot.setQuantity(1);
    lot.setOutstandingQuantity(BigDecimal.ONE);
    LotNature lotNature = lot.getLotNatureCode();
    if (lotNature != null) {
      lot.setSearchMethod(lotNature.getSearchMethod());
      lot.setValueSearch(lotNature.getValueSearch());
    }

    return lot;
  }

  private void getSetup(boolean b) {
    lotSetup = Beans.get(LotSetupRepository.class).all().fetchOne();
  }
}
