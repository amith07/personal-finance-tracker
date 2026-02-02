package com.ey.pft.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    // System categories = user is null
    List<Category> findByUserIsNullAndTypeOrderByNameAsc(CategoryType type);

    // User categories for given user id
    List<Category> findByUser_IdAndTypeOrderByNameAsc(UUID userId, CategoryType type);

    Optional<Category> findByIdAndUser_Id(UUID id, UUID userId);

    @Query("select c from Category c where c.user is null and c.type = :type and c.name = :name")
    Optional<Category> findSystemByTypeAndName(CategoryType type, String name);

    @Query("select c from Category c where c.user.id = :userId and c.type = :type and c.name = :name")
    Optional<Category> findUserCategoryByTypeAndName(UUID userId, CategoryType type, String name);

    List<Category> findByIdIn(Collection<UUID> ids);
}