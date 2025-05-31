package com.farmily.fhs.common.repository;

import com.farmily.fhs.common.repository.entity.UserEntity;

import java.util.Optional;

public interface UserRepository extends BaseRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
}
