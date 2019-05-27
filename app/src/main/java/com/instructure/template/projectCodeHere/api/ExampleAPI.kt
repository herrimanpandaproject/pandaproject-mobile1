/*
 * Copyright (C) 2019 - present Instructure, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package com.instructure.template.projectCodeHere.api

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.instructure.template.loginTemplate.api.ApiPrefs
import kotlinx.android.parcel.Parcelize
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

object ExampleAPI {

    interface ExampleInterface {

        // Here is where we define the actual url of the api request
        // You'll notice that its not a fully fledged url, that happens later, in the client
        // The end result looks something like this: https://pandaproject.instructure.com/api/example/user/user_1234
        @GET("/api/example/user/{user_id}")
        fun getMyLearnables(
            @Path("user_Id") userId: String,
            @Query("example_query") exampleQuery: String? = null,
            @Header("Authorization") authorization: String,
            @Header("User-Agent") userAgent: String
        ): Call<ExampleResponse>
    }

    fun usingTheExampleAPI() {
        // The retrofit client uses your interface defined above to make http requests!
        val client = Retrofit.Builder()
            .baseUrl(ApiPrefs.fullDomain) // This is creates the base of the url "https://pandaproject.instructure.com" which will always be the same
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val exampleAPI = client.create(ExampleAPI.ExampleInterface::class.java)

        // With the set up done, we can now make our api call!
        // Parameter 3 and 4 are very important! Those map to the two @Header parameters seen in the interface above.
        // The Authorization header authorizes the api calls with the api token
        // The User-Agent header let's the api know who we are :)
        val call: Call<ExampleResponse> =
            exampleAPI.getMyLearnables("userid", "example query", ApiPrefs.token, ApiPrefs.userAgent)

        // We want to make sure it runs asynchronously! So we use .enqueue() to get the response in a callback,
        // when its good and ready.
        call.enqueue(object : Callback<ExampleResponse> {
            override fun onFailure(call: Call<ExampleResponse>, t: Throwable) {
                // This  is where you would put code for the error/failure case
            }
            override fun onResponse(call: Call<ExampleResponse>, response: Response<ExampleResponse>) {
                // This is where you would put code for the success case!
                // The data is in the response body - response.body()
            }
        })
    }

    // The object used for the callback here can also be passed in as a function argument
    fun usingTheExampleAPIRoundTwo(callback: Callback<ExampleResponse>) {
        val client = Retrofit.Builder()
            .baseUrl(ApiPrefs.fullDomain)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val exampleAPI = client.create(ExampleAPI.ExampleInterface::class.java)
        val call: Call<ExampleResponse> =
            exampleAPI.getMyLearnables("userid", "example query", ApiPrefs.token, ApiPrefs.userAgent)

        call.enqueue(callback) // function argument in use
    }
}

/*
This data class is mapped directly from the json response we get from the http request.
This happens as a result of the GsonConverterFactory you can see getting added to the Retrofit client.
In order for the data class to be mapped to the json response correctly, you need to use the @Parcelize
annotation, like below. Also, the names of the properties of the data class must match the names of the properties
in the json. If you ever want the naming to be different, you can use @SerializedName("json_name") like I did below.
  */
@Parcelize
data class ExampleResponse(
    val exampleField: String,
    @SerializedName("bad_json_name")
    val badJsonName: Int
) : Parcelable