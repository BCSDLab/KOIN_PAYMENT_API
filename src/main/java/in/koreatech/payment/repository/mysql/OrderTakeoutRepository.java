package in.koreatech.payment.repository.mysql;

import org.springframework.data.repository.Repository;

import in.koreatech.payment.model.entity.OrderTakeout;

public interface OrderTakeoutRepository extends Repository<OrderTakeoutRepository, Integer> {

    void save(OrderTakeout orderTakeout);
}
