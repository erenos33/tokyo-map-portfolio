package me.tokyomap.dto.maps;

import lombok.Data;

/**
 * Google Place APIの詳細情報レスポンスを内包するラッパーDTO
 * resultフィールドに詳細情報オブジェクトを含む
 */
@Data
public class GooglePlaceDetailWrapper {
    private GooglePlaceDetailResponseDto result;
}