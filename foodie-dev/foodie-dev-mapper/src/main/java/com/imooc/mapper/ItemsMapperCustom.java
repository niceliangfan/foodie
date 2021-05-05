package com.imooc.mapper;

import com.imooc.pojo.vo.SearchItemVO;
import com.imooc.pojo.vo.ShopCartItemVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ItemsMapperCustom {

    public List<SearchItemVO> searchItems(@Param("paramsMap") Map<String, Object> map);

    public List<SearchItemVO> searchItemsByThirdCatId(@Param("paramsMap") Map<String, Object> map);

    public List<ShopCartItemVO> queryItemsBySpecIds(@Param("paramsList") List specIdsList);

    public Integer decreaseItemSpecStock(@Param("specId") String specId, @Param("buyCounts") Integer buyCounts);
}