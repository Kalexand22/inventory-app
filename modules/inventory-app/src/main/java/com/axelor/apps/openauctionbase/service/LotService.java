package com.axelor.apps.openauctionbase.service;

import com.axelor.apps.openauction.db.Lot;
import com.axelor.apps.openauction.db.PictureAttachement;
import com.axelor.apps.openauction.db.repo.LotRepository;
import com.axelor.apps.openauction.db.repo.PictureAttachementRepository;
import com.axelor.inject.Beans;
import com.axelor.meta.MetaFiles;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class LotService {
  PictureAttachementRepository pictureAttachementRepo;

  MetaFiles metaFiles;

  @Inject
  public LotService(PictureAttachementRepository pictureAttachementRepo, MetaFiles metaFiles) {
    this.pictureAttachementRepo = pictureAttachementRepo;
    this.metaFiles = metaFiles;
  }

  @Transactional
  public PictureAttachement addPicture(Lot lot) throws IOException {
    PictureAttachement pictureAttachement = new PictureAttachement();

    pictureAttachement.setSourceLotNo(lot);

    File imgTmpFile = MetaFiles.createTempFile("IMG", ".png").toFile();
    saveImage(
        "iVBORw0KGgoAAAANSUhEUgAAAKUAAACECAMAAADGBRt7AAAAY1BMVEX///8zMzP4+PhOTk4tLS0lJSWCgoKJiYnz8/NEREQAAAAfHx91dXUwMDD8/PwaGhp7e3vc3Nw5OTlvb28VFRXJycmgoKCXl5dkZGS+vr4KCgrV1dXi4uLp6emRkZFZWVmrq6v/eLJtAAADLElEQVR4nO2a69aqIBBARdHK+7XMr069/1OepLLEIW9AtNbsv7hsO87QgFgWgiAIgiAIgiAIgiAIgiA6iW0dxOskt4Wjg2K7ynLvUx00h1WWG5cQVzW3n/DWWuaeavL1ltHOXnWHcWKHSrBcl9njbCVYUrRkoKU80HIWf/s68f08PJ6AQUMsz4Vb0oiQyG3y/bCxMMNy39z+Ax9EpfvHjxtheSjJOzTnNU2w/NeQPtTnktMAyz+f8NC6f8fvW9oXOrAk5dUwy8odSpIoMMwyDQBL4vcK6PuWeQRZBr0FxNct42HtsPoJjbK0BZYFWs7mN964RcDqcc2qHsv7iZmogiyjsnfN9y3tGviHDK6GWYLdhmNat3Hrgfl3TgPzOjcr3vic5JG7wgRLy7rSt8Yo2WX8uBmWVnbxAxpFEaWNfxjeTZOlXRQj+3LnQ3FxnEt6hLamNVmmQRCOXWNtz2fBo2ixjMOE5GW4fJdTi2XIppqg+HzVBzRY2mnyKN7F0dRg+WonJuQmjHLL+BlJFs102ccl5Zb9JeLCaCq2bKu7RxIuiaZay3jY4iap+FYn0X2UWvZycjw3M1oIhpRaQpLiaJ5uzZAgH1Ra8jn5KiHIJctvPXsCR1Odpe0JJGFNJtkOQTO/Okt4l0qUm9mOPp9AoyVYOGLNk9st0aBoqrIU5eTL5f3qx+t+PMGwK1FjaX+OJJ+bGe0tdoddiRrLTznZaXYTUpeT3RAfTRWWdlrCYn3K9B6yUzLYNuCjqcKymBBJFjKWm+cc2tuoFVvaY4XzFjK2ZKsSYNct6K3m5FtOycnOheXmFXqs3iQg2zKelpNPGpaAVQlEM3mLpmzLcEYkWchYOV/hl67IckZOPimn5KZcyzk52bmwBDy6kOYzN2VaTpwnee7zZhVAufmYN2VaLonkK2SfoinPcrAQm07JOqQjlJv3SpdnuTSSr5AJohlLsnTssX5yjHs0qxwYatfGMk7jtScba+gr9wzc2vO8zQX6QuXWGy+PVlp67JQo0DHMg7aHQcHPaO3Q6lOimk7c+utO3G7DXzi9/BsnwREEQRAEQRAEQRAEQRAEmcl/H4o6R9C7nKwAAAAASUVORK5CYII=",
        imgTmpFile);

    pictureAttachement.setPicture(metaFiles.upload(imgTmpFile));
    pictureAttachementRepo.save(pictureAttachement);

    return pictureAttachement;
  }

  // read an image base 64 and save it to a file
  public static void saveImage(String imageBase64, File destinationFile) throws IOException {

    OutputStream os = new FileOutputStream(destinationFile);
    byte[] b = javax.xml.bind.DatatypeConverter.parseBase64Binary(imageBase64);
    os.write(b);
    os.close();
  }

  @Transactional
  public void changeLotMainPicture(Lot lot, PictureAttachement pictureAttachement) {
    LotRepository lotRepo = Beans.get(LotRepository.class);
    lot.setMainPicture(pictureAttachement.getPicture());
    lotRepo.save(lot);
  }
}
