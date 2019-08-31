package com.luch.gmall.manage.mapper;

import com.luch.gmall.bean.PmsBaseAttrInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author luch
 * @date 2019/8/16-8:09
 */
public interface PmsBaseAttrInfoMapper extends Mapper<PmsBaseAttrInfo>{
    List<PmsBaseAttrInfo> selAttrInfoByValueId(@Param("attrValueIdStr") String attrValueIdStr);
}
