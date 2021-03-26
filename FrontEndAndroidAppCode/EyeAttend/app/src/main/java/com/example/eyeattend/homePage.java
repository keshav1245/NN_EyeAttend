package com.example.eyeattend;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class homePage extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    private TextView heading;
    private LinearLayout linearLayout,options;
    private JSONObject obj;
    private ImageButton show_options;
    private Boolean tog = false;
    private Spinner spinner;
    private ImageButton profile;
    private Dialog dialog_updated;
    private ProgressDialog progressDialog;
    private ArrayList<String> subjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        getSupportActionBar().hide();
        sharedPreferences = getSharedPreferences("TEACHER_DATA", Context.MODE_PRIVATE);
        heading = (TextView)findViewById(R.id.heading);
        linearLayout = (LinearLayout)findViewById(R.id.mainLinearlayout);

        subjects = new ArrayList<>();


        progressDialog = new ProgressDialog(homePage.this);
        progressDialog.setCancelable(false);



        heading.setText(sharedPreferences.getString("prof_name","Welcome!"));

        show_options = (ImageButton) findViewById(R.id.show_options);
        options = (LinearLayout) findViewById(R.id.options);


        spinner = (Spinner)findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> adapter =  ArrayAdapter.createFromResource(this,R.array.streams,android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        spinner.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter_ =  ArrayAdapter.createFromResource(this,R.array.semester,android.R.layout.simple_spinner_item);

        adapter_.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);

