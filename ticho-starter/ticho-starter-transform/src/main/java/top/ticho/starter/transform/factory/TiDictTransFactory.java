package top.ticho.starter.transform.factory;

import lombok.extern.slf4j.Slf4j;
import top.ticho.starter.transform.annotation.TiDictTrans;
import top.ticho.starter.transform.component.TiDictTransStrategy;
import top.ticho.starter.transform.serializer.TiDictTransSerializer;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhajianjun
 * @date 2025-03-25 22:42
 */
@Slf4j
public class TiDictTransFactory {
    public final static TiDictTransStrategy DEFAULT = dictType -> Collections.emptyMap();
    private static final Map<String, TiDictTransStrategy> tiDictTransStrategyMap = new ConcurrentHashMap<>();

    public static void setDictTransStrategy(Map<String, TiDictTransStrategy> tiDictTransStrategyMap) {
        TiDictTransFactory.tiDictTransStrategyMap.putAll(tiDictTransStrategyMap);
    }

    public static TiDictTransSerializer createSerializer(TiDictTrans annotation) {
        TiDictTransStrategy tiDictTransStrategy = tiDictTransStrategyMap.getOrDefault(annotation.dictName(), DEFAULT);
        Map<String, String> dictMap = tiDictTransStrategy.dictMap(annotation.dictType());
        TiDictTransSerializer tiDictTransSerializer = new TiDictTransSerializer();
        tiDictTransSerializer.setDictMap(dictMap);
        return tiDictTransSerializer;
    }

}
