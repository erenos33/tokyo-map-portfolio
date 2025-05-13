package me.tokyomap.domain.restaurant.repository;

import me.tokyomap.domain.restaurant.entity.Restaurant;
import me.tokyomap.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * レストランの永続化・検索処理を担うリポジトリ
 * QueryDSLを用いた条件検索のため、RestaurantRepositoryCustomを継承
 */
public interface RestaurantRepository extends JpaRepository<Restaurant, Long>, RestaurantRepositoryCustom {
    Optional<Restaurant> findByPlaceId(String placeId);
    Page<Restaurant> findByRegisteredBy(User user, Pageable pageable);
}
