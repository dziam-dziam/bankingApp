package innowise.bankingApp.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "items")
@Getter
@Setter
public class ItemEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "items_generator")
    @SequenceGenerator(name = "items_generator", sequenceName = "items_seq", allocationSize = 1)
    private Long id;

    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY)
    private List<OrderItemEntity> orderItems = new ArrayList<>();

    @NotBlank(message = "Name cannot be empty")
    @Size(min = 1, max = 255, message = "Name has to be between 1 and 255 characters")
    @Column(nullable = false, length = 255)
    private String name;

    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.00", message = "Price cannot be negative")
    @Digits(integer = 17, fraction = 2, message = "Price can have up to 17 integer digits and 2 decimal places")
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    public void addOrderItem(OrderItemEntity orderItem) {
        ItemEntity previousItem = orderItem.getItem();

        if (previousItem != null && previousItem != this) {
            previousItem.getOrderItems().remove(orderItem);
        }

        orderItem.setItem(this);

        if (!orderItems.contains(orderItem)) {
            orderItems.add(orderItem);
        }
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof ItemEntity other)) {
            return false;
        }

        return getId() != null && getId().equals(other.getId());
    }

    @Override
    public int hashCode() {
        return ItemEntity.class.hashCode();
    }
}