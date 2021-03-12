package com.example.eyeattend;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class otp extends AppCompatActivity {

    private AutoCompleteTextView otp_text;
    private Button verify, resend;
    private int otp,otp_entered;
    private String govt_id;
    private String table_name,roll_number;
    private boolean prev_teacher = true;
    SharedPreferences sharedPreferences,sharedPreferencesStudent;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);


        sharedPreferences = getSharedPreferences("TEACHER_DATA", Context.MODE_PRIVATE);
        sharedPreferencesStudent = getSharedPreferences("STUDENT_DATA",Context.MODE_PRIVATE);

        Intent i = getIntent();

        prev_teacher = i.getBooleanExtra("previous_activity",true);
        otp = i.getIntExtra("otp",111111);

        if(prev_teacher)
            govt_id = i.getStringExtra("govt_id");
        else{
            table_name = i.getStringExtra("table_name");
            roll_number = i.getStringExtra("roll_number");
        }

        progressDialog = new ProgressDialog(otp.this);
        progressDialog.setCancelable(false);

        otp_text = (AutoCompleteTextView)findViewById(R.id.otp_text);
        verify = (Button)findViewById(R.id.verify);
        resend = (Button)findViewById(R.id.resend);

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = otp_text.getText().toString().trim();
                if(text.isEmpty()){
                    otp_text.requestFocus();
                    otp_text.setError("Enter OTP First :|");
                }else{
                    otp_entered = Integer.parseInt(text);
                    if(otp == otp_entered){
                        progressDialog.setMessage("Fetching User Data...");
                        progressDialog.show();
                        if(prev_teacher)
                            fetchTeacherData();
                        else
                            fetchStudentData();
                    }else{
                        Toast.makeText(otp.this,"Otp doesn't Match !",Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Re-Sending OTP");
                progressDialog.show();
                if (prev_teacher)
                    resendTeacherRequest();
                else
                    resendStudentRequest();

            }
        });

    }

    private void resendTeacherRequest(){


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.LOGIN_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.has("success")){
                        Toast.makeText(getApplicationContext(),"Check Mail for new otp...",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();

                        otp = jsonObject.getInt("success");
                    }else if (jsonObject.has("error")){
                        Toast.makeText(otp.this,"Error : "+jsonObject.get("error"),Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(otp.this,"Exception Caught : "+e.getMessage(),Toast.LENGTH_SHORT).show();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(otp.this,"Exception Caught : "+error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("govt_id",govt_id);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 2;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });

        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);


    }

    private void resendStudentRequest(){


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.LOGIN_URL_STU, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.has("success")){
                        Toast.makeText(getApplicationContext(),"Check Mail for new otp...",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();

                        otp = jsonObject.getInt("success");
                    }else if (jsonObject.has("error")){
                        Toast.makeText(otp.this,"Error : "+jsonObject.get("error"),Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(otp.this,"Exception Caught : "+e.getMessage(),Toast.LENGTH_SHORT).show();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(otp.this,"Exception Caught : "+error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("table_name",table_name);
                params.put("roll_number",roll_number);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 2;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });

        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);


    }



    private void fetchStudentData(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.FETCH_URL_STU, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);

                    JSONObject userData = new JSONObject(jsonObject.get("success").toString());
                    SharedPreferences.Editor editor = sharedPreferencesStudent.edit();
                    editor.putString("roll_number",userData.getString("roll_no"));
                    editor.putString("name",userData.getString("name"));
                    editor.putString("branch",userData.getString("branch"));
                    editor.putString("batch",userData.getString("batch"));
                    editor.putString("email",userData.getString("email"));
                    editor.putString("ccet_email",userData.getString("ccet_email"));

                    editor.commit();
                    finish();
                    progressDialog.dismiss();
                    Toast.makeText(otp.this, "Logging In...", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(otp.this,StudentHomepage.class));

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(otp.this,"Exception: "+e.getMessage(),Toast.LENGTH_SHORT).show();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(otp.this,"Error Recieved : "+error,Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("table_name",table_name);
                params.put("roll_number",roll_number);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 2;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });

        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void fetchTeacherData(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.FETCH_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);

                    JSONObject userData = new JSONObject(jsonObject.get("success").toString());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("govt_id",userData.getString("prof_id"));
                    editor.putString("prof_name",userData.getString("prof_name"));
                    editor.putString("prof_email",userData.getString("prof_email"));
                    editor.putString("dept",userData.getString("branch"));
                    editor.putString("contact",userData.getString("prof_contact"));
                    editor.putString("designation",userData.getString("prof_designation"));
                    editor.putString("specialization",userData.getString("specialization"));
                    editor.commit();
                    finish();
                    progressDialog.dismiss();
                    Toast.makeText(otp.this, "Welcome : "+userData.getString("prof_name"), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(otp.this,homePage.class));

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(otp.this,"Exception: "+e.getMessage(),Toast.LENGTH_SHORT).show();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(otp.this,"Error Recieved : "+error,Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("govt_id",govt_id);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 2;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });

        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }
}