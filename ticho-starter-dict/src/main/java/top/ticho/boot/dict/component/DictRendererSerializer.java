package top.ticho.boot.dict.component;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.beans.factory.InitializingBean;
import top.ticho.boot.cache.component.TiCacheTemplate;
import top.ticho.boot.dict.annotation.DictRender;
import top.ticho.tool.json.util.JsonUtil;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

/**
 * @author zhajianjun
 * @date 2024-12-28 15:37
 */
public class DictRendererSerializer extends JsonSerializer<Object> implements InitializingBean {

    @Resource
    private TiCacheTemplate tiCacheTemplate;
    @Resource
    private List<ObjectMapper> objectMappers;

    public DictRendererSerializer(TiCacheTemplate tiCacheTemplate, List<ObjectMapper> objectMappers) {
        this.tiCacheTemplate = tiCacheTemplate;
        this.objectMappers = objectMappers;
    }

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }
        try {
            Class<?> clazz = value.getClass();
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(DictRender.class)) {
                    DictRender annotation = field.getAnnotation(DictRender.class);
                    String type = annotation.type();
                    String dictValue = (String) field.get(value);
                    String dictLabel = tiCacheTemplate.get(type, dictValue, String.class);
                    field.set(value, dictLabel);
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to render dictionary values", e);
        }
        gen.writeObject(value);
    }

    @Override
    public void afterPropertiesSet() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(Object.class, this);
        JsonUtil.registerModule(module);
        objectMappers.forEach(objectMapper -> objectMapper.registerModule(module));
    }

}
