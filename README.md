# gmall

gmall-user-service      用户服务的service层   tomcat:8070     dubbo:20880
gmall-user-web          用户服务的web层       tomcat:8090     dubbo:20890
    
gmall-manage-service    用户服务的service层   tomcat:8071     dubbo:20881
gmall-manage-web        用户服务的web层       tomcat:8091     dubbo:20891

gmall-item-web          商品详情的web层       tomcat:8092     dubbo:20892

gmall-search-service    搜索功能service层     tomcat:8073    dubbo:20883
gmall-search-web        搜索功能web层         tomcat:8093    dubbo:20893

gmall-cart-service      购物车service层       tomcat:8074    dubbo:20884
gmall-cart-web          购物车web层           tomcat:8094    dubbo:20894

gmall-passport-web      认证中心web层         tomcat:8095    dubbo:20895

gmall-order-service      订单service层       tomcat:8076    dubbo:20905
gmall-order-web          订单web层           tomcat:8096    dubbo:20906

gmall-payment           支付模块              tomcat：8100   dubbo:20910

主动登录：跳转到用户中心，保存回调地址，进行如下操作：检验用户名密码，通过则将生成token存入redis，并返回token

去购物车结算包含的动作：验证用户名和密码，如果通过则将memberId和nickname添加到请求参数中，并将缓存中的oldToken更新

存放在缓存中的数据：
    用户token    user:"+memberId+":cart
    购物车信息   user:"+memberId+":token