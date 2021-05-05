package com.imooc.controller.center;

import com.imooc.controller.BaseController;
import com.imooc.pojo.Users;
import com.imooc.pojo.bo.center.UserCenterBO;
import com.imooc.resource.FileUpload;
import com.imooc.service.center.UserCenterService;
import com.imooc.utils.CookieUtils;
import com.imooc.utils.DateUtil;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.JsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("userInfo")
@Api(value = "用户信息接口", tags = "用户信息相关接口")
public class UserCenterController{

    @Autowired
    private UserCenterService userCenterService;
    @Autowired
    private FileUpload fileUpload;

    @ApiOperation(value = "修改用户信息", notes = "修改用户信息", httpMethod = "POST")
    @PostMapping("/update")
    public IMOOCJSONResult update(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId,
            @RequestBody @Valid UserCenterBO userCenterBO,
            BindingResult result,
            HttpServletRequest request, HttpServletResponse response) {

        // 判断BindingResult是否保存错误的验证信息，如果有，则直接return
        if (result.hasErrors()) {
            Map<String, String> errorMap = getErrors(result);
            return IMOOCJSONResult.errorMap(errorMap);
        }

        Users user = userCenterService.updateUserInfo(userId, userCenterBO);
        Users userResult = setNullProperty(user);
        CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(userResult), true);

        // TODO 后续要改，增加令牌token，会整合进redis，分布式会话
        return IMOOCJSONResult.ok();
    }

    @ApiOperation(value = "修改用户头像", notes = "修改用户头像", httpMethod = "POST")
    @PostMapping("/uploadFace")
    public IMOOCJSONResult uploadFace(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId,
            @ApiParam(name = "file", value = "头像文件", required = true)
            MultipartFile file,
            HttpServletRequest request, HttpServletResponse response) {

        // 1.定义头像保存地址
        String fileSpace = fileUpload.getDefaultUserFacePath();

        // 2.在路径上为每个用户增加一个userId，用于区分不同用户上传
        String uploadPathPrefix = File.separator + userId;

        String newFileName = "";
        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;

        // 3.开始上传文件
        if (file != null) {

            // 获取上传文件的文件名
            String fileName = file.getOriginalFilename();
            if (StringUtils.isNotBlank(fileName)) {

                // 文件重命名 face-123.png --> face_{userId}.png
                String[] fileNameArray = fileName.split("\\.");
                // 获取文件后缀
                String suffix = fileNameArray[fileNameArray.length - 1];
                // 图片格式校验，防止黑客攻击
                if (!suffix.equalsIgnoreCase("png") &&
                        !suffix.equalsIgnoreCase("jpg") &&
                        !suffix.equalsIgnoreCase("jpeg")) {
                    return IMOOCJSONResult.errorMsg("图片格式不正确！");
                }

                // 文件名重组
                newFileName = "face_" + userId + "." + suffix;

                // 上传头像的最终保存位置
                String finalFacePath = fileSpace + uploadPathPrefix + File.separator + newFileName;

                // 创建目录
                File outFile = new File(finalFacePath);
                if (outFile.getParentFile() != null) {
                    outFile.getParentFile().mkdirs();
                }

                try {
                    // 文件输出保存到目录
                    fileOutputStream = new FileOutputStream(outFile);
                    inputStream = file.getInputStream();
                    IOUtils.copy(inputStream, fileOutputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (fileOutputStream != null) {
                            fileOutputStream.flush();
                            fileOutputStream.close();
                        }
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            return IMOOCJSONResult.errorMsg("上传头像不能为空！");
        }

        // 获取图片服务地址
        String imageServerUrl = fileUpload.getImageServerUrl();
        String finalUserFaceUrl = imageServerUrl + "/" + userId + "/" + newFileName;
        // 更新用户头像到数据库
        Users userResult = userCenterService.updateUserFace(userId, finalUserFaceUrl);

        userResult = setNullProperty(userResult);
        // 由于浏览器可能存在缓存的情况，所以要加上时间戳来保证更新后的图片可以及时刷新
        finalUserFaceUrl += "?t=" + DateUtil.getCurrentDateString(DateUtil.DATE_PATTERN);
        userResult.setFace(finalUserFaceUrl);
        CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(userResult), true);

        // TODO 后续要改，增加令牌token，会整合进redis，分布式会话
        return IMOOCJSONResult.ok();
    }

    private Users setNullProperty(Users userResult) {

        userResult.setPassword(null);
        userResult.setMobile(null);
        userResult.setEmail(null);
        userResult.setCreatedTime(null);
        userResult.setUpdatedTime(null);
        userResult.setBirthday(null);
        return userResult;
    }

    private Map<String, String> getErrors(BindingResult result) {
        Map<String, String> map = new HashMap<>();
        List<FieldError> fieldErrorList = result.getFieldErrors();
        for (FieldError error: fieldErrorList) {
            String errorField = error.getField();
            String errorMessage = error.getDefaultMessage();
            map.put(errorField, errorMessage);
        }
        return map;
    }
}
