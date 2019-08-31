package com.luch.gmall.service;

import com.luch.gmall.bean.PmsSearchParam;
import com.luch.gmall.bean.PmsSearchSkuInfo;

import java.util.List;

/**
 * @author luch
 * @date 2019/8/22-14:51
 */
public interface SearchService {
    List<PmsSearchSkuInfo> getSearchSkuInfos(PmsSearchParam pmsSearchParam);
}
