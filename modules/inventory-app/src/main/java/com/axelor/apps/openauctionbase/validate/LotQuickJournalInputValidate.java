package com.axelor.apps.openauctionbase.validate;

import com.axelor.apps.base.db.Partner;
import com.axelor.apps.openauction.db.AuctionHeader;
import com.axelor.apps.openauction.db.ClassificationMembers;
import com.axelor.apps.openauction.db.Lot;
import com.axelor.apps.openauction.db.LotQuickInputJournal;
import com.axelor.apps.openauction.db.LotTemplate;
import com.axelor.apps.openauction.db.MissionHeader;
import com.axelor.apps.openauction.db.repo.LotQuickInputJournalRepository;
import com.axelor.apps.openauctionbase.service.AuctionLotValueManagement;
import com.axelor.apps.openauctionbase.service.ClassificationManagement;
import com.axelor.exception.AxelorException;
import com.axelor.exception.db.repo.TraceBackRepository;
import com.axelor.inject.Beans;
import java.math.BigDecimal;

public class LotQuickJournalInputValidate {

  AuctionLotValueManagement auctionLotValueManagement = Beans.get(AuctionLotValueManagement.class);
  ClassificationManagement classificationManagement = Beans.get(ClassificationManagement.class);

  /*
  * { 1   ;   ;Mission No.         ;Code20        ;TableRelation="Mission Header";
     OnValidate=BEGIN
                 IF MissionHeader.GET("Mission No.") THEN BEGIN
                     "Seller Contact No." := MissionHeader."Master Contact No."; //AP10.ST
                 END;
                 END;
  */
  public LotQuickInputJournal validateMissionNo(
      LotQuickInputJournal lotQuickInputJournal, MissionHeader missionNo) {
    lotQuickInputJournal.setMissionNo(missionNo);
    if (missionNo != null) {
      lotQuickInputJournal.setSellerContactNo(missionNo.getMasterContactNo());
    }

    return lotQuickInputJournal;
  }

  /*
  * { 3   ;   ;Auction No.         ;Code20        ;TableRelation="Auction Header".No.;
                                                OnValidate=BEGIN
                                                             //<<AP12.ISAT.EBA
                                                             IF AuctionHeader.GET("Auction No.") THEN
                                                               "Auction Date" := AuctionHeader."Auction Date"
                                                             ELSE
                                                               "Auction Date" := 0D;
                                                             //>>AP12.ISAT.EBA
                                                           END;

  */
  public LotQuickInputJournal validateAuctionNo(
      LotQuickInputJournal lotQuickInputJournal, AuctionHeader auctionNo) {
    lotQuickInputJournal.setAuctionNo(auctionNo);
    if (auctionNo != null) {
      lotQuickInputJournal.setAuctionDate(auctionNo.getAuctionDate());
    } else {
      lotQuickInputJournal.setAuctionDate(null);
    }
    return lotQuickInputJournal;
  }

