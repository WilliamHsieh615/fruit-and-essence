package com.williamhsieh.fruitandessence.dao;

import com.williamhsieh.fruitandessence.dto.ProductNutritionFactsRequest;
import com.williamhsieh.fruitandessence.dto.ProductQueryParams;
import com.williamhsieh.fruitandessence.dto.ProductRequest;
import com.williamhsieh.fruitandessence.dto.ProductVariantRequest;
import com.williamhsieh.fruitandessence.model.Product;
import com.williamhsieh.fruitandessence.model.ProductNutritionFacts;
import com.williamhsieh.fruitandessence.model.ProductVariant;
import com.williamhsieh.fruitandessence.model.StockHistory;
import com.williamhsieh.fruitandessence.rowmapper.ProductNutritionFactsRowMapper;
import com.williamhsieh.fruitandessence.rowmapper.ProductRowMapper;
import com.williamhsieh.fruitandessence.rowmapper.ProductVariantRowMapper;
import com.williamhsieh.fruitandessence.rowmapper.StockHistoryRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ProductDaoImpl implements ProductDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Integer countProduct(ProductQueryParams productQueryParams) {
        String sql = "SELECT count(*) FROM product WHERE 1=1";

        Map<String, Object> map = new HashMap<>();

        // 查詢條件
        sql = addFilteringSql(sql, map, productQueryParams);

        Integer total = namedParameterJdbcTemplate.queryForObject(sql, map, Integer.class);

        return total;
    }

    @Override
    public List<Product> getProducts(ProductQueryParams productQueryParams) {

        String sql = "SELECT p.product_id, p.product_name, p.product_category, " +
                "p.product_description, p.created_date, p.last_modified_date, MIN(pv.price) AS min_price " +
                "FROM product p LEFT JOIN product_variant pv ON p.product_id = pv.product_id WHERE 1=1";

        Map<String, Object> map = new HashMap<>();

        // 查詢條件
        sql = addFilteringSql(sql, map, productQueryParams);

        // 分組
        sql = sql + " GROUP BY p.product_id, p.product_name, p.product_category, p.product_description, p.created_date, p.last_modified_date";

        // 排序
        sql = sql + " ORDER BY " + mapOrderByToColumn(productQueryParams.getOrderBy()) + " " + productQueryParams.getSort();

        // 分頁
        sql = sql + " LIMIT :limit OFFSET :offset";

        map.put("limit", productQueryParams.getLimit());
        map.put("offset", productQueryParams.getOffset());

        List<Product> productList = namedParameterJdbcTemplate.query(sql, map, new ProductRowMapper());

        if (productList.isEmpty()) {
            return Collections.emptyList();
        } else {
            return productList;
        }
    }

    @Override
    public Product getProductById(Integer productId) {

        String sql = "SELECT product_id, product_name, product_category, product_description, " +
                "created_date, last_modified_date " +
                "FROM product WHERE product_id = :productId";

        Map<String, Object> map = new HashMap<>();
        map.put("productId", productId);

        List<Product> productList = namedParameterJdbcTemplate.query(sql, map, new ProductRowMapper());

        if (productList.isEmpty()) {
            return null;
        } else {
            return productList.get(0);
        }

    }

    @Override
    public List<Product> getProductsByIds(List<Integer> productIds) {
        String sql = "SELECT product_id, product_name, product_category, product_description, " +
                "created_date, last_modified_date " +
                "FROM product WHERE product_id IN (:productIds)";

        Map<String, Object> map = new HashMap<>();
        map.put("productIds", productIds);

        List<Product> productList = namedParameterJdbcTemplate.query(sql, map, new ProductRowMapper());

        if (productList.isEmpty()) {
            return Collections.emptyList();
        } else {
            return productList;
        }
    }

    @Override
    public List<ProductVariant> getVariantsByIds(List<Integer> productVariantIds) {

        String sql = "SELECT product_variant_id, product_id, product_size, price, discount_price, unit, " +
                "stock, sku, barcode " +
                "FROM product_variant WHERE product_variant_id IN (:productVariantIds)";

        Map<String, Object> map = new HashMap<>();
        map.put("productVariantIds", productVariantIds);

        List<ProductVariant>  productVariantList = namedParameterJdbcTemplate.query(sql, map, new ProductVariantRowMapper());

        if (productVariantList.isEmpty()) {
            return Collections.emptyList();
        } else  {
            return productVariantList;
        }
    }

    @Override
    public Map<Integer, List<ProductVariant>> getVariantsByProductIds(List<Integer> productIds) {

        String sql = "SELECT product_variant_id, product_id, product_size, price, discount_price, unit, " +
                "stock, sku, barcode " +
                "FROM product_variant WHERE product_id IN (:productIds)";

        Map<String, Object> map = new HashMap<>();
        map.put("productIds", productIds);

        List<ProductVariant> productVariantList = namedParameterJdbcTemplate.query(sql, map, new ProductVariantRowMapper());

        Map<Integer, List<ProductVariant>> productVariantListMap = productVariantList.stream().collect(Collectors.groupingBy(ProductVariant::getProductId));

        if (productVariantList.isEmpty()) {
            return Map.of();
        } else {
            return productVariantListMap;
        }

    }

    @Override
    public Map<Integer, ProductNutritionFacts> getNutritionByProductIds(List<Integer> productIds) {

        String sql = "SELECT product_id, calories, protein, fat, carbohydrates, sugar, fiber, sodium, vitamin_c " +
                "FROM product_nutrition_facts WHERE product_id IN (:productIds)";

        Map<String, Object> map = new HashMap<>();
        map.put("productIds", productIds);

        List<ProductNutritionFacts> productNutritionFactsList = namedParameterJdbcTemplate.query(sql, map, new ProductNutritionFactsRowMapper());

        Map<Integer, ProductNutritionFacts> productNutritionFactsListMap = productNutritionFactsList.stream().collect(Collectors.toMap(ProductNutritionFacts::getProductId, nf -> nf));

        if (productNutritionFactsList.isEmpty()) {
            return Map.of();
        } else {
            return productNutritionFactsListMap;
        }
    }

    @Override
    public Map<Integer, List<String>> getImagesByProductIds(List<Integer> productIds) {

        String sql = "SELECT product_id, image_url FROM product_images WHERE product_id IN (:productIds)";

        Map<String, Object> map = new HashMap<>();
        map.put("productIds", productIds);

        List<Map.Entry<Integer, String>> productImageList = namedParameterJdbcTemplate.query(sql, map, (resultSet, i) -> Map.entry(
                        resultSet.getInt("product_id"),
                        resultSet.getString("image_url")
                )
        );

        Map<Integer, List<String>> productImageListMap = productImageList.stream()
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                ));

        if (productImageList.isEmpty()) {
            return Map.of();
        } else {
            return productImageListMap;
        }
    }

    @Override
    public Map<Integer, List<String>> getIngredientByProductIds(List<Integer> productIds) {

        String sql = "SELECT product_id, ingredient_name FROM product_ingredient WHERE product_id IN (:productIds)";

        Map<String, Object> map = new HashMap<>();
        map.put("productIds", productIds);

        List<Map.Entry<Integer, String>> productIngredientList = namedParameterJdbcTemplate.query(sql, map, (resultSet, i) -> Map.entry(
                        resultSet.getInt("product_id"),
                        resultSet.getString("ingredient_name")
                )
        );

        Map<Integer, List<String>> productIngredientListMap = productIngredientList.stream()
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                ));

        if (productIngredientList.isEmpty()) {
            return Map.of();
        } else {
            return productIngredientListMap;
        }
    }

    @Override
    public List<StockHistory> getStockHistoryByProductVariantId(Integer productVariantId) {

        String sql = "stock_history_id, product_variant_id, change_amount, stock_after, reason, " +
                "created_date, last_modified_date " +
                "FROM stock_history WHERE product_variant_id = :productVariantId " +
                "ORDER BY created_date ASC";

        Map<String, Object> map = new HashMap<>();
        map.put("productVariantId", productVariantId);

        List<StockHistory> stockHistoryList = namedParameterJdbcTemplate.query(sql, map, new StockHistoryRowMapper());

        return stockHistoryList;
    }

    @Override
    public Map<Integer, List<StockHistory>> getStockHistoryByProductVariantIds(List<Integer> productVariantIds) {

        String sql = "SELECT stock_history_id ,product_variant_id, change_amount, stock_after , reason, created_date, last_modified_date " +
                "FROM stock_history WHERE product_variant_id IN (:productVariantIds) " +
                "ORDER BY product_variant_id, last_modified_date DESC";

        Map<String, Object> map = new HashMap<>();
        map.put("productVariantIds", productVariantIds);

        List<StockHistory> stockHistoryList = namedParameterJdbcTemplate.query(sql, map, new StockHistoryRowMapper());

        Map<Integer, List<StockHistory>> stockHistoryListMap = stockHistoryList.stream().collect(Collectors.groupingBy(StockHistory::getProductVariantId));

        return stockHistoryListMap;
    }

    @Override
    public Integer createProduct(Product product) {

        String sql = "INSERT INTO product(product_name, product_category, product_description, " +
                "created_date, last_modified_date) " +
                "VALUES (:productName, :productCategory, :productDescription, " +
                ":createdDate, :lastModifiedDate) ";

        Map<String, Object> map = new HashMap<>();
        map.put("productName", product.getProductName());
        map.put("productCategory", product.getProductCategory().toString());
        map.put("productDescription", product.getProductDescription());

        LocalDateTime now = LocalDateTime.now();
        map.put("createdDate", now);
        map.put("lastModifiedDate", now);

        // 取得與儲存資料庫自動生成的 id
        KeyHolder keyHolder = new GeneratedKeyHolder();

        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);

        int productId = keyHolder.getKey().intValue();

        return productId;
    }

    @Override
    public List<Integer> createProductVariants(Integer productId, List<ProductVariant> productVariantList) {

        String sql = "INSERT INTO product_variant (product_id, product_size, price, discount_price, unit, stock, sku, barcode) " +
                "VALUES (:productId, :productSize, :price, :discountPrice, :unit, :stock, :sku, :barcode) ";

        List<Integer> productVariantIdList = new ArrayList<>();

        for (ProductVariant productVariant : productVariantList) {
            Map<String, Object> map = new HashMap<>();
            map.put("productId", productId);
            map.put("productSize", productVariant.getProductSize().toString());
            map.put("price", productVariant.getPrice());
            map.put("discountPrice", productVariant.getDiscountPrice());
            map.put("unit", productVariant.getUnit());
            map.put("stock", productVariant.getStock());
            map.put("sku", productVariant.getSku());
            map.put("barcode", productVariant.getBarcode());

            KeyHolder keyHolder = new GeneratedKeyHolder();
            namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);
            productVariantIdList.add(keyHolder.getKey().intValue());
        }

        return productVariantIdList;
    }

    @Override
    public void createProductNutrition(Integer productId, ProductNutritionFacts productNutritionFacts) {

        String sql = "INSERT INTO product_nutrition_facts (product_id, serving_size, calories, " +
                "protein, fat, carbohydrates, sugar, fiber, sodium, vitaminC) " +
                "VALUES (:productId, :servingSize, :calories, " +
                ":protein, :fat, :carbohydrates, :sugar, :fiber, :sodium, :vitaminC) ";

        Map<String, Object> map = new HashMap<>();
        map.put("productId", productId);
        map.put("servingSize", productNutritionFacts.getServingSize());
        map.put("calories", productNutritionFacts.getCalories());
        map.put("protein", productNutritionFacts.getProtein());
        map.put("fat", productNutritionFacts.getFat());
        map.put("carbohydrates", productNutritionFacts.getCarbohydrates());
        map.put("sugar", productNutritionFacts.getSugar());
        map.put("fiber", productNutritionFacts.getFiber());
        map.put("sodium", productNutritionFacts.getSodium());
        map.put("vitaminC", productNutritionFacts.getVitaminC());

        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map));

    }

    @Override
    public void createProductImages(Integer productId, List<String> images) {

        String sql = "INSERT INTO product_images (product_id, image_url) " +
                "VALUES (:productId, :imageUrl)";

        List<Map<String, Object>> mapArrayList = new ArrayList<>();
        for (String image : images) {
            Map<String, Object> map = new HashMap<>();
            map.put("productId", productId);
            map.put("imageUrl", image);
            mapArrayList.add(map);
        }

        namedParameterJdbcTemplate.batchUpdate(sql, mapArrayList.toArray(new Map[0]));
    }

    @Override
    public void createProductIngredients(Integer productId, List<String> ingredients) {

        String sql = "INSERT INTO product_ingredient (product_id, ingredient_name) " +
                "VALUES (:productId, :ingredientName)";

        List<Map<String, Object>> mapArrayList = new ArrayList<>();
        for (String ingredient : ingredients) {
            Map<String, Object> map = new HashMap<>();
            map.put("productId", productId);
            map.put("ingredientName", ingredient);
            mapArrayList.add(map);
        }

        namedParameterJdbcTemplate.batchUpdate(sql, mapArrayList.toArray(new Map[0]));
    }

    @Override
    public void updateProduct(Integer productId, Product product) {

        String sql = "UPDATE product SET product_name = :productName, product_category = :productCategory, " +
                "product_description = :productDescription, last_modified_date = :lastModifiedDate " +
                "WHERE product_id = :productId";

        Map<String, Object> map = new HashMap<>();
        map.put("productId", productId);

        map.put("productName", product.getProductName());
        map.put("productCategory", product.getProductCategory().toString());
        map.put("productDescription", product.getProductDescription());

        map.put("lastModifiedDate", LocalDateTime.now());

        namedParameterJdbcTemplate.update(sql, map);

    }

    @Override
    public void updateProductVariants(Integer productId, List<ProductVariant> productVariantList) {

        List<Map<String, Object>> mapArrayList = new ArrayList<>();
        String sqlInsert = "INSERT INTO product_variant (product_id, product_size, price, discount_price, unit, stock, sku, barcode) " +
                "VALUES (:productId, :productSize, :price, :discountPrice, :unit, :stock, :sku, :barcode)";

        String sqlUpdate = "UPDATE product_variant SET product_size = :productSize, price = :price, discount_price = :discountPrice, " +
                "unit = :unit, stock = :stock, sku = :sku, barcode = :barcode WHERE product_variant_id = :productVariantId";

        for (ProductVariant productVariant : productVariantList) {
            Map<String, Object> map = new HashMap<>();
            if (productVariant.getProductVariantId() != null) {
                // update
                map.put("productVariantId", productVariant.getProductVariantId());
                map.put("productSize", productVariant.getProductSize().toString());
                map.put("price", productVariant.getPrice());
                map.put("discountPrice", productVariant.getDiscountPrice());
                map.put("unit", productVariant.getUnit());
                map.put("stock", productVariant.getStock());
                map.put("sku", productVariant.getSku());
                map.put("barcode", productVariant.getBarcode());
                namedParameterJdbcTemplate.update(sqlUpdate, map);
            } else {
                // insert
                map.put("productId", productId);
                map.put("productSize", productVariant.getProductSize().toString());
                map.put("price", productVariant.getPrice());
                map.put("discountPrice", productVariant.getDiscountPrice());
                map.put("unit", productVariant.getUnit());
                map.put("stock", productVariant.getStock());
                map.put("sku", productVariant.getSku());
                map.put("barcode", productVariant.getBarcode());
                mapArrayList.add(map);
            }
        }

        if (!mapArrayList.isEmpty()) {
            namedParameterJdbcTemplate.batchUpdate(sqlInsert, mapArrayList.toArray(new Map[0]));
        }
    }

    @Override
    public void updateProductNutrition(Integer productId, ProductNutritionFacts productNutritionFacts) {

        String sql = "UPDATE product_nutrition_facts SET serving_size = :servingSize, calories = :calories, " +
                "protein = :protein, fat = :fat, carbohydrates = :carbohydrates, " +
                "sugar = :sugar, fiber = :fiber, sodium = :sodium, vitaminC = :vitaminC " +
                "WHERE product_id = :productId";

        Map<String, Object> map = new HashMap<>();
        map.put("productId", productId);
        map.put("servingSize", productNutritionFacts.getServingSize());
        map.put("calories", productNutritionFacts.getCalories());
        map.put("protein", productNutritionFacts.getProtein());
        map.put("fat", productNutritionFacts.getFat());
        map.put("carbohydrates", productNutritionFacts.getCarbohydrates());
        map.put("sugar", productNutritionFacts.getSugar());
        map.put("fiber", productNutritionFacts.getFiber());
        map.put("sodium", productNutritionFacts.getSodium());
        map.put("vitaminC", productNutritionFacts.getVitaminC());

        namedParameterJdbcTemplate.update(sql, map);

    }

    @Override
    public void updateProductImages(Integer productId, List<String> images) {

        String sqlDelete = "DELETE FROM product_images WHERE product_id = :productId";
        namedParameterJdbcTemplate.update(sqlDelete, new MapSqlParameterSource("productId", productId));

        String sqlInsert = "INSERT INTO product_images (product_id, image_url) VALUES (:productId, :imageUrl)";
        List<Map<String, Object>> mapArrayList = new ArrayList<>();
        for (String image : images) {
            Map<String, Object> map = new HashMap<>();
            map.put("productId", productId);
            map.put("imageUrl", image);
            mapArrayList.add(map);
        }

        namedParameterJdbcTemplate.batchUpdate(sqlInsert, mapArrayList.toArray(new Map[0]));
    }

    @Override
    public void updateProductIngredients(Integer productId, List<String> ingredients) {

        String sqlDelete = "DELETE FROM product_ingredient WHERE product_id = :productId";
        namedParameterJdbcTemplate.update(sqlDelete, new MapSqlParameterSource("productId", productId));

        String sqlInsert = "INSERT INTO product_ingredient (product_id, ingredient_name) VALUES (:productId, :ingredientName)";
        List<Map<String, Object>> mapArrayList = new ArrayList<>();
        for (String ingredient : ingredients) {
            Map<String, Object> map = new HashMap<>();
            map.put("productId", productId);
            map.put("ingredientName", ingredient);
            mapArrayList.add(map);
        }

        namedParameterJdbcTemplate.batchUpdate(sqlInsert, mapArrayList.toArray(new Map[0]));

    }

    @Override
    public void insertStockHistory(StockHistory stockHistory) {

        String sql = "INSERT INTO stock_history (product_variant_id, change_amount, stock_after, reason, created_date, last_modified_date) " +
                "VALUES (:productVariantId, :changeAmount, :stockAfter, :reason, :createdDate, :lastModifiedDate)";

        Map<String, Object> map = new HashMap<>();
        map.put("productVariantId", stockHistory.getProductVariantId());
        map.put("changeAmount", stockHistory.getChangeAmount());
        map.put("stockAfter", stockHistory.getStockAfter());
        map.put("reason", stockHistory.getStockChangeReason().toString());

        map.put("createdDate", stockHistory.getCreatedDate());
        map.put("lastModifiedDate", LocalDateTime.now());

        namedParameterJdbcTemplate.update(sql, map);

    }

    @Override
    public void deleteProductById(Integer productId) {

        String sql = "DELETE FROM product WHERE product_id = :productId";

        Map<String, Object> map = new HashMap<>();
        map.put("productId", productId);

        namedParameterJdbcTemplate.update(sql, map);
    }

    @Override
    public void deleteProductVariantsByProductId(Integer productId) {

        String sql = "DELETE FROM product_variant WHERE product_id = :productId";
        namedParameterJdbcTemplate.update(sql, Map.of("productId", productId));
    }

    @Override
    public void deleteProductNutritionByProductId(Integer productId) {

        String sql = "DELETE FROM product_nutrition_facts WHERE product_id = :productId";
        namedParameterJdbcTemplate.update(sql, Map.of("productId", productId));
    }

    @Override
    public void deleteProductImagesByProductId(Integer productId) {

        String sql = "DELETE FROM product_image WHERE product_id = :productId";
        namedParameterJdbcTemplate.update(sql, Map.of("productId", productId));
    }

    @Override
    public void deleteProductIngredientsByProductId(Integer productId) {

        String sql = "DELETE FROM product_ingredient WHERE product_id = :productId";
        namedParameterJdbcTemplate.update(sql, Map.of("productId", productId));
    }

    @Override
    public void deleteStockByProductVariantIds(List<Integer> productVariantIds) {

        String sql = "DELETE FROM stock_history WHERE product_variant_id IN (:productVariantIds)";
        namedParameterJdbcTemplate.update(sql, Map.of("productVariantIds", productVariantIds));
    }

    // 前端統一用駝峰呼叫，再轉底線命名，從資料庫撈取資料
    private String mapOrderByToColumn(String orderBy) {
        switch (orderBy) {
            case "price":
                return "min_price";
            case "category":
                return "product_category";
            case "createdDate":
                return "created_date";
            case "lastModifiedDate":
                return "last_modified_date";
            default:
                return "product_name"; // 預設
        }
    }

    private String addFilteringSql(String sql, Map<String, Object> map, ProductQueryParams productQueryParams) {

        // 查詢條件
        if (productQueryParams.getCategory() != null) {
            sql = sql + " AND product_category = :category";
            map.put("category", productQueryParams.getCategory().name());
        }

        if (productQueryParams.getSearch() != null) {
            sql = sql + " AND product_name LIKE :search";
            map.put("search", "%" + productQueryParams.getSearch() + "%");
        }

        return sql;
    }

}
