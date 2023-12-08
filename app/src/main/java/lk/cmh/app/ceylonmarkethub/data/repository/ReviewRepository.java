package lk.cmh.app.ceylonmarkethub.data.repository;

import lk.cmh.app.ceylonmarkethub.data.model.review.Review;
import lk.cmh.app.ceylonmarkethub.data.model.review.ReviewDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ReviewRepository {

    @POST("products/reviews")
    Call<Review> addReview(@Body ReviewDto reviewDto);

}
