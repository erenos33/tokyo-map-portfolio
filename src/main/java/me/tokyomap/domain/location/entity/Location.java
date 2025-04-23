package me.tokyomap.domain.location.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //예 : "JAPAN"
    @Column(nullable = false)
    private String country;

    //예 : "Tokyo"
    @Column(nullable = false)
    private String adminLevel1;

    //예 : "Shibuya City"
    @Column(nullable = false)
    private String adminLevel2;

    //예 : "Dogenzaka"
    @Column(nullable = false)
    private String locality;

    //예 : "1 chome-2-3"
    @Column
    private String streetAddress;

    //예 : "150-0043"
    @Column
    private String postalCode;
}
