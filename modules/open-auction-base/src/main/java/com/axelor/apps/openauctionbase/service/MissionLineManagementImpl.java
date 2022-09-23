package com.axelor.apps.openauctionbase.service;

import java.math.BigDecimal;

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

public class MissionLineManagementImpl implements MissionLineManagement {

    Integer missionLineNo;
    MissionLineRepository missionLineRepo;

    @Inject
    public MissionLineManagementImpl(MissionLineRepository missionLineRepo) {
        this.missionLineRepo = missionLineRepo;
    }
 
    @Override
    public void SendLotToAuction(MissionLine pMissionLine) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Integer LotInsertMission(MissionHeader pMissionHeader, Lot pLot, MissionLine pMissionLine,
            LotQuickInputJournal pLotQuickInputJournal) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void DuplicateMissionLine(MissionLine pMissionLine, Boolean pSameLotNo) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Boolean IsAffectedLotInMission(MissionHeader pMissionHeader, Lot pLot) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Boolean DeleteMissionLine(MissionLine pMissionLine) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void SortMissionLine(MissionHeader pMission) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public MissionLine CreateMissionLine(MissionHeader pMissionHeader, Lot pLot,
            LotQuickInputJournal pLotQuickInputJournal) throws AxelorException{
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
        if (pMissionHeader.getMissionStatus().equals(MissionHeaderRepository.MISSIONSTATUS_SELECT_CANCELED )
            || pMissionHeader.getMissionStatus().equals(MissionHeaderRepository.MISSIONSTATUS_SELECT_FINISHED ) ) {
            throw new AxelorException(pMissionHeader,TraceBackRepository.CATEGORY_INCONSISTENCY ,"Le statut de la mission ne doit pas être annulé ou terminé");
        }
        if (IsAffectedLotInMission(pMissionHeader, pLot)) {
            return null;
        }
              
        /*
         * 
      MissionLineNo := 0;
      //>>AP01.ISAT.SC
      GetMissionLineNo(pMissionHeader."No.");
         */
        missionLineNo = 0;
        GetMissionLineNo(pMissionHeader);

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
            lMissionLine.setLotNoMission(GetNewLotMissionNo(pMissionHeader));
        }
        lMissionLine.setContactNo(pMissionHeader.getMasterContactNo());
        lMissionLine.setType(MissionLineRepository.TYPE_SELECT_LOT);

        //validate
        lMissionLine = missionLineValidate.validateNoLot(lMissionLine, pLot);
        //lMissionLine.setNoLot(pLot);
        lMissionLine.setActivityCode(pMissionHeader.getActivityCodeToLines());
        lMissionLine.setLotTemplateCode(pLot.getLotTemplateCode());
        lMissionLine.setLotNatureCode(pLot.getLotNatureCode());
        lMissionLine.setAuctionMission(pMissionHeader.getAuctionMission());
        lMissionLine.setMissionType(pMissionHeader.getMissionType());
        lMissionLine.setInventoryIndentation(pLotQuickInputJournal.getIndentation());
        lMissionLine.setQuantity(BigDecimal.valueOf(pLotQuickInputJournal.getQuantity()));
        //lMissionLine.setSortingSequenceNo(pLotQuickInputJournal.getSor());
        lMissionLine.setConsigAgreementAccepted(pMissionHeader.getGlobalConsAgreemAccepted());
        lMissionLine.setConsigAgreementRefused(pMissionHeader.getGlobalConsAgreemRefused());

        //TODO setLocationCode setBinCode
        //lMissionLine.setLocationCode(pLot.getCurr());
        //lMissionLine.setBinCode(pLot.getCurrentBinCode());
        missionLineRepo.save(lMissionLine);
        return lMissionLine;
    }

    @Override
    public void CreateCommentMissionLine(MissionHeader pMissionHeader, LotQuickInputJournal pLotQuickInputJournal) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String GetMissionLineNo(MissionHeader pMissionHeaderNo) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String ExistPostedBuyerInvoice(MissionHeader pMissionNo, Lot pLotNo) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String ExistPostedSellerInvoice(MissionHeader pMissionNo, Lot pLotNo) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String ExistPostedMissionInvoice(MissionHeader pMissionNo, Lot pLotNo) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer LotInsertMissionNotAuction(MissionHeader pMissionHeader, Lot pLot, MissionLine pMissionLine,
            LotQuickInputJournal pLotQuickInputJournal) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Boolean CreateMissionLineNotAuction(MissionHeader pMissionHeader, MissionLine pMissionLine, Lot pLot,
            LotQuickInputJournal pLotQuickInputJournal) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer GetNewLotMissionNo(MissionHeader pMissionNo) {
        // TODO Auto-generated method stub
        return null;
    }
}
