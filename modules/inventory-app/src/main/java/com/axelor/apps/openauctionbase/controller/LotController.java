package com.axelor.apps.openauctionbase.controller;

import com.axelor.apps.openauction.db.Lot;
import com.axelor.apps.openauction.db.PictureAttachement;
import com.axelor.apps.openauctionbase.service.LotService;
import com.axelor.exception.service.TraceBackService;
import com.axelor.inject.Beans;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;

public class LotController {
  public void addPicture(ActionRequest request, ActionResponse response) {

    try {

      Lot lot = request.getContext().asType(Lot.class);

      LotService lotService = Beans.get(LotService.class);
      PictureAttachement pictureAttachement = lotService.addPicture(lot);

      response.setView(
          ActionView.define("Ajouter une photo")
              .model(PictureAttachement.class.getName())
              .add("form", "picture-attachement-form")
              .param("popup", "reload")
              .param("show-toolbar", "false")
              .param("show-confirm", "false")
              .param("forceEdit", "true")
              .param("popup.maximized", "true")
              .context("_showRecord", pictureAttachement.getId())
              .context("_lot", lot)
              .map());

    } catch (Exception e) {
      TraceBackService.trace(response, e);
    }
  }

  public void changeLotMainPicture(ActionRequest request, ActionResponse response) {

    try {

      LotService lotService = Beans.get(LotService.class);
      PictureAttachement pictureAttachement = request.getContext().asType(PictureAttachement.class);

      if (pictureAttachement != null && pictureAttachement.getMain()) {
        Lot lot = pictureAttachement.getSourceLotNo();
        lotService.changeLotMainPicture(lot, pictureAttachement);
      }

    } catch (Exception e) {
      TraceBackService.trace(response, e);
    }
  }
}
