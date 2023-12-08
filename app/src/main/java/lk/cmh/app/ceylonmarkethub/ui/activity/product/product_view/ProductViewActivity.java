package lk.cmh.app.ceylonmarkethub.ui.activity.product.product_view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lk.cmh.app.ceylonmarkethub.R;
import lk.cmh.app.ceylonmarkethub.data.adapter.rv.ProductsRvAdapter;
import lk.cmh.app.ceylonmarkethub.data.adapter.rv.ReviewsRvAdapter;
import lk.cmh.app.ceylonmarkethub.data.model.OrderDto;
import lk.cmh.app.ceylonmarkethub.data.model.cart.CartItemDto;
import lk.cmh.app.ceylonmarkethub.data.model.product.Product;
import lk.cmh.app.ceylonmarkethub.data.model.product.ProductColors;
import lk.cmh.app.ceylonmarkethub.data.model.product.ProductSize;
import lk.cmh.app.ceylonmarkethub.data.model.review.Review;
import lk.cmh.app.ceylonmarkethub.data.util.GsonUtil;
import lk.cmh.app.ceylonmarkethub.databinding.ActivityProductViewBinding;
import lk.cmh.app.ceylonmarkethub.ui.activity.auth.login.LoginActivity;
import lk.cmh.app.ceylonmarkethub.ui.activity.orders.OrderConfirmationActivity;
import lk.cmh.app.ceylonmarkethub.ui.activity.main.MainActivity;
import lk.cmh.app.ceylonmarkethub.ui.activity.seller.profile.SellerProfileActivity;
import lk.cmh.app.ceylonmarkethub.ui.activity.wishlist.WishlistActivity;
import lk.cmh.app.ceylonmarkethub.ui.activity.wishlist.WishlistViewModel;
import lk.cmh.app.ceylonmarkethub.ui.fragment.cart.CartViewModel;

public class ProductViewActivity extends AppCompatActivity {

