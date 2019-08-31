package com.luch.gmall.user.mapper;

import com.luch.gmall.bean.UmsMember;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author luch
 * @date 2019/8/14-21:15
 */
public interface UserMapper extends Mapper<UmsMember>{
    List<UmsMember> selAllUser();
}
