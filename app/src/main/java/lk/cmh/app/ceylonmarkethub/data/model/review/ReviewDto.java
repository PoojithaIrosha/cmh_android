package lk.cmh.app.ceylonmarkethub.data.model.review;

public class ReviewDto {
    private String review;
    private int rating;
    private long productId;

public ReviewDto() {
    }

    public ReviewDto(String review, int rating, long productId) {
        this.review = review;
        this.rating = rating;
        this.productId = productId;
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

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    @Override
    public String toString() {
        return "ReviewDto{" +
                "review='" + review + '\'' +
                ", rating=" + rating +
                ", productId=" + productId +
                '}';
    }
}
