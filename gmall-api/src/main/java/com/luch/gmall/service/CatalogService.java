package com.luch.gmall.service;

import com.luch.gmall.bean.PmsBaseCatalog1;
import com.luch.gmall.bean.PmsBaseCatalog2;
import com.luch.gmall.bean.PmsBaseCatalog3;

import java.util.List;

/**
 * @author luch
 * @date 2019/8/15-22:50
 */
public interface CatalogService {

    public List<PmsBaseCatalog1> getCatalog1();

    public List<PmsBaseCatalog2> getCatalog2(String catalog1Id);

    public List<PmsBaseCatalog3> getCatalog3(String catalog2Id);
}
