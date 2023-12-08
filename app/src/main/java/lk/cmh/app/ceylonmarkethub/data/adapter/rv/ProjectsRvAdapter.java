package lk.cmh.app.ceylonmarkethub.data.adapter.rv;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.List;

import lk.cmh.app.ceylonmarkethub.R;
import lk.cmh.app.ceylonmarkethub.data.model.product.Product;
import lk.cmh.app.ceylonmarkethub.data.model.projects.Project;
import lk.cmh.app.ceylonmarkethub.databinding.LayoutMainProductItemBinding;
import lk.cmh.app.ceylonmarkethub.databinding.LayoutProjectItemBinding;
import lk.cmh.app.ceylonmarkethub.ui.activity.project.project_view.ProjectViewActivity;

public class ProjectsRvAdapter extends RecyclerView.Adapter<ProjectsRvAdapter.ViewHolder> {

    private static final String TAG = ProjectsRvAdapter.class.getSimpleName();
    private List<Project> projectList;
    private FirebaseStorage storage;

    public ProjectsRvAdapter(List<Project> projectList) {
        this.projectList = projectList;
        storage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        LayoutProjectItemBinding binding = LayoutProjectItemBinding.inflate(inflater);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Project project = projectList.get(position);

        holder.productName.setText(project.getName());
        holder.duration.setText(project.getDuration());
        holder.skills.setText(project.getSkills());

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

        Double price = project.getBudget();
        String format = String.format("%.2f", price);
        String[] split = format.split("\\.");
        holder.priceMain.setText(split[0]);
        holder.priceCents.setText("." + split[1]);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ProjectViewActivity.class);
            intent.putExtra("projectId", project.getId());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return projectList != null ? projectList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView productImage;
        TextView productName, currency, priceMain, priceCents, duration, skills;

        public ViewHolder(@NonNull View itemView, LayoutProjectItemBinding binding) {
            super(itemView);
            productImage = binding.productImage;
            productName = binding.productName;
            currency = binding.currency;
            priceMain = binding.priceMain;
            priceCents = binding.priceCents;
            duration = binding.tvDuration;
            skills = binding.tvSkills;
        }
    }

}
