package com.luch.gmall.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.luch.gmall.annotation.LoginRequired;
import com.luch.gmall.bean.OmsCartItem;
import com.luch.gmall.bean.OmsOrder;
import com.luch.gmall.bean.OmsOrderItem;
import com.luch.gmall.bean.UmsMemberReceiveAddress;
import com.luch.gmall.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author luch
 * @date 2019/8/28-19:24
 */
@Controller
public class OrderController {

    @Reference
    CartService cartService;

    @Reference
    OrderService orderService;

    @Reference
    UmsMemberReceiveAddressService umsMemberReceiveAddressService;

    @Reference
    SkuService skuService;



    @RequestMapping("/toTrade")
    @LoginRequired
    public String toTrade(ModelMap modelMap, HttpServletRequest request){

        String memberId = (String) request.getAttribute("memberId");
        String nickname = (String) request.getAttribute("nickname");

        UmsMemberReceiveAddress umsMemberReceiveAddress = new UmsMemberReceiveAddress();
        umsMemberReceiveAddress.setMemberId(memberId);
        List<UmsMemberReceiveAddress> userAddressList = umsMemberReceiveAddressService.getAddrByUserId(memberId);

        //将购物车集合转化为页面计算清单集合
        List<OmsOrderItem> omsOrderItems=new ArrayList<>();
        List<OmsCartItem> cartList = cartService.getCartList(memberId);
        for (OmsCartItem omsCartItem : cartList) {
            OmsOrderItem omsOrderItem = new OmsOrderItem();
            omsOrderItem.setProductName(omsCartItem.getProductName());
            omsOrderItem.setProductPic(omsCartItem.getProductPic());
            omsOrderItem.setProductQuantity(omsCartItem.getQuantity());
            omsOrderItems.add(omsOrderItem);
        }



        modelMap.put("nickname",nickname);
        modelMap.put("userAddressList",userAddressList);
        modelMap.put("omsOrderItems",omsOrderItems);
        modelMap.put("totalAmount",getTotalAmount(cartList));

        String tradeCode=orderService.generateTradeCode(memberId);
        modelMap.put("tradeCode",tradeCode);
        return "trade";
    }

    @RequestMapping("/submitOrder")
    @LoginRequired
    public ModelAndView submitOrder(String receiveAddressId, BigDecimal totalAmount, String tradeCode, HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap modelMap){

        String memberId = (String) request.getAttribute("memberId");
        String nickname = (String) request.getAttribute("nickname");

        ModelAndView mv=new ModelAndView();

        //检查交易码
        String success=orderService.checkTradeCode(memberId,tradeCode);

        if(success.equals("success")){
            List<OmsOrderItem> omsOrderItems=new ArrayList<>();
            //1、生成订单对象
            OmsOrder omsOrder = new OmsOrder();
            omsOrder.setAutoConfirmDay(7);
            omsOrder.setCreateTime(new Date());
            omsOrder.setDiscountAmount(null);
            omsOrder.setMemberId(memberId);
            omsOrder.setMemberUsername(nickname);
            omsOrder.setNote("快点发货");

            String outTradeNo="gmall"+System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-DD-HH:mm:ss");
            outTradeNo+=sdf.format(new Date());

            omsOrder.setOrderSn(outTradeNo);//外部订单号
            omsOrder.setPayAmount(totalAmount);
            omsOrder.setOrderType(1);

            UmsMemberReceiveAddress receiveAddress=umsMemberReceiveAddressService.getAddrById(receiveAddressId);
            omsOrder.setReceiverCity(receiveAddress.getCity());
            omsOrder.setReceiverDetailAddress(receiveAddress.getDetailAddress());
            omsOrder.setReceiverName(receiveAddress.getName());
            omsOrder.setReceiverPhone(receiveAddress.getPhoneNumber());
            omsOrder.setReceiverPostCode(receiveAddress.getPostCode());
            omsOrder.setReceiverProvince(receiveAddress.getProvince());
            omsOrder.setReceiverRegion(receiveAddress.getRegion());

            // 当前日期加一天，一天后配送
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE,1);
            Date time = calendar.getTime();
            omsOrder.setReceiveTime(time);

            omsOrder.setSourceType(0);
            omsOrder.setStatus("0");
            omsOrder.setOrderType(0);
            omsOrder.setTotalAmount(totalAmount);

            //生成OmsOrderItems属性
            List<OmsCartItem> omsCartItems = cartService.getCartList(memberId);
            for (OmsCartItem omsCartItem : omsCartItems) {
                if(omsCartItem.getIsChecked().equals("1")){
                    //将CartItem对象转化为OrderItem对象

                    //验价（价格在加入购物车后可能发生变化），
                    boolean b= skuService.checkPrice(omsCartItem.getProductSkuId(),omsCartItem.getPrice());
                    if(b==false){
                        mv.setViewName("tradeFail");
                        return mv;
                    }

                    //验库存

                    //将cartItem对象转换为orderItem对象
                    OmsOrderItem omsOrderItem = new OmsOrderItem();
                    omsOrderItem.setProductPic(omsCartItem.getProductPic());
                    omsOrderItem.setProductName(omsCartItem.getProductName());
                    omsOrderItem.setOrderSn(outTradeNo);//外部订单号，用来和其他系统进行交互，防止重复
                    omsOrderItem.setProductCategoryId(omsCartItem.getProductCategoryId());
                    omsOrderItem.setProductPrice(omsCartItem.getPrice());
                    omsOrderItem.setRealAmount(omsCartItem.getQuantity());
                    omsOrderItem.setProductSkuCode("666666666666666");
                    omsOrderItem.setProductSkuId(omsCartItem.getProductSkuId());
                    omsOrderItem.setProductId(omsCartItem.getProductId());
                    omsOrderItem.setProductSn("仓库对应的商品编号");//在仓库中的skuId

                    omsOrderItems.add(omsOrderItem);
                }
            }

            omsOrder.setOmsOrderItems(omsOrderItems);


            //2、将订单和订单详情写入数据库，并删除购物车的对应商品
            orderService.saveOrder(omsOrder);


            //3、重定向到支付系统
            mv.setViewName("redirect:http://localhost:8100/index");
            mv.addObject("outTradeNo",outTradeNo);
            mv.addObject("totalAmount",totalAmount);

            return mv;
        }

        mv.setViewName("tradeFail");
        return mv;
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
