package com.axelor.apps.openauctionbase.service;

import com.axelor.apps.base.db.Partner;
import com.axelor.apps.openauction.db.Lot;

public interface ContactLotManagement {

  // PROCEDURE InsertSellerContactbyLot@1000000001(pContactNo@1000000000 :
  // Code[20];pLotNo@1000000001 : Code[20]);
  public void insertSellerContactbyLot(Partner pContactNo, Lot pLotNo);
  // PROCEDURE InsertBuyerContactbyLot@1100281001(pContactNo@1000000000 : Code[20];pLotNo@1000000001
  // : Code[20]);
  public void insertBuyerContactbyLot(Partner pContactNo, Lot pLotNo);
  // PROCEDURE InsertAbsenteeBidContactbyLot@1100281002(pContactNo@1100281001 :
  // Code[20];pLotNo@1100281000 : Code[20]);
  public void insertAbsenteeBidContactbyLot(Partner pContactNo, Lot pLotNo);
  // PROCEDURE ChangeSellerContact@1100281003(pLotNo@1100281002 : Code[20];pNewContactNo@1100281000
  // : Code[20]);
  public void changeSellerContact(Lot pLotNo, Partner pNewContactNo);
  // PROCEDURE DropBuyerContactByLot@1000000004(pContactNo@1000000001 : Code[20];pLotNo@1000000000 :
  // Code[20]);
  public void dropBuyerContactByLot(Partner pContactNo, Lot pLotNo);
  // PROCEDURE ChangeBuyerContact@1100281004(pLotNo@1100281002 : Code[20];pNewContactNo@1100281000 :
  // Code[20]);
  public void changeBuyerContact(Lot pLotNo, Partner pNewContactNo);
  // PROCEDURE GetSetup@1100281000();
  public void getSetup();
}
