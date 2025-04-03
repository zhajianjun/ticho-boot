package top.ticho.starter.transform.serializer;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import top.ticho.starter.transform.annotation.TiDictTrans;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhajianjun
 * @date 2025-04-02 21:51
 */
public class TiDictModule extends SimpleModule {
    @Override
    public void setupModule(SetupContext context) {
        context.addBeanSerializerModifier(new DictSerializerModifier());
    }

    private static class DictSerializerModifier extends BeanSerializerModifier {

        @Override
        public List<BeanPropertyWriter> changeProperties(SerializationConfig config,
                                                         BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
            List<BeanPropertyWriter> newWriters = new ArrayList<>();
            for (BeanPropertyWriter writer : beanProperties) {
                TiDictTrans anno = writer.getAnnotation(TiDictTrans.class);
                if (anno != null && StrUtil.isNotBlank(anno.targetField())) {
                    newWriters.add(createVirtualWriter(anno.targetField(), writer));
                }
            }
            return newWriters;
        }

        private BeanPropertyWriter createVirtualWriter(String targetField, BeanPropertyWriter writer) {
            return null;
        }

    }


}
