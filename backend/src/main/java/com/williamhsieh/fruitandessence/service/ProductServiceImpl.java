package com.williamhsieh.fruitandessence.service;

import com.williamhsieh.fruitandessence.constant.ProductSize;
import com.williamhsieh.fruitandessence.constant.StockChangeReason;
import com.williamhsieh.fruitandessence.dao.ProductDao;
import com.williamhsieh.fruitandessence.dto.*;
import com.williamhsieh.fruitandessence.model.Product;
import com.williamhsieh.fruitandessence.model.ProductNutritionFacts;
import com.williamhsieh.fruitandessence.model.ProductVariant;
import com.williamhsieh.fruitandessence.model.StockHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

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

        LocalDateTime now = LocalDateTime.now();

        Product product = new Product();
        product.setProductName(productRequest.getProductName());
        product.setProductCategory(productRequest.getProductCategory());
        product.setProductDescription(productRequest.getProductDescription());
        product.setCreatedDate(now);
        product.setLastModifiedDate(now);

        Integer productId = productDao.createProduct(product);

        List<ProductVariant> productVariantList = new ArrayList<>();
        List<ProductVariantRequest> productVariantRequestList = productRequest.getProductVariants();
        for (ProductVariantRequest productVariantRequest : productVariantRequestList) {
            ProductVariant productVariant = new ProductVariant();
            productVariant.setProductSize(productVariantRequest.getProductSize());
            productVariant.setPrice(productVariantRequest.getPrice());
            productVariant.setDiscountPrice(productVariantRequest.getDiscountPrice());
            productVariant.setUnit(productVariantRequest.getUnit());
            productVariant.setStock(productVariantRequest.getStock());
            productVariant.setSku(generateSku(product, productVariant));

            // 實際上線後可至 GS1 Taiwan（財團法人中華編碼中心）申請 條碼
            String barcodeText = generateBarcodeText(product, productVariant);
            String barcodeImageUrl = generateBarcodeImageUrl(barcodeText);

            productVariant.setBarcode(barcodeImageUrl); // 假條碼

            productVariantList.add(productVariant);
        }

        List<Integer> productVariantIdList = productDao.createProductVariants(productId, productVariantList);

        for (int i = 0; i < productVariantList.size(); i++) {
            ProductVariant productVariant = productVariantList.get(i);
            Integer productVariantId = productVariantIdList.get(i);

            if (productVariant.getStock() != null && productVariant.getStock() > 0) {
                StockHistory stockHistory = new StockHistory();
                stockHistory.setProductVariantId(productVariantId);
                stockHistory.setChangeAmount(productVariant.getStock()); // 初始庫存量
                stockHistory.setStockAfter(productVariant.getStock());
                stockHistory.setStockChangeReason(StockChangeReason.INITIALIZATION);
                stockHistory.setCreatedDate(now);

                productDao.insertStockHistory(stockHistory);
            }
        }

        if (productRequest.getProductNutritionFacts() != null) {

            ProductNutritionFactsRequest productNutritionFactsRequest = productRequest.getProductNutritionFacts();
            ProductNutritionFacts productNutritionFacts = new ProductNutritionFacts();

            productNutritionFacts.setProductId(productId);
            productNutritionFacts.setServingSize(productNutritionFactsRequest.getServingSize());
            productNutritionFacts.setCalories(productNutritionFactsRequest.getCalories());
            productNutritionFacts.setProtein(productNutritionFactsRequest.getProtein());
            productNutritionFacts.setFat(productNutritionFactsRequest.getFat());
            productNutritionFacts.setCarbohydrates(productNutritionFactsRequest.getCarbohydrates());
            productNutritionFacts.setSugar(productNutritionFactsRequest.getSugar());
            productNutritionFacts.setFiber(productNutritionFactsRequest.getFiber());
            productNutritionFacts.setSodium(productNutritionFactsRequest.getSodium());
            productNutritionFacts.setVitaminC(productNutritionFactsRequest.getVitaminC());

            productDao.createProductNutrition(productId, productNutritionFacts);
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

        List<Integer> ProductVariantIdList = List.of(stockHistoryRequest.getProductVariantId());
        Map<Integer, ProductVariant> productVariantMap = productDao.getVariantsByIds(ProductVariantIdList);
        ProductVariant productVariant = productVariantMap.get(stockHistoryRequest.getProductVariantId());

        int currentStock = getCurrentStock(stockHistoryRequest.getProductVariantId());
        int newStock = currentStock + stockHistoryRequest.getChangeAmount();

        productVariant.setStock(newStock);
        productDao.updateProductVariants(productVariant.getProductId(), List.of(productVariant));

        StockHistory stockHistory = new StockHistory();
        stockHistory.setProductVariantId(stockHistoryRequest.getProductVariantId());
        stockHistory.setChangeAmount(stockHistoryRequest.getChangeAmount());
        stockHistory.setStockAfter(newStock);
        stockHistory.setStockChangeReason(mapReasonType(stockHistoryRequest.getStockChangeReason().toString()));

        return recordStockChange(stockHistory);
    }

    @Override
    public StockHistoryResponse decreaseStock(StockHistoryRequest stockHistoryRequest) {

        List<Integer> ProductVariantIdList = List.of(stockHistoryRequest.getProductVariantId());
        Map<Integer, ProductVariant> productVariantMap = productDao.getVariantsByIds(ProductVariantIdList);
        ProductVariant productVariant = productVariantMap.get(stockHistoryRequest.getProductVariantId());

        int currentStock = getCurrentStock(stockHistoryRequest.getProductVariantId());
        int newStock = currentStock - stockHistoryRequest.getChangeAmount();
        if (newStock < 0) {
            throw new IllegalArgumentException("stock cannot be negative");
        }

        productVariant.setStock(newStock);
        productDao.updateProductVariants(productVariant.getProductId(), List.of(productVariant));

        StockHistory stockHistory = new StockHistory();
        stockHistory.setProductVariantId(stockHistoryRequest.getProductVariantId());
        stockHistory.setChangeAmount(-Math.abs(stockHistoryRequest.getChangeAmount()));
        stockHistory.setStockAfter(newStock);
        stockHistory.setStockChangeReason(mapReasonType(stockHistoryRequest.getStockChangeReason().toString()));

        return recordStockChange(stockHistory);
    }

    @Override
    public StockHistoryResponse adjustStock(StockHistoryRequest stockHistoryRequest) {

        List<Integer> ProductVariantIdList = List.of(stockHistoryRequest.getProductVariantId());
        Map<Integer, ProductVariant> productVariantMap = productDao.getVariantsByIds(ProductVariantIdList);
        ProductVariant productVariant = productVariantMap.get(stockHistoryRequest.getProductVariantId());

        int newStock = stockHistoryRequest.getChangeAmount();

        productVariant.setStock(newStock);
        productDao.updateProductVariants(productVariant.getProductId(), List.of(productVariant));

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

        LocalDateTime now = LocalDateTime.now();

        Product product = productDao.getProductById(productId);
        product.setProductName(productRequest.getProductName());
        product.setProductCategory(productRequest.getProductCategory());
        product.setProductDescription(productRequest.getProductDescription());
        product.setLastModifiedDate(now);

        productDao.updateProduct(productId, product);

        List<Integer> variantIds = productRequest.getProductVariants()
                .stream()
                .map(ProductVariantRequest::getProductVariantId)
                .toList();

        Map<Integer, ProductVariant> oldProductVariantMap = productDao.getVariantsByIds(variantIds);

        List<ProductVariantRequest> productVariantRequestList = productRequest.getProductVariants();
        List<ProductVariant> newProductVariantList = new ArrayList<>();
        for (ProductVariantRequest productVariantRequest : productVariantRequestList) {

            ProductVariant productVariant = oldProductVariantMap.get(productVariantRequest.getProductVariantId());
            productVariant.setProductSize(productVariantRequest.getProductSize());
            productVariant.setPrice(productVariantRequest.getPrice());
            productVariant.setDiscountPrice(productVariantRequest.getDiscountPrice());
            productVariant.setUnit(productVariantRequest.getUnit());
            productVariant.setStock(productVariantRequest.getStock());

            newProductVariantList.add(productVariant);

            ProductVariant oldProductVariant = oldProductVariantMap.get(productVariant.getProductVariantId());
            Integer oldStock = oldProductVariant.getStock();
            Integer newStock = productVariant.getStock();

            List<StockHistory> stockHistoryList = productDao.getStockHistoryByProductVariantId(productVariant.getProductVariantId());
            StockHistory lastStockHistory = stockHistoryList.get(stockHistoryList.size() - 1);

            if (!oldStock.equals(lastStockHistory.getStockAfter())) {
                log.warn("Stock mismatch for variantId={}, DB stock={}, history stockAfter={}",
                        productVariant.getProductVariantId(), oldStock, lastStockHistory.getStockAfter());
            }

            if (newStock == null) {
                throw new IllegalArgumentException("Stock cannot be null for variantId=" + productVariantRequest.getProductVariantId());
            }
            if (newStock < 0) {
                throw new IllegalArgumentException("Stock cannot be negative for variantId=" + productVariantRequest.getProductVariantId());
            }

            if (newStock - oldStock != 0) {
                StockHistory stockHistory = new StockHistory();
                stockHistory.setProductVariantId(productVariantRequest.getProductVariantId());
                stockHistory.setChangeAmount(newStock - oldStock);
                stockHistory.setStockAfter(productVariant.getStock());
                stockHistory.setStockChangeReason(StockChangeReason.MANUAL_ADJUST);
                stockHistory.setCreatedDate(now);
                productDao.insertStockHistory(stockHistory);
            }
        }

        productDao.updateProductVariants(productId, newProductVariantList);


        if (productRequest.getProductNutritionFacts() != null) {

            ProductNutritionFactsRequest productNutritionFactsRequest = productRequest.getProductNutritionFacts();
            ProductNutritionFacts productNutritionFacts = productDao.getNutritionByProductId(product.getProductId());

            productNutritionFacts.setServingSize(productNutritionFactsRequest.getServingSize());
            productNutritionFacts.setCalories(productNutritionFactsRequest.getCalories());
            productNutritionFacts.setProtein(productNutritionFactsRequest.getProtein());
            productNutritionFacts.setFat(productNutritionFactsRequest.getFat());
            productNutritionFacts.setCarbohydrates(productNutritionFactsRequest.getCarbohydrates());
            productNutritionFacts.setSugar(productNutritionFactsRequest.getSugar());
            productNutritionFacts.setFiber(productNutritionFactsRequest.getFiber());
            productNutritionFacts.setSodium(productNutritionFactsRequest.getSodium());
            productNutritionFacts.setVitaminC(productNutritionFactsRequest.getVitaminC());

            productDao.updateProductNutrition(productId, productNutritionFacts);
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
                    productVariantResponse.setProductSizeLabel(variant.getProductSize().getLabel());
                    productVariantResponse.setProductSizeFluidOunce(variant.getProductSize().getFluidOunce());
                    productVariantResponse.setPrice(variant.getPrice());
                    productVariantResponse.setDiscountPrice(variant.getDiscountPrice());
                    productVariantResponse.setStock(variant.getStock());
                    productVariantResponse.setUnit(variant.getUnit());
                    productVariantResponse.setSku(variant.getSku());
                    productVariantResponse.setBarcode(variant.getBarcode());

                    // 比對 productVariant stock 與 最後一筆 stockHistory 有沒有一致
                    List<StockHistory> stockHistoryList = stockHistoryMap.getOrDefault(variant.getProductVariantId(), List.of());
                    if (stockHistoryList == null || stockHistoryList.isEmpty()) {
                        productVariantResponse.setStock(variant.getStock());
                    } else {
                        // 取最後一筆歷史紀錄
                        StockHistory lastStock = stockHistoryList.get(stockHistoryList.size() - 1);

                        // 比對是否一致
                        if (!variant.getStock().equals(lastStock.getStockAfter())) {
                            log.error("Stock mismatch for variantId={}, DB stock={}, history stockAfter={}",
                                    variant.getProductVariantId(),
                                    variant.getStock(),
                                    lastStock.getStockAfter());
                        }

                        productVariantResponse.setStock(variant.getStock());
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
        return stockHistoryResponse;
    }

    private StockHistoryResponse recordStockChange(StockHistory stockHistory) {

        stockHistory.setCreatedDate(LocalDateTime.now());

        productDao.insertStockHistory(stockHistory);

        StockHistoryResponse stockHistoryResponse = new StockHistoryResponse();
        stockHistoryResponse.setProductVariantId(stockHistory.getProductVariantId());
        stockHistoryResponse.setChangeAmount(stockHistory.getChangeAmount());
        stockHistoryResponse.setStockAfter(stockHistory.getStockAfter());
        stockHistoryResponse.setStockChangeReason(stockHistory.getStockChangeReason());
        stockHistoryResponse.setCreatedDate(stockHistory.getCreatedDate());

        return stockHistoryResponse;
    }

    // 將前端傳的庫存變動原因字串，轉換成 enum
    private StockChangeReason mapReasonType(String reasonType) {
        if (reasonType == null) return StockChangeReason.MANUAL_ADJUST;

        return switch (reasonType.toUpperCase()) {
            case "ORDER" -> StockChangeReason.ORDER;
            case "RETURN" -> StockChangeReason.RETURN;
            case "PURCHASE" -> StockChangeReason.PURCHASE;
            case "DAMAGE" -> StockChangeReason.DAMAGE;
            case "PROMOTION" -> StockChangeReason.PROMOTION;
            case "INITIALIZATION" -> StockChangeReason.INITIALIZATION;
            case "INVENTORY_AUDIT" -> StockChangeReason.INVENTORY_AUDIT;
            case "LOSS" -> StockChangeReason.LOSS;
            default -> StockChangeReason.MANUAL_ADJUST;
        };
    }

    private String generateSku(Product product, ProductVariant productVariant) {

        String skuProductCategory = "";
        String skuProductCategoryNumber = "";
        switch (product.getProductCategory().toString()) {
            case "REFRESHING":
                skuProductCategory = "REF";
                skuProductCategoryNumber = "01";
                break;
            case "SWEET_AND_FRUITY":
                skuProductCategory = "SF";
                skuProductCategoryNumber = "02";
                break;
            case "SUPERFOODS":
                skuProductCategory = "SFDS";
                skuProductCategoryNumber = "03";
                break;
            case "HEALTHY_VEGGIES":
                skuProductCategory = "HVG";
                skuProductCategoryNumber = "04";
                break;
            case "WELLNESS_AND_HERBAL":
                skuProductCategory = "WHB";
                skuProductCategoryNumber = "05";
                break;
        }

        String skuProductName = product.getProductName().replaceAll("\\s+", "").toUpperCase();
        String skuProductNameNumber = String.format("%06d", Math.abs(product.getProductName().hashCode()) % 1000000);

        String skuProductSize = "";
        String skuProductSizeNumber = "";

        switch (productVariant.getProductSize().toString()) {
            case "SMALL_300ML":
                skuProductSize = "S";
                skuProductSizeNumber = "01";
                break;
            case "MEDIUM_700ML":
                skuProductSize = "M";
                skuProductSizeNumber = "02";
                break;
            case "LARGE_1900ML":
                skuProductSize = "L";
                skuProductSizeNumber = "03";
                break;
        }

        // 商品版本
        String skuProductVersion = "V01";

        String sku = skuProductCategory + "-" +
                skuProductName + "-" +
                skuProductSize + "-" +
                skuProductCategoryNumber + "-" +
                skuProductNameNumber + "-" +
                skuProductSizeNumber + "-" +
                skuProductVersion;

        return sku;
    }

    private String generateBarcodeText(Product product, ProductVariant variant) {
        // 去除空白並轉大寫作為基底
        String base = product.getProductName().replaceAll("\\s+", "").toUpperCase();

        // 產生前 9 碼的 hash（ 限制 9 碼 ）
        String hash = String.format("%09d", Math.abs(base.hashCode()) % 1000000000);

        // 前 3 碼固定為台灣國碼（ 471 ）
        String partialCode = "471" + hash; // 共 12 碼，最後 1 碼要算檢查碼

        // 計算檢查碼
        int checkDigit = calculateEan13CheckDigit(partialCode);

        // 完整條碼（ 13 碼 ）
        return partialCode + checkDigit;
    }

    private int calculateEan13CheckDigit(String code12) {
        if (code12 == null || code12.length() != 12 || !code12.matches("\\d+")) {
            throw new IllegalArgumentException("Invalid 12-digit EAN base code: " + code12);
        }

        int sum = 0;
        for (int i = 0; i < code12.length(); i++) {
            int digit = Character.getNumericValue(code12.charAt(i));
            if ((i % 2) == 0) {
                sum += digit;
            } else {
                sum += digit * 3;
            }
        }

        int remainder = sum % 10;
        return (remainder == 0) ? 0 : 10 - remainder;
    }

    private String generateBarcodeImageUrl(String barcodeText) {
        return "https://barcode.tec-it.com/barcode.ashx?data="
                + barcodeText + "&code=EAN13&translate-esc=true";
    }
}
