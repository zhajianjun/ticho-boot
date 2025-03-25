package top.ticho.starter.transform.factory;

import top.ticho.starter.cache.component.TiCacheTemplate;
import top.ticho.starter.transform.annotation.TiDictTrans;
import top.ticho.starter.transform.serializer.TiDictTransSerializer;

/**
 * @author zhajianjun
 * @date 2025-03-25 22:42
 */
public class TiDictTransFactory {
    private static TiCacheTemplate tiCacheTemplate;

    public static void setCacheTemplate(TiCacheTemplate tiCacheTemplate) {
        TiDictTransFactory.tiCacheTemplate = tiCacheTemplate;
    }

    public static TiDictTransSerializer createSerializer(TiDictTrans annotation) {
        TiDictTransSerializer tiDictTransSerializer = new TiDictTransSerializer();
        tiDictTransSerializer.setTiCacheTemplate(tiCacheTemplate);
        tiDictTransSerializer.setTiDictTrans(annotation);
        return tiDictTransSerializer;
    }

}
