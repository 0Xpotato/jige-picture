package com.jige.jigepicturebackend.service;


import java.util.List;

public interface StpInterface {
    List<String> getPermissionList(Object loginId, String loginType);

    List<String> getRoleList(Object loginId, String loginType);
}
