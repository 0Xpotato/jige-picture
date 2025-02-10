package com.jige.jigepicturebackend.controller;

import com.jige.jigepicturebackend.common.BaseResponse;
import com.jige.jigepicturebackend.common.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class MainController {

    /**
     * 健康检查接口
     * 很轻量的接口，用来表示项目有没有正常运行
     * @return
     */
    @GetMapping("/health")
    public BaseResponse<String> health() {
        return ResultUtils.success("ok");
    }
}
