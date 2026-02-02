package com.ey.pft.account;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, UUID> {
	Page<Account> findByUser_Id(UUID userId, Pageable pageable);

	Optional<Account> findByIdAndUser_Id(UUID id, UUID userId);
}