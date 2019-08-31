package com.luch.gmall.service;

import com.luch.gmall.bean.PmsProductSaleAttr;

import java.util.List;

/**
 * @author luch
 * @date 2019/8/20-8:09
 */
public interface SpuService {

    List<PmsProductSaleAttr> getSpuSaleAttrListCheckBySku(String spuId, String skuId);

}
