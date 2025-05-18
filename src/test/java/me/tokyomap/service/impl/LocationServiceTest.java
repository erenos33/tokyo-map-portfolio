package me.tokyomap.service.impl;

import me.tokyomap.domain.location.entity.Location;
import me.tokyomap.domain.location.repository.LocationRepository;
import me.tokyomap.dto.location.LocationResponseDto;
import me.tokyomap.exception.CustomException;
import me.tokyomap.exception.ErrorCode;
import me.tokyomap.mapper.LocationMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class LocationServiceTest {

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private LocationServiceImpl locationService;

    @Test
    void 全地域一覧取得_成功() {
        // given 登録されている地域が1件存在する場合
        Location location = mock(Location.class);
        given(locationRepository.findAll()).willReturn(List.of(location));

        try (MockedStatic<LocationMapper> mapper = mockStatic(LocationMapper.class)) {
            mapper.when(() -> LocationMapper.toDto(location))
                    .thenReturn(new LocationResponseDto("Japan", "Tokyo", "Shibuya", "Dogenzaka", "1-1-1", "150-0001"));

            // when
            List<LocationResponseDto> result = locationService.getAllLocations();

            // then
            assertThat(result).hasSize(1);
            verify(locationRepository).findAll();
        }
    }

    @Test
    void 市区町村検索_成功() {
        // given "Shibuya" を含む地域が存在する場合
        String keyword = "Shibuya";
        Location location = mock(Location.class);
        given(locationRepository.findByAdminLevel2ContainingIgnoreCase(keyword))
                .willReturn(List.of(location));

        try (MockedStatic<LocationMapper> mapper = mockStatic(LocationMapper.class)) {
            mapper.when(() -> LocationMapper.toDto(location))
                    .thenReturn(new LocationResponseDto("Japan", "Tokyo", "Shibuya", "Dogenzaka", "1-1-1", "150-0001"));

            // when
            List<LocationResponseDto> result = locationService.getLocationsByAdminLevel2(keyword);

            // then
            assertThat(result).hasSize(1);
            verify(locationRepository).findByAdminLevel2ContainingIgnoreCase(keyword);
        }
    }

    @Test
    void 市区町村検索_失敗_該当なし() {
        // given 検索キーワードに該当する地域がない場合
        String keyword = "Nowhere";
        given(locationRepository.findByAdminLevel2ContainingIgnoreCase(keyword))
                .willReturn(Collections.emptyList());

        // when & then
        assertThatThrownBy(() -> locationService.getLocationsByAdminLevel2(keyword))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.LOCATION_NOT_FOUND.getMessage());
    }
}
