package com.luch.gmall.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.luch.gmall.bean.*;
import com.luch.gmall.service.AttrService;
import com.luch.gmall.service.SearchService;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

/**
 * @author luch
 * @date 2019/8/22-10:34
 */
@Controller
public class SearchController {

    @Reference
    SearchService searchService;

    @Reference
    AttrService attrService;

    @RequestMapping("list.html")
    public String list(PmsSearchParam pmsSearchParam, ModelMap modelMap){

        String keyword=pmsSearchParam.getKeyword();

        List<PmsSearchSkuInfo> skuLsInfoList=searchService.getSearchSkuInfos(pmsSearchParam);

        List<PmsBaseAttrInfo> attrList=new ArrayList<>();

        attrList= attrService.getDistinctAttrInfo(skuLsInfoList);
        
        //对平台属性集合进一步处理，去掉当前条件中valueId所在的属性组
        String[] delValueIds = pmsSearchParam.getValueId();
        if(delValueIds!=null){
            List<PmsSearchCrumb> pmsSearchCrumbs=new ArrayList<>();
            for (String delValueId : delValueIds) {

                PmsSearchCrumb pmsSearchCrumb = new PmsSearchCrumb();
                pmsSearchCrumb.setValueId(delValueId);
                pmsSearchCrumb.setUrlParam(getUrlParamForCrumb(pmsSearchParam,delValueId));

                Iterator<PmsBaseAttrInfo> iterator = attrList.iterator();
                while (iterator.hasNext()){
                    PmsBaseAttrInfo pmsBaseAttrInfo = iterator.next();
                    List<PmsBaseAttrValue> attrValueList = pmsBaseAttrInfo.getAttrValueList();
                    for (PmsBaseAttrValue pmsBaseAttrValue : attrValueList) {
                        String valueId = pmsBaseAttrValue.getId();
                        if(delValueId.equals(valueId)){
                            pmsSearchCrumb.setValueName(pmsBaseAttrValue.getValueName());
                            iterator.remove();
                        }
                    }
                }

                pmsSearchCrumbs.add(pmsSearchCrumb);
            }
            modelMap.put("attrValueSelectedList",pmsSearchCrumbs);
        }


        String urlParam = getUrlParam(pmsSearchParam);

        modelMap.put("urlParam",urlParam);

        modelMap.put("skuLsInfoList",skuLsInfoList);

        modelMap.put("attrList",attrList);

        modelMap.put("keyword",keyword);

        return "list";
    }

    @RequestMapping("index.html")
    public String list(){
        return "index";
    }

    public String getUrlParam(PmsSearchParam pmsSearchParam){
        String keyword = pmsSearchParam.getKeyword();
        String catalog3Id = pmsSearchParam.getCatalog3Id();
        String[] skuAttrValueList = pmsSearchParam.getValueId();

        String urlParam="";

        if(StringUtils.isNotBlank(keyword)){
            urlParam=urlParam+"keyword="+keyword;
        }

        if(StringUtils.isNotBlank(catalog3Id)){
            if(StringUtils.isNotBlank(urlParam)){
                urlParam+="&";
            }
            urlParam=urlParam+"catalog3Id="+catalog3Id;
        }

        if(skuAttrValueList!=null){
            if(StringUtils.isNotBlank(urlParam)){
                urlParam+="&";
            }
            for (String valueId : skuAttrValueList) {
                urlParam=urlParam+"valueId="+valueId;
            }
        }

        return urlParam;
    }

    public String getUrlParamForCrumb(PmsSearchParam pmsSearchParam,String delValueId){
        String keyword = pmsSearchParam.getKeyword();
        String catalog3Id = pmsSearchParam.getCatalog3Id();
        String[] skuAttrValueList = pmsSearchParam.getValueId();

        String urlParam="";

        if(StringUtils.isNotBlank(keyword)){
            urlParam=urlParam+"keyword="+keyword;
        }

        if(StringUtils.isNotBlank(catalog3Id)){
            if(StringUtils.isNotBlank(urlParam)){
                urlParam+="&";
            }
            urlParam=urlParam+"catalog3Id="+catalog3Id;
        }

        if(skuAttrValueList!=null){
            if(StringUtils.isNotBlank(urlParam)){
                urlParam+="&";
            }
            for (String valueId : skuAttrValueList) {
                if(!valueId.equals(delValueId)){
                    urlParam=urlParam+"valueId="+valueId;
                }                
            }
        }

        return urlParam;
    }

    @RequestMapping("/index")
    public String index(){
        return "index";
    }
}
