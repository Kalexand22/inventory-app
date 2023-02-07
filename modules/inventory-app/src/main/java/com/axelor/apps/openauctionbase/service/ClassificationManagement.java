package com.axelor.apps.openauctionbase.service;

import com.axelor.apps.openauction.db.ClassificationMembers;
import com.axelor.apps.openauction.db.Sector;

public interface ClassificationManagement {
  // PROCEDURE ValidateLotClassCode@1000000001(SectorCode@1000000001 : Code[10];ClassNo@1000000002 :
  // Integer;CategorieCode@1000000000 : Code[10]);
  public void validateLotClassCode(
      Sector sectorCode, int classNo, ClassificationMembers categorieCode);
  // PROCEDURE GetLotSectorClass@1000000002(SectorCode@1000000000 : Code[10];ClassNo@1000000002 :
  // Integer) : Code[10];
  public String getLotSectorClass(Sector sectorCode, int classNo);
  // PROCEDURE ValidateExpertClassCode@1000000003(SpecializingCode@1000000000 : Code[10]);
  public void validateExpertClassCode(String specializingCode);
  // PROCEDURE GetDescriptionCat1@1100281000(pSectorCode@1100281008 :
  // Code[10];pCategory1Code@1100281007 : Code[10]) : Text[50];
  public String getDescriptionCat1(Sector sectorCode, String category1Code);
  // PROCEDURE GetDescriptionCat2@1100281001(pSectorCode@1100281008 :
  // Code[10];pCategory2Code@1100281007 : Code[10]) : Text[50];
  public String getDescriptionCat2(Sector sectorCode, String category2Code);
  // PROCEDURE GetDescriptionCat3@1100281002(pSectorCode@1100281008 :
  // Code[10];pCategory3Code@1100281007 : Code[10]) : Text[50];
  public String getDescriptionCat3(Sector sectorCode, String category3Code);
  // PROCEDURE GetDescriptionCat4@1100281003(pSectorCode@1100281008 :
  // Code[10];pCategory4Code@1100281007 : Code[10]) : Text[50];
  public String getDescriptionCat4(Sector sectorCode, String category4Code);
}
