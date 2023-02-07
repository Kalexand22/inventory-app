package com.axelor.apps.openauctionbase.service;

import com.axelor.apps.base.db.Partner;
import com.axelor.apps.openauction.db.ActivityHeader;
import com.axelor.apps.openauction.db.LawyerBusiness;
import com.axelor.apps.openauction.db.MissionHeader;
import com.axelor.apps.openauction.db.MissionLine;
import com.axelor.apps.openauction.db.MissionTemplate;
import com.axelor.apps.openauction.db.repo.LawyerBusinessRepository;
import com.axelor.apps.openauction.db.repo.MissionHeaderRepository;
import com.axelor.apps.openauctionbase.validate.MissionHeaderValidate;
import com.axelor.apps.openauctiontemplate.service.TransferFields;
import com.axelor.exception.AxelorException;
import com.axelor.inject.Beans;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

public class MissionTemplateManagementImpl implements MissionTemplateManagement {

  MissionHeaderRepository missionHeaderRepository;
  Boolean HideTemplate;
  Boolean AuctionFromInv;
  Boolean SkipActivity;

  @Inject
  public MissionTemplateManagementImpl(MissionHeaderRepository missionHeaderRepository) {
    this.missionHeaderRepository = missionHeaderRepository;
    SkipActivity = false;
  }

