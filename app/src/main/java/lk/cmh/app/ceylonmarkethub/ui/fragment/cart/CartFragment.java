package lk.cmh.app.ceylonmarkethub.ui.fragment.cart;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import lk.cmh.app.ceylonmarkethub.data.adapter.rv.CartItemRvAdapter;
import lk.cmh.app.ceylonmarkethub.data.model.OrderDto;
import lk.cmh.app.ceylonmarkethub.data.model.cart.Cart;
import lk.cmh.app.ceylonmarkethub.data.model.cart.CartItem;
import lk.cmh.app.ceylonmarkethub.data.util.GsonUtil;
import lk.cmh.app.ceylonmarkethub.databinding.FragmentCartBinding;
import lk.cmh.app.ceylonmarkethub.ui.activity.auth.login.LoginActivity;
import lk.cmh.app.ceylonmarkethub.ui.activity.main.MainActivity;
import lk.cmh.app.ceylonmarkethub.ui.activity.orders.OrderConfirmationActivity;
import lk.cmh.app.ceylonmarkethub.ui.activity.wishlist.WishlistActivity;

public class CartFragment extends Fragment {

    private static final String TAG = CartFragment.class.getSimpleName();
    private FirebaseAuth firebaseAuth;
    private FragmentCartBinding binding;
    private CartViewModel cartViewModel;

    public CartFragment() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater);

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            userNotLoggedIn();
        } else {
            userLoggedIn();
        }

        binding.btnSignIn.setOnClickListener(v -> {
            Log.i(TAG, "Clicked");
            startActivity(new Intent(getContext(), LoginActivity.class));
        });

        binding.btnExploreItems.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MainActivity.class);
            intent.putExtra("menuItem", "home");
            startActivity(intent);
            getActivity().finish();
        });

        cartViewModel = new ViewModelProvider(this, ViewModelProvider.Factory.from(CartViewModel.initializer)).get(CartViewModel.class);

        cartViewModel.loadCart();

        cartViewModel.getCart().observe(getActivity(), new Observer<Cart>() {
            @Override
            public void onChanged(Cart cart) {
                if (cart != null) {
                    if (cart.getCartItems().size() < 1) {
                        userNotLoggedIn();
                    } else {
                        binding.tvCartHeaderCount.setText("Cart(" + cart.getCartItems().size() + ")");
                        binding.rvCartItems.setAdapter(new CartItemRvAdapter(cart.getCartItems(), cartViewModel));
                    }
                }
            }
        });

        cartViewModel.getSelectedItems().observe(getActivity(), new Observer<List<CartItem>>() {
            @Override
            public void onChanged(List<CartItem> cartItems) {
                AtomicReference<Double> total = new AtomicReference<>((double) 0);
                cartItems.forEach(cartItem -> {
                    // Update Prices
                    total.set(total.get() + (cartItem.getPrice() * cartItem.getQuantity()));
                });
                String format = String.format("%.2f", total.get());
                binding.tvCartTotal.setText("LKR " + format);
            }
        });

        binding.btnDelete.setOnClickListener(v -> {
            List<CartItem> items = cartViewModel.getSelectedItems().getValue();
            if (items == null || items.size() < 1) {
                Toast.makeText(getContext(), "Select an Item First!", Toast.LENGTH_SHORT).show();
                return;
            }

            new MaterialAlertDialogBuilder(getContext()).setTitle("Delete Item").setMessage("Are sure you want to delete this?").setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cartViewModel.deleteItems(items);
                }
            }).show();
        });

        binding.btnWishlist.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), WishlistActivity.class));
        });

        binding.srlReload.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                cartViewModel.loadCart();
                binding.srlReload.setRefreshing(false);
            }
        });

        binding.btnCheckOut.setOnClickListener(v -> {
            checkout();
        });

        return binding.getRoot();
    }

    private void userNotLoggedIn() {
        binding.srlReload.setVisibility(View.GONE);
        binding.clBottomBar2.setVisibility(View.GONE);
        binding.clNotLoggedIn.setVisibility(View.VISIBLE);
    }

    private void userLoggedIn() {
        binding.clNotLoggedIn.setVisibility(View.GONE);
        binding.srlReload.setVisibility(View.VISIBLE);
        binding.clBottomBar2.setVisibility(View.VISIBLE);
    }

    private void checkout() {
        List<CartItem> cartItems = cartViewModel.getSelectedItems().getValue();

        if (cartItems != null && cartItems.size() > 0) {
            List<OrderDto> orderDtoList = new ArrayList<>();
            AtomicLong colorId = new AtomicLong(0);
            AtomicLong sizeId = new AtomicLong(0);
            cartItems.forEach(cartItem -> {

                if (cartItem.getProductColor() != null) {
                    colorId.set(cartItem.getProductColor().getId());
                }

                if (cartItem.getProductSize() != null) {
                    sizeId.set(cartItem.getProductSize().getId());
                }

                OrderDto orderDto = new OrderDto(cartItem.getProduct().getId(), sizeId.get(), colorId.get(), cartItem.getQuantity(), cartItem.getProduct());
                orderDtoList.add(orderDto);
            });

            Log.i(TAG, orderDtoList.toString());
            Intent intent = new Intent(getContext(), OrderConfirmationActivity.class);
            String json = GsonUtil.getInstance().toJson(orderDtoList);
            intent.putExtra("order", json);
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "Please select an item", Toast.LENGTH_SHORT).show();
        }
    }
}