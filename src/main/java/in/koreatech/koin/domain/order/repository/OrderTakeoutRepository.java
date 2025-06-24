package in.koreatech.koin.domain.order.repository;

import org.springframework.data.repository.Repository;

import in.koreatech.koin.domain.order.model.OrderTakeout;

public interface OrderTakeoutRepository extends Repository<OrderTakeoutRepository, Integer> {

    void save(OrderTakeout orderTakeout);
}
