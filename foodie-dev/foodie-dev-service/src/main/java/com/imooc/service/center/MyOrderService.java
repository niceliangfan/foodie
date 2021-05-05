package com.imooc.service.center;

import com.imooc.pojo.Orders;
import com.imooc.pojo.vo.center.OrderStatusCountsVO;
import com.imooc.utils.PagedGridResult;

public interface MyOrderService {

    /**
     * 根据用户id、订单状态查询我的订单
     * @param userId
     * @param orderStatus
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult queryMyOrders(String userId, Integer orderStatus, Integer page, Integer pageSize);

    /**
     * @Descriptoin: 订单状态 --> 商家发货
     * @param orderId
     */
    public void updateDeliverOrderStatus(String orderId);

    /**
     * 查询我的订单
     *
     * @param userId
     * @param orderId
     * @return
     */
    public Orders queryMyOrder(String userId, String orderId);

    /**
     * 更新订单状态 —> 确认收货
     *
     * @return
     */
    public boolean updateReceiveOrderStatus(String orderId);

    /**
     * 删除订单（逻辑删除）
     * @param userId
     * @param orderId
     * @return
     */
    public boolean deleteOrder(String userId, String orderId);

    /**
     * 查询订单状态数
     * @param userId
     * @return
     */
    public OrderStatusCountsVO getOrderStatusCounts(String userId);

    /**
     * 查询订单动向
     * @param userId
     * @return
     */
    public PagedGridResult getMyOrderTrend(String userId, Integer page, Integer pageSize);
}
