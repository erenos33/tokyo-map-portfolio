package me.tokyomap.mapper;

import me.tokyomap.domain.location.entity.Location;
import me.tokyomap.dto.location.LocationResponseDto;
/**
 * LocationエンティティをLocationResponseDtoに変換するマッパークラス
 */
public class LocationMapper {

    /**
     * 地域エンティティをレスポンスDTOに変換する
     */
    public static LocationResponseDto toDto(Location location) {
        return new LocationResponseDto(
                location.getCountry(),
                location.getAdminLevel1(),
                location.getAdminLevel2(),
                location.getLocality(),
                location.getStreetAddress(),
                location.getPostalCode()
        );
    }
}
