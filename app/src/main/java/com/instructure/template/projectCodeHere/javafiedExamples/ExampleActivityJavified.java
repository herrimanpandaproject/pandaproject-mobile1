package com.instructure.template.projectCodeHere.javafiedExamples;

import android.content.Intent;
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
import com.instructure.template.loginTemplate.api.models.Course;
import com.instructure.template.loginTemplate.login.LoginActivity;
import com.instructure.template.projectCodeHere.GetCourses;
import com.instructure.template.projectCodeHere.GetProfile;
import com.instructure.template.projectCodeHere.ProfilePage;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExampleActivityJavified extends AppCompatActivity {
    private Toolbar toolbar;
    private Drawer drawer;
    private DrawerBuilder db;
    private List<PrimaryDrawerItem> coursesDrawerList;
    private List<GetCourses.CoursesResponse> courses;
    private final String TAG = "ExampleActivityJavified";
    private String selectedCourse; // TODO: Migrate this to use course IDs instead; I don't think course names are unique! -Hayden
    private String name="",iconUrl="";
    ProfileDrawerItem profile;
    private Bundle global;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getInfo();
        global=savedInstanceState;
        super.onCreate(savedInstanceState);
    }

    protected void continueCreate(@Nullable Bundle savedInstanceState) {
        Log.d("stuff",name+" "+iconUrl);
        //User test = UserManager.getSelf(Call<User>);


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
                                 db.addDrawerItems(new PrimaryDrawerItem().withName(c.getName()));
                                 Log.i(TAG, "Drawer item: " + c.getName());
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
            selectedCourse = String.valueOf(((Nameable) drawerItem).getName());
            Navigation.findNavController(findViewById(R.id.navHostFragment)).navigate(R.id.action_exampleFragmentJavified_to_fragmentCourse3);
            drawer.closeDrawer();
        }
        return true;
    }

    public GetCourses.CoursesResponse getSelectedCourse() {
        for (GetCourses.CoursesResponse c : courses) {
            if (c.getName().compareToIgnoreCase(selectedCourse) == 0) {
                return c;
            }
        }
        return null; // TODO: Don't do this. But there shouldn't be a reason this should ever return null, I hope!
    }
}

