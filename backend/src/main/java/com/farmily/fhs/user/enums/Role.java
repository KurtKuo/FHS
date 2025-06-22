package com.farmily.fhs.user.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 系統使用者角色定義
 * 用於表示不同權限與身份類型，並對應資料庫角色代碼
 */
@Getter
@AllArgsConstructor
public enum Role {

    /**
     * 匿名使用者（未登入，訪客）
     */
    ANONYMOUS(1, "匿名使用者，未登入的訪客"),

    /**
     * 一般會員
     */
    MEMBER(2, "一般會員，擁有基本使用權限"),

    /**
     * 員工（工作管理員、後台操作人員）
     */
    STAFF(3, "員工，管理系統操作人員"),

    /**
     * 老闆（最高管理者，擁有最高權限）
     */
    BOSS(4, "老闆，系統最高管理者");

    // 欄位
    private final Integer code;
    private final String description;

    /**
     * 依代碼取得 Role Enum
     * @param code 欲查找的角色代碼
     * @return 對應的 Role，找不到回傳 null
     */
    public static Role fromCode(Integer code) {
        for (Role role : Role.values()) {
            if (role.code == code) {
                return role;
            }
        }
        return null;
    }
}