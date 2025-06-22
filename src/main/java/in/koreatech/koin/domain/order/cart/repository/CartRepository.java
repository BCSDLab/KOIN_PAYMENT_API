package in.koreatech.koin.domain.order.cart.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import in.koreatech.koin.domain.order.cart.exception.CartNotFoundException;
import in.koreatech.koin.domain.order.cart.model.Cart;
import io.lettuce.core.dynamic.annotation.Param;

public interface CartRepository extends Repository<Cart, Integer> {

    @Query("""
        SELECT c FROM Cart c
        LEFT JOIN FETCH c.orderableShop
        LEFT JOIN FETCH c.cartMenuItems
        WHERE c.id = :cartId
    """)
    Optional<Cart> findByCardId(@Param("cartId") Integer cartId);

    default Cart getCartById(Integer cartId) {
        return findByCardId(cartId)
            .orElseThrow(() -> CartNotFoundException.withDetail("cartId : " + cartId));
    }

    void deleteByUserId(Integer userId);
}