  /*
  * { 25  ;   ;Lot No.             ;Code20        ;TableRelation=Lot.No.;
                                                 OnValidate=VAR
                                                              lLot@1000000000 : Record 8011404;
                                                              lLotVeh@1180113000 : Record 8011438;
                                                            BEGIN
                                                              IF lLot.GET("Lot No.") THEN BEGIN
                                                                "Auction Prod. Posting Group" := lLot."Auction Prod. Posting Group";
                                                                "VAT Prod. Posting Group" := lLot."VAT Prod. Posting Group";
                                                                "Lot Categorie 1 Code" := lLot."Lot Categorie 1 Code";
                                                                "Lot Categorie 2 Code" := lLot."Lot Categorie 2 Code";
                                                                "Lot Categorie 3 Code" := lLot."Lot Categorie 3 Code";
                                                                "Lot Categorie 4 Code" := lLot."Lot Categorie 4 Code";
                                                                CASE "Value Type" OF
                                                                  "Value Type"::Estimate :
                                                                    VALIDATE("Expert Contact No.",'');
                                                                  "Value Type"::Appraisal :
                                                                    VALIDATE("Expert Contact No.", lLot."Default Expert Contact No.");
                                                                END; // CASE
                                                              //<<AP09.ST
                                                                "External No." := lLot."External No.";
                                                                IF lLot.Vehicle THEN BEGIN
                                                                  IF lLotVeh.GET(lLot."No.") THEN BEGIN
                                                                    Registration := lLotVeh.Registration;
                                                                    Make := lLotVeh.Make;
                                                                  END;
                                                                END;
                                                              //>>AP09.ST
                                                              END;
                                                            END;
  */
  public LotQuickInputJournal validateLotNo(LotQuickInputJournal lotQuickInputJournal, Lot lotNo) {
    lotQuickInputJournal.setLotNo(lotNo);
    if (lotNo != null) {
      lotQuickInputJournal.setAuctionProductFamily(lotNo.getAuctionProductFamily());
      lotQuickInputJournal.setLotCategorie1Code(lotNo.getLotCategorie1Code1());
      lotQuickInputJournal.setLotCategorie2Code(lotNo.getLotCategorie2Code1());
      lotQuickInputJournal.setLotCategorie3Code(lotNo.getLotCategorie3Code1());
      lotQuickInputJournal.setLotCategorie4Code(lotNo.getLotCategorie4Code1());
      if (lotQuickInputJournal
          .getValueType()
          .equals(LotQuickInputJournalRepository.VALUETYPE_ESTIMATE)) {
        lotQuickInputJournal.setExpertContactNo(null);
      } else if (lotQuickInputJournal
          .getValueType()
          .equals(LotQuickInputJournalRepository.VALUETYPE_APPRAISAL)) {
        lotQuickInputJournal.setExpertContactNo(lotNo.getDefaultExpertContactNo());
      }
      lotQuickInputJournal.setExternalNo(lotNo.getExternalNo());
      if (lotNo.getVehicle()) {
        if (lotNo != null) {
          lotQuickInputJournal.setRegistration(lotNo.getRegistration());
          lotQuickInputJournal.setMake(lotNo.getMake());
        }
      }
    }
    return lotQuickInputJournal;
  }

  /*
  * { 26  ;   ;Lot Template Code   ;Code10        ;TableRelation="Lot Template".Code;
                                                OnValidate=BEGIN
                                                             GetLotTemplate;
                                                             "Auction Prod. Posting Group" := LotTemplate."Auction Prod. Posting Group";
                                                             "VAT Prod. Posting Group" := LotTemplate."VAT Prod. Posting Group";
                                                             "Lot Categorie 1 Code" := LotTemplate."Lot Categorie 1 Code";
                                                             "Lot Categorie 2 Code" := LotTemplate."Lot Categorie 2 Code";
                                                             "Lot Categorie 3 Code" := LotTemplate."Lot Categorie 3 Code";
                                                             "Lot Categorie 4 Code" := LotTemplate."Lot Categorie 4 Code";

                                                             GetDefaultExpert; //AP02.ISAT.ST
  */
  public LotQuickInputJournal validateLotTemplateCode(
      LotQuickInputJournal lotQuickInputJournal, LotTemplate lotTemplateCode) {
    lotQuickInputJournal.setLotTemplateCode(lotTemplateCode);
    if (lotTemplateCode != null) {
      lotQuickInputJournal.setAuctionProductFamily(lotTemplateCode.getProductFamilyAdj());
      lotQuickInputJournal.setLotCategorie1Code(lotTemplateCode.getLotCategorie1Code());
      lotQuickInputJournal.setLotCategorie2Code(lotTemplateCode.getLotCategorie2Code());
      lotQuickInputJournal.setLotCategorie3Code(lotTemplateCode.getLotCategorie3Code());
      lotQuickInputJournal.setLotCategorie4Code(lotTemplateCode.getLotCategorie4Code());
    }
    this.GetDefaultExpert(lotQuickInputJournal, lotTemplateCode);
    return lotQuickInputJournal;
  }
  /*
   * PROCEDURE GetDefaultExpert@1100281000();
  BEGIN
    //AP02.ISAT.ST
    CASE "Value Type" OF
      "Value Type"::Estimate :
        VALIDATE("Expert Contact No.",'');
      "Value Type"::Appraisal :
        BEGIN
          GetLotTemplate;
          VALIDATE("Expert Contact No.", LotTemplate."Default Expert");
        END;
    END; // CASE
  END;
   */
  public Partner GetDefaultExpert(
      LotQuickInputJournal lotQuickInputJournal, LotTemplate lotTemplateCode) {
    Partner partner = null;
    if (lotQuickInputJournal
        .getValueType()
        .equals(LotQuickInputJournalRepository.VALUETYPE_ESTIMATE)) {
      lotQuickInputJournal.setExpertContactNo(null);
    } else if (lotQuickInputJournal
        .getValueType()
        .equals(LotQuickInputJournalRepository.VALUETYPE_APPRAISAL)) {
      if (lotTemplateCode != null) {
        lotQuickInputJournal.setExpertContactNo(lotTemplateCode.getDefaultExpert());
      }
    }
    return partner;
  }

