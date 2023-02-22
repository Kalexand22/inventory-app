package com.axelor.apps.openauctionbase.listener;

import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;

import com.axelor.apps.openauction.db.PictureAttachement;

public class PictureAttachementListener {
    @PostPersist
    @PostUpdate
    private void onPostPersistOrUpdate(PictureAttachement pictureAttachement) {
        System.out.println("Contact saved");
    }
}
