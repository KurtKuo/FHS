package com.farmily.fhs.common.repository;

import com.farmily.fhs.common.repository.entity.UserEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 使用者 UserRepository
 */
@Repository
public interface UserRepository extends BaseRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
}
