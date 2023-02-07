package com.axelor.apps.openauctionbase.service;

import com.axelor.apps.base.db.Partner;
import com.axelor.apps.openauction.db.ActivityHeader;
import com.axelor.apps.openauction.db.ActivityLine;
import com.axelor.apps.openauction.db.AuctionHeader;
import com.axelor.apps.openauction.db.Lot;
import com.axelor.apps.openauction.db.LotTemplate;
import com.axelor.apps.openauction.db.MissionActivityLine;
import com.axelor.apps.openauction.db.MissionHeader;
import com.axelor.apps.openauction.db.MissionLine;
import com.axelor.apps.openauction.db.MissionServiceLine;
import com.axelor.apps.openauction.db.ServiceTemplate;
import com.axelor.apps.openauction.db.ServiceTemplateLine;
import com.axelor.apps.openauction.db.repo.ActivityHeaderRepository;
import com.axelor.apps.openauction.db.repo.ActivityLineRepository;
import com.axelor.apps.openauction.db.repo.MissionActivityLineRepository;
import com.axelor.apps.openauction.db.repo.MissionServiceLineRepository;
import com.axelor.apps.openauctionbase.repository.MissionServiceLineRepositoryExt;
import com.axelor.apps.openauctionbase.validate.MissionServiceLineValidate;
import com.axelor.exception.AxelorException;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActivityManagementImpl implements ActivityManagement {
  MissionServiceLineRepositoryExt missionServiceLineRepository;
  MissionActivityLineRepository missionActivityLineRepository;
  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Inject
  public ActivityManagementImpl(
      MissionServiceLineRepositoryExt missionServiceLineRepository,
      MissionActivityLineRepository missionActivityLineRepository) {
    this.missionServiceLineRepository = missionServiceLineRepository;
    this.missionActivityLineRepository = missionActivityLineRepository;
  }

  @Override
  public void createActivityLines2(
      ActivityHeader pActivityHeader,
      AuctionHeader pAuctionHeader,
      MissionHeader pMissionHeader,
      Lot pLotNo,
      LotTemplate pLotTemplate,
      Boolean pIsAuction,
      Boolean pIsActionOnly,
      Integer pTransactionLineNo)
      throws AxelorException {

    Boolean lLineToTreat = false;
    Partner lcontact = pMissionHeader != null ? pMissionHeader.getMasterContactNo() : null;
    ;

    // TODO pActivityHeader.TestUsage(lDate);

    for (ActivityLine line : pActivityHeader.getActivityLineList()) {
      lLineToTreat = false;
      if (line.getTodoCondition() == null || !line.getTodoCondition().equals("")) {
        // log.debug(pActivityHeader.getApplicableOn());
        if (pActivityHeader
            .getApplicableOn()
            .equals(ActivityHeaderRepository.APPLICABLEON_HEADER)) {
          lLineToTreat = true;
        }
        if (pActivityHeader.getApplicableOn().equals(ActivityHeaderRepository.APPLICABLEON_LINE)) {
          lLineToTreat =
              line.getLotTemplateFilter() == null
                  || line.getLotTemplateFilter().equals(pLotTemplate);
        }
        if (line.getToDoApplicableTo().equals(ActivityLineRepository.TODOAPPLICABLETO_SELLER)) {
          lcontact = pMissionHeader.getMasterContactNo();
        } else {
          lcontact = null;
        }

        if (lLineToTreat) {
          if (line.getServiceTemplateCode() != null)
            createMissionService(
                line,
                pAuctionHeader,
                pMissionHeader,
                pLotNo,
                lcontact,
                pIsAuction,
                pTransactionLineNo);
          // TODO gestion Interaction + Todo
          /*
          *
             IF (lActivityLine."Interaction Template Code" <> '') AND (pActionOnly = FALSE) THEN BEGIN
               CreateInteraction(lActivityLine,pMissionNo,pAuctionNo,pLotNo,pTransactionLineNo); //AP09
             END;
             IF lActivityLine."Todo Template Code" <> '' THEN BEGIN
               CreateTodo(lActivityLine,pMissionNo,pAuctionNo,pLotNo,lContactNo,lSalespersonCode,pTransactionLineNo); //AP09
             END;
          */
        }
      }
    }
  }

  @Override
  @Transactional
  public void removeActivityLines(
      ActivityHeader pActivityHeader,
      AuctionHeader pAuctionHeader,
      MissionHeader pMissionHeader,
      Lot pLotNo,
      Integer pTransactionMineNo) {
    // TODO Auto-generated method stub
  }

  /*
  @Override
  @Transactional
  public void createTodo(
      ActivityHeader pActivityHeader,
      AuctionHeader pAuctionHeader,
      MissionHeader pMissionHeader,
      Lot pLotNo,
      Integer pTransactionMineNo,
      Partner pContact,
      Partner pSalesPerson) {
    // TODO Auto-generated method stub

  }*/

  @Override
  public void createActivityLineFromMission(
      ActivityHeader pActivityHeader,
      MissionHeader pMissionHeader,
      MissionLine pMissionLine,
      Boolean pActionOnly)
      throws AxelorException {
    Lot lLot = pMissionLine.getNoLot();
    LotTemplate lLotTemplate = lLot == null ? null : lLot.getLotTemplateCode();
    createActivityLines2(
        pActivityHeader,
        null,
        pMissionHeader,
        lLot,
        lLotTemplate,
        false,
        pActionOnly,
        pMissionLine.getLineNo());
  }

  /*
       * LOCAL PROCEDURE CreateMissionService@1000000019(pActivityLine@1000000000 : Record 8011406;pMissionNo@1000000014 : Code[20];pAuctionNo@1000000013 : Code[20];pLotNo@1000000006 : Code[20];pContactNo@1000000009 : Code[20];pAuctionAct@1100481000 : Boolean;pTransactionLineNo@1100481001 : Integer);
      VAR
        lMissionSetup@1000000007 : Record 8011420;
        lMissionServiceTemplate@1000000001 : Record 8011409;
        lMissionServiceTemplateLine@1000000002 : Record 8011410;
        lMissionServiceLine@1000000004 : Record 8011449;
        lMissionActivityLine@1000000003 : Record 8011457;
        lValidateOK@1000000008 : Boolean;
        NameDataTypeSubtypeLength@1000000005 : Integer;
      BEGIN

  IF pActivityLine."Service Template Code" = '' THEN BEGIN
    EXIT;
  END;

  //InitGlobalServiceEntryNo(gLastServiceEntryNo); //ap20 isat.zw

  lValidateOK := pActivityLine."Todo Template Code" = '';
  lMissionServiceTemplateLine.RESET;
  lMissionServiceTemplateLine.SETRANGE("Service Template Code",pActivityLine."Service Template Code");
  IF lMissionServiceTemplateLine.FIND('-') THEN BEGIN
    REPEAT
      //gLastServiceEntryNo += 1;
      WITH lMissionServiceLine DO BEGIN
        INIT;
        //"Entry No." := gLastServiceEntryNo; //ap20 isat.zw
        "Entry No." := 0; //ap20 isat.zw
        "Document No." := ''; //ap20 isat.zw

  //<<ISAT.ST avant validation du lot
        IF pAuctionAct THEN
          VALIDATE("Transaction Type", "Transaction Type"::Auction)
        ELSE
          VALIDATE("Transaction Type", "Transaction Type"::Mission);
  //>>ISAT.ST
        VALIDATE("Mission No.", pMissionNo);
        VALIDATE("Auction No.",pAuctionNo);
        VALIDATE("Lot No.",pLotNo);
        VALIDATE("Transaction Line No.",pTransactionLineNo);//AP09
        VALIDATE(Type,lMissionServiceTemplateLine.Type);
        VALIDATE("No.",lMissionServiceTemplateLine."No.");
        VALIDATE("Chargeable Contact No.",pContactNo);
        VALIDATE("Contact Imputation Type"); //AP17.ST
        IF lMissionServiceTemplateLine.Description <> '' THEN BEGIN
          Description := lMissionServiceTemplateLine.Description;
        END;
        IF lMissionServiceTemplateLine."Unit of Measure Code" <> '' THEN BEGIN
          "Unit of Measure Code" := lMissionServiceTemplateLine."Unit of Measure Code";
        END;
        "Mis. Service Template Code" := lMissionServiceTemplateLine."Service Template Code";
        VALIDATE(Quantity,1);
        "Accept To Invoice" := TRUE;
        "Activity Header" := pActivityLine."Activity Code";
        "Activity Line" := pActivityLine."Line No.";
        "Web Site Code" := lMissionServiceTemplateLine."Web Site Code"; // AP22

        UpdatePrice;
        INSERT(TRUE);
      END;

      WITH lMissionActivityLine DO BEGIN
        INIT;
        "Entry No." := 0; //ap20 isat.zw
        "Document No." := ''; //ap20 isat.zw

        "Mission No." := pMissionNo;
        "Auction No." := pAuctionNo;
        "Lot No." := pLotNo;
        "Transaction Line No." := pTransactionLineNo;//AP09
        "Activity Code" := pActivityLine."Activity Code";
        "Activity Line No." := pActivityLine."Line No.";
        Type := Type::Service;
        "To-do No." := '';
        "Mission Service Entry No." := lMissionServiceLine."Entry No.";
        "Mission Service Doc. No." := lMissionServiceLine."Document No."; //ap20 isat.zw
        "Interaction Template Code" := '';
        "Previous Mission No." := '';
        INSERT(TRUE);
      END;
    UNTIL lMissionServiceTemplateLine.NEXT = 0;
  END;
       */

  private void createMissionService(
      ActivityLine pActivityLine,
      AuctionHeader pAuctionHeader,
      MissionHeader pMissionHeader,
      Lot pLot,
      Partner pContact,
      Boolean pAuctionAct,
      Integer pTransactionLineNo)
      throws AxelorException {
    if (pActivityLine.getServiceTemplateCode() == null) return;
    ServiceTemplate missionServiceTemplate;
    MissionServiceLineValidate missionServiceLineValidate = new MissionServiceLineValidate();
    missionServiceTemplate = pActivityLine.getServiceTemplateCode();
    for (ServiceTemplateLine lMissionServiceTemplateLine :
        missionServiceTemplate.getTemplateLineList()) {
      MissionServiceLine lMissionServiceLine = new MissionServiceLine();

      lMissionServiceLine.setEntryNo(0);
      lMissionServiceLine.setDocumentNo(null);

      if (pAuctionAct)
        lMissionServiceLine =
            missionServiceLineValidate.validateTransactionType(
                lMissionServiceLine, MissionServiceLineRepository.TRANSACTIONTYPE_VENTE);
      else
        lMissionServiceLine =
            missionServiceLineValidate.validateTransactionType(
                lMissionServiceLine, MissionServiceLineRepository.TRANSACTIONTYPE_MISSION);
      lMissionServiceLine =
          missionServiceLineValidate.validateMissionNo(lMissionServiceLine, pMissionHeader);
      lMissionServiceLine =
          missionServiceLineValidate.validateAuctionNo(lMissionServiceLine, pAuctionHeader);
      lMissionServiceLine = missionServiceLineValidate.validateLotNo(lMissionServiceLine, pLot);
      lMissionServiceLine =
          missionServiceLineValidate.validateTransactionLineNo(
              lMissionServiceLine, pTransactionLineNo);

      lMissionServiceLine =
          missionServiceLineValidate.validateType(
              lMissionServiceLine, lMissionServiceTemplateLine.getType());
      lMissionServiceLine =
          missionServiceLineValidate.validateProductNo(
              lMissionServiceLine, lMissionServiceTemplateLine.getProduct());

      lMissionServiceLine =
          missionServiceLineValidate.validateChargeableContactNo(lMissionServiceLine, pContact);
      lMissionServiceLine =
          missionServiceLineValidate.validateContactImputationType(
              lMissionServiceLine, lMissionServiceLine.getContactImputationType());

      if (lMissionServiceTemplateLine.getDescription() != null) {
        lMissionServiceLine.setDescription(lMissionServiceTemplateLine.getDescription());
      }
      if (lMissionServiceTemplateLine.getUnitOfMeasure() != null) {
        lMissionServiceLine.setUnitofMeasureCode(lMissionServiceTemplateLine.getUnitOfMeasure());
      }

      lMissionServiceLine.setMisServiceTemplateCode(
          lMissionServiceTemplateLine.getServiceTemplateCode());
      lMissionServiceLine =
          missionServiceLineValidate.validateQuantity(lMissionServiceLine, BigDecimal.ONE);
      lMissionServiceLine.setAcceptToInvoice(true);
      lMissionServiceLine.setWebSite(lMissionServiceTemplateLine.getWebSite());
      lMissionServiceLine.setActivityHeader(pActivityLine.getActivityCode());
      lMissionServiceLine.setActivityLine(pActivityLine.getLineNo());

      lMissionServiceLine = missionServiceLineValidate.updatePrice(lMissionServiceLine);
      lMissionServiceLine = missionServiceLineValidate.onInsert(lMissionServiceLine);
      missionServiceLineRepository.save(lMissionServiceLine);

      MissionActivityLine lMissionActivityLine = new MissionActivityLine();
      lMissionActivityLine.setEntryNo(0);

      lMissionActivityLine.setMissionNo(pMissionHeader);
      lMissionActivityLine.setAuctionNo(pAuctionHeader);
      lMissionActivityLine.setLotNo(pLot);
      lMissionActivityLine.setTransactionLineNo(pTransactionLineNo);
      lMissionActivityLine.setActivityCode(pActivityLine.getActivityCode());
      lMissionActivityLine.setActivityLineNo(pActivityLine.getLineNo());
      lMissionActivityLine.setType(MissionActivityLineRepository.TYPE_SERVICE);
      // TODO Interaction
      // lMissionActivityLine.setTodoNo(null);
      // lMissionActivityLine.setInteractionTemplateCode(null);
      lMissionActivityLine.setMissionServiceEntryNo(lMissionServiceLine);

      lMissionActivityLine.setPreviousMissionNo(null);
      // TODO eventuellement revoir le insert(true)
      missionActivityLineRepository.save(lMissionActivityLine);
    }
  }

  // TODO onValidate

}
