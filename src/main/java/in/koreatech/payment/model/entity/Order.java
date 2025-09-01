package in.koreatech.payment.model.entity;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static java.lang.Boolean.FALSE;
import static lombok.AccessLevel.PROTECTED;

import org.hibernate.annotations.Where;

import in.koreatech.koin.domain.order.shop.model.entity.shop.OrderableShop;
import in.koreatech.koin.domain.user.model.User;
import in.koreatech.payment.common.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(schema = "koin_payment", name = "`order`")
@Where(clause = "is_deleted=0")
@NoArgsConstructor(access = PROTECTED)
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;

    @NotBlank
    @Size(min = 6, max = 64)
    @Column(name = "pg_order_id", length = 64, nullable = false, updatable = false)
    private String pgOrderId;

    @NotNull
    @Enumerated(STRING)
    @Column(name = "order_type", length = 10, nullable = false, updatable = false)
    private OrderType orderType;

    @NotBlank
    @Size(max = 20)
    @Column(name = "phone_number", length = 20, nullable = false, updatable = false)
    private String phoneNumber;

    @NotNull
    @Column(name = "total_product_price", nullable = false, updatable = false)
    private Integer totalProductPrice;

    @NotNull
    @Column(name = "total_price", nullable = false, updatable = false)
    private Integer totalPrice;

    @NotNull
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = FALSE;

    @NotNull
    @Column(name = "orderable_shop_id", nullable = false, updatable = false)
    private Integer orderableShopId;

    @NotNull
    @Column(name = "user_id", nullable = false, updatable = false)
    private Integer userId;

    @OneToOne(mappedBy = "order", fetch = LAZY, cascade = ALL)
    private OrderDelivery orderDelivery;

    @OneToOne(mappedBy = "order", fetch = LAZY, cascade = ALL)
    private OrderTakeout orderTakeout;

    @Builder
    private Order(
        Integer id,
        String pgOrderId,
        OrderType orderType,
        String phoneNumber,
        Integer totalProductPrice,
        Integer totalPrice,
        Boolean isDeleted,
        Integer orderableShopId,
        Integer userId,
        OrderDelivery orderDelivery,
        OrderTakeout orderTakeout
    ) {
        this.id = id;
        this.pgOrderId = pgOrderId;
        this.orderType = orderType;
        this.phoneNumber = phoneNumber;
        this.totalProductPrice = totalProductPrice;
        this.totalPrice = totalPrice;
        this.isDeleted = isDeleted;
        this.orderableShopId = orderableShopId;
        this.userId = userId;
        this.orderDelivery = orderDelivery;
        this.orderTakeout = orderTakeout;
    }

    public void setOrderDelivery(OrderDelivery orderDelivery) {
        this.orderDelivery = orderDelivery;
    }

    public void setOrderTakeout(OrderTakeout orderTakeout) {
        this.orderTakeout = orderTakeout;
    }
}
