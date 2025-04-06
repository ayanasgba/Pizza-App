package whz.pti.eva.item.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import whz.pti.eva.cart.domai.Cart;
import whz.pti.eva.base.domain.BaseEntity;
import whz.pti.eva.pizza.domain.enums.PizzaSize;
import whz.pti.eva.pizza.domain.Pizza;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Item extends BaseEntity<UUID> {

    int quantity;

    @ManyToOne
    @JoinColumn(name = "pizza_id", nullable = false)
    private Pizza pizza;

    @Enumerated(EnumType.STRING)
    private PizzaSize pizzaSize;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    public Item() {
    }

    public Item(int quantity, Pizza pizza, PizzaSize pizzaSize) {
        this.quantity = quantity;
        this.pizza = pizza;
        this.pizzaSize = pizzaSize;
        this.price = switch (pizzaSize) {
            case SMALL -> pizza.getPriceSmall();
            case MEDIUM -> pizza.getPriceMedium();
            case LARGE -> pizza.getPriceLarge();
        };
    }

    public UUID getId() {
        return super.getId();
    }

    public void setId(UUID id) {
        super.setId(id);
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Pizza getPizza() {
        return pizza;
    }

    public void setPizza(Pizza pizza) {
        this.pizza = pizza;
    }

    public PizzaSize getPizzaSize() {
        return pizzaSize;
    }

    public void setPizzaSize(PizzaSize pizzaSize) {
        this.pizzaSize = pizzaSize;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public BigDecimal getTotalPrice() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    public void increaseQuantity(int additionalQuantity) {
        this.quantity += additionalQuantity;
    }

    public void decreaseQuantity(int quantityToRemove) {
        if (quantityToRemove >= this.quantity) {
            throw new IllegalArgumentException("Cannot remove more items than available in the cart");
        }
        this.quantity -= quantityToRemove;
    }
}
