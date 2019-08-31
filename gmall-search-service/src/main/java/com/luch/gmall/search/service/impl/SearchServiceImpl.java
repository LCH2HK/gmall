package com.luch.gmall.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.luch.gmall.bean.PmsSearchParam;
import com.luch.gmall.bean.PmsSearchSkuInfo;
import com.luch.gmall.bean.PmsSkuAttrValue;
import com.luch.gmall.service.SearchService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.transform.Source;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author luch
 * @date 2019/8/22-14:52
 */
@Service
public class SearchServiceImpl implements SearchService{

    @Autowired
    JestClient jestClient;

    @Override
    public List<PmsSearchSkuInfo> getSearchSkuInfos(PmsSearchParam pmsSearchParam) {

        String dslStr=getDslString(pmsSearchParam);

        //用api执行复杂查询
        List<PmsSearchSkuInfo> pmsSearchSkuInfos=new ArrayList<>();

        Search search=new Search.Builder(dslStr).addIndex("gmall").addType("SkuInfo").build();

        SearchResult result = null;
        try {
            result = jestClient.execute(search);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<SearchResult.Hit<PmsSearchSkuInfo, Void>> hits = result.getHits(PmsSearchSkuInfo.class);

        for (SearchResult.Hit<PmsSearchSkuInfo, Void> hit : hits) {
            PmsSearchSkuInfo pmsSearchSkuInfo = hit.source;
            Map<String, List<String>> highlight = hit.highlight;
            if(highlight!=null){
                String skuName = highlight.get("skuName").get(0);
                pmsSearchSkuInfo.setSkuName(skuName);
            }
            pmsSearchSkuInfos.add(pmsSearchSkuInfo);
        }

        return pmsSearchSkuInfos;
    }

    public String getDslString(PmsSearchParam pmsSearchParam){


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

//        List<PmsSkuAttrValue> skuAttrValueList = pmsSearchParam.getSkuAttrValueList();
        String[] skuAttrValueList = pmsSearchParam.getValueId();
        String catalog3Id = pmsSearchParam.getCatalog3Id();
        String keyword = pmsSearchParam.getKeyword();

        //bool
        BoolQueryBuilder boolQueryBuilder=new BoolQueryBuilder();

        //filter
        if(StringUtils.isNotBlank(catalog3Id)){
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("catalog3Id", catalog3Id);
            boolQueryBuilder.filter(termQueryBuilder);
        }

        if(skuAttrValueList!=null){
            for (String pmsSkuAttrValue : skuAttrValueList) {
//                String valueId = pmsSkuAttrValue.getValueId();
                TermQueryBuilder termQueryBuilder = new TermQueryBuilder("skuAttrValueList.valueId", pmsSkuAttrValue);
                boolQueryBuilder.filter(termQueryBuilder);
            }
        }

        //must
        if(StringUtils.isNotBlank(keyword)){
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName", keyword);
            boolQueryBuilder.must(matchQueryBuilder);
        }

        //jest的dsl工具
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();

        //query
        searchSourceBuilder.query(boolQueryBuilder);
        //from
        searchSourceBuilder.from(0);
        //size
        searchSourceBuilder.size(20);
        //highlight
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<span style='color:red;'>");
        highlightBuilder.field("skuName");
        highlightBuilder.postTags("</span>");
        searchSourceBuilder.highlight(highlightBuilder);

        // sort
        searchSourceBuilder.sort("id", SortOrder.DESC);

        String dslStr = searchSourceBuilder.toString();

        return  dslStr;
    }

}
