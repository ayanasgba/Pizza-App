package whz.pti.eva.order.domain;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import whz.pti.eva.base.domain.BaseEntity;
import whz.pti.eva.orderedItem.domain.OrderedItem;

@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Ordered extends BaseEntity<Long> {

    int numberOfItems;
    String userId;
    @Column(name = "total_price", precision = 10, scale = 2)
    private BigDecimal totalPrice = BigDecimal.ZERO;

    @OneToMany(mappedBy = "ordered", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<OrderedItem> orderedItems = new HashSet<>();

    public Ordered() {
        this.orderedItems = new HashSet<>();
    }

    public void addOrderedItem(OrderedItem item) {
        item.setOrdered(this);
        this.orderedItems.add(item);
    }

    public void removeOrderedItem(OrderedItem item) {
        this.orderedItems.remove(item);
        item.setOrdered(null);
    }

    public Set<OrderedItem> getOrderedItems() {
        return orderedItems;
    }

    public void setOrderedItems(Set<OrderedItem> orderedItems) {
        this.orderedItems = orderedItems;
    }

    public int getNumberOfItems() {
        return numberOfItems;
    }

    public void setNumberOfItems(int numberOfItems) {
        this.numberOfItems = numberOfItems;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
