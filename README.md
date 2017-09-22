# PermissionChecker

[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[ ![Download](https://api.bintray.com/packages/valleypioneer/maven/PermissionChecker/images/download.svg) ](https://bintray.com/valleypioneer/maven/PermissionChecker/_latestVersion)


* 动态权限申请流程封装（包括危险权限和特殊权限）

* Android版本自动判断

* 代码上传至Jcenter

---

## 更新说明
* 简化Fragment嵌套时的处理逻辑
* 由于在Fragment中申请权限会先回调activity中的相应方法，所以重新引入不同请求码机制

## 使用说明

* 项目依赖地址
    ```java
    compile 'com.ebupt.valleypioneer:PermissionChecker:lastestVersion'
    ```

* 回调接口ResultListener中方法的含义
1. void onSuccess()<br>
    含义：设备已拥有所申请的所有权限<br>
    回调场景：<br>
        1. 设备sdk低于23<br>
        2. 设备所申请的所有权限（包括普通权限、危险权限、特殊权限）都已拥有

2. void onFailure()<br>
    含义：设备在申请权限的过程中发生一些错误，尚未拥有所有权限<br>
    回调场景：<br>
        1. 设备在申请权限时遭到拒绝

* 用法
    1. 在需要申请权限的Activity或者Fragment中使用
        ```java
        PermissionChecker.checkPermission(ResultListener resultListener,String... permissions)
        ```
    2. 如果申请危险权限，那么需要在
        ```java
        @Override
            public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        ```
        此方法中加入
        ```java
        PermissionChecker.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ```
    3. 如果申请特殊权限，那么需要在
        ```java
         @Override
            protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                super.onActivityResult(requestCode, resultCode, data);
            }
        ```
        此方法中加入
        ```java
        PermissionChecker.onActivityResult(requestCode,resultCode,data);
        ```
---

测试后发现，小米手机在动态申请SYSTEM_ALERT_WINDOW权限时，应用程序会崩溃一次，然后重启，因此该权限的申请会导致接口回调多次，尚待解决！<br>
MIUI系统更新之后，权限申请机制似乎有所改变，该bug不再复现，迷！！！
