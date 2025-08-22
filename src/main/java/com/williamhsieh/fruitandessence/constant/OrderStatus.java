package com.williamhsieh.fruitandessence.constant;

public enum OrderStatus {

    PENDING, // 訂單建立(等待付款)
    PAID, // 已付款(等待出貨)
    PACKING, // 配貨中(正在打包水果
    SHIPPED, // 已出貨(物流配送中)
    DELIVERED, // 已送達
    COMPLETED, // 訂單完成(顧客確認收貨)
    CANCELLED, // 訂單取消
    REFUNDED // 已退款(如果取消或退貨成功)

}
