package top.ticho.generator;

import top.ticho.generator.handler.ContextHandler;

/**
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public class GeneratorApplication {

    public static void main(String[] args) {
        ContextHandler contextHandler = new ContextHandler();
        contextHandler.handle();
    }

}
