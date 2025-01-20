package top.ticho.boot.health;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.ticho.boot.view.core.TiResult;

/**
 * 健康检查
 *
 * @author zhajianjun
 * @date 2024-07-10 15:56
 */
@RestController
@RequestMapping("health")
public class HeathController {

    @Value("${spring.application.name:unknown}")
    private String applicationName;

    @GetMapping
    public TiResult<String> health() {
        return TiResult.ok(String.format("application[%s] is up", applicationName));
    }

}
