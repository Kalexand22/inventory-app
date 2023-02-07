package com.axelor.apps.openauctionbase.module;

import com.axelor.app.AxelorModule;
import com.axelor.apps.openauction.db.repo.MissionHeaderRepository;
import com.axelor.apps.openauction.db.repo.MissionServiceLineRepository;
import com.axelor.apps.openauctionbase.repository.MissionHeaderOpenAuctionRepo;
import com.axelor.apps.openauctionbase.repository.MissionServiceLineRepositoryExt;
import com.axelor.apps.openauctionbase.service.ActivityManagement;
import com.axelor.apps.openauctionbase.service.ActivityManagementImpl;
import com.axelor.apps.openauctionbase.service.AuctionLotValueManagement;
import com.axelor.apps.openauctionbase.service.AuctionLotValueManagementImpl;
import com.axelor.apps.openauctionbase.service.AuctionManagement;
import com.axelor.apps.openauctionbase.service.AuctionManagementImpl;
import com.axelor.apps.openauctionbase.service.AuctionServicePriceMgt;
import com.axelor.apps.openauctionbase.service.AuctionServicePriceMgtImpl;
import com.axelor.apps.openauctionbase.service.CaretakerServicePriceMgt;
import com.axelor.apps.openauctionbase.service.CaretakerServicePriceMgtImpl;
import com.axelor.apps.openauctionbase.service.ClassificationManagement;
import com.axelor.apps.openauctionbase.service.ClassificationManagementimpl;
import com.axelor.apps.openauctionbase.service.ContactLotManagement;
import com.axelor.apps.openauctionbase.service.ContactLotManagementImpl;
import com.axelor.apps.openauctionbase.service.LotInputJournalPostLine;
import com.axelor.apps.openauctionbase.service.LotInputJournalPostLineImpl;
import com.axelor.apps.openauctionbase.service.LotStatusMgt;
import com.axelor.apps.openauctionbase.service.LotStatusMgtImpl;
import com.axelor.apps.openauctionbase.service.LotTemplateManagement;
import com.axelor.apps.openauctionbase.service.LotTemplateManagementImpl;
import com.axelor.apps.openauctionbase.service.LotValueJournalPostLine;
import com.axelor.apps.openauctionbase.service.LotValueJournalPostLineImpl;
import com.axelor.apps.openauctionbase.service.MissionLineManagement;
import com.axelor.apps.openauctionbase.service.MissionLineManagementImpl;
import com.axelor.apps.openauctionbase.service.MissionManagement;
import com.axelor.apps.openauctionbase.service.MissionManagementImpl;
import com.axelor.apps.openauctionbase.service.MissionServicePriceManagement;
import com.axelor.apps.openauctionbase.service.MissionServicePriceManagementImpl;
import com.axelor.apps.openauctionbase.service.MissionStatusManagement;
import com.axelor.apps.openauctionbase.service.MissionStatusManagementImpl;
import com.axelor.apps.openauctionbase.service.MissionTemplateManagement;
import com.axelor.apps.openauctionbase.service.MissionTemplateManagementImpl;
import com.axelor.apps.openauctionbase.service.ToolsMissionChange;
import com.axelor.apps.openauctionbase.service.ToolsMissionChangeImpl;
import com.axelor.apps.openauctionbase.service.TransportAnalysisUpdate;
import com.axelor.apps.openauctionbase.service.TransportAnalysisUpdateImpl;
import com.axelor.apps.openauctionbase.service.WebSynchroManagement;
import com.axelor.apps.openauctionbase.service.WebSynchroManagementImpl;

public class OpenAuctionBaseModule extends AxelorModule {

  @Override
  protected void configure() {

    bind(MissionServiceLineRepository.class).to(MissionServiceLineRepositoryExt.class);
    bind(MissionTemplateManagement.class).to(MissionTemplateManagementImpl.class);
    bind(ActivityManagement.class).to(ActivityManagementImpl.class);
    bind(AuctionManagement.class).to(AuctionManagementImpl.class);
    bind(MissionManagement.class).to(MissionManagementImpl.class);
    bind(MissionServicePriceManagement.class).to(MissionServicePriceManagementImpl.class);
    bind(ContactLotManagement.class).to(ContactLotManagementImpl.class);
    bind(AuctionLotValueManagement.class).to(AuctionLotValueManagementImpl.class);
    bind(CaretakerServicePriceMgt.class).to(CaretakerServicePriceMgtImpl.class);
    bind(AuctionServicePriceMgt.class).to(AuctionServicePriceMgtImpl.class);
    bind(LotStatusMgt.class).to(LotStatusMgtImpl.class);
    bind(ToolsMissionChange.class).to(ToolsMissionChangeImpl.class);
    bind(MissionStatusManagement.class).to(MissionStatusManagementImpl.class);
    bind(LotInputJournalPostLine.class).to(LotInputJournalPostLineImpl.class);
    bind(MissionHeaderRepository.class).to(MissionHeaderOpenAuctionRepo.class);
    bind(LotTemplateManagement.class).to(LotTemplateManagementImpl.class);
    bind(MissionLineManagement.class).to(MissionLineManagementImpl.class);
    bind(ClassificationManagement.class).to(ClassificationManagementimpl.class);
    bind(LotValueJournalPostLine.class).to(LotValueJournalPostLineImpl.class);
    bind(TransportAnalysisUpdate.class).to(TransportAnalysisUpdateImpl.class);
    bind(WebSynchroManagement.class).to(WebSynchroManagementImpl.class);
  }
}
