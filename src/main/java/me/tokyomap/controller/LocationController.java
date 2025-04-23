package me.tokyomap.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.tokyomap.dto.common.ApiResponse;
import me.tokyomap.dto.location.LocationResponseDto;
import me.tokyomap.service.LocationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Location", description = "지역 정보 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/locations")
public class LocationController {

    private final LocationService locationService;

    @Operation(summary = "전체 지역 목록 또는 특정 구 필터 조회")
    @GetMapping
    public ApiResponse<List<LocationResponseDto>> getLocations(
            @Parameter(description = "구/시 이름 (ex: Shibuya City)", example = "Shibuya City")
            @RequestParam(required = false) String adminLevel2) {

        List<LocationResponseDto> result = (adminLevel2 != null && !adminLevel2.isBlank())
                ? locationService.getLocationsByAdminLevel2(adminLevel2)
                : locationService.getAllLocations();

        return ApiResponse.success(result);
    }
}
