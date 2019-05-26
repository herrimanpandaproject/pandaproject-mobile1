package com.instructure.template.projectCodeHere;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.instructure.template.R;
import com.instructure.template.loginTemplate.api.ApiPrefs;
import com.instructure.template.loginTemplate.api.apiHelpers.CanvasRestAdapter;
import com.instructure.template.loginTemplate.login.LoginActivity;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProfilePage extends Fragment {

    private TextView usernameprint;
    private Button logoutButton;
    private ImageView imageView;
    private TextView gpaPrint;
    private TextView emailPrint;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        logoutButton = view.findViewById(R.id.logoutButton);
        usernameprint = view.findViewById(R.id.username);
        imageView = view.findViewById(R.id.profileIcon);
        gpaPrint = view.findViewById(R.id.gpa);
        emailPrint = view.findViewById(R.id.userEmail);

        logoutButton.setOnClickListener(v -> {
            // Clear the local data (token, domain, etc)
            try {
                Objects.requireNonNull(CanvasRestAdapter.getOkHttpClient().cache()).evictAll();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ApiPrefs.clearAllData();
            // Take us back to login starting point
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
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
                usernameprint.setText(g.getName());
                new DownLoadImageTask((ImageView) view.findViewById(R.id.profileIcon)).execute(g.getAvatar_url());
                emailPrint.setText(g.getPrimary_email());
            }
        }));
        GetEnrollments getEnrollments = client.create(GetEnrollments.class);
        Call<List<GetEnrollments.EnrollmentResponse>> call2 = getEnrollments.enrollmentsCall(ApiPrefs.getUser().getId(), "Bearer " + ApiPrefs.getToken(), ApiPrefs.getUserAgent());
        call2.enqueue((new Callback<List<GetEnrollments.EnrollmentResponse>>() {
            public void onFailure(@NotNull Call call2, @NotNull Throwable t) {
                // This  is where you would put code for the error/failure case
            }

            public void onResponse(@NotNull Call call, @NotNull Response response) {
                // This is where you would put code for the success case!
                // The data is in the response body - response.body()
                List<GetEnrollments.EnrollmentResponse> g;
                g = (ArrayList<GetEnrollments.EnrollmentResponse>)response.body();
                int count=0;
                double total=0;
                for(GetEnrollments.EnrollmentResponse i : g) {
                    total+=i.getGrades().getCurrent_score();
                    count++;
                }
                double totalGPA=total/count,GPA=(totalGPA/20.0)-1;
                gpaPrint.setText(GPA+"");
            }
        }));
    }

    private class DownLoadImageTask extends AsyncTask<String,Void, Bitmap> {
        ImageView imageView;

        public DownLoadImageTask(ImageView imageView){
            this.imageView = imageView;
        }

        /*
            doInBackground(Params... params)
                Override this method to perform a computation on a background thread.
         */
        protected Bitmap doInBackground(String...urls){
            String urlOfImage = urls[0];
            Bitmap logo = null;
            try{
                InputStream is = new URL(urlOfImage).openStream();
                /*
                    decodeStream(InputStream is)
                        Decode an input stream into a bitmap.
                 */
                logo = BitmapFactory.decodeStream(is);
            }catch(Exception e){ // Catch the download exception
                e.printStackTrace();
            }
            return logo;
        }

        /*
            onPostExecute(Result result)
                Runs on the UI thread after doInBackground(Params...).
         */
        protected void onPostExecute(Bitmap result){
            imageView.setImageBitmap(result);
        }
    }
}
