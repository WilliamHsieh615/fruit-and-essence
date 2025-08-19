package com.williamhsieh.fruitandessence.dao;

import com.williamhsieh.fruitandessence.dto.ProductRequest;
import com.williamhsieh.fruitandessence.model.Product;

public interface ProductDao {

    Product getProductById(Integer productId);

    Integer createProduct(ProductRequest productRequest);

    void updateProduct(Integer productId, ProductRequest productRequest);

}
