package com.ticho.boot.web.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.ticho.boot.web.annotation.View;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.Ordered;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查接口
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56:30
 */
@RefreshScope
@RestController
@RequestMapping("health")
@Api(tags = "健康检查")
@ApiSort(Ordered.HIGHEST_PRECEDENCE)
@View
public class HeathyController {

    @Value("${spring.application.name:application}")
    private String applicationName;

    @Value("${server.port:}")
    private String port;

    @Value("${ticho.health:health}")
    private String healthy;


    @ApiOperation(value = "健康检查", notes = "健康检查")
    @ApiOperationSupport(order = 10)
    @GetMapping
    public String healthy() {
        return String.format("【应用名：%s。端口：%s。健康配置参数内容：%s】", applicationName, port, healthy);
    }

}
