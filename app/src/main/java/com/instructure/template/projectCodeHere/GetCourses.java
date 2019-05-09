package com.instructure.template.projectCodeHere;

import com.instructure.template.loginTemplate.api.ApiPrefs;
import com.instructure.template.loginTemplate.api.models.Course;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

import java.util.List;

public interface GetCourses {
    @GET("/api/v1/users/{user_id}/courses")
    Call<List<Course>> coursesCall(
            @Path("user_id") Long user_id,
            @Header("Authorization") String authorization,
            @Header("User-Agent") String userAgent
    );

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ApiPrefs.getFullDomain())
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
    //This is how one would implement this interface (returns json data)
    /*GetCourses getCourses = GetCourses.retrofit.create(GetCourses.class);
    Call<List<Course>> call = getCourses.coursesCall(ApiPrefs.getUser().getId(), "Bearer " + ApiPrefs.getToken(), ApiPrefs.getUserAgent());
    call.enqueue((new Callback() {
        public void onFailure(@NotNull Call call, @NotNull Throwable t) {
            // This  is where you would put code for the error/failure case
        }

        public void onResponse(@NotNull Call call, @NotNull Response response) {
            // This is where you would put code for the success case!
            // The data is in the response body - response.body()
            (new Gson().toJson(response.body()))
        }
    }));*/