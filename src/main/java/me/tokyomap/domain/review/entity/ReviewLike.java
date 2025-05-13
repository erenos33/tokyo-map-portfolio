package me.tokyomap.domain.review.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.tokyomap.domain.user.entity.User;

/**
 * レビューに対する「いいね（Like）」を管理する中間エンティティ
 * ユーザーとレビューの組み合わせが一意になるようユニーク制約を設定
 */
@Entity
@Getter
@NoArgsConstructor
@Table(name = "review_likes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "review_id"})
})
public class ReviewLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Review review;

    public ReviewLike(User user, Review review) {
        this.user = user;
        this.review = review;
    }
}
