package in.koreatech.koin.domain.shop.repository;

import org.springframework.data.repository.Repository;

import in.koreatech.koin.domain.shop.model.shop.Shop;

public interface ShopRepository extends Repository<Shop, Integer> {

    Shop save(Shop shop);
}
