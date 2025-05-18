package me.tokyomap.service.impl;

import me.tokyomap.domain.favorite.entity.Favorite;
import me.tokyomap.domain.favorite.repository.FavoriteRepository;
import me.tokyomap.domain.restaurant.entity.Restaurant;
import me.tokyomap.domain.restaurant.repository.RestaurantRepository;
import me.tokyomap.domain.user.entity.User;
import me.tokyomap.domain.user.repository.UserRepository;
import me.tokyomap.dto.favorite.FavoriteCheckResponseDto;
import me.tokyomap.dto.favorite.FavoriteRequestDto;
import me.tokyomap.dto.favorite.FavoriteRestaurantResponseDto;
import me.tokyomap.exception.CustomException;
import me.tokyomap.exception.ErrorCode;
import me.tokyomap.mapper.FavoriteMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private FavoriteServiceImpl favoriteService;

    private FavoriteRequestDto makeDto(long restaurantId) {
        return new FavoriteRequestDto() {{
            try {
                var f = getClass().getSuperclass().getDeclaredField("restaurantId");
                f.setAccessible(true);
                f.set(this, restaurantId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }};
    }

    @Test
    void お気に入り登録_成功() {
        String email = "user@example.com";
        FavoriteRequestDto dto = makeDto(1L);
        User user = mock(User.class);
        Restaurant restaurant = mock(Restaurant.class);

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(restaurantRepository.findById(1L)).willReturn(Optional.of(restaurant));
        given(favoriteRepository.existsByUserAndRestaurant(user, restaurant)).willReturn(false);

        favoriteService.addFavorite(email, dto);

        then(favoriteRepository).should().save(any(Favorite.class));
    }

    @Test
    void お気に入り登録_失敗_すでに登録済み() {
        String email = "user@example.com";
        FavoriteRequestDto dto = makeDto(1L);
        User user = mock(User.class);
        Restaurant restaurant = mock(Restaurant.class);

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(restaurantRepository.findById(1L)).willReturn(Optional.of(restaurant));
        given(favoriteRepository.existsByUserAndRestaurant(user, restaurant)).willReturn(true);

        assertThatThrownBy(() -> favoriteService.addFavorite(email, dto))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.ALREADY_FAVORITED.getMessage());
    }

    @Test
    void お気に入り解除_成功() {
        String email = "user@example.com";
        FavoriteRequestDto dto = makeDto(1L);
        User user = mock(User.class);
        Restaurant restaurant = mock(Restaurant.class);
        Favorite favorite = mock(Favorite.class);

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(restaurantRepository.findById(1L)).willReturn(Optional.of(restaurant));
        given(favoriteRepository.findByUserAndRestaurant(user, restaurant)).willReturn(Optional.of(favorite));

        favoriteService.removeFavorite(email, dto);

        then(favoriteRepository).should().delete(favorite);
    }

    @Test
    void お気に入り解除_失敗_存在しない() {
        String email = "user@example.com";
        FavoriteRequestDto dto = makeDto(1L);
        User user = mock(User.class);
        Restaurant restaurant = mock(Restaurant.class);

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(restaurantRepository.findById(1L)).willReturn(Optional.of(restaurant));
        given(favoriteRepository.findByUserAndRestaurant(user, restaurant)).willReturn(Optional.empty());

        assertThatThrownBy(() -> favoriteService.removeFavorite(email, dto))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.FAVORITE_NOT_FOUND.getMessage());
    }

    @Test
    void お気に入り状態確認_成功_登録済み() {
        String email = "user@example.com";
        Long restaurantId = 1L;
        User user = mock(User.class);
        Restaurant restaurant = mock(Restaurant.class);

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));
        given(favoriteRepository.existsByUserAndRestaurant(user, restaurant)).willReturn(true);

        FavoriteCheckResponseDto result = favoriteService.checkFavorite(email, restaurantId);

        assertThat(result.isLiked()).isTrue();
    }

    @Test
    void 自分のお気に入り一覧取得_成功() {
        String email = "user@example.com";
        User user = mock(User.class);
        Pageable pageable = Pageable.unpaged();
        Favorite favorite = mock(Favorite.class);
        Page<Favorite> page = new PageImpl<>(List.of(favorite));

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(favoriteRepository.findByUser(user, pageable)).willReturn(page);

        try (MockedStatic<FavoriteMapper> mapper = mockStatic(FavoriteMapper.class)) {
            mapper.when(() -> FavoriteMapper.toDto(any(Favorite.class)))
                    .thenReturn(FavoriteRestaurantResponseDto.builder()
                            .restaurantId(1L)
                            .name("すし三昧")
                            .address("銀座")
                            .averageRating(4.2)
                            .reviewCount(123)
                            .build());

            Page<FavoriteRestaurantResponseDto> result = favoriteService.getMyFavorites(email, pageable);

            assertThat(result.getContent()).hasSize(1);
        }
    }
}
