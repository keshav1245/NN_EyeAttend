package com.example.eyeattend;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class Profile extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    private TextView prof_id, prof_name, prof_designation, prof_dept,prof_specialization,
                        prof__email,prof_contact;

    private ImageButton logout,social,clgweb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().hide();


        sharedPreferences = getSharedPreferences("TEACHER_DATA", Context.MODE_PRIVATE);

        prof_id = (TextView) findViewById(R.id.prof_id);
        prof_name = (TextView) findViewById(R.id.prof_name);
        prof_designation = (TextView) findViewById(R.id.prof_designation);
        prof_dept = (TextView) findViewById(R.id.prof_dept);
        prof_specialization = (TextView) findViewById(R.id.prof_specialization);
        prof__email = (TextView) findViewById(R.id.prof_email);
        prof_contact = (TextView) findViewById(R.id.prof_contact);

        logout = (ImageButton) findViewById(R.id.logout);
        social = (ImageButton) findViewById(R.id.social);
        clgweb = (ImageButton) findViewById(R.id.clgweb);



        prof_id.setText(sharedPreferences.getString("govt_id",""));
        prof_name.setText(sharedPreferences.getString("prof_name",""));
        prof_designation.setText(sharedPreferences.getString("designation",""));
        prof_dept.setText("Dept.: "+sharedPreferences.getString("dept",""));
        prof_specialization.setText(sharedPreferences.getString("specialization",""));
        prof__email.setText(sharedPreferences.getString("prof_email",""));
        prof_contact.setText(sharedPreferences.getString("contact",""));

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                Toast.makeText(Profile.this, "Logging Out...",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Profile.this,TeacherLogin.class));
            }
        });


        social.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Profile.this,"Social Profile Coming Soon",Toast.LENGTH_SHORT).show();
            }
        });


        clgweb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Profile.this,"Redirecting...!!!",Toast.LENGTH_SHORT).show();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://ccet.ac.in/"));
                startActivity(browserIntent);
            }
        });



    }
}