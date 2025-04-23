package me.tokyomap.domain.location.repository;

import me.tokyomap.domain.location.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long> {

    /**
     * adminLevel2 (ex: Shibuya City) 기준으로 지역 조회
     */
    List<Location> findByAdminLevel2ContainingIgnoreCase(String adminLevel2);

    /**
     * adminLevel1 (ex: Tokyo) 기준으로 지역 조회
     */
    List<Location> findByAdminLevel1ContainingIgnoreCase(String adminLevel1);
}
