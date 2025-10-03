package com.williamhsieh.fruitandessence.dao;

import com.williamhsieh.fruitandessence.dto.ProductNutritionFactsRequest;
import com.williamhsieh.fruitandessence.dto.ProductQueryParams;
import com.williamhsieh.fruitandessence.dto.ProductRequest;
import com.williamhsieh.fruitandessence.dto.ProductVariantRequest;
import com.williamhsieh.fruitandessence.model.Product;
import com.williamhsieh.fruitandessence.model.ProductNutritionFacts;
import com.williamhsieh.fruitandessence.model.ProductVariant;
import com.williamhsieh.fruitandessence.model.StockHistory;

import java.util.List;
import java.util.Map;

public interface ProductDao {

    Integer countProduct(ProductQueryParams productQueryParams);

    List<Product> getProducts(ProductQueryParams productQueryParams);

    Product getProductById(Integer productId);

    Map<Integer, List<ProductVariant>> getVariantsByProductIds(List<Integer> productIds);

    Map<Integer, ProductNutritionFacts> getNutritionByProductIds(List<Integer> productIds);

    Map<Integer, List<String>> getImagesByProductIds(List<Integer> productIds);

    Map<Integer, List<String>> getIngredientByProductIds(List<Integer> productIds);

    Map<Integer, List<StockHistory>> getStockHistoryByProductVariantIds(List<Integer> productVariantIds);

    Integer createProduct(ProductRequest productRequest);

    List<Integer> createProductVariants(Integer productId, List<ProductVariantRequest> productVariantRequestList);

    void createProductNutrition(Integer productId, ProductNutritionFactsRequest productNutritionFactsRequest);

    void createProductImages(Integer productId, List<String> images);

    void createProductIngredients(Integer productId, List<String> ingredients);

    void updateProduct(Integer productId, ProductRequest productRequest);

    void updateProductVariants(Integer productId, List<ProductVariantRequest> productVariantRequestList);

    void updateProductNutrition(Integer productId, ProductNutritionFactsRequest productNutritionFactsRequest);

    void updateProductImages(Integer productId, List<String> images);

    void updateProductIngredients(Integer productId, List<String> ingredients);

    void insertStockHistory(StockHistory stockHistory);

    void deleteProductById(Integer productId);

    void deleteProductVariantsByProductId(Integer productId);

    void deleteProductNutritionByProductId(Integer productId);

    void deleteProductImagesByProductId(Integer productId);

    void deleteProductIngredientsByProductId(Integer productId);

    void deleteStockByProductVariantIds(List<Integer> productVariantIds);

}
