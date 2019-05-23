package com.instructure.template.projectCodeHere.javafiedExamples;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.gson.Gson;
import com.instructure.template.R;
import com.instructure.template.loginTemplate.api.ApiPrefs;
import com.instructure.template.loginTemplate.api.models.Course;
import com.instructure.template.projectCodeHere.ExampleAPI;
import com.instructure.template.projectCodeHere.GetCourses;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.List;

public class ExampleDestinationFragmentJavified extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Retrofit client = (new Retrofit.Builder())
                .baseUrl(ApiPrefs.getFullDomain())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GetCourses getCourses = client.create(GetCourses.class);
        Call<GetCourses.CoursesResponse> call = getCourses.coursesCall(ApiPrefs.getUser().getId(), "Bearer " + ApiPrefs.getToken(), ApiPrefs.getUserAgent());
        call.enqueue((new Callback<GetCourses.CoursesResponse>() {
            public void onFailure(@NotNull Call call, @NotNull Throwable t) {
                // This  is where you would put code for the error/failure case
                Log.d("ERROR", "Fail");
                Log.d("Throwable", t.toString());
                Log.d("Call", call.toString());
            }

            public void onResponse(@NotNull Call call, @NotNull Response response) {
                // This is where you would put code for the success case!
                // The data is in the response body - response.body()
                GetCourses.CoursesResponse g = (GetCourses.CoursesResponse)response.body();
                Log.d("Id", g.getId() + "");
                Log.d("Name", g.getEnrollments().toString());
            }
        }));
        return inflater.inflate(R.layout.fragment_example_destination, container, false);
    }
}
