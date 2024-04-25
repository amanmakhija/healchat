package com.sandeep.chatassistant.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import android.content.Intent;
import android.provider.MediaStore;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.sandeep.chatassistant.R;
import com.sandeep.chatassistant.adapter.MessageRVAdapter;
import com.sandeep.chatassistant.apiservice.ApiResponse;
import com.sandeep.chatassistant.apiservice.ApiResponsePlant;
import com.sandeep.chatassistant.apiservice.ApiService;
import com.sandeep.chatassistant.apiservice.ApiServicePlant;
import com.sandeep.chatassistant.data.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity  {
    private RecyclerView chatsRV;
    private final String BASE_URL = "http://192.168.102.90:5000";
    private EditText userMsgEdt;
    private final String USER_KEY = "user";
    private final String BOT_KEY = "bot";
    private String fullUserMsg = "";
    private AlertDialog alertDialog; // Declare the AlertDialog variable globally
    // creating a variable for array list and adapter class.
    private ArrayList<Message> messageModalArrayList;
    private MessageRVAdapter messageRVAdapter;
    private static final int PICK_IMAGE_REQUEST = 1; // Define PICK_IMAGE_REQUEST as a class variable
    private static final int CAMERA_REQUEST_CODE = 123; // You can use any integer value
    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // on below line we are initializing all our views.
        chatsRV = findViewById(R.id.idRVChats);
        ImageView sendMsgIB = findViewById(R.id.idIBSend);
        userMsgEdt = findViewById(R.id.idEdtMessage);
        ImageView plusIcon = findViewById(R.id.plusIcon);
        // below line is to initialize our request queue.
        // creating a variable for
        // our volley request queue.
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        mRequestQueue.getCache().clear();

        // creating a new array list
        messageModalArrayList = new ArrayList<>();

        // Set a click listener for the plus icon
        plusIcon.setOnClickListener(v -> {
            // Create a custom layout for the dialog
            View dialogView = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_image_selection, null);

            // Find the image views in the custom layout
            ImageView imageViewCamera = dialogView.findViewById(R.id.imageViewCamera);
            ImageView imageViewGallery = dialogView.findViewById(R.id.imageViewGallery);

            // Set click listeners for the camera and gallery icons
            imageViewCamera.setOnClickListener(v1 -> {
                // Handle camera icon click (e.g., open camera activity)
                openCamera();
                // Dismiss the dialog
                alertDialog.dismiss();
            });

            imageViewGallery.setOnClickListener(v12 -> {
                // Handle gallery icon click (e.g., open gallery activity)
                openGallery();
                // Dismiss the dialog
                alertDialog.dismiss();
            });

            // Create an AlertDialog with the custom layout
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setView(dialogView);
            alertDialog = builder.create(); // Initialize the alertDialog variable

            // Show the dialog
            alertDialog.show();
        });

        // adding on click listener for send message button.
        sendMsgIB.setOnClickListener(v -> {
            // checking if the message entered
            // by user is empty or not.
            if (userMsgEdt.getText().toString().isEmpty()) {
                // if the edit text is empty display a toast message.
                Toast.makeText(MainActivity.this, "Please enter your message..", Toast.LENGTH_SHORT).show();
                return;
            }

            messageModalArrayList.add(new Message(userMsgEdt.getText().toString(), USER_KEY, ""));
            messageRVAdapter.notifyDataSetChanged();

            if(userMsgEdt.getText().toString().equalsIgnoreCase("no")) {
                // calling a method to send message
                // to our bot to get response.
                try {
                    Log.d("message-1", fullUserMsg);
                    sendMessage(fullUserMsg);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
                // below line we are setting text in our edit text as empty
            } else  {
                String newMessage = "Do you have any other symptoms?";
                messageModalArrayList.add(new Message(newMessage, BOT_KEY, ""));
                messageRVAdapter.notifyDataSetChanged();
                if (fullUserMsg.equals("")) {
                    fullUserMsg = userMsgEdt.getText().toString();
                } else {
                    fullUserMsg += " " + userMsgEdt.getText().toString();
                }
            }
            userMsgEdt.setText("");
        });

        // on below line we are initializing our adapter class and passing our array list to it.
        messageRVAdapter = new MessageRVAdapter(messageModalArrayList, this);

        // below line we are creating a variable for our linear layout manager.
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);

        // below line is to set layout
        // manager to our recycler view.
        chatsRV.setLayoutManager(linearLayoutManager);

        // below line we are setting
        // adapter to our recycler view.
        chatsRV.setAdapter(messageRVAdapter);
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the selected image URI
            Uri imageUri = data.getData();

            // Add the image URI as a string to the ArrayList
            messageModalArrayList.add(new Message("", USER_KEY, imageUri.toString()));
            messageRVAdapter.notifyDataSetChanged();

            // Prepare the image file from the URI to be uploaded
            File imageFile = new File(getRealPathFromURI(imageUri));

            // Create a request body with the image file
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);

            // Create MultipartBody.Part from the request body
            MultipartBody.Part imagePart = MultipartBody.Part.createFormData("file", imageFile.getName(), requestFile);

            // Call the API to upload the image
            ApiServicePlant api = retrofit.create(ApiServicePlant.class);
            Call<ApiResponsePlant> call = api.uploadImage(imagePart);

            // Make the API request asynchronously
            call.enqueue(new Callback<ApiResponsePlant>() {
                @Override
                public void onResponse(@NonNull Call<ApiResponsePlant> call, @NonNull Response<ApiResponsePlant> response) {
                    // Handle API response here
                    if (response.isSuccessful()) {
                        // API call was successful, handle the response data
                        ApiResponsePlant apiResponse = response.body();
                        // Handle the API response as needed
                        assert apiResponse != null;
                        messageModalArrayList.add(new Message(apiResponse.getStatus(), BOT_KEY, ""));
                        messageRVAdapter.notifyDataSetChanged();
                    } else {
                        // API call was not successful, handle the error
                        // Log the error body to understand the cause of the failure
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e("API Error", errorBody);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ApiResponsePlant> call, Throwable t) {
                    // Handle network failure error
                    Toast.makeText(MainActivity.this, "Network Failure", Toast.LENGTH_SHORT).show();
                }
            });
        } else if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Handle the captured image from the camera
            Bitmap photo = (Bitmap) data.getExtras().get("data");

            // Convert the Bitmap to a File and get the image URI
            Uri imageUri = getImageUri(photo);

            // Add the image URI as a string to the ArrayList
            messageModalArrayList.add(new Message("", USER_KEY, imageUri.toString()));
            messageRVAdapter.notifyDataSetChanged();

            // Prepare the image file from the URI to be uploaded
            File imageFile = new File(getRealPathFromURI(imageUri));

            // Create a request body with the image file
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);

            // Create MultipartBody.Part from the request body
            MultipartBody.Part imagePart = MultipartBody.Part.createFormData("file", imageFile.getName(), requestFile);

            // Call the API to upload the image
            ApiServicePlant api = retrofit.create(ApiServicePlant.class);
            Call<ApiResponsePlant> call = api.uploadImage(imagePart);

            // Make the API request asynchronously (as previously provided)
            call.enqueue(new Callback<ApiResponsePlant>() {
                @Override
                public void onResponse(Call<ApiResponsePlant> call, Response<ApiResponsePlant> response) {
                    // Handle API response here
                    if (response.isSuccessful()) {
                        // API call was successful, handle the response data
                        ApiResponsePlant apiResponse = response.body();
                        // Handle the API response as needed
                        messageModalArrayList.add(new Message(apiResponse.getStatus(), BOT_KEY, ""));
                        messageRVAdapter.notifyDataSetChanged();
                    } else {
                        // API call was not successful, handle the error
                        // Log the error body to understand the cause of the failure
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e("API Error", errorBody);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ApiResponsePlant> call, Throwable t) {
                    // Handle network failure error
                    Toast.makeText(MainActivity.this, "Network Failure", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private Uri getImageUri(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    // Helper method to get the real path of the image file from its URI
    private String getRealPathFromURI(Uri contentUri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, projection, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String filePath = cursor.getString(column_index);
            cursor.close();
            return filePath;
        }
    }

    private void sendMessage(String userMsg) throws UnsupportedEncodingException {
        ApiService apiService = retrofit.create(ApiService.class);

        // Create a JSON object containing the userMsg field
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("userMsg", userMsg);
        } catch (JSONException e) {
            e.printStackTrace();
            // Handle JSON exception if needed
        }

        // Create a RequestBody from the JSON string
        String contentType = "application/json"; // or the appropriate content type
        RequestBody requestBody = RequestBody.create(MediaType.parse(contentType), jsonRequest.toString());
        Call<ApiResponse> call = apiService.postText(contentType, requestBody);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    String disease = apiResponse.getDisease();
                    String prevention = apiResponse.getPrevention();

                    // Handle the disease and prevention strings here as needed
                    messageModalArrayList.add(new Message("You might have "+disease, BOT_KEY, ""));
                    messageModalArrayList.add(new Message(prevention, BOT_KEY, ""));
                    messageRVAdapter.notifyDataSetChanged();
                } else {
                    // Log the error body to understand the cause of the failure
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e("API Error", errorBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // API call was not successful
                    Toast.makeText(MainActivity.this, "API call unsuccessful", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Handle network failure error
                Toast.makeText(MainActivity.this, "Network Failure", Toast.LENGTH_SHORT).show();
            }
        });
    }
}