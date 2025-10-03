package com.williamhsieh.fruitandessence.service;

import com.williamhsieh.fruitandessence.constant.ProductSize;
import com.williamhsieh.fruitandessence.constant.StockChangeReason;
import com.williamhsieh.fruitandessence.dao.ProductDao;
import com.williamhsieh.fruitandessence.dto.*;
import com.williamhsieh.fruitandessence.model.Product;
import com.williamhsieh.fruitandessence.model.ProductNutritionFacts;
import com.williamhsieh.fruitandessence.model.ProductVariant;
import com.williamhsieh.fruitandessence.model.StockHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductDao productDao;

    @Override
    public Integer countProduct(ProductQueryParams productQueryParams) {
        return productDao.countProduct(productQueryParams);
    }

    @Override
    public List<ProductResponse> getProducts(ProductQueryParams productQueryParams) {
        List<Product> products = productDao.getProducts(productQueryParams);
        if (products.isEmpty()) {
            return List.of();
        }

        List<Integer> productIds = products.stream()
                .map(Product::getProductId)
                .toList();

        Map<Integer, List<ProductVariant>> productVariantListMap = productDao.getVariantsByProductIds(productIds);
        Map<Integer, ProductNutritionFacts> productNutritionFactsList = productDao.getNutritionByProductIds(productIds);
        Map<Integer, List<String>> productImageListMap = productDao.getImagesByProductIds(productIds);
        Map<Integer, List<String>> productIngredientListMap = productDao.getIngredientByProductIds(productIds);

        List<Integer> variantIds = productVariantListMap.values().stream()
                .flatMap(List::stream)
                .map(ProductVariant::getProductVariantId)
                .toList();
        Map<Integer, List<StockHistory>> stockHistoryListMap = productDao.getStockHistoryByProductVariantIds(variantIds);

        List<ProductResponse> productResponseList = products.stream()
                .map(product -> mapProductToResponse(
                        product,
                        productVariantListMap.getOrDefault(product.getProductId(), List.of()),
                        productNutritionFactsList.get(product.getProductId()),
                        productImageListMap.getOrDefault(product.getProductId(), List.of()),
                        productIngredientListMap.getOrDefault(product.getProductId(), List.of()),
                        stockHistoryListMap
                ))
                .toList();

        return productResponseList;
    }

    @Override
    public ProductResponse getProductById(Integer productId) {
        Product product = productDao.getProductById(productId);
        if (product == null) return null;

        List<Integer> productIds = List.of(productId);

        Map<Integer, List<ProductVariant>> productVariantListMap = productDao.getVariantsByProductIds(productIds);
        Map<Integer, ProductNutritionFacts> productNutritionFactsList = productDao.getNutritionByProductIds(productIds);
        Map<Integer, List<String>> productImageListMap = productDao.getImagesByProductIds(productIds);
        Map<Integer, List<String>> productIngredientListMap = productDao.getIngredientByProductIds(productIds);

        List<Integer> variantIds = productVariantListMap.getOrDefault(productId, List.of())
                .stream()
                .map(ProductVariant::getProductVariantId)
                .toList();
        Map<Integer, List<StockHistory>> stockHistoryMap = productDao.getStockHistoryByProductVariantIds(variantIds);

        ProductResponse productResponse = mapProductToResponse(
                product,
                productVariantListMap.getOrDefault(productId, List.of()),
                productNutritionFactsList.get(productId),
                productImageListMap.getOrDefault(productId, List.of()),
                productIngredientListMap.getOrDefault(productId, List.of()),
                stockHistoryMap
        );

        return productResponse;
    }

    @Override
    public Map<Integer, List<StockHistoryResponse>> getStockHistory(List<Integer> productVariantIds) {

        if (productVariantIds == null || productVariantIds.isEmpty()) {
            return Map.of();
        }

        Map<Integer, List<StockHistory>> stockHistoryListMap = productDao.getStockHistoryByProductVariantIds(productVariantIds);

        Map<Integer, List<StockHistoryResponse>> stockHistoryResponseListMap = stockHistoryListMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .map(this::mapToStockHistoryResponse)
                                .toList()
                ));

        return stockHistoryResponseListMap;
    }

    @Transactional
    @Override
    public Integer createProduct(ProductRequest productRequest) {

        Integer productId = productDao.createProduct(productRequest);

        List<ProductVariantRequest> productVariantRequestList = productRequest.getProductVariants();
        List<Integer> productVariantIdList = List.of();
        if (productVariantRequestList != null && !productVariantRequestList.isEmpty()) {
            productVariantIdList = productDao.createProductVariants(productId, productVariantRequestList);

            for (int i = 0; i < productVariantRequestList.size(); i++) {
                ProductVariantRequest productVariantRequest = productVariantRequestList.get(i);
                Integer productVariantId = productVariantIdList.get(i);
                if (productVariantRequest.getStock() != null && productVariantRequest.getStock() > 0) {
                    StockHistory stockHistory = new StockHistory();
                    stockHistory.setProductVariantId(productVariantId);
                    stockHistory.setChangeAmount(productVariantRequest.getStock());
                    stockHistory.setStockAfter(productVariantRequest.getStock());
                    stockHistory.setStockChangeReason(StockChangeReason.MANUAL_ADJUST);
                    productDao.insertStockHistory(stockHistory);
                }
            }
        }

        if (productRequest.getProductNutritionFacts() != null) {
            productDao.createProductNutrition(productId, productRequest.getProductNutritionFacts());
        }

        if (productRequest.getProductImages() != null && !productRequest.getProductImages().isEmpty()) {
            productDao.createProductImages(productId, productRequest.getProductImages());
        }

        if (productRequest.getProductIngredients() != null && !productRequest.getProductIngredients().isEmpty()) {
            productDao.createProductIngredients(productId, productRequest.getProductIngredients());
        }

        return productId;
    }

    @Override
    public StockHistoryResponse increaseStock(StockHistoryRequest stockHistoryRequest) {
        int currentStock = getCurrentStock(stockHistoryRequest.getProductVariantId());
        int newStock = currentStock + stockHistoryRequest.getChangeAmount();

        StockHistory stockHistory = new StockHistory();
        stockHistory.setProductVariantId(stockHistoryRequest.getProductVariantId());
        stockHistory.setChangeAmount(stockHistoryRequest.getChangeAmount());
        stockHistory.setStockAfter(newStock);
        stockHistory.setStockChangeReason(mapReasonType(stockHistoryRequest.getStockChangeReason().toString()));

        return recordStockChange(stockHistory);
    }

    @Override
    public StockHistoryResponse decreaseStock(StockHistoryRequest stockHistoryRequest) {
        int currentStock = getCurrentStock(stockHistoryRequest.getProductVariantId());
        int newStock = currentStock - stockHistoryRequest.getChangeAmount();
        if (newStock < 0) {
            throw new IllegalArgumentException("庫存不足，無法扣減");
        }

        StockHistory stockHistory = new StockHistory();
        stockHistory.setProductVariantId(stockHistoryRequest.getProductVariantId());
        stockHistory.setChangeAmount(-Math.abs(stockHistoryRequest.getChangeAmount()));
        stockHistory.setStockAfter(newStock);
        stockHistory.setStockChangeReason(mapReasonType(stockHistoryRequest.getStockChangeReason().toString()));

        return recordStockChange(stockHistory);
    }

    @Override
    public StockHistoryResponse adjustStock(StockHistoryRequest stockHistoryRequest) {
        int newStock = stockHistoryRequest.getChangeAmount();

        StockHistory stockHistory = new StockHistory();
        stockHistory.setProductVariantId(stockHistoryRequest.getProductVariantId());
        stockHistory.setChangeAmount(newStock);
        stockHistory.setStockAfter(newStock);
        stockHistory.setStockChangeReason(mapReasonType(stockHistoryRequest.getStockChangeReason().toString()));

        return recordStockChange(stockHistory);
    }

    @Transactional
    @Override
    public void updateProduct(Integer productId, ProductRequest productRequest) {

        productDao.updateProduct(productId, productRequest);

        if (productRequest.getProductVariants() != null && !productRequest.getProductVariants().isEmpty()) {
            productDao.updateProductVariants(productId, productRequest.getProductVariants());

            for (ProductVariantRequest productVariantRequest : productRequest.getProductVariants()) {
                if (productVariantRequest.getStock() != null) {
                    StockHistory stockHistory = new StockHistory();
                    stockHistory.setProductVariantId(productVariantRequest.getProductVariantId());
                    stockHistory.setChangeAmount(productVariantRequest.getStock());
                    stockHistory.setStockAfter(productVariantRequest.getStock());
                    stockHistory.setStockChangeReason(StockChangeReason.MANUAL_ADJUST);
                    productDao.insertStockHistory(stockHistory);
                }
            }
        }

        if (productRequest.getProductNutritionFacts() != null) {
            productDao.updateProductNutrition(productId, productRequest.getProductNutritionFacts());
        }

        if (productRequest.getProductImages() != null && !productRequest.getProductImages().isEmpty()) {
            productDao.updateProductImages(productId, productRequest.getProductImages());
        }

        if (productRequest.getProductIngredients() != null && !productRequest.getProductIngredients().isEmpty()) {
            productDao.updateProductIngredients(productId, productRequest.getProductIngredients());
        }
    }

    @Transactional
    @Override
    public void deleteProductById(Integer productId) {

        Map<Integer, List<ProductVariant>> productVariantListMap = productDao.getVariantsByProductIds(List.of(productId));
        List<ProductVariant> productVariantList = productVariantListMap.getOrDefault(productId, List.of());

        List<Integer> productVariantIds = productVariantList.stream()
                .map(ProductVariant::getProductVariantId)
                .toList();

        productDao.deleteStockByProductVariantIds(productVariantIds);

        productDao.deleteProductVariantsByProductId(productId);
        productDao.deleteProductNutritionByProductId(productId);
        productDao.deleteProductImagesByProductId(productId);
        productDao.deleteProductIngredientsByProductId(productId);

        productDao.deleteProductById(productId);
    }

    private ProductResponse mapProductToResponse(
            Product product,
            List<ProductVariant> variants,
            ProductNutritionFacts baseNutrition,
            List<String> images,
            List<String> ingredients,
            Map<Integer, List<StockHistory>> stockHistoryMap
    ) {
        ProductResponse productResponse = new ProductResponse();
        productResponse.setProductId(product.getProductId());
        productResponse.setProductName(product.getProductName());
        productResponse.setProductCategory(product.getProductCategory());
        productResponse.setProductDescription(product.getProductDescription());
        productResponse.setCreatedDate(product.getCreatedDate());
        productResponse.setLastModifiedDate(product.getLastModifiedDate());

        productResponse.setProductImages(images);
        productResponse.setProductIngredients(ingredients);

        List<ProductVariantResponse> productVariantResponseList = variants.stream()
                .map(variant -> {
                    ProductVariantResponse productVariantResponse = new ProductVariantResponse();
                    productVariantResponse.setProductVariantId(variant.getProductVariantId());
                    productVariantResponse.setProductSize(variant.getProductSize());
                    productVariantResponse.setPrice(variant.getPrice());
                    productVariantResponse.setDiscountPrice(variant.getDiscountPrice());
                    productVariantResponse.setUnit(variant.getUnit());
                    productVariantResponse.setSku(variant.getSku());
                    productVariantResponse.setBarcode(variant.getBarcode());

                    // 庫存：取最後一筆 stockHistory
                    List<StockHistory> stockHistoryList = stockHistoryMap.getOrDefault(variant.getProductVariantId(), List.of());
                    if (!stockHistoryList.isEmpty()) {
                        StockHistory lastStock = stockHistoryList.get(stockHistoryList.size() - 1);
                        productVariantResponse.setStock(lastStock.getStockAfter());
                    }

                    // 營養資訊計算
                    productVariantResponse.setNutritionFacts(calculateNutritionFacts(baseNutrition, variant.getProductSize()));

                    return productVariantResponse;
                })
                .toList();

        productResponse.setProductVariants(productVariantResponseList);

        return productResponse;
    }

    private ProductNutritionFactsResponse calculateNutritionFacts(ProductNutritionFacts base, ProductSize size) {
        if (base == null) return null;

        // base 是 Small_300ml (10 fl oz)
        double factor = (double) size.getFluidOunce() / ProductSize.SMALL_300ML.getFluidOunce();

        ProductNutritionFactsResponse productNutritionFactsResponse = new ProductNutritionFactsResponse();
        productNutritionFactsResponse.setServingSize(size.getLabel());
        productNutritionFactsResponse.setCalories((int) (base.getCalories() * factor));
        productNutritionFactsResponse.setProtein(base.getProtein().multiply(BigDecimal.valueOf(factor)));
        productNutritionFactsResponse.setFat(base.getFat().multiply(BigDecimal.valueOf(factor)));
        productNutritionFactsResponse.setCarbohydrates(base.getCarbohydrates().multiply(BigDecimal.valueOf(factor)));
        productNutritionFactsResponse.setSugar(base.getSugar().multiply(BigDecimal.valueOf(factor)));
        productNutritionFactsResponse.setFiber(base.getFiber().multiply(BigDecimal.valueOf(factor)));
        productNutritionFactsResponse.setSodium(base.getSodium().multiply(BigDecimal.valueOf(factor)));
        productNutritionFactsResponse.setVitaminC(base.getVitaminC().multiply(BigDecimal.valueOf(factor)));

        return productNutritionFactsResponse;
    }

    private int getCurrentStock(Integer productVariantId) {
        Map<Integer, List<StockHistory>> historyMap =
                productDao.getStockHistoryByProductVariantIds(List.of(productVariantId));

        List<StockHistory> historyList = historyMap.getOrDefault(productVariantId, List.of());

        return historyList.isEmpty() ? 0 : historyList.get(historyList.size() - 1).getStockAfter();
    }

    private StockHistoryResponse mapToStockHistoryResponse(StockHistory stockHistory) {
        StockHistoryResponse stockHistoryResponse = new StockHistoryResponse();
        stockHistoryResponse.setStockHistoryId(stockHistory.getStockHistoryId());
        stockHistoryResponse.setProductVariantId(stockHistory.getProductVariantId());
        stockHistoryResponse.setChangeAmount(stockHistory.getChangeAmount());
        stockHistoryResponse.setStockAfter(stockHistory.getStockAfter());
        stockHistoryResponse.setStockChangeReason(stockHistory.getStockChangeReason());
        stockHistoryResponse.setCreatedDate(stockHistory.getCreatedDate());
        stockHistoryResponse.setLastModifiedDate(stockHistory.getLastModifiedDate());
        return stockHistoryResponse;
    }

    private StockHistoryResponse recordStockChange(StockHistory stockHistory) {
        stockHistory.setCreatedDate(LocalDateTime.now());
        stockHistory.setLastModifiedDate(LocalDateTime.now());

        productDao.insertStockHistory(stockHistory);

        StockHistoryResponse response = new StockHistoryResponse();
        response.setProductVariantId(stockHistory.getProductVariantId());
        response.setChangeAmount(stockHistory.getChangeAmount());
        response.setStockAfter(stockHistory.getStockAfter());
        response.setStockChangeReason(stockHistory.getStockChangeReason());
        response.setCreatedDate(stockHistory.getCreatedDate());
        response.setLastModifiedDate(stockHistory.getLastModifiedDate());

        return response;
    }

    // 將前端傳的庫存變動原因字串，轉換成 enum
    private StockChangeReason mapReasonType(String reasonType) {
        if (reasonType == null) return StockChangeReason.MANUAL_ADJUST;

        return switch (reasonType.toUpperCase()) {
            case "RETURN" -> StockChangeReason.RETURN;
            case "PURCHASE" -> StockChangeReason.PURCHASE;
            case "DAMAGE" -> StockChangeReason.DAMAGE;
            case "PROMOTION" -> StockChangeReason.PROMOTION;
            default -> StockChangeReason.MANUAL_ADJUST;
        };
    }

}
