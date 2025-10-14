package com.williamhsieh.fruitandessence.rowmapper;

import com.williamhsieh.fruitandessence.constant.StockChangeReason;
import com.williamhsieh.fruitandessence.model.StockHistory;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StockHistoryRowMapper implements RowMapper<StockHistory> {

    @Override
    public StockHistory mapRow(ResultSet resultSet, int i) throws SQLException {

        StockHistory stockHistory = new StockHistory();
        stockHistory.setStockHistoryId(resultSet.getInt("stock_history_id"));
        stockHistory.setProductVariantId(resultSet.getInt("product_variant_id"));
        stockHistory.setChangeAmount(resultSet.getInt("change_amount"));
        stockHistory.setStockAfter(resultSet.getInt("stock_after"));
        stockHistory.setStockChangeReason(StockChangeReason.valueOf(resultSet.getString("reason")));
        stockHistory.setCreatedDate(resultSet.getTimestamp("created_date").toLocalDateTime());

        return stockHistory;

    }
}
