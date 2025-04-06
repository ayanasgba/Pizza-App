package whz.pti.eva.order.service;

import whz.pti.eva.customer.domain.Customer;
import whz.pti.eva.order.domain.Ordered;

import java.util.List;

public interface OrderService {

    Ordered createOrderFromCart(Long cartId);

    Ordered getOrderById(Long id);

    List<Ordered> getAllOrderByUserId(Customer customer);

    List<Ordered> getAll();
}
