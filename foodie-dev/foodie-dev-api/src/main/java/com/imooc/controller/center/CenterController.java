package com.imooc.controller.center;

import com.imooc.pojo.Users;
import com.imooc.service.center.UserCenterService;
import com.imooc.utils.IMOOCJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("center")
@Api(value = "center - 用户中心", tags = {"用户中心展示的相关接口"})
public class CenterController {

    @Autowired
    private UserCenterService userCenterService;

    @ApiOperation(value = "获取用户信息", notes = "获取用户信息", httpMethod = "GET")
    @GetMapping("/userInfo")
    public IMOOCJSONResult userInfo(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId) {
        Users user = userCenterService.queryUserInfo(userId);
        return IMOOCJSONResult.ok(user);
    }
}
