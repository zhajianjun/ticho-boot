package top.ticho.starter.datasource.component;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import top.ticho.tool.core.TiIdUtil;

import java.io.Serializable;

/**
 * MybatisPlus ID生成器
 * 当对象id不存在时才会生效，一般由开发进行生成，此处仅仅是为了兜底，防止开发忘记了
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56
 */
@Component
@ConditionalOnMissingBean(IdentifierGenerator.class)
@Slf4j
public class TiIdGenerator implements IdentifierGenerator {

    @Override
    public Number nextId(Object entity) {
        long id = TiIdUtil.getId();
        printLog(entity, id);
        return id;
    }

    /**
     * 生成uuid
     *
     * @param entity 实体
     * @return uuid
     */
    @Override
    public String nextUUID(Object entity) {
        String uuid = TiIdUtil.uuid();
        printLog(entity, uuid);
        return uuid;
    }

    private void printLog(Object entity, Serializable id) {
        if (log.isDebugEnabled()) {
            log.debug("{}DB对象主键ID生成->:{}", entity.getClass().getName(), id);
        }
    }

}
