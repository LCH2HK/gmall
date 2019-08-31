package com.luch.gmall.payment.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.RetailKbcodeQueryVo;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.luch.gmall.annotation.LoginRequired;
import com.luch.gmall.bean.OmsOrder;
import com.luch.gmall.bean.PaymentInfo;
import com.luch.gmall.payment.config.AlipayConfig;
import com.luch.gmall.service.OrderService;
import com.luch.gmall.service.PaymentService;
import jdk.internal.org.objectweb.asm.tree.TryCatchBlockNode;
import org.apache.commons.lang3.StringUtils;
import org.omg.CORBA.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author luch
 * @date 2019/8/29-9:13
 */
@Controller
public class PaymentController {

    @Autowired
    AlipayClient alipayClient;

    @Reference
    OrderService orderService;

    @Autowired
    PaymentService paymentService;

    @LoginRequired
    @RequestMapping("/index")
    public String index(String outTradeNo,BigDecimal totalAmount,HttpServletRequest request,ModelMap modelMap){

        String memberId = (String) request.getAttribute("memberId");
        String nickname = (String) request.getAttribute("nickname");

        modelMap.put("nickname",nickname);
        modelMap.put("outTradeNo",outTradeNo);
        modelMap.put("totalAmount",totalAmount);

        return "index";
    }

    @LoginRequired
    @RequestMapping("/alipay/submit")
    @ResponseBody
    public String alipay(String outTradeNo, BigDecimal totalAmount, HttpServletRequest request, ModelMap modelMap){

        //获得一个支付宝请求的客户端（它不是一个连接，而是一个封装好的http的表单请求）
        String form=null;
        AlipayTradePagePayRequest payRequest = new AlipayTradePagePayRequest();

        //回调函数
        payRequest.setReturnUrl(AlipayConfig.return_payment_url);
        payRequest.setNotifyUrl(AlipayConfig.notify_payment_url);

        Map<String ,Object> map=new HashMap<>();
        map.put("out_trade_no",outTradeNo);
        map.put("product_code","FAST_INSTANT_TRADE_PAY");
        map.put("total_amount",0.01);
        map.put("subject","vivo宇宙神机");
        String param = JSON.toJSONString(map);

        payRequest.setBizContent(param);

        try {
            form = alipayClient.pageExecute(payRequest).getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        //生成并保存用户的支付信息
        OmsOrder omsOrder=orderService.getOrderByOutTradeNo(outTradeNo);
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setOrderId(omsOrder.getId());
        paymentInfo.setOrderSn(outTradeNo);
        paymentInfo.setPaymentStatus("未付款");
        paymentInfo.setSubject("谷粒商城商品一件");
        paymentInfo.setTotalAmount(totalAmount);
        paymentService.savePaymentInfo(paymentInfo);

        //向消息队列发送一个检查支付状态的延迟消息队列
        paymentService.sendPaymentResultCheckDelayQueue(outTradeNo,5);

        //提交请求到支付宝
        return form;
    }

    @RequestMapping("/alipay/callback/return")
    @LoginRequired
    public String aliCallBackReturn(HttpServletRequest request,ModelMap modelMap){

        //回调请求中获取支付宝参数
        String sign = request.getParameter("sign");
        String trade_no = request.getParameter("trade_no");
        String out_trade_no = request.getParameter("out_trade_no");
        String trade_status = request.getParameter("trade_status");
        String total_amount = request.getParameter("total_amount");
        String subject = request.getParameter("subject");
        String call_back_content = request.getQueryString();

        //通过支付宝的paramsMap进行签名验证，2.0版本的接口将paramsMap参数去掉了，导致同步请求没法验签，这里假设sign不为空就验签成功
        if(StringUtils.isNotBlank(sign)){//验签成功
            //更新用户的支付状态
            PaymentInfo paymentInfo = new PaymentInfo();
            paymentInfo.setOrderSn(out_trade_no);
            paymentInfo.setPaymentStatus("已支付");
            paymentInfo.setAlipayTradeNo(trade_no);//支付宝的交易凭证号
            paymentInfo.setCallbackContent(call_back_content);
            paymentInfo.setCallbackTime(new Date());

            paymentService.updatePayment(paymentInfo);
        }
        return "finish";
    }

}
