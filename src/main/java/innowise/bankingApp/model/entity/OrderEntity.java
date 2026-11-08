package innowise.bankingApp.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class OrderEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orders_generator")
    @SequenceGenerator(name = "orders_generator", sequenceName = "orders_seq", allocationSize = 10)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @NotNull(message = "UserEntity cannot be null")
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<OrderItemEntity> orderItems = new ArrayList<>();

    @NotBlank(message = "Status cannot be empty")
    @Size(min = 1, max = 32, message = "Status has to be between 1 and 32 characters")
    @Column(nullable = false, length = 32)
    private String status;

    @NotNull(message = "Total price cannot be null")
    @DecimalMin(value = "0.00", message = "Total price cannot be negative")
    @Digits(integer = 17, fraction = 2, message = "Total price can have up to 17 integer digits and 2 decimal places")
    @Column(name = "total_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalPrice = BigDecimal.ZERO;

    @Column(nullable = false)
    private boolean deleted;

    public void addOrderItem(OrderItemEntity orderItem) {
        OrderEntity previousOrder = orderItem.getOrder();

        if (previousOrder != null && previousOrder != this) {
            previousOrder.getOrderItems().remove(orderItem);
        }

        orderItem.setOrder(this);

        if (!orderItems.contains(orderItem)) {
            orderItems.add(orderItem);
        }
    }

    @Override
    public final boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof OrderEntity other)) {
            return false;
        }

        return getId() != null && getId().equals(other.getId());
    }

    @Override
    public final int hashCode() {
        return OrderEntity.class.hashCode();
    }
}