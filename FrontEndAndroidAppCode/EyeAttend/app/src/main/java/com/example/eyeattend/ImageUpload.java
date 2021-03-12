package com.example.eyeattend;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ImageUpload extends AppCompatActivity {
    //ImageView to display image selected
    ImageView imageView;

    //edittext for getting the tags input
    TextView editTextTags ,attendence , absentees;
    String tags,filename,branch,batch;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);


        getSupportActionBar().hide();

        Intent i = getIntent();

        tags = i.getStringExtra("tags");
        filename = i.getStringExtra("filename");
        branch = i.getStringExtra("branch");
        batch = i.getStringExtra("batch");

        imageView = (ImageView) findViewById(R.id.imageView);
        editTextTags = (TextView) findViewById(R.id.editTextTags);
        attendence = (TextView) findViewById(R.id.attendence);
        absentees = (TextView) findViewById(R.id.absent);

        editTextTags.setText(filename);
        editTextTags.setFocusable(false);
        editTextTags.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        progressDialog = new ProgressDialog(ImageUpload.this);
        progressDialog.setCancelable(false);

        //checking the permission
        //if the permission is not given we will open setting to add permission
        //else app will not open
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(),"Storage permissions are required to load Image",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + getPackageName()));
            finish();
            startActivity(intent);
            return;
        }


        //adding click listener to button
        findViewById(R.id.buttonUploadImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //if the tags edittext is empty
                //we will throw input error
                //if everything is ok we will open image chooser
                Toast.makeText(getApplicationContext(),"Choose image",Toast.LENGTH_SHORT).show();
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 100);
            }
        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {

            //getting the image Uri
            Uri imageUri = data.getData();
            try {
                //getting bitmap object from uri

                Glide.with(getApplicationContext()).asBitmap().load(imageUri).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        imageView.setImageBitmap(resource);
                        progressDialog.setMessage("Uploading Image to Model");
                        progressDialog.show();
                        uploadBitmap(resource);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            //displaying selected image to imageview


                //calling the method uploadBitmap to upload image

        }


    }

    /*
     * The method is taking Bitmap as an argument
     * then it will return the byte[] array for the given bitmap
     * and we will send this array to the server
     * here we are using PNG Compression with 80% quality
     * you can give quality between 0 to 100
     * 0 means worse quality
     * 100 means best quality
     * */
    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void uploadBitmap(final Bitmap bitmap) {

        //getting the tag from the edittext
        final String tags = editTextTags.getText().toString().trim();

        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, Constants.UPLOAD,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));

                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                            try {
                                JSONObject att = new JSONObject(obj.getString("script_out"));
                                if (att.has("present") && att.has("absent")) {
                                    JSONObject pre = new JSONObject(att.getString("present"));
                                    JSONObject ab = new JSONObject(att.getString("absent"));
                                    String roll_numbers = "";
                                    String absent = "";


                                    for (Iterator<String> it = pre.keys(); it.hasNext(); ) {
                                        roll_numbers = roll_numbers + pre.getString(it.next()) + " ";
                                    }
                                    attendence.setText(roll_numbers);

                                    for (Iterator<String> it = ab.keys(); it.hasNext(); ) {
                                        absent = absent + ab.getString(it.next()) + " ";
                                    }
                                    absentees.setText(absent);


                                }
                            }catch (JSONException e2){
                                Toast.makeText(getApplicationContext(),obj.getString("script_out"),Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Exception Occured : "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            Toast.makeText(getApplicationContext(), "Response : "+response, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), ""+error, Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            /*
             * If you want to add more parameters with the image
             * you can do it here
             * here we have only one parameter with the image
             * which is tags
             * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("tags", tags);
                params.put("branch",branch);
                params.put("batch",batch);
                return params;
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                params.put("pic", new DataPart(filename + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };

        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                500000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

}