package com.ticho.boot.security.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.ticho.boot.web.annotation.View;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.core.Ordered;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * 用户信息
 *
 * @author zhajianjun
 * @date 2022-09-22 15:36
 */

@RestController
@RequestMapping("principal")
@ApiSort(Ordered.HIGHEST_PRECEDENCE + 100)
@Api(tags = "权限用户信息")
@View
public class PrinciplController {

    @ApiOperation(value = "信息查询", notes = "信息查询")
    @ApiOperationSupport(order = 10)
    @GetMapping
    public Principal principal() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

}
