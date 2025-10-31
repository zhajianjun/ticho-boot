package top.ticho.starter.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.ticho.starter.view.core.TiResult;

/**
 * 健康检查
 *
 * @author zhajianjun
 * @date 2024-07-10 15:56
 */
@RestController
@RequestMapping("health")
public class HeathController {

    @GetMapping
    public TiResult<String> health() {
        return TiResult.ok("application is up");
    }

}
