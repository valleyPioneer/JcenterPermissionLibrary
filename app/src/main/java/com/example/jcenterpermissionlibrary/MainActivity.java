package com.example.jcenterpermissionlibrary;


import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.permission.PermissionChecker;
import com.example.permission.ResultListener;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Fragment fragment = new ParentFragment();
        //getSupportFragmentManager().beginTransaction().add(R.id.frameLayout,fragment).commit();
          requestPermissions();
    }

    private void requestPermissions(){
        PermissionChecker.checkPermission(this, new ResultListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this,"成功",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure() {
                Toast.makeText(MainActivity.this,"失败",Toast.LENGTH_SHORT).show();
            }
        },Manifest.permission.CAMERA,Manifest.permission.READ_PHONE_STATE,Manifest.permission.WRITE_SETTINGS,Manifest.permission.SYSTEM_ALERT_WINDOW);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PermissionChecker.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionChecker.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
