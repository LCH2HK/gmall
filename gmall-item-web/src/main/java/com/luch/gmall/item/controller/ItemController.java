package com.luch.gmall.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.luch.gmall.annotation.LoginRequired;
import com.luch.gmall.bean.PmsProductSaleAttr;
import com.luch.gmall.bean.PmsSkuInfo;
import com.luch.gmall.bean.PmsSkuSaleAttrValue;
import com.luch.gmall.service.SkuService;
import com.luch.gmall.service.SpuService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

/**
 * @author luch
 * @date 2019/8/18-15:23
 */

@Controller
@CrossOrigin
public class ItemController {

    @Reference
    SkuService skuService;

    @Reference
    SpuService spuService;

    @RequestMapping("{skuId}.html")
    @LoginRequired(required = false)
    public String index(@PathVariable String skuId,ModelMap modelMap){

        PmsSkuInfo skuInfo = skuService.getSkuInfo(skuId);

        String spuId=skuInfo.getProductId();

        List<PmsProductSaleAttr> spuSaleAttrListCheckBySku = spuService.getSpuSaleAttrListCheckBySku(spuId, skuId);

        modelMap.put("spuSaleAttrListCheckBySku",spuSaleAttrListCheckBySku);
        modelMap.put("skuInfo",skuInfo);

        //查询同一个SpuId下的各个Sku
        List<PmsSkuInfo> pmsSkuInfos= skuService.getSkuSaleAttrValueListBySpu(spuId);
        HashMap<String, String> skuSaleAttrValMap = new HashMap<>();
        for (PmsSkuInfo pmsSkuInfo : pmsSkuInfos) {
            String val=pmsSkuInfo.getId();
            String key="";
            List<PmsSkuSaleAttrValue> skuSaleAttrValueList = pmsSkuInfo.getSkuSaleAttrValueList();
            for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuSaleAttrValueList) {
                key+=pmsSkuSaleAttrValue.getSaleAttrValueId()+"|";
            }
            skuSaleAttrValMap.put(key,val);
        }

        String jsonString = JSON.toJSONString(skuSaleAttrValMap);
        modelMap.put("jsonString",jsonString);

        return "item";
    }
}
