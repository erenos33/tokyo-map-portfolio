package me.tokyomap.domain.location.repository;

import me.tokyomap.domain.location.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long> {

    /**
     * adminLevel2（例: Shibuya City）に部分一致する地域を取得
     */
    List<Location> findByAdminLevel2ContainingIgnoreCase(String adminLevel2);

    /**
     * ※ 現時点では未使用
     * adminLevel1（例: Tokyo）に部分一致する地域を取得するための予備メソッド
     */
    List<Location> findByAdminLevel1ContainingIgnoreCase(String adminLevel1);
}
