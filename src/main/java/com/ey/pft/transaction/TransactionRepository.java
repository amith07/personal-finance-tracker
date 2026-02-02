package com.ey.pft.transaction;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

	Optional<Transaction> findByIdAndUser_Id(UUID id, UUID userId);

	@Query("select t from Transaction t " + "where t.user.id = :userId "
			+ "and t.status = com.ey.pft.transaction.TransactionStatus.ACTIVE "
			+ "and (:type is null or t.type = :type) " + "and (:categoryId is null or t.category.id = :categoryId) "
			+ "and (:fromDate is null or t.transactionDate >= :fromDate) "
			+ "and (:toDate is null or t.transactionDate <= :toDate)")
	Page<Transaction> search(UUID userId, TransactionType type, UUID categoryId, LocalDate fromDate, LocalDate toDate,
			Pageable pageable);

	List<Transaction> findByUser_IdAndTransferGroupId(UUID userId, UUID transferGroupId);
}