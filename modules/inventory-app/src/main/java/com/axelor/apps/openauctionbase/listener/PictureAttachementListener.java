package com.axelor.apps.openauctionbase.listener;

import com.axelor.apps.openauction.db.PictureAttachement;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;

public class PictureAttachementListener {
  @PostPersist
  @PostUpdate
  private void onPostPersistOrUpdate(PictureAttachement pictureAttachement) {
    System.out.println("Contact saved");
  }
}
