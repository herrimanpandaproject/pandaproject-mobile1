package com.instructure.template.projectCodeHere;

import com.instructure.template.loginTemplate.api.ApiPrefs;
import com.instructure.template.loginTemplate.api.models.Enrollment;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

import java.util.List;

public interface GetEnrollments {
    @GET("/api/v1/users/{course_id}/enrollments")
    Call<List<Enrollment>> callEnrollment(
            @Path("course_id") Long course_id,
            @Header("Authorization") String authorization,
            @Header("User-Agent") String userAgent
    );

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ApiPrefs.getFullDomain())
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
