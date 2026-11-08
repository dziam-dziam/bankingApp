package innowise.bankingApp.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "order_items")
@Getter
@Setter
public class OrderItemEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_items_generator")
    @SequenceGenerator(name = "order_items_generator", sequenceName = "order_items_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @NotNull(message = "OrderEntity cannot be null")
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @NotNull(message = "ItemEntity cannot be null")
    @JoinColumn(name = "item_id", nullable = false)
    private ItemEntity item;

    @NotNull(message = "Quantity cannot be null")
    @Positive(message = "Quantity must be greater than zero")
    @Column(nullable = false)
    private Integer quantity;

    @Override
    public final boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof OrderItemEntity other)) {
            return false;
        }

        return getId() != null && getId().equals(other.getId());
    }

    @Override
    public final int hashCode() {
        return OrderItemEntity.class.hashCode();
    }
}