package ra.orderservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ra.orderservice.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
