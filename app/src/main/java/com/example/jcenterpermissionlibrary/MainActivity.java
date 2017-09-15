package com.example.jcenterpermissionlibrary;


import android.Manifest;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.permission.ResultListener;
import com.example.permission.activity.PermissionCheckerActivity;

public class MainActivity extends PermissionCheckerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Fragment fragment = new ParentFragment();
//        getSupportFragmentManager().beginTransaction().add(R.id.frameLayout,fragment).commit();
        requestPermission();
    }

    private void requestPermission(){
        checkPermission(new ResultListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this,"成功",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure() {
                Toast.makeText(MainActivity.this,"失败",Toast.LENGTH_SHORT).show();
            }
        }, Manifest.permission.CAMERA,Manifest.permission.SYSTEM_ALERT_WINDOW,Manifest.permission.WRITE_SETTINGS,Manifest.permission.READ_PHONE_STATE);
    }

}
