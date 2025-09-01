package in.koreatech.payment.repository.mysql;

import org.springframework.data.repository.Repository;

import in.koreatech.payment.model.entity.OrderDelivery;

public interface OrderDeliveryRepository extends Repository<OrderDelivery, String> {

    void save(OrderDelivery orderDelivery);
}
