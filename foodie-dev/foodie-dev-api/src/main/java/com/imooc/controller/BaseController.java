package com.imooc.controller;

import org.springframework.stereotype.Controller;

import java.io.File;

@Controller
public class BaseController {

    public final static String FOODIE_SHOPCART = "shopcart";

    public final static Integer DEFAULT_COMMENT_PAGE_SIZE = 10;
    public final static Integer DEFAULT_SEARCH_PAGE_SIZE = 20;
    public final static Integer DEFAULT_ORDER_PAGE_SIZE = 10;

    public final static String DEFAULT_USER_FACE_PATH = "E:" + File.separator +
                                                        "LiangFan" + File.separator +
                                                        "WorkSpace" + File.separator +
                                                        "foodie-dev" + File.separator +
                                                        "faces";

}
