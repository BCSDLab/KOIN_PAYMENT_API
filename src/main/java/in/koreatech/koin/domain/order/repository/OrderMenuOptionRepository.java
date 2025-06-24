package in.koreatech.koin.domain.order.repository;

import org.springframework.data.repository.Repository;

import in.koreatech.koin.domain.order.model.OrderMenuOption;

public interface OrderMenuOptionRepository extends Repository<OrderMenuOption, Integer> {

    void saveAll(Iterable<OrderMenuOption> orderMenuOptions);
}
