package lk.cmh.app.ceylonmarkethub.data.adapter.rv;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;

import lk.cmh.app.ceylonmarkethub.data.model.review.Review;
import lk.cmh.app.ceylonmarkethub.databinding.LayoutReviewBinding;

public class ReviewsRvAdapter extends RecyclerView.Adapter<ReviewsRvAdapter.ViewHolder> {

    private static final String TAG = ReviewsRvAdapter.class.getSimpleName();
    private List<Review> reviews;
    private LayoutReviewBinding binding;

    public ReviewsRvAdapter(List<Review> reviews) {
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        binding = LayoutReviewBinding.inflate(inflater);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.name.setText(review.getUser().getFirstName());
        holder.review.setText(review.getReview());

        Log.i(TAG, review.getCreatedAt().toString());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        holder.date.setText(dateFormat.format(review.getCreatedAt()));

        holder.ratingBar.setRating(review.getRating());
    }

    @Override
    public int getItemCount() {
        return reviews != null ? reviews.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, date, review;
        RatingBar ratingBar;
        public ViewHolder(@NonNull View itemView, LayoutReviewBinding binding) {
            super(itemView);
            name = binding.name;
            date = binding.date;
            review = binding.review;
            ratingBar = binding.ratingBar;
        }
    }

}
