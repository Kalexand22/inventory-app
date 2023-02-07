package com.axelor.apps.openauctionbase.service;

import com.axelor.apps.openauction.db.Lot;

public interface TransportAnalysisUpdate {
  // PROCEDURE ResetAnalysis@1180113001();
  public void resetAnalysis();
  // PROCEDURE FillAll@1180113002();
  public void fillAll();
  // PROCEDURE FillModified@1180113005();
  public void fillModified();
  // PROCEDURE GetFlowfields@1180113008(VAR pTrAnalysis@1180113000 : Record 8011805;VAR
  // pVehLotDescTemp@1180113001 : TEMPORARY Record 8011438);
  public void getFlowfields();
  // PROCEDURE GetInfos@1180113000(VAR pTrAnalysis@1180113001 : Record 8011805);
  public void getInfos();
  // PROCEDURE InsertOne@1180113015(VAR pLotNo@1180113000 : Code[20]);
  public void insertOne(Lot pLotNo);

  // PROCEDURE UpdateOne@1180113003(VAR pLotNo@1180113000 : Code[20]);
  public void updateOne(Lot pLotNo);
  // PROCEDURE SetSynchroRecord@1180113004(pLotNo@1180113000 : Code[20];pTableNo@1180113001 :
  // Integer;pFieldNo@1180113002 : Integer);
  public void setSynchroRecord(Lot pLotNo, int pTableNo, int pFieldNo);
}
