package com.luch.gmall.service;

import com.luch.gmall.bean.PmsProductImage;
import com.luch.gmall.bean.PmsSkuInfo;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author luch
 * @date 2019/8/18-8:50
 */
public interface SkuService {
     List<PmsProductImage> getSpuImageList(String spuId);

     String saveSkuInfo(PmsSkuInfo pmsSkuInfo);

     PmsSkuInfo getSkuInfo(String skuId);

    List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(String spuId);

    List<PmsSkuInfo> getAllSku(String catalog3Id);

    boolean checkPrice(String productSkuId, BigDecimal price);
}
