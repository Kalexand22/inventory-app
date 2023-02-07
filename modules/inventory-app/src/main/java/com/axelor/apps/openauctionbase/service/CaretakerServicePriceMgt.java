package com.axelor.apps.openauctionbase.service;

import com.axelor.apps.openauction.db.MissionServiceLine;
import com.axelor.apps.openauction.db.TariffScale;
import com.ibm.icu.math.BigDecimal;
import java.util.Date;

public interface CaretakerServicePriceMgt {
  // PROCEDURE FindMissionServicePrice@1000000001(VAR pMissionServiceLine@1000000000 : Record
  // 8011449;pEstimated@1000000004 : Boolean);
  public MissionServiceLine findMissionServicePrice(
      MissionServiceLine pMissionServiceLine, Boolean pEstimated);
  // PROCEDURE GetTariffScaleAmount@1000000009(pAmount@1000000002 : Decimal;pTariffScale@1000000001
  // : Code[20];VAR pAccesBuffer@1000000000 : Record 8011477);
  public void getTariffScaleAmount(
      BigDecimal pAmount, TariffScale pTariffScale, Object pAccesBuffer);
  // PROCEDURE GetPriceDate@1100481007(pMissServLine@1100481000 : Record 8011449) ReturnValue :
  // Date;
  public Date getPriceDate(MissionServiceLine pMissServLine);
}
