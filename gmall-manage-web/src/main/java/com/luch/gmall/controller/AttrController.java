package com.luch.gmall.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.luch.gmall.bean.*;
import com.luch.gmall.service.AttrService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author luch
 * @date 2019/8/16-8:02
 */
@RestController
@CrossOrigin
public class AttrController {
    @Reference
    AttrService attrService;

    @RequestMapping("/spuSaleAttrList")
    public List<PmsProductSaleAttr> getSpuSaleAttrList(String spuId){
        List<PmsProductSaleAttr> saleAttrList = attrService.getSaleAttrList(spuId);
        for (PmsProductSaleAttr pmsProductSaleAttr : saleAttrList) {
            List<PmsProductSaleAttrValue> pmsProductSaleAttrValueList= attrService.getSpuSaleAttrValue(spuId,pmsProductSaleAttr.getSaleAttrId());
            pmsProductSaleAttr.setSpuSaleAttrValueList(pmsProductSaleAttrValueList);
        }
        return saleAttrList;
    }


    @RequestMapping("/attrInfoList")
    public List<PmsBaseAttrInfo> getAttrInfoList(String catalog3Id){

        List<PmsBaseAttrInfo> attrInfoList = attrService.getAttrInfoList(catalog3Id);
        for (PmsBaseAttrInfo pmsBaseAttrInfo : attrInfoList) {
            pmsBaseAttrInfo.setAttrValueList(attrService.getAttrValueList(pmsBaseAttrInfo.getId()));
        }
        return attrInfoList;
    }

    @RequestMapping("/saveAttrInfo")
    public String saveAttrInfo(@RequestBody PmsBaseAttrInfo pmsBaseAttrInfo){

        return attrService.saveAttrInfo(pmsBaseAttrInfo);
    }

    @RequestMapping("/getAttrValueList")
    public List<PmsBaseAttrValue> getAttrValueList(String attrId){
        return attrService.getAttrValueList(attrId);
    }

    @RequestMapping("/baseSaleAttrList")
    public List<PmsBaseSaleAttr> getSaleAttrList(){
        return attrService.getSaleAttrList();
    }
}
