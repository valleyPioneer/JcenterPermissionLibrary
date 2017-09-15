# JcenterPermissionLibrary

* 动态权限申请流程封装（包括危险权限和特殊权限）

* Android版本自动判断

* 代码上传至Jcenter

---

## 使用说明

* 回调接口ResultListener中方法的含义
1. void onSuccess()<br>
    含义：设备已拥有所申请的所有权限<br>
    回调场景：<br>
        1. 设备sdk低于23
        2. 设备所申请的所有权限（包括普通权限、危险权限、特殊权限）都已拥有

2. void onFailure()<br>
    含义：设备在申请权限的过程中发生一些错误，尚未拥有所有权限<br>
    回调场景：<br>
        1. 设备在申请权限时遭到拒绝

* Activity用法

1. 需要申请权限的activity继承PermissionCheckerActivity

2. 在需要申请权限的地方调用
    ```java
    checkPermission(ResultListener resultListener,String... permissions)
    ```
* Fragment用法

    * 需要申请权限的fragment没有父fragment
        1. 需要申请权限的fragment继承PermissionCheckerFragment

        2. 在需要申请权限的地方调用
            ```java
             checkPermission(ResultListener resultListener,String... permissions)
             ```

    * 需要申请权限的fragment有父fragment
        1. 需要申请权限的子fragment及其父fragment两者同时继承PermissionCheckerFragment

        2. 在需要申请权限的子fragment中调用父fragment
            ```java
                checkPermission(ResultListener resultListener,String... permissions)
            ```

