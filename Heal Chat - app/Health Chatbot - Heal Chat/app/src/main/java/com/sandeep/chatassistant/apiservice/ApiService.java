package com.sandeep.chatassistant.apiservice;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;


public interface ApiService {
    @POST("/predict")
    Call<ApiResponse> postText(@Header("Content-Type") String contentType, @Body RequestBody body);
}
