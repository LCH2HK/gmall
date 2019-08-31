package com.luch.gmall.bean;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * 对应pms_base_sale_attr表，是一个字典表，包含可以添加的销售属性
 */
public class PmsBaseSaleAttr implements Serializable {

    @Id
    @Column
    String id ;

    @Column
    String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}