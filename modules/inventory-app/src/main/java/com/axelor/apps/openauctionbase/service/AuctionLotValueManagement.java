package com.axelor.apps.openauctionbase.service;

import com.axelor.apps.openauction.db.AuctionLine;
import com.axelor.apps.openauction.db.Lot;
import com.axelor.apps.openauction.db.LotQuickInputJournal;

public interface AuctionLotValueManagement {

  // PROCEDURE PostSellingOff@1000000000(VAR pAuctionLine@1000000000 : Record 8011401);
  public void postSellingOff(AuctionLine pAuctionLine);
  // PROCEDURE PostAuctionLineLotValues@1100281000(pAuctionLine@1100281000 : Record 8011401);
  public void postAuctionLineLotValues(AuctionLine pAuctionLine);
  // PROCEDURE CalcAuctionEstimByScale@1180113000(pLotNo@1180113000 :
  // Code[20];pAmountCalcBase@1180113003 : Decimal;VAR pMinAuctionEstimValue@1180113001 :
  // Decimal;VAR pMaxAuctionEstimValue@1180113002 : Decimal) : Boolean;
  public boolean calcAuctionEstimByScale(
      Lot pLotNo,
      double pAmountCalcBase,
      double pMinAuctionEstimValue,
      double pMaxAuctionEstimValue);
  // PROCEDURE CalcAuctionEstimByLotQuickJnl@1180113001(VAR pLotQuickInputJournal@1180113000 :
  // Record 8011467);
  public void calcAuctionEstimByLotQuickJnl(LotQuickInputJournal pLotQuickInputJournal);
  // PROCEDURE CalcAuctionEstimByLotInputJnl@1180113002(VAR pLotInputJournal@1180113000 : Record
  // 8011498);
  public void calcAuctionEstimByLotInputJnl(LotQuickInputJournal pLotQuickInputJournal);
  // PROCEDURE CreateAuctionEstimByLot@1180113003(pLotNo@1180113000 : Code[20]);
  public void createAuctionEstimByLot(Lot pLotNo);
}
