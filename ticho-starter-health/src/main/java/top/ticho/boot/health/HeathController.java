package top.ticho.boot.health;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.ticho.boot.view.core.Result;

/**
 * 健康检查接口
 *
 * @author zhajianjun
 * @date 2024-07-10 15:56
 */
@RestController
@RequestMapping("health")
@Api(tags = "健康检查")
@ApiSort(Ordered.HIGHEST_PRECEDENCE)
public class HeathController {

    @Value("${spring.application.name:unknown}")
    private String applicationName;

    @ApiOperation(value = "健康检查", notes = "健康检查")
    @ApiOperationSupport(order = 10)
    @GetMapping
    public Result<String> health() {
        return Result.ok(String.format("application[%s] is up", applicationName));
    }

}
