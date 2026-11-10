package innowise.bankingApp.repository;

import innowise.bankingApp.model.entity.ItemEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<ItemEntity, Long> {

    boolean existsByIdAndOrderItemsIsNotEmpty(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select item from ItemEntity item where item.id = :id")
    Optional<ItemEntity> findByIdForUpdate(@Param("id") Long id);
}