package com.axelor.apps.openauctionbase.listener;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;

import com.axelor.apps.openauction.db.MissionLine;

public class MissionLineListener {
    @PostPersist
    private void onPostPersist(MissionLine missionLine) {
        System.out.println("Contact saved");
    }

    @PostUpdate
    private void onPostUpdate(MissionLine missionLine) {
        System.out.println("Contact saved");
    }

    @PostRemove
    private void onPostRemove(MissionLine missionLine) {
        System.out.println("Contact saved");
    }
}
