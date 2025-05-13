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

/**
 * 地域（Location）情報を提供するコントローラー
 */
@Tag(name = "地域情報API", description = "地域（行政区）の一覧またはフィルタリング取得API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/locations")
public class LocationController {

    private final LocationService locationService;

    /**
     * 全地域の一覧、または指定された行政区（구/시）の地域一覧を取得
     */
    @GetMapping
    @Operation(
            summary = "地域一覧または行政区でのフィルター取得",
            description = "adminLevel2（区/市）を指定するとその地域の一覧を、指定しない場合は全地域を返します。"
    )
    public ApiResponse<List<LocationResponseDto>> getLocations(
            @Parameter(
                    description = "行政区名（例: Shibuya City）",
                    example = "Shibuya City"
            )
            @RequestParam(required = false) String adminLevel2) {

        List<LocationResponseDto> result = (adminLevel2 != null && !adminLevel2.isBlank())
                ? locationService.getLocationsByAdminLevel2(adminLevel2)
                : locationService.getAllLocations();

        return ApiResponse.success(result);
    }
}
