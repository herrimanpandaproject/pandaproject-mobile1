package com.instructure.template.projectCodeHere.api;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.instructure.template.R;
import com.instructure.template.loginTemplate.api.ApiPrefs;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.ArrayList;
import java.util.List;

public class UpdateGrades {

    private ArrayList<Double> grades = new ArrayList<>();
    private ArrayList<Double> previousgrades = new ArrayList();
    private ArrayList<Double> diffrences = new ArrayList();

    public void updateGrades() {
        if (previousgrades.isEmpty() == true && grades.isEmpty() == true)
        {
            SetGrades();
        }
        else
        {
            if (previousgrades.isEmpty() == true)
            {
                for (int i = 0; i < grades.size(); i++)
                {
                    previousgrades.add(grades.get(i));
                }
                grades.clear();
                SetGrades();
            }
            else
            {
                for (int i = 0; i < grades.size(); i++)
                {
                    diffrences.add((grades.get(i) - previousgrades.get(i)));
                }
                previousgrades.clear();
                for (int i = 0; i < grades.size(); i++)
                {
                    previousgrades.add(grades.get(i));
                }
                grades.clear();
                SetGrades();
            }
        }


    }
    private void SetGrades()
    {
        Retrofit client = (new Retrofit.Builder()).baseUrl(ApiPrefs.getFullDomain()).addConverterFactory(GsonConverterFactory.create()).build();
        GetEnrollments getEnrollments = client.create(GetEnrollments.class);
        Call<List<GetEnrollments.EnrollmentResponse>> call = getEnrollments.enrollmentsCall(ApiPrefs.getUser().getId(), "Bearer " + ApiPrefs.getToken(), ApiPrefs.getUserAgent());
        call.enqueue(new Callback<List<GetEnrollments.EnrollmentResponse>>() {
            public void onFailure(@NotNull Call call2, @NotNull Throwable t) {
                // This  is where you would put code for the error/failure case
            }

            public void onResponse(@NotNull Call call, @NotNull Response response) {
                // This is where you would put code for the success case!
                // The data is in the response body - response.body()
                List<GetEnrollments.EnrollmentResponse> g;
                g = (ArrayList<GetEnrollments.EnrollmentResponse>) response.body();
                for (GetEnrollments.EnrollmentResponse i : g) {
                    grades.add(i.getGrades().getCurrent_score());
                }
            }
        });
    }
}
