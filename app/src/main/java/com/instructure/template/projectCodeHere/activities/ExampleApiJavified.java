package com.instructure.template.projectCodeHere.activities;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import com.instructure.template.loginTemplate.api.ApiPrefs;
import com.instructure.template.projectCodeHere.api.ExampleAPI;
import org.jetbrains.annotations.NotNull;
import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class ExampleApiJavified {

    interface ExampleApiInterface {

        // Here is where we define the actual url of the api request
        // You'll notice that its not a fully fledged url, that happens later, in the client
        // The end result looks something like this: https://pandaproject.instructure.com/api/example/user/user_1234
        @GET("/api/example/user/{user_id}")
        public Call<ExampleResponse> getMyLearnables(
                @Path("user_Id") String userId,
                @Query("example_query") String exampleQuery,
                @Header("Authorization") String authorization,
                @Header("User-Agent") String userAgent);

    }

    public void usingTheExampleAPI() {
        Retrofit client = (new Retrofit.Builder())
                .baseUrl(ApiPrefs.getFullDomain())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ExampleAPI.ExampleInterface exampleAPI = client.create(ExampleAPI.ExampleInterface.class);
        Call call = exampleAPI.getMyLearnables("userid", "example query", ApiPrefs.getToken(), ApiPrefs.getUserAgent());
        call.enqueue((new Callback() {
            public void onFailure(@NotNull Call call, @NotNull Throwable t) {
                // This  is where you would put code for the error/failure case
            }

            public void onResponse(@NotNull Call call, @NotNull Response response) {
                // This is where you would put code for the success case!
                // The data is in the response body - response.body()
            }
        }));
    }

    public final void usingTheExampleAPIRoundTwo(@NotNull Callback callback) {
        Retrofit client = (new Retrofit.Builder())
                .baseUrl(ApiPrefs.getFullDomain())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ExampleAPI.ExampleInterface exampleAPI = client.create(ExampleAPI.ExampleInterface.class);
        Call call = exampleAPI.getMyLearnables("userid", "example query", ApiPrefs.getToken(), ApiPrefs.getUserAgent());
        call.enqueue(callback);
    }

    //@Parcelize
    class ExampleResponse implements Parcelable {
        private String exampleField;
        @SerializedName("bad_json_name")
        private int badJSONName;

        public String getExampleField() {
            return exampleField;
        }

        public void setExampleField(String exampleField) {
            this.exampleField = exampleField;
        }

        public int getBadJSONName() {
            return badJSONName;
        }

        public void setBadJSONName(int badJSONName) {
            this.badJSONName = badJSONName;
        }


        protected ExampleResponse(Parcel in) {
            exampleField = in.readString();
            badJSONName = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(exampleField);
            dest.writeInt(badJSONName);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public final Creator<ExampleResponse> CREATOR = new Creator<ExampleResponse>() {
            @Override
            public ExampleResponse createFromParcel(Parcel in) {
                return new ExampleResponse(in);
            }

            @Override
            public ExampleResponse[] newArray(int size) {
                return new ExampleResponse[size];
            }
        };

    }
}


