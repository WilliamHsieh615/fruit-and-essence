package com.williamhsieh.fruitandessence.constant;

public enum StockChangeReason {

    ORDER, // 出貨
    RETURN, // 退貨
    PURCHASE, // 進貨
    DAMAGE, // 過期
    PROMOTION, // 贈品
    INITIALIZATION, // 初始化
    MANUAL_ADJUST, // 人工調整
    INVENTORY_AUDIT, // 盤點調整
    LOSS // 遺失

}
