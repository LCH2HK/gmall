package com.luch.gmall.util;

import com.alibaba.fastjson.JSON;
import com.luch.gmall.annotation.LoginRequired;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TransferQueue;

/**
 * @author luch
 * @date 2019/8/25-6:54
 */
@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        System.out.println("拦截器拦截到了。。。");
        // 拦截代码

        //获取注解信息
        HandlerMethod hm = (HandlerMethod) handler;
        LoginRequired loginRequiredAnnotation = hm.getMethodAnnotation(LoginRequired.class);

        if(loginRequiredAnnotation!=null){
            //有loginRequired注解，需要进行登录验证
            boolean required = loginRequiredAnnotation.required();


            //请求认证中心的服务进行身份认证，需要提供token，请求对象，请求方的ip
            // 认证结果会返回一个ifSuccess字符串对象
            //如果值为fail则验证失败，若为success则验证成功
            String ifSuccess="fail";

//            String ip = request.getHeader("x-forwarded-for");// 通过nginx转发的客户端ip
//            if(StringUtils.isBlank(ip)){
//                ip = request.getRemoteAddr();// 从request中获取ip
//                if(StringUtils.isBlank(ip)){
//                    ip = "127.0.0.1";
//                }
//            }
            String ip = "127.0.0.1";

            //token可能存在于cookie中，或者request对象中（刚登录）,或者没有
            String token="";


            String oldToken = CookieUtil.getCookieValue(request, "current_user_token", true);
            if(StringUtils.isNotBlank(oldToken)){
                token=oldToken;
            }

            String reqToken = request.getParameter("token");
            if(StringUtils.isNotBlank(reqToken)){
                token=reqToken;
            }

            Map<String,String> verficationInfo=new HashMap<>();

            if(StringUtils.isNotBlank(token)){
                String verficationJson = HttpclientUtil.doGet("http://localhost:8095/verify?token=" + token + "&currentIp=" + ip);
                verficationInfo = JSON.parseObject(verficationJson, Map.class);
                ifSuccess = verficationInfo.get("status");
            }

            if(required){
                //需要登录成功才能进行接下来的操作，比如结算，支付等

                //登录失败
                if(!ifSuccess.equals("success")){
                    //重定向会认证中心登录
                    StringBuffer requestURL = request.getRequestURL();
                    response.sendRedirect("http://localhost:8095/index?ReturnUrl="+requestURL);
                    return false;
                }

                //登录成功
                //1.在需要身份验证的请求中，将token携带的用户信息写入请求参数中
                request.setAttribute("memberId",verficationInfo.get("memberId"));
                request.setAttribute("nickname",verficationInfo.get("nickname"));

                //2.覆盖cookie中的token
                if(StringUtils.isNotBlank(token)){
                    CookieUtil.setCookie(request, response,"current_user_token",token,60*60*2, true);
                }
            }else{
                //无需登录成功，也能进行接下来的操作，比如添加物品到购物车
                //但是需要根据验证情况，决定执行的分支

                if(ifSuccess.equals("success")){
                    //需要将token携带的用户信息写入
                    request.setAttribute("memberId",verficationInfo.get("memberId"));
                    request.setAttribute("nickname",verficationInfo.get("nickname"));

                    if(StringUtils.isNotBlank(token)){
                        CookieUtil.setCookie(request, response,"current_user_token",token,60*60*2, true);
                    }
                }
            }
        }

        return true;
    }
}

