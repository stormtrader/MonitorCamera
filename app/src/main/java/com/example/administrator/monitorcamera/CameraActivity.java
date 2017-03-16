package com.example.administrator.monitorcamera;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class CameraActivity extends FragmentActivity {

    private final static int REQUEST_DELETE_PHOTO = 1;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        //请求窗口特性：无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //添加窗口特性：全屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);

        //检查摄像头是否存在。
        PackageManager pm = getPackageManager();
        boolean hasCamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) ||
                pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT) ||
                Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD ||
                Camera.getNumberOfCameras() > 0;

        //根据检查结果进行布局
        if (!hasCamera) {
            setContentView(R.layout.activity_no_camera);
            return;
        }

        setContentView(R.layout.activity_camera);

        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.Fragment fragment = fm.findFragmentById(R.id.fragmentContainer_camera);

        if (fragment == null) {
            fragment = new CameraFragment();
            fm.beginTransaction().add(R.id.fragmentContainer_camera, fragment).commit();
        }

    }

}
