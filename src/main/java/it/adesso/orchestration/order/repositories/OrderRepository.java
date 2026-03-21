package it.adesso.orchestration.order.repositories;

import it.adesso.orchestration.order.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
