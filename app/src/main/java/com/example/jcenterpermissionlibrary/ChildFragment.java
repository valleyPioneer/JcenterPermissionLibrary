package com.example.jcenterpermissionlibrary;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.permission.PermissionChecker;
import com.example.permission.ResultListener;

/**
 * Created by 半米阳光 on 2017/9/21.
 */

public class ChildFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main,container,false);
   //     requestPermissions();
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PermissionChecker.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionChecker.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    private void requestPermissions(){
        PermissionChecker.checkPermission(this, new ResultListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getContext(),"成功",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure() {
                Toast.makeText(getContext(),"失败",Toast.LENGTH_SHORT).show();
            }
        },Manifest.permission.CAMERA,Manifest.permission.WRITE_SETTINGS,Manifest.permission.SYSTEM_ALERT_WINDOW);
    }
}
