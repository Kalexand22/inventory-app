package com.axelor.apps.openauctionbase.service;

public interface WebSynchroManagement {

  // PROCEDURE WriteSynchro@1180113006(pNo@1180113000 : Code[20];pTableNo@1180113001 :
  // Integer;pOption@1180113004 : 'Insertion,Modification,Suppression';pWebNo@1180113005 :
  // Code[20]);
  public void writeSynchro(String pNo, int pTableNo, String pOption, String pWebNo);
  // PROCEDURE ModifyERPContact@1180113004(pImportContactWeb@1180113000 : Record 8011648);
  // PROCEDURE ExportNavWebContact@1180113000();
  // PROCEDURE CreateContactXMLFile@1180113003(pPath@1180113000 : Text[1024]);
  // PROCEDURE ImportWebNavContact@1180113001();
  // PROCEDURE CreateAuction@1180113010(VAR pAuctionHeaderTemp@1180113002 : TEMPORARY Record
  // 8011400;VAR rAuctionHeader@1180113003 : Record 8011400) rAuctionNo : Code[20];
  // PROCEDURE "----"@1180113011();
  // PROCEDURE ImportWebNavSalesLine@1180113007();
  // PROCEDURE InsertSalesLine@1180113015(VAR pVehImportHist@1180113001 : Record 8011646) rError :
  // Text[250];
  // PROCEDURE DeleteSalesLine@1180113014(VAR pVehImportHist@1180113001 : Record 8011646) rError :
  // Text[250];
  // PROCEDURE ModifySalesLine@1180113012(VAR pVehImportHist@1180113001 : Record 8011646) rError :
  // Text[250];
  // PROCEDURE ControlFields@1180113009(pVehicleWebImportHistory@1180113000 : Record 8011646);
  // PROCEDURE ExportAnomaly@1180113016(pNoImport@1180113001 : Integer);
  // PROCEDURE ImportWebNavSchedule@1180113018();
  // PROCEDURE ExportWebNavSchedule@1180113008(VAR pAuctionHeaderTemp@1180113001 : TEMPORARY Record
  // 8011400);
  // PROCEDURE GoToHist@1180113019(pFromPath@1180113000 : Text[1024];pFileName@1180113002 :
  // Text[1024]);
  // PROCEDURE GoToErr@1180113013(pFromPath@1180113000 : Text[1024];pFileName@1180113002 :
  // Text[1024]);
  // PROCEDURE ExistLotInAuction@1180113005(VAR pAuctionNo@1180113000 : Code[20];VAR pLot@1180113001
  // : Code[20]) rExist : Boolean;
  // PROCEDURE ImportWebNavDestockage@1180113002();
  // PROCEDURE DestockLot@1180113017(VAR pLotTemp@1180113000 : TEMPORARY Record 8011404) rOk :
  // Boolean;
}
