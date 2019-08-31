package com.luch.gmall.search;

import com.alibaba.dubbo.config.annotation.Reference;
import com.luch.gmall.bean.PmsSearchSkuInfo;
import com.luch.gmall.bean.PmsSkuInfo;
import com.luch.gmall.service.SkuService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallSearchServiceApplicationTests {

	@Reference
	SkuService skuService;

	@Autowired
	JestClient jestClient;

	@Test
	public void contextLoads() {
		try {
			get();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void put() throws Exception{

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

		//导入es
		for (PmsSearchSkuInfo pmsSearchSkuInfo : pmsSearchSkuInfos) {
			Index index = new Index.Builder(pmsSearchSkuInfo).index("gmall").type("SkuInfo").id(pmsSearchSkuInfo.getId() + "").build();
			jestClient.execute(index);
		}
	}

	public void get() throws Exception{
		//jest的dsl工具
		SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();

//		GET gmall/SkuInfo/_search
//		{
//			"query": {
//				"bool": {
//					"filter": [
//						{"terms":{"skuAttrValueList.valueId":["39","40","41"]}},
//						{"term":{"skuAttrValueList.valueId":"43"}}
//					],
//					"must": [
//						{"match": {"skuName": "华为"}}
//      				]
//				}
//			}
//		}
			//bool
		BoolQueryBuilder boolQueryBuilder=new BoolQueryBuilder();
				//filter
		TermQueryBuilder termQueryBuilder = new TermQueryBuilder("skuAttrValueList.valueId", "43");
		boolQueryBuilder.filter(termQueryBuilder);
				//must
		MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName", "华为");
		boolQueryBuilder.must(matchQueryBuilder);
		//query
		searchSourceBuilder.query(boolQueryBuilder);
		//from
		searchSourceBuilder.from(0);
		//size
		searchSourceBuilder.size(200);
		//highlight
		searchSourceBuilder.highlight(null);

		String dslStr = searchSourceBuilder.toString();

		System.err.println(dslStr);

		//用api执行复杂查询
		List<PmsSearchSkuInfo> pmsSearchSkuInfos=new ArrayList<>();

		Search search=new Search.Builder(dslStr).addIndex("gmall").addType("SkuInfo").build();

		SearchResult result = jestClient.execute(search);

		List<SearchResult.Hit<PmsSearchSkuInfo, Void>> hits = result.getHits(PmsSearchSkuInfo.class);

		for (SearchResult.Hit<PmsSearchSkuInfo, Void> hit : hits) {
			PmsSearchSkuInfo pmsSearchSkuInfo = hit.source;
			pmsSearchSkuInfos.add(pmsSearchSkuInfo);
		}
		System.out.println(pmsSearchSkuInfos.size());
	}

}
