package top.ticho.tool.generator;

import top.ticho.tool.generator.handler.ContextHandler;

/**
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public class Application {

    public static void main(String[] args) {
        ContextHandler contextHandler = new ContextHandler();
        contextHandler.handle();
    }

}
