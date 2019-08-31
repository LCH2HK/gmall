package com.luch.gmall;

import com.luch.gmall.bean.PmsSearchSkuInfo;
import com.luch.gmall.bean.PmsSkuInfo;
import com.luch.gmall.service.SkuService;
import com.luch.gmall.util.RedisUtil;
import jdk.nashorn.internal.ir.annotations.Reference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallManageServiceApplicationTests {

	@Autowired
	SkuService skuService;



	@Test
	public void contextLoads() {
		try {
			put();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void put() throws Exception{
		System.out.println(skuService);
		//查询mysql数据
		List<PmsSkuInfo> pmsSkuInfos = skuService.getAllSku("61");
		//转化为es的数据结构
		List<PmsSearchSkuInfo> pmsSearchSkuInfos=new ArrayList<>();
		for (PmsSkuInfo pmsSkuInfo : pmsSkuInfos) {
			PmsSearchSkuInfo pmsSearchSkuInfo = new PmsSearchSkuInfo();
			BeanUtils.copyProperties(pmsSkuInfo,pmsSearchSkuInfo);
			pmsSearchSkuInfo.setId(Long.parseLong(pmsSkuInfo.getId()));
			pmsSearchSkuInfos.add(pmsSearchSkuInfo);
		}
		System.out.println();
		//导入es
	}

}
