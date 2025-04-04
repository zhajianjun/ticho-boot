package top.ticho.starter.transform.serializer;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import top.ticho.starter.transform.annotation.TiDictTrans;
import top.ticho.starter.transform.component.TiDictTransStrategy;
import top.ticho.starter.transform.factory.TiDictTransFactory;

import java.util.List;
import java.util.Map;

/**
 * @author zhajianjun
 * @date 2025-04-04 17:54
 */
public class TiDictTransSerializerModifier extends BeanSerializerModifier {

    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
        for (BeanPropertyWriter beanProperty : beanProperties) {
            TiDictTrans dictValue = beanProperty.getAnnotation(TiDictTrans.class);
            if (dictValue != null) {
                TiDictTransSerializer dictFieldSerializer = getTiDictTransSerializer(dictValue);
                // 自定以序列器
                beanProperty.assignSerializer(dictFieldSerializer);
                // null值序列器
                beanProperty.assignNullSerializer(NullSerializer.instance);
            }
        }
        return beanProperties;
    }

    private TiDictTransSerializer getTiDictTransSerializer(TiDictTrans dictValue) {
        TiDictTransStrategy dictTransStrategy = TiDictTransFactory.getDictTransStrategy();
        Map<String, String> dictMap = dictTransStrategy.dictMap(dictValue.dictType());
        TiDictTransSerializer dictFieldSerializer = new TiDictTransSerializer();
        dictFieldSerializer.setDictMap(dictMap);
        return dictFieldSerializer;
    }

}