//
//
        profile = (ImageButton) findViewById(R.id.profile);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(homePage.this,Profile.class));
            }
        });


        show_options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tog){
                    int margin = getInDip(-52);
                    show_options.setImageResource(R.drawable.ic_chevron_left);
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) options.getLayoutParams();
                    layoutParams.setMargins(0,0,margin,0);
                    options.setLayoutParams(layoutParams);
                    tog = false;
                }else{
                    show_options.setImageResource(R.drawable.ic_chevron_right);
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) options.getLayoutParams();
                    layoutParams.setMargins(0,0,0,0);
                    options.setLayoutParams(layoutParams);
                    tog = true;
                }
            }
        });

        fetchSubjects();




    }

    private int getInDip(float val_in_dip){
        float d = getApplicationContext().getResources().getDisplayMetrics().density;
        int margin = (int)(val_in_dip * d);

        return margin;
    }

    private void fetchSubjects(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.SUBJECTS_URL, new Response.Listener<String>() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onResponse(String response) {
                try {
                    final JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.has("success")){
                        JSONArray jsonArray = jsonObject.getJSONArray("success");
                        for (int i = 0;i<jsonArray.length();i++){
                            obj = jsonArray.getJSONObject(i);



                            LinearLayout linearLayoutInnerMain = new LinearLayout(getApplicationContext());

                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                            lp.setMargins(0,0,0,getInDip(30));
                            linearLayoutInnerMain.setLayoutParams(lp);
                            linearLayoutInnerMain.setOrientation(LinearLayout.VERTICAL);
                            linearLayoutInnerMain.setBackgroundResource(R.drawable.subjects_back);




                            LinearLayout grid_main = new LinearLayout(getApplicationContext());
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);


                            grid_main.setLayoutParams(layoutParams);
                            grid_main.setOrientation(LinearLayout.HORIZONTAL);
                            grid_main.setWeightSum(2f);
                            int m = getInDip(15);
                            grid_main.setPadding(m,m,m,m);


                            LinearLayout linearLayoutLeft = new LinearLayout(getApplicationContext());
                            linearLayoutLeft.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,.4f));
                            linearLayoutLeft.setOrientation(LinearLayout.VERTICAL);

                            TextView subjectCode = new TextView(getApplicationContext());
                            subjectCode.setTextSize(30f);
                            subjectCode.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.headings));
                            subjectCode.setText(obj.getString("course_code"));

                            subjects.add(obj.getString("course_code"));

                            TextView subjectTitle = new TextView(getApplicationContext());
                            subjectTitle.setTextSize(15f);
                            subjectTitle.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.black));
                            subjectTitle.setText(obj.getString("course_title"));

                            TextView subjectBranch = new TextView(getApplicationContext());
                            subjectBranch.setTextSize(12f);
                            subjectBranch.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.colorPrimaryDark));
                            subjectBranch.setText(obj.getString("branch"));

                            TextView subjectSemester = new TextView(getApplicationContext());
                            subjectSemester.setTextSize(12f);
                            subjectSemester.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.colorPrimaryDark));
                            subjectSemester.setText("Semester : "+obj.get("semester"));

                            TextView subjectCredits = new TextView(getApplicationContext());
                            subjectCredits.setTextSize(12f);
                            subjectSemester.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.colorPrimaryDark));
                            subjectCredits.setText("Credits : "+obj.get("credits"));
                            subjectCredits.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.black));

                            linearLayoutLeft.addView(subjectCode);
                            linearLayoutLeft.addView(subjectTitle);
                            linearLayoutLeft.addView(subjectBranch);
                            linearLayoutLeft.addView(subjectSemester);
                            linearLayoutLeft.addView(subjectCredits);

                            final String course_id= obj.getString("course_code");
                            final String subject_branch=obj.getString("branch");

                            final int sem = Integer.parseInt(obj.getString("semester"));
                            int year = Calendar.getInstance().get(Calendar.YEAR);

                            final int batch = year - ((int)sem/2);

                            ImageButton buttonRight = new ImageButton(getApplicationContext());

                            buttonRight.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT,.8f));
                            buttonRight.setImageResource(R.drawable.ic_assignment);
                            buttonRight.setBackgroundColor(getColor(R.color.white));
                            buttonRight.setScaleType(ImageView.ScaleType.FIT_CENTER);

                            ImageButton buttonDown = new ImageButton(getApplicationContext());

                            buttonDown.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT,.8f));
                            buttonDown.setImageResource(R.drawable.ic_file_download);
                            buttonDown.setBackgroundColor(getColor(R.color.white));
                            buttonDown.setScaleType(ImageView.ScaleType.FIT_CENTER);

                            Button updateBut = new Button(getApplicationContext());
                            LinearLayout.LayoutParams lparm = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                            int m_p = getInDip(5);
                            updateBut.setLayoutParams(lparm);
                            updateBut.setPadding(m_p,m_p,m_p,m_p);
                            updateBut.setText("Update Attendence");
                            updateBut.setBackgroundResource(R.drawable.update_back);

                            updateBut.setTextColor(getColor(R.color.white));


                            updateBut.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    String stream = subject_branch;
                                    if(subject_branch.contentEquals("AS")){
                                        stream = spinner.getSelectedItem().toString();
                                        if(stream.contentEquals("Choose Branch")){
                                            Toast.makeText(getApplicationContext(),"Choose Branch for Applied Students...!!",Toast.LENGTH_SHORT).show();
                                            return;
                                        }else{
                                            stream = subject_branch+"_"+stream;
                                        }
                                    }


                                    dialog_updated = new Dialog(homePage.this);
                                    dialog_updated.setContentView(R.layout.new_update_form);
                                    dialog_updated.setCancelable(false);
                                    ImageButton close_diag = (ImageButton) dialog_updated.findViewById(R.id.close_dialog);




                                    close_diag.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog_updated.dismiss();
                                        }
                                    });
                                    final TextView sub_code = (TextView) dialog_updated.findViewById(R.id.subject_spin);
                                    sub_code.setText(course_id);
                                    final TextView str = (TextView) dialog_updated.findViewById(R.id.stream_spin);
                                    str.setText(stream);
                                    final TextView sem_text = (TextView) dialog_updated.findViewById(R.id.sem_spin);
                                    sem_text.setText(""+batch);
                                    final TextView date = (TextView) dialog_updated.findViewById(R.id.date);
                                    final RadioGroup radioGroup = (RadioGroup) dialog_updated.findViewById(R.id.radio_group);
                                    final EditText roll = (EditText) dialog_updated.findViewById(R.id.roll_number);
                                    final RadioButton radioButton;



                                    date.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Calendar cal = Calendar.getInstance();
                                            int year = cal.get(Calendar.YEAR);
                                            int month = cal.get(Calendar.MONTH);
                                            int day = cal.get(Calendar.DAY_OF_MONTH);

                                            DatePickerDialog datePickerDialog = new DatePickerDialog(homePage.this, new DatePickerDialog.OnDateSetListener() {
                                                @Override
                                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                                    date.setText(year+"-"+(month+1)+"-"+dayOfMonth);
                                                }
                                            },year,month,day);
                                            datePickerDialog.show();
                                        }
                                    });

                                    Button submit = (Button) dialog_updated.findViewById(R.id.update_attendence);

                                    submit.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if(radioGroup.getCheckedRadioButtonId() == -1){
                                                Toast.makeText(homePage.this,"No Option Checked!",Toast.LENGTH_SHORT).show();
                                            }else{
                                                if(roll.getText().toString().isEmpty()){
                                                    roll.setError("Enter Roll number");
                                                    roll.requestFocus();
                                                }else{
                                                    if(date.getText().toString().contentEquals("Choose Date")){
                                                        Toast.makeText(homePage.this,"Choose Date of Attendence",Toast.LENGTH_SHORT).show();
                                                    }else{
                                                        String stream = str.getText().toString();

                                                        String pre_abs;
                                                        if(((RadioButton) dialog_updated.findViewById(radioGroup.getCheckedRadioButtonId())).getText().toString() == "Present"){
                                                            pre_abs = "P";
                                                        }else{
                                                            pre_abs = "A";
                                                        }

                                                        String batch = sem_text.getText().toString();
                                                        String table_name = sharedPreferences.getString("govt_id","")+"_"+sub_code.getText().toString()+"_"+
                                                                    stream+"_"+batch;
//                                                        Toast.makeText(homePage.this,"TABLE NAME : "+table_name,Toast.LENGTH_LONG).show();
//                                                        Toast.makeText(homePage.this,"P or A : "+pre_abs,Toast.LENGTH_LONG).show();
//                                                        Toast.makeText(homePage.this,"Date : "+date.getText().toString(),Toast.LENGTH_LONG).show();
//                                                        Toast.makeText(homePage.this,"Students : "+roll.getText().toString(),Toast.LENGTH_LONG).show();

                                                        markPresent(table_name,pre_abs,date.getText().toString().trim(),roll.getText().toString().trim());

                                                    }
                                                }
                                            }
                                        }
                                    });


                                    dialog_updated.show();

                                }
                            });

                            buttonDown.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String stream = subject_branch;
                                    if(subject_branch.contentEquals("AS")){
                                        stream = spinner.getSelectedItem().toString();
                                        if(stream.contentEquals("Choose Branch")){
                                            Toast.makeText(getApplicationContext(),"Choose Branch for Applied Students...!!",Toast.LENGTH_SHORT).show();
                                            return;
                                        }else{
                                            stream = subject_branch+"_"+stream;
                                        }
                                    }

                                    String prof_id = sharedPreferences.getString("govt_id","");

                                    String table_name = prof_id+"_"+course_id+"_"+stream+"_"+batch;

                                    String url = Constants.FETCH_EXCEL+table_name;
                                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                                    request.setDescription("Attendance : "+table_name);
                                    request.setTitle(table_name+" Attendance");
