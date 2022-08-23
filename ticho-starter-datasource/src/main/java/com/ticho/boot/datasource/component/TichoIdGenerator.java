package com.ticho.boot.datasource.component;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.ticho.boot.web.util.CloudIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * MybatisPlus ID生成器
 * 当对象id不存在时才会生效，一般由开发进行生成，此处仅仅是为了兜底，防止开发忘记了
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56:30
 */
@Component
@ConditionalOnMissingBean(IdentifierGenerator.class)
@Slf4j
public class TichoIdGenerator implements IdentifierGenerator {

    @Override
    public Number nextId(Object entity) {
        long id = CloudIdUtil.getId();
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
        String uuid = CloudIdUtil.getUuid();
        printLog(entity, uuid);
        return uuid;
    }

    private void printLog(Object entity, Serializable id) {
        log.debug("\n{}对象主键防空策略生成主键值->:{}", entity.getClass().getName(), id);
    }

}
