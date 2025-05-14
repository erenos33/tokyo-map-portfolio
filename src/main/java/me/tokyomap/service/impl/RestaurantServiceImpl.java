package me.tokyomap.service.impl;

import lombok.RequiredArgsConstructor;

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
import me.tokyomap.service.RestaurantService;
import me.tokyomap.util.EntityFinder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * レストラン機能に関するサービスの実装クラス
 * 検索、登録、取得、削除の各処理を担当
 */
@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    /**
     * 条件に応じてレストランを検索し、ページング形式で結果を返す
     */
    @Override
    public Page<RestaurantSearchResponseDto> searchRestaurants(RestaurantSearchRequestDto requestDto, Pageable pageable) {

        Page<Restaurant> result = restaurantRepository.searchByCondition(requestDto, pageable);

        return result.map(RestaurantMapper::toDto);
    }

    /**
     * IDで指定されたレストランの詳細情報を取得する
     */
    @Override
    public RestaurantSearchResponseDto getRestaurantById(Long id) {
        Restaurant r = EntityFinder.getRestaurantOrThrow(restaurantRepository, id);

        return RestaurantMapper.toDto(r);
    }

    /**
     * Google検索結果から新しいレストランを登録する（重複Place IDはエラー）
     */
    @Transactional
    @Override
    public Long registerGooglePlace(GooglePlaceRegisterRequestDto dto, String userEmail) {

        restaurantRepository.findByPlaceId(dto.getPlaceId())
                .ifPresent(r -> { throw new CustomException(ErrorCode.RESTAURANT_ALREADY_REGISTERED); });

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Restaurant restaurant = RestaurantMapper.fromGoogleDto(dto, user);

        restaurantRepository.save(restaurant);
        return restaurant.getId();
    }

    /**
     * 現在のユーザーが登録したレストラン一覧を取得する（ページング対応）
     */
    @Transactional(readOnly = true)
    @Override
    public Page<RestaurantSearchResponseDto> getMyRegisteredRestaurants(String userEmail, Pageable pageable) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return restaurantRepository.findByRegisteredBy(user, pageable)
                .map(RestaurantMapper::toDto);
    }

    /**
     * 現在のユーザーが登録したレストランを削除する
     * 他ユーザーの登録店舗にはアクセス不可（ACCESS_DENIED）
     */
    @Transactional
    @Override
    public void deleteMyRestaurant(Long restaurantId, String userEmail) {
        Restaurant restaurant = EntityFinder.getRestaurantOrThrow(restaurantRepository, restaurantId);

        if (!restaurant.getRegisteredBy().getEmail().equals(userEmail)) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        restaurantRepository.delete(restaurant);
    }

    /**
     * 管理者が任意のレストランを削除する（権限チェックはController側で）
     */
    @Override
    public void deleteRestaurantByAdmin(Long restaurantId) {
        restaurantRepository.deleteById(restaurantId);
    }

}
