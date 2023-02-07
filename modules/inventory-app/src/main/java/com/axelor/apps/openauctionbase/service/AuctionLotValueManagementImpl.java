package com.axelor.apps.openauctionbase.service;

import com.axelor.apps.openauction.db.AuctionLine;
import com.axelor.apps.openauction.db.Lot;
import com.axelor.apps.openauction.db.LotQuickInputJournal;

public class AuctionLotValueManagementImpl implements AuctionLotValueManagement {

  @Override
  public void postSellingOff(AuctionLine pAuctionLine) {
    // TODO Auto-generated method stub

  }

  @Override
  public void postAuctionLineLotValues(AuctionLine pAuctionLine) {
    // TODO Auto-generated method stub

  }

  @Override
  public boolean calcAuctionEstimByScale(
      Lot pLotNo,
      double pAmountCalcBase,
      double pMinAuctionEstimValue,
      double pMaxAuctionEstimValue) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void calcAuctionEstimByLotQuickJnl(LotQuickInputJournal pLotQuickInputJournal) {
    // TODO Auto-generated method stub

  }

  @Override
  public void calcAuctionEstimByLotInputJnl(LotQuickInputJournal pLotQuickInputJournal) {
    // TODO Auto-generated method stub

  }

  @Override
  public void createAuctionEstimByLot(Lot pLotNo) {
    // TODO Auto-generated method stub

  }
}
