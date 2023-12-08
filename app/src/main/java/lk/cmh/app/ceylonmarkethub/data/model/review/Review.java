package lk.cmh.app.ceylonmarkethub.data.model.review;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

import lk.cmh.app.ceylonmarkethub.data.model.user.User;

public class Review {
    private Long id;

    private String review;

    private int rating;

    private User user;
    private Date createdAt;

    public Review() {
    }

    public Review(Long id, String review, int rating) {
        this.id = id;
        this.review = review;
        this.rating = rating;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", review='" + review + '\'' +
                ", rating=" + rating +
                ", user=" + user +
                ", timestamp=" + createdAt +
                '}';
    }
}
