package com.luch.gmall.service;

import com.luch.gmall.bean.UmsMemberReceiveAddress;

import java.util.List;

/**
 * @author luch
 * @date 2019/8/15-0:27
 */
public interface UmsMemberReceiveAddressService {
    public List<UmsMemberReceiveAddress> getAllAddr();

    List<UmsMemberReceiveAddress> getAddrByUserId(String memberId);


    UmsMemberReceiveAddress getAddrById(String receiveAddressId);
}
