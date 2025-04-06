package whz.pti.eva.pizza.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import whz.pti.eva.pizza.domain.Pizza;

@Repository
public interface PizzaRepository extends JpaRepository<Pizza, Long> {

}
