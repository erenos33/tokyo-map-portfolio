package me.tokyomap.service.impl;

import lombok.RequiredArgsConstructor;
import me.tokyomap.domain.location.repository.LocationRepository;
import me.tokyomap.dto.location.LocationResponseDto;
import me.tokyomap.exception.CustomException;
import me.tokyomap.exception.ErrorCode;
import me.tokyomap.mapper.LocationMapper;
import me.tokyomap.service.LocationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 地域情報に関するサービスの実装クラス
 * 全地域の取得や、市区町村単位での検索処理を提供
 */
@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;

    /**
     * 登録されている全地域情報を取得する
     */
    @Override
    public List<LocationResponseDto> getAllLocations() {
        return locationRepository.findAll().stream()
                .map(LocationMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 指定された市区町村（adminLevel2）に部分一致する地域情報を検索する
     * 該当がない場合は例外をスローする
     */
    @Override
    public List<LocationResponseDto> getLocationsByAdminLevel2(String adminLevel2) {
        List<LocationResponseDto> result = locationRepository.findByAdminLevel2ContainingIgnoreCase(adminLevel2).stream()
                .map(LocationMapper::toDto)
                .collect(Collectors.toList());

        if (result.isEmpty()) {
            throw new CustomException(ErrorCode.LOCATION_NOT_FOUND);
        }
        return result;
    }
}
