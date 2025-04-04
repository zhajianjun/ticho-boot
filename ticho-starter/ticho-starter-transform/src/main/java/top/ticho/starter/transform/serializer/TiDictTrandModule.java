package top.ticho.starter.transform.serializer;

import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * @author zhajianjun
 * @date 2025-04-02 21:51
 */
public class TiDictTrandModule extends SimpleModule {

    @Override
    public void setupModule(SetupContext context) {
        context.addBeanSerializerModifier(new TiDictTransSerializerModifier());
    }

}
