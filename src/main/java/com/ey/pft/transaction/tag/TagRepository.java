package com.ey.pft.transaction.tag;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, UUID> {
	Optional<Tag> findByUser_IdAndNameIgnoreCase(UUID userId, String name);

	Optional<Tag> findByIdAndUser_Id(UUID id, UUID userId);
}