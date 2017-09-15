package com.example.permission.utils;

import android.Manifest;

import com.example.permission.PermissionTypes;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

/**
 * Created by 半米阳光 on 2017/9/15.
 */

public class RandomUtil {
    private static LinkedHashSet<Integer> dangerousPermissionRandomSet = new LinkedHashSet<>();
    private static LinkedHashSet<Integer> systemAlertWindowRandomSet = new LinkedHashSet<>();
    private static LinkedHashSet<Integer> writeSettingRandomSet = new LinkedHashSet<>();

    public static int getDifferentRandomNumber(PermissionTypes permissionTypes,String permission){
        Random random = new Random();
        switch (permissionTypes){
            case DANGEROUS:
                while(!dangerousPermissionRandomSet.add(random.nextInt(5) + 10));
                return getLastElementInLinkedHashSet(dangerousPermissionRandomSet);
            case SPECIAL:
                if(permission.equals(Manifest.permission.SYSTEM_ALERT_WINDOW)){
                    while (!systemAlertWindowRandomSet.add(random.nextInt(5) + 20));
                    return getLastElementInLinkedHashSet(systemAlertWindowRandomSet);
                }
                else if(permission.equals(Manifest.permission.WRITE_SETTINGS)){
                    while (!writeSettingRandomSet.add(random.nextInt(5) + 30));
                    return getLastElementInLinkedHashSet(writeSettingRandomSet);
                }
                /** 实际不可达 */
            default:
                return -1;
        }
    }

    private static <T> T  getLastElementInLinkedHashSet(LinkedHashSet<T> linkedHashSet){
        Iterator<T> iterator = linkedHashSet.iterator();
        T tail = null;
        while (iterator.hasNext())
            tail = iterator.next();
        return tail;
    }
}
