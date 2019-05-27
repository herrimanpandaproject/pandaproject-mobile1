package com.instructure.template.projectCodeHere.layouts;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.instructure.template.R;
import com.instructure.template.loginTemplate.api.ApiPrefs;
import com.instructure.template.loginTemplate.api.apiHelpers.CanvasRestAdapter;
import com.instructure.template.loginTemplate.login.LoginActivity;

import java.io.IOException;
import java.util.Objects;

public class ExampleFragmentJavified extends Fragment {

    private Button logoutButton;
    private Button navigationButton;
    private TextView textView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_example, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        logoutButton = view.findViewById(R.id.logoutButton);
        navigationButton = view.findViewById(R.id.navigationButton);
        textView = view.findViewById(R.id.textView);

        textView.setText("Hello world, this is the javified example!");

        logoutButton.setOnClickListener(v -> {
            // Clear the local data (token, domain, etc)
            try {
                Objects.requireNonNull(CanvasRestAdapter.getOkHttpClient().cache()).evictAll();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ApiPrefs.clearAllData();
            // Take us back to login starting point
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        navigationButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_exampleFragment_to_exampleDestinationFragment);
        });
    }
}
