package me.tokyomap.service.impl;

import me.tokyomap.domain.restaurant.entity.Restaurant;
import me.tokyomap.domain.restaurant.repository.RestaurantRepository;
import me.tokyomap.domain.user.entity.User;
import me.tokyomap.domain.user.repository.UserRepository;
import me.tokyomap.dto.restaurant.GooglePlaceRegisterRequestDto;
import me.tokyomap.dto.restaurant.RestaurantSearchRequestDto;
import me.tokyomap.dto.restaurant.RestaurantSearchResponseDto;
import me.tokyomap.exception.CustomException;
import me.tokyomap.exception.ErrorCode;
import me.tokyomap.mapper.RestaurantMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RestaurantServiceImpl restaurantService;

    @Test
    void レストラン検索_成功() {
        RestaurantSearchRequestDto dto = new RestaurantSearchRequestDto("ラーメン", "新宿", true);
        Pageable pageable = Pageable.unpaged();
        Restaurant restaurant = mock(Restaurant.class);
        Page<Restaurant> page = new PageImpl<>(List.of(restaurant));

        given(restaurantRepository.searchByCondition(dto, pageable)).willReturn(page);

        try (MockedStatic<RestaurantMapper> mapper = mockStatic(RestaurantMapper.class)) {
            mapper.when(() -> RestaurantMapper.toDto(any(Restaurant.class)))
                    .thenReturn(new RestaurantSearchResponseDto(
                            1L, "一蘭", "新宿区", 35.0, 139.0, 4.5,
                            "11:00–22:00", "₩₩", "03-1234-5678"
                    ));

            Page<RestaurantSearchResponseDto> result = restaurantService.searchRestaurants(dto, pageable);

            assertThat(result.getContent()).hasSize(1);
            verify(restaurantRepository).searchByCondition(dto, pageable);
        }
    }

    @Test
    void レストラン取得_失敗_ID存在しない() {
        Long restaurantId = 999L;
        given(restaurantRepository.findById(restaurantId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> restaurantService.getRestaurantById(restaurantId))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.RESTAURANT_NOT_FOUND.getMessage());
    }

    @Test
    void Googleレストラン登録_成功() {
        String email = "user@example.com";
        GooglePlaceRegisterRequestDto dto = new GooglePlaceRegisterRequestDto();
        dto.setPlaceId("placeId123");
        dto.setName("一蘭");
        dto.setAddress("東京都新宿区");
        dto.setLatitude(35.0);
        dto.setLongitude(139.0);
        dto.setRating(4.5);
        dto.setOpeningHours("11:00–22:00");
        dto.setPriceRange("₩₩");
        dto.setPhoneNumber("03-1234-5678");

        User user = mock(User.class);
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(restaurantRepository.findByPlaceId("placeId123")).willReturn(Optional.empty());

        restaurantService.registerGooglePlace(dto, email);

        verify(restaurantRepository).save(any(Restaurant.class));
    }

    @Test
    void Googleレストラン登録_失敗_重複登録() {
        String email = "user@example.com";
        GooglePlaceRegisterRequestDto dto = new GooglePlaceRegisterRequestDto();
        dto.setPlaceId("placeId123");

        given(restaurantRepository.findByPlaceId("placeId123"))
                .willReturn(Optional.of(mock(Restaurant.class)));

        assertThatThrownBy(() -> restaurantService.registerGooglePlace(dto, email))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.RESTAURANT_ALREADY_REGISTERED.getMessage());
    }

    @Test
    void 自分のレストラン一覧取得_成功() {
        String email = "owner@example.com";
        User user = mock(User.class);
        Restaurant restaurant = mock(Restaurant.class);
        Page<Restaurant> page = new PageImpl<>(List.of(restaurant));
        Pageable pageable = Pageable.unpaged();

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(restaurantRepository.findByRegisteredBy(user, pageable)).willReturn(page);

        try (MockedStatic<RestaurantMapper> mapper = mockStatic(RestaurantMapper.class)) {
            mapper.when(() -> RestaurantMapper.toDto(any(Restaurant.class)))
                    .thenReturn(new RestaurantSearchResponseDto(
                            1L, "トリトン", "札幌市", 43.0, 141.0, 4.3,
                            "10:00–22:00", "₩₩", "03-1234-9999"
                    ));

            Page<RestaurantSearchResponseDto> result = restaurantService.getMyRegisteredRestaurants(email, pageable);

            assertThat(result.getContent()).hasSize(1);
            verify(restaurantRepository).findByRegisteredBy(user, pageable);
        }
    }

    @Test
    void 自分のレストラン削除_成功() {
        Long restaurantId = 1L;
        String email = "owner@example.com";
        User user = mock(User.class);
        when(user.getEmail()).thenReturn(email);

        Restaurant restaurant = mock(Restaurant.class);
        when(restaurant.getRegisteredBy()).thenReturn(user);

        given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));

        restaurantService.deleteMyRestaurant(restaurantId, email);

        verify(restaurantRepository).delete(restaurant);
    }

    @Test
    void 管理者によるレストラン削除_成功() {
        // given
        Long restaurantId = 10L;

        // when
        restaurantService.deleteRestaurantByAdmin(restaurantId);

        // then
        verify(restaurantRepository).deleteById(restaurantId); // ❗ delete → deleteById 로 수정
    }
}
