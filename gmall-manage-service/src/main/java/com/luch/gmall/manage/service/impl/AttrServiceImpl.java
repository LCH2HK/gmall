package com.luch.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.luch.gmall.bean.*;
import com.luch.gmall.manage.mapper.*;
import com.luch.gmall.service.AttrService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author luch
 * @date 2019/8/16-8:07
 */
@Service
public class AttrServiceImpl implements AttrService {

    @Autowired
    PmsBaseAttrInfoMapper pmsBaseAttrInfoMapper;

    @Autowired
    PmsBaseAttrValueMapper pmsBaseAttrValueMapper;

    @Autowired
    PmsBaseSaleAttrMapper pmsBaseSaleAttrMapper;

    @Autowired
    PmsProductSaleAttrMapper pmsProductSaleAttrMapper;

    @Autowired
    PmsProductSaleAttrValueMapper pmsProductSaleAttrValueMapper;

    @Override
    public List<PmsBaseAttrInfo> getAttrInfoList(String catalog3Id) {

        PmsBaseAttrInfo pmsBaseAttrInfo=new PmsBaseAttrInfo();
        pmsBaseAttrInfo.setCatalog3Id(catalog3Id);

        return pmsBaseAttrInfoMapper.select(pmsBaseAttrInfo);
    }

    @Override
    public String saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo) {

        String id = pmsBaseAttrInfo.getId();


        if(StringUtils.isBlank(id)){//新增
            pmsBaseAttrInfoMapper.insertSelective(pmsBaseAttrInfo);
            List<PmsBaseAttrValue> attrValueList = pmsBaseAttrInfo.getAttrValueList();
            for (PmsBaseAttrValue pmsBaseAttrValue : attrValueList) {
                pmsBaseAttrValue.setAttrId(pmsBaseAttrInfo.getId());
                pmsBaseAttrValueMapper.insertSelective(pmsBaseAttrValue);
            }
        }else{//修改

            //属性修改
            Example example=new Example(PmsBaseAttrInfo.class);
            example.createCriteria().andEqualTo("id",id);
            pmsBaseAttrInfoMapper.updateByExampleSelective(pmsBaseAttrInfo,example);

            //属性值修改
            PmsBaseAttrValue pmsBaseAttrValue=new PmsBaseAttrValue();
            pmsBaseAttrValue.setAttrId(id);
            pmsBaseAttrValueMapper.delete(pmsBaseAttrValue);
            List<PmsBaseAttrValue> attrValueList = pmsBaseAttrInfo.getAttrValueList();
            for (PmsBaseAttrValue BaseAttrValue : attrValueList) {
                pmsBaseAttrValueMapper.insertSelective(BaseAttrValue);
            }
        }


        return "success";
    }

    @Override
    public List<PmsBaseAttrValue> getAttrValueList(String attrId) {

        PmsBaseAttrValue pmsBaseAttrValue = new PmsBaseAttrValue();
        pmsBaseAttrValue.setAttrId(attrId);
        List<PmsBaseAttrValue> pmsBaseAttrValues = pmsBaseAttrValueMapper.select(pmsBaseAttrValue);
        return pmsBaseAttrValues;
    }

    @Override
    public List<PmsBaseSaleAttr> getSaleAttrList() {
        return pmsBaseSaleAttrMapper.selectAll();
    }

    public List<PmsProductSaleAttr> getSaleAttrList(String spuId) {

        PmsProductSaleAttr pmsProductSaleAttr = new PmsProductSaleAttr();
        pmsProductSaleAttr.setProductId(spuId);
        return pmsProductSaleAttrMapper.select(pmsProductSaleAttr);
    }

    @Override
    public List<PmsProductSaleAttrValue> getSpuSaleAttrValue(String spuId, String saleAttrId) {

        List<PmsProductSaleAttr> saleAttrList = getSaleAttrList(spuId);

        PmsProductSaleAttrValue pmsProductSaleAttrValue=new PmsProductSaleAttrValue();
        pmsProductSaleAttrValue.setSaleAttrId(saleAttrId);
        pmsProductSaleAttrValue.setProductId(spuId);
        List<PmsProductSaleAttrValue> pmsProductSaleAttrValueList = pmsProductSaleAttrValueMapper.select(pmsProductSaleAttrValue);

        return pmsProductSaleAttrValueList;
    }

    @Override
    public List<PmsBaseAttrInfo> getDistinctAttrInfo(List<PmsSearchSkuInfo> skuLsInfoList) {


        Set<String> attrValueIdSet=new HashSet<>();

        for (PmsSearchSkuInfo pmsSearchSkuInfo : skuLsInfoList) {
            List<PmsSkuAttrValue> skuAttrValueList = pmsSearchSkuInfo.getSkuAttrValueList();
            for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
                attrValueIdSet.add(pmsSkuAttrValue.getValueId());
            }
        }

        String attrValueIdStr=StringUtils.join(attrValueIdSet,",");

        List<PmsBaseAttrInfo> pmsBaseAttrInfoList=pmsBaseAttrInfoMapper.selAttrInfoByValueId(attrValueIdStr);

        System.out.println();

        return pmsBaseAttrInfoList;

    }


}
