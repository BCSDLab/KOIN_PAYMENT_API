package in.koreatech.koin.domain.order.repository;

import org.springframework.data.repository.Repository;

import in.koreatech.koin.domain.order.model.OrderDelivery;

public interface OrderDeliveryRepository extends Repository<OrderDelivery, String> {

    void save(OrderDelivery orderDelivery);
}
