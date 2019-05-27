package com.instructure.template.projectCodeHere.api;

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


//Create a list containing enrollment objects
//Retrofit client = (new Retrofit.Builder()).baseUrl(ApiPrefs.getFullDomain()).addConverterFactory(GsonConverterFactory.create()).build();
//GetEnrollments getEnrollments = client.create(GetEnrollments.class);
//Call<List<GetEnrollments.EnrollmentResponse>> call = getEnrollments.enrollmentsCall(ApiPrefs.getUser().getId(), "Bearer " + ApiPrefs.getToken(), ApiPrefs.getUserAgent());
//call.enqueue((new Callback<List<GetEnrollments.EnrollmentResponse>>() {
//    public void onFailure(@NotNull Call call2, @NotNull Throwable t) {
//        // This  is where you would put code for the error/failure case
//    }
//
//    public void onResponse(@NotNull Call call, @NotNull Response response) {
//        // This is where you would put code for the success case!
//        // The data is in the response body - response.body()
//        List<GetEnrollments.EnrollmentResponse> g;
//        g = (ArrayList<GetEnrollments.EnrollmentResponse>)response.body();
//
//    }
//}));
public interface GetEnrollments {
    @GET("/api/v1/users/{user_id}/enrollments")
    Call<List<EnrollmentResponse>> enrollmentsCall(
            @Path("user_id") Long user_id,
            @Header("Authorization") String authorization,
            @Header("User-Agent") String userAgent
    );

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ApiPrefs.getFullDomain())
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    class EnrollmentResponse implements Parcelable {
        @SerializedName("id")
        private long id;
        @SerializedName("grades")
        private Grade grades;
        @SerializedName("course_id")
        private long course_id;

        public long getCourse_id() {
            return course_id;
        }

        public void setCourse_id(long course_id) {
            this.course_id = course_id;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public Grade getGrades() {
            return grades;
        }

        public void setGrades(Grade grades) {
            this.grades = grades;
        }

        protected EnrollmentResponse(Parcel in) {
            id = in.readLong();
            grades = new Grade(in);
            course_id = in.readLong();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(id);
            dest.writeValue(grades);
            dest.writeDouble(course_id);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public final Creator<EnrollmentResponse> CREATOR = new Creator<EnrollmentResponse>() {
            @Override
            public EnrollmentResponse createFromParcel(Parcel in) {
                return new EnrollmentResponse(in);
            }

            @Override
            public EnrollmentResponse[] newArray(int size) {
                return new EnrollmentResponse[size];
            }
        };

    }

    class Grade implements Parcelable {
        @SerializedName("current_score")
        private double current_score;

        public double getCurrent_score() {
            return current_score;
        }

        public void setCurrent_score(double current_score) {
            this.current_score = current_score;
        }

        protected Grade(Parcel in) {
            current_score = in.readDouble();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeDouble(current_score);
        }

        public final Creator<Grade> CREATOR = new Creator<Grade>() {
            @Override
            public Grade createFromParcel(Parcel in) {
                return new Grade(in);
            }

            @Override
            public Grade[] newArray(int size) {
                return new Grade[size];
            }
        };
    }
}

