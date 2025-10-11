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

    List<Product> getProductsByIds(List<Integer> productIds);

    List<ProductVariant> getVariantsByIds(List<Integer> productVariantIds);

    Map<Integer, List<ProductVariant>> getVariantsByProductIds(List<Integer> productIds);

    Map<Integer, ProductNutritionFacts> getNutritionByProductIds(List<Integer> productIds);

    Map<Integer, List<String>> getImagesByProductIds(List<Integer> productIds);

    Map<Integer, List<String>> getIngredientByProductIds(List<Integer> productIds);

    List<StockHistory> getStockHistoryByProductVariantId(Integer productVariantId);

    Map<Integer, List<StockHistory>> getStockHistoryByProductVariantIds(List<Integer> productVariantIds);

    Integer createProduct(Product product);

    List<Integer> createProductVariants(Integer productId, List<ProductVariant> productVariantList);

    void createProductNutrition(Integer productId, ProductNutritionFacts productNutritionFacts);

    void createProductImages(Integer productId, List<String> images);

    void createProductIngredients(Integer productId, List<String> ingredients);

    void updateProduct(Integer productId, Product product);

    void updateProductVariants(Integer productId, List<ProductVariant> productVariantList);

    void updateProductNutrition(Integer productId, ProductNutritionFacts productNutritionFacts);

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
