package me.tokyomap.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.tokyomap.dto.restaurant.RestaurantSearchRequestDto;
import me.tokyomap.dto.restaurant.RestaurantSearchResponseDto;
import me.tokyomap.service.RestaurantService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
@Tag(name = "Restaurant", description = "맛집 검색 및 조회 API")
public class RestaurantController {

    private final RestaurantService restaurantService;

    @Operation(summary = "맛집 검색", description = "카테고리, 지역, 영업여부로 맛집을 검색합니다.")
    @GetMapping("/search")
    public Page<RestaurantSearchResponseDto> searchRestaurants(
        /*    @Parameter(description = "검색 조건") @ModelAttribute RestaurantSearchRequestDto requestDto
            ) {
        return restaurantService.searchRestaurants(requestDto);*/
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Boolean openNow,
            @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        RestaurantSearchRequestDto dto = new RestaurantSearchRequestDto(category, city, openNow);
        return restaurantService.searchRestaurants(dto, pageable);
    }

    @Operation(summary = "맛집 상세 조회", description = "ID로 맛집 상세정보를 조회합니다.")
    @GetMapping("/{id}")
    public RestaurantSearchResponseDto getRestaurantById(
            @Parameter(description = "음식점 ID", example = "1") @PathVariable Long id
    ) {
        return restaurantService.getRestaurantById(id);
    }


}
