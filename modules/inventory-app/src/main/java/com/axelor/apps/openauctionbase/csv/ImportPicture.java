package com.axelor.apps.openauctionbase.csv;

import com.axelor.apps.openauction.db.Lot;
import com.axelor.apps.openauction.db.PictureAttachement;
import com.axelor.meta.MetaFiles;
import com.axelor.meta.db.MetaFile;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import java.io.File;
import java.nio.file.Path;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImportPicture {

  private static final Logger LOG = LoggerFactory.getLogger(ImportPicture.class);

  @Inject private MetaFiles metaFiles;

  public Object importMainPicture(Object bean, Map<String, Object> values) {

    Lot lot = (Lot) bean;

    final Path path = (Path) values.get("__path__");
    String fileName = (String) values.get("mainPicture_fileName");

    if (!Strings.isNullOrEmpty(fileName)) {
      try {
        final File image = path.resolve("img" + File.separator + fileName).toFile();
        if (image.exists()) {
          final MetaFile metaFile = metaFiles.upload(image);
          lot.setMainPicture(metaFile);
        }
      } catch (Exception e) {
        LOG.warn("Can't load image {} for app {}", fileName, lot.getName());
      }
    }

    return lot;
  }

  public Object importPicture(Object bean, Map<String, Object> values) {

    PictureAttachement pictureAttachement = (PictureAttachement) bean;

    final Path path = (Path) values.get("__path__");
    String fileName = (String) values.get("filePath");

    if (!Strings.isNullOrEmpty(fileName)) {
      try {
        final File image = path.resolve("img" + File.separator + fileName).toFile();
        if (image.exists()) {
          final MetaFile metaFile = metaFiles.upload(image);
          pictureAttachement.setPicture(metaFile);
        }
      } catch (Exception e) {
        LOG.warn("Can't load image {} for app {}", fileName, pictureAttachement.getSourceLotNo());
      }
    }

    return pictureAttachement;
  }
}
