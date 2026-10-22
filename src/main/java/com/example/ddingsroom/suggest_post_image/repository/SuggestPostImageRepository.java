package com.example.ddingsroom.suggest_post_image.repository;

import com.example.ddingsroom.suggest_post_image.entity.SuggestPostImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SuggestPostImageRepository extends JpaRepository<SuggestPostImageEntity, Long> {
    List<SuggestPostImageEntity> findBySuggestPost_Id(Long suggestPostId);

    @Query("SELECT i.fileUrl FROM SuggestPostImageEntity i WHERE i.suggestPost.id = :suggestPostId")
    List<String> findFileUrlsBySuggestPostId(@Param("suggestPostId") Long suggestPostId);

    @Query("SELECT i.fileUrl FROM SuggestPostImageEntity i WHERE i.suggestPost.user.id = :userId")
    List<String> findFileUrlsByUserId(@Param("userId") Long userId);
}
