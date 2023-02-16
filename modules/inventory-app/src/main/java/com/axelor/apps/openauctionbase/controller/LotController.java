package com.axelor.apps.openauctionbase.controller;

import com.axelor.apps.openauction.db.MissionLine;
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

      MissionLine missionLine = request.getContext().asType(MissionLine.class);
      String picturePath = missionLine.getId().toString();
      LotService lotService = Beans.get(LotService.class);
      PictureAttachement pictureAttachement = lotService.addPicture(missionLine);

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
              .context("_missionLine", missionLine)
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
        MissionLine lot = pictureAttachement.getSourceMissionLine();
        // lotService.changeLotMainPicture(lot, pictureAttachement);
      }

    } catch (Exception e) {
      TraceBackService.trace(response, e);
    }
  }
}
