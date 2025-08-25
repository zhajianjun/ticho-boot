package top.ticho.trace.common;

import lombok.extern.slf4j.Slf4j;
import top.ticho.tool.json.util.TiJsonUtil;

import java.util.List;
import java.util.Objects;

/**
 * @author zhajianjun
 * @date 2025-07-13 14:18
 */
@Slf4j
public class TiConsoleTraceReporter implements TiTraceReporter {

    @Override
    public void report(List<TiSpan> tiSpans) {
        if (Objects.isNull(tiSpans) || tiSpans.isEmpty()) {
            return;
        }
        // 打印链路日志,json序列化打印
        log.info("[Trace]{}[Trace]", TiJsonUtil.toJsonString(tiSpans));
    }

}