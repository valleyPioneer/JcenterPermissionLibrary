package com.example.permission.utils;

import android.Manifest;
import android.support.annotation.MainThread;
import android.text.TextUtils;

import com.example.permission.PermissionTypes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 半米阳光 on 2017/9/14.
 */

public class PermissionClassification {
    private static List<String> dangerousPermissionList = new ArrayList<>();
    private static List<String> specialPermissionList = new ArrayList<>();

    private static void fillPermissionList(){
        dangerousPermissionList.add(Manifest.permission.READ_CALENDAR);
        dangerousPermissionList.add(Manifest.permission.WRITE_CALENDAR);
        dangerousPermissionList.add(Manifest.permission.CAMERA);
        dangerousPermissionList.add(Manifest.permission.READ_CONTACTS);
        dangerousPermissionList.add(Manifest.permission.WRITE_CONTACTS);
        dangerousPermissionList.add(Manifest.permission.GET_ACCOUNTS);
        dangerousPermissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        dangerousPermissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        dangerousPermissionList.add(Manifest.permission.RECORD_AUDIO);
        dangerousPermissionList.add(Manifest.permission.READ_PHONE_STATE);
        dangerousPermissionList.add(Manifest.permission.CALL_PHONE);
        dangerousPermissionList.add(Manifest.permission.READ_CALL_LOG);
        dangerousPermissionList.add(Manifest.permission.WRITE_CALL_LOG);
        dangerousPermissionList.add(Manifest.permission.ADD_VOICEMAIL);
        dangerousPermissionList.add(Manifest.permission.USE_SIP);
        dangerousPermissionList.add(Manifest.permission.PROCESS_OUTGOING_CALLS);
        dangerousPermissionList.add(Manifest.permission.BODY_SENSORS);
        dangerousPermissionList.add(Manifest.permission.SEND_SMS);
        dangerousPermissionList.add(Manifest.permission.RECEIVE_SMS);
        dangerousPermissionList.add(Manifest.permission.READ_SMS);
        dangerousPermissionList.add(Manifest.permission.RECEIVE_WAP_PUSH);
        dangerousPermissionList.add(Manifest.permission.RECEIVE_MMS);
        dangerousPermissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        dangerousPermissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        specialPermissionList.add(Manifest.permission.SYSTEM_ALERT_WINDOW);
        specialPermissionList.add(Manifest.permission.WRITE_SETTINGS);
    }

    public static PermissionTypes classify(String permission){
        fillPermissionList();
        if(TextUtils.isEmpty(permission))
            return null;
        if(dangerousPermissionList.contains(permission))
            return PermissionTypes.DANGEROUS;
        else if(specialPermissionList.contains(permission))
            return PermissionTypes.SPECIAL;
        else
            return PermissionTypes.NORMAL;

    }
}
