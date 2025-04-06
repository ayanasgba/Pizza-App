package whz.pti.eva.order.service.Impl;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import whz.pti.eva.base.service.PaymentServiceClient;
import whz.pti.eva.cart.domai.Cart;
import whz.pti.eva.cart.repository.CartRepository;
import whz.pti.eva.customer.domain.Customer;
import whz.pti.eva.item.domain.Item;
import whz.pti.eva.item.repository.ItemRepository;
import whz.pti.eva.order.domain.Ordered;
import whz.pti.eva.order.repository.OrderedRepository;
import whz.pti.eva.order.service.OrderService;
import whz.pti.eva.orderedItem.domain.OrderedItem;

import java.math.BigDecimal;
import java.util.List;


@Service
public class OrderServiceImpl implements OrderService {

    private final OrderedRepository orderedRepository;
    private final CartRepository cartRepository;
    private final ItemRepository itemRepository;
    private final PaymentServiceClient paymentService;

    @Autowired
    public OrderServiceImpl(OrderedRepository orderedRepository, CartRepository cartRepository, ItemRepository itemRepository, PaymentServiceClient paymentServiceClient) {
        this.orderedRepository = orderedRepository;
        this.cartRepository = cartRepository;
        this.itemRepository = itemRepository;
        this.paymentService = paymentServiceClient;
    }

    @Transactional
    public Ordered createOrderFromCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found with ID: " + cartId));

        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cannot create an order from an empty cart.");
        }

        String userId = cart.getCustomer() != null ? cart.getCustomer().getId().toString() : cart.getSessionId();
        BigDecimal totalPrice = cart.getTotalPrice();

        boolean hasSufficientBalance = paymentService.checkBalance(Long.parseLong(userId), totalPrice);
        if (!hasSufficientBalance) {
            throw new IllegalArgumentException("Insufficient balance to place the order.");
        }


        Ordered order = new Ordered();
        order.setUserId(userId);

        for (Item cartItem : cart.getItems()) {
            OrderedItem orderedItem = new OrderedItem(
                    cartItem.getPizza().getId(),
                    cartItem.getPizza().getName(),
                    cartItem.getQuantity(),
                    order.getUserId()
            );
            order.addOrderedItem(orderedItem);
        }
        order.setNumberOfItems(cart.getTotalItems());
        order.setTotalPrice(cart.getTotalPrice());
        Ordered savedOrder = orderedRepository.save(order);

        paymentService.chargeCustomer(Long.parseLong(userId), totalPrice);

        cart.getItems().clear();
        cart.setQuantity(0);
        cartRepository.save(cart);

        return savedOrder;
    }

    @Override
    public Ordered getOrderById(Long id) {
        return orderedRepository.getById(id);
    }

    @Override
    public List<Ordered> getAllOrderByUserId(Customer customer) {
        List<Ordered> orders = orderedRepository.findAllByUserId(String.valueOf(customer.getId()));
        return orders;
    }

    @Override
    public List<Ordered> getAll() {
        return orderedRepository.findAll();
    }
}
