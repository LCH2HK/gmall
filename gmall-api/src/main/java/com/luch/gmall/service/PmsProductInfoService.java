package com.luch.gmall.service;

import com.luch.gmall.bean.PmsProductInfo;

import java.util.List;

/**
 * @author luch
 * @date 2019/8/16-15:01
 */
public interface PmsProductInfoService {

    public List<PmsProductInfo> spuList(String catalog3Id);

    String saveSpuInfo(PmsProductInfo pmsProductInfo);

}
