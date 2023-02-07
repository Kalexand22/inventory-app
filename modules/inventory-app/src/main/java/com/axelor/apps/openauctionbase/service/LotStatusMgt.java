package com.axelor.apps.openauctionbase.service;

import com.axelor.apps.openauction.db.Lot;

public interface LotStatusMgt {
  // PROCEDURE CheckGeneralStatus@1100481005();
  public Lot CheckGeneralStatus(Lot pLot);
  // PROCEDURE UpdLotInventoryStatus@1100481000(VAR pLot@1100481000 : Record 8011404);
  public Lot UpdLotInventoryStatus(Lot pLot);
  // PROCEDURE UpdLotOperationStatus@1100481001(VAR pLot@1100481000 : Record 8011404);
  public Lot UpdLotOperationStatus(Lot pLot);
  // PROCEDURE UpdLotMissionStatus@1100481002(VAR pLot@1100481000 : Record 8011404);
  public Lot UpdLotMissionStatus(Lot pLot);
  // PROCEDURE UpdLotAuctionStatus@1100481003(VAR pLot@1100481000 : Record 8011404);
  public Lot UpdLotAuctionStatus(Lot pLot);
  // PROCEDURE UpdateLotStatusByTodo@1100481007(VAR pApTodo@1100481000 : Record 8011325);
  public Lot UpdateLotStatusByTodo();
}
