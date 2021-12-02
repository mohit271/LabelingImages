package com.example.searchusingimages;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
 {

    ImageView image;
    Button takeImage, search,googleSearch;
    RecyclerView resultRv;
     SearchAdapter searchAdapter;
     String searchQuery;
     String apiKey;
     ArrayList<SearchModel> searchModelArrayList=new ArrayList<>();
    int REQUEST_CODE=1;
    ProgressBar pb;
    Bitmap imBitmap,rotatedBitmap;
    String title,link,dpLink,snip;
   // private FirebaseFunctions mFunctions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // getSupportActionBar().hide();
        image = findViewById(R.id.image);
        resultRv=findViewById(R.id.recyclerView);
        takeImage = findViewById(R.id.btnToPhoto);
        search = findViewById(R.id.btnToSearch);
        googleSearch=findViewById(R.id.btnToGoogleSearch);
        pb=findViewById(R.id.progressBar);
        searchModelArrayList= new ArrayList<SearchModel>();
        searchAdapter= new SearchAdapter(this, searchModelArrayList);
        resultRv.setAdapter(searchAdapter);


        try {
            ApplicationInfo applicationInfo =getApplicationContext().getPackageManager()
                    .getApplicationInfo(getApplicationContext().getPackageName(),PackageManager.GET_META_DATA);
            Bundle bundle=applicationInfo.metaData;
          apiKey=bundle.getString("keyValue");
            Log.i("APIKEY",apiKey);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        // pb.setVisibility(View.INVISIBLE);


        takeImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED)
                    ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.CAMERA}, REQUEST_CODE);
                else {
            takePictureIntent();
                searchModelArrayList.clear();
                    searchAdapter.notifyDataSetChanged();
                    }

            }
        });
            search.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    //    Toast.makeText(MainActivity.this, "click", Toast.LENGTH_SHORT).show();


                    if (rotatedBitmap != null) {
                        pb.setVisibility(View.VISIBLE);
                        searchModelArrayList.clear();
                        searchAdapter.notifyDataSetChanged();
                        //  getResultsCloud();
                        getResultNew();
                        //getResultNew();
                        searchAdapter = new SearchAdapter(MainActivity.this, searchModelArrayList);
                        resultRv.setAdapter(searchAdapter);
                        resultRv.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                      //  pb.setVisibility(View.INVISIBLE);

                  //  googleSearch.setVisibility(View.VISIBLE);

                }
                    else
                        Toast.makeText(MainActivity.this, "First Take Image", Toast.LENGTH_SHORT).show();
                }
            });

        googleSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = searchQuery;
                Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
                intent.putExtra("googleLink", str);
//
//        // start the Intent
                startActivity(intent);

            }
        });




    }

       private void getResultNew(){
          InputImage image = InputImage.fromBitmap(rotatedBitmap, 0);
          ImageLabeler labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);


//
            labeler.process(image)
                    .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                        @Override
                        public void onSuccess(List<ImageLabel> labels) {
                            // Task completed successfully
                             searchQuery = labels.get(0).getText();

                            getSearchResults(searchQuery);
                            Toast.makeText(MainActivity.this, searchQuery, Toast.LENGTH_SHORT).show();
                            // ...
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Image not detected", Toast.LENGTH_SHORT).show();
                        }
                    });
       }
    private void getResults() {
        // inside the label image method we are calling a firebase vision image
        // and passing our image bitmap to it.
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(rotatedBitmap);

        // on below line we are creating a labeler for our image bitmap and
        // creating a variable for our firebase vision image labeler.
        FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance().getOnDeviceImageLabeler();

        // calling a method to process an image and adding on success listener method to it.
        labeler.processImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
            @Override
            public void onSuccess(List<FirebaseVisionImageLabel> firebaseVisionImageLabels) {
                String searchQuery = firebaseVisionImageLabels.get(0).getText();
               getSearchResults(searchQuery);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // displaying error message.
                Toast.makeText(MainActivity.this, "Fail to detect image..", Toast.LENGTH_SHORT).show();
            }
        });
    }
