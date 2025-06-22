package com.farmily.fhs.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 餐點建立或修改請求物件
 * 用於新增或編輯菜單項目時，前端送出的資料格式
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MenuRequest {

    /**
     * 餐點名稱
     */
    private String name;

    /**
     * 餐點描述
     */
    private String description;

    /**
     * 價格
     */
    private BigDecimal price;

    /**
     * 單位（例如：份、杯、瓶）
     */
    private String unit;

    /**
     * 圖片網址
     */
    private String imageUrl;

    /**
     * 分類代碼（前端傳送的分類標識，例如：meal、beverage 等）
     */
    private String categoryCode;
}