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
//Create a list containing profile objects
//Retrofit client = (new Retrofit.Builder()).baseUrl(ApiPrefs.getFullDomain()).addConverterFactory(GsonConverterFactory.create()).build();
//GetProfile getProfile = client.create(GetProfile.class);
//Call<List<GetProfile.ProfileResponse>> call = getProfile.profileCall(ApiPrefs.getUser().getId(),"Bearer " + ApiPrefs.getToken(), ApiPrefs.getUserAgent());
//call.enqueue((new Callback<List<GetProfile.ProfileResponse>>() {
//    public void onFailure(@NotNull Call call, @NotNull Throwable t) {
//        // This  is where you would put code for the error/failure case
//    }
//
//    public void onResponse(@NotNull Call call, @NotNull Response response) {
//        // This is where you would put code for the success case!
//        // The data is in the response body - response.body()
//          List<GetProfile.ProfileResponse> g;
//          g = (ArrayList<GetProfile.ProfileResponse>)response.body();
//    }
//}));
public interface GetProfile {
    @GET("/api/v1/users/{user_id}/profile")
    Call<ProfileResponse> profileCall(
            @Path("user_id") Long user_id,
            @Header("Authorization") String authorization,
            @Header("User-Agent") String userAgent
    );

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ApiPrefs.getFullDomain())
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    class ProfileResponse implements Parcelable {
        @SerializedName("id")
        private long id;
        @SerializedName("name")
        private String name;
        @SerializedName("avatar_url")
        private String avatar_url;
        @SerializedName("primary_email")
        private String primary_email;

        public String getAvatar_url() {
            return avatar_url;
        }

        public void setAvatar_url(String avatar_url) {
            this.avatar_url = avatar_url;
        }

        public long getId() {
            return id;
        }

        public String getPrimary_email() {
            return primary_email;
        }

        public void setPrimary_email(String primary_email) {
            this.primary_email = primary_email;
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

        protected ProfileResponse(Parcel in) {
            id = in.readLong();
            name = in.readString();
            avatar_url=in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(id);
            dest.writeString(name);
            dest.writeString(avatar_url);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public final Creator<ProfileResponse> CREATOR = new Creator<ProfileResponse>() {
            @Override
            public ProfileResponse createFromParcel(Parcel in) {
                return new ProfileResponse(in);
            }

            @Override
            public ProfileResponse[] newArray(int size) {
                return new ProfileResponse[size];
            }
        };

    }
}