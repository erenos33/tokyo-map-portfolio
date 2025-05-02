package me.tokyomap.domain.restaurant.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.tokyomap.domain.common.BaseTimeEntity;
import me.tokyomap.domain.user.entity.User;

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

    @Column(name = "category")
    private String category; //음식 종류(ex: 라멘, 이자카야, 돈카츠)

    private Double rating; //별점 평균(nullable)
    private Integer reviewCount; // 리뷰 개수(nullable)

    //이용 옵션
    private Boolean isDineInAvailable; //매장 내 식사 가능 여부
    private Boolean isDeliveryAvailable; //배달 가능 여부

    //영업 시간(ex: "월 ~금 11:00 ~ 22:00")
    @Column(name = "opening_hours")
    private String openingHours;


    @Column(unique = true)
    private String placeId; // 🔸 Google Place ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registered_by")
    private User registeredBy;


    @Builder
    public Restaurant(String name, String address, Double latitude, Double longitude,
                      Double rating, String placeId, User registeredBy,
                      String priceRange, String openingHours, String phoneNumber) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rating = rating;
        this.placeId = placeId;
        this.registeredBy = registeredBy;
        this.priceRange = priceRange;
        this.openingHours = openingHours;
        this.phoneNumber = phoneNumber;
    }
}
