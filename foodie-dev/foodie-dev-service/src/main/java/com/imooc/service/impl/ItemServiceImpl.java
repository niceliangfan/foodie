package com.imooc.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.enums.CommentLevelEnum;
import com.imooc.enums.YesOrNoEnum;
import com.imooc.mapper.*;
import com.imooc.pojo.*;
import com.imooc.pojo.vo.ItemCommentLevelCountsVO;
import com.imooc.pojo.vo.ItemCommentVO;
import com.imooc.pojo.vo.SearchItemVO;
import com.imooc.pojo.vo.ShopCartItemVO;
import com.imooc.service.ItemService;
import com.imooc.utils.DesensitizationUtil;
import com.imooc.utils.PagedGridResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemsMapper itemsMapper;
    @Autowired
    private ItemsImgMapper itemsImgMapper;
    @Autowired
    private ItemsSpecMapper itemsSpecMapper;
    @Autowired
    private ItemsParamMapper itemsParamMapper;
    @Autowired
    private ItemsCommentsMapper itemsCommentsMapper;
    @Autowired
    private ItemsCommentsMapperCustom itemsCommentsMapperCustom;
    @Autowired
    private ItemsMapperCustom itemsMapperCustom;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Items queryItemById(String itemId) {

        Items result = itemsMapper.selectByPrimaryKey(itemId);
        return result;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<ItemsImg> queryItemImgList(String itemId) {

        Example itemImgExample = new Example(ItemsImg.class);
        Example.Criteria itemImgExampleCriteria = itemImgExample.createCriteria();
        itemImgExampleCriteria.andEqualTo("itemId", itemId);

        List<ItemsImg> result = itemsImgMapper.selectByExample(itemImgExample);
        return result;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<ItemsSpec> queryItemSpecList(String itemId) {

        Example itemSpecExample = new Example(ItemsSpec.class);
        Example.Criteria itemSpecExampleCriteria = itemSpecExample.createCriteria();
        itemSpecExampleCriteria.andEqualTo("itemId", itemId);

        List<ItemsSpec> result = itemsSpecMapper.selectByExample(itemSpecExample);
        return result;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public ItemsParam queryItemParam(String itemId) {

        Example itemParamExample = new Example(ItemsParam.class);
        Example.Criteria itemParamExampleCriteria = itemParamExample.createCriteria();
        itemParamExampleCriteria.andEqualTo("itemId", itemId);

        ItemsParam result = itemsParamMapper.selectOneByExample(itemParamExample);
        return result;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public ItemCommentLevelCountsVO queryItemCommentLevelCounts(String itemId) {

        Integer goodCounts = getItemCommentLevelCounts(itemId, CommentLevelEnum.GOOD.type);
        Integer normalCounts = getItemCommentLevelCounts(itemId, CommentLevelEnum.NORMAL.type);
        Integer badCounts = getItemCommentLevelCounts(itemId, CommentLevelEnum.BAD.type);
        Integer totalCounts = goodCounts + normalCounts + badCounts;

        ItemCommentLevelCountsVO itemCommentLevelCountsVO = new ItemCommentLevelCountsVO();
        itemCommentLevelCountsVO.setTotalCounts(totalCounts);
        itemCommentLevelCountsVO.setGoodCounts(goodCounts);
        itemCommentLevelCountsVO.setNormalCounts(normalCounts);
        itemCommentLevelCountsVO.setBadCounts(badCounts);

        return itemCommentLevelCountsVO;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    Integer getItemCommentLevelCounts(String itemId, Integer commentLevel) {
        ItemsComments itemsComments = new ItemsComments();
        itemsComments.setItemId(itemId);
        if (commentLevel != null) {
            itemsComments.setCommentLevel(commentLevel);
        }
        return itemsCommentsMapper.selectCount(itemsComments);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult queryItemPagedComments(String itemId, Integer level, Integer page, Integer pageSize) {

        Map<String, Object> map = new HashMap<>();
        map.put("itemId", itemId);
        map.put("level", level);

        /**
         * page: 第几页
         * pageSize: 每页显示条数
         */
        PageHelper.startPage(page, pageSize);

        List<ItemCommentVO> list = itemsCommentsMapperCustom.queryItemComments(map);
        for (ItemCommentVO vo: list) {
            vo.setNickname(DesensitizationUtil.commonDisplay(vo.getNickname()));
        }

        return setPagedGrid(list, page);
    }

    private PagedGridResult setPagedGrid(List<?> list, Integer page) {

        PageInfo<?> pageList = new PageInfo<>(list);
        PagedGridResult grid = new PagedGridResult();
        grid.setPage(page);
        grid.setRows(list);
        grid.setTotal(pageList.getPages());
        grid.setRecords(pageList.getTotal());
        return grid;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult searchPagedItems(String keywords, String sort, Integer page, Integer pageSize) {

        Map<String, Object> map = new HashMap<>();
        map.put("keywords", keywords);
        map.put("sort", sort);

        /**
         * page: 第几页
         * pageSize: 每页显示条数
         */
        PageHelper.startPage(page, pageSize);

        List<SearchItemVO> list = itemsMapperCustom.searchItems(map);
        return setPagedGrid(list, page);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult searchPagedItemsByThirdCatId(Integer catId, String sort, Integer page, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("catId", catId);
        map.put("sort", sort);

        /**
         * page: 第几页
         * pageSize: 每页显示条数
         */
        PageHelper.startPage(page, pageSize);

        List<SearchItemVO> list = itemsMapperCustom.searchItemsByThirdCatId(map);
        return setPagedGrid(list, page);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<ShopCartItemVO> queryItemsBySpecIds(String itemSpecIds) {

        String[] ids = itemSpecIds.split(",");
        List<String> specIdsList = new ArrayList<>();
        Collections.addAll(specIdsList, ids);

        return itemsMapperCustom.queryItemsBySpecIds(specIdsList);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public ItemsSpec queryItemSpecBySpecId(String specId) {
        return itemsSpecMapper.selectByPrimaryKey(specId);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public String queryItemMainImgById(String itemId) {
        ItemsImg itemImg = new ItemsImg();
        itemImg.setItemId(itemId);
        itemImg.setIsMain(YesOrNoEnum.YES.type);
        ItemsImg itemMainImg = itemsImgMapper.selectOne(itemImg);
        return itemMainImg != null ? itemMainImg.getUrl() : "";
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void decreaseItemSpecStock(String specId, Integer buyCounts) {
        Integer result = itemsMapperCustom.decreaseItemSpecStock(specId, buyCounts);
        if (result != 1) {
            throw new RuntimeException("创建订单失败，原因：库存不足！");
        }
    }
}
