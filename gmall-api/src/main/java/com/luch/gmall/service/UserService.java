package com.luch.gmall.service;

import com.luch.gmall.bean.UmsMember;

import java.util.List;

/**
 * @author luch
 * @date 2019/8/14-21:15
 */
public interface UserService {
    List<UmsMember> getAllUser();

    UmsMember checkUser(UmsMember umsMember);

    void addUserTokenToCache(String token, String memberId);
}
