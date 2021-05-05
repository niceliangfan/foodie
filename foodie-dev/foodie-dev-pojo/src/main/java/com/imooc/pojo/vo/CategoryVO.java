package com.imooc.pojo.vo;

import java.util.List;

/**
 * 二级分类VO
 */
public class CategoryVO {

    private  Integer id;
    private String name;
    private Integer type;
    private Integer fatherId;

    // 三级分类VO list
    // 对象名(subCatList)
    // 如果写别的对象名(如：subCategoryVOList)，则前端得不到后端数据。
    // 原因：前后端同一对象的属性名不一致
    private List<SubCategoryVO> subCatList;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getFatherId() {
        return fatherId;
    }

    public void setFatherId(Integer fatherId) {
        this.fatherId = fatherId;
    }

    public List<SubCategoryVO> getSubCatList() {
        return subCatList;
    }

    public void setSubCatList(List<SubCategoryVO> subCatList) {
        this.subCatList = subCatList;
    }
}
