package me.tokyomap.domain.restaurant.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.tokyomap.domain.common.BaseTimeEntity;
import me.tokyomap.domain.user.entity.User;

/**
 * レストランの基本情報を保持するエンティティ
 * Google MapsのplaceId、登録者情報、営業時間、価格帯などを含む
 */
@Entity
@Getter
@NoArgsConstructor
public class Restaurant extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    private String phoneNumber;

    private String website;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    private String priceRange;

    @Column(name = "category")
    private String category;

    private Double rating;
    private Integer reviewCount;

    private Boolean isDineInAvailable;
    private Boolean isDeliveryAvailable;

    @Column(name = "opening_hours")
    private String openingHours;

    @Column(unique = true)
    private String placeId;

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
