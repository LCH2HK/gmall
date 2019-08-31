package com.luch.gmall.passport.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.luch.gmall.annotation.LoginRequired;
import com.luch.gmall.bean.UmsMember;
import com.luch.gmall.service.UserService;
import com.luch.gmall.util.CookieUtil;
import com.luch.gmall.util.HttpclientUtil;
import com.luch.gmall.util.JwtUtil;
import com.luch.gmall.util.TokenUtil;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author luch
 * @date 2019/8/25-6:32
 */
@Controller
public class PassportController {

    @Reference
    UserService userService;

    @RequestMapping("/index")
    public String index(String ReturnUrl, ModelMap modelMap){
        modelMap.put("ReturnUrl",ReturnUrl);
        return "index";
    }

    @RequestMapping("/login")
    @ResponseBody
    public String login(UmsMember umsMember, HttpServletRequest request, HttpServletResponse response){

        String token="";

        //查找数据库，核对信息
        UmsMember loginUser=userService.checkUser(umsMember);

        if(loginUser==null){
            //用户名或密码错误
            token="fail";
        }else{
            //核对成功，返回token信息

            //用jwt制作token
            String memberId=loginUser.getId();
            String nickname = loginUser.getNickname();
            Map<String,Object> userInfo=new HashMap<>();
            userInfo.put("memberId",memberId);
            userInfo.put("nickname",nickname);

//            String ip = request.getHeader("x-forwarded-for");//通过nginx转发的客户端ip
//            if (StringUtils.isBlank(ip)){
//                ip=request.getRemoteAddr();//若不通过nginx，则直接从request中获取ip
//                if(StringUtils.isBlank(ip)){
//                    ip="127.0.0.1";
//                }
//            }

            String ip="127.0.0.1";

            //按照设计的算法对参数进行加密后，生成token
            token= JwtUtil.encode("2019gmall",userInfo,ip);

            //将token存入redis一份
            userService.addUserTokenToCache(token,memberId);

            //将token存入cookie
            CookieUtil.setCookie(request, response,"user:"+memberId+":token",token,60*60*2, true);
            CookieUtil.setCookie(request, response,"current_user_token",token,60*60*2, true);
        }

        return token;
    }

