package com.farmily.fhs.auth.service;

/**
 * 使用者相關操作的服務介面，例如帳號刪除等。
 */
public interface UserService {

    /**
     * 根據使用者名稱刪除帳號。
     *
     * @param username 要刪除的使用者名稱
     */
    void deleteUser(String username);
}
