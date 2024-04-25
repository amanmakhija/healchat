package com.sandeep.chatassistant.apiservice;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiServicePlant {
    // Define the API endpoint for uploading an image using MultipartBody.Part
    @Multipart
    @POST("/predict")
    Call<ApiResponsePlant> uploadImage(@Part MultipartBody.Part image);
}
