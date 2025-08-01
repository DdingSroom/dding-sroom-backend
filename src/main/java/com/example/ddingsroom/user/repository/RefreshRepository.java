package com.example.ddingsroom.user.repository;

import com.example.ddingsroom.user.entity.RefreshEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface RefreshRepository extends JpaRepository<RefreshEntity, Long> {
    Boolean existsByRefresh(String refresh);

    @Transactional
    void deleteByRefresh(String refresh);
    
    @Transactional
    void deleteByUsername(String username);
}