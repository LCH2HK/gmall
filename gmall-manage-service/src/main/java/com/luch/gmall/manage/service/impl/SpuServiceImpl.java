package com.luch.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.luch.gmall.bean.PmsProductSaleAttr;
import com.luch.gmall.manage.mapper.PmsProductSaleAttrMapper;
import com.luch.gmall.service.SpuService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author luch
 * @date 2019/8/20-8:10
 */
@Service
public class SpuServiceImpl implements SpuService {

    @Autowired
    PmsProductSaleAttrMapper pmsProductSaleAttrMapper;

    @Override
    public List<PmsProductSaleAttr> getSpuSaleAttrListCheckBySku(String spuId, String skuId) {
        return pmsProductSaleAttrMapper.selectSpuSaleAttrListCheckBySku(spuId,skuId);
    }
}