//        private void getResultsCloud(){
//            mFunctions = FirebaseFunctions.getInstance();
//
//            // Convert bitmap to base64 encoded string
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
//            byte[] imageBytes = byteArrayOutputStream.toByteArray();
//            String base64encoded = Base64.encodeToString(imageBytes, Base64.NO_WRAP);
//
//            // Create json request to cloud vision
//            JsonObject request = new JsonObject();
//// Add image to request
//            JsonObject image = new JsonObject();
//            image.add("content", new JsonPrimitive(base64encoded));
//            request.add("image", image);
////Add features to the request
//            JsonObject feature = new JsonObject();
//            feature.add("maxResults", new JsonPrimitive(5));
//            feature.add("type", new JsonPrimitive("LABEL_DETECTION"));
//            JsonArray features = new JsonArray();
//            features.add(feature);
//            request.add("features", features);
//
//
//            annotateImage(request.toString())
//                    .addOnCompleteListener(new OnCompleteListener<JsonElement>() {
//                        @Override
//                        public void onComplete(@NonNull Task<JsonElement> task) {
//                            if (!task.isSuccessful()) {
//                                // Task failed with an exception
//                                // ...
//                                Toast.makeText(MainActivity.this, "Task failed", Toast.LENGTH_SHORT).show();
//                            } else {
//                                // Task completed successfully
//                                JsonElement label=task.getResult().getAsJsonArray().get(0).getAsJsonObject().get("labelAnnotations").getAsJsonArray();
//                                JsonObject labelObj = label.getAsJsonObject();
//                                String searchQuery = labelObj.get("description").getAsString();
//                                getSearchResults(searchQuery);
//
//
//                            }
//                        }
//                    });
      //  }

    private void getSearchResults(String searchQuery) {

        String url ="https://serpapi.com/search.json?engine=google&q="+searchQuery+"&location=Delhi%2C+India&google_domain=google.co.in&gl=in&hl=en&api_key="+apiKey;
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest  objectRequest= new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray organicArray= response.getJSONArray("organic_results");
                    for (int i=0;i<organicArray.length();i++){
                        JSONObject organicObj=organicArray.getJSONObject(i);
                        if(organicObj.has("title")){
                        title =organicObj.getString("title");
                        }
                        if(organicObj.has("link")){
                            link =organicObj.getString("link");
                        }
                        if(organicObj.has("displayed_link")){
                            dpLink =organicObj.getString("displayed_link");
                        }
                        if(organicObj.has("snippet")){
                            snip =organicObj.getString("snippet");
                        }
        searchModelArrayList.add(new SearchModel(title,link,dpLink,snip));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pb.setVisibility(View.INVISIBLE);
                searchAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Search results not found", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(objectRequest);
        };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE)
        {
            if (grantResults.length>0&&grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
               //opencamera;
            }
            else
            {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }


    private void takePictureIntent() {

        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(i.resolveActivity(getPackageManager())!=null)
            startActivityForResult(i,REQUEST_CODE);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE){
            Bundle extras =data.getExtras();
            imBitmap= (Bitmap) extras.get("data");
            Matrix matrix = new Matrix();

            matrix.postRotate(90);



             rotatedBitmap = Bitmap.createBitmap(imBitmap, 0, 0, imBitmap.getWidth(), imBitmap.getHeight(), matrix, true);
            image.setImageBitmap(rotatedBitmap);
        }
    }

//    @Override
//    public void onItemClicked(SearchModel item) {
//        // and convert it to string
//        String str = item.getLink();
//
//        // Create the Intent object of this class Context() to Second_activity class
//        Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
//
//        // now by putExtra method put the value in key, value pair
//        // key is message_key by this key we will receive the value, and put the string
//
//        intent.putExtra("urlLink", str);
//
//        // start the Intent
//        startActivity(intent);
//    }

//    private Task<JsonElement> annotateImage(String requestJson) {
//        return mFunctions
//                .getHttpsCallable("annotateImage")
//                .call(requestJson)
//                .continueWith(new Continuation<HttpsCallableResult, JsonElement>() {
//                    @Override
//                    public JsonElement then(@NonNull Task<HttpsCallableResult> task) {
//                        // This continuation runs on either success or failure, but if the task
//                        // has failed then getResult() will throw an Exception which will be
//                        // propagated down.
//                        return JsonParser.parseString(new Gson().toJson(task.getResult().getData()));
//                    }
//                });
//    }

}