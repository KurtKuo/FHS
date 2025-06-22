package com.farmily.fhs.menu.controller;

import com.farmily.fhs.common.repository.entity.MenuEntity;
import com.farmily.fhs.menu.service.MenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜單管理的 REST API 控制器。
 * <p>
 * 負責提供菜單項目的增刪改查功能。
 * 使用 @Slf4j 啟用 Lombok SLF4J 日誌功能，
 * 讓本類別能使用 log 物件方便輸出日誌。
 */
@RestController
@RequestMapping("/api/menus")
@Slf4j // 啟用 Lombok SLF4J 日誌功能，產生 log 物件
public class MenuController {

    @Autowired
    private MenuService menuService;

    /**
     * 查詢所有菜單項目
     *
     * @return 菜單列表
     */
    @GetMapping("/getAllMenu")
    public ResponseEntity<List<MenuEntity>> getAll() {
        log.info("查詢所有菜單項目");
        return ResponseEntity.ok(menuService.findAll());
    }

    /**
     * 根據 ID 查詢單一菜單項目
     *
     * @param id 菜單項目 ID
     * @return 該菜單項目，若找不到則回傳 404
     */
    @GetMapping("/getMenuById/{id}")
    public ResponseEntity<MenuEntity> getById(@PathVariable Long id) {
        log.info("查詢菜單項目 ID: {}", id);
        return menuService.findById(id)
                .map(menu -> {
                    log.info("找到菜單項目: {}", menu.getName());
                    return ResponseEntity.ok(menu);
                })
                .orElseGet(() -> {
                    log.warn("未找到菜單項目 ID: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    /**
     * 更新菜單項目
     *
     * @param id   要更新的菜單項目 ID
     * @param menu 新的菜單資料
     * @return 更新後的菜單項目
     */
    @PreAuthorize("hasAnyRole('STAFF','BOSS')")
    @PutMapping("/updateMenu/{id}")
    public ResponseEntity<MenuEntity> update(@PathVariable Long id, @RequestBody MenuEntity menu) {
        log.info("更新菜單項目 ID: {}", id);
        return ResponseEntity.ok(menuService.update(id, menu));
    }

    /**
     * 建立菜單項目
     *
     * @param menu 菜單資料
     * @return 建立完成的菜單項目
     */
    @PreAuthorize("hasAnyRole('STAFF','BOSS')")
    @PostMapping("/create")
    public ResponseEntity<MenuEntity> create(@RequestBody MenuEntity menu) {
        log.info("新增菜單項目: {}", menu.getName());
        return ResponseEntity.ok(menuService.create(menu));
    }

    /**
     * 刪除菜單項目
     *
     * @param id 要刪除的菜單項目 ID
     * @return 無內容回應
     */
    @PreAuthorize("hasAnyRole('STAFF','BOSS')")
    @DeleteMapping("/deleteMenu/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("刪除菜單項目 ID: {}", id);
        menuService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
