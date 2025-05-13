package me.tokyomap.domain.favorite.repository;

import me.tokyomap.domain.favorite.entity.Favorite;
import me.tokyomap.domain.restaurant.entity.Restaurant;
import me.tokyomap.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * ユーザーのお気に入り情報を操作するリポジトリ
 * N+1問題を回避するため、EntityGraphを使用してレストラン情報を一括取得
 */
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    boolean existsByUserAndRestaurant(User user, Restaurant restaurant);

    Optional<Favorite> findByUserAndRestaurant(User user, Restaurant restaurant);

    @EntityGraph(attributePaths = "restaurant")
    Page<Favorite> findByUser(User user, Pageable pageable);
}
