package top.ticho.starter.web.converter;

import cn.hutool.core.text.CharSequenceUtil;
import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import top.ticho.tool.json.constant.TiDateFormatConst;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * LocalDate转换
 * <p>
 * GET请求，Query查询Date时间类型参数转换
 * </p>
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56
 */
@Component
public class TiLocalDateConverter implements Converter<String, LocalDate> {
    @Override
    public LocalDate convert(@NonNull String source) {
        if (CharSequenceUtil.isBlank(source)) {
            return null;
        }
        if (source.matches(TiDateFormatConst.YYYY_MM_DD_REGEX)) {
            return LocalDate.parse(source, DateTimeFormatter.ofPattern(TiDateFormatConst.YYYY_MM_DD));
        }
        throw new IllegalArgumentException("Invalid value '" + source + "'");
    }
}