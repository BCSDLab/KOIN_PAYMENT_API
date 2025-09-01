package in.koreatech.payment.repository.mysql;

import java.util.List;

import org.springframework.data.repository.Repository;

import in.koreatech.payment.model.entity.OrderMenu;

public interface OrderMenuRepository extends Repository<OrderMenu, Integer> {

    void saveAll(Iterable<OrderMenu> orderMenus);

    List<OrderMenu> findAllByOrderId(Integer orderId);
}
