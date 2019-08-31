package com.luch.gmall.user.service.Impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.luch.gmall.bean.UmsMemberReceiveAddress;
import com.luch.gmall.service.UmsMemberReceiveAddressService;
import com.luch.gmall.user.mapper.UmsMemberReceiveAddressMapper;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.List;

/**
 * @author luch
 * @date 2019/8/15-0:28
 */
@Service
public class UmsMemberReceiveAddressServiceImpl implements UmsMemberReceiveAddressService {

    @Autowired
    UmsMemberReceiveAddressMapper umsMemberReceiveAddressMapper;

    @Override
    public List<UmsMemberReceiveAddress> getAllAddr() {
        return umsMemberReceiveAddressMapper.selectAll();
    }

    @Override
    public List<UmsMemberReceiveAddress> getAddrByUserId(String memberId) {
        UmsMemberReceiveAddress umsMemberReceiveAddress = new UmsMemberReceiveAddress();
        umsMemberReceiveAddress.setMemberId(memberId);
        List<UmsMemberReceiveAddress> select = umsMemberReceiveAddressMapper.select(umsMemberReceiveAddress);
        return select;
    }

    @Override
    public UmsMemberReceiveAddress getAddrById(String receiveAddressId) {
        UmsMemberReceiveAddress umsMemberReceiveAddress = new UmsMemberReceiveAddress();
        umsMemberReceiveAddress.setId(receiveAddressId);
        UmsMemberReceiveAddress umsMemberReceiveAddress1 = umsMemberReceiveAddressMapper.selectOne(umsMemberReceiveAddress);
        return umsMemberReceiveAddress1;
    }
}