  // Appraisal_Value Decimal
  // OnValidateBEGIN
  // IF "Line Type" <> "Line Type"::" " THEN BEGIN
  //  "Appraisal Value" := 0;
  // END;//
  public LotQuickInputJournal validateAppraisalValue(
      LotQuickInputJournal lotQuickInputJournal, BigDecimal appraisalValue) {
    lotQuickInputJournal.setAppraisalValue(appraisalValue);
    if (!lotQuickInputJournal.getLineType().equals(LotQuickInputJournalRepository.LINETYPE_EMPTY)) {
      lotQuickInputJournal.setAppraisalValue(BigDecimal.ZERO);
    }
    return lotQuickInputJournal;
  }

  // Min_Appraisal_Value Decimal
  // OnValidateBEGIN
  // IF "Line Type" <> "Line Type"::" " THEN BEGIN
  //  "Min. Appraisal Value" := 0;
  // END;//
  public LotQuickInputJournal validateMinAppraisalValue(
      LotQuickInputJournal lotQuickInputJournal, BigDecimal minAppraisalValue) {
    lotQuickInputJournal.setMinAppraisalValue(minAppraisalValue);
    if (!lotQuickInputJournal.getLineType().equals(LotQuickInputJournalRepository.LINETYPE_EMPTY)) {
      lotQuickInputJournal.setMinAppraisalValue(BigDecimal.ZERO);
    }
    return lotQuickInputJournal;
  }
  // Max_Appraisal_Value Decimal
  // OnValidateBEGIN
  // IF "Line Type" <> "Line Type"::" " THEN BEGIN
  //  "Max. Appraisal Value" := 0;
  // END;//
  public LotQuickInputJournal validateMaxAppraisalValue(
      LotQuickInputJournal lotQuickInputJournal, BigDecimal maxAppraisalValue) {
    lotQuickInputJournal.setMaxAppraisalValue(maxAppraisalValue);
    if (!lotQuickInputJournal.getLineType().equals(LotQuickInputJournalRepository.LINETYPE_EMPTY)) {
      lotQuickInputJournal.setMaxAppraisalValue(BigDecimal.ZERO);
    }
    return lotQuickInputJournal;
  }

  // Gross_Reserve_Price Decimal
  // OnValidateBEGIN
  // IF "Line Type" <> "Line Type"::" " THEN BEGIN
  //  "Gross Reserve Price" := 0;
  // END;
  // AuctionLotValueMgt.CalcAuctionEstimByLotQuickJnl(Rec);//
  public LotQuickInputJournal validateGrossReservePrice(
      LotQuickInputJournal lotQuickInputJournal, BigDecimal grossReservePrice) {
    lotQuickInputJournal.setGrossReservePrice(grossReservePrice);
    if (!lotQuickInputJournal.getLineType().equals(LotQuickInputJournalRepository.LINETYPE_EMPTY)) {
      lotQuickInputJournal.setGrossReservePrice(BigDecimal.ZERO);
    }
    auctionLotValueManagement.calcAuctionEstimByLotQuickJnl(lotQuickInputJournal);
    return lotQuickInputJournal;
  }
  // Net_Reserve_Price Decimal
  // OnValidateBEGIN
  // IF "Line Type" <> "Line Type"::" " THEN BEGIN
  //  "Net Reserve Price" := 0;
  // END;
  // AuctionLotValueMgt.CalcAuctionEstimByLotQuickJnl(Rec);//

  public LotQuickInputJournal validateNetReservePrice(
      LotQuickInputJournal lotQuickInputJournal, BigDecimal netReservePrice) {
    lotQuickInputJournal.setNetReservePrice(netReservePrice);
    if (!lotQuickInputJournal.getLineType().equals(LotQuickInputJournalRepository.LINETYPE_EMPTY)) {
      lotQuickInputJournal.setNetReservePrice(BigDecimal.ZERO);
    }
    auctionLotValueManagement.calcAuctionEstimByLotQuickJnl(lotQuickInputJournal);
    return lotQuickInputJournal;
  }
  // Estimate_Value Decimal
  // OnValidateBEGIN
  // IF "Line Type" <> "Line Type"::" " THEN BEGIN
  //  "Estimate Value" := 0;
  // END;//
  public LotQuickInputJournal validateEstimateValue(
      LotQuickInputJournal lotQuickInputJournal, BigDecimal estimateValue) {
    lotQuickInputJournal.setEstimateValue(estimateValue);
    if (!lotQuickInputJournal.getLineType().equals(LotQuickInputJournalRepository.LINETYPE_EMPTY)) {
      lotQuickInputJournal.setEstimateValue(BigDecimal.ZERO);
    }
    return lotQuickInputJournal;
  }

