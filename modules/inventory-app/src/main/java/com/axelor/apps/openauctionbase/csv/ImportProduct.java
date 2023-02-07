package com.axelor.apps.openauctionbase.csv;
/*
 * Axelor Business Solutions
 *
 * Copyright (C) 2022 Axelor (<http://axelor.com>).
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License, version 3,
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import com.axelor.apps.base.db.Product;
import com.axelor.apps.base.db.repo.ProductRepository;
import java.util.Map;

public class ImportProduct {

  public Object importProduct(Object bean, Map<String, Object> values) {

    Product product = (Product) bean;

    product.setProductTypeSelect(ProductRepository.PRODUCT_TYPE_SERVICE);
    product.setProcurementMethodSelect(ProductRepository.PROCUREMENT_METHOD_BUYANDPRODUCE);

    return product;
  }
}
