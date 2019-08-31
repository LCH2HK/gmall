package com.luch.gmall.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.luch.gmall.annotation.LoginRequired;
import com.luch.gmall.bean.OmsCartItem;
import com.luch.gmall.bean.PmsSkuInfo;
import com.luch.gmall.service.CartService;
import com.luch.gmall.service.CatalogService;
import com.luch.gmall.service.SkuService;
import com.luch.gmall.util.CookieUtil;
import com.sun.org.apache.bcel.internal.generic.MONITORENTER;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author luch
 * @date 2019/8/23-10:25
 */
@Controller
public class CartController {

    @Reference
    SkuService skuService;

    @Reference
    CartService cartService;

    @RequestMapping("/addToCart")
    @LoginRequired(required = false)
    public String addToCart(String skuId, int quantity, HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap modelMap){

        //调用商品服务查询商品信息
        PmsSkuInfo skuInfo = skuService.getSkuInfo(skuId);

        //将商品信息封装成购物车信息
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setProductSkuId(skuId);
        omsCartItem.setCreateDate(new Date());
        omsCartItem.setPrice(skuInfo.getPrice());
        omsCartItem.setProductId(skuInfo.getProductId());
        omsCartItem.setProductName(skuInfo.getSkuName());
        omsCartItem.setQuantity(new BigDecimal(quantity));
        omsCartItem.setIsChecked("1");

        BigDecimal totalPrice = omsCartItem.getQuantity().multiply(omsCartItem.getPrice());
        omsCartItem.setTotalPrice(totalPrice);

        omsCartItem.setProductPic(skuInfo.getSkuDefaultImg());
        omsCartItem.setProductCategoryId(skuInfo.getCatalog3Id());

        //判断用户是否登录
        String memberId=(String)request.getAttribute("memberId");

        //如果用户未登录，将购物车信息保存在cookie中，购物车信息在cookie中的保存形式为List<OmsCartItem>的JSON字符串
        if(StringUtils.isBlank(memberId)){
            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
            //如果cookie为空，则新增cookie
            if(StringUtils.isBlank(cartListCookie)){
                List<OmsCartItem> cartList=new ArrayList<>();
                cartList.add(omsCartItem);
                cartListCookie= JSON.toJSONString(cartList);
            }else{//cookie不为空，则添加
                List<OmsCartItem> omsCartItems = JSON.parseArray(cartListCookie, OmsCartItem.class);
                boolean exist=false;
                for (OmsCartItem cartItem : omsCartItems) {
                    if(cartItem.getProductSkuId().equals(omsCartItem.getProductSkuId())){
                        BigDecimal total = cartItem.getQuantity().add(new BigDecimal(quantity));
                        BigDecimal totalp = cartItem.getPrice().multiply(total);
                        cartItem.setQuantity(total);
                        cartItem.setTotalPrice(totalp);
                        exist=true;
                    }
                }
                if(!exist){
                    omsCartItems.add(omsCartItem);
                }
                cartListCookie = JSON.toJSONString(omsCartItems);
            }
            CookieUtil.setCookie(request,response,"cartListCookie",cartListCookie,60*60*72,true);
        }else{
            //如果用户已登录，将购物车信息保存在db和cache中
            OmsCartItem cartItem=cartService.getCartItem(memberId,skuId);

            if(cartItem==null){//当前用户没有采购过该商品
                //保存进db
                omsCartItem.setMemberId(memberId);
                cartService.addCart(omsCartItem);
            }else{//当前用户采购过该商品
                BigDecimal total= cartItem.getQuantity().add(new BigDecimal(quantity));
                BigDecimal totalp = cartItem.getPrice().multiply(total);
                cartItem.setQuantity(total);
                cartItem.setTotalPrice(totalp);
                cartService.updateCart(cartItem);
            }
//            将缓存信息更新
//            缓存为hash结构，mapKey为"user:" + userId + ":cart"，mapValue为键为skuId，值为cartItem的Map
            cartService.updateCache(memberId);
        }

        modelMap.put("skuInfo",skuInfo);

        return "redirect:/success.html";
    }

    @RequestMapping("/cartList")
    @LoginRequired(required = false)
    public String cartList(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap modelMap){


        String memberId=(String) request.getAttribute("memberId");
        List<OmsCartItem> cartList=new ArrayList<>();

        if(StringUtils.isBlank(memberId)){
            //未登录，查cookie
            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
            if(StringUtils.isNotBlank(cartListCookie)){
                cartList = JSON.parseArray(cartListCookie, OmsCartItem.class);
            }
        }else{
            //已登录，查cache
            cartList=cartService.getCartList(memberId);
        }

        BigDecimal totalAmount=getTotalAmount(cartList);
        modelMap.put("totalAmount",totalAmount);

        modelMap.put("cartList",cartList);

        return "cartList";
    }

    @RequestMapping("/checkCart")
    @LoginRequired
    public String checkCart(String isChecked,String skuId,HttpServletRequest request,HttpServletResponse response,HttpSession session,ModelMap modelMap){

        String memberId=request.getParameter("memberId");

        //修改数据库中对应商品的选中状态
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setMemberId(memberId);
        omsCartItem.setIsChecked(isChecked);
        omsCartItem.setProductSkuId(skuId);
        cartService.checkCart(omsCartItem);

        //将最新的数据从缓存中查出，渲染给内嵌页
        List<OmsCartItem> cartList = cartService.getCartList(memberId);
        modelMap.put("cartList",cartList);

        BigDecimal totalAmount=getTotalAmount(cartList);
        modelMap.put("totalAmount",totalAmount);

        return "cartListInner";
    }

    private BigDecimal getTotalAmount(List<OmsCartItem> cartList) {
        BigDecimal totalAmount = new BigDecimal(0);
        for (OmsCartItem omsCartItem : cartList) {
            BigDecimal totalPrice = omsCartItem.getTotalPrice();
            String isChecked = omsCartItem.getIsChecked();
            if(isChecked.equals("1")){
                totalAmount=totalAmount.add(totalPrice);
            }
        }

        return totalAmount;
    }

}
