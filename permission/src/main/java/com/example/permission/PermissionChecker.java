package com.example.permission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;


import com.example.permission.PermissionTypes;
import com.example.permission.ResultListener;
import com.example.permission.utils.PermissionClassification;
import com.example.permission.utils.RandomUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by 半米阳光 on 2017/9/14.
 */

public class PermissionChecker {

    private static List<String> dangerousPermissionList = new ArrayList<>();
    private static List<String> specialPermissionList = new ArrayList<>();
    private static ResultListener mResultListener;

    private static Context mContext; /** 其实一直都是activity类型的对象 */
    private static Fragment fragmentReference;
    private static int mode;//0代表activity，1代表fragment

    public static void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Constants.SYSTEM_ALERT_WINDOW){
            Log.d("Permission","run into system alert window");
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(!Settings.canDrawOverlays(mContext)){
                    Toast.makeText(mContext,"悬浮窗权限被禁用,请在设置中手动开启！",Toast.LENGTH_SHORT).show();
                    mResultListener.onFailure();
                }
                else{
                    if(specialPermissionList.size() == 1) {
                        Log.d("Permission", "ready to invoke onSuccess in system alert window");
                        mResultListener.onSuccess();
                    }
                    /** 继续申请第二个权限 */
                    else{
                        Log.d("Permission", "request write permission");
                        requestSpecialPermission(Manifest.permission.WRITE_SETTINGS);
                    }

                }


            }
        }
        else if(requestCode == Constants.WRITE_SETTING_REQUEST_CODE){
            Log.d("Permission","run into write setting");
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(!Settings.System.canWrite(mContext)){
                    Toast.makeText(mContext,"系统设置权限被禁用,请在设置中手动开启！",Toast.LENGTH_SHORT).show();
                    mResultListener.onFailure();
                }
                else{
                    Log.d("Permission", "run 3");
                    mResultListener.onSuccess();
                }

            }

        }
    }

    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == Constants.DANGEROUS_PERMISSION_REQUEST_CODE){
            boolean allGranted = true;
            for(int i = 0; i < grantResults.length;i++){
                if(grantResults[i] == PackageManager.PERMISSION_DENIED){
                    allGranted = false;
                    break;
                }
            }
            if (allGranted){
                /** 危险权限申请完毕，开始申请特殊权限 */
                if (!specialPermissionList.isEmpty()){
                    if(specialPermissionList.size() == 1)
                        requestSpecialPermission(specialPermissionList.get(0));
                    else if(specialPermissionList.size() == 2)
                    /** 先申请system_alert_window权限 */
                        requestSpecialPermission(Manifest.permission.SYSTEM_ALERT_WINDOW);
                }
                else
                    mResultListener.onSuccess();

            }
            else{
                Toast.makeText(mContext,"危险权限已被禁用，请在系统设置中手动开启！",Toast.LENGTH_SHORT).show();
                mResultListener.onFailure();
            }

        }
    }


    public static void checkPermission(Object context,ResultListener resultListener,String... permissions){
        if(context instanceof Activity){
            mContext = (Activity)context;
            mode = 0;
        }
        else if(context instanceof Fragment){
            mContext = ((Fragment)context).getContext();
            mode = 1;
            fragmentReference = (Fragment) context;
        }

        mResultListener = resultListener;

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            /** 系统版本低于6.0直接回调成功接口 */
            mResultListener.onSuccess();
            return;
        }


        Boolean isReady = false;

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
            if (!dangerousPermissionList.isEmpty())
                requestDangerousPermissions();
            else if (!specialPermissionList.isEmpty()){
                Log.d("Permission","run 0");
                if(specialPermissionList.size() == 1)
                    requestSpecialPermission(specialPermissionList.get(0));
                else if(specialPermissionList.size() == 2)
                    /** 先申请system_alert_window权限 */
                    requestSpecialPermission(Manifest.permission.SYSTEM_ALERT_WINDOW);
            }
        }
        else{
            /** 无需申请权限,回调成功接口 */
            mResultListener.onSuccess();
        }

    }


    private static boolean checkDangerousPermission(String permission){
        if(ContextCompat.checkSelfPermission(mContext,permission) == PackageManager.PERMISSION_DENIED)
            return false;
        else
            return true;

    }

    private static void requestDangerousPermissions(){
        if(mode == 0){
            String[] permissionArray = dangerousPermissionList.toArray(new String[dangerousPermissionList.size()]);
            ActivityCompat.requestPermissions((Activity) mContext,permissionArray, Constants.DANGEROUS_PERMISSION_REQUEST_CODE);
        }
        else{
            String[] permissionArray = dangerousPermissionList.toArray(new String[dangerousPermissionList.size()]);
            fragmentReference.requestPermissions(permissionArray, Constants.DANGEROUS_PERMISSION_REQUEST_CODE);
        }
       
    }
    
    private static boolean checkSpecialPermission(String permission){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            switch (permission){
                case Manifest.permission.SYSTEM_ALERT_WINDOW:
                    if(!Settings.canDrawOverlays(mContext))
                        return false;
                    else
                        return true;

                case Manifest.permission.WRITE_SETTINGS:
                    if(!Settings.System.canWrite(mContext))
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

    private static void requestSpecialPermission(String permission){
        switch (permission){
            case Manifest.permission.SYSTEM_ALERT_WINDOW:
                Intent intent1 = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + mContext.getPackageName()));
                if(mode == 0)
                    ((Activity)mContext).startActivityForResult(intent1,Constants.SYSTEM_ALERT_WINDOW);
                else
                    fragmentReference.startActivityForResult(intent1,Constants.SYSTEM_ALERT_WINDOW);
                break;
            case Manifest.permission.WRITE_SETTINGS:
                Intent intent2 = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                        Uri.parse("package:" + mContext.getPackageName()));
                if(mode == 0)
                    ((Activity)mContext).startActivityForResult(intent2, Constants.WRITE_SETTING_REQUEST_CODE);
                else
                    fragmentReference.startActivityForResult(intent2, Constants.WRITE_SETTING_REQUEST_CODE);
                break;
            default:
        }
    }
}
