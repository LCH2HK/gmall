package com.luch.gmall.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.luch.gmall.bean.PmsProductInfo;
import com.luch.gmall.service.PmsProductInfoService;
import com.luch.gmall.utils.PmsUploadUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author luch
 * @date 2019/8/16-14:58
 */
@RestController
@CrossOrigin
public class SpuController {

    @Reference
    PmsProductInfoService pmsProductInfoService;

    @RequestMapping("/spuList")
    public List<PmsProductInfo> spuList(String catalog3Id){
        return pmsProductInfoService.spuList(catalog3Id);
    }

    @RequestMapping("/saveSpuInfo")
    public String saveSpuInfo(@RequestBody PmsProductInfo pmsProductInfo){

        String status=pmsProductInfoService.saveSpuInfo(pmsProductInfo);
        return status;
    }

    @RequestMapping("/fileUpload")
    public String fileUpload(@RequestParam("file") MultipartFile multipartFile){
        String imageUrl = PmsUploadUtil.uploadImage(multipartFile);
        return imageUrl;
    }


}
