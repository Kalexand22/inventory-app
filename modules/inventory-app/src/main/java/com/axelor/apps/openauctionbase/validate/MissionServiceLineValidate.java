package com.axelor.apps.openauctionbase.validate;

import com.axelor.apps.account.db.FiscalPosition;
import com.axelor.apps.base.db.Partner;
import com.axelor.apps.base.db.Product;
import com.axelor.apps.base.db.Unit;
import com.axelor.apps.base.db.repo.ProductRepository;
import com.axelor.apps.base.service.app.AppBaseService;
import com.axelor.apps.openauction.db.AuctionHeader;
import com.axelor.apps.openauction.db.AuctionLine;
import com.axelor.apps.openauction.db.AuctionLotPriceGroup;
import com.axelor.apps.openauction.db.AuctionSetup;
import com.axelor.apps.openauction.db.Lot;
import com.axelor.apps.openauction.db.MissionContactPriceGroup;
import com.axelor.apps.openauction.db.MissionHeader;
import com.axelor.apps.openauction.db.MissionLotPriceGroup;
import com.axelor.apps.openauction.db.MissionServiceLine;
import com.axelor.apps.openauction.db.repo.AuctionLineRepository;
import com.axelor.apps.openauction.db.repo.AuctionSetupRepository;
import com.axelor.apps.openauction.db.repo.MissionServiceLineRepository;
import com.axelor.apps.openauctionbase.service.AuctionServicePriceMgt;
import com.axelor.apps.openauctionbase.service.CaretakerServicePriceMgt;
import com.axelor.apps.openauctionbase.service.MissionServicePriceManagement;
import com.axelor.auth.AuthUtils;
import com.axelor.exception.AxelorException;
import com.axelor.exception.db.repo.TraceBackRepository;
import com.axelor.inject.Beans;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class MissionServiceLineValidate {

  private Boolean skipChangeCheck = false;
  AuctionLine auctionLine = null;
  /*
  // Mission_No Code20
  // OnValidateBEGIN
  // GetMission;
  // "Mission Template Code" := MissionHeader."Mission Template Code";
  // MissionHeader.TESTFIELD("VAT Business Posting Group");
  //// <<AP35.ST
  //
  // IF "Lot No." = '' THEN //AP21.ST
  //  VALIDATE("Seller VAT Bus. Posting Group", MissionHeader."VAT Business Posting Group");
  //// "Margin Based VAT" := MissionHeader."Margin Based VAT"; AP21.ST désactivé
  //
  // GetMissVATBus;
  //// >>AP35.ST
  // IF "No." <> '' THEN
  //  VALIDATE("Contact Imputation Type");

  // IF MissionHeader."Responsibility Center" <> '' THEN
  //  "Responsibility Center" := MissionHeader."Responsibility Center"; // AP11 isat.sf

  //// ChangeDocumentNo; //ap46 isat.zw//*/
  public MissionServiceLine validateMissionNo(
      MissionServiceLine missionServiceLine, MissionHeader missionNo) throws AxelorException {
    missionServiceLine.setMissionNo(missionNo);
    missionServiceLine.setMissionTemplateCode(missionNo.getMissionTemplateCode());
    /*
    if (missionNo.getFiscalPosition() == null) {
      throw new AxelorException(
          TraceBackRepository.CATEGORY_INCONSISTENCY, "La position fiscale est obligatoire");
    }*/
    missionServiceLine = this.GetMissVATBus(missionServiceLine);
    if (missionServiceLine.getProductNo() != null) {
      missionServiceLine =
          this.validateContactImputationType(
              missionServiceLine, missionServiceLine.getContactImputationType());
    }

    if (missionNo.getResponsibilityCenter() != null) {
      missionServiceLine.setResponsibilityCenter(missionNo.getResponsibilityCenter());
    }
    return missionServiceLine;
  }

  private MissionServiceLine GetMissVATBus(MissionServiceLine missionServiceLine)
      throws AxelorException {
    FiscalPosition fiscalPosition = null;
    if (missionServiceLine.getLotNo() != null) {
      fiscalPosition = missionServiceLine.getLotNo().getFiscalposition();
    }
    if (fiscalPosition == null && missionServiceLine.getMissionNo() != null) {
      fiscalPosition = missionServiceLine.getMissionNo().getFiscalPosition();
    }
    if (fiscalPosition == null
        && missionServiceLine.getContactImputationType()
            == MissionServiceLineRepository.CONTACTIMPUTATIONTYPE_SELLER
        && missionServiceLine.getChargeableContactNo() != null) {
      fiscalPosition = missionServiceLine.getChargeableContactNo().getFiscalPosition();
    }
    if (fiscalPosition != missionServiceLine.getSellerFiscalPosition()) {
      missionServiceLine = this.validateSellerFiscalePosition(missionServiceLine, fiscalPosition);
    }

    return missionServiceLine;
  }

  /*
  // Auction_No Code20
  // OnValidateVAR
  // lAuctionHeader@1000000000 : Record 8011400;
  // BEGIN
  // IF "Auction No." <> '' THEN BEGIN
  //  lAuctionHeader.GET("Auction No.");

  //  "Auction Template Code" := lAuctionHeader."Auction Template Code";
  //  "Auction Incl. VAT" := lAuctionHeader."Prices Including VAT";
  //// <<AP24.ST
  //  IF ("Transaction Type" = "Transaction Type"::Auction) AND ("Price Date" <>
  // lAuctionHeader."Auction Date") THEN
  //    VALIDATE("Price Date", lAuctionHeader."Auction Date");
  //// >>AP24.ST
  //// << AP19 isat.SF
  // END ELSE BEGIN
  //  "Auction Template Code" := '';
  //  "Auction Incl. VAT" := FALSE;
  //// <<AP24.ST
  //  IF ("Transaction Type" = "Transaction Type"::Auction) THEN
  //    "Price Date" := WORKDATE;
  //// >>AP24.ST
  // END;
  //// >> AP19 isat.SF//*/
  public MissionServiceLine validateAuctionNo(
      MissionServiceLine missionServiceLine, AuctionHeader auctionNo) throws AxelorException {

    missionServiceLine.setAuctionNo(auctionNo);
    if (auctionNo == null) {
      return missionServiceLine;
    }
    missionServiceLine.setAuctionTemplateCode(auctionNo.getAuctionTemplate());
    missionServiceLine.setAuctionInclVAT(auctionNo.getPricesIncludingVAT());
    if (missionServiceLine
            .getTransactionType()
            .equals(MissionServiceLineRepository.TRANSACTIONTYPE_VENTE)
        && missionServiceLine.getPriceDate() != auctionNo.getAuctionDate()) {
      missionServiceLine = this.validatePriceDate(missionServiceLine, auctionNo.getAuctionDate());
    } else {
      missionServiceLine.setAuctionTemplateCode(null);
      missionServiceLine.setAuctionInclVAT(false);
      if (missionServiceLine
          .getTransactionType()
          .equals(MissionServiceLineRepository.TRANSACTIONTYPE_VENTE)) {

        missionServiceLine.setPriceDate(
            Beans.get(AppBaseService.class).getTodayDate(AuthUtils.getUser().getActiveCompany()));
      }
    }
    return missionServiceLine;
  }

  /*
  // Lot_No Code20
  // OnValidateBEGIN
  // GetLot;
  // "Lot Template Code" := Lot."Lot Template Code";
  // "Responsibility Center" := Lot."Responsibility Center";
  // GetMissVATBus;//AP21.ST

  // CASE "Transaction Type" OF
  //  "Transaction Type"::Mission : BEGIN
  //     "Lot Price Group" := Lot."Lot Mission Price Group";
  //     "Transaction Line No." := Lot."Current Mission Line No.";
  //  END;
  //  "Transaction Type"::Auction : BEGIN
  //     "Lot Price Group" := Lot."Lot Auction Price Group";
  //     //"Transaction Line No." := Lot."Current Auction Line No."; // isat.sf géré au niveau de
  // l'auction line
  //   END;
  // END;
  //// <<AP03.ISAT.ST
  // IF "No." <> '' THEN
  //  VALIDATE("No.");
  //// >>AP03.ISAT.ST//*/
  public MissionServiceLine validateLotNo(MissionServiceLine missionServiceLine, Lot lot)
      throws AxelorException {
    missionServiceLine.setLotNo(lot);
    if (lot == null) {
      return missionServiceLine;
    }
    missionServiceLine.setLotTemplateCode(lot.getLotTemplateCode());
    missionServiceLine.setResponsibilityCenter(lot.getResponsibilityCenter());
    missionServiceLine = this.GetMissVATBus(missionServiceLine);
    if (missionServiceLine
        .getTransactionType()
        .equals(MissionServiceLineRepository.TRANSACTIONTYPE_MISSION)) {
      missionServiceLine.setLotPriceGroup(lot.getLotMissionPriceGroup());
      missionServiceLine.setTransactionLineNo(lot.getCurrentMissionLineNo().getLineNo());
    } else if (missionServiceLine
        .getTransactionType()
        .equals(MissionServiceLineRepository.TRANSACTIONTYPE_VENTE)) {
      missionServiceLine.setAuctionLotPriceGroup(lot.getLotAuctionPriceGroup());
    }
    if (missionServiceLine.getProductNo() != null) {
      missionServiceLine =
          this.validateProductNo(missionServiceLine, missionServiceLine.getProductNo());
    }
    return missionServiceLine;
  }

  /*
  //// >>AP03.ISAT.ST
  //    //GetVat2014; // AP50 isat.sf
  //    //"Commission Type" := Item."Commission Type";
  //    VALIDATE("Contact Imputation Type",Item."Contact Imputation Type");
  //    //"Lot Imputation Type" := Item."Lot Imputation Type";
  //    "Service Type" := Item."Service Type";
  //    "Imputation Base" := Item."Imputation Base";
  //    Appreciation := Item."Value Added Item";
  //    VALIDATE("Unit of Measure Code",Item."Sales Unit of Measure"); // Recalcul le prix (via
  // quantité)
  //    "Gen. Prod. Posting Group" := Item."Gen. Prod. Posting Group";
  //    VALIDATE("Unit Cost", Item."Unit Cost");
  //  END;
  //  Type::Resource :  BEGIN
  //    GetResource;
  //    Description := Resource.Name;
  //    VALIDATE("Unit of Measure Code",Resource."Base Unit of Measure");
  //  END;
  // END;
  // VALIDATE("VAT Prod. Posting Group");//*/
  public MissionServiceLine validateProductNo(
      MissionServiceLine missionServiceLine, Product productNo) throws AxelorException {

    missionServiceLine.setProductNo(productNo);
    if (productNo == null) {
      return missionServiceLine;
    }
    missionServiceLine.setDescription(productNo.getDescription());
    if (missionServiceLine.getAuctionBid()) {
      missionServiceLine.setInvoicingType(MissionServiceLineRepository.INVOICINGTYPE_BILLABLEONBID);
      missionServiceLine.setChargeable(true);
      missionServiceLine.setQuantity(BigDecimal.ONE);
    } else {
      missionServiceLine.setInvoicingType(productNo.getInvoicingType());
    }

    if (productNo.getUseProductFamilyLot() && missionServiceLine.getLotNo() != null) {
      missionServiceLine.setProductFamily(missionServiceLine.getLotNo().getAuctionProductFamily());
    } else {
      missionServiceLine.setProductFamily(productNo.getProductFamily());
    }

    missionServiceLine.setCommissionType(productNo.getCommissionType());

    missionServiceLine =
        this.validateContactImputationType(
            missionServiceLine, productNo.getContactImputationType());

    missionServiceLine.setServiceType(productNo.getServiceType());
    missionServiceLine.setImputationBase(productNo.getImputationBase());

    missionServiceLine.setAppreciation(productNo.getValueAddedItem());
    missionServiceLine =
        this.validateUnitOfMeasureCode(missionServiceLine, productNo.getSalesUnit());
    missionServiceLine = this.validateUnitCost(missionServiceLine, productNo.getCostPrice());

    return missionServiceLine;
  }

  private MissionServiceLine validateSellerFiscalePosition(
      MissionServiceLine missionServiceLine, FiscalPosition fiscalPosition) {
    return missionServiceLine;
  }

  private MissionServiceLine CalcAmounts(MissionServiceLine missionServiceLine) {
    return missionServiceLine;
  }

  private MissionServiceLine InitOutstanding(MissionServiceLine missionServiceLine) {
    return missionServiceLine;
  }
  /*
  //Quantity Decimal
   //OnValidateBEGIN
   //IF CurrFieldNo = FIELDNO(Quantity) THEN
   //  IF Rec.Quantity <> xRec.Quantity THEN
   //    CheckIfInvoiced;  // debug isat.sf 100211

   //IF "Auction Bid" THEN
   //  TESTFIELD(Quantity,1);
   //IF Quantity < "Invoiced Quantity" THEN
   //  ERROR(STRSUBSTNO(Text8011402,"Invoiced Quantity"));

   //InitOutstanding;
   //VALIDATE("Qty. to Invoice", "Outstanding Quantity");
   //CalcAmounts;
   //"Cost Amount" := "Unit Cost" * Quantity;//*/
  public MissionServiceLine validateQuantity(
      MissionServiceLine missionServiceLine, BigDecimal quantity) throws AxelorException {

    if (missionServiceLine.getQuantity().compareTo(quantity) != 0) {
      missionServiceLine = this.checkIfInvoiced(missionServiceLine);
    }
    missionServiceLine.setQuantity(quantity);
    if (missionServiceLine.getAuctionBid()) {
      if (quantity.compareTo(BigDecimal.ONE) != 0) {
        throw new AxelorException(
            missionServiceLine, TraceBackRepository.CATEGORY_INCONSISTENCY, "Quantity must be 1");
      }
    }
    if (quantity.compareTo(missionServiceLine.getInvoicedQuantity()) < 0) {
      throw new AxelorException(
          missionServiceLine,
          TraceBackRepository.CATEGORY_CONFIGURATION_ERROR,
          "Quantity must be greater than invoiced quantity");
    }
    missionServiceLine = this.InitOutstanding(missionServiceLine);
    missionServiceLine =
        this.validateQtyToInvoice(missionServiceLine, missionServiceLine.getOutstandingQuantity());
    missionServiceLine = this.CalcAmounts(missionServiceLine);
    missionServiceLine.setCostAmount(missionServiceLine.getUnitCost().multiply(quantity));
    return missionServiceLine;
  }

  private MissionServiceLine checkIfInvoiced(MissionServiceLine missionServiceLine) {
    // TODO check if invoiced
    return missionServiceLine;
  }

  /*
  //Qty_to_Invoice Decimal
   //OnValidateBEGIN
   //IF ("Qty. to Invoice" * Quantity < 0) OR
   //   (ABS("Qty. to Invoice") > ABS("Outstanding Quantity"))
   //THEN BEGIN
   //  ERROR(Text8011400,ABS("Qty. to Invoice"), "Entry No.","Mission No.");     //ap16 isat.zw
   //END;

   ////<<AP26.ST
   //IF Quantity = 0 THEN
   //  "Amount To Invoice Incl. VAT" := 0
   //ELSE
   //  "Amount To Invoice Incl. VAT" := "Amount Incl. VAT" * ("Qty. to Invoice"/Quantity);
   ////>>AP26.ST//
   */
  public MissionServiceLine validateQtyToInvoice(
      MissionServiceLine missionServiceLine, BigDecimal qtyToInvoice) throws AxelorException {
    missionServiceLine.setQtytoInvoice(qtyToInvoice);
    if (qtyToInvoice.multiply(missionServiceLine.getQuantity()).compareTo(BigDecimal.ZERO) < 0
        || qtyToInvoice.abs().compareTo(missionServiceLine.getOutstandingQuantity().abs()) > 0) {
      throw new AxelorException(
          missionServiceLine,
          TraceBackRepository.CATEGORY_CONFIGURATION_ERROR,
          "Quantity to invoice must be between 0 and outstanding quantity");
    }
    if (missionServiceLine.getQuantity().compareTo(BigDecimal.ZERO) == 0) {
      missionServiceLine.setAmountToInvoiceInclVAT(BigDecimal.ZERO);
    } else {
      missionServiceLine.setAmountToInvoiceInclVAT(
          missionServiceLine
              .getAmountToInvoiceInclVAT()
              .multiply(qtyToInvoice.divide(missionServiceLine.getQuantity(), 2)));
    }
    return missionServiceLine;
  }
  // Invoiced_Quantity Decimal
  // OnValidateBEGIN
  // InitOutstanding; //AP26.ST//
  public MissionServiceLine validateInvoicedQuantity(
      MissionServiceLine missionServiceLine, BigDecimal invoicedQuantity) {
    missionServiceLine.setInvoicedQuantity(invoicedQuantity);
    missionServiceLine = this.InitOutstanding(missionServiceLine);
    return missionServiceLine;
  }

  private MissionServiceLine validateAcceptToInvoice(
      MissionServiceLine missionServiceLine, boolean b) {
    return missionServiceLine;
  }
  /*
    * //Outstanding_Quantity Decimal
  //OnValidateBEGIN
  ////<<AP26.ST
  ////"Outstanding Amount" := "Unit Price"*"Outstanding Quantity"; // AP15 isat.sf
  //IF "Outstanding Quantity" = Quantity THEN
  //  "Outstanding Amount" := "Amount Incl. VAT"
  //ELSE BEGIN
  //  IF Quantity <> 0 THEN
  //    "Outstanding Amount" := "Amount Incl. VAT" * ("Outstanding Quantity"/Quantity)
  //  ELSE
  //    "Outstanding Amount" := 0;
  //END;
  //"Completely Invoiced" := ("Outstanding Quantity" = 0);

  //IF "Outstanding Quantity" = 0 THEN BEGIN
  //  VALIDATE("Accept To Invoice", FALSE)
  //END
  //ELSE
  //  IF Chargeable THEN
  //    VALIDATE("Accept To Invoice", TRUE);
  ////>>AP26.ST//
    */
  public MissionServiceLine validateOutstandingQuantity(
      MissionServiceLine missionServiceLine, BigDecimal outstandingQuantity) {
    missionServiceLine.setOutstandingQuantity(outstandingQuantity);
    if (outstandingQuantity.compareTo(missionServiceLine.getQuantity()) == 0) {
      missionServiceLine.setOutstandingAmount(missionServiceLine.getAmountInclVAT());
    } else {
      if (missionServiceLine.getQuantity().compareTo(BigDecimal.ZERO) != 0) {
        missionServiceLine.setOutstandingAmount(
            missionServiceLine
                .getAmountInclVAT()
                .multiply(outstandingQuantity.divide(missionServiceLine.getQuantity(), 2)));
      } else {
        missionServiceLine.setOutstandingAmount(BigDecimal.ZERO);
      }
    }
    missionServiceLine.setCompletelyInvoiced(outstandingQuantity.compareTo(BigDecimal.ZERO) == 0);
    if (outstandingQuantity.compareTo(BigDecimal.ZERO) == 0) {
      missionServiceLine = this.validateAcceptToInvoice(missionServiceLine, false);
    } else {
      if (missionServiceLine.getChargeable()) {
        missionServiceLine = this.validateAcceptToInvoice(missionServiceLine, true);
      }
    }
    return missionServiceLine;
  }

  private MissionServiceLine validateServiceType(
      MissionServiceLine missionServiceLine, Integer servicetypeSelectAuctionbid) {
    return missionServiceLine;
  }

  /* //Auction_Bid Boolean
  //OnValidateBEGIN
  //IF "Auction Bid" THEN BEGIN
  //  VALIDATE("Service Type", "Service Type"::AuctionBid);
  //  "Price Includes VAT" := "Auction Incl. VAT";
  //END;//
  */
  public MissionServiceLine validateAuctionBid(
      MissionServiceLine missionServiceLine, boolean auctionBid) {
    missionServiceLine.setAuctionBid(auctionBid);
    if (auctionBid) {
      missionServiceLine =
          this.validateServiceType(
              missionServiceLine, MissionServiceLineRepository.SERVICETYPE_AUCTIONBID);
      missionServiceLine.setPriceIncludesVAT(missionServiceLine.getAuctionInclVAT());
    }
    return missionServiceLine;
  }

  /*
  * PROCEDURE UpdatePrice@1100281000();
   VAR
     lMissionPriceMgt@1100281000 : Codeunit 8011332;
     lAuctionPriceMgt@1100281001 : Codeunit 8011331;
   BEGIN
     IF "Auction Bid" THEN EXIT;
     CheckQuantity;
     //<<ap36 isat.zw
     GetItem;
     CASE Item."Service Price System" OF
       Item."Service Price System"::Standard : BEGIN
         IF "Transaction Type" = "Transaction Type"::Mission THEN
           MissionServicePriceMgt.FindMissionServicePrice(Rec,FALSE)
         ELSE
           AuctionServicePriceMgt.UpdateAuctionServicePrice(Rec,FALSE);
       END;
       Item."Service Price System"::Transport : BEGIN
         ShippingAgentPriceMgt.FindTransportServPrice(Rec,FALSE);
       END;
       Item."Service Price System"::Caretaker : BEGIN
         CaretakerServPriceMgt.FindMissionServicePrice(Rec,FALSE);
       END;
     END;
     //<<ap36 isat.zw
   END;
  */
  public MissionServiceLine updatePrice(MissionServiceLine missionServiceLine)
      throws AxelorException {
    MissionServicePriceManagement missionServicePriceManagement =
        Beans.get(MissionServicePriceManagement.class);
    AuctionServicePriceMgt auctionServicePriceMgt = Beans.get(AuctionServicePriceMgt.class);
    // ShippingAgentPriceMgt shippingAgentPriceMgt = Beans.get(ShippingAgentPriceMgt.class);
    // TODO ShippingAgentPriceMgt
    CaretakerServicePriceMgt caretakerServPriceMgt = Beans.get(CaretakerServicePriceMgt.class);
    if (missionServiceLine.getAuctionBid()) {
      return missionServiceLine;
    }
    missionServiceLine = this.CheckQuantity(missionServiceLine);
    switch (missionServiceLine.getProductNo().getServicePriceSystem()) {
      case ProductRepository.SERVICEPRICESYSTEM_STANDARD:
        if (missionServiceLine
            .getTransactionType()
            .equals(MissionServiceLineRepository.TRANSACTIONTYPE_MISSION)) {
          missionServiceLine =
              missionServicePriceManagement.findMissionServicePrice(missionServiceLine, false);
        } else {
          missionServiceLine =
              auctionServicePriceMgt.updateAuctionServicePrice(missionServiceLine, false);
        }
        break;
      case ProductRepository.SERVICEPRICESYSTEM_TRANSPORT:
        // missionServiceLine = shippingAgentPriceMgt.findTransportServPrice(missionServiceLine);
        // TODO ShippingAgentPriceMgt
        break;
      case ProductRepository.SERVICEPRICESYSTEM_CARETAKER:
        missionServiceLine =
            caretakerServPriceMgt.findMissionServicePrice(missionServiceLine, false);
        break;
    }
    return missionServiceLine;
  }

  private MissionServiceLine CheckQuantity(MissionServiceLine missionServiceLine) {
    return missionServiceLine;
  }

  // Lot_Price_Group Code10
  // OnValidateBEGIN
  // UpdatePrice; //AP23.ST//
  public MissionServiceLine validateLotPriceGroup(
      MissionServiceLine missionServiceLine, MissionLotPriceGroup lotPriceGroup)
      throws AxelorException {
    missionServiceLine.setLotPriceGroup(lotPriceGroup);
    missionServiceLine = this.updatePrice(missionServiceLine);
    return missionServiceLine;
  }

  public MissionServiceLine validateAuctionLotPriceGroup(
      MissionServiceLine missionServiceLine, AuctionLotPriceGroup lotPriceGroup)
      throws AxelorException {
    missionServiceLine.setAuctionLotPriceGroup(lotPriceGroup);
    missionServiceLine = this.updatePrice(missionServiceLine);
    return missionServiceLine;
  }

  /*//Contact_Price_Group Code10
    //OnValidateBEGIN
    //UpdatePrice; //AP23.ST//
  */
  public MissionServiceLine validateContactPriceGroup(
      MissionServiceLine missionServiceLine, MissionContactPriceGroup contactPriceGroup)
      throws AxelorException {
    missionServiceLine.setContactPriceGroup(contactPriceGroup);
    missionServiceLine = this.updatePrice(missionServiceLine);
    return missionServiceLine;
  }

  /*//Chargeable_Contact_No Code20
  //OnValidateBEGIN
  ////<<AP09.ISAT.ST
  //IF ("Chargeable Contact No." <> '') AND
  //   ("Chargeable Contact No." <> xRec."Chargeable Contact No.") THEN
  //  IF NOT SkipChangeCheck THEN
  //    MissServLineTools.AllowChargeableContactChange(Rec,TRUE);
  ////>>AP09.ISAT.ST

  //GetContact("Chargeable Contact No.");
  //CASE "Transaction Type" OF
  //  "Transaction Type"::Mission : "Contact Price Group" := Contact."Mission Contact Price Group";
  //  "Transaction Type"::Auction : "Contact Price Group" := Contact."Auction Contact Price Group";
  //END;
  //CASE "Contact Imputation Type" OF
  //  "Contact Imputation Type"::Buyer :
  //    IF "Buyer VAT Bus. Posting Group" <> Contact."VAT Bus. Posting Group" THEN
  //      VALIDATE("Buyer VAT Bus. Posting Group", Contact."VAT Bus. Posting Group");
  //  "Contact Imputation Type"::Seller : BEGIN
  //    IF "Mission No." = '' THEN BEGIN
  //      IF "Seller VAT Bus. Posting Group" <> Contact."VAT Bus. Posting Group" THEN
  //        VALIDATE("Seller VAT Bus. Posting Group", Contact."VAT Bus. Posting Group");
  //    END
  //    ELSE VALIDATE("Mission No.");
  //  END;
  //END; // CASE//
   */
  public MissionServiceLine validateChargeableContactNo(
      MissionServiceLine missionServiceLine, Partner chargeableContactNo) throws AxelorException {

    /*if (!missionServiceLine.getChargeableContactNo().equals(chargeableContactNo)) {
      if (!skipChangeCheck) {
        // TODO : MissServLineTools.AllowChargeableContactChange(Rec,TRUE);
      }
    }*/

    if (chargeableContactNo == null) {
      missionServiceLine.setChargeableContactNo(null);
      return missionServiceLine;
    }
    missionServiceLine.setChargeableContactNo(chargeableContactNo);
    if (missionServiceLine
        .getTransactionType()
        .equals(MissionServiceLineRepository.TRANSACTIONTYPE_MISSION)) {
      missionServiceLine.setContactPriceGroup(
          missionServiceLine.getChargeableContactNo().getContactMissionPriceGroup());
    } else {
      missionServiceLine.setAuctionContactPriceGroup(
          missionServiceLine.getChargeableContactNo().getContactAuctionPriceGroup());
    }

    if (missionServiceLine
        .getContactImputationType()
        .equals(MissionServiceLineRepository.CONTACTIMPUTATIONTYPE_BUYER)) {

      if (missionServiceLine.getChargeableContactNo() == null
          || missionServiceLine.getChargeableContactNo().getFiscalPosition() == null) {
        missionServiceLine.setBuyerFiscalPosition(null);
      } else if (!missionServiceLine
          .getBuyerFiscalPosition()
          .equals(missionServiceLine.getChargeableContactNo().getFiscalPosition())) {
        missionServiceLine.setBuyerFiscalPosition(
            missionServiceLine.getChargeableContactNo().getFiscalPosition());
      }
    } else {
      if (missionServiceLine.getMissionNo().equals(null)) {
        if (!missionServiceLine
            .getSellerFiscalPosition()
            .equals(missionServiceLine.getChargeableContactNo().getFiscalPosition())) {
          missionServiceLine.setSellerFiscalPosition(
              missionServiceLine.getChargeableContactNo().getFiscalPosition());
        }
      } else {
        missionServiceLine =
            this.validateMissionNo(missionServiceLine, missionServiceLine.getMissionNo());
      }
    }

    return missionServiceLine;
  }

  /*//Price_Date Date
  //OnValidateBEGIN
  //IF (CurrFieldNo = FIELDNO("Price Date")) AND ("Transaction Type" = "Transaction Type"::Auction) THEN
  //  ERROR(Text8011405);
  //UpdatePrice;// */
  public MissionServiceLine validatePriceDate(
      MissionServiceLine missionServiceLine, LocalDate priceDate) throws AxelorException {
    if (missionServiceLine
        .getTransactionType()
        .equals(MissionServiceLineRepository.TRANSACTIONTYPE_VENTE)) {
      throw new AxelorException(
          missionServiceLine,
          TraceBackRepository.CATEGORY_CONFIGURATION_ERROR,
          "Vous ne pouvez pas changer la date pour cette ligne");
    }
    missionServiceLine.setPriceDate(priceDate);
    missionServiceLine = this.updatePrice(missionServiceLine);
    return missionServiceLine;
  }

  /*//Unit_Price Decimal
  //OnValidateBEGIN
  //IF CurrFieldNo = FIELDNO("Unit Price") THEN
  //  CheckIfInvoiced;  // AP18 isat.sf
  ////IF CurrFieldNo = FIELDNO("Unit Price") THEN
  ////  IF xRec."Unit Price" <> Rec."Unit Price" THEN
  ////    "Fixed Amount" := TRUE; //  isat.Sf 210909

  //CalcAmounts;
  ////"Outstanding Amount" := "Unit Price"*"Outstanding Quantity"; // AP26.ST //debug Montant restant --> montant ttc
  //"Outstanding Amount" := "Amount Incl. VAT"*"Outstanding Quantity"; // ZW 11/06/2010

  //IF CurrFieldNo = FIELDNO("Unit Price") THEN
  //  IF Rec."Unit Price" <> xRec."Unit Price" THEN
  //    IF Rec."Unit Price" <> 0 THEN
  //      //VALIDATE("Fixed Amount",TRUE);
  //      TESTFIELD("Fixed Amount",TRUE); // AP41 isat.sf//
  */
  public MissionServiceLine validateUnitPrice(
      MissionServiceLine missionServiceLine, BigDecimal unitPrice) throws AxelorException {
    missionServiceLine.setUnitPrice(unitPrice);
    missionServiceLine = this.checkIfInvoiced(missionServiceLine);
    missionServiceLine = this.CalcAmounts(missionServiceLine);
    missionServiceLine.setOutstandingAmount(
        missionServiceLine
            .getAmountInclVAT()
            .multiply(missionServiceLine.getOutstandingQuantity()));

    if (!missionServiceLine.getUnitPrice().equals(missionServiceLine.getUnitPrice())) {
      if (!missionServiceLine.getUnitPrice().equals(BigDecimal.ZERO)) {
        missionServiceLine.setFixedAmount(true);
      }
    }
    if (missionServiceLine.getUnitPrice().compareTo(unitPrice) != 0
        && missionServiceLine.getUnitPrice().compareTo(BigDecimal.ZERO) != 0) {
      if (!missionServiceLine.getFixedAmount()) {
        throw new AxelorException(
            missionServiceLine,
            TraceBackRepository.CATEGORY_CONFIGURATION_ERROR,
            "Vous ne pouvez pas changer le prix pour cette ligne");
      }
    }
    return missionServiceLine;
  }
  /*//Amount Decimal
  //OnValidateBEGIN
  //CalcAmounts;// */
  public MissionServiceLine validateAmount(MissionServiceLine missionServiceLine, BigDecimal amount)
      throws AxelorException {
    missionServiceLine.setAmount(amount);
    missionServiceLine = this.CalcAmounts(missionServiceLine);
    return missionServiceLine;
  }
  /*//Unit_Cost Decimal
  //OnValidateBEGIN
  //"Cost Amount" := "Unit Cost" * Quantity;
  ////<<AP30.ST
  //IF "Unit Cost" <> 0 THEN BEGIN
  //  GetItem;
  //  //IF Item."Profit %" <> 0 THEN //ap36 isat.zw désactivé
  //    VALIDATE("Unit Price", "Unit Cost" * (1+(Item."Profit %"/100)));
  //END;
  ////>>AP30.ST// */
  public MissionServiceLine validateUnitCost(
      MissionServiceLine missionServiceLine, BigDecimal unitCost) throws AxelorException {
    missionServiceLine.setUnitCost(unitCost);
    missionServiceLine.setCostAmount(
        missionServiceLine.getUnitCost().multiply(missionServiceLine.getQuantity()));
    if (missionServiceLine.getUnitCost().compareTo(BigDecimal.ZERO) != 0) {
      missionServiceLine =
          this.validateUnitPrice(
              missionServiceLine,
              missionServiceLine
                  .getUnitCost()
                  .multiply(missionServiceLine.getProductNo().getManagPriceCoef()));
    }
    return missionServiceLine;
  }

  /*PROCEDURE GetQtyPerUnit@1000000005();
  BEGIN
    CASE Type OF
      Type::Service : BEGIN
        GetItem;
        "Qty. per Unit Of Measure" := UOMMgt.GetQtyPerUnitOfMeasure(Item,"Unit of Measure Code");
      END;
      Type::Resource : BEGIN
        IF "Unit of Measure Code" = '' THEN BEGIN
          GetResource;
          "Unit of Measure Code" := Resource."Base Unit of Measure";
        END;
        ResourceUnitofMeasure.GET("No.","Unit of Measure Code");
        "Qty. per Unit Of Measure" := ResourceUnitofMeasure."Qty. per Unit of Measure";
      END;
    END;
    VALIDATE(Quantity);
  END; */
  private MissionServiceLine getQtyPerUnit(MissionServiceLine missionServiceLine) {
    missionServiceLine.setQtyperUnitOfMeasure(BigDecimal.ONE);
    return missionServiceLine;
  }

  /*//Unit_of_Measure_Code Code10
  //OnValidateBEGIN
  //GetQtyPerUnit;// */
  public MissionServiceLine validateUnitOfMeasureCode(
      MissionServiceLine missionServiceLine, Unit unitOfMeasureCode) throws AxelorException {
    missionServiceLine.setUnitofMeasureCode(unitOfMeasureCode);
    missionServiceLine = this.getQtyPerUnit(missionServiceLine);
    return missionServiceLine;
  }

  /*//Contact_Imputation_Type Option
  //OnValidateVAR
  //NewContactNo@1000000000 : Code[20];
                                                             BEGIN
  ////AP02.ISAT.ST
  //CASE "Contact Imputation Type" OF
  //  "Contact Imputation Type"::Seller :
  //    BEGIN
  //      GetMission;
  //      NewContactNo := MissionHeader."Master Contact No.";
  //    END;
  //  "Contact Imputation Type"::Buyer :
  //    BEGIN
  //      TESTFIELD("Lot No.");
  //      NewContactNo := '';
  //      IF GetAuctionLine THEN
  //        IF AuctionLine."Is Auctionned" THEN
  //          NewContactNo := AuctionLine."Buyer Contact No.";
  //    END;
  //  "Contact Imputation Type"::None :
  //    NewContactNo := '';
  //END; // CASE
  //IF NewContactNo <> "Chargeable Contact No." THEN
  //  VALIDATE("Chargeable Contact No.", NewContactNo);// */
  public MissionServiceLine validateContactImputationType(
      MissionServiceLine missionServiceLine, Integer contactImputationType) throws AxelorException {
    missionServiceLine.setContactImputationType(contactImputationType);
    if (contactImputationType == null) {
      return missionServiceLine;
    }

    Partner newContactNo = null;
    switch (missionServiceLine.getContactImputationType()) {
      case MissionServiceLineRepository.CONTACTIMPUTATIONTYPE_SELLER:
        newContactNo = missionServiceLine.getMissionNo().getMasterContactNo();
        break;
      case MissionServiceLineRepository.CONTACTIMPUTATIONTYPE_BUYER:
        if (missionServiceLine.getLotNo() == null) {
          throw new AxelorException(
              missionServiceLine,
              TraceBackRepository.CATEGORY_CONFIGURATION_ERROR,
              "Vous devez sélectionner un lot");
        }
        AuctionLine auctionLine = this.getAuctionLine(missionServiceLine);
        if (auctionLine != null && auctionLine.getIsAuctionned()) {
          newContactNo = auctionLine.getBuyerContactNo();
        }
        break;
      case MissionServiceLineRepository.CONTACTIMPUTATIONTYPE_NONE:
        newContactNo = null;
        break;
    }
    if (newContactNo != null && !newContactNo.equals(missionServiceLine.getChargeableContactNo())) {
      missionServiceLine = this.validateChargeableContactNo(missionServiceLine, newContactNo);
    }

    return missionServiceLine;
  }

  /*
  * LOCAL PROCEDURE GetAuctionLine@1000000010() : Boolean;
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
   END;
  */
  private AuctionLine getAuctionLine(MissionServiceLine missionServiceLine) {
    if (missionServiceLine.getAuctionNo() == null
        || missionServiceLine.getLotNo() == null
        || missionServiceLine.getTransactionLineNo() == 0) {
      return null;
    }
    if (auctionLine != null
        && auctionLine.getAuctionNo().equals(missionServiceLine.getAuctionNo())
        && auctionLine.getLotNo().equals(missionServiceLine.getLotNo())
        && auctionLine.getLineNo() == missionServiceLine.getTransactionLineNo()) {
      return auctionLine;
    }

    auctionLine =
        Beans.get(AuctionLineRepository.class)
            .all()
            .filter(
                "self.auctionNo = ?1 AND self.lotNo = ?2 AND self.lineNo = ?3",
                missionServiceLine.getAuctionNo(),
                missionServiceLine.getLotNo(),
                missionServiceLine.getTransactionLineNo())
            .fetchOne();
    return auctionLine;
  }
  /*//Chargeable Boolean
  //OnValidateBEGIN
  ////AP26.ST
  //IF "Accept To Invoice" <> Chargeable THEN BEGIN
  //  IF "Outstanding Quantity" <> 0 THEN      //ap34 isat.zw
  //    VALIDATE("Accept To Invoice", Chargeable)
  //  ELSE
  //    VALIDATE("Accept To Invoice", FALSE);
  //END;// */
  public MissionServiceLine validateChargeable(
      MissionServiceLine missionServiceLine, Boolean chargeable) throws AxelorException {
    missionServiceLine.setChargeable(chargeable);
    if (missionServiceLine.getAcceptToInvoice() != missionServiceLine.getChargeable()) {
      if (missionServiceLine.getOutstandingQuantity().compareTo(BigDecimal.ZERO) != 0) {
        missionServiceLine =
            this.validateAcceptToInvoice(missionServiceLine, missionServiceLine.getChargeable());
      } else {
        missionServiceLine = this.validateAcceptToInvoice(missionServiceLine, false);
      }
    }
    return missionServiceLine;
  }
  /*
  * //Price_Includes_VAT Boolean
  //OnValidateVAR
  //VATPostingSetup@1000000000 : Record 325;
                                                             BEGIN
  //IF CurrFieldNo = FIELDNO("Price Includes VAT") THEN
  //  CheckIfInvoiced;  // AP19 isat.sf

  ////<< AP20 isat.sf
  ////IF CurrFieldNo = FIELDNO("Price Includes VAT") THEN BEGIN
  //IF Quantity <> 0 THEN //AP33.ST
  //  IF "Price Includes VAT"  THEN BEGIN
  //    VALIDATE("Unit Price","Amount Incl. VAT" / Quantity);
  //  END ELSE BEGIN
  //    VALIDATE("Unit Price",Amount / Quantity);
  //  END;
  ////END;
  ////>> AP20 isat.sf

  ////<< AP44 isat.sf
  //IF CurrFieldNo = FIELDNO("Price Includes VAT") THEN
  //  IF Rec."Price Includes VAT" <> xRec."Price Includes VAT" THEN
  //    TESTFIELD("Fixed Amount",TRUE); // AP41 isat.sf
  ////>> AP44 isat.sf//
  */
  public MissionServiceLine validatePriceIncludesVAT(
      MissionServiceLine missionServiceLine, Boolean priceIncludesVAT) throws AxelorException {
    missionServiceLine.setPriceIncludesVAT(priceIncludesVAT);
    if (missionServiceLine.getQuantity().compareTo(BigDecimal.ZERO) != 0) {
      if (missionServiceLine.getPriceIncludesVAT()) {
        missionServiceLine =
            this.validateUnitPrice(
                missionServiceLine,
                missionServiceLine
                    .getAmountInclVAT()
                    .divide(missionServiceLine.getQuantity(), 2, RoundingMode.HALF_UP));
      } else {
        missionServiceLine =
            this.validateUnitPrice(
                missionServiceLine,
                missionServiceLine
                    .getAmount()
                    .divide(missionServiceLine.getQuantity(), 2, RoundingMode.HALF_UP));
      }
    }
    if (missionServiceLine.getPriceIncludesVAT() != priceIncludesVAT
        && !missionServiceLine.getFixedAmount()) {
      throw new AxelorException(
          missionServiceLine,
          TraceBackRepository.CATEGORY_CONFIGURATION_ERROR,
          "Le champ 'Montant imposé' doit être coché");
    }
    return missionServiceLine;
  }
  /*//Service_Percent Decimal
  //OnValidateVAR
  //lUnitPrice@1100481000 : Decimal;
                                                             BEGIN
  ////<< AP18 isat.sf
  //IF CurrFieldNo = FIELDNO("Service %") THEN
  //  CheckIfInvoiced;
  //IF "Fixed Amount" AND ("Service %" <> 0) THEN BEGIN
  //  lUnitPrice := ("Reference Amount"*"Service %"/100);
  //  VALIDATE("Unit Price",lUnitPrice);
  //END;
  ////>> AP18 isat.sf

  ////IF CurrFieldNo = FIELDNO("Service %") THEN
  ////  IF xRec."Service %" <> Rec."Service %" THEN
  ////    "Fixed Amount" := TRUE; //  isat.Sf 210909

  //IF CurrFieldNo = FIELDNO("Service %") THEN
  //  IF Rec."Service %" <> xRec."Service %" THEN
  //    IF Rec."Service %" <> 0 THEN
  //      //VALIDATE("Fixed Amount",TRUE);
  //      TESTFIELD("Fixed Amount",TRUE); // AP41 isat.sf// */
  public MissionServiceLine validateServicePercent(
      MissionServiceLine missionServiceLine, BigDecimal servicePercent) throws AxelorException {
    missionServiceLine.setServicePercent(servicePercent);
    missionServiceLine = this.checkIfInvoiced(missionServiceLine);
    if (missionServiceLine.getFixedAmount()
        && missionServiceLine.getServicePercent().compareTo(BigDecimal.ZERO) != 0) {
      BigDecimal lUnitPrice =
          missionServiceLine
              .getReferenceAmount()
              .multiply(missionServiceLine.getServicePercent())
              .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
      missionServiceLine = this.validateUnitPrice(missionServiceLine, lUnitPrice);
    }
    if (missionServiceLine.getServicePercent().compareTo(servicePercent) != 0) {
      if (missionServiceLine.getServicePercent().compareTo(BigDecimal.ZERO) != 0
          && !missionServiceLine.getFixedAmount()) {
        throw new AxelorException(
            missionServiceLine,
            TraceBackRepository.CATEGORY_INCONSISTENCY,
            "Le champ 'Montant imposé' doit être coché");
      }
    }
    return missionServiceLine;
  }

  /*//Accept_To_Invoice Boolean
  //OnValidateBEGIN
  //IF "Accept To Invoice" THEN BEGIN
  //  //IF "Outstanding Quantity" = 0 THEN         //désactivé ap38 isat.zw
  //  //  FIELDERROR("Outstanding Quantity");      //désactivé ap38 isat.zw
  //  TESTFIELD(Chargeable);
  //  VALIDATE("Qty. to Invoice", "Outstanding Quantity");
  //END
  //ELSE
  //  VALIDATE("Qty. to Invoice", 0); */
  public MissionServiceLine validateAcceptToInvoice(
      MissionServiceLine missionServiceLine, Boolean acceptToInvoice) throws AxelorException {
    missionServiceLine.setAcceptToInvoice(acceptToInvoice);
    if (missionServiceLine.getAcceptToInvoice()) {
      if (missionServiceLine.getOutstandingQuantity().compareTo(BigDecimal.ZERO) == 0) {
        if (missionServiceLine.getChargeable()) {
          missionServiceLine =
              this.validateQtyToInvoice(
                  missionServiceLine, missionServiceLine.getOutstandingQuantity());
        } else {
          throw new AxelorException(
              missionServiceLine,
              TraceBackRepository.CATEGORY_INCONSISTENCY,
              "Le champ 'Facturable' doit être coché");
        }
      }
    } else {
      missionServiceLine = this.validateQtyToInvoice(missionServiceLine, BigDecimal.ZERO);
    }
    return missionServiceLine;
  }

  public MissionServiceLine validateSellerFiscalPosition(
      MissionServiceLine missionServiceLine, FiscalPosition sellerFiscalPosition)
      throws AxelorException {
    missionServiceLine.setSellerFiscalPosition(sellerFiscalPosition);

    missionServiceLine = this.updateVAT(missionServiceLine);
    return missionServiceLine;
  }

  /*
  * PROCEDURE UpdateVAT@1000000017();
   VAR
     lVATPostingSetup@1000000002 : Record 325;
     lAuctionSetup@1000000000 : Record 8011377;
     lBuyerVATPostingGroup@1000000003 : Code[10];
     OldVAT@1000000001 : Decimal;
   BEGIN
     OldVAT := "VAT %"; //AP26.ST
     IF ("VAT Prod. Posting Group" <> '') AND
        ("Gen. Prod. Posting Group" <> '') AND
        ("Contact Imputation Type" <> "Contact Imputation Type"::None) THEN BEGIN
       // Valeur par défaut acheteur
       IF "Buyer VAT Bus. Posting Group" = '' THEN BEGIN
         lAuctionSetup.GET;
         lBuyerVATPostingGroup := lAuctionSetup."Default VAT Bus. Posting Group";
       END
       ELSE
         lBuyerVATPostingGroup := "Buyer VAT Bus. Posting Group";
       APPostingMgt.GetVATPostingSetup("Gen. Prod. Posting Group", "VAT Prod. Posting Group",
                                       "Seller VAT Bus. Posting Group", lBuyerVATPostingGroup,
                                       "Contact Imputation Type" = "Contact Imputation Type"::Buyer, "Auction Incl. VAT",
                                       lVATPostingSetup);
     END;

     IF "Auction Bid" THEN BEGIN
       "VAT %" := lVATPostingSetup."Bid VAT %";
       "VAT Identifier" := lVATPostingSetup."Bid VAT Identifier";
     END
     ELSE BEGIN
       "VAT %" := lVATPostingSetup."VAT %";
       "VAT Identifier" := lVATPostingSetup."VAT Identifier";
     END;
     "VAT Calculation Type" := lVATPostingSetup."VAT Calculation Type";
     //<<AP28.ST
     IF ("VAT %" <> OldVAT) AND ("Price Includes VAT") THEN BEGIN
       GetItem;
       IF "Transaction Type" = "Transaction Type"::Mission THEN
         MissionServicePriceMgt.VATConvert(Item,Rec)
       ELSE
         AuctionServicePriceMgt.VATConvert(Item,Rec);
     END;
     //>>AP28.ST
     //<<AP25.ST
     CASE "VAT Calculation Type" OF
       "VAT Calculation Type"::"Reverse Charge VAT",
       "VAT Calculation Type"::Exemption, //AP48.ST
       "VAT Calculation Type"::"Sales Tax":
         "VAT %" := 0;
     END;
     //>>AP25.ST
     CalcAmounts;
   END;
  */
  private MissionServiceLine updateVAT(MissionServiceLine missionServiceLine) {
    FiscalPosition buyerFiscalPosition;
    // BigDecimal oldVAT = missionServiceLine.getvATPercent();
    if (missionServiceLine.getProductFamily() != null
        && missionServiceLine.getContactImputationType() != null
        && missionServiceLine.getContactImputationType()
            != MissionServiceLineRepository.CONTACTIMPUTATIONTYPE_NONE) {
      // Valeur par défaut acheteur
      if (missionServiceLine.getBuyerFiscalPosition() == null) {
        AuctionSetup auctionSetup = Beans.get(AuctionSetupRepository.class).all().fetchOne();
        buyerFiscalPosition = auctionSetup.getDefaultFiscalPosition();
      } else buyerFiscalPosition = missionServiceLine.getBuyerFiscalPosition();

      // TODO : APPostingMgt.GetVATPostingSetup
    }
    return missionServiceLine;
  }

  /*//Buyer_VAT_Bus_Posting_Group Code10
  //OnValidateBEGIN
  ////<<AP09.ISAT.ST
  //IF ("Buyer VAT Bus. Posting Group" <> '') AND
  //   ("Buyer VAT Bus. Posting Group" <> xRec."Buyer VAT Bus. Posting Group") THEN
  //  IF NOT SkipChangeCheck THEN
  //    MissServLineTools.AllowBuyerVATGrpChange(Rec,TRUE);
  ////>>AP09.ISAT.ST
  //
  ////<< AP07 ISAT.EB
  //IF ("Buyer VAT Bus. Posting Group" <> '') AND
  //   ("Buyer VAT Bus. Posting Group" <> xRec."Buyer VAT Bus. Posting Group")
  //THEN BEGIN
  //  IF CurrFieldNo = FIELDNO("Buyer VAT Bus. Posting Group") THEN
  //    ERROR(Text8011401,FIELDCAPTION("Buyer VAT Bus. Posting Group"));
  //END;
  ////>> AP07 ISAT.EB
  //
  //UpdateVAT;// */
  public MissionServiceLine validateBuyerFiscalPosition(
      MissionServiceLine missionServiceLine, FiscalPosition buyerFiscalPosition) {
    missionServiceLine.setBuyerFiscalPosition(buyerFiscalPosition);
    missionServiceLine = this.updateVAT(missionServiceLine);
    return missionServiceLine;
  }

  /*//Reserve_Variance Decimal
  //OnValidateBEGIN
  //IF "Reserve Variance" <> 0 THEN BEGIN
  //  TESTFIELD("Auction Bid", TRUE);
  //  TESTFIELD("Contact Imputation Type", "Contact Imputation Type"::Seller);
  //END;
  //TESTFIELD("Invoiced Quantity",0);// */
  public MissionServiceLine validateReserveVariance(
      MissionServiceLine missionServiceLine, BigDecimal reserveVariance) throws AxelorException {
    missionServiceLine.setReserveVariance(reserveVariance);
    if (missionServiceLine.getReserveVariance().compareTo(BigDecimal.ZERO) != 0) {
      if (!missionServiceLine.getAuctionBid()) {
        throw new AxelorException(
            missionServiceLine,
            TraceBackRepository.CATEGORY_INCONSISTENCY,
            "Le champ 'Adjudication' doit être coché");
      }
      if (missionServiceLine.getContactImputationType()
          != MissionServiceLineRepository.CONTACTIMPUTATIONTYPE_SELLER) {
        throw new AxelorException(
            missionServiceLine,
            TraceBackRepository.CATEGORY_INCONSISTENCY,
            "Le champ 'Type imputation contact' doit être de type 'Vendeur'");
      }
    }
    if (missionServiceLine.getInvoicedQuantity().compareTo(BigDecimal.ZERO) != 0) {
      throw new AxelorException(
          missionServiceLine,
          TraceBackRepository.CATEGORY_INCONSISTENCY,
          "Le champ 'Quantité facturée' doit être différent de 0");
    }
    return missionServiceLine;
  }

  // Auction_Incl_VAT Boolean
  // OnValidateBEGIN
  // VALIDATE("Auction Bid"); // AP08 ISAT.EB
  // UpdateVAT;//
  public MissionServiceLine validateAuctionInclVAT(
      MissionServiceLine missionServiceLine, Boolean auctionInclVAT) {
    missionServiceLine.setAuctionInclVAT(auctionInclVAT);
    missionServiceLine =
        this.validateAuctionBid(missionServiceLine, missionServiceLine.getAuctionBid());
    missionServiceLine = this.updateVAT(missionServiceLine);
    return missionServiceLine;
  }
  /*
  * //Fixed_Amount Boolean
  //OnValidateBEGIN
  //IF CurrFieldNo = FIELDNO("Fixed Amount") THEN
  //  CheckIfInvoiced;  // AP18 isat.sf

  ////<< AP18 isat.sf
  //IF (xRec."Fixed Amount" = TRUE) AND (Rec."Fixed Amount" = FALSE) THEN BEGIN
  //  IF "Invoiced Amount" <> 0 THEN BEGIN
  //    //VALIDATE("Unit Price","Invoiced Amount");
  //    "Fixed Amount" := TRUE;
  //  END ELSE BEGIN
  //    UpdatePrice;
  //  END;
  //END;
  ////>> AP18 isat.sf

  ////IF "Fixed Amount" = TRUE THEN    // désactivé AP20 isat.sf
  ////  VALIDATE("Price Includes VAT",TRUE); //  AP19 isat.sf //ap37 isat.zw désactivé//
  */
  public MissionServiceLine validateFixedAmount(
      MissionServiceLine missionServiceLine, Boolean fixedAmount) throws AxelorException {
    missionServiceLine.setFixedAmount(fixedAmount);
    if (missionServiceLine.getFixedAmount() && !fixedAmount) {

      // TODO MISSION SERVICE LEDGER ENTRY
      /*
      if (missionServiceLine.getInvoicedAmount().compareTo(BigDecimal.ZERO) != 0) {
        missionServiceLine.setFixedAmount(true);
      } else {
        missionServiceLine = this.updatePrice(missionServiceLine);
      }
      */
    }
    return missionServiceLine;
  }
  /*
  * //Cancelled Option
  //OnValidateBEGIN
  ////<<AP29.ST
  //TESTFIELD("Invoiced Quantity", 0);
  //IF Cancelled = Cancelled::Yes THEN BEGIN
  //  IF Chargeable THEN
  //    VALIDATE(Chargeable, FALSE);
  //END;
  ////>>AP29.ST//
  */
  public MissionServiceLine validateCancelled(
      MissionServiceLine missionServiceLine, Boolean cancelled) throws AxelorException {
    missionServiceLine.setCancelled(cancelled);
    if (missionServiceLine.getInvoicedQuantity().compareTo(BigDecimal.ZERO) != 0) {
      throw new AxelorException(
          missionServiceLine,
          TraceBackRepository.CATEGORY_INCONSISTENCY,
          "Le champ 'Quantité facturée' doit être égal à 0");
    }
    if (missionServiceLine.getCancelled()) {

      if (missionServiceLine.getChargeable()) {
        missionServiceLine = this.validateChargeable(missionServiceLine, false);
      }
    }
    return missionServiceLine;
  }

  public MissionServiceLine validateTransactionLineNo(
      MissionServiceLine lMissionServiceLine, Integer pTransactionLineNo) {
    lMissionServiceLine.setTransactionLineNo(pTransactionLineNo);
    return lMissionServiceLine;
  }

  public MissionServiceLine validateType(MissionServiceLine lMissionServiceLine, Integer type) {
    lMissionServiceLine.setType(type);
    return lMissionServiceLine;
  }

  public MissionServiceLine validateTransactionType(
      MissionServiceLine lMissionServiceLine, Integer transactiontypeMission) {
    lMissionServiceLine.setTransactionType(transactiontypeMission);
    return lMissionServiceLine;
  }

  public MissionServiceLine onInsert(MissionServiceLine lMissionServiceLine) {
    // TODO à implémenter
    return lMissionServiceLine;
  }
}
