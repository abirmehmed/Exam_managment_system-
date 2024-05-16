package com.example.myapplication.models;

import android.app.Application;
import com.example.myapplication.activities.TeacherHomepageActivity;

public class MyApplication extends Application {
    private static TeacherHomepageActivity teacherHomepageActivity;

    public static void setTeacherHomepageActivity(TeacherHomepageActivity activity) {
        teacherHomepageActivity = activity;
    }

    public static TeacherHomepageActivity getTeacherHomepageActivity() {
        return teacherHomepageActivity;
    }
}
