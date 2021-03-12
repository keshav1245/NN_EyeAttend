package com.example.eyeattend;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class StudentHomepage extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView heading;
    SharedPreferences sharedPreferences;
    private ImageButton logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_homepage);

        getSupportActionBar().hide();
        sharedPreferences = getSharedPreferences("STUDENT_DATA", Context.MODE_PRIVATE);
        heading = (TextView)findViewById(R.id.heading);
        logout = (ImageButton) findViewById(R.id.stu_logout);


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                Toast.makeText(StudentHomepage.this, "Logging Out...",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(StudentHomepage.this,StudentLogin.class));
            }
        });



        heading.setText(sharedPreferences.getString("name","")+"\n"+sharedPreferences.getString("roll_number",""));

        int batch = Integer.parseInt(sharedPreferences.getString("batch",""));
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int sem;
        if(month >= 7){
            sem = (year-batch)*2 + 1;
        }else{
            sem = (year-batch)*2;
        }

        fetchSubjects(sharedPreferences.getString("branch",""),sem);
    }

    private void fetchSubjects(final String branch, final int semester){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.SUBJECTS_URL_STU, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("success");
                    init_recycler_view(jsonArray);
                } catch (JSONException e) {
                    Toast.makeText(StudentHomepage.this,"JSON Exception : "+e.getMessage(),Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(StudentHomepage.this,"onErrorResponse : "+error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("semester",semester+"");
                params.put("branch",branch);
                return params;
            }
        };

        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

    }

    private void init_recycler_view(JSONArray jsonArray){
        recyclerView = (RecyclerView) findViewById(R.id.stu_subject_recyclerview);
        StudentRecyclerViewAdapter studentRecyclerViewAdapter = new StudentRecyclerViewAdapter(jsonArray,StudentHomepage.this);
        recyclerView.setAdapter(studentRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(StudentHomepage.this));
    }

}