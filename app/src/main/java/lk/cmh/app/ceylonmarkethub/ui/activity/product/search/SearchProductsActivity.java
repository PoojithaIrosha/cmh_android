package lk.cmh.app.ceylonmarkethub.ui.activity.product.search;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import lk.cmh.app.ceylonmarkethub.data.adapter.lv.SearchLvAdapter;
import lk.cmh.app.ceylonmarkethub.data.model.product.Product;
import lk.cmh.app.ceylonmarkethub.databinding.ActivitySearchProductsBinding;

public class SearchProductsActivity extends AppCompatActivity {

    private static final String TAG = SearchProductsActivity.class.getSimpleName();
    private ActivitySearchProductsBinding binding;
    private SearchProductVM searchProductVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchProductsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        searchProductVM = new ViewModelProvider(this).get(SearchProductVM.class);

        binding.searchBox.requestFocus();

        binding.mainTopAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                searchProductVM.getSearchProduct(text);
                Log.i(TAG, text);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchProductVM.getSearchLiveData().observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                binding.lvSearch.setAdapter(new SearchLvAdapter(products));
            }
        });

        binding.searchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Intent intent = new Intent(SearchProductsActivity.this, ProductSearchViewActivity.class);
                    intent.putExtra("search", binding.searchBox.getText().toString());
                    startActivity(intent);
                    finish();
                    return true;
                }
                return false;
            }
        });
    }
}