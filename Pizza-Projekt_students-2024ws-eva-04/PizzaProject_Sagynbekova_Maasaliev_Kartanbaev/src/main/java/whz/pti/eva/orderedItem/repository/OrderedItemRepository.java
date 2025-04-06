package whz.pti.eva.orderedItem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import whz.pti.eva.orderedItem.domain.OrderedItem;

@Repository
public interface OrderedItemRepository extends JpaRepository<OrderedItem, Long> {
}

