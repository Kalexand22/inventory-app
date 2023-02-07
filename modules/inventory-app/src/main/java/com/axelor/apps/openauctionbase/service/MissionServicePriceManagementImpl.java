package com.axelor.apps.openauctionbase.service;

import com.axelor.apps.base.db.Partner;
import com.axelor.apps.base.db.Product;
import com.axelor.apps.base.db.TradingName;
import com.axelor.apps.base.db.repo.ProductRepository;
import com.axelor.apps.openauction.db.AccesBuffer;
import com.axelor.apps.openauction.db.Lot;
import com.axelor.apps.openauction.db.LotResaleRights;
import com.axelor.apps.openauction.db.MissionContactPriceGroup;
import com.axelor.apps.openauction.db.MissionHeader;
import com.axelor.apps.openauction.db.MissionLine;
import com.axelor.apps.openauction.db.MissionLotPriceGroup;
import com.axelor.apps.openauction.db.MissionServiceLine;
import com.axelor.apps.openauction.db.MissionServicePrice;
import com.axelor.apps.openauction.db.MissionTemplate;
import com.axelor.apps.openauction.db.TariffScale;
import com.axelor.apps.openauction.db.repo.LotResaleRightsRepository;
import com.axelor.apps.openauction.db.repo.MissionHeaderRepository;
import com.axelor.apps.openauction.db.repo.MissionLineRepository;
import com.axelor.apps.openauction.db.repo.MissionServiceLineRepository;
import com.axelor.apps.openauction.db.repo.MissionServicePriceRepository;
import com.axelor.apps.openauctionbase.validate.MissionHeaderValidate;
import com.axelor.apps.openauctionbase.validate.MissionLineValidate;
import com.axelor.apps.openauctionbase.validate.MissionServiceLineValidate;
import com.axelor.exception.AxelorException;
import com.axelor.exception.db.repo.TraceBackRepository;
import com.axelor.inject.Beans;
import com.google.inject.Inject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MissionServicePriceManagementImpl implements MissionServicePriceManagement {

  /*
  * MissionMgt@1180113000 : Codeunit 8011366;
     Currency@1000000031 : Record 4;
     PricesInCurrency@1000000000 : Boolean;
     CurrencyFactor@1000000007 : Decimal;
     ExchRateDate@1000000008 : Date;
     GLSetup@1000000009 : Record 98;
     PricesInclVAT@1000000013 : Boolean;
     VATPerCent@1000000012 : Decimal;
     VATBusPostingGr@1000000010 : Code[10];
     LineDiscPerCent@1000000018 : Decimal;
     AllowLineDisc@1000000017 : Boolean;
     AllowInvDisc@1000000016 : Boolean;
     Text010@1000000021 : TextConst 'ENU=Prices including VAT cannot be calculated when %1 is %2.;FRA=Les prix TTC ne peuvent pas être calculés quand %1 est identique à %2.;NLB=Prijzen inclusief BTW kunnen niet berekend worden als %1 %2 is.';
     VATCalcType@1000000023 : 'Normal VAT,Reverse Charge VAT,Full VAT,Sales Tax';
     MissionServicePrice@1000000024 : Record 8011469;
     TempAccesBuffer@1000000006 : TEMPORARY Record 8011477;
     Item@1000000001 : Record 27;
     Lot@1000000026 : Record 8011404;
     Currency2@1000000005 : Record 4;
     BaseAmount@1000000034 : Decimal;
     ReserveAmount@1000000025 : Decimal;
     EstimatedMinAmount@1000000029 : Decimal;
     EstimatedMaxAmount@1000000030 : Decimal;
     BidReferenceAmount@1180113002 : Decimal;
     BidPriceInclVAT@1000000038 : Decimal;
     BidPrice@1000000039 : Decimal;
     MissionValue@1100481001 : Decimal;
     ReferenceAmountCalcOk@1000000027 : Boolean;
     PricesInCurrency2@1000000033 : Boolean;
     Text8011400@1000000035 : TextConst 'ENU=Unit Price;FRA=Prix unitaire';
     Text8011401@1000000036 : TextConst 'ENU=Commision;FRA=Commision';
     IsApplyOnBidPrice@1000000041 : Boolean;
     Text001@1100481000 : TextConst 'ENU=Update services lines \@1@@@@@@@@@@@@@@@@@@@@@@@@@\;FRA=Mise à jour des prestations \@1@@@@@@@@@@@@@@@@@@@@@@@@@\';
     Text8011402@1100481002 : TextConst 'ENU=Mission value is not definied for this mission type.;FRA=Le montant mission n''est pas défini pour ce type de mission.';
     HideDialog@1180113001 : Boolean;
  */
  Product product;
  TariffScale tariffScale;
  Boolean lUseJudicialTariff;
  BigDecimal lPriceFactor;
  BigDecimal lBaseFactor;
  BigDecimal baseAmount;
  MissionServicePrice missionServicePrice;
  AccesBuffer tempAccesBuffer;
  List<AccesBuffer> tempAccesBufferList;
  Boolean lTariffFound;
  LotResaleRights lLotResaleRights;
  Boolean referenceAmountCalcOk;
  BigDecimal bidReferenceAmount;
  BigDecimal bidPriceInclVAT;
  BigDecimal bidPrice;
  BigDecimal missionValue;
  BigDecimal reserveAmount;
  BigDecimal estimatedMinAmount;
  BigDecimal estimatedMaxAmount;
  Boolean isApplyOnBidPrice;
  Boolean hideDialog;
  Boolean lJudInvCalculated;
  MissionManagement missionMgt;
  MissionServiceLineValidate missionServiceLineValidate;
  MissionLineRepository missionLineRepository;
  MissionServiceLineRepository missionServiceLineRepository;

  @Inject
  public MissionServicePriceManagementImpl(
      MissionLineRepository missionLineRepository,
      MissionServiceLineRepository missionServiceLineRepository) {
    missionMgt = Beans.get(MissionManagement.class);
    missionServiceLineValidate = Beans.get(MissionServiceLineValidate.class);
    this.missionLineRepository = missionLineRepository;
    this.missionServiceLineRepository = missionServiceLineRepository;
    tempAccesBufferList = new ArrayList<AccesBuffer>();
    lUseJudicialTariff = false;
    lPriceFactor = BigDecimal.ZERO;
    lBaseFactor = BigDecimal.ZERO;
    baseAmount = BigDecimal.ZERO;
    isApplyOnBidPrice = false;
    hideDialog = false;
    lJudInvCalculated = false;
    lTariffFound = false;
  }

  /*
  * PROCEDURE FindMissionServicePrice@1000000001(VAR pMissionServiceLine@1000000000 : Record 8011449;pEstimated@1000000004 : Boolean);
     VAR
       lTariffScale@1000000005 : Record 8011428;
       lTempAmount@1000000001 : Decimal;
       lTariffFound@1000000003 : Boolean;
       lUseJudicialTariff@1000000002 : Boolean;
       lLotResaleRights@1100281000 : Record 8011499;
       lPriceFactor@1100281001 : Decimal;
       lBaseFactor@1100281002 : Decimal;
       lJudInvCalculated@1180113000 : Boolean;
     BEGIN
       ReferenceAmountCalcOk := FALSE;

       WITH pMissionServiceLine DO BEGIN

       // AP13 isat.Sf aucun update sur les lignes de débours coché "report de somme"
       IF pMissionServiceLine.Type = pMissionServiceLine.Type::Service THEN BEGIN
         //IF Item.GET(pMissionServiceLine."No.") THEN BEGIN
         //  IF Item."Outlay Sum By Deferring" THEN
         //    EXIT;
         //END;
         IF pMissionServiceLine."Fixed Amount" THEN BEGIN
           EXIT;
         END;
       END;

       // Calcul estimation : pas de test Chargeable
       //  IF NOT Chargeable THEN BEGIN
       //    EXIT;
       //  END;
         CASE Type OF
           Type::Service : BEGIN
             IF Item."No." <> "No." THEN BEGIN
               Item.GET("No.");
             END;

             IF lTariffScale.GET(Item."Tariff Scale") THEN BEGIN
               lUseJudicialTariff := lTariffScale."Scale Type" = lTariffScale."Scale Type"::"Judicial Inventory";
             END;

       //<<AP02.ISAT.SC
             lPriceFactor := 1;
             lBaseFactor := 1;
             IF "Resale Right" THEN BEGIN
               lLotResaleRights.INIT;
               IF lLotResaleRights.GET(pMissionServiceLine."Lot No.",pMissionServiceLine."Organization Contact") THEN BEGIN
                 lPriceFactor := lLotResaleRights."Organization Distribution"/100;
                 lBaseFactor := lLotResaleRights."Auction Bid Percentage"/100;
               END;
             END;
       //>>AP02.ISAT.SC

             IF NOT lUseJudicialTariff THEN BEGIN
               lTariffFound :=
                 FindServicePrice(
                    "No.","Mission Template Code","Mission No.",
                    "Lot Price Group","Lot No.",
                    "Contact Price Group","Chargeable Contact No.",
                    GetPriceDate(pMissionServiceLine),
                    "Responsibility Center");
             END;

             "Reference Amount" := BaseAmount * lBaseFactor;

             IF lTariffFound AND NOT lUseJudicialTariff THEN BEGIN
               "Price Includes VAT" := MissionServicePrice."Price Includes VAT"; //AP03
               CASE MissionServicePrice."Calculation Type" OF
                 MissionServicePrice."Calculation Type"::UnitPrice : BEGIN
                  "Calculation Type" := "Calculation Type"::UnitPrice;
                   VALIDATE("Unit Price",MissionServicePrice."Unit Price" * lPriceFactor);
                   TempAccesBuffer.DELETEALL;
                   TempAccesBuffer.INIT;
                   TempAccesBuffer.Code := Text8011400;
                   TempAccesBuffer."Decimal 1" := 0;
                   TempAccesBuffer."Decimal 2" := 0;
                   TempAccesBuffer."Decimal 3" := "Reference Amount";
                   TempAccesBuffer."Decimal 5" := "Unit Price";
                   TempAccesBuffer."Decimal 6" := "Unit Price";
                   TempAccesBuffer."Decimal 4" := 0;
                   TempAccesBuffer.INSERT;
                   "Service %" := 0; //ap07 isat.zw
                 END;
                 MissionServicePrice."Calculation Type"::"Service%" : BEGIN
                  "Calculation Type" := "Calculation Type"::"Service%";
                   VALIDATE("Unit Price", "Reference Amount" * (MissionServicePrice."Service %" / 100) * lPriceFactor);
                   TempAccesBuffer.DELETEALL;
                   TempAccesBuffer.INIT;
                   TempAccesBuffer.Code := Text8011401;
                   TempAccesBuffer."Decimal 1" := 0;
                   TempAccesBuffer."Decimal 2" := 0;
                   TempAccesBuffer."Decimal 3" := "Reference Amount" ;
                   TempAccesBuffer."Decimal 4" := MissionServicePrice."Service %" * lPriceFactor;
                   TempAccesBuffer."Decimal 5" := "Reference Amount" * (MissionServicePrice."Service %" / 100) * lPriceFactor;
                   TempAccesBuffer."Decimal 6" := "Reference Amount" * (MissionServicePrice."Service %" / 100) * lPriceFactor;
                   TempAccesBuffer.INSERT;
                   "Service %" := MissionServicePrice."Service %";
                 END;
                 MissionServicePrice."Calculation Type"::"Commission Scale" : BEGIN
                  "Calculation Type" := "Calculation Type"::"Commission Scale";
                   GetTariffScaleAmount("Reference Amount",MissionServicePrice."Tariff Scale",TempAccesBuffer);
                   lTempAmount := 0;
                   IF TempAccesBuffer.FINDFIRST THEN BEGIN
                     REPEAT
                       lTempAmount += TempAccesBuffer."Decimal 5";
                     UNTIL TempAccesBuffer.NEXT = 0;
                   END;
                   VALIDATE("Unit Price",lTempAmount * lPriceFactor);
                   "Service %" := 0; //ap07 isat.zw
                 END;
               END; // CASE Calculation type
               "Estimated Value" := pEstimated;
               CheckAmount(pMissionServiceLine, MissionServicePrice, Item); //AP15.ST
               VATConvert(Item, pMissionServiceLine); //AP16.ST
             END ELSE BEGIN // lTariffFound AND NOT lUseJudicialTariff
               IF NOT ReferenceAmountCalcOk THEN BEGIN
                 GetLotAmount(pMissionServiceLine."Mission No.",pMissionServiceLine."Lot No.");
               END;
               IF IsApplyOnBidPrice THEN BEGIN
                 CASE Item."Base Calc. After Auction" OF
                   Item."Base Calc. After Auction"::"Bid Price" : BaseAmount := BidReferenceAmount; //AP23.st
                   Item."Base Calc. After Auction"::"BidPrice Incl.VAT" : BaseAmount := BidPriceInclVAT; //AP23.st
                   Item."Base Calc. After Auction"::"Bid Price Excl. VAT" : BaseAmount := BidPrice;
                   Item."Base Calc. After Auction"::"Reserve Price" : BaseAmount := ReserveAmount;
                 END; //CASE
               END ELSE BEGIN
                 CASE Item."Base Calc. Before Auction" OF
                   Item."Base Calc. Before Auction"::"Reserve Price" : BaseAmount := ReserveAmount;
                   Item."Base Calc. Before Auction"::"Estimated Value Min" : BaseAmount := EstimatedMinAmount;
                   Item."Base Calc. Before Auction"::"Estimated Value Max" : BaseAmount := EstimatedMaxAmount;
                   Item."Base Calc. Before Auction"::MissionValue, Item."Base Calc. Before Auction"::MissionAdjust,
                   Item."Base Calc. Before Auction"::MissionInitValue :
                     BaseAmount := MissionValue; //AP14.ST
                   //<< AP19 isat.sf
                   Item."Base Calc. Before Auction"::"Popular Lines On Operating" : BEGIN
                     lJudInvCalculated := FALSE;
                     BaseAmount := 0 ;
                     "Service %" := 0;
                     MissionMgt.CalcJudInvService(pMissionServiceLine);
                     lJudInvCalculated := TRUE;
                   END;
                   Item."Base Calc. Before Auction"::"Popular Lines Mid Operating Realization" : BEGIN
                     lJudInvCalculated := FALSE;
                     BaseAmount := 0 ;
                     "Service %" := 0;
                     MissionMgt.CalcJudInvService(pMissionServiceLine);
                     lJudInvCalculated := TRUE;
                   END;
                   //>> AP19 isat.sf
                   //<< AP20 isat.sf
                   Item."Base Calc. Before Auction"::"Popular Lines On Realization" : BEGIN
                     lJudInvCalculated := FALSE;
                     BaseAmount := 0 ;
                     "Service %" := 0;
                     MissionMgt.CalcJudInvService(pMissionServiceLine);
                     lJudInvCalculated := TRUE;
                   END;
                   //>> AP20 isat.sf
                   //<< AP21 isat.sf
                   Item."Base Calc. Before Auction"::"Popular Lines Mid 2" : BEGIN
                     lJudInvCalculated := FALSE;
                     BaseAmount := 0 ;
                     "Service %" := 0;
                     MissionMgt.CalcJudInvService(pMissionServiceLine);
                     lJudInvCalculated := TRUE;
                   END;
                   Item."Base Calc. Before Auction"::"Popular Lines On Realization 2" : BEGIN
                     lJudInvCalculated := FALSE;
                     BaseAmount := 0 ;
                     "Service %" := 0;
                     MissionMgt.CalcJudInvService(pMissionServiceLine);
                     lJudInvCalculated := TRUE;
                   END;
                   //>> AP21 isat.sf
                 END; //CASE
               END;

               BaseAmount := ABS(BaseAmount); //AP01.ISAT.ST
               "Reference Amount" := BaseAmount*lBaseFactor;

               // Calcul prix sur article
               IF lJudInvCalculated = FALSE THEN // AP19 isat.sf
                "Price Includes VAT" := Item."Price Includes VAT";  //AP03

               CASE TRUE OF
                 Item."Unit Price" <> 0 : BEGIN
                   "Calculation Type" := "Calculation Type"::UnitPrice;
                   VALIDATE("Unit Price",Item."Unit Price" * lPriceFactor);
                   TempAccesBuffer.DELETEALL;
                   TempAccesBuffer.INIT;
                   TempAccesBuffer.Code := Text8011400;
                   TempAccesBuffer."Decimal 1" := 0;
                   TempAccesBuffer."Decimal 2" := 0;
                   TempAccesBuffer."Decimal 3" := "Reference Amount";
                   TempAccesBuffer."Decimal 4" := 0;
                   TempAccesBuffer."Decimal 5" := "Unit Price";
                   TempAccesBuffer."Decimal 6" := "Unit Price";
                   TempAccesBuffer.INSERT;
                   //"Service %" := MissionServicePrice."Service %";
                   "Service %" := 0; //ap07 isat.zw
                 END;
                 Item."Service %" <> 0 : BEGIN
                   "Calculation Type" := "Calculation Type"::"Service%";
                   VALIDATE("Unit Price","Reference Amount" * (Item."Service %" / 100) * lPriceFactor);
                   pMissionServiceLine."Service %" := Item."Service %" * lPriceFactor;
                   TempAccesBuffer.DELETEALL;
                   TempAccesBuffer.INIT;
                   TempAccesBuffer.Code := Text8011401;
                   TempAccesBuffer."Decimal 1" := 0;
                   TempAccesBuffer."Decimal 2" := 0;
                   TempAccesBuffer."Decimal 3" := "Reference Amount";
                   TempAccesBuffer."Decimal 4" := Item."Service %" * lPriceFactor;
                   TempAccesBuffer."Decimal 5" := "Reference Amount" * (Item."Service %" / 100)* lPriceFactor;
                   TempAccesBuffer."Decimal 6" := "Reference Amount" * (Item."Service %" / 100) * lPriceFactor;
                   TempAccesBuffer.INSERT;
                 END;
                 Item."Tariff Scale" <> '' : BEGIN
                   IF lJudInvCalculated = FALSE THEN BEGIN  // AP19 isat.sf
                     "Calculation Type" := "Calculation Type"::"Commission Scale";
                      GetTariffScaleAmount("Reference Amount",Item."Tariff Scale",TempAccesBuffer);
                      lTempAmount := 0;
                      IF TempAccesBuffer.FINDFIRST THEN BEGIN
                        REPEAT
                          lTempAmount += TempAccesBuffer."Decimal 5";
                        UNTIL TempAccesBuffer.NEXT = 0;
                      END;
                      VALIDATE("Unit Price",lTempAmount * lPriceFactor);
                      "Service %" := 0; //ap07 isat.zw
                    END;
                 END;
                 ELSE BEGIN
                   VALIDATE("Unit Price",0);   //AP12.ZW
                   "Service %" := 0; //AP12.ZW
                 END;
               END;
               "Estimated Value" := pEstimated;
               CheckAmount(pMissionServiceLine, MissionServicePrice, Item); //AP15.ST
               VATConvert(Item,pMissionServiceLine);
             END;
           END;
           Type::Resource : BEGIN
           END;
         END;
       END;
     END;

  */
  @Override
  public MissionServiceLine findMissionServicePrice(
      MissionServiceLine pMissionServiceLine, Boolean pEstimated) throws AxelorException {

    if (pMissionServiceLine.getType().equals(MissionServiceLineRepository.TYPE_SERVICE)) {
      if (pMissionServiceLine.getFixedAmount()) {
        return pMissionServiceLine;
      }

      if (product == null) {
        product = pMissionServiceLine.getProductNo();
      }
      if (tariffScale == null && product.getTariffScale() != null) {
        tariffScale = product.getTariffScale();
      }
      findPriceAndBaseFactor(pMissionServiceLine);
      lTariffFound = findServicePrice(pMissionServiceLine);
      pMissionServiceLine.setReferenceAmount(baseAmount.multiply(lBaseFactor));
      if (lTariffFound && !lUseJudicialTariff) {
        pMissionServiceLine = applyTariff(pMissionServiceLine, pEstimated);
      } else {
        pMissionServiceLine = calcWhenNoTariff(pMissionServiceLine);

        baseAmount = baseAmount.abs();
        pMissionServiceLine.setReferenceAmount(baseAmount.multiply(lBaseFactor));
        if (lJudInvCalculated == null || !lJudInvCalculated) {
          // TODO pMissionServiceLine.setPriceIncludesVat(product.getPriceIncludesVat());
        }

        if (product.getSalePrice().compareTo(BigDecimal.ZERO) != 0) {
          pMissionServiceLine.setCalculationType(
              MissionServicePriceRepository.CALCULATIONTYPE_UNITPRICE);
          pMissionServiceLine =
              missionServiceLineValidate.validateUnitPrice(
                  pMissionServiceLine, product.getSalePrice().multiply(lPriceFactor));

          tempAccesBuffer = new AccesBuffer();
          tempAccesBuffer.setCode("Prix unitaire");
          tempAccesBuffer.setDecimal1(BigDecimal.ZERO);
          tempAccesBuffer.setDecimal2(BigDecimal.ZERO);
          tempAccesBuffer.setDecimal3(pMissionServiceLine.getReferenceAmount());
          tempAccesBuffer.setDecimal4(BigDecimal.ZERO);
          tempAccesBuffer.setDecimal5(pMissionServiceLine.getUnitPrice());
          tempAccesBuffer.setDecimal6(pMissionServiceLine.getUnitPrice());
          tempAccesBufferList.add(tempAccesBuffer);
          pMissionServiceLine.setServicePercent(BigDecimal.ZERO);
        } else if (product.getServicePercent().compareTo(BigDecimal.ZERO) != 0) {
          pMissionServiceLine.setCalculationType(
              MissionServicePriceRepository.CALCULATIONTYPE_SERVICE);
          pMissionServiceLine =
              missionServiceLineValidate.validateUnitPrice(
                  pMissionServiceLine,
                  pMissionServiceLine
                      .getReferenceAmount()
                      .multiply(product.getServicePercent().divide(new BigDecimal(100)))
                      .multiply(lPriceFactor));
          pMissionServiceLine.setServicePercent(product.getServicePercent().multiply(lPriceFactor));
          tempAccesBuffer = new AccesBuffer();
          tempAccesBuffer.setCode("Commission");
          tempAccesBuffer.setDecimal1(BigDecimal.ZERO);
          tempAccesBuffer.setDecimal2(BigDecimal.ZERO);
          tempAccesBuffer.setDecimal3(pMissionServiceLine.getReferenceAmount());
          tempAccesBuffer.setDecimal4(product.getServicePercent().multiply(lPriceFactor));
          tempAccesBuffer.setDecimal5(
              pMissionServiceLine
                  .getReferenceAmount()
                  .multiply(product.getServicePercent().divide(new BigDecimal(100)))
                  .multiply(lPriceFactor));
          tempAccesBuffer.setDecimal6(
              pMissionServiceLine
                  .getReferenceAmount()
                  .multiply(product.getServicePercent().divide(new BigDecimal(100)))
                  .multiply(lPriceFactor));
          tempAccesBufferList.add(tempAccesBuffer);
        } else if (product.getTariffScale() != null) {
          if (!lJudInvCalculated) {
            pMissionServiceLine.setCalculationType(
                MissionServicePriceRepository.CALCULATIONTYPE_COMMISSIONSCALE);
            tariffScale = product.getTariffScale();
            List<AccesBuffer> accesBuffersList =
                getTariffScaleAmount(pMissionServiceLine.getReferenceAmount(), tariffScale);
            BigDecimal lTempAmount = BigDecimal.ZERO;
            for (AccesBuffer lAccesBuffer : accesBuffersList) {
              if (accesBuffersList != null) {
                lTempAmount = lTempAmount.add(lAccesBuffer.getDecimal5());
              }
            }
            pMissionServiceLine =
                missionServiceLineValidate.validateUnitPrice(
                    pMissionServiceLine, lTempAmount.multiply(lPriceFactor));
            pMissionServiceLine.setServicePercent(BigDecimal.ZERO);
          }
        } else {
          pMissionServiceLine =
              missionServiceLineValidate.validateUnitPrice(pMissionServiceLine, BigDecimal.ZERO);
          pMissionServiceLine.setServicePercent(BigDecimal.ZERO);
        }

        pMissionServiceLine.setEstimatedValue(pEstimated);
        pMissionServiceLine = checkAmount(pMissionServiceLine, missionServicePrice, product);
        pMissionServiceLine = VATConvert(product, pMissionServiceLine);
      }
    }
    return pMissionServiceLine;
  }
  /*
  * PROCEDURE UPDATE_SERVICE_PRICE
  * "Price Includes VAT" := MissionServicePrice."Price Includes VAT"; //AP03
             CASE MissionServicePrice."Calculation Type" OF
               MissionServicePrice."Calculation Type"::UnitPrice : BEGIN
                "Calculation Type" := "Calculation Type"::UnitPrice;
                 VALIDATE("Unit Price",MissionServicePrice."Unit Price" * lPriceFactor);
                 TempAccesBuffer.DELETEALL;
                 TempAccesBuffer.INIT;
                 TempAccesBuffer.Code := Text8011400;
                 TempAccesBuffer."Decimal 1" := 0;
                 TempAccesBuffer."Decimal 2" := 0;
                 TempAccesBuffer."Decimal 3" := "Reference Amount";
                 TempAccesBuffer."Decimal 5" := "Unit Price";
                 TempAccesBuffer."Decimal 6" := "Unit Price";
                 TempAccesBuffer."Decimal 4" := 0;
                 TempAccesBuffer.INSERT;
                 "Service %" := 0;
               END;
               MissionServicePrice."Calculation Type"::"Service%" : BEGIN
                "Calculation Type" := "Calculation Type"::"Service%";
                 VALIDATE("Unit Price", "Reference Amount" * (MissionServicePrice."Service %" / 100) * lPriceFactor);
                 TempAccesBuffer.DELETEALL;
                 TempAccesBuffer.INIT;
                 TempAccesBuffer.Code := Text8011401;
                 TempAccesBuffer."Decimal 1" := 0;
                 TempAccesBuffer."Decimal 2" := 0;
                 TempAccesBuffer."Decimal 3" := "Reference Amount" ;
                 TempAccesBuffer."Decimal 4" := MissionServicePrice."Service %" * lPriceFactor;
                 TempAccesBuffer."Decimal 5" := "Reference Amount" * (MissionServicePrice."Service %" / 100) * lPriceFactor;
                 TempAccesBuffer."Decimal 6" := "Reference Amount" * (MissionServicePrice."Service %" / 100) * lPriceFactor;
                 TempAccesBuffer.INSERT;
                 "Service %" := MissionServicePrice."Service %";
               END;
               MissionServicePrice."Calculation Type"::"Commission Scale" : BEGIN
                "Calculation Type" := "Calculation Type"::"Commission Scale";
                 GetTariffScaleAmount("Reference Amount",MissionServicePrice."Tariff Scale",TempAccesBuffer);
                 lTempAmount := 0;
                 IF TempAccesBuffer.FINDFIRST THEN BEGIN
                   REPEAT
                     lTempAmount += TempAccesBuffer."Decimal 5";
                   UNTIL TempAccesBuffer.NEXT = 0;
                 END;
                 VALIDATE("Unit Price",lTempAmount * lPriceFactor);
                 "Service %" := 0;
               END;
             END;
             "Estimated Value" := pEstimated;
             CheckAmount(pMissionServiceLine, MissionServicePrice, Item); //AP15.ST
             VATConvert(Item, pMissionServiceLine); //AP16.ST
  */
  private MissionServiceLine applyTariff(MissionServiceLine pMissionServiceLine, Boolean pEstimated)
      throws AxelorException {
    pMissionServiceLine.setPriceIncludesVAT(missionServicePrice.getPriceIncludesVAT());
    switch (missionServicePrice.getCalculationType()) {
      case MissionServicePriceRepository.CALCULATIONTYPE_UNITPRICE:
        pMissionServiceLine.setCalculationType(
            MissionServicePriceRepository.CALCULATIONTYPE_UNITPRICE);
        pMissionServiceLine.setUnitPrice(missionServicePrice.getUnitPrice().multiply(lPriceFactor));
        tempAccesBuffer = new AccesBuffer();
        tempAccesBuffer.setCode("Prix unitaire");
        tempAccesBuffer.setDecimal1(BigDecimal.ZERO);
        tempAccesBuffer.setDecimal2(BigDecimal.ZERO);
        tempAccesBuffer.setDecimal3(pMissionServiceLine.getReferenceAmount());
        tempAccesBuffer.setDecimal5(pMissionServiceLine.getUnitPrice());
        tempAccesBuffer.setDecimal6(pMissionServiceLine.getUnitPrice());
        tempAccesBuffer.setDecimal4(BigDecimal.ZERO);
        tempAccesBufferList.add(tempAccesBuffer);
        pMissionServiceLine.setServicePercent(BigDecimal.ZERO);
        break;
      case MissionServicePriceRepository.CALCULATIONTYPE_SERVICE:
        pMissionServiceLine.setCalculationType(
            MissionServicePriceRepository.CALCULATIONTYPE_SERVICE);
        pMissionServiceLine.setUnitPrice(
            pMissionServiceLine
                .getReferenceAmount()
                .multiply(missionServicePrice.getServicePercent().divide(new BigDecimal(100)))
                .multiply(lPriceFactor));
        tempAccesBuffer = new AccesBuffer();
        tempAccesBuffer.setCode("Commission");
        tempAccesBuffer.setDecimal1(BigDecimal.ZERO);
        tempAccesBuffer.setDecimal2(BigDecimal.ZERO);
        tempAccesBuffer.setDecimal3(pMissionServiceLine.getReferenceAmount());
        tempAccesBuffer.setDecimal4(missionServicePrice.getServicePercent().multiply(lPriceFactor));
        tempAccesBuffer.setDecimal5(
            pMissionServiceLine
                .getReferenceAmount()
                .multiply(missionServicePrice.getServicePercent().divide(new BigDecimal(100)))
                .multiply(lPriceFactor));
        tempAccesBuffer.setDecimal6(
            pMissionServiceLine
                .getReferenceAmount()
                .multiply(missionServicePrice.getServicePercent().divide(new BigDecimal(100)))
                .multiply(lPriceFactor));
        tempAccesBufferList.add(tempAccesBuffer);
        pMissionServiceLine.setServicePercent(missionServicePrice.getServicePercent());
        break;
      case MissionServicePriceRepository.CALCULATIONTYPE_COMMISSIONSCALE:
        pMissionServiceLine.setCalculationType(
            MissionServicePriceRepository.CALCULATIONTYPE_COMMISSIONSCALE);
        List<AccesBuffer> tempAccesBufferList =
            getTariffScaleAmount(
                pMissionServiceLine.getReferenceAmount(), missionServicePrice.getTariffScale());

        BigDecimal lTempAmount = BigDecimal.ZERO;
        if (tempAccesBuffer != null) {
          for (AccesBuffer accesBuffer : tempAccesBufferList) {
            lTempAmount = lTempAmount.add(accesBuffer.getDecimal5());
          }
        }
        pMissionServiceLine.setUnitPrice(lTempAmount.multiply(lPriceFactor));
        pMissionServiceLine.setServicePercent(BigDecimal.ZERO);
        break;
    }
    pMissionServiceLine.setEstimatedValue(pEstimated);
    pMissionServiceLine = checkAmount(pMissionServiceLine, missionServicePrice, product);
    pMissionServiceLine = VATConvert(product, pMissionServiceLine);

    return pMissionServiceLine;
  }

  /*
  * IF NOT ReferenceAmountCalcOk THEN BEGIN
               GetLotAmount(pMissionServiceLine."Mission No.",pMissionServiceLine."Lot No.");
             END;
             IF IsApplyOnBidPrice THEN BEGIN
               CASE Item."Base Calc. After Auction" OF
                 Item."Base Calc. After Auction"::"Bid Price" : BaseAmount := BidReferenceAmount; //AP23.st
                 Item."Base Calc. After Auction"::"BidPrice Incl.VAT" : BaseAmount := BidPriceInclVAT; //AP23.st
                 Item."Base Calc. After Auction"::"Bid Price Excl. VAT" : BaseAmount := BidPrice;
                 Item."Base Calc. After Auction"::"Reserve Price" : BaseAmount := ReserveAmount;
               END; //CASE
             END ELSE BEGIN
               CASE Item."Base Calc. Before Auction" OF
                 Item."Base Calc. Before Auction"::"Reserve Price" : BaseAmount := ReserveAmount;
                 Item."Base Calc. Before Auction"::"Estimated Value Min" : BaseAmount := EstimatedMinAmount;
                 Item."Base Calc. Before Auction"::"Estimated Value Max" : BaseAmount := EstimatedMaxAmount;
                 Item."Base Calc. Before Auction"::MissionValue, Item."Base Calc. Before Auction"::MissionAdjust,
                 Item."Base Calc. Before Auction"::MissionInitValue :
                   BaseAmount := MissionValue; //AP14.ST
                 //<< AP19 isat.sf
                 Item."Base Calc. Before Auction"::"Popular Lines On Operating" : BEGIN
                   lJudInvCalculated := FALSE;
                   BaseAmount := 0 ;
                   "Service %" := 0;
                   MissionMgt.CalcJudInvService(pMissionServiceLine);
                   lJudInvCalculated := TRUE;
                 END;
                 Item."Base Calc. Before Auction"::"Popular Lines Mid Operating Realization" : BEGIN
                   lJudInvCalculated := FALSE;
                   BaseAmount := 0 ;
                   "Service %" := 0;
                   MissionMgt.CalcJudInvService(pMissionServiceLine);
                   lJudInvCalculated := TRUE;
                 END;
                 //>> AP19 isat.sf
                 //<< AP20 isat.sf
                 Item."Base Calc. Before Auction"::"Popular Lines On Realization" : BEGIN
                   lJudInvCalculated := FALSE;
                   BaseAmount := 0 ;
                   "Service %" := 0;
                   MissionMgt.CalcJudInvService(pMissionServiceLine);
                   lJudInvCalculated := TRUE;
                 END;
                 //>> AP20 isat.sf
                 //<< AP21 isat.sf
                 Item."Base Calc. Before Auction"::"Popular Lines Mid 2" : BEGIN
                   lJudInvCalculated := FALSE;
                   BaseAmount := 0 ;
                   "Service %" := 0;
                   MissionMgt.CalcJudInvService(pMissionServiceLine);
                   lJudInvCalculated := TRUE;
                 END;
                 Item."Base Calc. Before Auction"::"Popular Lines On Realization 2" : BEGIN
                   lJudInvCalculated := FALSE;
                   BaseAmount := 0 ;
                   "Service %" := 0;
                   MissionMgt.CalcJudInvService(pMissionServiceLine);
                   lJudInvCalculated := TRUE;
                 END;
                 //>> AP21 isat.sf
               END; //CASE
  */
  private MissionServiceLine calcWhenNoTariff(MissionServiceLine pMissionServiceLine)
      throws AxelorException {
    if (!referenceAmountCalcOk) {
      getLotAmount(pMissionServiceLine.getMissionNo(), pMissionServiceLine.getLotNo());
    }
    if (isApplyOnBidPrice) {
      switch (product.getBaseCalcAfterAuction()) {
        case ProductRepository.BASECALCAFTERAUCTION_BIDPRICE:
          baseAmount = bidReferenceAmount;
          break;
        case ProductRepository.BASECALCAFTERAUCTION_BIDPRICEINCLVAT:
          baseAmount = bidPriceInclVAT;
          break;
        case ProductRepository.BASECALCAFTERAUCTION_BIDPRICEEXCLVAT:
          baseAmount = bidPrice;
          break;
        case ProductRepository.BASECALCAFTERAUCTION_RESERVEPRICE:
          baseAmount = reserveAmount;
          break;
      }
    } else {
      switch (product.getBaseCalcBeforeAuction()) {
        case ProductRepository.BASECALCBEFOREAUCTION_RESERVEPRICE:
          baseAmount = reserveAmount;
          break;
        case ProductRepository.BASECALCBEFOREAUCTION_ESTIMATEDVALUEMIN:
          baseAmount = estimatedMinAmount;
          break;
        case ProductRepository.BASECALCBEFOREAUCTION_ESTIMATEDVALUEMAX:
          baseAmount = estimatedMaxAmount;
          break;
        case ProductRepository.BASECALCBEFOREAUCTION_MISSIONVALUE:
        case ProductRepository.BASECALCBEFOREAUCTION_MISSIONADJUST:
        case ProductRepository.BASECALCBEFOREAUCTION_MISSIONINITVALUE:
          baseAmount = missionValue;
          break;
        case ProductRepository.BASECALCBEFOREAUCTION_POPULARLINESONOPERATING:
        case ProductRepository.BASECALCBEFOREAUCTION_POPULARLINESMIDOPERATINGREALIZATION:
        case ProductRepository.BASECALCBEFOREAUCTION_POPULARLINESONREALIZATION:
        case ProductRepository.BASECALCBEFOREAUCTION_POPULARLINESMID2:
        case ProductRepository.BASECALCBEFOREAUCTION_POPULARLINESONREALIZATION2:
          lJudInvCalculated = false;
          baseAmount = BigDecimal.ZERO;
          pMissionServiceLine.setServicePercent(BigDecimal.ZERO);
          pMissionServiceLine = missionMgt.calcJudInvService(pMissionServiceLine);
          lJudInvCalculated = true;
          break;
      }
    }

    return pMissionServiceLine;
  }

  /*
  * LOCAL PROCEDURE CheckAmount@1100481006(VAR pMissServLine@1100481000 : Record 8011449;pServPrice@1100481001 : Record 8011469;pItem@1100481002 : Record 27);
   BEGIN
     //AP16.ST
     IF pMissServLine."Auction Bid" THEN
       EXIT;
     IF ((Item."Check Charge Amount Range" = Item."Check Charge Amount Range"::AfterBidding) AND (NOT IsApplyOnBidPrice)) OR
        ((Item."Check Charge Amount Range" = Item."Check Charge Amount Range"::BeforeBidding) AND IsApplyOnBidPrice) THEN
       EXIT;
     WITH pServPrice DO BEGIN
       IF ("Minimum Charge Amount" <> 0) OR ("Maximum Charge Amount" <> 0) THEN BEGIN
         // Controle que l'on a dans le min-max
         IF PriceRangeChange(pMissServLine."Unit Price", "Minimum Charge Amount", "Maximum Charge Amount") THEN BEGIN
           pMissServLine."Calculation Type" := pMissServLine."Calculation Type"::UnitPrice;
           pMissServLine."Service %" := 0;
           pMissServLine."Commission %" := 0;
           pMissServLine.VALIDATE("Unit Price");
         END;
         EXIT;
       END;
     END;
     WITH pItem DO BEGIN
       IF ("Minimum Charge Amount" <> 0) OR ("Maximum Charge Amount" <> 0) THEN BEGIN
         // Controle que l'on a dans le min-max
         IF PriceRangeChange(pMissServLine."Unit Price", "Minimum Charge Amount", "Maximum Charge Amount") THEN BEGIN
           pMissServLine."Calculation Type" := pMissServLine."Calculation Type"::UnitPrice;
           pMissServLine."Service %" := 0;
           pMissServLine."Commission %" := 0;
           pMissServLine.VALIDATE("Unit Price");
         END;
         EXIT;
       END;
     END;
   END;

  */
  private MissionServiceLine checkAmount(
      MissionServiceLine pMissionServiceLine,
      MissionServicePrice missionServicePrice2,
      Product product2)
      throws AxelorException {
    if (pMissionServiceLine.getAuctionBid()) return pMissionServiceLine;
    if (product2.getCheckChargeAmountRange()
            == ProductRepository.CHECKCHARGEAMOUNTRANGE_AFTERBIDDING
        && !isApplyOnBidPrice) return pMissionServiceLine;
    if (product2.getCheckChargeAmountRange()
            == ProductRepository.CHECKCHARGEAMOUNTRANGE_BEFOREBIDDING
        && isApplyOnBidPrice) return pMissionServiceLine;
    if (missionServicePrice2 != null
        && (missionServicePrice2.getMinimumChargeAmount().compareTo(BigDecimal.ZERO) != 0
            || missionServicePrice2.getMaximumChargeAmount().compareTo(BigDecimal.ZERO) != 0)) {

      if (priceRangeChange(
          pMissionServiceLine.getUnitPrice(),
          missionServicePrice2.getMinimumChargeAmount(),
          missionServicePrice2.getMaximumChargeAmount())) {
        pMissionServiceLine.setCalculationType(
            MissionServicePriceRepository.CALCULATIONTYPE_UNITPRICE);
        pMissionServiceLine.setServicePercent(BigDecimal.ZERO);
        pMissionServiceLine.setCommissionPercent(BigDecimal.ZERO);
        pMissionServiceLine =
            missionServiceLineValidate.validateUnitPrice(
                pMissionServiceLine, pMissionServiceLine.getUnitPrice());
      }
      return pMissionServiceLine;
    }

    if (product2.getMinimumChargeAmount().compareTo(BigDecimal.ZERO) != 0
        || product2.getMaximumChargeAmount().compareTo(BigDecimal.ZERO) != 0) {

      if (priceRangeChange(
          pMissionServiceLine.getUnitPrice(),
          product2.getMinimumChargeAmount(),
          product2.getMaximumChargeAmount())) {
        pMissionServiceLine.setCalculationType(
            MissionServicePriceRepository.CALCULATIONTYPE_UNITPRICE);
        pMissionServiceLine.setServicePercent(BigDecimal.ZERO);
        pMissionServiceLine.setCommissionPercent(BigDecimal.ZERO);
        pMissionServiceLine =
            missionServiceLineValidate.validateUnitPrice(
                pMissionServiceLine, pMissionServiceLine.getUnitPrice());
      }

      return pMissionServiceLine;
    }

    return pMissionServiceLine;
  }

  private boolean priceRangeChange(
      BigDecimal unitPrice, BigDecimal minimumChargeAmount, BigDecimal maximumChargeAmount) {
    return false;
  }

  /*
  * lPriceFactor := 1;
           lBaseFactor := 1;
           IF "Resale Right" THEN BEGIN
             lLotResaleRights.INIT;
             IF lLotResaleRights.GET(pMissionServiceLine."Lot No.",pMissionServiceLine."Organization Contact") THEN BEGIN
               lPriceFactor := lLotResaleRights."Organization Distribution"/100;
               lBaseFactor := lLotResaleRights."Auction Bid Percentage"/100;
             END;
           END;
  */
  private void findPriceAndBaseFactor(MissionServiceLine pMissionServiceLine) {
    lPriceFactor = BigDecimal.ONE;
    lBaseFactor = BigDecimal.ONE;
    if (pMissionServiceLine.getResaleRight()) {
      lLotResaleRights =
          Beans.get(LotResaleRightsRepository.class)
              .all()
              .filter(
                  "self.lotNo = ?1 AND self.organizationNo = ?2",
                  pMissionServiceLine.getLotNo(),
                  pMissionServiceLine.getOrganizationContact())
              .fetchOne();
      if (lLotResaleRights != null) {
        lPriceFactor =
            lLotResaleRights.getOrganizationDistribution().divide(BigDecimal.valueOf(100));
        lBaseFactor = lLotResaleRights.getAuctionBidPercentage().divide(BigDecimal.valueOf(100));
      }
    }
  }

  /*
  * IF NOT lUseJudicialTariff THEN BEGIN
             lTariffFound :=
               FindServicePrice(
                  "No.","Mission Template Code","Mission No.",
                  "Lot Price Group","Lot No.",
                  "Contact Price Group","Chargeable Contact No.",
                  GetPriceDate(pMissionServiceLine),
                  "Responsibility Center");
           END;
  */
  private Boolean findServicePrice(MissionServiceLine pMissionServiceLine) throws AxelorException {
    if (lUseJudicialTariff == null || !lUseJudicialTariff) {
      return findServicePrice(
          pMissionServiceLine.getProductNo(),
          pMissionServiceLine.getMissionTemplateCode(),
          pMissionServiceLine.getMissionNo(),
          pMissionServiceLine.getLotPriceGroup(),
          pMissionServiceLine.getLotNo(),
          pMissionServiceLine.getContactPriceGroup(),
          pMissionServiceLine.getChargeableContactNo(),
          getPriceDate(pMissionServiceLine),
          pMissionServiceLine.getResponsibilityCenter());
    }
    return false;
  }

  /*
  * LOCAL PROCEDURE FindServicePrice@1000000003(pItemNo@1000000007 : Code[20];pDocTemplate@1000000001 : Code[20];pDocNo@1000000002 : Code[20];pLotGroup@1000000003 : Code[10];pLotCode@1000000004 : Code[20];pContactGroup@1000000005 : Code[10];pContactCode@1000000006 : Code[20];pPriceDate@1000000009 : Date;pResponsibilityCenter@1000000011 : Code[10]) rFound : Boolean;
     VAR
       lMissionServicePrice@1000000008 : Record 8011469;
       lFound2@1100481000 : Boolean;
     BEGIN
       WITH lMissionServicePrice DO BEGIN
         SETCURRENTKEY("Item No.","Price Priority");
         ASCENDING(TRUE);
         SETRANGE("Item No.",pItemNo);
         SETRANGE("Starting Date",0D,pPriceDate);
         SETFILTER("Ending Date",'%1|>=%2',0D,pPriceDate);
         SETFILTER("Responsibility Center",'%1|=%2','',pResponsibilityCenter); //ap11 isat.zw
                                                                                 //replacer par 2 findfirst
         //SETRANGE("Responsibility Center", pResponsibilityCenter); //ap11 isat.zw

         IF pDocTemplate = '' THEN BEGIN
           SETFILTER("Mission Type",'<>%1',"Mission Type"::"Mission Template");
         END;
         SETFILTER("Mission Code",'=%1|=%2|=%3','',pDocTemplate,pDocNo); //AP05.ST
         IF pLotGroup = '' THEN BEGIN
           SETFILTER("Lot Type",'<>%1',"Lot Type"::"Lot Price Group");
         END;
         SETFILTER("Lot Code",'=%1|=%2|=%3','',pLotGroup,pLotCode); //AP05.ST
         IF pContactGroup = '' THEN BEGIN
           SETFILTER("Contact Type",'<>%1',"Contact Type"::"Contact Price Group");
         END;
         SETFILTER("Contact Code",'=%1|=%2|=%3','',pContactGroup,pContactCode); //AP05.ST

         GetLotAmount(pDocNo,pLotCode);

         SETFILTER("Minimum Base Amount",STRSUBSTNO('=0|<=%1',BaseAmount));

         IF FINDSET(FALSE,FALSE) THEN BEGIN
           REPEAT
             rFound := TestPrice(lMissionServicePrice,pDocTemplate,pDocNo,pLotGroup,pLotCode,pContactGroup,pContactCode);
             IF rFound THEN BEGIN
               SETRANGE("Price Priority", "Price Priority");
               IF FINDLAST THEN BEGIN
                 MissionServicePrice := lMissionServicePrice;
       //<<AP16.ST
                 IF "Responsibility Center" = '' THEN BEGIN
                   SETRANGE("Responsibility Center", pResponsibilityCenter);
                   IF FINDLAST THEN
                     MissionServicePrice := lMissionServicePrice;
                 END
       //>>AP16.ST
               END;
               EXIT; //AP18.ST évite le NEXT
             END;
           UNTIL (NEXT = 0) OR rFound;
         END;
       END;
     END;

  */
  private Boolean findServicePrice(
      Product productNo,
      MissionTemplate missionTemplateCode,
      MissionHeader missionNo,
      MissionLotPriceGroup lotPriceGroup,
      Lot lotNo,
      MissionContactPriceGroup contactPriceGroup,
      Partner contact,
      Date priceDate,
      TradingName responsibilityCenter)
      throws AxelorException {
    if (!findServicePriceWithRespCenter(
        productNo,
        missionTemplateCode,
        missionNo,
        lotPriceGroup,
        lotNo,
        contactPriceGroup,
        contact,
        priceDate,
        responsibilityCenter,
        true)) {
      findServicePriceWithRespCenter(
          productNo,
          missionTemplateCode,
          missionNo,
          lotPriceGroup,
          lotNo,
          contactPriceGroup,
          contact,
          priceDate,
          responsibilityCenter,
          false);
    }
    return missionServicePrice != null;
  }

  private Boolean findServicePriceWithRespCenter(
      Product productNo,
      MissionTemplate missionTemplateCode,
      MissionHeader missionNo,
      MissionLotPriceGroup lotPriceGroup,
      Lot lotNo,
      MissionContactPriceGroup contactPriceGroup,
      Partner contact,
      Date priceDate,
      TradingName responsibilityCenter,
      Boolean checkRespCenter)
      throws AxelorException {
    MissionServicePriceRepository missionServicePriceRepo =
        Beans.get(MissionServicePriceRepository.class);
    String filter = "self.itemNo = :product ";
    /*
        filter += " AND self.startingDate <= :priceDate";
        filter += " AND ( self.endingDate is null OR self.endingDate >= :priceDate )";
        if (checkRespCenter) {
          filter += " AND self.responsibilityCenter = :responsibilityCenter";
        } else {
          filter +=
              " AND ( self.responsibilityCenter is null OR self.responsibilityCenter = :responsibilityCenter)";
        }

        if (missionTemplateCode == null) {
          filter += " AND self.missionType <> :missionType ";
        }
        filter +=
            " AND ( (self.missionHeader is null AND self.missionTemplate is null ) OR self.missionHeader = :missionNo OR self.missionTemplate = :missionTemplateCode ) ";
        if (lotPriceGroup == null) {
          filter += " AND self.lotType <> :lotType ";
        }
        filter +=
            " AND ((self.lot is null AND self.lotPriceGroup is null) OR self.lot = :lotCode OR self.lotPriceGroup = :lotPriceGroup) ";
        if (contactPriceGroup == null) {
          filter += " AND self.contactType <> :contactType ";
        }
        filter +=
            " AND ((self.contact is null AND self.contactPriceGroup is null) OR self.contact = :contactCode OR self.contactPriceGroup = :contactPriceGroup) ";
        filter += " AND self.minimumBaseAmount <= :baseAmount ";
    */
    getLotAmount(missionNo, lotNo);
    List<MissionServicePrice> missionServicePriceList =
        missionServicePriceRepo
            .all()
            .filter(filter)
            .bind("product", productNo)
            .bind("priceDate", priceDate)
            .bind("priceDate", priceDate)
            .bind("missionNo", missionNo)
            .bind("responsibilityCenter", responsibilityCenter)
            .bind("missionType", MissionServicePriceRepository.MISSIONTYPE_MISSIONTEMPLATE)
            .bind("missionTemplateCode", missionTemplateCode)
            .bind("lotType", MissionServicePriceRepository.LOTTYPE_LOTPRICEGROUP)
            .bind("lotPriceGroup", lotPriceGroup)
            .bind("lotCode", lotNo)
            .bind("contactType", MissionServicePriceRepository.CONTACTTYPE_CONTACTPRICEGROUP)
            .bind("contactPriceGroup", contactPriceGroup)
            .bind("contactCode", contact)
            .bind("baseAmount", baseAmount)
            .order("pricePriority")
            .fetch();

    for (MissionServicePrice lMissionServicePrice : missionServicePriceList) {
      if (testPrice(
          lMissionServicePrice,
          missionTemplateCode,
          missionNo,
          lotPriceGroup,
          lotNo,
          contactPriceGroup,
          contact)) {
        missionServicePrice = lMissionServicePrice;

        return true;
      }
    }
    return false;
  }

  @Override
  public void findMissServPWithBaseAmount(
      MissionServiceLine pMissionServiceLine, Double pBaseAmount, Boolean pEstimated) {
    // TODO Auto-generated method stub

  }

  /*PROCEDURE TestPrice@1000000008(VAR pServiceMissionPrice@1000000008 : Record 8011469;DocTemplate@1000000006 : Code[20];DocNo@1000000005 : Code[20];LotGroup@1000000004 : Code[10];LotCode@1000000003 : Code[20];ContactGroup@1000000002 : Code[10];ContactCode@1000000001 : Code[20]) : Boolean;
  BEGIN
    WITH pServiceMissionPrice DO BEGIN
      CASE "Mission Type" OF
        "Mission Type"::"Mission Template" : BEGIN
          IF "Mission Code" <> DocTemplate THEN BEGIN
            EXIT(FALSE);
          END;
        END;
        "Mission Type"::"Mission No." : BEGIN
          IF "Mission Code" <> DocNo THEN BEGIN
            EXIT(FALSE);
          END;
        END;
      END; // CASE
      CASE "Lot Type" OF
        "Lot Type"::"Lot Price Group" : BEGIN
          IF "Lot Code" <> LotGroup THEN BEGIN
            EXIT(FALSE);
          END;
        END;
        "Lot Type"::Lot : BEGIN
          IF "Lot Code" <> LotCode THEN BEGIN
            EXIT(FALSE);
          END;
        END;
      END; // CASE
      CASE "Contact Type" OF
        "Contact Type"::"Contact Price Group" : BEGIN
          IF "Contact Code" <> ContactGroup THEN BEGIN
            EXIT(FALSE);
          END;
        END;
        "Contact Type"::Contact : BEGIN
          IF "Contact Code" <> ContactCode THEN BEGIN
            EXIT(FALSE);
          END;
        END;
      END; // CASE
    END; // WITH pServicePrice
    EXIT(TRUE);
  END; */
  @Override
  public Boolean testPrice(
      MissionServicePrice pServiceMissionPrice,
      MissionTemplate DocTemplate,
      MissionHeader DocNo,
      MissionLotPriceGroup LotGroup,
      Lot LotCode,
      MissionContactPriceGroup ContactGroup,
      Partner ContactCode) {
    switch (pServiceMissionPrice.getMissionType()) {
      case MissionServicePriceRepository.MISSIONTYPE_MISSIONTEMPLATE:
        if (!pServiceMissionPrice.getMissionTemplate().equals(DocTemplate)) {
          return false;
        }
        break;
      case MissionServicePriceRepository.MISSIONTYPE_MISSION:
        if (!pServiceMissionPrice.getMissionHeader().equals(DocNo)) {
          return false;
        }
        break;
    }
    switch (pServiceMissionPrice.getLotType()) {
      case MissionServicePriceRepository.LOTTYPE_LOTPRICEGROUP:
        if (!pServiceMissionPrice.getLotPriceGroup().equals(LotGroup)) {
          return false;
        }
        break;
      case MissionServicePriceRepository.LOTTYPE_LOT:
        if (!pServiceMissionPrice.getLot().equals(LotCode)) {
          return false;
        }
        break;
    }
    switch (pServiceMissionPrice.getContactType()) {
      case MissionServicePriceRepository.CONTACTTYPE_CONTACTPRICEGROUP:
        if (!pServiceMissionPrice.getContactPriceGroup().equals(ContactGroup)) {
          return false;
        }
        break;
      case MissionServicePriceRepository.CONTACTTYPE_CONTACT:
        if (!pServiceMissionPrice.getContact().equals(ContactCode)) {
          return false;
        }
        break;
    }
    return true;
  }

  /*
  *
   PROCEDURE GetLotAmount@1000000005(pMissionHeaderNo@1000000001 : Code[20];pLotNo@1000000000 : Code[20]);
   VAR
     lMissionLine@1000000002 : Record 8011403;
     lMissionServiceLine@1000000003 : Record 8011449;
   BEGIN
     ReserveAmount := 0;
     EstimatedMinAmount := 0;
     EstimatedMaxAmount := 0;
     BidReferenceAmount := 0; //AP23.ST
     BidPriceInclVAT := 0;
     BidPrice := 0;
     MissionValue := 0; //AP14.ST

     IF pLotNo = '' THEN BEGIN
     //<<AP14.ST
       IF Item."Base Calc. Before Auction" IN [Item."Base Calc. Before Auction"::MissionValue,
                                               Item."Base Calc. Before Auction"::MissionAdjust,
                                               Item."Base Calc. Before Auction"::MissionInitValue
                                               ]  THEN BEGIN
         GetMissionAmount(pMissionHeaderNo);
         BaseAmount := MissionValue;
       END;
     //>>AP14.ST
       EXIT;
     END;

     lMissionLine.SETCURRENTKEY("Mission No.",Type,"No.");
     lMissionLine.SETRANGE("Mission No.",pMissionHeaderNo);
     lMissionLine.SETRANGE(Type,lMissionLine.Type::Lot);
     lMissionLine.SETRANGE("No.",pLotNo);
     IF lMissionLine.FINDFIRST THEN BEGIN
       SearchBidLine(pMissionHeaderNo,pLotNo,lMissionServiceLine);
       lMissionLine.CALCFIELDS("Reserve Price","Net Reserve Price", "Estimate Value Min","Estimate Value Max");
       //<<ap17 isat.zw
       IF lMissionLine."Reserve Price" = 0 THEN
         ReserveAmount := lMissionLine."Net Reserve Price"
       ELSE
       //>>ap17 isat.zw
         ReserveAmount := lMissionLine."Reserve Price";
       EstimatedMinAmount := lMissionLine."Estimate Value Min";
       EstimatedMaxAmount := lMissionLine."Estimate Value Max";
       BidPriceInclVAT := lMissionServiceLine."Amount Incl. VAT";
       BidPrice := lMissionServiceLine.Amount;
       BidReferenceAmount := lMissionServiceLine."Reference Amount"; //AP23.st
     END;

     IF IsApplyOnBidPrice THEN BEGIN
       CASE Item."Base Calc. After Auction" OF
         Item."Base Calc. After Auction"::"Bid Price" : BaseAmount := BidReferenceAmount; //AP23.St
         Item."Base Calc. After Auction"::"Bid Price Excl. VAT" : BaseAmount := BidPrice;
         Item."Base Calc. After Auction"::"Reserve Price" : BaseAmount := ReserveAmount;
         Item."Base Calc. After Auction"::"BidPrice Incl.VAT" : BaseAmount := BidPriceInclVAT; //AP23.st
       END;
     END ELSE BEGIN
       CASE Item."Base Calc. Before Auction" OF
         Item."Base Calc. Before Auction"::"Reserve Price" : BaseAmount := ReserveAmount;
         Item."Base Calc. Before Auction"::"Estimated Value Min" : BaseAmount := EstimatedMinAmount;
         Item."Base Calc. Before Auction"::"Estimated Value Max" : BaseAmount := EstimatedMaxAmount;
         //<<AP14.ST
         Item."Base Calc. Before Auction"::MissionValue, Item."Base Calc. Before Auction"::MissionAdjust,
         Item."Base Calc. Before Auction"::MissionInitValue : BEGIN
           GetMissionAmount(pMissionHeaderNo);
           BaseAmount := MissionValue; //AP14.ST
         END;
         //>>AP14.ST
       END;
     END;
     BaseAmount := ABS(BaseAmount); //AP01.ISAT.ST
     ReferenceAmountCalcOk := TRUE;
   END;
  */
  @Override
  public void getLotAmount(MissionHeader pMissionHeaderNo, Lot pLotNo) throws AxelorException {
    reserveAmount = BigDecimal.ZERO;
    estimatedMinAmount = BigDecimal.ZERO;
    estimatedMaxAmount = BigDecimal.ZERO;
    bidReferenceAmount = BigDecimal.ZERO;
    bidPriceInclVAT = BigDecimal.ZERO;
    bidPrice = BigDecimal.ZERO;
    missionValue = BigDecimal.ZERO;
    if (pLotNo == null) {
      if (product.getBaseCalcBeforeAuction() == ProductRepository.BASECALCBEFOREAUCTION_MISSIONVALUE
          || product.getBaseCalcBeforeAuction()
              == ProductRepository.BASECALCBEFOREAUCTION_MISSIONADJUST
          || product.getBaseCalcBeforeAuction()
              == ProductRepository.BASECALCBEFOREAUCTION_MISSIONINITVALUE) {
        getMissionAmount(pMissionHeaderNo);
        baseAmount = missionValue;
      }
      return;
    }
    MissionLine lMissionLine =
        missionLineRepository
            .all()
            .filter(
                "self.missionNo = ?1 AND self.type = ?2 AND self.noLot = ?3",
                pMissionHeaderNo,
                MissionLineRepository.TYPE_LOT,
                pLotNo)
            .fetchOne();
    if (lMissionLine != null) {
      MissionServiceLine lMissionServiceLine = searchBidLine(pMissionHeaderNo, pLotNo);

      MissionLineValidate lMissionLineValidate = new MissionLineValidate();
      lMissionLine = lMissionLineValidate.calcFields(lMissionLine);

      if (lMissionLine.getReservePrice().compareTo(BigDecimal.ZERO) == 0) {
        reserveAmount = lMissionLine.getNetReservePrice();
      } else {
        reserveAmount = lMissionLine.getReservePrice();
      }
      estimatedMinAmount = lMissionLine.getEstimateMinValue();
      estimatedMaxAmount = lMissionLine.getEstimateMaxValue();

      if (lMissionServiceLine != null) {
        bidPriceInclVAT = lMissionServiceLine.getAmountInclVAT();
        bidPrice = lMissionServiceLine.getAmount();
        bidReferenceAmount = lMissionServiceLine.getReferenceAmount();
      }
    }
    if (isApplyOnBidPrice) {
      switch (product.getBaseCalcAfterAuction()) {
        case ProductRepository.BASECALCAFTERAUCTION_BIDPRICE:
          baseAmount = bidReferenceAmount;
          break;
        case ProductRepository.BASECALCAFTERAUCTION_BIDPRICEEXCLVAT:
          baseAmount = bidPrice;
          break;
        case ProductRepository.BASECALCAFTERAUCTION_RESERVEPRICE:
          baseAmount = reserveAmount;
          break;
        case ProductRepository.BASECALCAFTERAUCTION_BIDPRICEINCLVAT:
          baseAmount = bidPriceInclVAT;
          break;
      }
    } else {
      switch (product.getBaseCalcBeforeAuction()) {
        case ProductRepository.BASECALCBEFOREAUCTION_RESERVEPRICE:
          baseAmount = reserveAmount;
          break;
        case ProductRepository.BASECALCBEFOREAUCTION_ESTIMATEDVALUEMIN:
          baseAmount = estimatedMinAmount;
          break;
        case ProductRepository.BASECALCBEFOREAUCTION_ESTIMATEDVALUEMAX:
          baseAmount = estimatedMaxAmount;
          break;
        case ProductRepository.BASECALCBEFOREAUCTION_MISSIONVALUE:
        case ProductRepository.BASECALCBEFOREAUCTION_MISSIONADJUST:
        case ProductRepository.BASECALCBEFOREAUCTION_MISSIONINITVALUE:
          getMissionAmount(pMissionHeaderNo);
          baseAmount = missionValue;
          break;
      }
    }
    baseAmount = baseAmount.abs();
    referenceAmountCalcOk = true;
  }

  /*PROCEDURE GetMissionAmount@1100481008(pMissionHeaderNo@1100481000 : Code[20]);
  VAR
    lMissionHeader@1100481001 : Record 8011402;
  BEGIN
    //AP14.ST
    MissionValue := 0;
    IF pMissionHeaderNo = '' THEN
      EXIT;
    IF NOT (Item."Base Calc. Before Auction" IN [Item."Base Calc. Before Auction"::MissionValue,
                                                 Item."Base Calc. Before Auction"::MissionAdjust,
                                                 Item."Base Calc. Before Auction"::MissionInitValue]) THEN
     EXIT;
    WITH lMissionHeader DO BEGIN
      GET(pMissionHeaderNo);
      IF lMissionHeader."Process Type" = "Process Type"::Recovery THEN BEGIN
        CASE Item."Base Calc. Before Auction" OF
          Item."Base Calc. Before Auction"::MissionValue : BEGIN
            CALCFIELDS("Adjusted Debt");
            MissionValue := "Adjusted Debt";
          END;
          Item."Base Calc. Before Auction"::MissionAdjust : BEGIN
            CALCFIELDS("Adjustment Amount");
            MissionValue := "Adjustment Amount";
          END;
          Item."Base Calc. Before Auction"::MissionInitValue : BEGIN
            CALCFIELDS("Main Debt");
            MissionValue := "Main Debt";
          END;
        END; // CASE
      END
      ELSE
        ERROR(Text8011402);
    END; // WITH
  END; */
  @Override
  public void getMissionAmount(MissionHeader pMissionHeaderNo) throws AxelorException {
    MissionHeader lMissionHeader = pMissionHeaderNo;
    missionValue = BigDecimal.ZERO;
    if (pMissionHeaderNo == null) {
      return;
    }
    if (product.getBaseCalcBeforeAuction() != ProductRepository.BASECALCBEFOREAUCTION_MISSIONVALUE
        && product.getBaseCalcBeforeAuction()
            != ProductRepository.BASECALCBEFOREAUCTION_MISSIONADJUST
        && product.getBaseCalcBeforeAuction()
            != ProductRepository.BASECALCBEFOREAUCTION_MISSIONINITVALUE) {
      return;
    }
    if (lMissionHeader.getProcessType() == MissionHeaderRepository.PROCESSTYPE_RECOVERY) {
      MissionHeaderValidate lMissionHeaderValidate = new MissionHeaderValidate();
      switch (product.getBaseCalcBeforeAuction()) {
        case ProductRepository.BASECALCBEFOREAUCTION_MISSIONVALUE:
          lMissionHeader = lMissionHeaderValidate.calcFieldAdjustedDebt(lMissionHeader);
          missionValue = lMissionHeader.getAdjustedDebt();
          break;
        case ProductRepository.BASECALCBEFOREAUCTION_MISSIONADJUST:
          lMissionHeader = lMissionHeaderValidate.calcFieldAdjustedDebt(lMissionHeader);
          missionValue = lMissionHeader.getAdjustmentAmount();
          break;
        case ProductRepository.BASECALCBEFOREAUCTION_MISSIONINITVALUE:
          lMissionHeader = lMissionHeaderValidate.calcFieldMainDebt(lMissionHeader);
          missionValue = lMissionHeader.getMainDebt();
          break;
      }
    } else {
      throw new AxelorException(
          lMissionHeader,
          TraceBackRepository.CATEGORY_INCONSISTENCY,
          "Le montant mission n'est pas défini pour ce type de mission");
    }
  }

  @Override
  public void getTempServiceMissionPrice(MissionServiceLine pMissionServicePrice) {
    // TODO Auto-generated method stub

  }

  @Override
  public List<AccesBuffer> getTariffScaleAmount(BigDecimal pAmount, TariffScale pTariffScale) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<AccesBuffer> getTariffScaleDetail() {
    return tempAccesBufferList;
  }

  @Override
  public Double getBaseAMount() {
    // TODO Auto-generated method stub
    return null;
  }

  /*
  *
   PROCEDURE SearchBidLine@1000000007(pMissionHeaderNo@1000000002 : Code[20];pLotNo@1000000000 : Code[20];VAR pBidMissionServiceLine@1000000001 : Record 8011449);
   VAR
     lMissionServiceLine@1000000003 : Record 8011449;
   BEGIN
     //<<AP14.ST
     IF pLotNo = '' THEN BEGIN
       CLEAR(pBidMissionServiceLine);
       pBidMissionServiceLine.RESET;
       IsApplyOnBidPrice := FALSE;
     END;
     //>>AP14.ST
     lMissionServiceLine.SETCURRENTKEY("Lot No.");
     lMissionServiceLine.SETRANGE("Mission No.",pMissionHeaderNo);
     lMissionServiceLine.SETRANGE("Lot No.",pLotNo);
     lMissionServiceLine.SETRANGE(Chargeable,TRUE);
     lMissionServiceLine.SETRANGE("Auction Bid",TRUE);
     IF lMissionServiceLine.FINDFIRST THEN BEGIN
       pBidMissionServiceLine := lMissionServiceLine;
       IsApplyOnBidPrice := TRUE;
     END ELSE BEGIN
       CLEAR(pBidMissionServiceLine);
       pBidMissionServiceLine.RESET;
       IsApplyOnBidPrice := FALSE;
     END;
   END;

  */
  @Override
  public MissionServiceLine searchBidLine(MissionHeader pMissionHeaderNo, Lot pLotNo) {
    MissionServiceLine lMissionServiceLine = new MissionServiceLine();
    MissionServiceLineRepository missionServiceLineRepo =
        Beans.get(MissionServiceLineRepository.class);
    lMissionServiceLine =
        missionServiceLineRepo
            .all()
            .filter(
                "self.missionNo = ?1 AND self.lotNo = ?2 AND self.chargeable = ?3 AND self.auctionBid = ?4",
                pMissionHeaderNo,
                pLotNo,
                true,
                true)
            .fetchOne();
    if (lMissionServiceLine != null) {
      isApplyOnBidPrice = true;
      return lMissionServiceLine;
    } else {
      isApplyOnBidPrice = false;
      return null;
    }
  }

  @Override
  public void modifyPrice(MissionServiceLine pRec, MissionServiceLine pXRec, String pAction) {
    // TODO Auto-generated method stub

  }

  @Override
  public void applyNewPrice(MissionServiceLine pMissionServicePrice, String pAction) {
    // TODO Auto-generated method stub

  }

  @Override
  public void getUsedServPriceForMission(
      MissionHeader pMissionNo, MissionServiceLine pMissionServicePrice) {
    // TODO Auto-generated method stub

  }

  @Override
  public void markMissionServPrice(
      MissionServiceLine pMissionServiceLine, MissionServiceLine pMissionServicePrice) {
    // TODO Auto-generated method stub

  }

  @Override
  public void getServPriceByMissServLine(
      MissionServiceLine pMissionServiceLine, MissionServiceLine pMissionServicePrice) {
    // TODO Auto-generated method stub

  }

  @Override
  public void getServPriceByLotNoItemNo(
      Lot pLotNo, Product pItemNo, MissionServiceLine pMissionServicePrice) {
    // TODO Auto-generated method stub

  }

  @Override
  public Date getPriceDate(MissionServiceLine pMissServLine) {
    // TODO Auto-generated method stub
    return null;
  }

  /*
  * PROCEDURE VATConvert@1180113000(pItem@1180113000 : Record 27;VAR pMissServLine@1180113002 : Record 8011449);
   VAR
     lVATFactor@1180113004 : Decimal;
     lVATPostingSetup@1180113006 : TEMPORARY Record 325;
     lVATPostingSetup2@1180113007 : TEMPORARY Record 325;
     lAPPostingSetup@1180113008 : Codeunit 8011670;
     lTempMissServLine@1180113003 : TEMPORARY Record 8011449;
   BEGIN
     //AP16.ST
     IF (pItem."VAT Bus. Posting Gr. (Price)" = '') OR
        (NOT pMissServLine."Price Includes VAT") THEN
       EXIT;
     lTempMissServLine := pMissServLine;
     WITH lTempMissServLine DO BEGIN
       SetSkipChangeCheck(TRUE);
       CASE "Contact Imputation Type" OF
         "Contact Imputation Type"::Seller :
           IF "Seller VAT Bus. Posting Group" = pItem."VAT Bus. Posting Gr. (Price)" THEN
             EXIT
           ELSE
             VALIDATE("Seller VAT Bus. Posting Group", pItem."VAT Bus. Posting Gr. (Price)");
         "Contact Imputation Type"::Buyer :
           IF "Buyer VAT Bus. Posting Group" = pItem."VAT Bus. Posting Gr. (Price)" THEN
             EXIT
           ELSE
             VALIDATE("Buyer VAT Bus. Posting Group", pItem."VAT Bus. Posting Gr. (Price)");
         ELSE
           EXIT;
       END; //CASE
     END;

     IF pMissServLine."VAT Calculation Type" = pMissServLine."VAT Calculation Type"::"Reverse Charge VAT" THEN
       lVATFactor := 1 / (1 + lTempMissServLine."VAT %" / 100)
     ELSE
       lVATFactor := (100 + pMissServLine."VAT %") / (100 + lTempMissServLine."VAT %");

     IF lVATFactor = 1 THEN
       EXIT;

     // Modification du prix unitaire ou  %
     WITH pMissServLine DO BEGIN
       CASE "Calculation Type" OF
         "Calculation Type"::UnitPrice, "Calculation Type"::"Commission Scale" :
           VALIDATE("Unit Price", "Unit Price" * lVATFactor);
         "Calculation Type"::"Service%" : BEGIN
           "Service %" := "Service %" * lVATFactor;
           VALIDATE("Unit Price", "Unit Price" * lVATFactor);
         END;
       END; // case
     END; //WITH
   END;
  */
  @Override
  public MissionServiceLine VATConvert(Product pItem, MissionServiceLine pMissServLine) {
    // TODO à finir

    return pMissServLine;
  }

  @Override
  public String getSellerComByAuctionLine(
      MissionServiceLine pAuctionLine, String pCalcType, Double pService, Double pUnitPrice) {
    // TODO Auto-generated method stub
    return null;
  }
}
