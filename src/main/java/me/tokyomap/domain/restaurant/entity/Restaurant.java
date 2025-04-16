package me.tokyomap.domain.restaurant.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.tokyomap.domain.common.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor
public class Restaurant extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //기본 정보
    @Column(nullable = false)
    private String name; //음식점 이름

    @Column(nullable = false)
    private String address; //전체 주소

    private String phoneNumber; //전화번호(nullable)

    private String website; //웹사이트 URL(nullable)

    //위치 정보(구글맵 연동용)
    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    // 추가 정보
    private String priceRange; //가격대 (ex: "1000엔)
    private String category; //음식 종류(ex: 라멘, 이자카야, 돈카츠)

    private Double rating; //별점 평균(nullable)
    private Integer reviewCount; // 리뷰 개수(nullable)

    //이용 옵션
    private Boolean isDineInAvailable; //매장 내 식사 가능 여부
    private Boolean isDeliveryAvailable; //배달 가능 여부

    //영업 시간(ex: "월 ~금 11:00 ~ 22:00")
    private String openingHours;


    public Restaurant(String name, String address, Double latitude, Double longitude) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
