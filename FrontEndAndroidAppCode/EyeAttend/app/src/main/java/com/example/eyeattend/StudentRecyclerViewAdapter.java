package com.example.eyeattend;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class StudentRecyclerViewAdapter extends RecyclerView.Adapter<StudentRecyclerViewAdapter.ViewHolder>{


    private static final String TAG = "StudentRecyclerViewAdapter";


    private JSONArray studentSubjects = new JSONArray();
    private Context context;
    SharedPreferences sharedPreferences;

    public StudentRecyclerViewAdapter(JSONArray studentSubjects, Context context) {
        this.studentSubjects = studentSubjects;
        this.context = context;
        sharedPreferences = this.context.getSharedPreferences("STUDENT_DATA",Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stu_subject_listitem,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            final JSONObject subject = studentSubjects.getJSONObject(position);
            holder.course_code.setText(subject.getString("course_code"));
            holder.course_title.setText(subject.getString("course_title"));
            holder.prof_name.setText(subject.getString("prof_name"));

            final int sem = Integer.parseInt(subject.getString("semester"));
            int year = Calendar.getInstance().get(Calendar.YEAR);

            final int batch = year - ((int)sem/2);

            final String table_name = subject.getString("prof_id")+"_"+subject.getString("course_code")+
                    "_"+subject.getString("branch")+"_"+batch;

            holder.getAttendance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        DownLoadCSV(table_name,sharedPreferences.getString("roll_number",""),subject.getString("course_code"));
                    } catch (MalformedURLException | JSONException e) {
                        Toast.makeText(context,"Download Exeption : "+e.getMessage(),Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            });



        } catch (JSONException e) {
            Toast.makeText(context,"Exception JSON : "+e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return studentSubjects.length();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout parentLayout;
        LinearLayout mainBox,textViewLayout;
        TextView course_code, course_title, prof_name;
        Button getAttendance;

        public ViewHolder(View itemView) {

            super(itemView);
            parentLayout = itemView.findViewById(R.id.parentLayout);
            mainBox = itemView.findViewById(R.id.mainBoxLayout);
            textViewLayout = itemView.findViewById(R.id.textViewLL);
            course_code = itemView.findViewById(R.id.sub_code);
            course_title = itemView.findViewById(R.id.sub_name);
            prof_name = itemView.findViewById(R.id.prof_name);
            getAttendance = itemView.findViewById(R.id.get_attendance);


        }
    }

    public void DownLoadCSV(String table_name,String roll_no,String course_code) throws MalformedURLException {

        String url = Constants.STUDENT_STTENDANCE+"?table_name="+table_name+"&roll_no="+roll_no;
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("Attendance for Roll Number : "+roll_no);
        request.setTitle(course_code+" Attendance");
// in order for this if to run, you must use the android 3.2 to compile your app
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, roll_no+"_"+course_code+"_attendance.csv");

// get download service and enqueue file
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);


    }

}
