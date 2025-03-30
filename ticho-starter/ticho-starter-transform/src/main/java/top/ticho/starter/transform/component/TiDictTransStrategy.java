package top.ticho.starter.transform.component;

import java.util.Map;

/**
 * @author zhajianjun
 * @date 2025-03-30 16:54
 */
public interface TiDictTransStrategy {

    /**
     * 字典转换策略接口
     *
     * @param dictType 字典类型标识
     * @return 字典映射集合 key=字典值, value=字典显示名称
     */
    Map<String, String> dictMap(String dictType);

}
