package com.instructure.template.projectCodeHere.javafiedExamples;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.gson.Gson;
import com.instructure.template.R;
import com.instructure.template.loginTemplate.api.ApiPrefs;
import com.instructure.template.loginTemplate.api.models.Course;
import com.instructure.template.projectCodeHere.GetCourses;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class ExampleDestinationFragmentJavified extends Fragment{
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_example_destination, container, false);
        TextView newText = (TextView)view.findViewById(R.id.TextViewExampleDestination);
        GetCourses getCourses = GetCourses.retrofit.create(GetCourses.class);

        Call<List<Course>> call = getCourses.coursesCall(ApiPrefs.getUser().getId(), ApiPrefs.getToken(), ApiPrefs.getUserAgent());
        call.enqueue((new Callback() {
            public void onFailure(@NotNull Call call, @NotNull Throwable t) {
                // This  is where you would put code for the error/failure case
            }

            public void onResponse(@NotNull Call call, @NotNull Response response) {
                // This is where you would put code for the success case!
                // The data is in the response body - response.body()

                // Temporarily setting textfield to the json text just to see if it worked
                newText.setText(new Gson().toJson(response.body()));
            }
        }));
        return view;
    }
}
