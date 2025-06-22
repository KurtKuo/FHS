package com.farmily.fhs.common.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 使用者帳號實體，對應資料表 users。
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

    /** 使用者主鍵 ID，自動遞增 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** 使用者帳號，唯一且不可為 null */
    @Column(unique = true, nullable = false)
    private String username;

    /** 加密後的密碼，不可為 null */
    @Column(nullable = false)
    private String password;

    /** 使用者電子郵件 */
    private String email;

    /** 使用者聯絡電話 */
    private String phone;

    /** 使用者角色代碼（1=匿名, 2=會員, 3=員工, 4=老闆） */
    @Column(name = "role")
    private Integer role;

    /** 帳號建立時間 */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /** 資料最後更新時間（自動更新） */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
