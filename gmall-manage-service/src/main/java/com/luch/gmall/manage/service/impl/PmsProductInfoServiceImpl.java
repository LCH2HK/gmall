package com.luch.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.luch.gmall.bean.PmsProductImage;
import com.luch.gmall.bean.PmsProductInfo;
import com.luch.gmall.bean.PmsProductSaleAttr;
import com.luch.gmall.bean.PmsProductSaleAttrValue;
import com.luch.gmall.manage.mapper.PmsProductImageMapper;
import com.luch.gmall.manage.mapper.PmsProductInfoMapper;
import com.luch.gmall.manage.mapper.PmsProductSaleAttrMapper;
import com.luch.gmall.manage.mapper.PmsProductSaleAttrValueMapper;
import com.luch.gmall.service.PmsProductInfoService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author luch
 * @date 2019/8/16-15:02
 */
@Service
public class PmsProductInfoServiceImpl implements PmsProductInfoService {

    @Autowired
    PmsProductInfoMapper pmsProductInfoMapper;

    @Autowired
    PmsProductImageMapper pmsProductImageMapper;

    @Autowired
    PmsProductSaleAttrMapper pmsProductSaleAttrMapper;

    @Autowired
    PmsProductSaleAttrValueMapper pmsProductSaleAttrValueMapper;


    @Override
    public List<PmsProductInfo> spuList(String catalog3Id) {
        PmsProductInfo pmsProductInfo = new PmsProductInfo();
        pmsProductInfo.setCatalog3Id(catalog3Id);
        return pmsProductInfoMapper.select(pmsProductInfo);
    }

    @Override
    public String saveSpuInfo(PmsProductInfo pmsProductInfo) {

        int rows = pmsProductInfoMapper.insertSelective(pmsProductInfo);
        String productId = pmsProductInfo.getId();

        List<PmsProductSaleAttr> spuSaleAttrList = pmsProductInfo.getSpuSaleAttrList();
        for (PmsProductSaleAttr pmsProductSaleAttr : spuSaleAttrList) {
            pmsProductSaleAttr.setProductId(productId);
            pmsProductSaleAttrMapper.insert(pmsProductSaleAttr);

            List<PmsProductSaleAttrValue> spuSaleAttrValueList = pmsProductSaleAttr.getSpuSaleAttrValueList();
            for (PmsProductSaleAttrValue pmsProductSaleAttrValue : spuSaleAttrValueList) {
                pmsProductSaleAttrValue.setProductId(productId);
                pmsProductSaleAttrValueMapper.insert(pmsProductSaleAttrValue);
            }
        }

        List<PmsProductImage> spuImageList = pmsProductInfo.getSpuImageList();
        for (PmsProductImage pmsProductImage : spuImageList) {
            pmsProductImage.setProductId(productId);
            pmsProductImageMapper.insertSelective(pmsProductImage);
        }
        return rows==0?"fail":"success";
    }
}