// in order for this if to run, you must use the android 3.2 to compile your app
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                        request.allowScanningByMediaScanner();
                                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                    }
                                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, table_name+"_attendance.csv");

// get download service and enqueue file
                                    DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                                    manager.enqueue(request);
                                }
                            });


                            final Intent intent = new Intent(homePage.this,ImageUpload.class);
                            buttonRight.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    String stream = subject_branch;
                                     if(subject_branch.contentEquals("AS")){
                                         stream = spinner.getSelectedItem().toString();
                                         if(stream.contentEquals("Choose Branch")){
                                             Toast.makeText(getApplicationContext(),"Choose Branch for Applied Students...!!",Toast.LENGTH_SHORT).show();
                                             return;
                                         }else{
                                             stream = subject_branch+"_"+stream;
                                         }
                                     }

                                    Toast.makeText(getApplicationContext(),"Taking Attendence !",Toast.LENGTH_SHORT).show();

                                    intent.putExtra("tags","defaultTags");
                                    String prof_id = sharedPreferences.getString("govt_id","");

                                    intent.putExtra("filename",prof_id+"_"+course_id+"_"+stream+"_"+batch);
                                    intent.putExtra("branch",stream);
                                    intent.putExtra("batch",""+batch);
                                    startActivity(intent);
                                }
                            });



                            grid_main.addView(linearLayoutLeft);
                            grid_main.addView(buttonDown);
                            grid_main.addView(buttonRight);

                            linearLayoutInnerMain.addView(grid_main);
                            linearLayoutInnerMain.addView(updateBut);

                            linearLayout.addView(linearLayoutInnerMain);
                        }

                    }else if(jsonObject.has("error")){
                        Toast.makeText(homePage.this,jsonObject.get("error").toString(),Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(homePage.this,"Error Response : "+error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("govt_id",sharedPreferences.getString("govt_id",""));
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

    private void markPresent(final String table_name, final String pre_abs, final String date, final String students){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.UPDATE_LINK, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(homePage.this,"On Response : "+response,Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(homePage.this,"On ErrorResponse : "+error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();

                params.put("table_name",table_name);
                params.put("attendence",pre_abs);
                params.put("date",date);
                params.put("students",students);

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