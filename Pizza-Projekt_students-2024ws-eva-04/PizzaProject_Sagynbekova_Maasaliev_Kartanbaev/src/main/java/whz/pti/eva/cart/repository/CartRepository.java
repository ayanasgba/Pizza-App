package whz.pti.eva.cart.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import whz.pti.eva.cart.domai.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByCustomer_Id(Long customerId);
    Optional<Cart> findBySessionId(String sessionId);

}