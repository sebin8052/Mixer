package com.Mixer.library.repository;

import com.Mixer.library.model.Image;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long>
{
    @Query(value = "select * from images where product_id = :id", nativeQuery = true)
    List<Image> findImageBy(@Param("id")long id);

    @Transactional
    @Modifying
    @Query(value = "delete from images where product_id = :id",nativeQuery = true)
    void deleteImagesByProductId(@Param("id") long id);


}


