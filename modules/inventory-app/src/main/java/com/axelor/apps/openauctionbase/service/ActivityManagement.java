package com.axelor.apps.openauctionbase.service;

import com.axelor.apps.openauction.db.ActivityHeader;
import com.axelor.apps.openauction.db.AuctionHeader;
import com.axelor.apps.openauction.db.Lot;
import com.axelor.apps.openauction.db.LotTemplate;
import com.axelor.apps.openauction.db.MissionHeader;
import com.axelor.apps.openauction.db.MissionLine;
import com.axelor.exception.AxelorException;

public interface ActivityManagement {
  public void createActivityLines2(
      ActivityHeader pActivityHeader,
      AuctionHeader pAuctionHeader,
      MissionHeader pMissionHeader,
      Lot pLotNo,
      LotTemplate pLotTemplate,
      Boolean pIsAuction,
      Boolean pIsActionOnly,
      Integer pTransactionMineNo)
      throws AxelorException;

  public void removeActivityLines(
      ActivityHeader pActivityHeader,
      AuctionHeader pAuctionHeader,
      MissionHeader pMissionHeader,
      Lot pLotNo,
      Integer pTransactionMineNo);

  /*
    public void createTodo(
        ActivityHeader pActivityHeader,
        AuctionHeader pAuctionHeader,
        MissionHeader pMissionHeader,
        Lot pLotNo,
        Integer pTransactionMineNo,
        Partner pContact,
        Partner pSalesPerson);
  */
  public void createActivityLineFromMission(
      ActivityHeader pActivityHeader,
      MissionHeader pMissionHeader,
      MissionLine pMissionLine,
      Boolean pActionOnly)
      throws AxelorException;
}
