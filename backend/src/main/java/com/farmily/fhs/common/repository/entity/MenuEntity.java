package com.farmily.fhs.common.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 菜單項目實體，對應資料表 menu_items。
 */
@Entity
@Table(name = "menu_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuEntity {

    /** 菜單項目 ID，自動遞增主鍵 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 餐點名稱，不可為 null */
    @Column(nullable = false)
    private String name;

    /** 餐點描述，使用 TEXT 儲存長文字 */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** 餐點價格，精確到小數點後兩位 */
    @Column(name = "price", precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    /** 餐點分類（如開胃菜、主餐、飲料等） */
    private String category;

    /** 是否供應中，true 表示可點餐 */
    @Column(name = "is_available")
    private Boolean available;

    /** 餐點圖片的 URL 路徑 */
    @Column(name = "image_url")
    private String imageUrl;
}
