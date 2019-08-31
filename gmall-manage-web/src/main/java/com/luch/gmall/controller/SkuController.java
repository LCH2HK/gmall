package com.luch.gmall.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.luch.gmall.bean.PmsProductImage;
import com.luch.gmall.bean.PmsSkuInfo;
import com.luch.gmall.service.SkuService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author luch
 * @date 2019/8/18-8:49
 */
@RestController
@CrossOrigin
public class SkuController {

    @Reference
    SkuService skuService;

    @RequestMapping("/spuImageList")
    public List<PmsProductImage> getSpuImageList(String spuId){
        return skuService.getSpuImageList(spuId);
    }

    @RequestMapping("/saveSkuInfo")
    public String saveSkuInfo(@RequestBody PmsSkuInfo pmsSkuInfo){
        skuService.saveSkuInfo(pmsSkuInfo);
        return "success";
    }
}
