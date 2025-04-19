package me.tokyomap.domain.review.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.tokyomap.domain.user.entity.User;
import me.tokyomap.domain.review.entity.Review;

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

    //좋아요 누른 유저
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    //좋아요 누른 리뷰
    @ManyToOne(fetch = FetchType.LAZY)
    private Review review;

    public ReviewLike(User user, Review review) {
        this.user = user;
        this.review = review;
    }
}
