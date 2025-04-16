package me.tokyomap.domain.restaurant.repository;

import me.tokyomap.domain.restaurant.entity.Restaurant;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RestaurantRepositoryTest {

    @BeforeEach
    void clean() {
        restaurantRepository.deleteAll(); // 모든 레코드 삭제
    }

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Test
    void save_and_find() {
        Restaurant r = new Restaurant("스시진", "도쿄 미나토구", 35.6, 139.7);
        restaurantRepository.save(r);

        List<Restaurant> list = restaurantRepository.findAll();
        Assertions.assertThat(list).hasSize(1);
    }

}