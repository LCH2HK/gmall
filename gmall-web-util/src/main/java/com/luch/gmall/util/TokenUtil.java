package com.luch.gmall.util;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author luch
 * @date 2019/8/28-9:57
 */
public class TokenUtil {
    public static String getIp(HttpServletRequest request){
        String ip = request.getHeader("x-forwarded-for");// 通过nginx转发的客户端ip
        if(StringUtils.isBlank(ip)){
            ip = request.getRemoteAddr();// 从request中获取ip
            if(StringUtils.isBlank(ip)){
                ip = "127.0.0.1";
            }
        }
        return ip;
    }
}