  @Override
  @Transactional
  public MissionHeader createMissionFromMission(
      MissionHeader pMissionHeader,
      MissionTemplate pMissionTemplate,
      Boolean pJudicialFilter,
      String pLawyerBusNo)
      throws AxelorException {
    MissionHeaderValidate missionHeaderValidate = Beans.get(MissionHeaderValidate.class);

    /*
    * IF lMissionTemplate.GET(lMissionTemplateCode) THEN BEGIN
       lMissionHeader.INIT;
       lMissionHeader.VALIDATE("Mission Template Code",lMissionTemplateCode);
       lMissionHeader.TRANSFERFIELDS(lMissionTemplate);
       IF pMissionHeader."No." <> '' THEN
         lMissionHeader."No." := pMissionHeader."No."; // AP14 isat.sf
       lMissionHeader."Mission Type" := lMissionType;
       lMissionHeader."Activity Code To Header" := lMissionTemplate."Activity Code To Header";
       lMissionHeader.INSERT(TRUE);



    */

    if (pMissionTemplate != null) {
      pMissionHeader.setMissionTemplateCode(pMissionTemplate);
      pMissionHeader =
          (MissionHeader) TransferFields.transferFields(pMissionTemplate, pMissionHeader);
      if (pMissionHeader.getNo() != null) {
        pMissionHeader.setNo(pMissionHeader.getNo());
      }
      pMissionHeader.setMissionType(pMissionHeader.getMissionType());
      pMissionHeader.setActivityCodeToHeader(pMissionTemplate.getActivityCodeToHeader());
      // missionHeaderRepository.save(lmissionHeader);

      /*
      * lMissionHeader.VALIDATE("Auctioneer Code",lAuctioneerCode);

       IF lMissionTitle <> '' THEN BEGIN
         lMissionHeader.Description := lMissionTitle;
       END ELSE BEGIN
         lMissionHeader.Description := COPYSTR(lMissionTemplate.Code + ' '
                                       + FinNav.GetContactName(lMissionHeader."Master Contact No."),1,49);
       END;

       IF (lMissionTemplate."Process Type" = lMissionTemplate."Process Type"::Recovery)
          AND (pMissionHeader.Description <> '') THEN BEGIN
         lMissionHeader.Description := pMissionHeader.Description; // AP09 isat.sf
       END;

       lMissionHeader.VALIDATE("Mission Manager Code",lMissionManagerCode);
       IF lActivityCodeToHeader <> '' THEN    // isat.Sf 121109
         lMissionHeader."Activity Code To Header" := lActivityCodeToHeader;
       IF lActivityCodeToLine <> '' THEN   // isat.Sf 121109
         lMissionHeader."Activity Code To Lines" := lActivityCodeToLine;
       lMissionHeader.VALIDATE("Previous Mission No.",lOriginMissionNo);
       lMissionHeader.VALIDATE("Responsibility Center",lMissionResponsabilityCenter);
      */
      pMissionHeader =
          missionHeaderValidate.validateAuctioneerCode(
              pMissionHeader, pMissionHeader.getAuctionnerCode());
      String lMissionTitle = pMissionHeader.getDescription();
      if (lMissionTitle != null && !lMissionTitle.isEmpty()) {
        pMissionHeader.setDescription(lMissionTitle);
      } else {
        pMissionHeader.setDescription(
            pMissionTemplate.getCode() + " " + pMissionHeader.getMasterContactNo().getFullName());
      }
      pMissionHeader =
          missionHeaderValidate.validateResponsibilityCenter(
              pMissionHeader, pMissionHeader.getResponsibilityCenter());

      /*
      * IF lLawyerBus."No." <> '' THEN BEGIN

           //<<  AP11 isat.Sf
           CASE lLawyerBus."Process Type" OF
             lLawyerBus."Process Type"::"Procedure RJ" : BEGIN
               lMissionHeader.VALIDATE("Master Contact No.",lLawyerBus."Debtor Contact No.");
             END;
             //<< AP19 isat.sf
             lLawyerBus."Process Type"::Backup : BEGIN
               lMissionHeader.VALIDATE("Master Contact No.",lLawyerBus."Debtor Contact No.");
             END;
             //>> AP19 isat.sf
             lLawyerBus."Process Type"::"Procedure LJ" : BEGIN
               lMissionHeader.VALIDATE("Master Contact No.",lMasterContactNo);
             END;
             lLawyerBus."Process Type"::Recovery : BEGIN
               lMissionHeader.VALIDATE(Manager,lLawyerBus.Manager);
               lMissionHeader.VALIDATE("Main Interlocutor No.",lLawyerBus."Main Interlocutor No.");

               lMissionHeader.VALIDATE("Master Contact No.",lLawyerBus."Debtor Contact No.");
               lMissionHeader.VALIDATE("Correspondent Contact No.",lLawyerBus."Contact No.");

               lMissionHeader.VALIDATE("Debtor Lawyer",lLawyerBus."Debtor Lawyer");
               lMissionHeader.VALIDATE("Creditors Lawyer",lLawyerBus."Creditors Lawyer");
               lMissionHeader.VALIDATE("Representative of Creditors",lLawyerBus."Representative of Creditors");
             END;
             ELSE BEGIN
               lMissionHeader.VALIDATE("Master Contact No.",lMasterContactNo);
             END;
           END;
      */
      if (pLawyerBusNo != null && !pLawyerBusNo.isEmpty()) {
        LawyerBusiness lLawyerBus =
            Beans.get(LawyerBusinessRepository.class)
                .all()
                .filter("self.no = ?", pLawyerBusNo)
                .fetchOne();
        if (lLawyerBus != null) {
          // TODO REVOIR LE CODE
          switch (lLawyerBus.getProcessType()) {
            case 1:
              pMissionHeader =
                  missionHeaderValidate.validateMasterContactNo(
                      pMissionHeader, lLawyerBus.getDebtorContactNo());
              break;
            case 8:
              pMissionHeader =
                  missionHeaderValidate.validateMasterContactNo(
                      pMissionHeader, lLawyerBus.getDebtorContactNo());
              break;
            case 2:
              pMissionHeader =
                  missionHeaderValidate.validateMasterContactNo(
                      pMissionHeader, pMissionHeader.getMasterContactNo());
              break;
            case 3:
              pMissionHeader =
                  missionHeaderValidate.validateManager(pMissionHeader, lLawyerBus.getManager());
              // lmissionHeader = missionHeaderValidate.validateMainInterlocutorNo(lmissionHeader,
              // lLawyerBus.getMainInterlocutorNo());
              pMissionHeader =
                  missionHeaderValidate.validateMasterContactNo(
                      pMissionHeader, lLawyerBus.getDebtorContactNo());
              pMissionHeader.setCorrespondentContactNo(lLawyerBus.getContactNo());
              pMissionHeader.setDebtorLawyer(lLawyerBus.getDebtorLawyer());
              pMissionHeader.setCreditorsLawyer(lLawyerBus.getCreditorsLawyer());
              pMissionHeader.setRepresentativeofCreditors(
                  lLawyerBus.getRepresentativeofCreditors());
              break;
            default:
              pMissionHeader =
                  missionHeaderValidate.validateMasterContactNo(
                      pMissionHeader, pMissionHeader.getMasterContactNo());
              break;
          }
        }
      } else {
        pMissionHeader =
            missionHeaderValidate.validateMasterContactNo(
                pMissionHeader, pMissionHeader.getMasterContactNo());
      }

      // TODO Méthode à compléter?
    }

    pMissionHeader = Beans.get(MissionHeaderValidate.class).onInsert(pMissionHeader);
    missionHeaderRepository.save(pMissionHeader);

    if (!SkipActivity) {
      this.createActivity2(pMissionHeader, pMissionHeader.getActivityCodeToHeader());
    }

    return pMissionHeader;
  }

  @Override
  @Transactional
  public Boolean createMissionFromContact(Partner pContact) {
    return null;
  }

  @Override
  public void getCustomerNo() {}

  @Override
  public void setAuctionFromInv(Boolean pTrue) {
    AuctionFromInv = pTrue;
  }

  @Override
  public void setSkipActivityCreation(Boolean pSkipActivity) {
    SkipActivity = pSkipActivity;
  }

  @Override
  @Transactional
  public void createActivity2(MissionHeader pMissionHeader, ActivityHeader pActivityCodeToHeader)
      throws AxelorException {
    MissionLine lMissionLine = new MissionLine();
    ActivityManagement activityManagement = Beans.get(ActivityManagement.class);
    if (pActivityCodeToHeader != null) {
      activityManagement.createActivityLineFromMission(
          pActivityCodeToHeader, pMissionHeader, lMissionLine, false);
    } else {
      if (pMissionHeader.getActivityCodeToHeader() != null) {
        activityManagement.createActivityLineFromMission(
            pMissionHeader.getActivityCodeToHeader(), pMissionHeader, lMissionLine, false);
      }
    }
  }
}
