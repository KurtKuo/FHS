package com.farmily.fhs.common.security;

import com.farmily.fhs.common.repository.entity.UserEntity;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@AllArgsConstructor
public class SecurityUser implements UserDetails {

    // 封裝你的 UserEntity，讓 Spring Security 可以透過它取到帳號密碼與權限等資訊
    private final UserEntity user;

    /**
     * 回傳授權角色，目前沒有實作角色權限，所以回傳空集合
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    // 以下四個方法都回傳 true，代表帳號狀態正常
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // 如果需要直接取得 UserEntity 物件，可以用這個方法
    public UserEntity getUserEntity() {
        return user;
    }
}
