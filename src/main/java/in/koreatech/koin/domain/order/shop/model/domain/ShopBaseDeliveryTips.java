package in.koreatech.koin.domain.order.shop.model.domain;

import static jakarta.persistence.CascadeType.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import in.koreatech.koin.domain.order.shop.model.entity.delivery.ShopBaseDeliveryTip;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
public class ShopBaseDeliveryTips {

    @OneToMany(mappedBy = "shop", orphanRemoval = true, cascade = {PERSIST, REFRESH, MERGE, REMOVE})
    private List<ShopBaseDeliveryTip> baseDeliveryTips = new ArrayList<>();
}
