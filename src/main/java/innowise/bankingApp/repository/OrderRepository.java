package innowise.bankingApp.repository;

import innowise.bankingApp.model.entity.OrderEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    @EntityGraph(attributePaths = {"orderItems", "orderItems.item"})
    @Query("""
            select orderEntity
            from OrderEntity orderEntity
            where orderEntity.id = :id
            """)
    Optional<OrderEntity> findDetailsById(@Param("id") Long id);
}