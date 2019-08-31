package com.luch.gmall.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.luch.gmall.bean.UmsMemberReceiveAddress;
import com.luch.gmall.service.UmsMemberReceiveAddressService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author luch
 * @date 2019/8/15-0:32
 */
@RestController
public class UmsMemberReceiveAddressController {

    @Reference
    UmsMemberReceiveAddressService umsMemberReceiveAddressService;

    @RequestMapping("/getAllAddr")
    public List<UmsMemberReceiveAddress> getAllAddr(){
        return umsMemberReceiveAddressService.getAllAddr();
    }
}
