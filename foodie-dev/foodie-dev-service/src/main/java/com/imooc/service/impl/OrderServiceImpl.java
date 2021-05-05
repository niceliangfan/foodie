package com.imooc.service.impl;

import com.imooc.enums.OrderStatusEnum;
import com.imooc.enums.YesOrNoEnum;
import com.imooc.mapper.OrderItemsMapper;
import com.imooc.mapper.OrderStatusMapper;
import com.imooc.mapper.OrdersMapper;
import com.imooc.pojo.*;
import com.imooc.pojo.bo.SubmitOrderBO;
import com.imooc.service.AddressService;
import com.imooc.service.ItemService;
import com.imooc.service.OrderService;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private OrderItemsMapper orderItemsMapper;
    @Autowired
    private OrderStatusMapper orderStatusMapper;

    @Autowired
    private Sid sid;

    @Autowired
    private AddressService addressService;
    @Autowired
    private ItemService itemService;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public String createOrder(SubmitOrderBO submitOrderBO) {

        String userId = submitOrderBO.getUserId();
        String addressId = submitOrderBO.getAddressId();
        String itemSpecIds = submitOrderBO.getItemSpecIds();
        Integer payMethod = submitOrderBO.getPayMethod();
        String leftMsg = submitOrderBO.getLeftMsg();
        Integer postAmount = 0;

        // 1.新订单数据保存至订单表（orders）
        Orders newOrder = new Orders();
        String orderId = sid.nextShort();
        newOrder.setId(orderId);
        newOrder.setUserId(userId);

        UserAddress userAddress = addressService.queryUserAddress(userId, addressId);
        newOrder.setReceiverName(userAddress.getReceiver());
        newOrder.setReceiverMobile(userAddress.getMobile());
        newOrder.setReceiverAddress(userAddress.getProvince() + " "
                                    + userAddress.getCity() + " "
                                    + userAddress.getDistrict() + " "
                                    + userAddress.getDetail());

        //newOrder.setTotalAmount();
        //newOrder.setRealPayAmount();

        // 包邮，费用为0
        newOrder.setPostAmount(postAmount);
        newOrder.setPayMethod(payMethod);
        newOrder.setLeftMsg(leftMsg);

        newOrder.setIsComment(YesOrNoEnum.No.type);
        newOrder.setIsDelete(YesOrNoEnum.No.type);

        newOrder.setCreatedTime(new Date());
        newOrder.setUpdatedTime(new Date());


        // 2.循环，根据itemSpecId保存订单商品表（order_items）
        String[] itemSpecIdArray = itemSpecIds.split(",");
        Integer totalAmount = 0;
        Integer realPayAmount = 0;
        // TODO 整合redis后，商品购买的数量重新从redis的购物车中获取
        Integer buyCounts = 1;
        for (String itemSpecId: itemSpecIdArray) {

            OrderItems orderItem = new OrderItems();
            String orderItemId = sid.nextShort();
            orderItem.setId(orderItemId);
            orderItem.setOrderId(orderId);

            ItemsSpec itemSpec = itemService.queryItemSpecBySpecId(itemSpecId);
            orderItem.setItemSpecId(itemSpecId);
            orderItem.setItemSpecName(itemSpec.getName());
            orderItem.setPrice(itemSpec.getPriceDiscount());
            totalAmount += itemSpec.getPriceNormal() * buyCounts;
            realPayAmount += itemSpec.getPriceDiscount() * buyCounts;

            String itemId = itemSpec.getItemId();
            Items item = itemService.queryItemById(itemId);
            orderItem.setItemId(itemId);
            orderItem.setItemName(item.getItemName());
            String imgUrl = itemService.queryItemMainImgById(itemId);
            orderItem.setItemImg(imgUrl);

            orderItem.setBuyCounts(buyCounts);
            orderItemsMapper.insert(orderItem);

            itemService.decreaseItemSpecStock(itemSpecId, buyCounts);
        }

        newOrder.setTotalAmount(totalAmount);
        newOrder.setRealPayAmount(realPayAmount);
        ordersMapper.insert(newOrder);

        // 3.保存订单状态表（order_status）
        OrderStatus waitPayOrderStatus = new OrderStatus();
        waitPayOrderStatus.setOrderId(orderId);
        waitPayOrderStatus.setOrderStatus(OrderStatusEnum.WAIT_PAY.type);
        waitPayOrderStatus.setCreatedTime(new Date());
        orderStatusMapper.insert(waitPayOrderStatus);

        return orderId;
    }
}
