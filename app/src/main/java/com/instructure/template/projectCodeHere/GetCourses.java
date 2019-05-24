package com.instructure.template.projectCodeHere;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import com.instructure.template.loginTemplate.api.ApiPrefs;
import com.instructure.template.loginTemplate.api.models.Enrollment;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

import java.util.List;

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
        @SerializedName("enrollments")
        private List<Enrollment> enrollments;
        @SerializedName("name")
        private String name;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public List<Enrollment> getEnrollments() {
            return enrollments;
        }

        public void setEnrollments(List<Enrollment> enrollments) {
            this.enrollments = enrollments;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        protected CoursesResponse(Parcel in) {
            id = in.readLong();
            in.readList(enrollments, Enrollment.class.getClassLoader());
            name = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(id);
            dest.writeList(enrollments);
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