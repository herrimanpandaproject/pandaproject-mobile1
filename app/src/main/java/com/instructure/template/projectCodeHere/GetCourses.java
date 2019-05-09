package com.instructure.template.projectCodeHere;

import com.instructure.template.loginTemplate.api.apiHelpers.RestBuilder;
import com.instructure.template.loginTemplate.api.apiHelpers.RestParams;
import com.instructure.template.loginTemplate.api.apiHelpers.StatusCallback;
import com.instructure.template.loginTemplate.api.models.Course;
import com.instructure.template.loginTemplate.api.models.User;
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
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}