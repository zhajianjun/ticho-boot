package top.ticho.tool.template;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.ReThrowConsoleErrorHandler;
import org.beetl.core.Template;
import org.beetl.core.resource.ClasspathResourceLoader;
import org.beetl.core.resource.StringTemplateResourceLoader;
import top.ticho.tool.core.TiMapUtil;
import top.ticho.tool.core.TiObjUtil;
import top.ticho.tool.core.TiStrUtil;

import java.io.IOException;
import java.util.Map;

/**
 * @author zhajianjun
 * @date 2025-08-04 22:33
 */
public class TiTemplateUtil {
    private static final GroupTemplate gtForString;
    private static final GroupTemplate gtForFile;

    static {
        StringTemplateResourceLoader stringTemplateResourceLoader = new StringTemplateResourceLoader();
        ClasspathResourceLoader classpathResourceLoader = new ClasspathResourceLoader();
        Configuration cfg;
        try {
            cfg = Configuration.defaultConfiguration();
        } catch (IOException var3) {
            throw new RuntimeException(var3);
        }
        gtForString = new GroupTemplate(stringTemplateResourceLoader, cfg);
        gtForString.setErrorHandler(new ReThrowConsoleErrorHandler());
        gtForFile = new GroupTemplate(classpathResourceLoader, cfg);
        gtForFile.setErrorHandler(new ReThrowConsoleErrorHandler());
    }

    public static String render(String template, Map<String, ?> paramsMap) {
        Template t = gtForString.getTemplate(template);
        if (TiMapUtil.isNotEmpty(paramsMap)) {
            t.binding(paramsMap);
        }
        return t.render();
    }

    public static String render(String template, String key, Object value) {
        Template t = gtForString.getTemplate(template);
        if (TiStrUtil.isNotBlank(key)) {
            t.binding(key, TiObjUtil.defaultIfNull(value, TiStrUtil.EMPTY));
        }
        return t.render();
    }

    /**
     * 渲染
     *
     * @param template  模板表达式
     * @param paramsMap 参数map
     * @return {@link String}
     */
    public static String render(String template, Map<String, ?> paramsMap, boolean isClasspath) {
        Template t = isClasspath ? gtForFile.getTemplate(template) : gtForString.getTemplate(template);
        if (TiMapUtil.isNotEmpty(paramsMap)) {
            t.binding(paramsMap);
        }
        return t.render();
    }

    public static String render(String template, String key, Object value, boolean isClasspath) {
        Template t = isClasspath ? gtForFile.getTemplate(template) : gtForString.getTemplate(template);
        if (TiStrUtil.isNotBlank(key)) {
            t.binding(key, TiObjUtil.defaultIfNull(value, TiStrUtil.EMPTY));
        }
        return t.render();
    }

}
