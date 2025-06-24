package in.koreatech.koin.domain.order.repository;

import org.springframework.data.repository.Repository;

import in.koreatech.koin.domain.order.model.OrderMenu;

public interface OrderMenuRepository extends Repository<OrderMenu, Integer> {

    void save(OrderMenu orderMenu);
}