    @RequestMapping("/vlogin")
    public String vlogin(String code,HttpServletRequest request){

        String url="https://api.weibo.com/oauth2/access_token?";

        String client_id="3806584367";
        String client_secret="d335bf5e4fc32b0eb0263e6caf38fea0";
        String grant_type="authorization_code";
        String redirect_uri="http://passport.gmall.com:8095/vlogin";

        Map<String,String> paramMap=new HashMap<>();
        paramMap.put("client_id",client_id);
        paramMap.put("client_secret",client_secret);
        paramMap.put("grant_type",grant_type);
        paramMap.put("redirect_uri",redirect_uri);
        //授权有效期内可以使用，没新生成一次授权码，说明用户对第三方数据进行重启授权，之前的access_token和授权码全部过期
        paramMap.put("code",code);

        //{"access_token":"2.006j2T1HDUCcJEcc39adf871Dbl9ED","remind_in":"157679999","expires_in":157679999,"uid":"6715451595","isRealName":"true"}
        String access_token_Json= HttpclientUtil.doPost(url,paramMap);

        // access_token换取用户信息
        Map<String,String> access_map = JSON.parseObject(access_token_Json, Map.class);
        String uid = access_map.get("uid");
        String access_token = access_map.get("access_token");

        String getUserInfoUrl="https://api.weibo.com/2/users/show.json?access_token="+access_token+"&uid="+uid;
        //{"id":6715451595,"idstr":"6715451595","class":1,"screen_name":"用户6715451595","name":"用户6715451595","province":"33","city":"1","location":"浙江 杭州","description":"","url":"","profile_image_url":"https://tvax1.sinaimg.cn/default/images/default_avatar_female_50.gif?KID=imgbed,tva&Expires=1566965513&ssig=U6yEWc5jiq","cover_image_phone":"http://ww1.sinaimg.cn/crop.0.0.640.640.640/549d0121tw1egm1kjly3jj20hs0hsq4f.jpg","profile_url":"u/6715451595","domain":"","weihao":"","gender":"f","followers_count":1,"friends_count":9,"pagefriends_count":2,"statuses_count":0,"video_status_count":0,"favourites_count":0,"created_at":"Fri Sep 21 16:13:14 +0800 2018","following":false,"allow_all_act_msg":false,"geo_enabled":true,"verified":false,"verified_type":-1,"remark":"","insecurity":{"sexual_content":false},"ptype":0,"allow_all_comment":true,"avatar_large":"https://tvax1.sinaimg.cn/default/images/default_avatar_female_180.gif?KID=imgbed,tva&Expires=1566965513&ssig=E20jZhZ2x%2F","avatar_hd":"https://tvax1.sinaimg.cn/default/images/default_avatar_female_180.gif?KID=imgbed,tva&Expires=1566965513&ssig=E20jZhZ2x%2F","verified_reason":"","verified_trade":"","verified_reason_url":"","verified_source":"","verified_source_url":"","follow_me":false,"like":false,"like_me":false,"online_status":0,"bi_followers_count":0,"lang":"zh-cn","star":0,"mbtype":0,"mbrank":0,"block_word":0,"block_app":0,"credit_score":80,"user_ability":2097152,"urank":4,"story_read_state":-1,"vclub_member":0,"is_teenager":0,"is_guardian":0,"is_teenager_list":0}
        String userInfoJson = HttpclientUtil.doGet(getUserInfoUrl);
        Map<String,String> userInfo = JSON.parseObject(userInfoJson, Map.class);

        //检查数据库是否存在使用该微博登录的用户，没有则添加用户，有则更新用户信息
        String sourceUid=userInfo.get("idStr");
        UmsMember umsMemberParam = new UmsMember();
        umsMemberParam.setSourceUid(sourceUid);
        UmsMember OauthUser = userService.checkUser(umsMemberParam);
        UmsMember umsMember =null;
        if(OauthUser!=null){
            umsMember=OauthUser;
        }else{
            umsMember = new UmsMember();
        }

        //将用户信息保存数据库，用户类型设置为微博用户
        umsMember.setSourceType("2");
        umsMember.setAccessCode(code);
        umsMember.setAccessToken(access_token);
        umsMember.setSourceUid(userInfo.get("idStr"));
        umsMember.setCity(userInfo.get("location"));
        umsMember.setNickname(userInfo.get("screen_name"));

        String sex="0";
        String gender = userInfo.get("gender");
        if(gender.equals("m")){
            sex="1";
        }
        umsMember.setGender(sex);

        // 生成jwt的token，并且重定向到首页，携带该token
        String ip= TokenUtil.getIp(request);
        String token="";
        String memberId = umsMember.getId();
        String nickname = umsMember.getNickname();
        Map<String,Object> userMap=new HashMap<>();
        userMap.put("memberId",memberId);
        userMap.put("nickname",nickname);
        token = JwtUtil.encode("2019gmall", userMap, ip);

        //将token存入redis一份
        userService.addUserTokenToCache(token,memberId);

        return "redirect:http://search.gmall.com:8093/index?token="+token;
    }


    @RequestMapping("/verify")
    @ResponseBody
    public String verify(String token,String currentIp){
        //通过jwt校验token真假
        Map<String,String> verificationInfo=new HashMap<>();
        Map<String, Object> decode = JwtUtil.decode(token, "2019gmall", currentIp);

        if(decode!=null){
            verificationInfo.put("status","success");
            verificationInfo.put("memberId",(String)decode.get("memberId"));
            verificationInfo.put("nickname",(String)decode.get("nickname"));
        }else{
            verificationInfo.put("status","fail");
        }

        return JSON.toJSONString(verificationInfo);


    }

}
