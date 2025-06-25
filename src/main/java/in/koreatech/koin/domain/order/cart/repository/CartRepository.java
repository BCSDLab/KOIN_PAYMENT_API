package in.koreatech.koin.domain.order.cart.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import in.koreatech.koin.domain.order.cart.exception.CartNotFoundException;
import in.koreatech.koin.domain.order.cart.model.Cart;
import io.lettuce.core.dynamic.annotation.Param;

public interface CartRepository extends Repository<Cart, Integer> {

    Optional<Cart> findByUserId(Integer userId);

    default Cart getCartByUserId(Integer userId) {
        return findByUserId(userId)
            .orElseThrow(() -> CartNotFoundException.withDetail("userId : " + userId));
    }

    void deleteByUserId(Integer userId);
}
