package top.ticho.starter.transform.factory;

import lombok.extern.slf4j.Slf4j;
import top.ticho.starter.transform.annotation.TiDictTrans;
import top.ticho.starter.transform.component.TiDictTransStrategy;
import top.ticho.starter.transform.serializer.TiDictTransSerializer;

import java.util.Collections;
import java.util.Map;

/**
 * @author zhajianjun
 * @date 2025-03-25 22:42
 */
@Slf4j
public class TiDictTransFactory {
    public final static TiDictTransStrategy DEFAULT = dictType -> Collections.emptyMap();
    private static TiDictTransStrategy INSTANCE;

    public static void setDictTransStrategy(TiDictTransStrategy tiDictTransStrategy) {
        INSTANCE = tiDictTransStrategy;
    }

    public static TiDictTransStrategy getDictTransStrategy() {
        if (INSTANCE == null) {
            INSTANCE = DEFAULT;
        }
        return INSTANCE;
    }

    public static TiDictTransSerializer createSerializer(TiDictTrans annotation) {
        TiDictTransStrategy dictTransStrategy = getDictTransStrategy();
        Map<String, String> dictMap = dictTransStrategy.dictMap(annotation.dictType());
        TiDictTransSerializer tiDictTransSerializer = new TiDictTransSerializer();
        tiDictTransSerializer.setDictMap(dictMap);
        return tiDictTransSerializer;
    }


}
