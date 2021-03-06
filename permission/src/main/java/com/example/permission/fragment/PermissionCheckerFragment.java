package com.example.permission.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.example.permission.Constants;
import com.example.permission.PermissionTypes;
import com.example.permission.ResultListener;
import com.example.permission.utils.PermissionClassification;
import com.example.permission.utils.RandomUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by 半米阳光 on 2017/9/15.
 */

public class PermissionCheckerFragment extends Fragment{
    private List<String> dangerousPermissionList = new ArrayList<>();
    private List<String> specialPermissionList = new ArrayList<>();
    private ResultListener mResultListener;

    /** 由于在fragment中请求权限会先回调Activity的方法，所以加入不同请求码的机制 */
    private int dangerousRequestCode = RandomUtil.getDifferentRandomNumber(PermissionTypes.DANGEROUS,"");
    private int systemAlertWindowRequestCode = RandomUtil.getDifferentRandomNumber(PermissionTypes.SPECIAL,Manifest.permission.SYSTEM_ALERT_WINDOW);
    private int writeSettingsRequestCode = RandomUtil.getDifferentRandomNumber(PermissionTypes.SPECIAL,Manifest.permission.WRITE_SETTINGS);

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        List<Fragment> fragments = getChildFragmentManager().getFragments();
//        if(fragments != null){
//            *//** 如果存在子fragment，将结果回调会子fragment的onActivityResult中 *//*
//            for(Fragment fragment : fragments)
//                if(fragment != null)
//                    fragment.onActivityResult(requestCode,resultCode,data);
//        }
//        else{
            if(requestCode == systemAlertWindowRequestCode){
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(!Settings.canDrawOverlays(getContext())){
                        Toast.makeText(getContext(),"悬浮窗权限被禁用,请在设置中手动开启！",Toast.LENGTH_SHORT).show();
                        mResultListener.onFailure();
                    }
                    else{
                        if(specialPermissionList.size() == 1)
                            mResultListener.onSuccess();
                        /** 继续申请第二个权限 */
                        else
                            requestSpecialPermission(Manifest.permission.WRITE_SETTINGS);
                    }
                }
            }
            else if(requestCode == writeSettingsRequestCode){
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(!Settings.System.canWrite(getContext())){
                        Toast.makeText(getContext(),"系统设置权限被禁用,请在设置中手动开启！",Toast.LENGTH_SHORT).show();
                        mResultListener.onFailure();
                    }
                    else
                        mResultListener.onSuccess();
                }

            }
        }
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        List<Fragment> fragments = getChildFragmentManager().getFragments();
//        if(fragments != null){
//            /** 如果存在子fragment，将结果回调会子fragment的onRequestPermissionResult中 */
//            for(Fragment fragment : fragments)
//                if(fragment != null)
//                    fragment.onRequestPermissionsResult(requestCode,permissions,grantResults);
//        }
//        else{
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
                    Toast.makeText(getContext(),"危险权限已被禁用，请在系统设置中手动开启！",Toast.LENGTH_SHORT).show();
                    mResultListener.onFailure();
                }

            }
        }

//    }


    public final void checkPermission(ResultListener resultListener,String... permissions){
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


    private boolean checkDangerousPermission(String permission){
        if(ContextCompat.checkSelfPermission(getContext(),permission) == PackageManager.PERMISSION_DENIED)
            return false;
        else
            return true;

    }

    private void requestDangerousPermissions(){
        String[] permissionArray = dangerousPermissionList.toArray(new String[dangerousPermissionList.size()]);
//        Fragment parentFragment = getParentFragment();
//        if(parentFragment == null)
//            requestPermissions(permissionArray, dangerousRequestCode);
//        /** 如果父fragment存在，则回调父fragment的requestPermissions方法 */
//        else
//            parentFragment.requestPermissions(permissionArray, dangerousRequestCode);
        requestPermissions(permissionArray, dangerousRequestCode);
    }

    private boolean checkSpecialPermission(String permission){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            switch (permission){
                case Manifest.permission.SYSTEM_ALERT_WINDOW:
                    if(!Settings.canDrawOverlays(getContext()))
                        return false;
                    else
                        return true;

                case Manifest.permission.WRITE_SETTINGS:
                    if(!Settings.System.canWrite(getContext()))
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
                        Uri.parse("package:" + getContext().getPackageName()));
                startActivityForResult(intent1,systemAlertWindowRequestCode);
                break;
            case Manifest.permission.WRITE_SETTINGS:
                Intent intent2 = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                        Uri.parse("package:" + getContext().getPackageName()));
                startActivityForResult(intent2, writeSettingsRequestCode);
                break;
            default:
        }
    }
}
