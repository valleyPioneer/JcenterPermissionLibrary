package com.example.permission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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

    /** 由于在fragment中请求权限会先回调Activity的方法，所以加入不同请求码的机制 */
    private static int dangerousRequestCode = RandomUtil.getDifferentRandomNumber(PermissionTypes.DANGEROUS,"");
    private static int systemAlertWindowRequestCode = RandomUtil.getDifferentRandomNumber(PermissionTypes.SPECIAL,Manifest.permission.SYSTEM_ALERT_WINDOW);
    private static int writeSettingsRequestCode = RandomUtil.getDifferentRandomNumber(PermissionTypes.SPECIAL,Manifest.permission.WRITE_SETTINGS);

    public static void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == systemAlertWindowRequestCode){
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
        else if(requestCode == writeSettingsRequestCode){
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
        if(requestCode == dangerousRequestCode){
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
                boolean shouldShowRequestPermissionRationale = false;
                for(String permission : permissions)
                    if(shouldShowRequestPermissionRationale(permission)){
                        shouldShowRequestPermissionRationale = true;
                        break;
                    }
                if(!shouldShowRequestPermissionRationale){
                    Toast.makeText(mContext,"危险权限已被禁用，请在系统设置中手动开启！",Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(mContext,"危险权限已被禁用！",Toast.LENGTH_SHORT).show();
                /** 标志本次权限申请失败，下次仍然可以以弹窗的形式申请权限 */
                mResultListener.onFailure();
            }

        }
    }


    public static void checkPermission(Object context,ResultListener resultListener,String... permissions){
        /** 由于是static变量，生命周期存在与整个application中 */
        dangerousPermissionList.clear();
        specialPermissionList.clear();

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
        /** 得知权限尚未获取后，加入弹窗判断是否已经拒绝过权限申请 */
        boolean shouldShowRequestPermissionRationale = false;
        /** 只要这组权限中有一个被拒绝过，那么这组权限必然都被拒绝过 */
        for(String permission : dangerousPermissionList)
            if(shouldShowRequestPermissionRationale(permission)){
                shouldShowRequestPermissionRationale = true;
                break;
            }

        if (shouldShowRequestPermissionRationale) {
            new AlertDialog.Builder(mContext)
                    .setMessage("该应用的正常运行需要权限支持！")
                    .setPositiveButton("马上申请", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestRealDangerousPermission();
                        }
                    })
                    .setNegativeButton("稍后再说", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(mContext,"危险权限已被禁用！",Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setCancelable(false)
                    .show();
        }
        else{
                requestRealDangerousPermission();
        }
    }

    private static void requestRealDangerousPermission(){
        String[] permissionArray = dangerousPermissionList.toArray(new String[dangerousPermissionList.size()]);
        if (mode == 0) {
            ActivityCompat.requestPermissions((Activity) mContext, permissionArray, dangerousRequestCode);
        } else {
            fragmentReference.requestPermissions(permissionArray, dangerousRequestCode);
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
                    ((Activity)mContext).startActivityForResult(intent1,systemAlertWindowRequestCode);
                else
                    fragmentReference.startActivityForResult(intent1,systemAlertWindowRequestCode);
                break;
            case Manifest.permission.WRITE_SETTINGS:
                Intent intent2 = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                        Uri.parse("package:" + mContext.getPackageName()));
                if(mode == 0)
                    ((Activity)mContext).startActivityForResult(intent2, writeSettingsRequestCode);
                else
                    fragmentReference.startActivityForResult(intent2, writeSettingsRequestCode);
                break;
            default:
        }
    }

    private static boolean shouldShowRequestPermissionRationale(String permission){
        if(mode == 0){
            return ActivityCompat.shouldShowRequestPermissionRationale((Activity)mContext,permission);
        }else{
            return fragmentReference.shouldShowRequestPermissionRationale(permission);
        }
    }
}
