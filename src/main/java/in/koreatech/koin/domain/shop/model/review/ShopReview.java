package in.koreatech.koin.domain.shop.model.review;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import java.util.ArrayList;
import java.util.List;

import in.koreatech.koin.domain.shop.model.shop.Shop;
import in.koreatech.payment.common.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "shop_reviews")
@NoArgsConstructor(access = PROTECTED)
public class ShopReview extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "content")
    private String content;

    @Column(nullable = false)
    private Integer rating;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @NotNull
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @OneToMany(mappedBy = "review", orphanRemoval = true, cascade = ALL, fetch = LAZY)
    private List<ShopReviewImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "review", orphanRemoval = true, cascade = ALL, fetch = LAZY)
    private List<ShopReviewMenu> menus = new ArrayList<>();

    @OneToMany(mappedBy = "review", orphanRemoval = true, cascade = ALL, fetch = LAZY)
    private List<ShopReviewReport> reports = new ArrayList<>();
}
