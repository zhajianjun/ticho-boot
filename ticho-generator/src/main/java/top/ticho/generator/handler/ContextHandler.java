package top.ticho.generator.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.core.config.Configurator;
import org.slf4j.MDC;
import top.ticho.generator.config.GlobalConfig;
import top.ticho.generator.constant.CommConst;
import top.ticho.generator.exception.GenerateException;
import top.ticho.generator.util.ObjUtil;
import top.ticho.generator.util.StrUtil;
import top.ticho.generator.util.TraceUtil;
import top.ticho.generator.yml.GlobalYml;
import top.ticho.generator.yml.ProjectYml;
import top.ticho.tool.json.constant.TiDateFormatConst;
import top.ticho.tool.json.util.TiJsonUtil;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhajianjun
 * @date 2024-11-23 20:14
 */
@Slf4j
public class ContextHandler {
    // 增加日期格式缓存
    private static final Map<String, DateTimeFormatter> FORMATTER_CACHE = new ConcurrentHashMap<>();

    public void handle() {
        // 初始化日志
        Configurator.initialize(CommConst.CONFIG_LOG_NAME, CommConst.CONFIG_LOG_XML);
        TraceUtil.trace();
        try {
            handle1();
        } catch (Exception e) {
            log.error("任务执行异常：{}", e.getMessage(), e);
        } finally {
            MDC.clear();
        }
    }

    public void handle1() {
        log.warn("执行路径[{}]", CommConst.PROJECT_PATH);
        GlobalYml globalYml = getGlobalYml();
        GlobalConfig globalConfig = Optional.ofNullable(globalYml).map(GlobalYml::getGlobalConfig).orElse(null);
        if (Objects.isNull(globalConfig)) {
            log.warn("全局配置[{}]不存在", CommConst.GLOBAL_YML);
            return;
        }
        handleDate(globalConfig);
        log.info("全局配置[{}]加载完成", CommConst.GLOBAL_YML);
        List<String> envs = globalConfig.getEnvs();
        if (ObjUtil.isEmpty(envs)) {
            log.warn("环境变量[globalConfig.envs]不存在");
            return;
        }
        log.info("环境变量[{}]", String.join(CommConst.COMMA, envs));
        envs.forEach(env -> {
            TraceUtil.traceEnv(env);
            handleProject(env, globalConfig);
        });
    }

    private void handleDate(GlobalConfig globalConfig) {
        if (StrUtil.isNotBlank(globalConfig.getDate())) {
            return;
        }
        String dateFormat = Optional.ofNullable(globalConfig.getDateFormat())
            .orElse(TiDateFormatConst.YYYY_MM_DD_HH_MM_SS);

        DateTimeFormatter formatter = FORMATTER_CACHE.computeIfAbsent(dateFormat, DateTimeFormatter::ofPattern);
        globalConfig.setDate(LocalDateTime.now().format(formatter));
    }

    private void handleProject(String env, GlobalConfig globalConfig) {
        try {
            String configYml = String.format(CommConst.CONFIG_ENV_YML, env);
            ProjectYml projectYml = getProjectYml(configYml);
            if (ObjUtil.isEmpty(projectYml)) {
                log.warn("项目配置加载失败，[{}]不存在", configYml);
                return;
            }
            ProjectHandler projectHandler = new ProjectHandler(
                globalConfig,
                projectYml.getProjectConfig(),
                projectYml.getDataSourceConfig(),
                projectYml.getFileTemplateConfig(),
                env
            );
            projectHandler.handle();
        } catch (Exception e) {
            if (Boolean.TRUE.equals(globalConfig.getIgnoreError())) {
                log.error("执行失败，忽略异常继续执行：{}", e.getMessage(), e);
                return;
            }
            throw new GenerateException(e);
        }
    }

    private ProjectYml getProjectYml(String configYml) {
        if (StrUtil.isBlank(configYml)) {
            return null;
        }
        File projectConfigFile = new File(CommConst.PROJECT_PATH, configYml);
        if (!projectConfigFile.exists()) {
            return null;
        }
        // 获取项目配置
        ProjectYml projectYml = TiJsonUtil.toJavaObjectFromYaml(projectConfigFile, new TypeReference<>() {
        });
        log.warn("项目配置加载成功，配置根路径[{}]", projectConfigFile.getAbsolutePath());
        if (Objects.isNull(projectYml)) {
            return null;
        }
        return projectYml;
    }

    private GlobalYml getGlobalYml() {
        File globalConfigFile = new File(CommConst.PROJECT_PATH, CommConst.GLOBAL_YML);
        if (!globalConfigFile.exists()) {
            return null;
        }
        // 获取全局配置
        return TiJsonUtil.toJavaObjectFromYaml(globalConfigFile, new TypeReference<>() {
        });
    }

}
