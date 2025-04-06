package whz.pti.eva.cart.domai;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import whz.pti.eva.customer.domain.Customer;
import whz.pti.eva.base.domain.BaseEntity;
import whz.pti.eva.item.domain.Item;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Cart extends BaseEntity<Long> {

    int quantity;

    @ManyToOne
    Customer customer;

    @Column(name = "session_id", nullable = true)
    String sessionId;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Item> items = new ArrayList<>();


    @Column(name = "total_price", precision = 10, scale = 2)
    private BigDecimal totalPrice = BigDecimal.ZERO; // Итоговая цена

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Cart() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return super.getId();
    }

    public void setId(Long id) {
        super.setId(id);
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public void addItem(Item item) {
        this.items.add(item);
        item.setCart(this);
        recalculateTotals();
    }

    public void removeItem(Item item) {
        this.items.remove(item);
        item.setCart(null);
        recalculateTotals();
    }

    public void clear() {
        this.items.clear();
        recalculateTotals();
    }

    public void recalculateTotals() {
        this.quantity = items.stream().mapToInt(Item::getQuantity).sum();
        this.totalPrice = items.stream()
                .map(Item::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getTotalItems() {
        return items.stream()
                .mapToInt(Item::getQuantity)
                .sum();
    }


}