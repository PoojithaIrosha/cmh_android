package lk.cmh.app.ceylonmarkethub.data.repository;


import java.util.List;

import lk.cmh.app.ceylonmarkethub.data.model.category.Category;
import retrofit2.Call;
import retrofit2.http.GET;

public interface CategoryRepository {

    @GET("categories")
    Call<List<Category>> getCategories();

}
