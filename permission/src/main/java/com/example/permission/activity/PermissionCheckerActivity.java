package com.example.permission.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.example.permission.Constants;
import com.example.permission.PermissionTypes;
import com.example.permission.utils.PermissionClassification;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 半米阳光 on 2017/9/14.
 */

public class PermissionCheckerActivity extends AppCompatActivity {

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    public final void checkPermission(String... permissions){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return;

        Boolean isReady = false;
        List<String> dangerousPermissionList = new ArrayList<>();
        List<String> specialPermissionList = new ArrayList<>();

        for(String permission : permissions){
            PermissionTypes type = PermissionClassification.classify(permission);

            switch (type){
                case DANGEROUS:
                    if(!checkDangerousPermission(permission)){
                        dangerousPermissionList.add(permission);
                        isReady = true;
                    }
                    break;
                case SPECIAL:
                    if(!checkSpecialPermission(permission)){
                        specialPermissionList.add(permission);
                        isReady = true;
                    }
                    break;
                default:
            }
        }

        /** 对权限进行统一化申请 */
        if(isReady){
            if(!dangerousPermissionList.isEmpty())
                requestDangerousPermissions(dangerousPermissionList);

            if(!specialPermissionList.isEmpty())
                for(String permission : specialPermissionList)
                    requestSpecialPermission(permission);
        }

    }


    private boolean checkDangerousPermission(String permission){
        if(ActivityCompat.checkSelfPermission(this,permission) == PackageManager.PERMISSION_DENIED)
            return false;
        else
            return true;

    }

    private void requestDangerousPermissions(List<String> dangerousPermissionList){
        String[] permissionArray = dangerousPermissionList.toArray(new String[dangerousPermissionList.size()]);
        ActivityCompat.requestPermissions(this,permissionArray, Constants.DANGEROUS_PERMISSION_REQUEST_CODE);
    }

    private boolean checkSpecialPermission(String permission){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            switch (permission){
                case Manifest.permission.SYSTEM_ALERT_WINDOW:
                    if(!Settings.canDrawOverlays(this))
                        return false;
                    else
                        return true;

                case Manifest.permission.WRITE_SETTINGS:
                    if(!Settings.System.canWrite(this))
                      return false;
                    else
                        return true;

                    /** 实际上不可达 */
                default:
                    return true;
            }
        }
        else
            return true;
    }

    private void requestSpecialPermission(String permission){
        switch (permission){
            case Manifest.permission.SYSTEM_ALERT_WINDOW:
                Intent intent1 = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent1,Constants.SPECIAL_PERMISSION_REQUEST_CODE);
                break;
            case Manifest.permission.WRITE_SETTINGS:
                Intent intent2 = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent2, Constants.SPECIAL_PERMISSION_REQUEST_CODE);
                break;
            default:
        }
    }
}