  // Min_Estimate_Value Decimal
  // OnValidateBEGIN
  // IF "Line Type" <> "Line Type"::" " THEN BEGIN
  //  "Min. Estimate Value" := 0;
  // END;
  // AuctionLotValueMgt.CalcAuctionEstimByLotQuickJnl(Rec);//
  public LotQuickInputJournal validateMinEstimateValue(
      LotQuickInputJournal lotQuickInputJournal, BigDecimal minEstimateValue) {
    lotQuickInputJournal.setMinEstimateValue(minEstimateValue);
    if (!lotQuickInputJournal.getLineType().equals(LotQuickInputJournalRepository.LINETYPE_EMPTY)) {
      lotQuickInputJournal.setMinEstimateValue(BigDecimal.ZERO);
    }
    auctionLotValueManagement.calcAuctionEstimByLotQuickJnl(lotQuickInputJournal);
    return lotQuickInputJournal;
  }

  // Max_Estimate_Value Decimal
  // OnValidateBEGIN
  // IF "Line Type" <> "Line Type"::" " THEN BEGIN
  //  "Max. Estimate Value" := 0;
  // END;
  // AuctionLotValueMgt.CalcAuctionEstimByLotQuickJnl(Rec);//
  public LotQuickInputJournal validateMaxEstimateValue(
      LotQuickInputJournal lotQuickInputJournal, BigDecimal maxEstimateValue) {
    lotQuickInputJournal.setMaxEstimateValue(maxEstimateValue);
    if (!lotQuickInputJournal.getLineType().equals(LotQuickInputJournalRepository.LINETYPE_EMPTY)) {
      lotQuickInputJournal.setMaxEstimateValue(BigDecimal.ZERO);
    }
    auctionLotValueManagement.calcAuctionEstimByLotQuickJnl(lotQuickInputJournal);
    return lotQuickInputJournal;
  }

  // Lot_Categorie_1_Code Code10
  // OnValidateBEGIN
  // IF ("Lot Categorie 1 Code" <> xRec."Lot Categorie 1 Code") AND
  //   ("Lot Categorie 1 Code" <> '')
  // THEN BEGIN
  //  ValidateCategorieCode(1,"Lot Categorie 1 Code");
  // END;//

  public LotQuickInputJournal validateLotCategorie1Code(
      LotQuickInputJournal lotQuickInputJournal, ClassificationMembers lotCategorie1Code) {
    lotQuickInputJournal.setLotCategorie1Code(lotCategorie1Code);
    if (!lotQuickInputJournal
            .getLotCategorie1Code()
            .equals(lotQuickInputJournal.getLotCategorie1Code())
        && !lotQuickInputJournal.getLotCategorie1Code().equals(null)) {
      validateCategorieCode(lotQuickInputJournal, 1, lotQuickInputJournal.getLotCategorie1Code());
    }
    return lotQuickInputJournal;
  }

  // Lot_Categorie_2_Code Code10
  // OnValidateBEGIN
  // IF ("Lot Categorie 2 Code" <> xRec."Lot Categorie 2 Code") AND
  //   ("Lot Categorie 2 Code" <> '')
  // THEN BEGIN
  //  ValidateCategorieCode(2,"Lot Categorie 2 Code");
  // END;//
  public LotQuickInputJournal validateLotCategorie2Code(
      LotQuickInputJournal lotQuickInputJournal, ClassificationMembers lotCategorie2Code) {
    lotQuickInputJournal.setLotCategorie2Code(lotQuickInputJournal.getLotCategorie2Code());
    if (!lotQuickInputJournal
            .getLotCategorie2Code()
            .equals(lotQuickInputJournal.getLotCategorie2Code())
        && !lotQuickInputJournal.getLotCategorie2Code().equals(null)) {
      validateCategorieCode(lotQuickInputJournal, 2, lotQuickInputJournal.getLotCategorie2Code());
    }
    return lotQuickInputJournal;
  }

