package com.instructure.template.projectCodeHere.javafiedExamples;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.instructure.template.R;
import com.instructure.template.loginTemplate.api.ApiPrefs;
import com.instructure.template.projectCodeHere.GetAssignments;
import com.instructure.template.projectCodeHere.GetCourses;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.ArrayList;
import java.util.List;

public class FragmentCourse extends Fragment {
    public static FragmentCourse newInstance() {
        return new FragmentCourse();
    }
    private final String TAG = "FragmentCourse";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_course, container, false);
    }

    TextView textView;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ExampleActivityJavified parentActivity = (ExampleActivityJavified) this.getActivity();
        GetCourses.CoursesResponse selectedCourse = parentActivity.getSelectedCourse();
        Log.i(TAG, "We got course: " + parentActivity.getSelectedCourse());

        textView = view.findViewById(R.id.courseDisplay);
        Retrofit client = (new Retrofit.Builder()).baseUrl(ApiPrefs.getFullDomain()).addConverterFactory(GsonConverterFactory.create()).build();
        GetAssignments getCourses = client.create(GetAssignments.class);
        Call<List<GetAssignments.AssignmentsResponse>> call = getCourses.assignmentCall(ApiPrefs.getUser().getId(),selectedCourse.getId(),"Bearer " + ApiPrefs.getToken(), ApiPrefs.getUserAgent());
        call.enqueue((new Callback<List<GetAssignments.AssignmentsResponse>>() {
            public void onFailure(@NotNull Call call, @NotNull Throwable t) {
                // This  is where you would put code for the error/failure case
            }

            public void onResponse(@NotNull Call call, @NotNull Response response) {
                // This is where you would put code for the success case!
                // The data is in the response body - response.body()
                  List<GetAssignments.AssignmentsResponse> g;
                  g = (ArrayList<GetAssignments.AssignmentsResponse>)response.body();
                  String s = "";
                  if (!(g.get(0).getDue_at()==null)) {
                      for (GetAssignments.AssignmentsResponse i : g) {
                          Log.d("getId", i.getId() + "");
                          Log.d("getDescription", i.getDescription() + "");
                          Log.d("getDue_at", i.getDue_at() + "");
                          Log.d("getName", i.getName() + "");
                          Log.d("getCourse_id", i.getCourse_id() + "");
                          s += i.getName() + " ";
                          s += "Due on: " + i.getDue_at().substring(0, 10) + "\n\t\t\t\t";
                          s += i.getDescription().substring(2, i.getDescription().length() - 4) + "\n";
                      }
                      textView.setText(s);
                  } else {
                      textView.setText(R.string.failToFindAssignments);
                  }
            }
        }));

        Log.d("name", selectedCourse.getName());
        TextView tv = getView().findViewById(R.id.courseTextView);
        tv.setText(selectedCourse.getName());

        TextView courseDesc = getView().findViewById(R.id.courseDescriptionView);
        courseDesc.setText(selectedCourse.getPublic_description());
    }
}


