package com.williamhsieh.fruitandessence.dao;

import com.williamhsieh.fruitandessence.dto.ProductQueryParams;
import com.williamhsieh.fruitandessence.dto.ProductRequest;
import com.williamhsieh.fruitandessence.model.Product;
import com.williamhsieh.fruitandessence.rowmapper.ProductRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ProductDaoImpl  implements ProductDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    // 前端統一用駝峰呼叫，再轉底線命名，從資料庫撈取資料
    private String mapOrderByToColumn(String orderBy) {
        switch (orderBy) {
            case "pricePerUnit": return "price_per_unit";
            case "createdDate": return "created_date";
            case "lastModifiedDate": return "last_modified_date";
            case "stock": return "stock";
            default: return "product_name"; // 預設
        }
    }

    @Override
    public List<Product> getProducts(ProductQueryParams productQueryParams) {

        String sql = "SELECT product_id, product_name, category, image_url, " +
                     "price_per_unit, stock, unit, unit_type, weight, quantity, " +
                     "description, created_date, last_modified_date " +
                     "FROM product WHERE 1=1";
        Map<String, Object> map = new HashMap<>();

        if (productQueryParams.getCategory() != null) {
            sql = sql + " AND category = :category";
            map.put("category", productQueryParams.getCategory().name());
        }

        if (productQueryParams.getSearch() != null) {
            sql = sql + " AND product_name LIKE :search";
            map.put("search", "%" + productQueryParams.getSearch() + "%");
        }

        sql = sql + " ORDER BY " + mapOrderByToColumn(productQueryParams.getOrderBy()) + " " + productQueryParams.getSort();

        List<Product> productList = namedParameterJdbcTemplate.query(sql, map, new ProductRowMapper());

        return productList;
    }

    @Override
    public Product getProductById(Integer productId) {

        String sql = "SELECT product_id, product_name, category, image_url, " +
                     "price_per_unit, stock, unit, unit_type, weight, quantity, " +
                     "description, created_date, last_modified_date " +
                     "FROM product " +
                     "WHERE product_id = :productId";

        Map<String,Object> map = new HashMap<>();
        map.put("productId", productId);

        List<Product> productList = namedParameterJdbcTemplate.query(sql, map, new ProductRowMapper());

        if(productList.isEmpty()){
            return null;
        } else {
            return productList.get(0);
        }
    }

    @Override
    public Integer createProduct(ProductRequest productRequest) {

        String sql = "INSERT INTO product(product_name, category, image_url, " +
                     "price_per_unit, stock, unit, unit_type, weight, quantity, " +
                     "description, created_date, last_modified_date) " +
                     "VALUES (:product_name, :category, :image_url, " +
                     ":price_per_unit, :stock, :unit, :unit_type, :weight, :quantity, " +
                     ":description, :created_date, :last_modified_date) ";

        Map<String,Object> map = new HashMap<>();
        map.put("product_name", productRequest.getProductName());
        map.put("category", productRequest.getCategory().toString());
        map.put("image_url", productRequest.getImageUrl());
        map.put("price_per_unit", productRequest.getPricePerUnit());
        map.put("stock", productRequest.getStock());
        map.put("unit", productRequest.getUnit());
        map.put("unit_type", productRequest.getUnitType().toString());
        map.put("weight", productRequest.getWeight());
        map.put("quantity", productRequest.getQuantity());
        map.put("description", productRequest.getDescription());

        LocalDateTime now = LocalDateTime.now();
        map.put("created_date", now);
        map.put("last_modified_date", now);

        // 取得與儲存資料庫自動生成的 id
        KeyHolder keyHolder = new GeneratedKeyHolder();

        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);

        int productId = keyHolder.getKey().intValue();

        return productId;
    }


    @Override
    public void updateProduct(Integer productId, ProductRequest productRequest) {

        String sql = "UPDATE product SET product_name = :productName, category = :category, image_url = :imageUrl, " +
                     "price_per_unit = :pricePerUnit, stock = :stock, unit = :unit, unit_type = :unitType, weight = :weight, quantity = :quantity, " +
                     "description = :description, last_modified_date = :lastModifiedDate " +
                     "WHERE product_id = :productId";

        Map<String,Object> map = new HashMap<>();
        map.put("productId", productId);

        map.put("productName", productRequest.getProductName());
        map.put("category", productRequest.getCategory().toString());
        map.put("imageUrl", productRequest.getImageUrl());
        map.put("pricePerUnit", productRequest.getPricePerUnit());
        map.put("stock", productRequest.getStock());
        map.put("unit", productRequest.getUnit());
        map.put("unitType", productRequest.getUnitType().toString());
        map.put("weight", productRequest.getWeight());
        map.put("quantity", productRequest.getQuantity());
        map.put("description", productRequest.getDescription());

        map.put("lastModifiedDate", LocalDateTime.now());

        namedParameterJdbcTemplate.update(sql, map);


    }

    @Override
    public void deleteProductById(Integer productId) {

        String sql = "DELETE FROM product WHERE product_id = :productId";

        Map<String,Object> map = new HashMap<>();
        map.put("productId", productId);

        namedParameterJdbcTemplate.update(sql, map);
    }
}
