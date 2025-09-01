package in.koreatech.payment.repository.mysql;

import org.springframework.data.repository.Repository;

import in.koreatech.payment.model.entity.OrderMenuOption;

public interface OrderMenuOptionRepository extends Repository<OrderMenuOption, Integer> {

    void saveAll(Iterable<OrderMenuOption> orderMenuOptions);
}
