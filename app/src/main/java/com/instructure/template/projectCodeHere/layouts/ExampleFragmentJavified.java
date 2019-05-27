package com.instructure.template.projectCodeHere.layouts;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.instructure.template.R;
import com.instructure.template.loginTemplate.api.ApiPrefs;
import com.instructure.template.projectCodeHere.api.GetProfile;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ExampleFragmentJavified extends Fragment {

    private TextView loggedIn;
    private ImageView canvasIcon;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_example, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loggedIn = view.findViewById(R.id.loggedIn);
        canvasIcon = view.findViewById(R.id.canvasIcon);

        canvasIcon.setImageResource(R.drawable.vd_canvas_logo_red);
        Retrofit client = (new Retrofit.Builder()).baseUrl(ApiPrefs.getFullDomain()).addConverterFactory(GsonConverterFactory.create()).build();
        GetProfile getProfile = client.create(GetProfile.class);
        Call<GetProfile.ProfileResponse> call = getProfile.profileCall(ApiPrefs.getUser().getId(),"Bearer " + ApiPrefs.getToken(), ApiPrefs.getUserAgent());
        call.enqueue((new Callback<GetProfile.ProfileResponse>() {
            public void onFailure(@NotNull Call call, @NotNull Throwable t) {
                // This  is where you would put code for the error/failure case
                Log.d("ERROR","call has failed");
                Log.d("call",call.toString());
                Log.d("throwable",t.toString());
            }

            public void onResponse(@NotNull Call call, @NotNull Response response) {
                // This is where you would put code for the success case!
                // The data is in the response body - response.body()
                GetProfile.ProfileResponse g;
                g = (GetProfile.ProfileResponse)response.body();
                loggedIn.setText("You are logged in as "+g.getName()+".");
            }
        }));
    }
}
