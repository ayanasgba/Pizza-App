package whz.pti.eva.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import whz.pti.eva.customer.domain.Customer;
import whz.pti.eva.order.domain.Ordered;

import java.util.List;

@Repository
public interface OrderedRepository extends JpaRepository<Ordered, Long>{
    List<Ordered> findAllByUserId(String userId);
}
