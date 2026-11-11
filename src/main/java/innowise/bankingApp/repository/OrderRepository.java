package innowise.bankingApp.repository;

import innowise.bankingApp.model.entity.OrderEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository
        extends Repository<OrderEntity, Long>, JpaSpecificationExecutor<OrderEntity> {

    <S extends OrderEntity> S saveAndFlush(S order);

    void flush();

    @Query("select o from OrderEntity o where o.id = :id")
    Optional<OrderEntity> findById(@Param("id") Long id);

    List<OrderEntity> findAllByUser_IdOrderByIdAsc(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from OrderEntity o where o.id = :id")
    Optional<OrderEntity> findByIdForUpdate(@Param("id") Long id);

    @EntityGraph(attributePaths = {"orderItems", "orderItems.item"})
    @Query("select o from OrderEntity o where o.id = :id")
    Optional<OrderEntity> findDetailsById(@Param("id") Long id);
}