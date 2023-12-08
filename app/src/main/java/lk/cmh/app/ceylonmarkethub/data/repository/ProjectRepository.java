package lk.cmh.app.ceylonmarkethub.data.repository;

import com.google.gson.JsonObject;

import java.util.List;

import lk.cmh.app.ceylonmarkethub.data.model.projects.Project;
import lk.cmh.app.ceylonmarkethub.data.model.projects.ProjectDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ProjectRepository {

    @GET("projects")
    Call<List<Project>> getAllProjects();

    @GET("projects/{id}")
    Call<Project> getProject(@Path("id") long id);

    @GET("projects/seller")
    Call<List<Project>> getSellersProjects();

    @GET("projects/enabled")
    Call<List<Project>> getAllEnabledProjects();

    @POST("projects/create")
    Call<Project> createProject(@Body ProjectDto projectDto);

    @PUT("projects/update/{id}")
    Call<Project> updateProject(@Path("id") long id, @Body ProjectDto projectDto);

    @PUT("projects/{id}/enable")
    Call<JsonObject> enableProject(@Path("id") long id);

    @GET("projects/search")
    Call<List<Project>> searchProjects(@Query("keyword") String text);
}
