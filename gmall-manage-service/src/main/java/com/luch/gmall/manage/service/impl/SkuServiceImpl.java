package com.luch.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.luch.gmall.bean.*;
import com.luch.gmall.manage.mapper.*;
import com.luch.gmall.service.SkuService;
import com.luch.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @author luch
 * @date 2019/8/18-8:51
 */
@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    PmsSkuImageMapper pmsSkuImageMapper;

    @Autowired
    PmsProductImageMapper pmsProductImageMapper;

    @Autowired
    PmsSkuInfoMapper pmsSkuInfoMapper;

    @Autowired
    PmsSkuAttrValueMapper pmsSkuAttrValueMapper;

    @Autowired
    PmsSkuSaleAttrValueMapper pmsSkuSaleAttrValueMapper;

    @Autowired
    RedisUtil redisUtil;

    @Override
    public List<PmsProductImage> getSpuImageList(String spuId) {

        PmsProductImage pmsProductImage = new PmsProductImage();
        pmsProductImage.setProductId(spuId);
        List<PmsProductImage> pmsProductImages = pmsProductImageMapper.select(pmsProductImage);
        return pmsProductImages;
    }

    @Override
    public String saveSkuInfo(PmsSkuInfo pmsSkuInfo) {

        pmsSkuInfo.setProductId(pmsSkuInfo.getSpuId());
        String defaultImgUrl = pmsSkuInfo.getSkuImageList().get(0).getImgUrl();
        pmsSkuInfo.setSkuDefaultImg(defaultImgUrl);
        pmsSkuInfoMapper.insert(pmsSkuInfo);

        String skuId = pmsSkuInfo.getId();

        List<PmsSkuAttrValue> skuAttrValueList = pmsSkuInfo.getSkuAttrValueList();
        for (PmsSkuAttrValue skuAttrValue : skuAttrValueList) {
            skuAttrValue.setSkuId(skuId);
            pmsSkuAttrValueMapper.insert(skuAttrValue);
        }

        List<PmsSkuImage> skuImageList = pmsSkuInfo.getSkuImageList();
        for (PmsSkuImage pmsSkuImage : skuImageList) {
            pmsSkuImage.setSkuId(skuId);
            pmsSkuImage.setProductImgId(pmsSkuImage.getSpuImgId());
            if(skuImageList.indexOf(pmsSkuImage)==0){
                pmsSkuImage.setIsDefault("1");
            }
            pmsSkuImageMapper.insert(pmsSkuImage);
        }

        List<PmsSkuSaleAttrValue> skuSaleAttrValueList = pmsSkuInfo.getSkuSaleAttrValueList();
        for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuSaleAttrValueList) {
            pmsSkuSaleAttrValue.setSkuId(skuId);
            pmsSkuSaleAttrValueMapper.insert(pmsSkuSaleAttrValue);
        }

        return "success";
    }

    @Override
    public PmsSkuInfo getSkuInfo(String skuId) {
        PmsSkuInfo skuInfo = getSkuInfoByRedis(skuId);
        return skuInfo;
    }

    public PmsSkuInfo getSkuInfoByDB(String skuId) {
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        pmsSkuInfo.setId(skuId);
        PmsSkuInfo SkuInfo = pmsSkuInfoMapper.selectOne(pmsSkuInfo);


        PmsSkuImage pmsSkuImage = new PmsSkuImage();
        pmsSkuImage.setSkuId(skuId);
        List<PmsSkuImage> pmsSkuImages = pmsSkuImageMapper.select(pmsSkuImage);
        SkuInfo.setSkuImageList(pmsSkuImages);

        return SkuInfo;
    }

    public PmsSkuInfo getSkuInfoByRedis(String skuId) {
        Jedis jedis = redisUtil.getJedis();
        String skuKey="sku:"+skuId+":info";
        String skuInfoJson = jedis.get(skuKey);
        PmsSkuInfo pmsSkuInfo=null;
        //对应数据存在缓存
        if(StringUtils.isNotBlank(skuInfoJson)){
            pmsSkuInfo = JSON.parseObject(skuInfoJson, PmsSkuInfo.class);
        }else{//对应数据没有被缓存
            //是否获得分布式锁
            String lockKey= "sku:" + skuId + ":lock";
            String lockVal=UUID.randomUUID().toString();
            String OK = jedis.set(lockKey, lockVal, "nx", "ex", 10000);
            if(StringUtils.isNotBlank(OK)&&"OK".equals(OK)){//获得分布式锁
                pmsSkuInfo = getSkuInfoByDB(skuId);
                if(pmsSkuInfo!=null){
                    jedis.set(skuKey,JSON.toJSONString(pmsSkuInfo));
                }else{
                    jedis.setex(skuKey,60*3,"");//防止缓存穿透
                }
//                String lockVal0 = jedis.get(lockKey);
//                if(StringUtils.isNotBlank(lockVal0)&&lockVal.equals(lockVal0)){
//                    jedis.del("sku:" + skuId + ":lock");
//                }
                //用lua脚本代替上面的语句，防止在if判断的时候key过期，该线程删除了其他线程的lockKey
                String script ="if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                jedis.eval(script, Collections.singletonList(lockKey),Collections.singletonList(lockVal));
            }else{//未获得分布式锁
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //必须加return，否则就是新增了一条线程
                return getSkuInfoByRedis(skuId);
            }
        }

        jedis.close();
        return pmsSkuInfo;
    }

    @Override
    public List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(String spuId) {
        List<PmsSkuInfo> pmsSkuInfos= pmsSkuInfoMapper.selSkuSaleAttrValueListBySpu(spuId);
        return pmsSkuInfos;
    }

    @Override
    public List<PmsSkuInfo> getAllSku(String catalog3Id) {
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        pmsSkuInfo.setCatalog3Id(catalog3Id);
        List<PmsSkuInfo> pmsSkuInfos = pmsSkuInfoMapper.selectAll();
        for (PmsSkuInfo skuInfo : pmsSkuInfos) {
            PmsSkuAttrValue pmsSkuAttrValue=new PmsSkuAttrValue();
            pmsSkuAttrValue.setSkuId(skuInfo.getId());
            List<PmsSkuAttrValue> pmsSkuAttrValueList = pmsSkuAttrValueMapper.select(pmsSkuAttrValue);
            skuInfo.setSkuAttrValueList(pmsSkuAttrValueList);
        }
        return pmsSkuInfos;
    }

    @Override
    public boolean checkPrice(String productSkuId, BigDecimal price) {
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        pmsSkuInfo.setId(productSkuId);
        PmsSkuInfo pmsSkuInfo1 = pmsSkuInfoMapper.selectOne(pmsSkuInfo);
        BigDecimal currentPrice = pmsSkuInfo1.getPrice();
        if(currentPrice.compareTo(price)==0){
            return true;
        }
        return false;
    }

    public PmsSkuInfo getSkuInfo1(String skuId) {
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        pmsSkuInfo.setId(skuId);
        PmsSkuInfo SkuInfo = pmsSkuInfoMapper.selectOne(pmsSkuInfo);

        String productId=SkuInfo.getProductId();

        PmsSkuImage pmsSkuImage = new PmsSkuImage();
        pmsSkuImage.setSkuId(skuId);
        List<PmsSkuImage> pmsSkuImages = pmsSkuImageMapper.select(pmsSkuImage);
        SkuInfo.setSkuImageList(pmsSkuImages);


        return SkuInfo;

    }



}