  // Lot_Categorie_3_Code Code10
  // OnValidateBEGIN
  // IF ("Lot Categorie 3 Code" <> xRec."Lot Categorie 3 Code") AND
  //   ("Lot Categorie 3 Code" <> '')
  // THEN BEGIN
  //  ValidateCategorieCode(3,"Lot Categorie 3 Code");
  // END;//
  public LotQuickInputJournal validateLotCategorie3Code(
      LotQuickInputJournal lotQuickInputJournal, ClassificationMembers lotCategorie3Code) {
    lotQuickInputJournal.setLotCategorie3Code(lotQuickInputJournal.getLotCategorie3Code());
    if (!lotQuickInputJournal
            .getLotCategorie3Code()
            .equals(lotQuickInputJournal.getLotCategorie3Code())
        && !lotQuickInputJournal.getLotCategorie3Code().equals(null)) {
      validateCategorieCode(lotQuickInputJournal, 3, lotQuickInputJournal.getLotCategorie3Code());
    }
    return lotQuickInputJournal;
  }

  // Lot_Categorie_4_Code Code10
  // OnValidateBEGIN
  // IF ("Lot Categorie 4 Code" <> xRec."Lot Categorie 4 Code") AND
  //   ("Lot Categorie 4 Code" <> '')
  // THEN BEGIN
  //  ValidateCategorieCode(4,"Lot Categorie 4 Code");
  // END;//
  public LotQuickInputJournal validateLotCategorie4Code(
      LotQuickInputJournal lotQuickInputJournal, ClassificationMembers lotCategorie4Code) {
    lotQuickInputJournal.setLotCategorie4Code(lotQuickInputJournal.getLotCategorie4Code());
    if (!lotQuickInputJournal
            .getLotCategorie4Code()
            .equals(lotQuickInputJournal.getLotCategorie4Code())
        && !lotQuickInputJournal.getLotCategorie4Code().equals(null)) {
      validateCategorieCode(lotQuickInputJournal, 4, lotQuickInputJournal.getLotCategorie4Code());
    }
    return lotQuickInputJournal;
  }

  // Value_Type Option
  // OnValidateBEGIN
  // GetDefaultExpert;//AP02.ISAT.ST//

  // Expert_Contact_No Code20
  // OnValidateBEGIN
  // IF "Expert Contact No." <> '' THEN
  //  TESTFIELD("Value Type","Value Type"::Appraisal);//
  public LotQuickInputJournal validateExpertContactNo(
      LotQuickInputJournal lotQuickInputJournal, Partner expertContactNo) throws AxelorException {
    lotQuickInputJournal.setExpertContactNo(expertContactNo);
    if (!lotQuickInputJournal.getExpertContactNo().equals(null)) {
      if (!lotQuickInputJournal
          .getValueType()
          .equals(LotQuickInputJournalRepository.VALUETYPE_APPRAISAL)) {
        throw new AxelorException(
            TraceBackRepository.CATEGORY_INCONSISTENCY,
            "Value type must be appraisal",
            lotQuickInputJournal.getValueType());
      }
    }
    return lotQuickInputJournal;
  }

  /*
     * LOCAL PROCEDURE ValidateCategorieCode@1000000002(CategorieNo@1000000000 : Integer;CategorieCode@1000000001 : Code[10]);
  VAR
    lLotTemplate@1000000003 : Record 8011411;
    lClassMgt@1000000002 : Codeunit 8011371;
  BEGIN
    TESTFIELD("Lot Template Code");
    IF NOT lLotTemplate.GET("Lot Template Code") THEN BEGIN
      lLotTemplate.INIT;
    END;
    lClassMgt.ValidateLotClassCode(lLotTemplate."Sector Code",CategorieNo,CategorieCode);
  END;
     */
  private void validateCategorieCode(
      LotQuickInputJournal lotQuickInputJournal,
      Integer categorieNo,
      ClassificationMembers categorieCode) {
    // TODO
    LotTemplate lLotTemplate = lotQuickInputJournal.getLotTemplateCode();
    if (lLotTemplate == null) {
      lLotTemplate = new LotTemplate();
    }

    classificationManagement.validateLotClassCode(
        lLotTemplate.getSectorCode(), categorieNo, categorieCode);
  }
}
