package com.example.eyeattend;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class TeacherLogin extends AppCompatActivity {

    private EditText govt_id;
    SharedPreferences sharedPreferences,sharedPreferencesStu;
    private TextView student;
    private Button login;
    private  String govt_id_str;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_login);

        getSupportActionBar().hide();


        sharedPreferences = getSharedPreferences("TEACHER_DATA", Context.MODE_PRIVATE);
        sharedPreferencesStu = getSharedPreferences("STUDENT_DATA",Context.MODE_PRIVATE);

        if(!sharedPreferences.getString("govt_id","").isEmpty()){
            govt_id_str = sharedPreferences.getString("govt_id","");
            Toast.makeText(TeacherLogin.this,"Already Logged In !",Toast.LENGTH_SHORT).show();
            fetchData();
        }else if(!sharedPreferencesStu.getString("name","").isEmpty()){
            finish();
            startActivity(new Intent(TeacherLogin.this,StudentLogin.class));
        }


        progressDialog = new ProgressDialog(TeacherLogin.this);
        progressDialog.setCancelable(false);

        govt_id =  (EditText)findViewById(R.id.govt_id);
        login = (Button)findViewById(R.id.login);
        student = (TextView)findViewById(R.id.student);

        student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(TeacherLogin.this,StudentLogin.class));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (constraintSatisfied()){
                    progressDialog.setMessage("Loggin you in...");
                    progressDialog.show();
                    govt_id.setEnabled(false);
                    login.setEnabled(false);
                    searchProfile();
                }
            }
        });
    }

    private boolean constraintSatisfied(){

        String data = govt_id.getText().toString().toUpperCase();
        if (data.isEmpty()){
            govt_id.requestFocus();
            govt_id.setError("Enter Something Please");
            return false;
        }else if(data.length() != 10){
            govt_id.hasFocus();
            govt_id.setError("Invalid ID");
            return false;
        }

        return true;
    }

    private void searchProfile(){


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.LOGIN_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                govt_id.setEnabled(true);
                login.setEnabled(true);
                progressDialog.dismiss();


                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.has("success")){

                        int otp = (int) jsonObject.get("success");
                        Intent intent = new Intent(TeacherLogin.this,otp.class);
                        intent.putExtra("previous_activity",true);
                        intent.putExtra("otp",otp);
                        intent.putExtra("govt_id",govt_id.getText().toString().trim());

                        startActivity(intent);

                    }else if(jsonObject.has("error")){
                        Toast.makeText(TeacherLogin.this,"Recieved Data : " + jsonObject.getString("error"),Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                govt_id.setEnabled(true);
                login.setEnabled(true);
                Toast.makeText(TeacherLogin.this,"Error : "+error,Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("govt_id",govt_id.getText().toString().trim().toUpperCase());
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

    private void fetchData(){
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
                    startActivity(new Intent(getApplicationContext(),homePage.class));

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"Exception: "+e.getMessage(),Toast.LENGTH_SHORT).show();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error Recieved : "+error,Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("govt_id",govt_id_str);
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