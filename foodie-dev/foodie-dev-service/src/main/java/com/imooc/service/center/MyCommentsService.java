package com.imooc.service.center;

import com.imooc.pojo.OrderItems;
import com.imooc.pojo.bo.center.OrderItemsCommentBO;
import com.imooc.utils.PagedGridResult;

import java.util.List;

public interface MyCommentsService {

    /**
     * 查询待评价的订单
     * @param orderId
     * @return
     */
    public List<OrderItems> queryPendingComments(String orderId);

    /**
     * 保存用户的评论
     * @param orderId
     * @param userId
     * @param commentList
     */
    public void saveComments(String userId, String orderId, List<OrderItemsCommentBO> commentList);

    /**
     * 查询用户评论
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult queryMyComments(String userId, Integer page, Integer pageSize);

}