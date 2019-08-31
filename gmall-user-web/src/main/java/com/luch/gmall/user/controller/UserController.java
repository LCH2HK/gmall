package com.luch.gmall.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.luch.gmall.bean.UmsMember;
import com.luch.gmall.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author luch
 * @date 2019/8/14-21:15
 */
@RestController
public class UserController {

    @Reference
    UserService userService;


    @RequestMapping("/hello")
    public String hello(){
        return "hello world";
    }

    @RequestMapping("/getAllUser")
    public List<UmsMember> getAllUser(){
        return userService.getAllUser();
    }
}
