package com.williamhsieh.fruitandessence.service;

import com.williamhsieh.fruitandessence.dto.*;

import java.util.List;
import java.util.Map;

public interface ProductService {

    Integer countProduct(ProductQueryParams productQueryParams);

    List<ProductResponse> getProducts(ProductQueryParams productQueryParams);

    ProductResponse getProductById(Integer productId);

    Map<Integer, List<StockHistoryResponse>> getStockHistory(List<Integer> productVariantIds);

    Integer createProduct(ProductRequest productRequest);

    StockHistoryResponse increaseStock(StockHistoryRequest request);

    StockHistoryResponse decreaseStock(StockHistoryRequest request);

    StockHistoryResponse adjustStock(StockHistoryRequest request);

    void updateProduct(Integer productId, ProductRequest productRequest);

    void deleteProductById(Integer productId);

}
