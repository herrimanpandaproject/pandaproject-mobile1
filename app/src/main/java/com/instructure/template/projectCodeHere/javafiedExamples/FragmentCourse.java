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
import com.instructure.template.projectCodeHere.GetCourses;

public class FragmentCourse extends Fragment {
    public static FragmentCourse newInstance() {
        return new FragmentCourse();
    }
    private final String TAG = "FragmentCourse";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_course, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ExampleActivityJavified parentActivity = (ExampleActivityJavified) this.getActivity();
        GetCourses.CoursesResponse selectedCourse = parentActivity.getSelectedCourse();
        Log.i(TAG, "We got course: " + parentActivity.getSelectedCourse());

        TextView tv = getView().findViewById(R.id.courseTextView);
        tv.setText(selectedCourse.getName());

        TextView courseDesc = getView().findViewById(R.id.courseDescriptionView);
        courseDesc.setText(selectedCourse.getPublic_description());
    }
}
