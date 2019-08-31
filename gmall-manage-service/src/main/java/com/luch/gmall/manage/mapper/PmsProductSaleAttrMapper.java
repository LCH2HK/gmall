package com.luch.gmall.manage.mapper;

import com.luch.gmall.bean.PmsProductSaleAttr;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author luch
 * @date 2019/8/18-6:53
 */
public interface PmsProductSaleAttrMapper extends Mapper<PmsProductSaleAttr>{

    public List<PmsProductSaleAttr> selectSpuSaleAttrListCheckBySku(@Param("spuId") String spuId, @Param("skuId") String skuId);
}
