package lk.cmh.app.ceylonmarkethub.ui.fragment.account;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.Map;

import lk.cmh.app.ceylonmarkethub.R;
import lk.cmh.app.ceylonmarkethub.data.model.user.User;
import lk.cmh.app.ceylonmarkethub.data.repository.UserRepository;
import lk.cmh.app.ceylonmarkethub.data.util.RetrofitUtil;
import lk.cmh.app.ceylonmarkethub.databinding.FragmentAccountBinding;
import lk.cmh.app.ceylonmarkethub.ui.activity.auth.AuthViewModel;
import lk.cmh.app.ceylonmarkethub.ui.activity.auth.login.LoginActivity;
import lk.cmh.app.ceylonmarkethub.ui.activity.main.MainActivity;
import lk.cmh.app.ceylonmarkethub.ui.activity.order_history.OrderHistoryActivity;
import lk.cmh.app.ceylonmarkethub.ui.activity.seller.my_projects.MyProjectsActivity;
import lk.cmh.app.ceylonmarkethub.ui.activity.seller.my_orders.MyOrdersActivity;
import lk.cmh.app.ceylonmarkethub.ui.activity.seller.my_products.MyProductsActivity;
import lk.cmh.app.ceylonmarkethub.ui.activity.settings.SettingsActivity;
import lk.cmh.app.ceylonmarkethub.ui.activity.wishlist.WishlistActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountFragment extends Fragment {

    private FragmentAccountBinding binding;
    private FirebaseAuth firebaseAuth;

    private UserRepository userRepository;
    private AuthViewModel authViewModel;

    public AccountFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentAccountBinding.inflate(getLayoutInflater());
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        firebaseAuth = FirebaseAuth.getInstance();

        userRepository = RetrofitUtil.getInstance().getRepository(UserRepository.class, getActivity().getSharedPreferences("auth", Context.MODE_PRIVATE));

        SharedPreferences auth = getContext().getSharedPreferences("auth", Context.MODE_PRIVATE);
        String role = auth.getString("role", "ROLE_CUSTOMER");

        if(auth.contains("accessToken")) {
            if(role.equals("ROLE_SELLER")) {
                binding.llBecomeASeller.setVisibility(View.GONE);
                binding.llSellerOptions.setVisibility(View.VISIBLE);
                binding.llSellerOptions2.setVisibility(View.VISIBLE);
            }else {
                binding.llBecomeASeller.setVisibility(View.VISIBLE);
                binding.llSellerOptions.setVisibility(View.GONE);
                binding.llSellerOptions2.setVisibility(View.GONE);
            }
        }else {
            binding.btnSettings.setVisibility(View.GONE);
            binding.mdSellerOptions.setVisibility(View.GONE);
            binding.tvSellerOptions.setVisibility(View.GONE);
            binding.llSellerOptions.setVisibility(View.GONE);
            binding.llSellerOptions2.setVisibility(View.GONE);
            binding.llBecomeASeller.setVisibility(View.GONE);
        }

        if (firebaseAuth.getCurrentUser() != null) {
            userRepository.getMe().enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    User user = response.body();

                    String firstName = user.getFirstName() != null ? user.getFirstName() : "";
                    String lastName = user.getLastName() != null ? user.getLastName() : "";

                    if (user.getPicture() != null && !user.getPicture().isEmpty()) {
                        Picasso.get().load(user.getPicture()).placeholder(R.drawable.placeholder_category_item_image).fit().centerCrop().into(binding.profilePicture);
                    }

                    binding.tvLoggedUsername.setText(firstName + " " + lastName);
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    t.printStackTrace();
                }
            });
            binding.llLoggedInUser.setVisibility(View.VISIBLE);
            binding.tvSigning.setVisibility(View.GONE);
        } else {
            binding.tvSigning.setVisibility(View.VISIBLE);
            binding.llLoggedInUser.setVisibility(View.GONE);
            binding.tvSigning.setOnClickListener(v -> {
                startActivity(new Intent(getContext(), LoginActivity.class));
                getActivity().finish();
            });
        }


        binding.llCart.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MainActivity.class);
            intent.putExtra("menuItem", "cart");
            startActivity(intent);
        });

        binding.llWishlist.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), WishlistActivity.class));
        });

        binding.llSellerMyProjects.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), MyProjectsActivity.class));
        });

        if (firebaseAuth.getCurrentUser() != null) {
            binding.btnLogout.setOnClickListener(v -> {

                authViewModel.signOut(getContext());

                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.putExtra("menuItem", "home");
                startActivity(intent);
                getActivity().finish();
            });
        } else {
            binding.btnLogout.setVisibility(View.GONE);
        }

        binding.llOrderHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseAuth.getCurrentUser() != null) {
                    startActivity(new Intent(getContext(), OrderHistoryActivity.class));
                } else {
                    startActivity(new Intent(getContext(), LoginActivity.class));
                }
            }
        });

        binding.llBecomeASeller.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(getContext())
                    .setTitle("Confirmation")
                    .setMessage("Are you sure you want to become a seller?")
                    .setNegativeButton("Cancel", (dialog, which) -> {
                    })
                    .setPositiveButton("Yes", (dialog, which) -> {
                        userRepository.becomeSeller().enqueue(new Callback<Map<String, String>>() {
                            @Override
                            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                                Map<String, String> body = response.body();
                                if(body.get("message").equals("success")) {
                                    new MaterialAlertDialogBuilder(getContext())
                                            .setTitle("Congratulations")
                                            .setMessage("You are now a Seller. We wish you to sell more and earn more. Please logout and sign in again.")
                                            .setPositiveButton("Logout", (dialog, which) -> {
                                                authViewModel.signOut(getContext());
                                                startActivity(new Intent(getContext(), LoginActivity.class));
                                            }).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                                t.printStackTrace();
                            }
                        });
                    }).show();
        });

        binding.llSellerMyProducts.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), MyProductsActivity.class));
        });

        binding.llSellersOrders.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), MyOrdersActivity.class));
        });

        binding.btnSettings.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), SettingsActivity.class));
        });

        return binding.getRoot();
    }
}
