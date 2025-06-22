package com.farmily.fhs.menu.service.impl;

import com.farmily.fhs.common.repository.MenuRepository;
import com.farmily.fhs.common.repository.entity.MenuEntity;
import com.farmily.fhs.menu.service.MenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 菜單服務實作類，負責處理菜單相關的資料庫存取與業務邏輯。
 */
@Service
@Slf4j // 啟用 Lombok SLF4J 日誌功能，會自動產生 private static final Logger log
public class MenuServiceImpl implements MenuService {

    /** 注入菜單資料存取物件 */
    @Autowired
    private MenuRepository menuRepository;

    /**
     * 根據 ID 查詢菜單項目
     * @param id 菜單項目 ID
     * @return Optional 包含查詢結果，若無則為空
     */
    @Override
    public Optional<MenuEntity> findById(Long id) {
        log.info("Finding menu item by ID: {}", id);
        return menuRepository.findById(id);
    }

    /**
     * 查詢所有菜單項目
     * @return 所有菜單項目列表
     */
    @Override
    public List<MenuEntity> findAll() {
        log.info("Fetching all menu items");
        return menuRepository.findAll();
    }

    /**
     * 建立新的菜單項目
     * @param menu 要建立的菜單物件
     * @return 建立後的菜單物件（含自動產生的 ID）
     */
    @Override
    public MenuEntity create(MenuEntity menu) {
        log.info("Creating new menu item: {}", menu.getName());
        MenuEntity saved = menuRepository.save(menu);
        log.info("Created menu item with ID: {}", saved.getId());
        return saved;
    }

    /**
     * 更新指定 ID 的菜單項目
     * @param id 欲更新的菜單項目 ID
     * @param updatedMenu 新的菜單資料
     * @return 更新後的菜單項目物件
     * @throws RuntimeException 找不到指定 ID 時丟出例外
     */
    @Override
    public MenuEntity update(Long id, MenuEntity updatedMenu) {
        log.info("Updating menu item ID: {}", id);
        return menuRepository.findById(id)
                .map(existing -> {
                    existing.setName(updatedMenu.getName());
                    existing.setDescription(updatedMenu.getDescription());
                    existing.setPrice(updatedMenu.getPrice());
                    existing.setCategory(updatedMenu.getCategory());
                    existing.setAvailable(updatedMenu.getAvailable());
                    existing.setImageUrl(updatedMenu.getImageUrl());
                    MenuEntity saved = menuRepository.save(existing);
                    log.info("Updated menu item ID: {}", id);
                    return saved;
                })
                .orElseThrow(() -> {
                    log.error("Menu item not found with ID: {}", id);
                    return new RuntimeException("Menu item not found with id " + id);
                });
    }

    /**
     * 刪除指定 ID 的菜單項目
     * @param id 欲刪除的菜單項目 ID
     */
    @Override
    public void delete(Long id) {
        log.info("Deleting menu item ID: {}", id);
        menuRepository.deleteById(id);
        log.info("Deleted menu item ID: {}", id);
    }
}
