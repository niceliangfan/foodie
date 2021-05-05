package com.imooc.controller.center;

import com.imooc.controller.BaseController;
import com.imooc.enums.YesOrNoEnum;
import com.imooc.pojo.OrderItems;
import com.imooc.pojo.Orders;
import com.imooc.pojo.bo.center.OrderItemsCommentBO;
import com.imooc.service.center.MyCommentsService;
import com.imooc.service.center.MyOrderService;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.PagedGridResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("mycomments")
@Api(value = "用户中心我的评论", tags = {"用户中心我的评论相关接口"})
public class MyCommentController extends BaseController {

    @Autowired
    private MyCommentsService myCommentsService;
    @Autowired
    private MyOrderService myOrderService;

    @ApiOperation(value = "查询待评论的订单", notes = "查询待评论的订单", httpMethod = "POST")
    @PostMapping("/pending")
    public IMOOCJSONResult pending(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId,
            @ApiParam(name = "orderId", value = "订单id", required = true)
            @RequestParam String orderId) {

        // 判断用户与订单是否关联
        IMOOCJSONResult checkResult = checkUserOrder(userId, orderId);
        if (checkResult.getStatus() != HttpStatus.OK.value()) {
            return checkResult;
        }
        // 判断订单是否已评价
        Orders order = (Orders) checkResult.getData();
        if (order.getIsComment() == YesOrNoEnum.YES.type) {
            return IMOOCJSONResult.errorMsg("该订单已评价！");
        }

        List<OrderItems> list = myCommentsService.queryPendingComments(orderId);
        return IMOOCJSONResult.ok(list);
    }

    @ApiOperation(value = "保存商品评论列表", notes = "保存商品评论列表", httpMethod = "POST")
    @PostMapping("/saveList")
    public IMOOCJSONResult saveList(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId,
            @ApiParam(name = "orderId", value = "订单id", required = true)
            @RequestParam String orderId,
            @ApiParam(name = "commentList", value = "评论列表", required = true)
            @RequestBody List<OrderItemsCommentBO> commentList) {

        // 判断用户与订单是否关联
        IMOOCJSONResult checkResult = checkUserOrder(userId, orderId);
        if (checkResult.getStatus() != HttpStatus.OK.value()) {
            return checkResult;
        }
        // 判断评论是否为空
        if (commentList == null || commentList.isEmpty() || commentList.size() == 0) {
            return IMOOCJSONResult.errorMsg("评论不能为空！");
        }

        // System.out.println(commentList);
        myCommentsService.saveComments(userId, orderId, commentList);
        return IMOOCJSONResult.ok();
    }

    @ApiOperation(value = "查询用户订单", notes = "查询用户订单", httpMethod = "POST")
    @PostMapping("/query")
    public IMOOCJSONResult query(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId,
            @ApiParam(name = "page", value = "查询下一页的第几页", required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize", value = "分页的每一页显示的条数", required = false)
            @RequestParam Integer pageSize) {

        if (StringUtils.isBlank(userId)) {
            IMOOCJSONResult.errorMsg(null);
        }
        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            pageSize = DEFAULT_COMMENT_PAGE_SIZE;
        }

        PagedGridResult grid = myCommentsService.queryMyComments(userId, page, pageSize);
        return IMOOCJSONResult.ok(grid);
    }

    /**
     * 用于验证用户和订单是否有关联关系，避免非法用户调用
     * @return
     */
    private IMOOCJSONResult checkUserOrder(String userId, String orderId) {
        Orders order = myOrderService.queryMyOrder(userId, orderId);
        if (order == null) {
            return IMOOCJSONResult.errorMsg("订单不存在！");
        }
        return IMOOCJSONResult.ok(order);
    }
}
