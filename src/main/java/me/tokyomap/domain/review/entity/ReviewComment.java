package me.tokyomap.domain.review.entity;

import jakarta.persistence.*;
import lombok.*;
import me.tokyomap.domain.common.BaseTimeEntity;
import me.tokyomap.domain.user.entity.User;

/**
 * レビューに対するコメントを保持するエンティティ
 * コメント作成者（ユーザー）と対象レビューとの関連を管理
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewComment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 500)
    private String content;

}
