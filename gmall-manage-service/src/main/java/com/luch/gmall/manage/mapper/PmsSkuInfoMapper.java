package com.luch.gmall.manage.mapper;

import com.luch.gmall.bean.PmsSkuInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author luch
 * @date 2019/8/18-10:26
 */
public interface PmsSkuInfoMapper extends Mapper<PmsSkuInfo>{

    List<PmsSkuInfo> selSkuSaleAttrValueListBySpu(@Param("spuId") String spuId);
}
