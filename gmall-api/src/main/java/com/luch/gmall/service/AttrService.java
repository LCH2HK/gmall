package com.luch.gmall.service;

import com.luch.gmall.bean.*;

import java.util.List;
import java.util.Set;

/**
 * @author luch
 * @date 2019/8/16-8:06
 */

public interface AttrService {

    List<PmsBaseAttrInfo> getAttrInfoList(String catalog3Id);

    String saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo);

    List<PmsBaseAttrValue> getAttrValueList(String attrId);

    List<PmsBaseSaleAttr> getSaleAttrList();

    public List<PmsProductSaleAttr> getSaleAttrList(String spuId);

    List<PmsProductSaleAttrValue> getSpuSaleAttrValue(String spuId, String saleAttrId);

    List<PmsBaseAttrInfo> getDistinctAttrInfo(List<PmsSearchSkuInfo> skuLsInfoList);
}
