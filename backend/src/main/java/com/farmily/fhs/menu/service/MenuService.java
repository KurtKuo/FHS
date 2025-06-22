package com.farmily.fhs.menu.service;

import com.farmily.fhs.common.repository.entity.MenuEntity;

import java.util.List;
import java.util.Optional;

/**
 * 菜單業務邏輯服務介面。
 */
public interface MenuService {

    MenuEntity create(MenuEntity menu);

    Optional<MenuEntity> findById(Long id);

    List<MenuEntity> findAll();

    MenuEntity update(Long id, MenuEntity updatedMenu);

    void delete(Long id);
}
