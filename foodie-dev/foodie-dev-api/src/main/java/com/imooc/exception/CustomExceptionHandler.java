package com.imooc.exception;

import com.imooc.utils.IMOOCJSONResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class CustomExceptionHandler {
    // 上传图片大小超过500k, 捕获异常：MaxUploadSizeExceededException
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public IMOOCJSONResult handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        return IMOOCJSONResult.errorMsg("上传图片大小不能超过500k，请压缩图片或降低图片质量再上传！");
    }
}
