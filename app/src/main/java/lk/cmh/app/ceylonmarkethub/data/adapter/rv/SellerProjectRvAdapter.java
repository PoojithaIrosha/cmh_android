package lk.cmh.app.ceylonmarkethub.data.adapter.rv;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.util.List;

import lk.cmh.app.ceylonmarkethub.R;
import lk.cmh.app.ceylonmarkethub.data.model.product.Product;
import lk.cmh.app.ceylonmarkethub.data.model.projects.Project;
import lk.cmh.app.ceylonmarkethub.data.repository.ProductRepository;
import lk.cmh.app.ceylonmarkethub.data.repository.ProjectRepository;
import lk.cmh.app.ceylonmarkethub.databinding.LayoutSellerMyProductItemBinding;
import lk.cmh.app.ceylonmarkethub.databinding.LayoutSellerMyProjectItemBinding;
import lk.cmh.app.ceylonmarkethub.ui.activity.product.product_view.ProductViewActivity;
import lk.cmh.app.ceylonmarkethub.ui.activity.project.project_view.ProjectViewActivity;
import lk.cmh.app.ceylonmarkethub.ui.fragment.seller.products.update_product.UpdateProductFragment;
import lk.cmh.app.ceylonmarkethub.ui.fragment.seller.projects.update_project.UpdateProjectFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellerProjectRvAdapter extends RecyclerView.Adapter<SellerProjectRvAdapter.ViewHolder> {

    private static final String TAG = SellerProjectRvAdapter.class.getSimpleName();
    private List<Project> projects;
    private FirebaseStorage storage;
    private ProjectRepository projectRepository;

    public SellerProjectRvAdapter(List<Project> projects, ProjectRepository projectsRepository) {
        this.projects = projects;
        this.storage = FirebaseStorage.getInstance();
        this.projectRepository = projectsRepository;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        LayoutSellerMyProjectItemBinding binding = LayoutSellerMyProjectItemBinding.inflate(inflater);
        binding.getRoot().setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Project project = projects.get(position);

        holder.title.setText(project.getName());
        holder.currency.setText("LKR");
        Double price = project.getBudget();
        String format = String.format("%.2f", price);
        String[] split = format.split("\\.");
        holder.price.setText(split[0]);
        holder.cents.setText("." + split[1]);
        holder.duration.setText(project.getDuration());
        holder.category.setText(project.getCategory());
        holder.checkBox.setChecked(project.isEnabled());

        storage.getReference("projects/" + project.getProjectImages().get(0).getUrl()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).placeholder(R.drawable.placeholder_category_item_image).fit().centerCrop().into(holder.productImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, e.getMessage());
            }
        });

        holder.productImage.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ProjectViewActivity.class);
            intent.putExtra("projectId", project.getId());
            v.getContext().startActivity(intent);
        });

        holder.btnEdit.setOnClickListener(v -> {
            UpdateProjectFragment updateProjectFragment = new UpdateProjectFragment();
            Bundle bundle = new Bundle();
            bundle.putLong("pid", project.getId());
            updateProjectFragment.setArguments(bundle);
            ((FragmentActivity) v.getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, updateProjectFragment).commit();
        });

        holder.checkBox.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(v.getContext())
                    .setTitle("Confirmation")
                    .setMessage("Are you sure you want to " + (holder.checkBox.isChecked() ? "enable" : "disable") + " the project")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        projectRepository.enableProject(project.getId()).enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                Log.i(TAG, response.body().toString());
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {
                                holder.checkBox.setChecked(!holder.checkBox.isChecked());
                            }
                        });
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        holder.checkBox.setChecked(!holder.checkBox.isChecked());
                    })
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return projects != null ? projects.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView productImage;
        TextView title, currency, price, cents, duration, category;
        ImageButton btnEdit;
        MaterialCheckBox checkBox;

        public ViewHolder(@NonNull View itemView, LayoutSellerMyProjectItemBinding binding) {
            super(itemView);
            productImage = binding.productImage;
            title = binding.tvProductTitle;
            currency = binding.tvCurrency;
            price = binding.tvPrice;
            cents = binding.tvCents;
            duration = binding.tvDuration;
            category = binding.tvCategory;
            btnEdit = binding.btnEdit;
            checkBox = binding.checkBox;
        }
    }

}
