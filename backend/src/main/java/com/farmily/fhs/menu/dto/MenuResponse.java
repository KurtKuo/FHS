package com.farmily.fhs.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 餐點查詢回應物件
 * 用於前端顯示菜單項目的資料格式
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MenuResponse {

    /**
     * 餐點 ID
     */
    private Long id;

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
     * 分類代碼（例如：meal、beverage）
     */
    private String categoryCode;

    /**
     * 分類名稱（例如：餐點、飲料）— 提供前端顯示用
     */
    private String categoryName;
}