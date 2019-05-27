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

public interface GetAssignments {
    @GET("/api/v1/users/{user_id}/courses/{course_id}/assignments")
    Call<List<AssignmentsResponse>> assignmentCall(
            @Path("user_id") Long user_id,
            @Path("course_id") Long course_id,
            @Header("Authorization") String authorization,
            @Header("User-Agent") String userAgent
    );

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ApiPrefs.getFullDomain())
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    class AssignmentsResponse implements Parcelable {
        @SerializedName("id")
        private long id;
        @SerializedName("name")
        private String name;
        @SerializedName("description")
        private String description;
        @SerializedName("course_id")
        private long course_id;
        @SerializedName("due_at")
        private String due_at;

        protected AssignmentsResponse(Parcel in) {
            id = in.readLong();
            name = in.readString();
            description = in.readString();
            course_id = in.readLong();
            due_at = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(id);
            dest.writeString(name);
            dest.writeLong(course_id);
            dest.writeString(description);
            dest.writeString(due_at);
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public long getCourse_id() {
            return course_id;
        }

        public void setCourse_id(long course_id) {
            this.course_id = course_id;
        }

        public String getDue_at() {
            return due_at;
        }

        public void setDue_at(String due_at) {
            this.due_at = due_at;
        }

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

        @Override
        public int describeContents() {
            return 0;
        }

        public final Creator<AssignmentsResponse> CREATOR = new Creator<AssignmentsResponse>() {
            @Override
            public AssignmentsResponse createFromParcel(Parcel in) {
                return new AssignmentsResponse(in);
            }

            @Override
            public AssignmentsResponse[] newArray(int size) {
                return new AssignmentsResponse[size];
            }
        };

    }
}