    private static final String TAG = ProductViewActivity.class.getSimpleName();
    private ActivityProductViewBinding binding;
    private ProductViewVM productViewVM;
    private FirebaseStorage storage;
    private CartViewModel cartViewModel;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private WishlistViewModel wishlistViewModel;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        bottomSheetBehavior = BottomSheetBehavior.from(binding.standardBottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        storage = FirebaseStorage.getInstance();
        productViewVM = new ViewModelProvider(this).get(ProductViewVM.class);
        cartViewModel = new ViewModelProvider(this, ViewModelProvider.Factory.from(CartViewModel.initializer)).get(CartViewModel.class);
        wishlistViewModel = new ViewModelProvider(this, ViewModelProvider.Factory.from(WishlistViewModel.initializer)).get(WishlistViewModel.class);

        binding.backBtn.setOnClickListener(v -> {
            finish();
        });

        binding.mainTopBarCartBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ProductViewActivity.this, MainActivity.class);
            intent.putExtra("menuItem", "cart");
            startActivity(intent);
        });

        productViewVM.getLoading().setValue(true);

        productViewVM.getLoading().observe(this, new Observer<Boolean>() {
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

        long productId = getIntent().getExtras().getLong("productId", -1);
        if (productId == -1) {
            Intent intent = new Intent(ProductViewActivity.this, MainActivity.class);
            intent.putExtra("menuItem", "home");
            startActivity(intent);
            finish();
        } else {
            loadProductDetails(productId);

            binding.mainTopBarWishlist.setOnClickListener(v -> {
                wishlistViewModel.addToWishlist(productId);
                Toast.makeText(this, "Product added to wishlist", Toast.LENGTH_SHORT).show();

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    startActivity(new Intent(ProductViewActivity.this, WishlistActivity.class));
                }, 1000);
            });

            binding.btnAddReview.setOnClickListener(v -> {
                String review = binding.etReview.getText().toString();
                int rating = binding.ratingBar.getRating() == 0 ? 1 : (int) binding.ratingBar.getRating() + 1;

                if (review.isEmpty()) {
                    Toast.makeText(this, "Review cannot be empty", Toast.LENGTH_SHORT).show();
                } else {
                    productViewVM.addReview(productId, review, rating);
                    binding.etReview.setText("");
                    binding.ratingBar.setRating(0);
                    Log.i(TAG, "Review: " + review + " | rating: " + rating);
                }
            });

        }
    }

    private void loadProductDetails(long id) {
        productViewVM.loadProduct(id);

        productViewVM.getProduct().observe(this, new Observer<Product>() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onChanged(Product product) {
                productViewVM.getLoading().setValue(true);
                if (product != null) {
                    Double price = product.getPrice();
                    String format = String.format("%.2f", price);
                    String[] split = format.split("\\.");
                    double oldPrice = price + (price * 0.1);

                    binding.tvProductTitle.setText(product.getName());
                    binding.priceMain.setText(split[0]);
                    binding.priceCents.setText("." + split[1]);
                    binding.oldPrice.setText("LKR " + String.format("%.2f", oldPrice));
                    binding.tvCategoryName.setText(product.getCategory().getName());
                    binding.tvDescription.setText(product.getDescription());
                    binding.sellerName.setText(product.getSeller().getFirstName() + " " + product.getSeller().getLastName());
                    binding.condition.setText(product.getProductCondition());

                    List<Review> reviewList = product.getReviews();
                    binding.rvReviews.setAdapter(new ReviewsRvAdapter(reviewList.stream().sorted(Comparator.comparing(Review::getCreatedAt).reversed()).collect(Collectors.toList())));

                    binding.sellerDetails.setOnClickListener(v -> {
                        Intent intent = new Intent(ProductViewActivity.this, SellerProfileActivity.class);
                        intent.putExtra("sellerId", product.getSeller().getId());
                        startActivity(intent);
                    });

                    binding.bsTvTitle.setText(product.getName());
                    binding.bsTvPrice.setText("LKR " + format);

                    List<ProductColors> productColors = product.getProductColors();
                    if (productColors.size() > 0) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 0; i < productColors.size(); i++) {
                            stringBuilder.append(productColors.get(i).getName().toUpperCase());

                            if (i < productColors.size() - 1) {
                                stringBuilder.append(" · ");
                            }
                        }
                        binding.tvColors.setText(stringBuilder.toString());
                    } else {
                        binding.mdColors.setVisibility(View.GONE);
                        binding.tvColorsTitle.setVisibility(View.GONE);
                        binding.tvColors.setVisibility(View.GONE);
                    }

                    List<ProductSize> productSizes = product.getProductSizes();
                    if (productSizes.size() > 0) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 0; i < productSizes.size(); i++) {
                            stringBuilder.append(productSizes.get(i).getName().toUpperCase());

                            if (i < productSizes.size() - 1) {
                                stringBuilder.append(" · ");
                            }
                        }
                        binding.tvSizes.setText(stringBuilder.toString());
                    } else {
                        binding.mdSizes.setVisibility(View.GONE);
                        binding.tvSizesTitle.setVisibility(View.GONE);
                        binding.tvSizes.setVisibility(View.GONE);
                    }

                    ArrayList<SlideModel> imageList = new ArrayList<>();
                    product.getProductImages().forEach(productImage -> {
                        storage.getReference("products/" + productImage.getUrl()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.i(TAG, uri.toString());
                                imageList.add(new SlideModel(uri.toString(), ScaleTypes.CENTER_INSIDE));
                                binding.imageSlider.setImageList(imageList);
                                productViewVM.getLoading().setValue(false);

                                Picasso.get().load(uri).placeholder(R.drawable.placeholder_category_item_image).fit().centerCrop().into(binding.bsProductImage);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i(TAG, e.getMessage());
                            }
                        });
                    });
                    Log.i(TAG, "TEST");

                    productViewVM.loadProductsByCategory(product.getCategory().getId(), product.getId());

                    productViewVM.getRecommendations().observe(ProductViewActivity.this, new Observer<List<Product>>() {
                        @Override
                        public void onChanged(List<Product> products) {
                            Log.i(TAG, products.toString());
                            ProductsRvAdapter adapter = new ProductsRvAdapter();
                            adapter.setProductList(products);
                            binding.rvRecommendations.setAdapter(adapter);
                            productViewVM.getLoading().setValue(false);
                        }
                    });

                    binding.btnAddToCart.setOnClickListener(v -> {
                        showOrHideBottomSheet("addtocart");
                    });

                    binding.btnBuyNow.setOnClickListener(v -> {
                        showOrHideBottomSheet("buynow");
                    });

                    if (product.getProductColors() == null || product.getProductColors().size() < 1) {
                        binding.llColorsTitleContainer.setVisibility(View.GONE);
                    } else {
                        productViewVM.getProductColors().setValue(product.getProductColors());
                        productViewVM.getSelectedColor().setValue("none");
                        for (ProductColors productColor : product.getProductColors()) {
                            Chip chip = new Chip(ProductViewActivity.this);
                            chip.setId(View.generateViewId());
                            chip.setText(productColor.getName().toUpperCase());
                            chip.setCheckable(true);
                            chip.setCheckedIcon(getDrawable(R.drawable.ic_check));
                            binding.chipGroupColors.addView(chip);
                        }
                    }

                    if (product.getProductSizes() == null || product.getProductSizes().size() < 1) {
                        binding.llSizesTitleContainer.setVisibility(View.GONE);
                    } else {
                        productViewVM.getProductSizes().setValue(product.getProductSizes());
                        productViewVM.getSelectedSize().setValue("none");
                        for (ProductSize productSize : product.getProductSizes()) {
                            Chip chip = new Chip(ProductViewActivity.this);
                            chip.setId(View.generateViewId());
                            chip.setText(productSize.getName().toUpperCase());
                            chip.setCheckable(true);
                            chip.setCheckedIcon(getDrawable(R.drawable.ic_check));
                            binding.chipGroupSizes.addView(chip);
                        }
                    }

                    binding.chipGroupColors.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
                        @Override
                        public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                            if (checkedIds.size() > 0) {
                                Chip chip = group.findViewById(checkedIds.get(0));
                                productViewVM.getSelectedColor().setValue(chip.getText().toString().toUpperCase());
                                binding.bsTvSelectedColor.setText(chip.getText().toString().toUpperCase());
                            } else {
                                binding.bsTvSelectedColor.setText("");
                            }
                        }
                    });

                    binding.chipGroupSizes.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
                        @Override
                        public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                            if (checkedIds.size() > 0) {
                                Chip chip = group.findViewById(checkedIds.get(0));
                                productViewVM.getSelectedSize().setValue(chip.getText().toString().toUpperCase());
                                binding.bsTvSelectedSize.setText(chip.getText().toString().toUpperCase());
                            } else {
                                binding.bsTvSelectedSize.setText("");
                            }
                        }
                    });


                    // Add to cart
                    binding.bsBtnAddToCart.setOnClickListener(v -> {
                        addToCart(id, Integer.parseInt(binding.bsTvQty.getText().toString()));
                    });

                    // Buy Now
                    binding.bsBtnBuyNow.setOnClickListener(v -> {
                        buyNow(product, Integer.parseInt(binding.bsTvQty.getText().toString()));
                    });

                    binding.bsIncrementQty.setOnClickListener(v -> {
                        int qty = Integer.parseInt(binding.bsTvQty.getText().toString());
                        if (product.getQuantity() > qty) {
                            binding.bsTvQty.setText(String.valueOf(qty + 1));
                        }
                    });

                    binding.bsDecrementQty.setOnClickListener(v -> {
                        int qty = Integer.parseInt(binding.bsTvQty.getText().toString());
                        if (qty > 1) {
                            binding.bsTvQty.setText(String.valueOf(qty - 1));
                        }
                    });

                }
            }
        });
    }

    private void showOrHideBottomSheet(String type) {
        switch (type) {
            case "buynow":
                binding.bsBtnAddToCart.setVisibility(View.GONE);
                binding.bsBtnBuyNow.setVisibility(View.VISIBLE);
                break;
            case "addtocart":
                binding.bsBtnAddToCart.setVisibility(View.VISIBLE);
                binding.bsBtnBuyNow.setVisibility(View.GONE);
                break;
            default:
                break;
        }

        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    private void addToCart(long productId, int quantity) {
        String color = productViewVM.getSelectedColor().getValue();
        String size = productViewVM.getSelectedSize().getValue();

        if (color != null && color.equals("none")) {
            Toast.makeText(this, "Select a color", Toast.LENGTH_SHORT).show();
        } else if (size != null && size.equals("none")) {
            Toast.makeText(this, "Select a size", Toast.LENGTH_SHORT).show();
        } else {
            List<ProductColors> productColorsList = productViewVM.getProductColors().getValue();
            List<ProductSize> productSizeList = productViewVM.getProductSizes().getValue();

            long colorId = 0;
            long sizeId = 0;

            if (productColorsList != null) {
                Optional<ProductColors> optionalProductColor = productColorsList.stream().filter(pc -> pc.getName().equalsIgnoreCase(color)).findFirst();
                if (optionalProductColor.isPresent()) {
                    colorId = optionalProductColor.get().getId();
                }
            }

            if (productSizeList != null) {
                Optional<ProductSize> optionalProductSize = productSizeList.stream().filter(ps -> ps.getName().equalsIgnoreCase(size)).findFirst();
                if (optionalProductSize.isPresent()) {
                    sizeId = optionalProductSize.get().getId();
                }
            }

            if (firebaseAuth.getCurrentUser() != null) {
                CartItemDto cartItemDto = new CartItemDto(productId, quantity, sizeId, colorId);

                cartViewModel.addToCart(cartItemDto, () -> {
                    Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show();
                    showOrHideBottomSheet("");
                });
            } else {
                startActivity(new Intent(ProductViewActivity.this, LoginActivity.class));
            }

        }
    }

    public void buyNow(Product product, Integer quantity) {
        String color = productViewVM.getSelectedColor().getValue();
        String size = productViewVM.getSelectedSize().getValue();

        if (color != null && color.equals("none")) {
            Toast.makeText(ProductViewActivity.this, "Select a color", Toast.LENGTH_SHORT).show();
        } else if (size != null && size.equals("none")) {
            Toast.makeText(ProductViewActivity.this, "Select a size", Toast.LENGTH_SHORT).show();
        } else {
            List<ProductColors> productColorsList = productViewVM.getProductColors().getValue();
            List<ProductSize> productSizeList = productViewVM.getProductSizes().getValue();

            long colorId = 0;
            long sizeId = 0;

            if (productColorsList != null) {
                Optional<ProductColors> optionalProductColor = productColorsList.stream().filter(pc -> pc.getName().equalsIgnoreCase(color)).findFirst();
                if (optionalProductColor.isPresent()) {
                    colorId = optionalProductColor.get().getId();
                }
            }

            if (productSizeList != null) {
                Optional<ProductSize> optionalProductSize = productSizeList.stream().filter(ps -> ps.getName().equalsIgnoreCase(size)).findFirst();
                if (optionalProductSize.isPresent()) {
                    sizeId = optionalProductSize.get().getId();
                }
            }

            if (firebaseAuth.getCurrentUser() != null) {
                Intent intent = new Intent(ProductViewActivity.this, OrderConfirmationActivity.class);

                OrderDto orderDto = new OrderDto(product.getId(), sizeId, colorId, quantity, product);

                List<OrderDto> dtoList = new ArrayList<>();
                dtoList.add(orderDto);

                String json = GsonUtil.getInstance().toJson(dtoList);
                intent.putExtra("order", json);
                startActivity(intent);
            } else {
                startActivity(new Intent(ProductViewActivity.this, LoginActivity.class));
            }

        }
    }
}