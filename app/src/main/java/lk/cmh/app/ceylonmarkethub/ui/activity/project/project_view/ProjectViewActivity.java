package lk.cmh.app.ceylonmarkethub.ui.activity.project.project_view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import lk.cmh.app.ceylonmarkethub.R;
import lk.cmh.app.ceylonmarkethub.data.adapter.rv.ProductsRvAdapter;
import lk.cmh.app.ceylonmarkethub.data.adapter.rv.ReviewsRvAdapter;
import lk.cmh.app.ceylonmarkethub.data.model.product.Product;
import lk.cmh.app.ceylonmarkethub.data.model.product.ProductColors;
import lk.cmh.app.ceylonmarkethub.data.model.product.ProductSize;
import lk.cmh.app.ceylonmarkethub.data.model.projects.Project;
import lk.cmh.app.ceylonmarkethub.data.model.review.Review;
import lk.cmh.app.ceylonmarkethub.databinding.ActivityProjectViewBinding;
import lk.cmh.app.ceylonmarkethub.ui.activity.main.MainActivity;
import lk.cmh.app.ceylonmarkethub.ui.activity.product.product_view.ProductViewActivity;
import lk.cmh.app.ceylonmarkethub.ui.activity.seller.profile.SellerProfileActivity;
import lk.cmh.app.ceylonmarkethub.ui.activity.wishlist.WishlistActivity;

public class ProjectViewActivity extends AppCompatActivity {

    private ActivityProjectViewBinding binding;
    private ProjectViewVM projectViewVM;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProjectViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        projectViewVM = new ViewModelProvider(this).get(ProjectViewVM.class);
        storage = FirebaseStorage.getInstance();

        long projectId = getIntent().getExtras().getLong("projectId", -1);

        binding.backBtn.setOnClickListener(v -> {
            finish();
        });

        projectViewVM.getLoading().setValue(true);

        projectViewVM.getLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    binding.containerMain.setVisibility(View.GONE);
                    binding.containerLoding.setVisibility(View.VISIBLE);
                } else {
                    binding.containerLoding.setVisibility(View.GONE);
                    binding.containerMain.setVisibility(View.VISIBLE);
                }
            }
        });

        if (projectId == -1) {
            Intent intent = new Intent(ProjectViewActivity.this, MainActivity.class);
            intent.putExtra("menuItem", "home");
            startActivity(intent);
            finish();
        } else {
            projectViewVM.loadProduct(projectId);

            projectViewVM.getProject().observe(this, new Observer<Project>() {
                @Override
                public void onChanged(Project project) {
                    projectViewVM.getLoading().setValue(true);
                    if (project != null) {
                        binding.tvProjectTitle.setText(project.getName());
                        binding.tvDescription.setText(project.getDescription());

                        Double price = project.getBudget();
                        String format = String.format("%.2f", price);
                        String[] split = format.split("\\.");
                        double oldPrice = price + (price * 0.1);

                        binding.priceMain.setText(split[0]);
                        binding.priceCents.setText("." + split[1]);
                        binding.oldPrice.setText("LKR " + String.format("%.2f", oldPrice));
                        binding.sellerName.setText(project.getSeller().getFirstName() + " " + project.getSeller().getLastName());
                        binding.tvCategoryName.setText(project.getCategory());
                        binding.tvSkills.setText(Arrays.stream(project.getSkills().split(",")).collect(Collectors.joining(" · ")));

                        binding.sellerDetails.setOnClickListener(v -> {
                            Intent intent = new Intent(ProjectViewActivity.this, SellerProfileActivity.class);
                            intent.putExtra("sellerId", project.getSeller().getId());
                            startActivity(intent);
                        });

                        binding.btnSeller.setOnClickListener(v -> {
                            Intent intent = new Intent(ProjectViewActivity.this, SellerProfileActivity.class);
                            intent.putExtra("sellerId", project.getSeller().getId());
                            startActivity(intent);
                        });

                        ArrayList<SlideModel> imageList = new ArrayList<>();
                        project.getProjectImages().forEach(projectImage -> {
                            storage.getReference("projects/" + projectImage.getUrl()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imageList.add(new SlideModel(uri.toString(), ScaleTypes.CENTER_INSIDE));
                                    binding.imageSlider.setImageList(imageList);
                                    projectViewVM.getLoading().setValue(false);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.i("TEST", e.getMessage());
                                }
                            });
                        });

                        binding.btnCall.setOnClickListener(v -> {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + project.getSeller().getAddress().getMobileNumber()));
                            startActivity(intent);
                        });

                        binding.btnMessage.setOnClickListener(v -> {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("sms:" + project.getSeller().getAddress().getMobileNumber()));
                            startActivity(intent);
                        });

                        binding.btnWhatsapp.setOnClickListener(v -> {
                            String phoneNumber = "+94" + project.getSeller().getAddress().getMobileNumber();
                            Intent sendIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/" + phoneNumber));
                            sendIntent.setPackage("com.whatsapp");
                            if (sendIntent.resolveActivity(getPackageManager()) != null) {
                                startActivity(sendIntent);
                            } else {
                                Toast.makeText(ProjectViewActivity.this, "WhatsApp is not installed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    }
}