package me.tokyomap.domain.favorite.repository;

import me.tokyomap.domain.favorite.entity.Favorite;
import me.tokyomap.domain.restaurant.entity.Restaurant;
import me.tokyomap.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    //중복 체크용
    boolean existsByUserAndRestaurant(User user, Restaurant restaurant);

    //즐겨찾기 찾기(삭제용 등)
    Optional<Favorite> findByUserAndRestaurant(User user, Restaurant restaurant);

    //특정 유저의 모든 즐겨찾기 가져오기
    @EntityGraph(attributePaths = "restaurant")
    Page<Favorite> findByUser(User user, Pageable pageable);
}
