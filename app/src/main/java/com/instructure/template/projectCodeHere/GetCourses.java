package com.instructure.template.projectCodeHere;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import com.instructure.template.loginTemplate.api.ApiPrefs;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.*;


import java.util.List;

//Create a list containing enrollment objects
//Retrofit client = (new Retrofit.Builder()).baseUrl(ApiPrefs.getFullDomain()).addConverterFactory(GsonConverterFactory.create()).build();
//GetCourses getCourses = client.create(GetCourses.class);
//Call<List<GetCourses.CoursesResponse>> call = getCourses.coursesCall(ApiPrefs.getUser().getId(),"Bearer " + ApiPrefs.getToken(), ApiPrefs.getUserAgent());
//call.enqueue((new Callback<List<GetCourses.CoursesResponse>>() {
//    public void onFailure(@NotNull Call call, @NotNull Throwable t) {
//        // This  is where you would put code for the error/failure case
//    }
//
//    public void onResponse(@NotNull Call call, @NotNull Response response) {
//        // This is where you would put code for the success case!
//        // The data is in the response body - response.body()
//          List<GetCourses.CoursesResponse> g;
//          g = (ArrayList<GetCourses.CoursesResponse>)response.body();
//    }
//}));
public interface GetCourses {
    @GET("/api/v1/users/{user_id}/courses")
    Call<List<CoursesResponse>> coursesCall(
            @Path("user_id") Long user_id,
            @Header("Authorization") String authorization,
            @Header("User-Agent") String userAgent
    );

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ApiPrefs.getFullDomain())
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    class CoursesResponse implements Parcelable {
        @SerializedName("id")
        private long id;
        @SerializedName("name")
        private String name;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        protected CoursesResponse(Parcel in) {
            id = in.readLong();
            name = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(id);
            dest.writeString(name);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public final Creator<CoursesResponse> CREATOR = new Creator<CoursesResponse>() {
            @Override
            public CoursesResponse createFromParcel(Parcel in) {
                return new CoursesResponse(in);
            }

            @Override
            public CoursesResponse[] newArray(int size) {
                return new CoursesResponse[size];
            }
        };

    }
}