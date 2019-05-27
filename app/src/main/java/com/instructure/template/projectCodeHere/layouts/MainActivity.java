package com.instructure.template.projectCodeHere.layouts;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.Navigation;
import com.instructure.template.R;
import com.instructure.template.loginTemplate.api.ApiPrefs;
import com.instructure.template.loginTemplate.api.apiHelpers.CanvasRestAdapter;
import com.instructure.template.loginTemplate.login.LoginActivity;
import com.instructure.template.projectCodeHere.api.GetCourses;
import com.instructure.template.projectCodeHere.api.GetEnrollments;
import com.instructure.template.projectCodeHere.api.GetProfile;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.squareup.picasso.Picasso;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Drawer drawer;
    private DrawerBuilder db;
    private List<PrimaryDrawerItem> coursesDrawerList;
    private List<GetCourses.CoursesResponse> courses;
    private final String TAG = "MainActivity";
    private long selectedCourse;
    private String name="",iconUrl="";
    ProfileDrawerItem profile;
    private Bundle global;
    private HashMap<String, Long> hmap = new HashMap<String, Long>();
    private List<GetEnrollments.EnrollmentResponse> enrollments;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getEnrollemntsFromApi();
        global=savedInstanceState;
        super.onCreate(savedInstanceState);
    }

    protected void continueCreate(@Nullable Bundle savedInstanceState) {
        Log.d("stuff",name+" "+iconUrl);
        //User test = UserManager.getSelf(Call<User>);

        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Picasso.get().load(uri).placeholder(placeholder).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Picasso.get().cancelRequest(imageView);
            }
        });
        profile = new ProfileDrawerItem().withName(name)
               .withIcon(iconUrl);

        db = new DrawerBuilder()
                .addDrawerItems(profile)
                .addDrawerItems(new DividerDrawerItem());

        getCoursesFromAPI();
        /* add drawer items from that course list into the DrawerBuilder */
        for (PrimaryDrawerItem pdi : coursesDrawerList) {
            db.addDrawerItems(pdi);
        }


        /* Create the navigation drawer */
        setContentView(R.layout.activity_example);
        db.withOnDrawerItemClickListener((Drawer.OnDrawerItemClickListener) this::onItemClick);
        db.withActivity(this);

        drawer = db.build();

        /* Create the menu containing the logout button (for now) */
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    /**
     * Gets the coursesDrawerList from the API, and builds a list of PrimaryDrawerItems into coursesDrawerList.
     * This is used later to place the drawer items into the hamburger menu.
     */
    private void getCoursesFromAPI() {
        List<PrimaryDrawerItem> courseItems = new ArrayList<PrimaryDrawerItem>();
        GetCourses getCourses = GetCourses.retrofit.create(GetCourses.class);
        Call<List<GetCourses.CoursesResponse>> call = getCourses.coursesCall(ApiPrefs.getUser().getId(), "Bearer " + ApiPrefs.getToken(), ApiPrefs.getUserAgent());
        call.enqueue(new Callback<List<GetCourses.CoursesResponse>>() {
                 public void onFailure(@NotNull Call call, @NotNull Throwable t) {
                     Log.e(TAG, "Failed to get coursesDrawerList! " + t.getMessage());
                 }

                 public void onResponse(@NotNull Call call, @NotNull Response response) {
                     courses = (List<GetCourses.CoursesResponse>) response.body();
                     Log.i(TAG, "Response body: " + response.body().toString());
                     Log.i(TAG, courses.toString());
                     for (GetCourses.CoursesResponse c : courses) {
                         double tmp = 0;
                         for (GetEnrollments.EnrollmentResponse i : enrollments) {
                             if (i.getCourse_id()==c.getId()) {
                                 tmp = i.getGrades().getCurrent_score();
                             }
                         }
                         db.addDrawerItems(new PrimaryDrawerItem().withName(c.getName() + "\t\t" + tmp + "%"));
                         Log.i(TAG, "Drawer item: " + c.getName());
                         hmap.put(c.getName(),c.getId());
                     }
                 }
             }
        );
        coursesDrawerList = courseItems;
    }

    private void getInfo() {
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
                name=g.getName();
                Log.d("stuff2",g.getName());
                Log.d("stuff2",name);
                iconUrl=g.getAvatar_url();

                Log.d("stuff2",iconUrl);
                continueCreate(global);
            }
        }));
    }

    private void getEnrollemntsFromApi() {
        Retrofit client = (new Retrofit.Builder()).baseUrl(ApiPrefs.getFullDomain()).addConverterFactory(GsonConverterFactory.create()).build();
        GetEnrollments getEnrollments = client.create(GetEnrollments.class);
        Call<List<GetEnrollments.EnrollmentResponse>> call = getEnrollments.enrollmentsCall(ApiPrefs.getUser().getId(), "Bearer " + ApiPrefs.getToken(), ApiPrefs.getUserAgent());
        call.enqueue((new Callback<List<GetEnrollments.EnrollmentResponse>>() {
            public void onFailure(@NotNull Call call2, @NotNull Throwable t) {
                // This  is where you would put code for the error/failure case
            }

            public void onResponse(@NotNull Call call, @NotNull Response response) {
                // This is where you would put code for the success case!
                // The data is in the response body - response.body()
                enrollments = (ArrayList<GetEnrollments.EnrollmentResponse>)response.body();
                getInfo();
            }
        }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logoutMenuButton:
                return logout();
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private boolean logout() {
        try {
            Objects.requireNonNull(CanvasRestAdapter.getOkHttpClient().cache()).evictAll();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ApiPrefs.clearAllData();
        // Take us back to login starting point
        Intent intent = new Intent(getBaseContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        return true;
    }

    private boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
        if (drawerItem instanceof Nameable) {
            Navigation.findNavController(findViewById(R.id.navHostFragment)).navigate(R.id.exampleFragmentJavified);
            String tmp = String.valueOf(((Nameable) drawerItem).getName());
            if ((tmp.substring(tmp.indexOf("%")-2)).substring(0,1).equals(".")) {
                selectedCourse = hmap.get(tmp.substring(0,tmp.indexOf("%")-6));
            } else {
                selectedCourse = hmap.get(tmp.substring(0,tmp.indexOf("%")-7));
            }

            Navigation.findNavController(findViewById(R.id.navHostFragment)).navigate(R.id.action_exampleFragmentJavified_to_fragmentCourse3);
            drawer.closeDrawer();
        }
        else if (drawerItem instanceof ProfileDrawerItem) {
            Navigation.findNavController(findViewById(R.id.navHostFragment)).navigate(R.id.exampleFragmentJavified);
            Navigation.findNavController(findViewById(R.id.navHostFragment)).navigate(R.id.action_exampleFragment_to_ProfilePage);
            drawer.closeDrawer();
        }
        return true;
    }

    public GetCourses.CoursesResponse getSelectedCourse() {
        for (GetCourses.CoursesResponse c : courses) {
            if (c.getId() == selectedCourse) {
                return c;
            }
        }
        return null; // TODO: Don't do this. But there shouldn't be a reason this should ever return null, I hope!
    }
}

