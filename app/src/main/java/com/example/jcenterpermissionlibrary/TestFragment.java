package com.example.jcenterpermissionlibrary;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.permission.ResultListener;
import com.example.permission.fragment.PermissionCheckerFragment;

/**
 * Created by 半米阳光 on 2017/9/15.
 */

public class TestFragment extends PermissionCheckerFragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main,container,false);
        return view;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestPermission();
    }

    private void requestPermission(){
        checkPermission(new ResultListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getContext(),"成功",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure() {
                Toast.makeText(getContext(),"失败",Toast.LENGTH_SHORT).show();
            }
        }, Manifest.permission.CAMERA,Manifest.permission.WRITE_SETTINGS,Manifest.permission.SYSTEM_ALERT_WINDOW,Manifest.permission.READ_PHONE_STATE);
    }
}
