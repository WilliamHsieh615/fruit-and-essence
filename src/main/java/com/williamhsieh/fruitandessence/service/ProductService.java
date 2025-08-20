package com.williamhsieh.fruitandessence.service;

import com.williamhsieh.fruitandessence.dto.ProductQueryParams;
import com.williamhsieh.fruitandessence.dto.ProductRequest;
import com.williamhsieh.fruitandessence.dto.ProductResponse;

import java.util.List;

public interface ProductService {

    List<ProductResponse> getProducts(ProductQueryParams productQueryParams);

    ProductResponse getProductById(Integer productId);

    Integer createProduct(ProductRequest productRequest);

    void updateProduct(Integer productId, ProductRequest productRequest);

    void deleteProductById(Integer productId);

}
