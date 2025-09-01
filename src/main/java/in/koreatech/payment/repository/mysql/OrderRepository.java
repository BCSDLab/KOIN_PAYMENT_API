package in.koreatech.payment.repository.mysql;

import org.springframework.data.repository.Repository;

import in.koreatech.payment.model.entity.Order;

public interface OrderRepository extends Repository<Order, String> {

    void save(Order order);
}
