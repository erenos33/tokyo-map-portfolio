package me.tokyomap.domain.review.entity;

import jakarta.persistence.*;
import lombok.*;
import me.tokyomap.domain.common.BaseTimeEntity;
import me.tokyomap.domain.restaurant.entity.Restaurant;
import me.tokyomap.domain.user.entity.User;

/**
 * レストランに対するレビュー情報を保持するエンティティ
 * レビューの投稿者（ユーザー）と対象レストランへの関連を管理
 */
@Entity
@Table(name = "review")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"user", "restaurant"})
public class Review extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false)
    private int rating;
}
