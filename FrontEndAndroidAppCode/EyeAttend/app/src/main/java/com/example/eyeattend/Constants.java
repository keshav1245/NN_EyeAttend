package com.example.eyeattend;

public class Constants {

    private static final String ROOT_URL = "http://192.168.1.8/EyeAttendScripts/php/";
    public static final String LOGIN_URL = ROOT_URL + "teacherLogin.php";
    public static final String FETCH_URL = ROOT_URL + "fecthTeacherData.php";
    public static final String SUBJECTS_URL = ROOT_URL + "fetchTeacherSubjects.php";
    public static final String IMAGE_URL = ROOT_URL + "fileUpload.php?apicall=";
    public static final String UPLOAD = IMAGE_URL + "uploadpic";
    public static final String FETCH_EXCEL = ROOT_URL+"getAllAttendence.php?table_name=";
    public static final String UPDATE_LINK = ROOT_URL+"updateText.php";
    public static final String LOGIN_URL_STU = ROOT_URL + "studentLogin.php";
    public static final String FETCH_URL_STU = ROOT_URL + "fetStudentData.php";
    public static final String SUBJECTS_URL_STU = ROOT_URL + "fetchStudentSubjects.php";
    public static final String STUDENT_STTENDANCE = ROOT_URL + "getStudentAttendence.php";

}
