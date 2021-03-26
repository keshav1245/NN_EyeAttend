package com.example.eyeattend;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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

import java.util.HashMap;
import java.util.Map;

public class StudentLogin extends AppCompatActivity {

    private AutoCompleteTextView roll_number;
    private TextView teacher;
    private Button login;
    private ProgressDialog progressDialog;
    SharedPreferences sharedPreferencesStu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);

        getSupportActionBar().hide();

        sharedPreferencesStu = getSharedPreferences("STUDENT_DATA", Context.MODE_PRIVATE);

        if(!sharedPreferencesStu.getString("name","").isEmpty()){
            fetchStudentData("batch_"+sharedPreferencesStu.getString("batch",""),sharedPreferencesStu.getString("roll_number",""));
            finish();
            startActivity(new Intent(StudentLogin.this,StudentHomepage.class));
        }



        roll_number = (AutoCompleteTextView) findViewById(R.id.roll_number);
        teacher = (TextView) findViewById(R.id.teacher);
        progressDialog = new ProgressDialog(StudentLogin.this);
        progressDialog.setCancelable(false);

        teacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(StudentLogin.this,TeacherLogin.class));
            }
        });

        login = (Button) findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.setMessage("Logging In...");


                if(validate()){
                    progressDialog.show();
                    String roll = roll_number.getText().toString().trim().toUpperCase();
                    String batch = "batch_20";
                    if(roll.length() % 2 == 0){
                        batch = batch +roll.charAt(3)+roll.charAt(4);
                    }else{
                        batch = batch +roll.charAt(2)+roll.charAt(3);
                    }

                    searchProfile(batch,roll);

                }

            }
        });

    }

    private boolean validate(){

        String roll = roll_number.getText().toString().trim().toUpperCase();
        if(roll.contentEquals("")){
            roll_number.hasFocus();
            roll_number.setError("Enter Roll Number");
            return false;
        }else if(!(roll.length() >= 7 && roll.length() <= 8)){
            roll_number.hasFocus();
            roll_number.setError("Invalid Roll Number");
            return false;
        }else if(!(roll.startsWith("CO") || roll.startsWith("LCO"))){
            roll_number.hasFocus();
            roll_number.setError("Invalid Roll Number");
            return false;
        }
        return true;
    }

    private void searchProfile(final String batch, final String roll){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.LOGIN_URL_STU, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.has("success")){
                        progressDialog.dismiss();
                        Toast.makeText(StudentLogin.this,"Check your mail for OTP!",Toast.LENGTH_SHORT).show();

                        int otp =  Integer.parseInt(jsonObject.getString("success"));

                        Intent intent = new Intent(StudentLogin.this,otp.class);
                        intent.putExtra("otp",otp);
                        intent.putExtra("roll_number",roll);
                        intent.putExtra("table_name",batch);
                        intent.putExtra("previous_activity",false);
                        startActivity(intent);

                    }else if (jsonObject.has("error")){
                        Toast.makeText(StudentLogin.this,"onResponseError : "+jsonObject.getString("error"),Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    Toast.makeText(StudentLogin.this,"JSONException : "+e.getMessage(),Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(StudentLogin.this,"onError : "+error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> params = new HashMap<>();

                params.put("table_name",batch);
                params.put("roll_number",roll);
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

    private void fetchStudentData(final String table_name, final String roll){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.FETCH_URL_STU, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);

                    JSONObject userData = new JSONObject(jsonObject.get("success").toString());
                    SharedPreferences.Editor editor = sharedPreferencesStu.edit();
                    editor.putString("roll_number",userData.getString("roll_no"));
                    editor.putString("name",userData.getString("name"));
                    editor.putString("branch",userData.getString("branch"));
                    editor.putString("batch",userData.getString("batch"));
                    editor.putString("email",userData.getString("email"));
                    editor.putString("ccet_email",userData.getString("ccet_email"));

                    editor.commit();
                    finish();
                    progressDialog.dismiss();
                    Toast.makeText(StudentLogin.this, "Welcome : "+userData.getString("name"), Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(otp.this,homePage.class));

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(StudentLogin.this,"Exception: "+e.getMessage(),Toast.LENGTH_SHORT).show();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(StudentLogin.this,"Error Recieved : "+error,Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("table_name",table_name);
                params.put("roll_number",roll);
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