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

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String adminLevel1;

    @Column(nullable = false)
    private String adminLevel2;

    @Column(nullable = false)
    private String locality;

    @Column
    private String streetAddress;

    @Column
    private String postalCode;
}
